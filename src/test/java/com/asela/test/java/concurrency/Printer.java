package com.asela.test.java.concurrency;

import java.time.LocalTime;
import java.util.function.Function;

@FunctionalInterface
public interface Printer {
    void print();

    Function<String, Printer> print = string -> () -> {
        System.out.printf("[%s]%s=%s%n", LocalTime.now(), Thread.currentThread().getName(), string);
    };

    Printer begin = print.apply("Begin");
    Printer complete = print.apply("Complete");
    Printer interrupted = print.apply("Interrupted");
}
