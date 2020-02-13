package org.yaml.lee.demo;

public class ThreadCloseForce {

    /**
     * 将实际操作放在守护线程中，通过结束父线程，强制结束守护线程
     */
    public static class ForceClosedThread extends Thread {
        private boolean finishFlag = false;

        @Override
        public void run() {
            Thread act = new Thread(() -> {
                //用sleep模拟长时间操作
                try {
                    Thread.sleep(1_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("act正常结束。");
            });
            act.setDaemon(true);
            act.start();
            try {
                act.join();
            } catch (InterruptedException e) {
                System.out.println("运行超时，强制结束。");
            }
            finishFlag = true;
        }

        /**
         * 在给定等待时间内，若act没有执行完成，则强制结束线程。
         * @param waitTime 最长等待时间
         */
        void shutdown(long waitTime) {
            long beginTime = System.currentTimeMillis();
            while(!finishFlag){
                if(beginTime + waitTime < System.currentTimeMillis()){
                    this.interrupt();
                    break;
                }
                //休眠的目的是让主线程休眠，以便cpu切换线程更新缓存中的finishFlag
                //若将finishFlag声明为volatile变量，则不需要休眠，但维护volatile变量消耗资源极可能大于休眠
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        long beginTime = System.currentTimeMillis();
        ForceClosedThread forceClosedThread = new ForceClosedThread();
        forceClosedThread.start();
        long waitTime = 3_000;
        forceClosedThread.shutdown(waitTime);
        System.out.println(System.currentTimeMillis() - beginTime);
    }

}
