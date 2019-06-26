package com.asela.test.java.concurrency;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.*;

public class ExecutorsTest {

    private Random random = new Random(1L);

    @Test
    public void testSingleThreadExecutor() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        checkExecutorService(executorService, 5, 2);
        executorService.shutdown();
    }

    @Test
    public void testDualThreadExecutor() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        checkExecutorService(executorService, 5, 2);
        executorService.shutdown();
    }


    private void checkExecutorService(final ExecutorService executorService, final int noOfTask, final long seconds) {
        Printer.begin.print();

        final Phaser phaser = new Phaser();

        phaser.register();

        Runnable instance = () -> {

            Printer.begin.print();
            try {
                TimeUnit.SECONDS.sleep(seconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            phaser.arrive();
            Printer.complete.print();
        };


        for (int i = 0; i < noOfTask; i++) {
            phaser.register();
            executorService.execute(instance);
        }
        phaser.arriveAndAwaitAdvance();

        Printer.complete.print();
    }

    @Test
    public void testFuture() {
        Callable<Integer> instance = () -> {

            Printer.begin.print();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Printer.complete.print();

            return random.nextInt(100);
        };

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Set<Future<Integer>> futures = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            futures.add(executorService.submit(instance));
        }

        try {
            Assert.assertEquals(457, futures.stream().mapToInt(f -> {
                try {
                    return f.get().intValue();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                throw new RuntimeException("Summation Failed");
            }).sum());
        } finally {

            executorService.shutdown();
        }
    }
}
