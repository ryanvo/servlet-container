package edu.upenn.cis455.webserver.connector;

import edu.upenn.cis455.webserver.engine.Container;
import edu.upenn.cis455.webserver.servlet.exception.http.BadRequestException;
import edu.upenn.cis455.webserver.servlet.exception.http.UnsupportedRequestException;
import edu.upenn.cis455.webserver.servlet.http.HttpRequest;
import edu.upenn.cis455.webserver.servlet.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;

public class ConnectionHandler implements Runnable {

    private static Logger log = LogManager.getLogger(ConnectionHandler.class);

    private Socket connection;
    private Container container;
    private RequestProcessor requestProcessor;

    public ConnectionHandler(Socket connection,
                             Container container,
                             RequestProcessor requestProcessor) {
        this.connection = connection;
        this.container = container;
        this.requestProcessor = requestProcessor;
    }

    /**
     * Origin for all servlet requests. All error handling occurs here.
     */
    @Override
    public void run() {

        ConnectionManager manager = (ConnectionManager) container.getContext("webapp").getAttribute("ConnectionManager");

        try {
            connection.setSoTimeout(30000);
        } catch (SocketException e) {
            log.error("Failed to set socket timeout", e);
            return;
        }

        while (!connection.isClosed()) {

            HttpRequest request = new HttpRequest();
            HttpResponse response = new HttpResponse();

            try {
                response.setOutputStream(connection.getOutputStream());
                request.setInputStream(connection.getInputStream());


                requestProcessor.process(request);
                log.debug("Request successfully populated: uri:" + request.getRequestURI());

                manager.update(Thread.currentThread().getId(), request.getRequestURI());
                log.debug("Connection manager updated: " + "tid:" + Thread.currentThread().getId() + " uri:" + request
                        .getRequestURI());

                String connectionHeaderValue = !request.getProtocol().endsWith("1") ? "close" : "keep-alive";
                response.addHeader("Connection", connectionHeaderValue);

                /* Check that Host header exists for HTTP/1.1 and up */
                if (!hasValidHostHeader(request.getProtocol(), request.getHeaders())) {
                    log.info("Request is missing Host header entry");
                    throw new BadRequestException();
                }

                /* Send 100 Continue after receiving headers if request asked for it */
                handle100ContinueRequest(request, connection.getOutputStream());

                /* Exceptions throw by servlet are caught under ServletException */
                log.info(String.format("Dispatching requestUri:%s, method:%s", request.getRequestURI(), request.getMethod()));
                container.dispatch(request, response);


            } catch (SocketException e) {
                log.error("Broken pipe");
                return;
            } catch (IllegalStateException e) {
                log.info("Server IO Error", e);

            } catch (NullPointerException e) {
                response.sendError(400, "Bad Request");
                System.exit(1);
                log.info("400 Bad Request sent because of null pointer", e);
            } catch (SocketTimeoutException e) {
                log.debug("Socket timeout, disconnecting from client with requestUri:" + request.getRequestURI());
//                break;
            } catch (IOException e) {
                response.sendError(500, "Server IO Error");
                log.error("Server IO Error", e);

                return;
            } catch (BadRequestException e) {
                System.exit(1);

                response.sendError(400, "Bad Request");

                log.info("400 Bad Request sent to client");

            } catch (ServletException e) {

                log.info("Servlet threw exception: ", e);
                if (e.getRootCause() instanceof UnsupportedRequestException) {
                    response.sendError(501, "Not Implemented");
                    log.info("501 Not Implemented sent to client");
                } else if (e.getRootCause() instanceof BadRequestException) {
                    response.sendError(400, "Bad Request");
                    log.info("400 Bad Request sent to client from servlet");
                } else if (e.getRootCause() instanceof IOException) {
                    log.info("Server IO Error", e);
                    return;
                } else {
                    log.error("unrecognized exception", e);
                }
            }

            try {
                if (!response.isCommitted()) {
                    response.flushBuffer();
                }
            } catch (IOException e) {
                log.error(e);
            }

        }
        // TODO log.info(String.format("HttpRequest Parsed %s Request with URI %s", method, uri));

        try {
            connection.close();
            manager.update(Thread.currentThread().getId(), "waiting");
            log.debug("Socket closed");
        } catch (IOException e) {
            log.error("Failed to close socket", e);
        }
    }


    public boolean hasValidHostHeader(String version, Map<String, List<String>> headers) {
        return !version.endsWith("1") || headers.containsKey("host");
    }

    public void handle100ContinueRequest(HttpRequest request, OutputStream out) throws IOException {

        if (!request.getProtocol().endsWith("1")) {
            return;
        }

        Map<String, List<String>> headers = request.getHeaders();
        if (headers.containsKey("expect") && headers.get("expect").contains("100-continue")) {
            out.write("HTTP/1.1 100 Continue\n\n".getBytes());
            log.info(String.format("Sent 100 continue uri:%s, method:%s", request.getRequestURI(), request.getMethod()));
        }

    }

}


