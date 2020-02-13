package org.yaml.lee.demo;

import java.util.stream.Stream;

public class ThreadCommunicate {
    private int i = 1;
    private boolean done = false;

    private final Object LOCK = new Object();

    private void produce() {
        synchronized (LOCK) {
            while (done) {
                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            i++;
            System.out.println(Thread.currentThread().getName() + "->" + i);
            done = true;
            LOCK.notifyAll();
        }
    }

    private void custom() {
        synchronized (LOCK) {
            if (done) {
                System.out.println(Thread.currentThread().getName() + "->" + i);
                done = false;
                LOCK.notifyAll();
            } else {
                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        ThreadCommunicate tc = new ThreadCommunicate();
        Stream.of("p1", "p2").forEach(name ->
                new Thread(() -> {
                    while (true) {
                        tc.produce();
                    }
                }, name).start()
        );

        Stream.of("c1", "c2").forEach(name ->
                new Thread(() -> {
                    while (true) {
                        tc.custom();
                    }
                }, name).start()
        );
    }
}
