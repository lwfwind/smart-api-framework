package com.qa.framework.core;

import com.qa.framework.config.ProjectEnvironment;
import com.qa.framework.library.base.IOHelper;
import org.apache.log4j.Logger;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * 封装对测试数据的操作
 * Created by apple on 15/11/18.
 */
public class DataManager {

    private static final Logger logger = Logger.getLogger(DataManager.class);
    private static String xmlName = null;
    private static String xmlDataNmae = null;

    @DataProvider(name = "data")
    @SuppressWarnings("unchecked")
    public static Iterator<Object[]> getDataProvider(Method method) {
        if (method.getName().equals("Testcase")) {
            return getDataProvider();
        } else {
            return getDataProvider(method.getDeclaringClass(), method);
        }
    }

    public static Iterator getDataProvider(Class cls, Method method) {
        logger.info("class package name is: " + cls.getPackage().getName() + ", Method name is: " + method.getName());
        String xmlPath = ProjectEnvironment.srcPath() + cls.getPackage().getName().replace(".", File.separator);
        String filePath = xmlPath + File.separator + method.getName() + ".xml";
        logger.info("filePath is: " + filePath);
        return new XmlDataProvider(filePath);
    }

    public static Iterator getDataProvider() {
        List<String> files = IOHelper.listFilesInDirectoryRecursive(System.getProperty("user.dir"), xmlName + ".xml");
        String filePath = files.get(0);
        if (xmlDataNmae != null) {
            return new XmlDataProvider(filePath, xmlDataNmae);
        } else {
            return new XmlDataProvider(filePath);
        }

    }

    public static String getXmlName() {
        return xmlName;
    }

    public static void setXmlName(String xmlName) {
        DataManager.xmlName = xmlName;
    }

    public static String getXmlDataNmae() {
        return xmlDataNmae;
    }

    public static void setXmlDataNmae(String xmlDataNmae) {
        DataManager.xmlDataNmae = xmlDataNmae;
    }
}
