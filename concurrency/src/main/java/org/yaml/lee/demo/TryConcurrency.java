package org.yaml.lee.demo;

public class TryConcurrency {
    public static void main(String[] args) {
        //lambda 语法
        Thread t1 = new Thread(()->{
            System.out.println("true = " + 1);
        });
        t1.start();
    }
}
