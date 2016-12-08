package com.qa.framework.core;

import com.library.common.DynamicCompileHelper;
import com.library.common.JsonHelper;
import com.library.common.StringHelper;
import com.qa.framework.bean.*;
import com.qa.framework.cache.StringCache;
import com.qa.framework.library.database.DBHelper;
import com.qa.framework.library.reflect.ReflectHelper;
import com.qa.framework.util.StringUtil;
import com.qa.framework.verify.ContainExpectResult;
import com.qa.framework.verify.IExpectResult;
import com.qa.framework.verify.PairExpectResult;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


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
        if (this.testDataList.size()==0){
            logger.info("没有找打相匹配的TestData Name 请检查！");
        }
        stringCache = new StringCache();
    }

    /**
     * Process.
     */
    public void process() {
        processBefore();
        processSetupParam();
        processSetupResultParam();
        processTestDataParam();
    }

    public static void processSingleTestdata(TestData testData){
        StringCache stringCache1=new StringCache();
        ProcessorMethod.processBefore(testData);
        ProcessorMethod.processSetupParam(testData,stringCache1);
        ProcessorMethod.processSetupResultParam(testData,stringCache1);
        ProcessorMethod.processTestDataParam(testData,stringCache1);
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
            ProcessorMethod.processSetupParam(testData,stringCache);
        }
    }
    /**
     * 处理setup中param的占位
     */
    private void processSetupResultParam() {
        for (TestData testData : testDataList) {
            ProcessorMethod.processSetupResultParam(testData,stringCache);
        }
    }
    /**
     * Process test data param.
     * 处理正常流程中的param的sql,从其他函数和setup接受值问题
     */
    public void processTestDataParam() {
        for (TestData testData : testDataList) {
            ProcessorMethod.processTestDataParam(testData,stringCache);
        }

    }

    public void clearStringCache() {
        stringCache.mapCache.clear();
    }
}

