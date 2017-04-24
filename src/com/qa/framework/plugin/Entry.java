package com.qa.framework.plugin;


import com.library.common.IOHelper;
import com.qa.framework.bean.DataConfig;
import com.qa.framework.bean.TestData;
import com.qa.framework.core.DataConvertor;
import com.qa.framework.core.ParamValueProcessor;
import com.qa.framework.library.httpclient.HttpMethod;
import com.qa.framework.mock.IMockServer;
import com.qa.framework.testnglistener.TestResultListener;
import com.qa.framework.verify.Verify;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.qa.framework.classfinder.ClassHelper.findImplementClass;


public class Entry {
    private static Logger logger = Logger.getLogger(Entry.class);
    private static IMockServer mockServer = null;

    public static void before() throws IllegalAccessException, InstantiationException {
        Class<?> clazz = findImplementClass(IMockServer.class);
        if (clazz != null) {
            mockServer = (IMockServer) clazz.newInstance();
            mockServer.startServer();
            mockServer.settingRules();
        }
    }

    public static void after() {
        if (mockServer != null) {
            mockServer.stopServer();
        }
    }

    public static void main(String[] args) {
        String xmlName = "";
        String xmlDataName = "";
        try {
            before();
            Options options = new Options();
            options.addOption("testSuiteName", true, "testSuite name");
            options.addOption("testCaseName", true, "testCase name");
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = null;
            cmd = parser.parse(options, args);
            if (cmd.hasOption("testSuiteName")) {
                xmlName = cmd.getOptionValue("testSuiteName");
            }
            if (cmd.hasOption("testCaseName")) {
                xmlDataName = cmd.getOptionValue("testCaseName");
            }
            logger.info(String.format("start execute testSuiteName: %s testCaseName: %s",xmlName,xmlDataName));
            String xmlPath = IOHelper.listFilesInDirectoryRecursive(System.getProperty("user.dir"), xmlName + ".xml").get(0);
            DataConvertor dataConvertor = new DataConvertor(xmlPath);
            DataConfig dataConfig = dataConvertor.getDataConfig();
            if(xmlDataName.equals("null")) {
                for (TestData testData : dataConfig.getTestDataList()) {
                    ParamValueProcessor.processTestData(testData);
                    String content = HttpMethod.request(dataConfig.getUrl(), testData.getHeaders(), testData.getParams(), dataConfig.getHttpMethod(), testData.isStoreCookie(), testData.isUseCookie());
                    Verify.verifyResult(testData, content);
                    ParamValueProcessor.processAfter(testData);
                }
            }
            else {
                for (TestData testData : dataConfig.getTestDataList()) {
                    if (testData.getName().equals(xmlDataName)) {
                        ParamValueProcessor.processTestData(testData);
                        String content = HttpMethod.request(dataConfig.getUrl(), testData.getHeaders(), testData.getParams(), dataConfig.getHttpMethod(), testData.isStoreCookie(), testData.isUseCookie());
                        Verify.verifyResult(testData, content);
                        ParamValueProcessor.processAfter(testData);
                        break;
                    }
                }
            }
        } catch (IllegalAccessException | InstantiationException | ParseException e) {
            logger.error(e.getMessage(),e);
        } finally {
            after();
        }
        logger.info(String.format("end execute testSuiteName: %s testCaseName: %s",xmlName,xmlDataName));
    }

}
