package edu.upenn.cis.cis455.webserver.thread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyPoolThread extends Thread {

    static Logger log = LogManager.getLogger(MyPoolThread.class);

    private final MyBlockingQueue pool;
    private volatile boolean isRunning = true;

    public MyPoolThread(MyBlockingQueue pool) {
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
