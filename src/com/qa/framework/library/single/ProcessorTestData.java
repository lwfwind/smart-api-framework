package com.qa.framework.library.single;

import com.qa.framework.bean.DataConfig;
import com.qa.framework.bean.TestData;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Administrator on 2016/12/6.
 */
public class ProcessorTestData {
    public static BlockingQueue<Object[]> TestDataQueue = new LinkedBlockingQueue<Object[]>(30);
    protected static Logger logger = Logger.getLogger(ProcessorTestData.class);

    public static int product(DataConfig dataConfig) throws InterruptedException {
        int num = 0;
        if (TestDataQueue.size() == 10) {
            logger.info("队列已满暂不执行--生产");
        }
        List<TestData> testDataList = dataConfig.getTestDataList();
        for (TestData testData : testDataList) {
            Object[] data = new Object[]{testData, dataConfig.getUrl(), dataConfig.getHttpMethod()};
            TestDataQueue.put(data);
            logger.debug("生产者：" + data + "运行次数" + (num++));
        }
        return num;
    }

    public static Object[] consume() throws InterruptedException {
        if (TestDataQueue.size() == 0) {
            logger.info("队列为空暂不执行--消费");
        }
        return TestDataQueue.take();
    }

    public static BlockingQueue getQueue() {
        return TestDataQueue;
    }

    public static boolean ListIsNull() {
        boolean isNUll = false;
        if (TestDataQueue.size() == 0) {
            isNUll = true;
        }
        return isNUll;
    }
}



