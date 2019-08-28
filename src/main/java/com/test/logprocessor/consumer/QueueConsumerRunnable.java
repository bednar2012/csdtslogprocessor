package com.test.logprocessor.consumer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class QueueConsumerRunnable<T> implements Runnable {

    private static final long POLL_TIMEOUT = 5;

    private BlockingQueue<T> queue;

    QueueConsumerRunnable(BlockingQueue<T> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                T element = queue.poll(POLL_TIMEOUT, TimeUnit.SECONDS);
                if (element == null) {
                    break;
                }
                consume(element);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected abstract void consume(T element);

}
