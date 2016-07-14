package com.qa.framework.factory;

import com.qa.framework.bean.TestData;
import com.qa.framework.core.ParamValueGenerator;
import com.qa.framework.core.TestXmlData;
import org.testng.annotations.Factory;

/**
 * Created by Administrator on 2016/6/23.
 */
public class FactoryExecutor {
    @Factory(dataProviderClass = TestXmlData.class, dataProvider = "xmlFactoryData")
    public Object[] execute(TestData testData, String url, String httpMethod, ParamValueGenerator paramValueGenerator) {
        Object[] tests = new Object[1];
        tests[0] = new Executor(testData, url, httpMethod, paramValueGenerator);
        return tests;
    }

}
