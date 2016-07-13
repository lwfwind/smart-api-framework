package com.qa.framework.generator;


import com.qa.framework.library.base.IOHelper;
import com.qa.framework.library.base.StringHelper;
import com.qa.framework.library.base.XMLHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.dom4j.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestngXmlGenerator {

    private static List<Map<String, Object>> methodListMap = new ArrayList<Map<String, Object>>();
    private static String methodPath;

    public static String factoryFileInDirectoryRecursive() {
        File directory = new File(System.getProperty("user.dir"));
        List<File> Files = (List<File>) FileUtils.listFiles(directory,
                new WildcardFileFilter("FactoryExecutor.java"),
                TrueFileFilter.INSTANCE);
        String strFiles = null;
        for (File file : Files) {
            if (file.getName().equals("FactoryExecutor.java"))
                strFiles = file.getAbsolutePath();
        }
        return strFiles;
    }

    /**
     * System.setProperty("browser","chrome");
     * System.setProperty("hubURL","http://192.168.20.196:4444/wd/hub");
     * autoGenerate("E:\\Git\\API_Automation_Framework\\src\\com\\qa\\testcase\\abc360\\MobileAPI","D:\\git\\web_ui_automation\\test-xml\\","1");
     * autoGenerate("D:\\git\\web_ui_automation\\src","D:\\git\\web_ui_automation\\test-xml\\","11");
     * autoGenerate("D:\\git\\web_ui_automation\\src","D:\\git\\web_ui_automation\\test-xml\\","11");
     * autoGenerateFactory("E:\\Git\\API_Automation_Framework\\src\\com\\qa\\Factory\\FactoryTest.java","E:\\Git\\API_Automation_Framework\\test-xml\\","1");
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        for (String arg : args) {
            System.out.println("------->" + arg);
        }
//        autoGenerate(args[0], args[1], args[2]);
        String factoryPath = factoryFileInDirectoryRecursive();
        autoGenerateFactory(factoryPath, args[0], args[1]);
    }

    public static void autoGenerate(String testCasePath, String outputPath, String threadCnt) {
        methodListMap.clear();
        List<String> files = IOHelper.listFilesInDirectoryRecursive(testCasePath, "*.java");
        String className = null;
        String packageName = null;
        for (String file : files) {
            boolean isMatchedFile = false;
            List<String> lines = IOHelper.readLinesToList(file);
            List<String> noBlankLines = new ArrayList<>();
            for (String line : lines) {
                if (!(line.trim().equals("") || line.trim().startsWith("//"))) {
                    noBlankLines.add(line);
                }
            }
            String contents = IOHelper.readFileToString(file);
            for (String line : noBlankLines) {
                if (contents != null && contents.lastIndexOf("@Test") > 0 && line.lastIndexOf("class") > 0 && line.lastIndexOf("extends") > 0) {
                    className = StringHelper.getBetweenString(line, "class", "extends").trim();
                    isMatchedFile = true;
                    break;
                }
            }
            if (isMatchedFile) {
                int index = 0;
                for (String line : noBlankLines) {
                    if (StringHelper.startsWithIgnoreCase(line.trim(), "package ")) {
                        packageName = StringHelper.getBetweenString(line, "package ", ";").trim();
                    }
                    if (StringHelper.startsWithIgnoreCase(line.trim(), "@Test") && !line.trim().contains("enabled = false")) {
                        Map<String, Object> methodMap = new HashMap<>();
                        methodMap.put("className", className);
                        methodMap.put("packageName", packageName);
                        String methodLine = noBlankLines.get(index + 1);
                        String methodName = StringHelper.getBetweenString(methodLine, "void ", "(").trim();
                        methodMap.put("methodName", methodName);
                        methodListMap.add(methodMap);
                    }
                    index++;
                }
            }
        }

        XMLHelper xml = new XMLHelper();
        xml.createDocument();
        Element root = xml.createDocumentRoot("suite");
        xml.addAttribute(root, "name", "xml_" + threadCnt);
        xml.addAttribute(root, "thread-count", threadCnt);
        xml.addAttribute(root, "parallel", "tests");
        xml.addAttribute(root, "verbose", "1");
        Element listeners = xml.addChildElement(root, "listeners");
        Element listener = xml.addChildElement(listeners, "listener");
        xml.addAttribute(listener, "class-name", "RetryListener");
        for (Map<String, Object> methodMap : methodListMap) {
            Element test = xml.addChildElement(root, "test");
            xml.addAttribute(test, "name", methodMap.get("className").toString() + "_" + methodMap.get("methodName").toString());
            xml.addAttribute(test, "timeout", "600000");
            if (System.getProperty("browser") != null) {
                Element parameter = xml.addChildElement(test, "parameter");
                xml.addAttribute(parameter, "name", "browser");
                xml.addAttribute(parameter, "value", System.getProperty("browser"));
            }
            if (System.getProperty("hubURL") != null) {
                Element parameter = xml.addChildElement(test, "parameter");
                xml.addAttribute(parameter, "name", "hubURL");
                xml.addAttribute(parameter, "value", System.getProperty("hubURL"));
            }
            Element classes = xml.addChildElement(test, "classes");
            Element cls = xml.addChildElement(classes, "class");
            xml.addAttribute(cls, "name", methodMap.get("packageName").toString() + "." + methodMap.get("className").toString());
            Element methods = xml.addChildElement(cls, "methods");
            Element include = xml.addChildElement(methods, "include");
            xml.addAttribute(include, "name", methodMap.get("methodName").toString());
        }
        IOHelper.deleteDirectory(outputPath);
        IOHelper.createNestDirectory(outputPath);
        if (outputPath.endsWith("/")) {
            xml.saveTo(outputPath + "xml_" + threadCnt + ".xml");
        } else {
            xml.saveTo(outputPath + File.separator + "xml_" + threadCnt + ".xml");
        }
    }

    public static void autoGenerateFactory(String testCasePath, String outputPath, String threadCnt) {
        methodListMap.clear();
        String file = testCasePath;
        String className = null;
        String packageName = null;
        boolean isMatchedFile = false;
        List<String> lines = IOHelper.readLinesToList(file);
        List<String> noBlankLines = new ArrayList<>();
        for (String line : lines) {
            if (!(line.trim().equals("") || line.trim().startsWith("//"))) {
                noBlankLines.add(line);
            }
        }
        String contents = IOHelper.readFileToString(file);
        for (String line : noBlankLines) {
            if (contents != null && contents.lastIndexOf("@Factory") > 0 && line.lastIndexOf("class") > 0) {
                className = StringHelper.getBetweenString(line, "class", "{").trim();
                isMatchedFile = true;
                break;
            }
        }
        if (isMatchedFile) {
            int index = 0;
            for (String line : noBlankLines) {
                if (StringHelper.startsWithIgnoreCase(line.trim(), "package ")) {
                    packageName = StringHelper.getBetweenString(line, "package ", ";").trim();
                }
                if (StringHelper.startsWithIgnoreCase(line.trim(), "@Factory")) {
                    Map<String, Object> methodMap = new HashMap<>();
                    methodMap.put("className", className);
                    methodMap.put("packageName", packageName);
                    String methodLine = noBlankLines.get(index + 1);
                    String methodName = StringHelper.getBetweenString(methodLine, "Object[] ", "(").trim();
                    methodMap.put("methodName", methodName);
                    methodListMap.add(methodMap);
                }
                index++;
            }
        }


        XMLHelper xml = new XMLHelper();
        xml.createDocument();
        Element root = xml.createDocumentRoot("suite");
        xml.addAttribute(root, "name", "xml_" + threadCnt);
        xml.addAttribute(root, "thread-count", threadCnt);
        xml.addAttribute(root, "parallel", "tests");
        xml.addAttribute(root, "verbose", "1");
        Element listeners = xml.addChildElement(root, "listeners");
        Element listener = xml.addChildElement(listeners, "listener");
        xml.addAttribute(listener, "class-name", "RetryListener");
        for (Map<String, Object> methodMap : methodListMap) {
            Element test = xml.addChildElement(root, "test");
            xml.addAttribute(test, "name", methodMap.get("className").toString() + "_" + methodMap.get("methodName").toString());
            xml.addAttribute(test, "timeout", "600000");
            if (System.getProperty("browser") != null) {
                Element parameter = xml.addChildElement(test, "parameter");
                xml.addAttribute(parameter, "name", "browser");
                xml.addAttribute(parameter, "value", System.getProperty("browser"));
            }
            if (System.getProperty("hubURL") != null) {
                Element parameter = xml.addChildElement(test, "parameter");
                xml.addAttribute(parameter, "name", "hubURL");
                xml.addAttribute(parameter, "value", System.getProperty("hubURL"));
            }
            Element classes = xml.addChildElement(test, "classes");
            Element cls = xml.addChildElement(classes, "class");
            xml.addAttribute(cls, "name", methodMap.get("packageName").toString() + "." + methodMap.get("className").toString());
            Element methods = xml.addChildElement(cls, "methods");
            Element include = xml.addChildElement(methods, "include");
            xml.addAttribute(include, "name", methodMap.get("methodName").toString());
        }
        IOHelper.deleteDirectory(outputPath);
        IOHelper.createNestDirectory(outputPath);
        if (outputPath.endsWith("/")) {
            xml.saveTo(outputPath + "xml_" + "Factory.xml");
        } else {
            xml.saveTo(outputPath + File.separator + "xml_" + "Factory.xml");
        }
    }

    public static String getMethodPath() {
        return methodPath;
    }

    public static void setMethodPath(String val) {
        methodPath = val;
    }
}
