package edu.upenn.cis.cis455.webserver.container;

import edu.upenn.cis.cis455.webserver.thread.WorkExecutorService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the HTTP requests delegated to WorkExecutorService. Maintains the status of each thread
 * and can issue a shutdown of the entire thread pool. Used for the Control Page
 */
public class HttpConnectionManager {

    private final Map<Long, String> idToUri;
    private final WorkExecutorService executorService;

    public HttpConnectionManager(WorkExecutorService executorService) {
        idToUri = new ConcurrentHashMap<>();
        this.executorService = executorService;
        for (Thread thread : executorService.threadPool()) {
            update(thread.getId(), "waiting");
        }
    }

    /**
     * Updates the status of a thread
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
        executorService.shutdown();
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

        html.append("<p><a href=\"/shutdown/\">Shutdown</a></p></body></html>");
        return html.toString();
    }

}
