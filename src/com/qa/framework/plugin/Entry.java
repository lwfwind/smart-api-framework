package com.qa.framework.plugin;


import com.library.common.IOHelper;
import com.qa.framework.bean.TestCase;
import com.qa.framework.bean.TestSuite;
import com.qa.framework.core.ParamValueProcessor;
import com.qa.framework.core.TestSuiteConvertor;
import com.qa.framework.library.httpclient.HttpMethod;
import com.qa.framework.mock.IMockServer;
import com.qa.framework.verify.Verify;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

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
            logger.info(String.format("start execute testSuiteName: %s testCaseName: %s", xmlName, xmlDataName));
            String xmlPath = IOHelper.listFilesInDirectoryRecursive(System.getProperty("user.dir"), xmlName + ".xml").get(0);
            TestSuiteConvertor testSuiteConvertor = new TestSuiteConvertor(xmlPath);
            TestSuite testSuite = testSuiteConvertor.getTestSuite();
            if (xmlDataName.equals("null")) {
                for (TestCase testCase : testSuite.getTestCaseList()) {
                    ParamValueProcessor.processTestCase(testCase, testSuite);
                    String content = HttpMethod.request(testSuite.getUrl(), testCase.getHeaders(), testCase.getParams(), testSuite.getHttpMethod(), testCase.isStoreCookie(), testCase.isUseCookie());
                    Verify.verifyResult(testCase, content);
                    ParamValueProcessor.processAfter(testCase);
                }
            } else {
                for (TestCase testCase : testSuite.getTestCaseList()) {
                    if (testCase.getName().equals(xmlDataName)) {
                        ParamValueProcessor.processTestCase(testCase, testSuite);
                        String content = HttpMethod.request(testSuite.getUrl(), testCase.getHeaders(), testCase.getParams(), testSuite.getHttpMethod(), testCase.isStoreCookie(), testCase.isUseCookie());
                        Verify.verifyResult(testCase, content);
                        ParamValueProcessor.processAfter(testCase);
                        break;
                    }
                }
            }
        } catch (IllegalAccessException | InstantiationException | ParseException e) {
            logger.error(e.getMessage(), e);
        } finally {
            after();
        }
        logger.info(String.format("finish execute testSuiteName: %s testCaseName: %s", xmlName, xmlDataName));
    }

}
