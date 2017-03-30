package edu.upenn.cis455.webserver.thread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Queue;

public class WorkQueue {

    private static Logger log = LogManager.getLogger(WorkQueue.class);

    final private Queue<Runnable> queue = new ArrayDeque<>();
    private int size;

    public WorkQueue(int size) {
        this.size = size;
    }

    public synchronized void put(Runnable request) {
        while (queue.size() == size) {
            try {
                wait();
            } catch (InterruptedException e) {
                log.error(e);
            }
        }

        queue.offer(request);
        notify();
    }

    public synchronized Runnable take() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }

        Runnable work = queue.poll();
        notify();
        return work;
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

}

