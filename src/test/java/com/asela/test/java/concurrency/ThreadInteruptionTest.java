package com.asela.test.java.concurrency;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadInteruptionTest {

    @Test
    public void testSleep() throws InterruptedException {
        Printer.begin.print();
        Runnable task = () -> {
            Printer.begin.print();

            try {
                Thread.sleep(10_000);
                Printer.complete.print();
            } catch (InterruptedException e) {
                Printer.interrupted.print();
            }
        };

        Thread thread = new Thread(task);
        thread.start();


        Thread.sleep(3_000);
        thread.interrupt();
        Thread.sleep(3_000);
        Printer.complete.print();
    }


    @Test
    public void infiniteLoopBreak() throws InterruptedException {
        Printer.begin.print();
        Runnable task = () -> {

            Printer.begin.print();

            int x = 0;
            for (int i = 0 ; i < 11; i++) {

                 for(int j = 0 ; j < 1_000_000_00; j++)
                    x  = i % 100 + j % 169 + x % 2;

                if(Thread.currentThread().isInterrupted()) {
                    System.out.println(x + "-->" + i);
                    Printer.interrupted.print();
                    return;
                }
            }

            System.out.println(x);
            Printer.complete.print();
        };

        Thread thread = new Thread(task);
        thread.start();


        Thread.sleep(2_000);
        thread.interrupt();
        Thread.sleep(2_000);
        Printer.complete.print();
    }
}
