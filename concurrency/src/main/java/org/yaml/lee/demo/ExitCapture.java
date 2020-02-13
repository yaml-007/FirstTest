package org.yaml.lee.demo;

public class ExitCapture {
    public static void main(String[] args) {
        //为程序添加进程，当程序意外终止时，能做些释放资源等操作 对kill -9无效
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("release resource...");
        }));

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("I am working...");
        }
    }
}
