package com.qa.framework.core;

import com.qa.framework.bean.TestCase;
import com.qa.framework.bean.TestSuite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XmlDataProvider implements Iterator {


    private TestSuiteConvertor testSuiteConvertor;
    private Iterator iterator;
    private ParamValueProcessor paramValueProcessor;
    private TestSuite testSuite;

    /**
     * Instantiates a new Xml data provider.
     *
     * @param xmlPath the xml path
     */
    public XmlDataProvider(String xmlPath) {
        new GlobalConvertor();
        testSuiteConvertor = new TestSuiteConvertor(xmlPath);
        testSuite = testSuiteConvertor.getTestSuite();
        iterator = testSuite.getTestCaseList().iterator();
    }

    /**
     * Instantiates a new Xml data provider.
     *
     * @param xmlPath      the xml path
     * @param testDataName the test data name
     */
    public XmlDataProvider(String xmlPath, String testDataName) {
        new GlobalConvertor();
        testSuiteConvertor = new TestSuiteConvertor(xmlPath);
        testSuite = testSuiteConvertor.getTestSuite();
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
                iterator.next(), testSuite
        };
    }

    public void remove() {
        iterator.remove();
    }
}
