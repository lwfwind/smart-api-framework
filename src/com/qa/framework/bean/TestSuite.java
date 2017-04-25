package com.qa.framework.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestSuite {
    private List<TestCase> testCaseList;

    private Map<String, TestCase> testDataMap;
    private String url;
    private String httpMethod = "get";

    /**
     * Instantiates a new Data config.
     *
     * @param testCaseList the test data list
     */
    public TestSuite(List<TestCase> testCaseList) {
        this.testCaseList = testCaseList;
    }

    /**
     * Instantiates a new Data config.
     */
    public TestSuite() {
    }

    /**
     * Gets url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets url.
     *
     * @param url the url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets http method.
     *
     * @return the http method
     */
    public String getHttpMethod() {
        return httpMethod;
    }

    /**
     * Sets http method.
     *
     * @param httpMethod the http method
     */
    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    /**
     * Gets test data list.
     *
     * @return the test data list
     */
    public List<TestCase> getTestCaseList() {
        return testCaseList;
    }

    /**
     * Add test data.
     *
     * @param testCase the test data
     */
    public void addTestCase(TestCase testCase) {
        if (testCaseList == null) {
            testCaseList = new ArrayList<TestCase>();
        }
        testCaseList.add(testCase);
    }

}
