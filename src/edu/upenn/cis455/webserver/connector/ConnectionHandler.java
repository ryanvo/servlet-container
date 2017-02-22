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
    private ResponseProcessor responseProcessor;
    private ConnectionManager manager;

    public ConnectionHandler(Socket connection,
                             Container container,
                             RequestProcessor requestProcessor,
                             ResponseProcessor responseProcessor) {
        this.connection = connection;
        this.container = container;
        this.requestProcessor = requestProcessor;
        this.responseProcessor = responseProcessor;
        this.manager = (ConnectionManager)container.getContext("webapp").getAttribute("ConnectionManager");

    }

    /**
     * Origin for all servlet requests. All error handling occurs here.
     */
    @Override
    public void run() {


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
                request.setInputStream(connection.getInputStream());

                requestProcessor.process(request);
                if (request.getRequestURI() == null) {
                    continue;
                }

                response.setHTTP(request.getProtocol());

                log.debug("Request successfully populated: uri:" + request.getRequestURI());

                manager.update(Thread.currentThread().getId(), request.getRequestURI());
                log.debug("Connection manager updated: " + "tid:" + Thread.currentThread().getId() + " uri:" + request
                        .getRequestURI());

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

            } catch (SocketTimeoutException e) {
                log.debug("Socket timeout, disconnecting from client with requestUri:" + request.getRequestURI());
                    break;
            } catch (IOException e) {
                response.sendError(500);
                log.error("Server IO Error", e);

                return;
            } catch (BadRequestException e) {

                response.sendError(400);

                log.info("400 Bad Request sent to client");

            } catch (ServletException e) {

                log.info("Servlet threw exception: ", e);
                if (e.getRootCause() instanceof UnsupportedRequestException) {
                    response.sendError(501);
                    log.info("501 Not Implemented sent to client");
                } else if (e.getRootCause() instanceof BadRequestException) {
                    response.sendError(400);
                    log.info("400 Bad Request sent to client from servlet");
                } else if (e.getRootCause() instanceof IOException) {
                    log.info("Server IO Error", e);
                    return;
                } else {
                    log.error("unrecognized exception", e);
                }
            }


            /*
             * Send
             */
            try {
                responseProcessor.process(response, connection.getOutputStream());
            } catch (IOException e) {
                log.error(e);
                break;
            }

            log.info(String.format("HttpRequest Parsed %s Request with URI %s", request.getMethod(), request.getRequestURI()));

        }


        try {
            connection.close();
            manager.update(Thread.currentThread().getId(), "waiting");
            log.debug("Socket closed");
        } catch (IOException e) {
            log.error("Failed to close socket", e);
        }
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


