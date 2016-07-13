package com.qa.framework.core;

import com.qa.framework.bean.*;
import com.qa.framework.exception.NoSuchSetupException;
import com.qa.framework.library.base.JsonHelper;
import com.qa.framework.library.httpclient.HttpMethod;
import com.qa.framework.verify.ContainExpectResult;
import com.qa.framework.verify.IExpectResult;
import com.qa.framework.verify.PairExpectResult;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 提供一个模板类
 * Created by apple on 15/11/19.
 */
public abstract class TestBase {
    protected static final Logger logger = Logger.getLogger(TestBase.class);

    /**
     * Get content string. 获得模拟httpmethod的内容
     *
     * @param params      the params
     * @param url         the url
     * @param httpMethod  the http method
     * @param storeCookie the store cookie
     * @param useCookie   the use cookie
     * @return the string
     */
    public String request(String url, List<Param> params, String httpMethod, boolean storeCookie, boolean useCookie) {
        String content = null;
        switch (httpMethod) {
            case "get":
                content = HttpMethod.useGetMethod(url, params, storeCookie, useCookie);
                break;
            case "post":
                content = HttpMethod.usePostMethod(url, params, storeCookie, useCookie);
                break;
            case "put":
                content = HttpMethod.usePutMethod(url, params, storeCookie, useCookie);
                break;
        }

        content = JsonHelper.decodeUnicode(content);
        logger.info("返回的信息:" + content);
        Assert.assertNotNull(content, "response返回空");
        return content;
    }

    public void processSetup(Setup setup) {
        String content = request(setup.getUrl(),setup.getParams(), setup.getHttpMethod(), setup.isStoreCookie(), setup.isUseCookie());
        Map<String, Object> jsonObject = JsonHelper.getJsonMapString(content);
        setup.setValue(jsonObject.get("data").toString());
    }

    /**
     * Process param. 处理param中需要接收setup中的返回值
     */
    public void processParam(TestData testData) {
        logger.info("Process Param in the Setup");
        if (testData.getSetups() != null) {
            Map<String, String> setupMap = new HashMap<String, String>();
            testData.setUseCookie(true);
            for (Setup setup : testData.getSetups()) {
                processSetup(setup);
                if (setup.getValue() != null && !"".equalsIgnoreCase(setup.getValue())) {
                    setupMap.put(setup.getName(), setup.getValue());
                }
            }
            for (Param param : testData.getParams()) { //处理param中接受setup值的问题
                if (param.getValue().contains("#{") && !param.getValue().contains(".")) {
                    //remove #{}
                    String key = param.getValue().substring(2, param.getValue().length() - 1);
                    if (setupMap.containsKey(key)) {
                        param.setValue(setupMap.get(key));
                    } else {
                        throw new NoSuchSetupException(key);
                    }
                }
            }
        }
    }


    /**
     * Verify result. 验证结果
     *
     * @param testData the test data
     * @param content  the content
     */
    public void verifyResult(TestData testData, String content, ParamValueGenerator paramValueGenerator) {
        //处理多余字符串
        processAfter(testData);
        logger.info("match the result class");
        paramValueGenerator.processExpect(testData);
        ExpectResult expectResult = testData.getExpectResult();
        for (IExpectResult iExpectResult : expectResult.getExpectResultImp()) {
            if (iExpectResult instanceof ContainExpectResult) {
                ContainExpectResult containKeyExpectResult = (ContainExpectResult) iExpectResult;
                containKeyExpectResult.comparereal(content);
            } else if (iExpectResult instanceof PairExpectResult) {
                PairExpectResult mapExpectResult = (PairExpectResult) iExpectResult;
                mapExpectResult.compareReal(content);
            } else {
                throw new IllegalArgumentException("没有匹配的期望结果集！");
            }
        }
        processExtraCheck(content, testData);

    }


    protected void processAfter(TestData testData) {
        logger.info("Process After in Test");
        if (testData.getAfter() != null) {
            try {
                Class cls = Class.forName(testData.getAfter().getClsName());
                Method method = cls.getDeclaredMethod(testData.getAfter().getMethodName());
                Object object = cls.newInstance();
                Object value = method.invoke(object);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void processExtraCheck(String content, TestData testData) {
        if (testData.getExtraCheck() != null) {
            for (Function function : testData.getExtraCheck().getFunctionList()) {
                try {
                    Class clz = Class.forName(function.getClsName());
                    Method method = clz.getDeclaredMethod(function.getMethodName(), String.class, TestData.class);
                    Object obj = clz.newInstance();
                    method.invoke(obj, content, testData);
                } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
