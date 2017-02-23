package edu.upenn.cis455.webserver.connector;

import edu.upenn.cis455.webserver.engine.Container;
import edu.upenn.cis455.webserver.servlet.exception.http.BadRequestException;
import edu.upenn.cis455.webserver.servlet.http.HttpRequest;
import edu.upenn.cis455.webserver.servlet.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;

/**
 * Serves as the handle for incoming/connections to/from the Http Request Listener.
 * Client requests arrive here first for processing. Valid requests are then dispatched
 * to the appropriate servlet by the WebApoManager. Once the response is finished,
 * ConnectionHandler writes the headers and message body to to the socket. If the is
 * no activity for 30s, the handler will disconnect. Otherwise, there are persistent
 * connections.
 *
 * @author rtv
 */
public class ConnectionHandler implements Runnable {

    private static Logger log = LogManager.getLogger(ConnectionHandler.class);

    private Socket connection;
    private Container container;
    private RequestProcessor requestProcessor;
    private ResponseProcessor responseProcessor;
    private ConnectionManager manager;

    private HttpRequest request = new HttpRequest();
    private HttpResponse response = new HttpResponse();

    public ConnectionHandler(Socket connection,
                             Container container,
                             RequestProcessor requestProcessor,
                             ResponseProcessor responseProcessor) {
        this.connection = connection;
        this.container = container;
        this.requestProcessor = requestProcessor;
        this.responseProcessor = responseProcessor;
        this.manager = (ConnectionManager) container.getContext("webapp").getAttribute("ConnectionManager");

    }

    /**
     * Origin for all servlet requests. All error handling occurs here.
     * If the issue is in the connection, this method returns. IO
     * exceptions places the thread back into the working queue.
     * <p>
     * The HttpResponse and HttpRequest objects are reused between each
     * request during persistent connection.
     * <p>
     * There is a 30 second timeout on the persistent connection.
     */
    @Override
    public void run() {


        try {
            connection.setSoTimeout(30000);
        } catch (SocketException e) {
            log.error("Failed to set socket timeout", e);
            return;
        }

        int count = 1;

        while (!connection.isClosed()) {

            try {

                request.setInputStream(connection.getInputStream());

                try {
                    requestProcessor.process(request);
                } catch (NullPointerException e) {
                    log.info("Client disconnected");
                    break;
                }
                response.setHTTP(request.getProtocol());

                manager.update(Thread.currentThread().getId(), request.getRequestURI());
                log.debug("Connection manager updated: " + "tid:" + Thread.currentThread().getId() + " uri:" + request
                        .getRequestURI());

                /* Send 100 Continue after receiving headers if request asked for it */
                handle100ContinueRequest(request, connection.getOutputStream());

                /* Exceptions throw by servlet are caught under ServletException */
                log.info(String.format("Dispatched method:%s : uri:%s", request.getMethod(), request.getRequestURI()));
                container.dispatch(request, response);


            } catch (SocketException e) {
                log.error("Broken pipe");
                return;
            } catch (IllegalStateException e) {
                log.info("Server IO Error", e);
                break;
            } catch (SocketTimeoutException e) {
                log.debug("Socket timeout, disconnecting from client with requestUri:" + request.getRequestURI());
                break;
            } catch (BadRequestException e) {
                response.sendError(400);
                log.info("400 Bad Request sent to client");
            } catch (IOException|ServletException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

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


            log.info(String.format("Succesfully handled method:%s : uri:%s", request.getMethod(), request.getRequestURI()));
            response = new HttpResponse();
            request = new HttpRequest();
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


