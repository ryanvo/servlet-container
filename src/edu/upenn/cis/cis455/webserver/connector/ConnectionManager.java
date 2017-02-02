package edu.upenn.cis.cis455.webserver.connector;

import edu.upenn.cis.cis455.webserver.engine.ServletContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the HTTP requests delegated to RequestProcessor. Maintains the status of each connector
 * and can issue a stop of the entire connector pool. Used for the Control Page
 */
public class ConnectionManager {

    private final Map<Long, String> idToUri;
    private final RequestProcessor executorService;

    public ConnectionManager(ServletContext context) {
        idToUri = new ConcurrentHashMap<>();
        this.executorService = (RequestProcessor) (context.getAttribute("executor"));
        for (Thread thread : executorService.threadPool()) {
            update(thread.getId(), "waiting");
        }
    }

    /**
     * Updates the status of a connector
     * @param threadId
     * @param uri currently serving
     */
    public void update(long threadId, String uri) {
        idToUri.put(threadId, uri);
    }

    /**
     * Tells executor service to stop all threads
     */
    public void issueShutdown() {
        executorService.stop();
    }

    /**
     * @return html string for status of the control page
     */
    public String getHtmlResponse() {
        StringBuilder html = new StringBuilder();

        html.append("<html><body><h1>Control Panel</h1>")
                .append("<p><h2>Thread &nbsp; &nbsp; &nbsp; &nbsp;Running</h2></p>");


        for (Thread thread : executorService.threadPool()) {
            long tid = thread.getId();
            html.append("<p>").append(tid).append("&nbsp; &nbsp; &nbsp; &nbsp &nbsp; &nbsp; " +
                    "&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;").append(idToUri.get(tid));
        }

        html.append("<p><a href=\"/stop/\">Shutdown</a></p></body></html>");
        return html.toString();
    }

}
