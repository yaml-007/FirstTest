package org.yaml.lee.demo;

public class ThreadCloseByInterrupt {

    public static class SleepThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    break;
                }
            }
            System.out.println("SleepThread stop.");
        }
    }

    public static class IsInterruptedThread extends Thread {
        @Override
        public void run() {
            while(true){
                if(this.isInterrupted()){
                    break;
                }
            }
            System.out.println("IsInterruptThread stop.");
        }
    }

    public static void main(String[] args) {
        SleepThread sleepThread = new SleepThread();
        sleepThread.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sleepThread.interrupt();

        IsInterruptedThread isInterruptedThread = new IsInterruptedThread();
        isInterruptedThread.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isInterruptedThread.interrupt();
    }

}
