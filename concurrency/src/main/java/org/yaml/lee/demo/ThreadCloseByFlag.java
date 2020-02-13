package org.yaml.lee.demo;

public class ThreadCloseByFlag {

    public static class FlagThread extends Thread{
        private volatile boolean runningFlag = true;

        @Override
        public void run() {
            while(runningFlag){
                //TODO
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        void shutdown(){
            runningFlag = false;
        }
    }

    public static void main(String[] args) {
        FlagThread ft = new FlagThread();
        ft.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ft.shutdown();
    }

}
