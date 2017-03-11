package com.qa.framework.config;


import com.library.common.StringHelper;
import com.qa.framework.classfinder.annotation.Value;

import static com.qa.framework.classfinder.ValueHelp.initConfigFields;

/**
 * Created by apple on 15/11/18.
 */
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
    @Value("isMultithread")
    private static boolean isMultithread = false;
    @Value("sendMsg")
    private static boolean sendMsg = false;
    //SMS配置
    @Value("SN")
    private static String SN;
    @Value("SNPWD")
    private static String SNPWD;

    //    单例模式
    private static PropConfig propConfig;

    static {
        PropConfig prop = new PropConfig();
    }

    private PropConfig() {
        initConfigFields(this);
    }

    public static PropConfig getInstance() {
        if (propConfig == null) {
            synchronized (PropConfig.class) {
                propConfig = new PropConfig();
            }
        }
        return propConfig;
    }

    public static String getBasePackage() {
        return basePackage;
    }

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
    public static void setUseProxy(String val) {
        useProxy = StringHelper.changeString2boolean(val);
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

    public static boolean getIsMultithread() {
        return isMultithread;
    }

    public static void setIsMultithread(String val) {
        isMultithread = StringHelper.changeString2boolean(val);
    }

    public static boolean isSendMsg() {
        return sendMsg;
    }

    public static void setSendMsg(String val) {
        sendMsg = StringHelper.changeString2boolean(val);
    }

    public static String getSN() {
        return SN;
    }

    public static void setSN(String val) {
        SN = val;
    }

    public static String getSNPWD() {
        return SNPWD;
    }

    public static void setSNPWD(String val) {
        SNPWD = val;
    }
}
