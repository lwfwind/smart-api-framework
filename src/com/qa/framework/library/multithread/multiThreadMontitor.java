package com.qa.framework.library.multithread;


import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Administrator on 2016/9/13.
 */
public class multiThreadMontitor implements Runnable {
    private static final Logger logger = Logger.getLogger(multiThreadMontitor.class);
    private ThreadPoolExecutor executor;
    private Boolean run=true;
    private int seconds;
    public multiThreadMontitor(ExecutorService executorService, int delay)
    {
        this.executor =(ThreadPoolExecutor) executorService;
        this.seconds=delay;
    }
    @Override
    public void run() {
        logger.info("-------- Now multiThreadMontitor is working --------");
        while(run){
            logger.info(
                    String.format("[multiThreadMontitor]-----> [%d/%d] Active: %d, Completed: %d, Task: %d, isShutdown: %s, isTerminated: %s",
                    this.executor.getPoolSize(),
                    this.executor.getCorePoolSize(),
                    this.executor.getActiveCount(),
                    this.executor.getCompletedTaskCount(),
                    this.executor.getTaskCount(),
                    this.executor.isShutdown(),
                    this.executor.isTerminated()));
            logger.info(this.executor.toString());
            shutDown();
        }
    }
    public void shutDown(){
        try {
            Thread.sleep(seconds*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (this.executor.getCompletedTaskCount()==this.executor.getTaskCount()&&this.executor.getActiveCount()==0){
            this.executor.shutdown();
        }
        if (this.executor.isTerminated()) {
            run = false;
            System.out.println("-------- Now multiThreadMontitor is close --------");
        }
    }
}
