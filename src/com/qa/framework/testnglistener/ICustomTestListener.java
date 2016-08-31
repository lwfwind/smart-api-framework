package com.qa.framework.testnglistener;

import org.testng.ITestResult;

/**
 * Created by Administrator on 2016/8/30.
 */
public interface ICustomTestListener{
    /**
     * On test failure.
     *
     * @param tr the tr
     */
    void onTestFailure(ITestResult tr);

    /**
     * On test skipped.
     *
     * @param tr the tr
     */
    void onTestSkipped(ITestResult tr);

    /**
     * On test start.
     *
     * @param tr the tr
     */
    void onTestStart(ITestResult tr);

    /**
     * On test success.
     *
     * @param tr the tr
     */
    void onTestSuccess(ITestResult tr);
}
