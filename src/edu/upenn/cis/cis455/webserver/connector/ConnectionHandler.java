package edu.upenn.cis.cis455.webserver.connector;

import edu.upenn.cis.cis455.webserver.engine.Container;
import edu.upenn.cis.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis.cis455.webserver.engine.http.HttpResponse;
import edu.upenn.cis.cis455.webserver.exception.BadRequestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

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

        ConnectionManager manager = (ConnectionManager) container.getContext().getAttribute("ConnectionManager");
        HttpRequest request = new HttpRequest();
        HttpResponse response = new HttpResponse();

        try {
            request.setInputStream(connection.getInputStream());
            response.setOutputStream(connection.getOutputStream());
            response.addHeader("Server", "ryanvo-server/1.00");


            requestProcessor.process(request);

            manager.update(Thread.currentThread().getId(), request.getRequestURI());
            container.dispatch(request, response);

        } catch (IOException e) {
            response.sendError(500, "Server IO Error");
            log.debug("400 Bad Request sent ot client");
        } catch (BadRequestException e) {
            response.sendError(400, "Bad Request");
            log.debug("400 Bad Request sent ot client");
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


}


