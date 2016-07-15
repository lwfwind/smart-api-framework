package com.qa.framework.factory;

import com.qa.framework.bean.TestData;
import com.qa.framework.core.TestBase;
import com.qa.framework.testngListener.PowerEmailableReporter;
import com.qa.framework.testngListener.TestResultListener;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * The type Executor.
 */
@Listeners({TestResultListener.class, PowerEmailableReporter.class})
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
     * Testcase.
     *
     * @param testData   the test data
     * @param url        the url
     * @param httpMethod the http method
     */
    @Test(dataProvider = "data")
    public void testcase(TestData testData, String url, String httpMethod) {
        processSetupResultParam(testData);
        String content = request(url, testData.getParams(), httpMethod, testData.isStoreCookie(), testData.isUseCookie());
        verifyResult(testData, content);
    }
}