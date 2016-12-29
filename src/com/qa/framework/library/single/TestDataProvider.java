package com.qa.framework.library.single;

import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/12/7.
 */
public class TestDataProvider implements Iterator {
    protected static Logger logger = Logger.getLogger(TestDataProvider.class);
    private List<Object[]> testDatas;
    private Iterator iterator;

    public TestDataProvider() {
        testDatas = getTestDataList();
        iterator = getTestDataList().iterator();
    }

    public List<Object[]> getTestDataList() {
        ProcessorTestData processorTestData = new ProcessorTestData();
        consumedTestDate c = new consumedTestDate();
        productTestData p = new productTestData();
        Thread prodThread = new Thread(p);
        Thread consThread = new Thread(c);
        //Starting producer and Consumer thread
        prodThread.start();
        consThread.start();
        try {
            prodThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (!c.isWait()) {
            try {
                logger.info("消费者仍在执行陷入等待");
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            consThread.interrupt();
            consThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return c.getTestdatas();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Object next() {

        return iterator.next();
    }

    @Override
    public void remove() {
        iterator.remove();
    }


}
