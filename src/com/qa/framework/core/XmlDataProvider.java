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

    /**
     * Instantiates a new Xml data provider.
     *
     * @param xmlPath the xml path
     */
    public XmlDataProvider(String xmlPath) {
        dataConvertor = new DataConvertor(xmlPath);
        dataConfig = dataConvertor.getDataConfig();
        paramValueProcessor = new ParamValueProcessor(dataConfig);
        paramValueProcessor.process();
        iterator = dataConfig.getTestDataList().iterator();

    }

    /**
     * Instantiates a new Xml data provider.
     *
     * @param xmlPath      the xml path
     * @param testDataName the test data name
     */
    public XmlDataProvider(String xmlPath, String testDataName) {
        dataConvertor = new DataConvertor(xmlPath);
        dataConfig = dataConvertor.getDataConfig();
        paramValueProcessor = new ParamValueProcessor(dataConfig, testDataName);
        paramValueProcessor.process();
        List<TestData> testDataList = new ArrayList<TestData>();
        for (TestData testData : dataConfig.getTestDataList()) {
            if (testData.getName().equals(testDataName)) {
                testDataList.add(testData);
            }
        }
        iterator = testDataList.iterator();

    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public Object next() {
        return new Object[]{
                iterator.next(), dataConfig.getUrl(), dataConfig.getHttpMethod()
        };
    }

    public void remove() {
        iterator.remove();
    }
}
