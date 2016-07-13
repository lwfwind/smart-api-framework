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
    private String xmlPath;

    private DataConvertor dataConvertor;

    private Iterator iterator;

    private ParamValueGenerator paramValueGenerator;

    private DataConfig dataConfig;
    private List<TestData> testDatas = new ArrayList<TestData>();

    public XmlDataProvider(String xmlPath) {
        this.xmlPath = xmlPath;
        dataConvertor = new DataConvertor(xmlPath);
        dataConfig = dataConvertor.getDataConfig();
        paramValueGenerator = new ParamValueGenerator(dataConfig);
        iterator = dataConfig.getTestDataLists().iterator();

    }

    public XmlDataProvider(String xmlPath, String testDataName) {
        this.xmlPath = xmlPath;
        dataConvertor = new DataConvertor(xmlPath);
        dataConfig = dataConvertor.getDataConfig();
        paramValueGenerator = new ParamValueGenerator(dataConfig, testDataName);
        for (TestData testData : dataConfig.getTestDataLists()) {
            if (testData.getName().equals(testDataName)) {
                this.testDatas.add(testData);
            }
        }
        iterator = testDatas.iterator();

    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public Object next() {
        return new Object[]{
                iterator.next(), dataConfig.getUrl(), dataConfig.getHttpMethod(), paramValueGenerator
        };
    }

    public void remove() {
        iterator.remove();
    }
}
