package com.qa.framework.core;

import com.library.common.IOHelper;
import com.qa.framework.bean.DataConfig;
import com.qa.framework.bean.TestData;
import com.qa.framework.config.PropConfig;
import com.qa.framework.library.multithread.multiThreadHandle;
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

    /**
     * Xml factory data iterator.
     *
     * @return the iterator
     */
    @DataProvider(name = "xmlFactoryData")
    public static Iterator<Object[]> xmlFactoryData() throws InterruptedException {
        List<Object[]> xmldata = new ArrayList<Object[]>();
        List<String> files = getTestCaseFiles();
        if (PropConfig.getIsMultithread()){
            final List<Object[]> xmldataMulti = new ArrayList<Object[]>();
            for (final String filePath : files) {
                Runnable e= new Runnable(){
               String fileName;
                @Override
                public void run() {
                    DataConvertor dataConvertor = new DataConvertor(filePath);
                    DataConfig dataConfig = dataConvertor.getDataConfig();
                    ParamValueProcessor paramValueProcessor = new ParamValueProcessor(dataConfig);
                    paramValueProcessor.process();
                    for (TestData data : dataConfig.getTestDataList()) {
                        Object[] d = {data, dataConfig.getUrl(), dataConfig.getHttpMethod()};
                        xmldataMulti.add(d);
                    }
                }
            };
            multiThreadHandle.buildThreadPool(e);
            }
            if (multiThreadHandle.isEnd()) {
                xmldata=xmldataMulti;
                logger.info("processed xmldata is end");
            }
        }else {
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
        List<String> files = IOHelper.listFilesInDirectoryRecursive(useDir, "*.xml");
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
