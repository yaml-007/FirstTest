package org.yaml.lee.demo;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * 限制活动线程不超过指定个数
 */
public class ThreadTeam {
    private final Object LOCK = new Object();

    private final int activeLimit = 10;
    private List<Long> waitTeam = new LinkedList<>();
    private List<Long> activeTeam = new LinkedList<>();

    private void threadCreator() {
        Random random = new Random();
        int totalThreadCount = random.nextInt(100), i = 0;
        System.out.println(totalThreadCount);
        while (i < totalThreadCount) {
            new Thread(() -> {
                //判断活跃列表是否满员
                synchronized (LOCK) {
                    while (activeTeam.size() >= activeLimit) {
                        try {
                            waitTeam.add(Thread.currentThread().getId());
                            LOCK.wait();
                            waitTeam.remove(Thread.currentThread().getId());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    activeTeam.add(Thread.currentThread().getId());
                }

                System.out.println(Thread.currentThread().getName() + " running. Active team size : " + activeTeam.size());
                try {
                    Thread.sleep(random.nextInt(10_000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                activeTeam.remove(Thread.currentThread().getId());
                System.out.println(Thread.currentThread().getName() + " finish. Active team size : " + activeTeam.size());
                System.out.println(waitTeam.size()+" is waiting.");
                synchronized (LOCK){
                    if (waitTeam.size() > 0) {
                        LOCK.notifyAll();
                    }
                }
            }).start();
            i++;
        }
    }

    public static void main(String[] args) {
        ThreadTeam test = new ThreadTeam();
        test.threadCreator();
    }
}
