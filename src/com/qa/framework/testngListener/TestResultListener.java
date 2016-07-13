package com.qa.framework.testngListener;

import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

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
        logger.error(tr.getName() + " Failure");
        printStackTrace(tr);
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        super.onTestSkipped(tr);
        logger.info(tr.getName() + " Skipped");
        printStackTrace(tr);
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        logger.info(tr.getName() + " Success");
    }

    @Override
    public void onTestStart(ITestResult tr) {
        super.onTestStart(tr);
        logger.info(tr.getName() + " Start");
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
