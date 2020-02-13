package org.yaml.lee.demo;

import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.Optional;

public class ThreadException {

    static class uncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            Optional.of(e).ifPresent(System.out::println);
            Optional.of(t).ifPresent(System.out::println);
            Optional.of(t.getStackTrace())
                    .ifPresent(trace -> Arrays.asList(trace).stream()
                            //.filter(s -> !s.isNativeMethod())
                            .forEach(s -> System.out.println("at " + s.getClassName() + "." + s.getMethodName() + "(" + s.getFileName() + ":" + s.getLineNumber() + ")")));
        }
    }

    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            int i = 0;
            i = 5 / i;
        }, "withException");

        /*t.setUncaughtExceptionHandler((t1,e)->{
            Optional.of(t1).ifPresent(System.out::println);
            Optional.of(e).ifPresent(System.out::println);
        });*/

        t.setUncaughtExceptionHandler(new uncaughtExceptionHandler());

        t.start();
    }

}
