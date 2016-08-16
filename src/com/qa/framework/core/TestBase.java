package com.qa.framework.core;

import com.qa.framework.bean.*;
import com.qa.framework.library.base.JsonHelper;
import com.qa.framework.library.base.StringHelper;
import com.qa.framework.library.database.DBHelper;
import com.qa.framework.library.httpclient.HttpMethod;
import com.qa.framework.util.StringUtil;
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
import java.util.Set;

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
    public String request(String url, List<Param> params, String httpMethod, boolean storeCookie, boolean useCookie) {
        String content = null;
        if (params!=null) {
            for (Param param : params) {
                logger.info("--------" + param.toString());
            }
        }
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
            case "delete":
                content = HttpMethod.usePutMethod(url, params, storeCookie, useCookie);
                break;
        }

        content = JsonHelper.decodeUnicode(content);
        logger.info("返回的信息:" + content);
        Assert.assertNotNull(content, "response返回空");
        return content;
    }

    /**
     * Process param. 处理param中需要接收setup中的返回值
     *
     * @param testData the test data
     */
    @SuppressWarnings("unchecked")
    public void processSetupResultParam(TestData testData) {
        if (testData.getSetupList() != null) {
            Map<String, String> setupMap = new HashMap<String, String>();
            testData.setUseCookie(true);
            for (Setup setup : testData.getSetupList()) {
                logger.info("Process Setup in xml-" + testData.getCurrentFileName() + " TestData-" + testData.getName() + " Setup-" + setup.getName());
                String content = request(setup.getUrl(), setup.getParams(), setup.getHttpMethod(), setup.isStoreCookie(), setup.isUseCookie());
                Map<String, Object> jsonObject = JsonHelper.getJsonMapString(content);
                if (jsonObject.size() > 0) {
                    Set<String> Set = jsonObject.keySet();
                    for (String key : Set) {
                        Object object = jsonObject.get(key);
                        if (object instanceof Map) {
                            Map<String, Object> map = (Map<String, Object>) object;
                            for (String subKey : map.keySet()) {
                                setupMap.put(subKey, map.get(subKey).toString());
                                setupMap.put(setup.getName() + "." + subKey, map.get(subKey).toString());
                            }
                        } else if (object instanceof List) {
                            List<Map<String, Object>> listMap = (List<Map<String, Object>>) object;
                            for (Map<String, Object> map : listMap) {
                                for (String subKey : map.keySet()) {
                                    setupMap.put(subKey, map.get(subKey).toString());
                                    setupMap.put(setup.getName() + "." + subKey, map.get(subKey).toString());
                                }
                            }
                        } else {
                            setupMap.put(key, object.toString());
                            setupMap.put(setup.getName() + "." + key, object.toString());
                        }
                    }
                }
            }
            for (Param param : testData.getParams()) { //处理param中接受setup值的问题
                if (param.getValue().contains("#{")) {
                    //处理语句中的#{}问题
                    //第一步将#{\\S+}的值找出来
                    List<String> lists = StringHelper.find(param.getValue(), "#\\{[a-zA-Z0-9._]*\\}");
                    String[] replacedStr = new String[lists.size()];   //替换sql语句中的#{}
                    int i = 0;
                    for (String list : lists) {
                        //去掉#{}
                        String proStr = list.substring(2, list.length() - 1);
                        //从缓存中去取相应的值
                        replacedStr[i++] = setupMap.get(proStr);
                    }
                    param.setValue(StringUtil.handleSpecialChar(param.getValue(), replacedStr));
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
