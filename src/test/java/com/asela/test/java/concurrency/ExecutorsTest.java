package com.asela.test.java.concurrency;

import org.junit.Test;

import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class ExecutorsTest {

    @FunctionalInterface
    private interface Printer {
         void print();
    }

    private Function<String, Printer> print = string -> () -> System.out.printf("%s=%s%n", Thread.currentThread().getName(), string);

    private Printer begin = print.apply("Begin");
    private Printer complete = print.apply("Complete");

    @Test
    public void testSingleThreadExecutor() {
        checkExecutorService(Executors.newSingleThreadExecutor(), 5, 2);
    }

    @Test
    public void testDualThreadExecutor() {
        checkExecutorService(Executors.newFixedThreadPool(2), 5, 2);
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


        for(int i = 0; i < noOfTask; i++ ) {
            phaser.register();
            executorService.execute(instance);
        }
        phaser.arriveAndAwaitAdvance();

        complete.print();
    }
}
