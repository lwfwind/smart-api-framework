package com.qa.framework.testngListener;

import com.qa.framework.factory.Executor;
import com.qa.framework.library.base.IOHelper;
import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.internal.BaseTestMethod;

import java.lang.reflect.Field;

/**
 * Test result Listener.
 */
public class TestResultListener extends TestListenerAdapter {

    private static Logger logger = Logger.getLogger(TestResultListener.class);

    @Override
    public void onStart(ITestContext testContext) {
        super.onStart(testContext);
        logger.info("testContext Start");
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        super.onTestFailure(tr);
        Executor executor = (Executor) tr.getInstance();
        executor.processAfter(executor.getTestData());
        String testName = IOHelper.getBaseName(executor.getTestData().getCurrentFileName()) + "_" + executor.getTestData().getName();
        logger.error(testName + " Failure");
        printStackTrace(tr);
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        super.onTestSkipped(tr);
        Executor executor = (Executor) tr.getInstance();
        executor.processAfter(executor.getTestData());
        String testName = IOHelper.getBaseName(executor.getTestData().getCurrentFileName()) + "_" + executor.getTestData().getName();
        logger.info(testName + " Skipped");
        printStackTrace(tr);
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        Executor executor = (Executor) tr.getInstance();
        executor.processAfter(executor.getTestData());
        String testName = IOHelper.getBaseName(executor.getTestData().getCurrentFileName()) + "_" + executor.getTestData().getName();
        logger.info(testName + " Success");
    }

    @Override
    public void onTestStart(ITestResult tr) {
        super.onTestStart(tr);
        Executor executor = (Executor) tr.getInstance();
        String testName = IOHelper.getBaseName(executor.getTestData().getCurrentFileName()) + "_" + executor.getTestData().getName();
        try {
            BaseTestMethod bm = (BaseTestMethod) tr.getMethod();
            Field f = bm.getClass().getSuperclass().getDeclaredField("m_methodName");
            f.setAccessible(true);
            f.set(bm, testName);
        } catch (Exception ex) {
            System.out.println("ex" + ex.getMessage());
        }
        logger.info(testName + " Start");
    }

    @Override
    public void onFinish(ITestContext testContext) {
        super.onFinish(testContext);
        logger.info("testContext Finish");

        for (ITestResult passedTest : testContext.getPassedTests().getAllResults()) {
            logger.info("PassedTests = " + passedTest.getName());
        }

        for (ITestResult skipTest : testContext.getSkippedTests().getAllResults()) {
            logger.info("SkipTest = " + skipTest.getName());
        }

        for (ITestResult failedTest : testContext.getFailedTests().getAllResults()) {
            logger.info("FailedTest = " + failedTest.getName());
        }
    }

    private void printStackTrace(ITestResult tr) {
        StringBuilder stackTrace = new StringBuilder();
        Throwable throwable = tr.getThrowable();
        if (throwable != null) {
            stackTrace.append(throwable.toString());
            stackTrace.append("\n");
            StackTraceElement[] se = throwable.getStackTrace();
            for (StackTraceElement e : se) {
                stackTrace.append("     at ");
                stackTrace.append(e.toString());
                stackTrace.append("\n");
            }
            logger.error(stackTrace);
        }
    }

}
