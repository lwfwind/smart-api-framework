package com.qa.framework.library.single;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016/12/6.
 */
public class consumedTestDate implements Runnable {
    protected static Logger logger = Logger.getLogger(consumedTestDate.class);
    List<Object[]> Testdatas = Collections.synchronizedList(new ArrayList<Object[]>());
    private volatile boolean shutdownRequested = false;
    private int runNum = 0;

    public consumedTestDate() {
        logger.info("消费者建立");
    }

    @Override
    public void run() {
        logger.debug("消费者等待执行");
        try {
            while (!shutdownRequested) {
                Object[] data = ProcessorTestData.consume();
                logger.debug("消费者消费" + data + "运行次数" + (runNum++));
                Testdatas.add(data);
            }
            logger.info("消费者运行次数为" + runNum);
        } catch (InterruptedException ex) {
            System.out.println("Consumer Interrupted");
        } finally {
            doShutDown();
        }
    }

    public final void doShutDown() {
        shutdownRequested = true;
        logger.info("消费者线程结束");
    }

    public boolean isWait() {
        return ProcessorTestData.ListIsNull();
    }

    public List<Object[]> getTestdatas() {
        return Testdatas;
    }

}
