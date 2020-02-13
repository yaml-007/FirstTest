package org.yaml.lee.demo;

import java.util.Optional;

public class ThreadDemo {
    public static void main(String[] args) {
        Thread t1 = new Thread("Thread1") {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    System.out.println("i = " + i);
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        System.out.println("t1打断状态：" + t1.isInterrupted());
        t1.start();
        try {
            Thread.sleep(1000);
            System.out.println("t1打断状态：" + t1.isInterrupted());
            //中断线程当前执行的操作，执行下一条操作
            t1.interrupt();
            System.out.println("t1打断状态：" + t1.isInterrupted());
            System.out.println("t1打断状态：" + t1.isInterrupted());
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t1.interrupt();
        System.out.println("t1打断状态：" + t1.isInterrupted());

        new Thread("Thread2") {
            public void run() {
                for (int j = 0; j < 10; j++) {
                    System.out.println("j = " + j);
                    System.out.println("t1打断状态："+t1.isInterrupted());
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        try {
            //父线程等待该线程指定时间后再执行后续代码；若无参数则等该线程执行完，父线程再执行
            t1.join();
//            t1.join(1000);
//            t1.join(1000,1000_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread daemon = new Thread(() -> {
            try {
                Thread.sleep(100_000);
                System.out.println("daemon time out");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        //设置为守护线程，当没有非守护线程存活时，自动结束线程；需放在线程启动前
        daemon.setDaemon(true);
        daemon.start();
        daemon.setPriority(Thread.MAX_PRIORITY);
        Optional.of("守护线程id：" + daemon.getId()).ifPresent(System.out::println);
        Optional.of("守护线程优先级：" + daemon.getPriority()).ifPresent(System.out::println);
    }
}
