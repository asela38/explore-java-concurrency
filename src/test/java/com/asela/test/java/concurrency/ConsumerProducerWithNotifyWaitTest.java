package com.asela.test.java.concurrency;

import org.junit.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConsumerProducerWithNotifyWaitTest {

    private Random random = new Random(2L);

    @Test
    public void testClassic() throws IOException {


        Queue<Integer> queue = new LinkedList<>();
        Object lock = new Object();

        Runnable producerTask = () -> {
            while (true)
                synchronized (lock) {
                    while (queue.isEmpty()) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.printf("[%s] Consumer Polling = %s %n", Thread.currentThread().getName(), queue.poll());
                    lock.notifyAll();
                }
        };


        Runnable consumerTask = () -> {
            while (true)
                synchronized (lock) {
                    while (queue.size() > 100) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.printf("[%s] Producer pushed = %s (%s) %n", Thread.currentThread().getName(),
                            queue.offer(random.nextInt(300)), queue.size());
                    lock.notifyAll();
                }
        };

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(consumerTask);
        executorService.execute(producerTask);


        System.in.read(new byte[1]);
    }

    @Test
    public void testModern() throws IOException {


        Queue<Integer> queue = new LinkedList<>();
        Lock lock = new ReentrantLock();
        Condition queueIsFull = lock.newCondition();
        Condition queueIsEmpty = lock.newCondition();
        Runnable producerTask = () -> {
            while (true)
                try {
                    lock.lock();
                    while (queue.isEmpty()) {
                        queueIsEmpty.await();
                    }
                    System.out.printf("[%s] Consumer Polling = %s %n", Thread.currentThread().getName(), queue.poll());

                    queueIsFull.signal();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
        };


        Runnable consumerTask = () -> {
            while (true)
                try  {
                    lock.lock();
                    while (queue.size() > 100) {
                        try {
                            queueIsFull.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.printf("[%s] Producer pushed = %s (%s) %n", Thread.currentThread().getName(),
                            queue.offer(random.nextInt(300)), queue.size());

                    queueIsEmpty.signal();
                } finally {
                    lock.unlock();
                }
        };

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(consumerTask);
        executorService.execute(producerTask);


        System.in.read(new byte[1]);
    }
}
