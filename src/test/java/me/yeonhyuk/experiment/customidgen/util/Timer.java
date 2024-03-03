package me.yeonhyuk.experiment.customidgen.util;

import java.time.Duration;
import java.time.Instant;

public class Timer {

    private Instant beforeTime = null;
    private Instant afterTime = null;

    public Timer() {

    }

    public void start() {
        beforeTime = Instant.now();
    }
    public void end() {
        afterTime = Instant.now();
    }
    public void printResult() {
        long diffTime = Duration.between(beforeTime, afterTime).toNanos();
        System.out.println("=============================================");
        System.out.println("=============================================");
        System.out.println("Time Duration(ns) : " + diffTime);
        System.out.println("=============================================");
        System.out.println("=============================================");
    }
}
