package com.qa.framework.factory;

import com.qa.framework.bean.TestCase;
import com.qa.framework.bean.TestSuite;
import com.qa.framework.core.ParamValueProcessor;
import com.qa.framework.core.TestBase;
import com.qa.framework.library.httpclient.HttpMethod;
import com.qa.framework.testnglistener.TestResultListener;
import com.qa.framework.verify.Verify;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * The type Executor.
 */
@Listeners({TestResultListener.class})
public class Executor extends TestBase {
    private TestCase testCase;
    private TestSuite testSuite;

    public Executor(TestCase testCase, TestSuite testSuite) {
        this.testCase = testCase;
        this.testSuite = testSuite;
    }

    /**
     * Gets test data.
     *
     * @return the test data
     */
    public TestCase getTestCase() {
        return testCase;
    }

    /**
     * Data object [ ] [ ].
     *
     * @return the object [ ] [ ]
     */
    @DataProvider
    public Object[][] data() {
        return new Object[][]{
                {testCase, testSuite},
        };
    }

    /**
     * Before suite.
     *
     * @param context the context
     */
    @BeforeSuite
    public void beforeSuite(ITestContext context) {
        for (ITestNGMethod testNGMethod : context.getAllTestMethods()) {
            Executor executor = (Executor) testNGMethod.getInstance();
            testNGMethod.setInvocationCount(executor.getTestCase().getInvocationCount());
        }
    }

    /**
     * Testcase.
     *
     * @param testCase   the test data
     */
    @Test(dataProvider = "data")
    public void testcase(TestCase testCase, TestSuite testSuite) {
        ParamValueProcessor.processTestCase(testCase,testSuite);
        String content = HttpMethod.request(testSuite.getUrl(), testCase.getHeaders(), testCase.getParams(), testSuite.getHttpMethod(), testCase.isStoreCookie(), testCase.isUseCookie());
        Verify.verifyResult(testCase, content);
        ParamValueProcessor.processAfter(testCase);
    }
}