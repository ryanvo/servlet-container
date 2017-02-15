package edu.upenn.cis455.webserver.thread;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorkerThread extends Thread {

    private static Logger log = LogManager.getLogger(WorkerThread.class);

    private final WorkQueue pool;

    public WorkerThread(WorkQueue pool) {
        this.pool = pool;
    }

    @Override
    public void run() {

        while (!isInterrupted()) {
            try {
                pool.take().run();
            } catch (InterruptedException e) {
                break;
            } catch (NullPointerException e) {
                log.error(e);
            }
        }
    }

}
