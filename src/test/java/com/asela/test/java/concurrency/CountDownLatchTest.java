package com.asela.test.java.concurrency;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class CountDownLatchTest {

    @Test
    public void test() throws InterruptedException {
        boolean useLatch = true;
        CountDownLatch countDownLatch = new CountDownLatch(1);

        Runnable task = () -> {
            for (int i = 0; i < 1000 ; i++) {
                System.out.print(i +" ");
            }
            if (useLatch) countDownLatch.countDown();
        };


        Executors.newSingleThreadExecutor().execute(task);

        if (useLatch) countDownLatch.await();
    }
}
