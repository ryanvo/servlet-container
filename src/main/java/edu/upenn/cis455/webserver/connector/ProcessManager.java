package edu.upenn.cis455.webserver.connector;

import java.util.Map;

/**
 * @author rtv
 */
public interface ProcessManager {

    void assign(Runnable runnable) throws IllegalStateException;
    void update(long threadId, String status);
    void shutdown();
    Map<Long, String> getStatus();
    boolean isRunning();

}
