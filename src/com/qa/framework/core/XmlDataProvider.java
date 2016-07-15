package com.qa.framework.core;

import com.qa.framework.bean.DataConfig;
import com.qa.framework.bean.TestData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by apple on 15/11/18.
 */
public class XmlDataProvider implements Iterator {


    private DataConvertor dataConvertor;
    private Iterator iterator;
    private ParamValueProcessor paramValueProcessor;
    private DataConfig dataConfig;
    private List<TestData> testDataList = new ArrayList<TestData>();

    public XmlDataProvider(String xmlPath) {
        dataConvertor = new DataConvertor(xmlPath);
        dataConfig = dataConvertor.getDataConfig();
        paramValueProcessor = new ParamValueProcessor(dataConfig);
        paramValueProcessor.process();
        iterator = dataConfig.getTestDataList().iterator();

    }

    public XmlDataProvider(String xmlPath, String testDataName) {
        dataConvertor = new DataConvertor(xmlPath);
        dataConfig = dataConvertor.getDataConfig();
        paramValueProcessor = new ParamValueProcessor(dataConfig, testDataName);
        paramValueProcessor.process();
        for (TestData testData : dataConfig.getTestDataList()) {
            if (testData.getName().equals(testDataName)) {
                this.testDataList.add(testData);
            }
        }
        iterator = testDataList.iterator();

    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public Object next() {
        return new Object[]{
                iterator.next(), dataConfig.getUrl(), dataConfig.getHttpMethod(), paramValueProcessor
        };
    }

    public void remove() {
        iterator.remove();
    }
}
