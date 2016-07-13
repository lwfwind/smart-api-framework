package com.qa.framework.config;


import com.qa.framework.library.base.StringHelper;
import com.qa.framework.library.reflect.ReflectHelper;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * Created by apple on 15/11/18.
 */
public class PropConfig {
    private static Properties props = new Properties();
    //代理配置
    private static boolean useProxy;
    private static String localhost;
    private static String localport;
    private static String timeout;
    //测试服务器配置
    private static String webPath;
    private static String dbPoolName;
    //失败重试次数
    private static int retryCount;
    //自定义report
    private static String sourceCodeEncoding;
    private static String sourceCodeDir;
    private static String testCasePath;

    static {
        File file = new File(System.getProperty("user.dir") + File.separator + "config.properties");
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            props.load(fileReader);
            Field[] fields = PropConfig.class.getDeclaredFields();
            for (Field field : fields) {
                if (!field.getName().equals("props")) {
                    ReflectHelper.setMethod(PropConfig.class, field.getName(), props.getProperty(field.getName()), String.class);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static String getWebPath() {
        return webPath;
    }

    public static void setWebPath(String val) {
        webPath = val;
    }

    /**
     * Gets db pool name.
     *
     * @return the db pool name
     */
    public static String getDbPoolName() {
        return dbPoolName;
    }

    /**
     * Sets db pool name.
     *
     * @param dbPoolName the db pool name
     */
    public static void setDbPoolName(String dbPoolName) {
        PropConfig.dbPoolName = dbPoolName;
    }

    public static boolean isUseProxy() {
        return useProxy;
    }

    public static void setUseProxy(String val) {
        useProxy = StringHelper.changeString2boolean(val);
    }

    public static String getLocalhost() {
        return localhost;
    }

    public static void setLocalhost(String val) {
        localhost = val;
    }

    public static String getLocalport() {
        return localport;
    }

    public static void setLocalport(String val) {
        localport = val;
    }

    public static String getTimeout() {
        return timeout;
    }

    public static void setTimeout(String val) {
        timeout = val;
    }

    public static int getRetryCount() {
        return retryCount;
    }

    public static void setRetryCount(String val) {
        retryCount = Integer.parseInt(val);
    }

    public static String getSourceCodeEncoding() {
        return sourceCodeEncoding;
    }

    public static void setSourceCodeEncoding(String val) {
        sourceCodeEncoding = val;
    }

    public static String getSourceCodeDir() {
        return sourceCodeDir;
    }

    public static void setSourceCodeDir(String val) {
        sourceCodeDir = val;
    }


    public static String getTestCasePath() {
        return testCasePath;
    }

    public static void setTestCasePath(String testCasePath) {
        PropConfig.testCasePath = testCasePath;
    }
}
