package com.qa.framework.library.single;

import com.qa.framework.bean.DataConfig;
import com.qa.framework.core.DataConvertor;
import com.qa.framework.core.TestXmlData;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016/12/6.
 */
public class productTestData implements Runnable {
    protected Logger logger = Logger.getLogger(productTestData.class);
    List<String> files = Collections.synchronizedList(TestXmlData.getTestCaseFiles());
    private int runNum = 0;

    public productTestData() {
        logger.debug("生产者建立");
    }

    @Override
    public void run() {
        try {
            logger.info("开始处理TestDate中的数据");
            while (files.size() != 0) {
                String filePath;
                synchronized (files) {
                    filePath = this.files.get(0);
                    this.files.remove(0);
                }
                files.remove(filePath);
                DataConvertor dataConvertor = new DataConvertor(filePath);
                DataConfig dataConfig = dataConvertor.getDataConfig();
                runNum = runNum + ProcessorTestData.product(dataConfig);
            }
            logger.info("生产者的运行次数为" + runNum);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

