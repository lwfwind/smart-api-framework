package com.qa.framework.testnglistener;

import com.library.common.IOHelper;
import com.qa.framework.factory.Executor;
import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import java.lang.reflect.Field;
import java.util.*;

import static com.qa.framework.classfinder.ClassHelper.findImplementClass;

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
        Class<?> clazz = findImplementClass(ICustomTestListener.class);
        if (clazz != null) {
            ICustomTestListener testListenerImp = null;
            try {
                testListenerImp = (ICustomTestListener) clazz.newInstance();
                testListenerImp.onTestFailure(tr);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        Object obj = tr.getInstance();
        if (obj instanceof Executor) {
            Executor executor = (Executor) obj;
            executor.processAfter(executor.getTestData());
            String testName = IOHelper.getBaseName(executor.getTestData().getCurrentFileName()) + "_" + executor.getTestData().getName();
            logger.error(testName + " Failure");
        }
        printStackTrace(tr);
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        super.onTestSkipped(tr);
        Class<?> clazz = findImplementClass(ICustomTestListener.class);
        if (clazz != null) {
            ICustomTestListener testListenerImp = null;
            try {
                testListenerImp = (ICustomTestListener) clazz.newInstance();
                testListenerImp.onTestSkipped(tr);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        Object obj = tr.getInstance();
        if (obj instanceof Executor) {
            Executor executor = (Executor) obj;
            executor.processAfter(executor.getTestData());
            String testName = IOHelper.getBaseName(executor.getTestData().getCurrentFileName()) + "_" + executor.getTestData().getName();
            logger.info(testName + " Skipped");
        }
        printStackTrace(tr);
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        Class<?> clazz = findImplementClass(ICustomTestListener.class);
        if (clazz != null) {
            ICustomTestListener testListenerImp = null;
            try {
                testListenerImp = (ICustomTestListener) clazz.newInstance();
                testListenerImp.onTestFailure(tr);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        Object obj = tr.getInstance();
        if (obj instanceof Executor) {
            Executor executor = (Executor) obj;
            executor.processAfter(executor.getTestData());
            String testName = IOHelper.getBaseName(executor.getTestData().getCurrentFileName()) + "_" + executor.getTestData().getName();
            logger.info(testName + " Success");
        }
    }

    @Override
    public void onTestStart(ITestResult tr) {
        super.onTestStart(tr);
        Class<?> clazz = findImplementClass(ICustomTestListener.class);
        if (clazz != null) {
            ICustomTestListener testListenerImp = null;
            try {
                testListenerImp = (ICustomTestListener) clazz.newInstance();
                testListenerImp.onTestStart(tr);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        Object obj = tr.getInstance();
        if (obj instanceof Executor) {
            Executor executor = (Executor) obj;
            String testName = IOHelper.getBaseName(executor.getTestData().getCurrentFileName()) + "_" + executor.getTestData().getName();
            try {
                BaseTestMethod bm = (BaseTestMethod) tr.getMethod();
                Field methodNameField = bm.getClass().getSuperclass().getDeclaredField("m_methodName");
                methodNameField.setAccessible(true);
                methodNameField.set(bm, testName);

                TestResult trImp = (TestResult) tr;
                Field nameField = trImp.getClass().getDeclaredField("m_name");
                nameField.setAccessible(true);
                nameField.set(trImp, testName);
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }
            logger.info(testName + " Start");
        }
    }

    @Override
    public void onFinish(ITestContext testContext) {
        super.onFinish(testContext);
        logger.info("testContext Finish");

        // List of test results which we will delete later
        ArrayList<ITestResult> testsToBeRemoved = new ArrayList<ITestResult>();
        // collect all id's from passed test
        Set<Integer> passedTestIds = new HashSet<Integer>();
        for (ITestResult passedTest : testContext.getPassedTests().getAllResults()) {
            logger.info("PassedTests = " + passedTest.getName());
            passedTestIds.add(getId(passedTest));
        }

        // Eliminate the repeat methods
        Set<Integer> skipTestIds = new HashSet<Integer>();
        for (ITestResult skipTest : testContext.getSkippedTests().getAllResults()) {
            logger.info("skipTest = " + skipTest.getName());
            // id = class + method + dataprovider
            int skipTestId = getId(skipTest);
            if (skipTestIds.contains(skipTestId) || passedTestIds.contains(skipTestId)) {
                testsToBeRemoved.add(skipTest);
            } else {
                skipTestIds.add(skipTestId);
            }
        }

        // Eliminate the repeat failed methods
        Set<Integer> failedTestIds = new HashSet<Integer>();
        for (ITestResult failedTest : testContext.getFailedTests().getAllResults()) {
            logger.info("failedTest = " + failedTest.getName());
            // id = class + method + dataprovider
            int failedTestId = getId(failedTest);

            // if we saw this test as a failed test before we mark as to be
            // deleted
            // or delete this failed test if there is at least one passed
            // version
            if (failedTestIds.contains(failedTestId) || passedTestIds.contains(failedTestId) ||
                    skipTestIds.contains(failedTestId)) {
                testsToBeRemoved.add(failedTest);
            } else {
                failedTestIds.add(failedTestId);
            }
        }

        // finally delete all tests that are marked
        for (Iterator<ITestResult> iterator = testContext.getFailedTests().getAllResults().iterator(); iterator.hasNext(); ) {
            ITestResult testResult = iterator.next();
            if (testsToBeRemoved.contains(testResult)) {
                logger.info("Remove repeat Fail Test: " + testResult.getName());
                iterator.remove();
            }
        }
        Class<?> clazz = findImplementClass(ICustomTestListener.class);
        if (clazz != null) {
            ICustomTestListener testListenerImp = null;
            try {
                testListenerImp = (ICustomTestListener) clazz.newInstance();
                testListenerImp.onFinish(testContext);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private int getId(ITestResult result) {
        int id = result.getTestClass().getName().hashCode();
        id = id + result.getMethod().getMethodName().hashCode();
        id = id + (result.getParameters() != null ? Arrays.hashCode(result.getParameters()) : 0);
        return id;
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
