package org.yaml.lee.demo.runnable;

public class RunnableClass implements Runnable {
    private int index = 1;
    private final Object LOCK = new Object();
//    private final int MAX = 5000;

    static {
        //静态代码块最先加载，只能用类的class来加锁
        synchronized (RunnableClass.class){
        }
    }

    //静态方法利用类的class加锁
    public static synchronized void demo(){
    }

    //利用this加锁
//    public synchronized void run() {
    public void run() {
        int MAX = 500;
        while (true) {
            //利用括号中的实例加锁
            synchronized (LOCK) {
                if (index > MAX) break;
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " index = " + index++);
            }
        }
    }
}
