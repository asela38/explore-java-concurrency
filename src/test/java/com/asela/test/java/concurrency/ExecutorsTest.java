package com.asela.test.java.concurrency;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class ExecutorsTest {

    private Random random = new Random(1L);


    @FunctionalInterface
    private interface Printer {
        void print();
    }

    private Function<String, Printer> print = string -> () -> System.out.printf("%s=%s%n", Thread.currentThread().getName(), string);

    private Printer begin = print.apply("Begin");
    private Printer complete = print.apply("Complete");

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
        begin.print();

        final Phaser phaser = new Phaser();

        phaser.register();

        Runnable instance = () -> {

            begin.print();
            try {
                TimeUnit.SECONDS.sleep(seconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            phaser.arrive();
            complete.print();
        };


        for (int i = 0; i < noOfTask; i++) {
            phaser.register();
            executorService.execute(instance);
        }
        phaser.arriveAndAwaitAdvance();

        complete.print();
    }

    @Test
    public void testFuture() {
        Callable<Integer> instance = () -> {

            begin.print();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            complete.print();

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
