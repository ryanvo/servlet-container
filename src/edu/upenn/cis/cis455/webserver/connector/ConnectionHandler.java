package edu.upenn.cis.cis455.webserver.connector;

import edu.upenn.cis.cis455.webserver.engine.Container;
import edu.upenn.cis.cis455.webserver.servlet.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.servlet.http.HttpResponse;
import edu.upenn.cis.cis455.webserver.exception.http.BadRequestException;
import edu.upenn.cis.cis455.webserver.exception.http.UnsupportedRequestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
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
        HttpRequest request = new HttpRequest();
        HttpResponse response = new HttpResponse();

        try {
            response.setOutputStream(connection.getOutputStream());
            response.addHeader("Server", "ryanvo-server/1.00");
            request.setInputStream(connection.getInputStream());

            requestProcessor.process(request);
            manager.update(Thread.currentThread().getId(), request.getRequestURI());

            /* Check that Host header exists for HTTP/1.1 and up */
            if (!hasValidHostHeader(request.getProtocol(), request.getHeaders())) {
                log.debug("Request is missing Host header entry");
                throw new BadRequestException();
            }

            /* Send 100 Continue after receiving headers if request asked for it */
            handle100ContinueRequest(request, connection.getOutputStream());

            /* Exceptions throw by servlet are caught under ServletException */
            log.info(String.format("Dispatching requestUri:%s, method:%s", request.getRequestURI(), request.getMethod()));
            container.dispatch(request, response);

        } catch (IOException e) {
            response.sendError(500, "Server IO Error");
            log.debug("400 Bad Request sent to client");
        } catch (BadRequestException e) {
            response.sendError(400, "Bad Request");
            log.debug("400 Bad Request sent to client");
        } catch (ServletException e) {
            log.info("Servlet throw exception: ", e);
            if (e.getRootCause() instanceof UnsupportedRequestException) {
                response.sendError(501, "Not Implemented");
                log.info("501 Not Implemented sent to client");
            }

            if (e.getRootCause() instanceof BadRequestException) {
                response.sendError(400, "Bad Request");
                log.debug("400 Bad Request sent to client");
            }

            if (e.getRootCause() instanceof IOException) {
                response.sendError(500, "Server IO Error");
                log.debug("400 Bad Request sent to client");
            }
        }


        // TODO log.info(String.format("HttpRequest Parsed %s Request with URI %s", method, uri));

        try {
            connection.close();
            manager.update(Thread.currentThread().getId(), "waiting");
            log.debug("Socket closed requestUri:" + request.getRequestURI());
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
            out.write("HTTP/1.1 100 Continue\r\n".getBytes());
            out.flush();
            log.debug(String.format("Sent 100 continue uri:%s, method:%s", request.getRequestURI(), request.getMethod()));
        }

    }

}


