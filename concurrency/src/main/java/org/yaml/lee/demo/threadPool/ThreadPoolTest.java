package org.yaml.lee.demo.threadPool;

import java.util.Optional;
import java.util.stream.IntStream;

public class ThreadPoolTest {
    public static void main(String[] args) {
        ThreadPoolDemo threadPool = new ThreadPoolDemo();
        IntStream.range(0, 1000).forEach(i -> {
            try {
                threadPool.commit(() -> {
                    System.out.println("Task-" + i + " start.");
                    Optional.of(threadPool.getThreadsSize()+"========================")
                            .ifPresent(System.out::println);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Optional.of(threadPool.getThreadsSize()+"========================")
                            .ifPresent(System.out::println);
                    System.out.println("Task-" + i + " finish.");
                });
            } catch (ThreadPoolDemo.FullWaitingTaskException e) {
                e.printStackTrace();
            }
        });
        try {
            Thread.sleep(3_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        threadPool.close();
        IntStream.range(1000, 1200).forEach(j -> {
            try {
                threadPool.commit(() -> {
                    System.out.println("Task-" + j + " start.");
                    Optional.of(threadPool.getThreadsSize()+"========================")
                            .ifPresent(System.out::println);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Optional.of(threadPool.getThreadsSize()+"========================")
                            .ifPresent(System.out::println);
                    System.out.println("Task-" + j + " finish.");
                });
            } catch (ThreadPoolDemo.FullWaitingTaskException e) {
                e.printStackTrace();
            }
        });
    }
}
