package edu.upenn.cis.cis455.webserver.connector;

import edu.upenn.cis.cis455.webserver.engine.Container;
import edu.upenn.cis.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.engine.http.HttpResponse;
import edu.upenn.cis.cis455.webserver.exception.BadRequestException;
import edu.upenn.cis.cis455.webserver.exception.UnsupportedRequestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import java.io.IOException;
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
            request.setInputStream(connection.getInputStream());

            requestProcessor.process(request);
            manager.update(Thread.currentThread().getId(), request.getRequestURI());

            /* Check that Host header exists for HTTP/1.1 and up */
            if (!hasValidHostHeader(request.getProtocol(), request.getHeaders())) {
                log.debug("Request is missing Host header entry");
                throw new BadRequestException();
            }

            if (request.hasHeader("expect") && request.getHeader("expect").contains("100-continue")) {
                connection.getOutputStream().write("HTTP/1.1 100 Continue\r\n\r\n".getBytes());
            }

            /* Exceptions throw by servlet are caught under ServletException */
            response.setOutputStream(connection.getOutputStream());
            response.addHeader("Server", "ryanvo-server/1.00");
            container.dispatch(request, response);

        } catch (IOException e) {
            response.sendError(500, "Server IO Error");
            log.debug("400 Bad Request sent to client");
        } catch (BadRequestException e) {
            response.sendError(400, "Bad Request");
            log.debug("400 Bad Request sent to client");
        }  catch (ServletException e) {
            log.info("Servlet throw exception: ", e);
            if (e.getRootCause() instanceof UnsupportedRequestException) {
                response.sendError(501, "Not Implemented");
                log.info("501 Not Implemented sent to client");
            }
        }


        // TODO log.info(String.format("HttpRequest Parsed %s Request with URI %s", method, uri));

        try {
            connection.close();
            manager.update(Thread.currentThread().getId(), "waiting");
            log.info("Socket Closed");
        } catch (IOException e) {
            log.error("Could Not Close Socket After Sending Response", e);
        }
    }


    public boolean hasValidHostHeader(String version, Map<String, List<String>> headers) {
        return !version.endsWith("1") || headers.containsKey("host");
    }

}


