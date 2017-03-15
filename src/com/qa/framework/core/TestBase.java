package com.qa.framework.core;

import com.qa.framework.bean.*;
import com.qa.framework.library.database.DBHelper;
import com.qa.framework.library.httpclient.HttpMethod;
import com.qa.framework.verify.ContainExpectResult;
import com.qa.framework.verify.IExpectResult;
import com.qa.framework.verify.PairExpectResult;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 提供一个模板类
 * Created by apple on 15/11/19.
 */
public abstract class TestBase {
    /**
     * The constant logger.
     */
    protected static final Logger logger = Logger.getLogger(TestBase.class);

    /**
     * Get content string. 获得模拟httpmethod的内容
     *
     * @param url         the url
     * @param params      the params
     * @param httpMethod  the http method
     * @param storeCookie the store cookie
     * @param useCookie   the use cookie
     * @return the string
     */
    public static String request(String url, Headers headers, List<Param> params, String httpMethod, boolean storeCookie, boolean useCookie) {
        String content = null;
        if (params != null) {
            for (Param param : params) {
                logger.info("--------" + param.toString());
            }
        }
        switch (httpMethod) {
            case "get":
                content = HttpMethod.useGetMethod(url, headers, params, storeCookie, useCookie);
                break;
            case "post":
                content = HttpMethod.usePostMethod(url, headers, params, storeCookie, useCookie);
                break;
            case "put":
                content = HttpMethod.usePutMethod(url, headers, params, storeCookie, useCookie);
                break;
            case "delete":
                content = HttpMethod.useDeleteMethod(url, headers, params, storeCookie, useCookie);
                break;
        }

        content = StringEscapeUtils.unescapeJava(content);
        logger.info("返回的信息:" + StringEscapeUtils.unescapeJava(content));
        Assert.assertNotNull(content, "response返回空");
        return content;
    }

    /**
     * Verify result. 验证结果
     *
     * @param testData the test data
     * @param content  the content
     */
    public void verifyResult(TestData testData, String content) {
        ExpectResult expectResult = testData.getExpectResult();
        for (IExpectResult iExpectResult : expectResult.getExpectResultImp()) {
            if (iExpectResult instanceof ContainExpectResult) {
                ContainExpectResult containKeyExpectResult = (ContainExpectResult) iExpectResult;
                containKeyExpectResult.compareReal(content);
            } else if (iExpectResult instanceof PairExpectResult) {
                PairExpectResult mapExpectResult = (PairExpectResult) iExpectResult;
                mapExpectResult.compareReal(content);
            } else {
                throw new IllegalArgumentException("没有匹配的期望结果集！");
            }
        }
    }

    /**
     * Process after.
     *
     * @param testData the test data
     */
    @SuppressWarnings("unchecked")
    public void processAfter(TestData testData) {
        if (testData.getAfter() != null) {
            try {
                logger.info("Process After in xml-" + testData.getCurrentFileName() + " TestData-" + testData.getName());
                After after = testData.getAfter();
                if (after.getSqls() != null) {
                    List<Sql> sqls = after.getSqls();
                    for (Sql sql : sqls) {
                        logger.info("需更新语句：" + sql.getSqlStatement());
                        DBHelper.executeUpdate(sql.getSqlStatement());
                    }
                } else if (after.getFunctions() != null) {
                    List<Function> functions = after.getFunctions();
                    for (Function function : functions) {
                        Class cls = Class.forName(function.getClsName());
                        Method method = cls.getDeclaredMethod(function.getMethodName());
                        Object object = cls.newInstance();
                        method.invoke(object);
                    }
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
