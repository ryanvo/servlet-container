package edu.upenn.cis455.webserver.thread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;


/**
 * @author rtv
 */
public class WorkerPool {

    private Logger log = LogManager.getLogger(WorkerPool.class);

    private Set<WorkerThread> threadPool = new HashSet<>();
    private WorkQueue queue;

    public WorkerPool(int size, WorkQueue queue) {
        this.queue = queue;

        for (int i = 0; i < size; i++) {
            threadPool.add(new WorkerThread(queue));
        }

        for (Thread thread : threadPool) {
            thread.start();
            log.debug(String.format("Thread #%d started", thread.getId()));
        }
    }

    public void offer(Runnable httpRequest) {
        queue.put(httpRequest);
    }

    public Set<WorkerThread> getWorkers() {
        return threadPool;
    }

    public void kill() {

        while (!queue.isEmpty()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {}
        }

        for (WorkerThread thread : threadPool) {
            thread.interrupt();
            log.debug(String.format("Thread #%d stopped", thread.getId()));
        }

        log.info(String.format("Worked pool stopped %d threads", threadPool.size()));
    }


}
