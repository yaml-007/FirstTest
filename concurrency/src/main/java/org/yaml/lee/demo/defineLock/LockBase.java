package org.yaml.lee.demo.defineLock;

import java.util.Collection;

public interface LockBase {

    class TimeOutException extends Exception {
        public TimeOutException(String message) {
            super(message);
        }
    }

    void lock() throws InterruptedException;

    void lock(long waitTime) throws InterruptedException, TimeOutException;

    void unlock();

    Collection<Thread> getBlockedThreads();

    int getBlockedSize();

}