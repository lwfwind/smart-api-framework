package com.qa.framework.core;

import com.qa.framework.bean.DataConfig;
import com.qa.framework.bean.TestData;
import com.qa.framework.config.PropConfig;
import com.qa.framework.library.base.IOHelper;
import org.apache.log4j.Logger;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/6/23.
 */
public class TestXmlData {
    private static final Logger logger = Logger.getLogger(DataManager.class);
    private static DataConvertor dataConvertor;
    private static ParamValueGenerator paramValueGenerator;
    private static DataConfig dataConfig;

    @DataProvider(name = "xmlFactoryData")
    public static Iterator<Object[]> doBusinessBookSuccessData() {
        List<Object[]> xmldata = new ArrayList<Object[]>();
        List<String> files = new ArrayList<String>();
        String testCasePath = PropConfig.getTestCasePath();
        if (testCasePath == null) {
            files = getTestCasePath();
        } else {
            testCasePath = System.getProperty("user.dir") + File.separator + testCasePath;
            files = IOHelper.listFilesInDirectoryRecursive(testCasePath, "*.xml");
        }
        int i = 0, j = 0;
        for (String filePath : files) {
            dataConvertor = new DataConvertor(filePath);
            dataConfig = dataConvertor.getDataConfig();
            paramValueGenerator = new ParamValueGenerator(dataConfig);
            List<TestData> Testdatas = dataConfig.getTestDataLists();
            for (TestData data : Testdatas) {
                Object[] d = {data, dataConfig.getUrl(), dataConfig.getHttpMethod(), paramValueGenerator};
                xmldata.add(d);
                j++;
            }
            i++;
        }
        return xmldata.iterator();
    }


    public static List<String> getTestCasePath() {
        String useDir = System.getProperty("user.dir");
        List<String> files = IOHelper.listFilesInDirectoryRecursive(useDir, "*.xml");
        List<String> testCaseXml = new ArrayList<String>();
        for (String filePath : files) {
            String contents = IOHelper.readFileToString(filePath);
            if (contents != null && contents.lastIndexOf("<DataConfig") > 0) {
                testCaseXml.add(filePath);
                break;
            }
        }

        return testCaseXml;
    }
}
