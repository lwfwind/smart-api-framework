package com.qa.framework.library.multithread;


import org.apache.log4j.Logger;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Administrator on 2016/9/13.
 */
public class MonitorThread implements Runnable {
    private static final Logger logger = Logger.getLogger(MonitorThread.class);
    private ThreadPoolExecutor executor;
    private Boolean run=true;
    private int seconds;
    public MonitorThread(ThreadPoolExecutor executor, int delay)
    {
        this.executor = executor;
        this.seconds=delay;
    }
    @Override
    public void run() {
        logger.info("-------- Now MonitorThread is working --------");
        while(run){
            logger.info(
                    String.format("[MonitorThread]-----> [%d/%d] Active: %d, Completed: %d, Task: %d, isShutdown: %s, isTerminated: %s",
                    this.executor.getPoolSize(),
                    this.executor.getCorePoolSize(),
                    this.executor.getActiveCount(),
                    this.executor.getCompletedTaskCount(),
                    this.executor.getTaskCount(),
                    this.executor.isShutdown(),
                    this.executor.isTerminated()));
            System.out.println(this.executor.toString());
            shutDown();
            try {
                Thread.sleep(seconds*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void shutDown(){
        if (this.executor.getCompletedTaskCount()==this.executor.getTaskCount()&&this.executor.getTaskCount()!=0) {
            executor.shutdown();
        }
        if (this.executor.isTerminated()) {
            run = false;
            System.out.println("-------- Now MonitorThread is close --------");
        }
    }
}
