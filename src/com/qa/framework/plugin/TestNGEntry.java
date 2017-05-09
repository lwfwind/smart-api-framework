package com.qa.framework.plugin;

import com.qa.framework.bean.TestCase;
import com.qa.framework.bean.TestSuite;
import com.qa.framework.core.DataManager;
import com.qa.framework.core.ParamValueProcessor;
import com.qa.framework.library.httpclient.HttpMethod;
import com.qa.framework.mock.IMockServer;
import com.qa.framework.verify.Verify;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static com.qa.framework.classfinder.ClassHelper.findImplementClass;

public class TestNGEntry {
    private static Logger logger = Logger.getLogger(Entry.class);
    private static IMockServer mockServer = null;


    @Test(dataProviderClass = DataManager.class, dataProvider = "data")
    public void debug(TestCase testCase, TestSuite testSuite) {
        ParamValueProcessor.processTestCase(testCase, testSuite);
        String content = HttpMethod.request(testSuite.getUrl(), testCase.getHeaders(), testCase.getParams(), testSuite.getHttpMethod(), testCase.isStoreCookie(), testCase.isUseCookie());
        Verify.verifyResult(testCase, content);
        ParamValueProcessor.processAfter(testCase);
    }

    @BeforeTest(alwaysRun = true)
    public void beforeTest() {
        Class<?> clazz = findImplementClass(IMockServer.class);
        if (clazz != null) {
            try {
                mockServer = (IMockServer) clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                logger.error(e.getMessage(), e);
            }
            mockServer.startServer();
            mockServer.settingRules();
        }
        String testSuiteName = System.getProperty("testSuiteName");
        String testCaseName = System.getProperty("testCaseName");
        DataManager.setXmlName(testSuiteName);
        if (!testCaseName.equalsIgnoreCase("null")) {
            DataManager.setXmlDataName(testCaseName);
        }
    }

    @AfterTest(alwaysRun = true)
    public void afterBefore() {
        if (mockServer != null) {
            mockServer.stopServer();
        }
    }
}
