package com.qa.framework.core;

import com.library.common.IOHelper;
import com.qa.framework.bean.TestCase;
import com.qa.framework.bean.TestSuite;
import org.apache.log4j.Logger;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        new GlobalConvertor();
        List<String> files = getTestCaseFiles();
        for (String filePath : files) {
            TestSuiteConvertor testSuiteConvertor = new TestSuiteConvertor(filePath);
            TestSuite testSuite = testSuiteConvertor.getTestSuite();
            List<TestCase> testCaseList = testSuite.getTestCaseList();
            for (TestCase data : testCaseList) {
                Object[] d = {data, testSuite};
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
        String xmlPath = System.getProperty("xmlPath");
        if (xmlPath == null) {
            xmlPath = System.getProperty("user.dir") + File.separator + "src";
        }
        List<String> files = IOHelper.listFilesInDirectoryRecursive(xmlPath, "*.xml");
        List<String> testCaseXml = new ArrayList<String>();
        for (String filePath : files) {
            String contents = IOHelper.readFileToString(filePath);
            if (contents != null && contents.lastIndexOf("<TestSuite") > 0) {
                testCaseXml.add(filePath);
            }
        }
        return testCaseXml;
    }
}
