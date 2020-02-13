package org.yaml.lee.demo.threadPool;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.IntStream;

public class ThreadPoolDemo {

    private final String threadGroupName;
    private static final String DEFAULT_THREAD_GROUP_NAME = "MyThreadPool";
    private static ThreadGroup threadGroup;

    private volatile int threadCount;
    private static final int DEFAULT_THREAD_COUNT = 10;
    private static final int MIN_THREAD_COUNT = 4;
    private static final int MAX_THREAD_COUNT = 100;
    private volatile int threadSeq = 0;
    private static final ArrayList<Thread> threads = new ArrayList<>();

    private volatile int maxTaskCount;
    private static final int DEFAULT_MAX_TASK_COUNT = 1000;
    private static final LinkedList<Runnable> taskTeam = new LinkedList<>();

    /**
     * 任务线程比
     * 当任务队列中等待的数量超过可用线程对应倍数时，增加可用线程数量；反之，则减少
     * 默认任务:线程 = 10:1
     */
    private volatile float maxTaskThreadRate;
    private static final float DEFAULT_MAX_TASK_THREAD_RATE = 10;

    /**
     * 维护线程数量频率
     * 守护线程每隔对应毫秒判断任务线程比，对可用线程数进行增减
     * 默认每10秒维护一次
     */
    private volatile long maintainThreadsFrequency;
    private static final long DEFAULT_MAINTAIN_THREADS_FREQUENCY = 2_000;

    /**
     * 拒绝策略，当任务队列已满时，对提交任务的处理方法
     * 1.默认抛出“任务队列已满”异常
     * 2.无操作，任务队列会无上限，有OutOfMemory风险
     * 3.return，任务会被直接丢弃
     * 4.Thread.sleep(mills)，commit进入阻塞状态
     */
    private final RefusePolicy refusePolicy;
    private static final RefusePolicy DEFAULT_REFUSE_POLICY = () -> {
        throw new FullWaitingTaskException("任务队列已满");
    };

    public interface RefusePolicy {
        void refuse() throws FullWaitingTaskException;
    }

    public ThreadPoolDemo() {
        this(DEFAULT_THREAD_GROUP_NAME, DEFAULT_THREAD_COUNT, DEFAULT_MAX_TASK_COUNT,
                DEFAULT_REFUSE_POLICY, DEFAULT_MAX_TASK_THREAD_RATE,
                DEFAULT_MAINTAIN_THREADS_FREQUENCY);
    }

    public ThreadPoolDemo(String threadGroupName, int initThreadCount, int maxTaskCount,
                          RefusePolicy refusePolicy, float maxTaskThreadRate,
                          long maintainThreadsFrequency) {
        this.threadGroupName = threadGroupName;
        threadCount = initThreadCount;
        this.maxTaskCount = maxTaskCount;
        this.refusePolicy = refusePolicy;
        this.maxTaskThreadRate = maxTaskThreadRate;
        this.maintainThreadsFrequency = maintainThreadsFrequency;
        init();
    }

    private void init() {
        threadGroup = new ThreadGroup(threadGroupName);
        IntStream.range(0, threadCount).forEach(
                i -> createThread());
        Thread maintainThreadCount = new Thread(threadGroup, () -> {
            do {
                try {
                    Thread.sleep(maintainThreadsFrequency);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                threadCount = (int) (taskTeam.size() / maxTaskThreadRate);
                if (threadCount > MAX_THREAD_COUNT) threadCount = MAX_THREAD_COUNT;
                if (threadCount < MIN_THREAD_COUNT) threadCount = MIN_THREAD_COUNT;
                while (threadCount > threads.size()) {
                    createThread();
                }
                while (threadCount < threads.size()) {
                    closeThread();
                }
            } while (true);
        }, threadGroupName + "-MaintainThreadCount");
        maintainThreadCount.setDaemon(true);
        maintainThreadCount.start();
    }

    private void closeThread() {
        for (int i = 0; i < threads.size(); i++) {
            //预先休眠足够时间，同步ThreadState
            /*try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            ThreadWithStates thread = (ThreadWithStates) (threads.get(i));
            if (thread.getThreadState() == ThreadState.BLOCKED) {
                thread.close();
                synchronized (threads) {
                    threads.remove(thread);
                }
                return;
            }
        }
    }

    private void createThread() {
        ThreadWithStates t = new ThreadWithStates(threadGroup, threadGroupName + threadSeq++);
        synchronized (threads) {
            threads.add(t);
        }
        t.start();
    }

    public void commit(Runnable task) throws FullWaitingTaskException {
        if (task == null) {
            throw new NullPointerException("提交任务为空");
        }
        synchronized (taskTeam) {
            if (taskTeam.size() >= maxTaskCount) {
                refusePolicy.refuse();
            }
            taskTeam.addLast(task);
            taskTeam.notifyAll();
        }
    }

    public void close() {
        while (threads.size() > 0) {
            while (taskTeam.size() > 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            closeThread();
        }
    }

    public void setMaxTaskCount(int maxTaskCount) {
        this.maxTaskCount = maxTaskCount;
    }

    public void setMaxTaskThreadRate(float maxTaskThreadRate) {
        this.maxTaskThreadRate = maxTaskThreadRate;
    }

    public void setMaintainThreadsFrequency(long maintainThreadsFrequency) {
        this.maintainThreadsFrequency = maintainThreadsFrequency;
    }

    public String getThreadGroupName() {
        return threadGroupName;
    }

    public int getThreadsSize() {
        return threads.size();
    }

    public int getTaskTeamSize() {
        return taskTeam.size();
    }

    public int getMaxTaskCount() {
        return maxTaskCount;
    }

    public float getMaxTaskThreadRate() {
        return maxTaskThreadRate;
    }

    public long getMaintainThreadsFrequency() {
        return maintainThreadsFrequency;
    }

    static class FullWaitingTaskException extends Exception {
        FullWaitingTaskException(String message) {
            super(message);
        }
    }

    private enum ThreadState {
        NEW, RUNNABLE, BLOCKED, DEAD
    }

    /**
     * 封装Thread，添加可操作state
     */
    public static class ThreadWithStates extends Thread {
        ThreadWithStates(ThreadGroup group, String name) {
            super(group, name);
        }

        private volatile ThreadState threadState = ThreadState.NEW;

        ThreadState getThreadState() {
            return threadState;
        }

        void close() {
            synchronized (taskTeam) {
                threadState = ThreadState.DEAD;
                this.interrupt();
            }
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName()+" born.");
            Runnable runnable;
            shutdown:
            while (threadState != ThreadState.DEAD) {
                synchronized (taskTeam) {
                    while (taskTeam.size() == 0) {
                        try {
                            threadState = ThreadState.BLOCKED;
                            taskTeam.wait();
                        } catch (InterruptedException e) {
                            if (threadState == ThreadState.DEAD) {
                                break shutdown;
                            }
                        }
                    }
                    runnable = taskTeam.removeFirst();
                }
                threadState = ThreadState.RUNNABLE;
                runnable.run();
            }
            System.out.println(Thread.currentThread().getName()+" go die.");
        }
    }

}
