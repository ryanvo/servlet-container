package edu.upenn.cis.cis455.webserver.thread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorkerThread extends Thread {

    private static Logger log = LogManager.getLogger(WorkerThread.class);

    private final WorkerPool pool;
    private volatile boolean isRunning = true;

    public WorkerThread(WorkerPool pool) {
        this.pool = pool;
    }

    @Override
    public void run() {

        log.info("Thread Started");
        while (!isInterrupted()) {
            try {
                pool.take().run();
            } catch (InterruptedException e) {
                break;
            }
        }
        log.info("Thread Stopped");
    }

}
