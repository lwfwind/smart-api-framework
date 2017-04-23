package com.qa.framework.core;

import com.library.common.IOHelper;
import com.qa.framework.bean.DataConfig;
import com.qa.framework.bean.TestData;
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
    private static final Logger logger = Logger.getLogger(TestXmlData.class);

    /**
     * Xml factory data iterator.
     *
     * @return the iterator
     * @throws InterruptedException the interrupted exception
     */
    @DataProvider(name = "xmlFactoryData")
    public static Iterator<Object[]> xmlFactoryData() throws InterruptedException {
        return getXmlTestData();
    }

    private static Iterator<Object[]> getXmlTestData() {
        List<Object[]> xmldata = new ArrayList<Object[]>();
        List<String> files = getTestCaseFiles();
        for (String filePath : files) {
            DataConvertor dataConvertor = new DataConvertor(filePath);
            DataConfig dataConfig = dataConvertor.getDataConfig();
            List<TestData> testDataList = dataConfig.getTestDataList();
            for (TestData data : testDataList) {
                Object[] d = {data, dataConfig.getUrl(), dataConfig.getHttpMethod()};
                xmldata.add(d);
            }
        }
        return xmldata.iterator();
    }


    /**
     * Gets test case files.
     *
     * @return the test case files
     */
    public static List<String> getTestCaseFiles() {
        String useDir = System.getProperty("user.dir");
        List<String> files = IOHelper.listFilesInDirectoryRecursive(useDir + File.separator + "src", "*.xml");
        List<String> testCaseXml = new ArrayList<String>();
        for (String filePath : files) {
            String contents = IOHelper.readFileToString(filePath);
            if (contents != null && contents.lastIndexOf("<DataConfig") > 0) {
                testCaseXml.add(filePath);
            }
        }
        return testCaseXml;
    }
}
