package com.qa.framework.core;

import com.qa.framework.bean.DataConfig;
import com.qa.framework.bean.TestData;
import com.qa.framework.library.base.IOHelper;
import org.apache.log4j.Logger;
import org.testng.annotations.DataProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/6/23.
 */
public class TestXmlData {
    private static final Logger logger = Logger.getLogger(TestXmlData.class);

    @DataProvider(name = "xmlFactoryData")
    public static Iterator<Object[]> xmlFactoryData() {
        List<Object[]> xmldata = new ArrayList<Object[]>();
        List<String> files = getTestCaseFiles();
        for (String filePath : files) {
            DataConvertor dataConvertor = new DataConvertor(filePath);
            DataConfig dataConfig = dataConvertor.getDataConfig();
            ParamValueProcessor paramValueProcessor = new ParamValueProcessor(dataConfig);
            paramValueProcessor.process();
            List<TestData> testDataList = dataConfig.getTestDataList();
            for (TestData data : testDataList) {
                Object[] d = {data, dataConfig.getUrl(), dataConfig.getHttpMethod()};
                xmldata.add(d);
            }
        }
        return xmldata.iterator();
    }


    public static List<String> getTestCaseFiles() {
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
