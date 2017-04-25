package com.qa.framework.factory;

import com.qa.framework.bean.TestCase;
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
    private String url;
    private String httpMethod;

    /**
     * Instantiates a new Executor.
     *
     * @param testCase   the test data
     * @param url        the url
     * @param httpMethod the http method
     */
    public Executor(TestCase testCase, String url, String httpMethod) {
        this.testCase = testCase;
        this.url = url;
        this.httpMethod = httpMethod;
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
                {testCase, url, httpMethod},
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
     * @param url        the url
     * @param httpMethod the http method
     */
    @Test(dataProvider = "data")
    public void testcase(TestCase testCase, String url, String httpMethod) {
        ParamValueProcessor.processTestData(testCase);
        String content = HttpMethod.request(url, testCase.getHeaders(), testCase.getParams(), httpMethod, testCase.isStoreCookie(), testCase.isUseCookie());
        Verify.verifyResult(testCase, content);
        ParamValueProcessor.processAfter(testCase);
    }
}