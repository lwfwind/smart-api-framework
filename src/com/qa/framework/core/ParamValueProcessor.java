package com.qa.framework.core;

import com.qa.framework.bean.DataConfig;
import com.qa.framework.bean.TestData;
import com.qa.framework.cache.JsonPairCache;
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
    private JsonPairCache jsonPairCache;

    /**
     * Instantiates a new Param value processor.
     *
     * @param dataConfig the data config
     */
    public ParamValueProcessor(DataConfig dataConfig) {
        this.dataConfig = dataConfig;
        this.testDataList = dataConfig.getTestDataList();
        jsonPairCache = new JsonPairCache();
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
        jsonPairCache = new JsonPairCache();
    }

    public static void processTestData(TestData testData) {
        JsonPairCache jsonPairCache1 = new JsonPairCache();
        ProcessorMethod.processBefore(testData);
        ProcessorMethod.processSetupParam(testData, jsonPairCache1);
        ProcessorMethod.processSetupResultParam(testData, jsonPairCache1);
        ProcessorMethod.processTestDataParam(testData, jsonPairCache1);
    }

    public void clearStringCache() {
        jsonPairCache.getMap().clear();
        logger.debug("清空StringCache");
    }
}

