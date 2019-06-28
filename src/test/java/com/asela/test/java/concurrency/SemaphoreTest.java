package com.asela.test.java.concurrency;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SemaphoreTest {

    @Test
    public void testAcquire() throws InterruptedException {

        Printer.begin.print();

        Semaphore semaphore = new Semaphore(5);

        Runnable task = () -> {
            Printer.begin.print();

            try{
                semaphore.acquire(2);

                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                Printer.interrupted.print();
                return;
            } finally {
                semaphore.release(2);
            }

            Printer.complete.print();
        };

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.execute(task);
        executorService.execute(task);
        executorService.execute(task);
        executorService.execute(task);

        TimeUnit.SECONDS.sleep(3);
        Printer.complete.print();
    }
}
