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

    public DataConfig(List<TestData> testDataList) {
        this.testDataList = testDataList;
    }

    public DataConfig() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public List<TestData> getTestDataList() {
        return testDataList;
    }

    public void addTestData(TestData testData) {
        if (testDataList == null) {
            testDataList = new ArrayList<TestData>();
        }
        testDataList.add(testData);
    }

}
