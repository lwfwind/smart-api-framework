package com.qa.framework.exception;

public class TestCaseParamException extends RuntimeException {

    public TestCaseParamException(String testCaseName, String param, String paramValue) {
        super("paramValue can't be replaced for TestCase:" + testCaseName + " param:" + param + " paramValue:" + paramValue);
    }
}
