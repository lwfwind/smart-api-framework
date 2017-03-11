package com.qa.framework.core;

import com.qa.framework.bean.DataConfig;
import com.qa.framework.bean.TestData;
import com.qa.framework.cache.StringCache;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * 处理param中value的变量
 * Created by apple on 15/11/23.
 */
public class ParamValueProcessor {
    private static final Logger logger = Logger.getLogger(ParamValueProcessor.class);
    private DataConfig dataConfig;
    private List<TestData> testDataList = new ArrayList<TestData>();
    private StringCache stringCache;

    /**
     * Instantiates a new Param value processor.
     *
     * @param dataConfig the data config
     */
    public ParamValueProcessor(DataConfig dataConfig) {
        this.dataConfig = dataConfig;
        this.testDataList = dataConfig.getTestDataList();
        stringCache = new StringCache();
    }

    /**
     * Instantiates a new Param value processor.
     *
     * @param dataConfig   the data config
     * @param testDataName the test data name
     */
    public ParamValueProcessor(DataConfig dataConfig, String testDataName) {
        this.dataConfig = dataConfig;
        for (TestData testData : dataConfig.getTestDataList()) {
            if (testData.getName().equals(testDataName)) {
                this.testDataList.add(testData);
            }
        }
        if (this.testDataList.size() == 0) {
            logger.info("没有找打相匹配的TestData Name 请检查！");
        }
        stringCache = new StringCache();
    }

    public static void processTestData(TestData testData) {
        StringCache stringCache1 = new StringCache();
        ProcessorMethod.processBefore(testData);
        ProcessorMethod.processSetupParam(testData, stringCache1);
        ProcessorMethod.processSetupResultParam(testData, stringCache1);
        ProcessorMethod.processTestDataParam(testData, stringCache1);
    }

    public void clearStringCache() {
        stringCache.mapCache.clear();
        logger.debug("清空StringCahe");
    }
}

