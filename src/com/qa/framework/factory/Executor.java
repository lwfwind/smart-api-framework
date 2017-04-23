package com.qa.framework.factory;

import com.qa.framework.bean.TestData;
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
    private TestData testData;
    private String url;
    private String httpMethod;

    /**
     * Instantiates a new Executor.
     *
     * @param testData   the test data
     * @param url        the url
     * @param httpMethod the http method
     */
    public Executor(TestData testData, String url, String httpMethod) {
        this.testData = testData;
        this.url = url;
        this.httpMethod = httpMethod;
    }

    /**
     * Gets test data.
     *
     * @return the test data
     */
    public TestData getTestData() {
        return testData;
    }

    /**
     * Data object [ ] [ ].
     *
     * @return the object [ ] [ ]
     */
    @DataProvider
    public Object[][] data() {
        return new Object[][]{
                {testData, url, httpMethod},
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
            testNGMethod.setInvocationCount(executor.getTestData().getInvocationCount());
        }
    }

    /**
     * Testcase.
     *
     * @param testData   the test data
     * @param url        the url
     * @param httpMethod the http method
     */
    @Test(dataProvider = "data")
    public void testcase(TestData testData, String url, String httpMethod) {
        ParamValueProcessor.processTestData(testData);
        String content = HttpMethod.request(url, testData.getHeaders(), testData.getParams(), httpMethod, testData.isStoreCookie(), testData.isUseCookie());
        Verify.verifyResult(testData, content);
        ParamValueProcessor.processAfter(testData);
    }
}