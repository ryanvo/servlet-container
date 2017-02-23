package edu.upenn.cis455.webserver.connector;

import edu.upenn.cis455.webserver.engine.Container;
import edu.upenn.cis455.webserver.engine.SessionManager;
import edu.upenn.cis455.webserver.engine.http.exception.http.BadRequestException;
import edu.upenn.cis455.webserver.engine.http.HttpRequest;
import edu.upenn.cis455.webserver.engine.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
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
 * to the appropriate http by the WebApoManager. Once the response is finished,
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
    private ConnectionManager connectionManager;
    private SessionManager sessionManager;

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
        this.connectionManager = (ConnectionManager) container.getContext("webapp").getAttribute("ConnectionManager");
        this.sessionManager = (SessionManager) container.getContext("webapp").getAttribute("SessionManager");

    }

    /**
     * Origin for all http requests. All error handling occurs here.
     * If the issue is in the connection, this method returns. IO
     * exceptions places the thread back into the working queue.
     *
     * The HttpResponse and HttpRequest objects are reused between each
     * request during persistent connection.
     *
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


        while (!connection.isClosed()) {

            try {

                request.setInputStream(connection.getInputStream());
                request.setSessionManager(sessionManager);
                try {
                    requestProcessor.process(request);
                } catch (NullPointerException e) {
                    log.info("Client disconnected");
                    break;
                }

                /* Update response with the protocol version */
                response.setHTTP(request.getProtocol());

                /* Update the status of the thread */
                connectionManager.update(Thread.currentThread().getId(), request.getRequestURI());
                log.debug("Connection connectionManager updated: " + "tid:" + Thread.currentThread().getId() + " uri:" + request
                        .getRequestURI());

                /* Send 100 Continue after receiving headers if request asked for it */
                handle100ContinueRequest(request, connection.getOutputStream());

                /* Exceptions throw by http are caught under ServletException */
                log.info(String.format("Dispatched method:%s : uri:%s", request.getMethod(), request.getRequestURI()));
                container.dispatch(request, response);

                /* If the servlet requested a session, attach the JSESSIONID cookie to response */
                String sessionId = request.getRequestedSessionId();
                if (sessionId != null && sessionManager.isValid(sessionId)) {

                    Cookie sessionCookie = new Cookie("JSESSIONID", sessionId);
                    sessionCookie.setMaxAge(3600); // Default to 24 hrs
                    response.addCookie(sessionCookie);
                    log.info("JESSIONID cookie added to response: id:" + sessionId);

                }

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


            log.info(String.format("Successfully handled method:%s : uri:%s", request.getMethod(), request.getRequestURI()));
            response = new HttpResponse();
            request = new HttpRequest();

        }


        try {
            connection.close();
            connectionManager.update(Thread.currentThread().getId(), "waiting");
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


