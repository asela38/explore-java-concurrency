package com.asela.test.java.concurrency;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockTest {
    
    @Test
    public void lockInterruptabilityPattern() throws InterruptedException {
        Printer.begin.print();

        Lock lock = new ReentrantLock();

        Runnable task = () -> {
            Printer.begin.print();

            try {
                lock.lockInterruptibly();
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Printer.complete.print();
        };

        ExecutorService executorService =  Executors.newSingleThreadExecutor();
        executorService.execute(task);

        TimeUnit.SECONDS.sleep(3);

        Printer.complete.print();
    }
}
