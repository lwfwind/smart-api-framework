package com.qa.framework.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by apple on 15/11/18.
 */
public class DataConfig {
    private List<TestData> testDataList;

    private Map<String, TestData> testDataMap;
    private String url;
    private String httpMethod = "get";

    /**
     * Instantiates a new Data config.
     *
     * @param testDataList the test data list
     */
    public DataConfig(List<TestData> testDataList) {
        this.testDataList = testDataList;
    }

    /**
     * Instantiates a new Data config.
     */
    public DataConfig() {
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
    public List<TestData> getTestDataList() {
        return testDataList;
    }

    /**
     * Add test data.
     *
     * @param testData the test data
     */
    public void addTestData(TestData testData) {
        if (testDataList == null) {
            testDataList = new ArrayList<TestData>();
        }
        testDataList.add(testData);
    }

}
