package edu.upenn.cis.cis455.webserver.thread;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class MyExecutorService {

    static Logger log = LogManager.getLogger(MyPoolThread.class);

    private volatile boolean isRunning = true;
    private MyBlockingQueue queue;
    private Set<MyPoolThread> threadPool = new HashSet<>();

    public MyExecutorService(int poolSize, MyBlockingQueue queue) {
        this.queue = queue;

        for (int i = 0; i < poolSize; i++) {
            threadPool.add(new MyPoolThread(queue));
        }

        for (Thread thread : threadPool) {
            thread.start();
        }
    }

    public void execute(Runnable request) throws IllegalStateException {
        if (!isRunning) {
            throw new IllegalStateException("Executor Service is stopped");
        }
        queue.put(request);
    }

    public void shutdown() {
        isRunning = false;

        while (!queue.isEmpty()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignore) {}
        }

        for (MyPoolThread thread : threadPool) {
                thread.interrupt();
        }

        log.info("All threads stopped");
    }

    public Set<MyPoolThread> threadPool() {
        return threadPool;
    }

    public boolean isRunning() {
        return isRunning;
    }

}
