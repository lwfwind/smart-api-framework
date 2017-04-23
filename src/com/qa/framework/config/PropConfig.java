package com.qa.framework.config;


import com.library.common.ReflectHelper;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

public class PropConfig {
    //代理配置
    @Value("useProxy")
    private static boolean useProxy = false;
    @Value("localhost")
    private static String localhost = "127.0.0.1";
    @Value("localport")
    private static String localport = "8888";
    @Value("timeout")
    private static String timeout = "30000";
    //测试服务器配置
    @Value("webPath")
    private static String webPath;
    @Value("dbPoolName")
    private static String dbPoolName;
    //失败重试次数
    @Value("retryCount")
    private static int retryCount = 1;
    //自定义report
    @Value("sourceCodeEncoding")
    private static String sourceCodeEncoding = "UTF-8";
    @Value("sourceCodeDir")
    private static String sourceCodeDir = "src";
    //base package name
    @Value("basePackage")
    private static String basePackage;
    @Value("sendMsg")
    private static boolean sendMsg = false;
    //SMS配置
    @Value("SN")
    private static String SN;
    @Value("SNPWD")
    private static String SNPWD;

    static {
        Properties props = getProperties();
        Field[] fields = PropConfig.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Value.class)) {
                Value annotation = field.getAnnotation(Value.class);
                String propValue = props.getProperty(annotation.value());
                if (propValue != null) {
                    ReflectHelper.setMethod(PropConfig.class, field.getName(), propValue, field.getType());
                }
            }
        }
    }

    private static Properties getProperties() {
        Properties props = new Properties();
        File file = new File(System.getProperty("user.dir") + File.separator + "config.properties");
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            props.load(fileReader);
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
        return props;
    }

    /**
     * Gets base package.
     *
     * @return the base package
     */
    public static String getBasePackage() {
        return basePackage;
    }

    /**
     * Sets base package.
     *
     * @param val the val
     */
    public static void setBasePackage(String val) {
        basePackage = val;
    }

    /**
     * Gets web path.
     *
     * @return the web path
     */
    public static String getWebPath() {
        return webPath;
    }

    /**
     * Sets web path.
     *
     * @param val the val
     */
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

    /**
     * Is use proxy boolean.
     *
     * @return the boolean
     */
    public static boolean isUseProxy() {
        return useProxy;
    }

    /**
     * Sets use proxy.
     *
     * @param val the val
     */
    public static void setUseProxy(boolean val) {
        useProxy = val;
    }

    /**
     * Gets localhost.
     *
     * @return the localhost
     */
    public static String getLocalhost() {
        return localhost;
    }

    /**
     * Sets localhost.
     *
     * @param val the val
     */
    public static void setLocalhost(String val) {
        localhost = val;
    }

    /**
     * Gets localport.
     *
     * @return the localport
     */
    public static String getLocalport() {
        return localport;
    }

    /**
     * Sets localport.
     *
     * @param val the val
     */
    public static void setLocalport(String val) {
        localport = val;
    }

    /**
     * Gets timeout.
     *
     * @return the timeout
     */
    public static String getTimeout() {
        return timeout;
    }

    /**
     * Sets timeout.
     *
     * @param val the val
     */
    public static void setTimeout(String val) {
        timeout = val;
    }

    /**
     * Gets retry count.
     *
     * @return the retry count
     */
    public static int getRetryCount() {
        return retryCount;
    }

    /**
     * Sets retry count.
     *
     * @param val the val
     */
    public static void setRetryCount(String val) {
        retryCount = Integer.parseInt(val);
    }

    /**
     * Gets source code encoding.
     *
     * @return the source code encoding
     */
    public static String getSourceCodeEncoding() {
        return sourceCodeEncoding;
    }

    /**
     * Sets source code encoding.
     *
     * @param val the val
     */
    public static void setSourceCodeEncoding(String val) {
        sourceCodeEncoding = val;
    }

    /**
     * Gets source code dir.
     *
     * @return the source code dir
     */
    public static String getSourceCodeDir() {
        return sourceCodeDir;
    }

    /**
     * Sets source code dir.
     *
     * @param val the val
     */
    public static void setSourceCodeDir(String val) {
        sourceCodeDir = val;
    }

    /**
     * Is send msg boolean.
     *
     * @return the boolean
     */
    public static boolean isSendMsg() {
        return sendMsg;
    }

    /**
     * Sets send msg.
     *
     * @param val the val
     */
    public static void setSendMsg(boolean val) {
        sendMsg = val;
    }

    /**
     * Gets sn.
     *
     * @return the sn
     */
    public static String getSN() {
        return SN;
    }

    /**
     * Sets sn.
     *
     * @param val the val
     */
    public static void setSN(String val) {
        SN = val;
    }

    /**
     * Gets snpwd.
     *
     * @return the snpwd
     */
    public static String getSNPWD() {
        return SNPWD;
    }

    /**
     * Sets snpwd.
     *
     * @param val the val
     */
    public static void setSNPWD(String val) {
        SNPWD = val;
    }
}
