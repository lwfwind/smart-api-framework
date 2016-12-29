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

    public static void processSingleTestdata(TestData testData) {
        StringCache stringCache1 = new StringCache();
        ProcessorMethod.processBefore(testData);
        ProcessorMethod.processSetupParam(testData, stringCache1);
        ProcessorMethod.processSetupResultParam(testData, stringCache1);
        ProcessorMethod.processTestDataParam(testData, stringCache1);
    }

    /**
     * Process.
     */
    public void process() {
        processBefore();
        processSetupParam();
        processTestDataParam();
    }

    /**
     * Process before.
     */
    @SuppressWarnings("unchecked")
    public void processBefore() {
        for (TestData testData : testDataList) {
            ProcessorMethod.processBefore(testData);
        }
    }

    /**
     * 处理setup中param的占位
     */
    public void processSetupParam() {
        for (TestData testData : testDataList) {
            ProcessorMethod.processSetupParam(testData, stringCache);
        }
    }

    /**
     * 处理setup中param的占位
     */
    private void processSetupResultParam() {
        for (TestData testData : testDataList) {
            ProcessorMethod.processSetupResultParam(testData, stringCache);
        }
    }

    /**
     * Process test data param.
     * 处理正常流程中的param的sql,从其他函数和setup接受值问题
     */
    public void processTestDataParam() {
        for (TestData testData : testDataList) {
            ProcessorMethod.processTestDataParam(testData, stringCache);
        }

    }

    public void clearStringCache() {
        stringCache.mapCache.clear();
        logger.debug("清空StringCahe");
    }
}

