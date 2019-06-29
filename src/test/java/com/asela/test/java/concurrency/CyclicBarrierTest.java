package com.asela.test.java.concurrency;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class CyclicBarrierTest {

    @Test
    public void testBasic() throws InterruptedException {

        CyclicBarrier cyclicBarrier = new CyclicBarrier(4);
        Phaser phaser = new Phaser();

        boolean useBarrier = false;
        int limit = 10;
        Map<String, Integer> map = Collections.synchronizedMap(new HashMap<>());
        Runnable task =() -> {
            for(int i = 0 ; i < limit; i++ ) {
                System.out.println(i);
                if(useBarrier) {
                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            }
            phaser.arrive();
        };


        ExecutorService executorService =  Executors.newFixedThreadPool(4);
        phaser.register();
        executorService.execute(task);
        phaser.register();
        executorService.execute(task);
        phaser.register();
        executorService.execute(task);
        phaser.register();
        executorService.execute(task);
        phaser.register();
        phaser.arriveAndAwaitAdvance();
    }
}
