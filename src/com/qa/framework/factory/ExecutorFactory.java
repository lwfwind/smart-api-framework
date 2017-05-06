package com.qa.framework.factory;

import com.qa.framework.bean.TestCase;
import com.qa.framework.bean.TestSuite;
import com.qa.framework.core.TestXmlData;
import com.qa.framework.mock.IMockServer;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Factory;

import static com.qa.framework.classfinder.ClassHelper.findImplementClass;

/**
 * The type Executor factory.
 */
public class ExecutorFactory {
    private IMockServer mockServer = null;

    /**
     * Before class.
     *
     * @throws IllegalAccessException the illegal access exception
     * @throws InstantiationException the instantiation exception
     */
    @BeforeSuite(alwaysRun = true)
    public void beforeClass() throws IllegalAccessException, InstantiationException {
        Class<?> clazz = findImplementClass(IMockServer.class);
        if (clazz != null) {
            mockServer = (IMockServer) clazz.newInstance();
            mockServer.startServer();
            mockServer.settingRules();
        }
    }

    /**
     * After class.
     */
    @AfterSuite(alwaysRun = true)
    public void afterClass() {
        if (mockServer != null) {
            mockServer.stopServer();
        }
    }


    /**
     * Execute object [ ].
     *
     * @param testCase   the test data
     * @return the object [ ]
     */
    @Factory(dataProviderClass = TestXmlData.class, dataProvider = "xmlFactoryData")
    public Object[] execute(TestCase testCase,TestSuite testSuite) {
        Object[] tests = new Object[1];
        tests[0] = new Executor(testCase, testSuite);
        return tests;
    }

}
