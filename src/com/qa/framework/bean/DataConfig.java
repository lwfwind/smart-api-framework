package com.qa.framework.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by apple on 15/11/18.
 */
public class DataConfig {
    private List<TestData> testDataLists;

    private Map<String, TestData> testDataMap;
    private String url;
    private String httpMethod = "get";

    public DataConfig(List<TestData> testDataLists) {
        this.testDataLists = testDataLists;
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

    public List<TestData> getTestDataLists() {
        return testDataLists;
    }

    public void setTestDataLists(List<TestData> testDataLists) {
        this.testDataLists = testDataLists;
    }

    public void addTestData(TestData testData) {
        if (testDataLists == null) {
            testDataLists = new ArrayList<TestData>();
        }
        testDataLists.add(testData);
    }

    public void fillMap() {
        if (testDataLists != null) {
            for (TestData testData : testDataLists) {
                if (testDataMap == null) {
                    testDataMap = new HashMap<String, TestData>();
                }
                testDataMap.put(testData.getName(), testData);
            }
        }
    }

    public Map<String, TestData> getTestDataMap() {
        if (testDataMap == null) {
            fillMap();
        }
        return testDataMap;
    }

    public void setTestDataMap(Map<String, TestData> testDataMap) {
        this.testDataMap = testDataMap;
    }

}
