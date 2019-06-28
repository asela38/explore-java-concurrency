package com.asela.test.java.concurrency;

import org.junit.Test;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.IntStream;

public class ReentrantReadWriteLockForHahMap {

    @Test
    public void testHashMapForConcurrency_Issue() throws InterruptedException {
        Map<Long, String> map = new HashMap<>();
        verify(map, k -> map.get(k), (k1, v) -> map.put(k1, v));
    }

    @Test
    public void testHashMapForConcurrency_FixWithSynchronizedMap() throws InterruptedException {
        Map<Long, String> map = Collections.synchronizedMap(new HashMap<>());
        verify(map, k -> map.get(k), (k1, v) -> map.put(k1, v));
    }

    @Test
    public void testHashMapForConcurrency_FixReadWriteLock() throws InterruptedException {


        Map<Long, String> map = new HashMap<>();

        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

        verify(map, k -> {
            try {
                readWriteLock.readLock().lock();
                return map.get(k);
            } finally {
                readWriteLock.readLock().unlock();
            }
        }, (k1, v) -> {
            try {
                readWriteLock.writeLock().lock();
                map.put(k1, v);
            } finally {
                readWriteLock.writeLock().unlock();
            }
        });
    }

    private void verify(Map<Long, String> map, Function<Long, String> get, BiConsumer<Long, String> put) throws InterruptedException {
        Random random = new Random(5);

        Lock printLock = new ReentrantLock();
        Runnable task = () -> {
            Printer.begin.print();
            while (true) {
                long l = random.nextInt(1_000);
                put.accept(l, Long.toBinaryString(l));
                if (get.apply(l) == null) {
                    printLock.lock();
                    System.out.println("Map doesn't have : " + l);
                    System.out.flush();
                    printLock.unlock();
                }

                if (Thread.interrupted() == true) {
                    Printer.interrupted.print();
                    break;
                }
            }
            Printer.complete.print();
        };

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        HashSet<Future<?>> futures = new HashSet<>();
        IntStream.range(1, 5).forEach(i -> {
            futures.add(executorService.submit(task));
        });


        Thread.sleep(3_000);
        for (Future<?> future : futures) {
            future.cancel(true);
        }
        System.out.println("map.size() = " + map.size());
    }

}
