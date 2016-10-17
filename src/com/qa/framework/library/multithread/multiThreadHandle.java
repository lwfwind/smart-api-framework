package com.qa.framework.library.multithread;

import java.util.concurrent.*;

/**
 * Created by Administrator on 2016/9/19.
 */
public class multiThreadHandle {
    public static multiThreadHandle handleThread=null;
    private static ExecutorService executorService;
    private static Thread monitorThread;
    public multiThreadHandle(){
        if(handleThread==null){
            synchronized (multiThreadHandle.class){
                handleThread=new multiThreadHandle();
            }
        }
    }
    static {
        executorService=Executors.newCachedThreadPool();
        multiThreadMontitor monitor = new multiThreadMontitor(executorService, 1);
        monitorThread = new Thread(monitor);
        monitorThread.start();
    }
    public static void buildThreadPool(Runnable t) {
        executorService.execute(t);
    }
    public static void buildThreadPool(Runnable t,int circulateSum) {
        for (int i=0;i<circulateSum;i++) {
            executorService.execute(t);
        }
    }
    public static boolean isEnd(){
        boolean isend=false;
        try {
            monitorThread.join();
            isend=true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return isend;
    }


}
