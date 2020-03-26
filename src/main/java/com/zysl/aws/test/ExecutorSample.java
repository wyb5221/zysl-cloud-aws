package com.zysl.aws.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorSample {
    public static void main(String[] args) {
        //创建核心线程数为5的线程池
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 20; i++) {
//            new Thread(new RunnableThread()).start();
            executor.submit(new RunnableThread());
        }
//        executor.shutdownNow();
        executor.isTerminated();
    }
}
