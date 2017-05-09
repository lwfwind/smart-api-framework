package com.qa.framework.testnglistener;

import org.testng.ITestContext;
import org.testng.ITestResult;

/**
 * Created by Administrator on 2016/8/30.
 */
public interface ICustomTestListener {
    void onStart(ITestContext testContext);

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

    /**
     * On finish.
     *
     * @param testContext the test context
     */
    void onFinish(ITestContext testContext);
}
