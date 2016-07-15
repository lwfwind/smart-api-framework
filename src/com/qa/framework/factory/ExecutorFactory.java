package com.qa.framework.factory;

import com.qa.framework.bean.TestData;
import com.qa.framework.core.ParamValueProcessor;
import com.qa.framework.core.TestXmlData;
import org.testng.annotations.Factory;

/**
 * Created by Administrator on 2016/6/23.
 */
public class ExecutorFactory {
    /**
     * Execute object [ ].
     *
     * @param testData   the test data
     * @param url        the url
     * @param httpMethod the http method
     * @return the object [ ]
     */
    @Factory(dataProviderClass = TestXmlData.class, dataProvider = "xmlFactoryData")
    public Object[] execute(TestData testData, String url, String httpMethod) {
        Object[] tests = new Object[1];
        tests[0] = new Executor(testData, url, httpMethod);
        return tests;
    }

}
