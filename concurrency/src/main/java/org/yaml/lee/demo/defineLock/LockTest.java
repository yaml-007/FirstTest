package org.yaml.lee.demo.defineLock;

import java.util.Optional;
import java.util.stream.Stream;

public class LockTest {
    public static void main(String[] args) {
        BooleanLock lock = new BooleanLock();
        Stream.of("T1", "T2").forEach(name -> new Thread(() -> {
            try {
                lock.lock(6000);
                Optional.of(Thread.currentThread().getName())
                        .ifPresent(n -> System.out.println(n + " is running."));
                Thread.sleep(3000);
                Optional.of(Thread.currentThread().getName())
                        .ifPresent(n -> System.out.println(n + " finish."));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (LockBase.TimeOutException e) {
                Optional.of(Thread.currentThread().getName())
                        .ifPresent(n -> System.out.println(n + " wait too long to get start. Mission quit."));
            } finally {
                lock.unlock();
            }
        }, name).start());

        new Thread(() -> {
            try {
                lock.lock();
                Optional.of(Thread.currentThread().getName())
                        .ifPresent(n -> System.out.println(n + " is running."));
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Optional.of(Thread.currentThread().getName())
                    .ifPresent(n -> System.out.println(n + " finish."));
            lock.unlock();
        }, "T3").start();
    }
}
