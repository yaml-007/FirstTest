package org.yaml.lee.demo.defineLock;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class BooleanLock implements LockBase {

    private boolean locked = false;
    private Thread current;
    private Collection<Thread> blockedThreads = new LinkedList<>();

    @Override
    public synchronized void lock() throws InterruptedException {
        while (locked) {
            blockedThreads.add(Thread.currentThread());
            this.wait();
            blockedThreads.remove(Thread.currentThread());
        }
        locked = true;
        current = Thread.currentThread();
        Thread.sleep(1);
    }

    @Override
    public synchronized void lock(long waitTime) throws InterruptedException, TimeOutException {
        //waitTime需要大于0，若非法，则按lock()处理
        if (waitTime <= 0) {
            lock();
        }
        long remainTime;
        long timeOut = System.currentTimeMillis() + waitTime;
        while (locked) {
            blockedThreads.add(Thread.currentThread());
            remainTime = timeOut - System.currentTimeMillis();
            if (remainTime <= 0) {
                throw new TimeOutException("Time out!");
            }
            this.wait(remainTime);
            blockedThreads.remove(Thread.currentThread());
        }
        locked = true;
        current = Thread.currentThread();
        Thread.sleep(1);
    }

    @Override
    public synchronized void unlock() {
        if (current == Thread.currentThread()) {
            locked = false;
            this.notifyAll();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Collection<Thread> getBlockedThreads() {
        return Collections.unmodifiableCollection(blockedThreads);
    }

    @Override
    public int getBlockedSize() {
        return blockedThreads.size();
    }

}
