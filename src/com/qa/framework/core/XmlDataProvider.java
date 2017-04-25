package com.qa.framework.core;

import com.qa.framework.bean.TestCase;
import com.qa.framework.bean.TestSuite;

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
    private TestSuite testSuite;

    /**
     * Instantiates a new Xml data provider.
     *
     * @param xmlPath the xml path
     */
    public XmlDataProvider(String xmlPath) {
        dataConvertor = new DataConvertor(xmlPath);
        testSuite = dataConvertor.getTestSuite();
        iterator = testSuite.getTestCaseList().iterator();
    }

    /**
     * Instantiates a new Xml data provider.
     *
     * @param xmlPath      the xml path
     * @param testDataName the test data name
     */
    public XmlDataProvider(String xmlPath, String testDataName) {
        dataConvertor = new DataConvertor(xmlPath);
        testSuite = dataConvertor.getTestSuite();
        List<TestCase> testCaseList = new ArrayList<TestCase>();
        for (TestCase testCase : testSuite.getTestCaseList()) {
            if (testCase.getName().equals(testDataName)) {
                testCaseList.add(testCase);
            }
        }
        iterator = testCaseList.iterator();

    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public Object next() {
        return new Object[]{
                iterator.next(), testSuite.getUrl(), testSuite.getHttpMethod()
        };
    }

    public void remove() {
        iterator.remove();
    }
}
