package org.yaml.lee.demo;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class ThreadGroupDemo {

    public static void main(String[] args) {
        ThreadGroup tg1 = new ThreadGroup("tg1");
        ThreadGroup tg2 = new ThreadGroup(tg1, "tg2");

        Thread t1 = new Thread(tg1, "t1") {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    System.out.println(Thread.currentThread().getName() + " finish");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        t1.start();

        Thread t2 = new Thread(tg2, () -> {
            try {
                Thread.sleep(3000);
                System.out.println(Thread.currentThread().getName() + " finish");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2");

        t2.start();

        tg2.setDaemon(true);

        Thread[] threads = new Thread[3];
        Thread[] threads2 = new Thread[3];

        tg1.enumerate(threads);
        Stream.of(threads).forEach(a -> System.out.println(a));

        tg1.enumerate(threads2, false);
        Arrays.asList(threads2).stream().forEach(a -> System.out.println(a));

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(tg1.isDestroyed());
        System.out.println(tg2.isDestroyed());

        tg1.list();

    }

}
