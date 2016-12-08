package com.qa.framework.core;

import com.library.common.IOHelper;
import com.qa.framework.config.ProjectEnvironment;
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
    private static String xmlDataName = null;

    /**
     * Gets data provider.
     *
     * @param method the method
     * @return the data provider
     */
    @DataProvider(name = "data")
    @SuppressWarnings("unchecked")
    public static Iterator<Object[]> getDataProvider(Method method) {
        if (method.getName().equals("debug")) {
            return getDataProvider();
        } else {
            return getDataProvider(method.getDeclaringClass(), method);
        }
    }

    /**
     * Gets data provider.
     *
     * @param cls    the cls
     * @param method the method
     * @return the data provider
     */
    public static Iterator getDataProvider(Class cls, Method method) {
        logger.info("class package name is: " + cls.getPackage().getName() + ", Method name is: " + method.getName());
        String xmlPath = ProjectEnvironment.srcPath() + cls.getPackage().getName().replace(".", File.separator);
        String filePath = xmlPath + File.separator + method.getName() + ".xml";
        logger.info("filePath is: " + filePath);
        return new XmlDataProvider(filePath);
    }

    /**
     * Gets data provider.
     *
     * @return the data provider
     */
    public static Iterator getDataProvider() {
        List<String> files = IOHelper.listFilesInDirectoryRecursive(System.getProperty("user.dir"), xmlName + ".xml");
        String filePath = files.get(0);
        if (xmlDataName != null) {
            return new XmlDataProvider(filePath, xmlDataName);
        } else {
            return new XmlDataProvider(filePath);
        }

    }

    /**
     * Gets xml name.
     *
     * @return the xml name
     */
    public static String getXmlName() {
        return xmlName;
    }

    /**
     * Sets xml name.
     *
     * @param val the xml name
     */
    public static void setXmlName(String val) {
        xmlName = val;
    }

    /**
     * Gets xml data nmae.
     *
     * @return the xml data nmae
     */
    public static String getXmlDataName() {
        return xmlDataName;
    }

    /**
     * Sets xml data nmae.
     *
     * @param val the xml data nmae
     */
    public static void setXmlDataName(String val) {
        xmlDataName = val;
    }
}
