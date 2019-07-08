package com.asela.test.java.completablefuture;

import com.asela.test.java.concurrency.Printer;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompletableFutureTest {


    @Test
    public void testCompletableFutureDefault() throws InterruptedException {
        Printer.begin.print();

        CompletableFuture.runAsync(() -> {
            Printer.begin.print();
            System.out.println(" I am running asynchronously");
            Printer.complete.print();
        });


        Thread.sleep(100);
        Printer.complete.print();

    }

    @Test
    public void testCompletableFutureWithExecutor() {
        Printer.begin.print();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        CompletableFuture.runAsync(() -> {
            Printer.begin.print();
            System.out.println(" I am running asynchronously");
            Printer.complete.print();
        }, executorService);


        Printer.complete.print();

        executorService.shutdown();

    }

    @Test
    public void testCompletableFutureDefaultWithSuppler() throws InterruptedException {
        Printer.begin.print();

        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
            Printer.begin.print();
            System.out.println(" I am running asynchronously");
            Printer.complete.print();

            return 0;
        });


        System.out.println("completableFuture.join() = " + completableFuture.join());
        Printer.complete.print();

    }

    @Test
    public void testCompletableFutureWithExecutorWithSuppler() {
        Printer.begin.print();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
            Printer.begin.print();
            System.out.println(" I am running asynchronously");
            Printer.complete.print();
            return 0;
        }, executorService);


        System.out.println("integerCompletableFuture.join() = " + integerCompletableFuture.join());
        
        Printer.complete.print();

        executorService.shutdown();

    }

    @Test
    public void testCompletableFutureWithExecutorWithSupplerForcingComplete() {
        Printer.begin.print();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
            Printer.begin.print();
            System.out.println(" I am running asynchronously");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Printer.complete.print();
            return 0;
        }, executorService);

        integerCompletableFuture.complete(Integer.MIN_VALUE);
        System.out.println("integerCompletableFuture.join() = " + integerCompletableFuture.join());

        Printer.complete.print();

        executorService.shutdown();

    }

    @Test
    public void testCompletableFutureWithExecutorWithSupplerForcingCompleteAfterCompleted() {
        Printer.begin.print();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
            Printer.begin.print();
            System.out.println(" I am running asynchronously");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Printer.complete.print();
            return 0;
        }, executorService);


        System.out.println("integerCompletableFuture.join() = " + integerCompletableFuture.join());
        integerCompletableFuture.complete(Integer.MIN_VALUE);
        System.out.println("integerCompletableFuture.join() = " + integerCompletableFuture.join());

        Printer.complete.print();

        executorService.shutdown();

    }

    @Test
    public void testCompletableFutureWithExecutorWithSupplerForcingObrudeAfterCompleted() {
        Printer.begin.print();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
            Printer.begin.print();
            System.out.println(" I am running asynchronously");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Printer.complete.print();
            return 0;
        }, executorService);


        System.out.println("integerCompletableFuture.join() = " + integerCompletableFuture.join());
        integerCompletableFuture.obtrudeValue(Integer.MIN_VALUE);
        System.out.println("integerCompletableFuture.join() = " + integerCompletableFuture.join());

        Printer.complete.print();

        executorService.shutdown();

    }

    @Test
    public void createNew() {

        CompletableFuture<Void> cf = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            Printer.begin.print();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cf.complete(null);
            Printer.complete.print();
        });

        Void nil = cf.join();
        System.out.println("nil = " + nil);
    }
}
