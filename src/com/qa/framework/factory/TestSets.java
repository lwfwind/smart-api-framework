package com.qa.framework.factory;

import com.qa.framework.bean.TestData;
import com.qa.framework.core.ParamValueGenerator;
import com.qa.framework.core.TestBase;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestSets extends TestBase {
    public TestData testData;
    private String filePath;
    private String url;
    private String httpMethod;
    private ParamValueGenerator paramValueGenerator;

    public TestSets(TestData TESTDATA, String URL, String HTTPMethod, ParamValueGenerator PARAMValueGenerator) {
        testData = TESTDATA;
        url = URL;
        httpMethod = HTTPMethod;
        paramValueGenerator = PARAMValueGenerator;
    }

    @DataProvider
    public Object[][] data() {
        return new Object[][]{
                {testData, url, httpMethod},

        };
    }

    @Test(dataProvider = "data")
    public void testcase(TestData testData, String url, String httpMethod) {
        processParam(testData);
        String content = request(url,testData.getParams(), httpMethod, testData.isStoreCookie(), testData.isUseCookie());
        verifyResult(testData, content, this.paramValueGenerator);
    }
}