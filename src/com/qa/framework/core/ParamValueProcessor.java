package com.qa.framework.core;

import com.library.common.DynamicCompileHelper;
import com.library.common.JsonHelper;
import com.library.common.ReflectHelper;
import com.library.common.StringHelper;
import com.qa.framework.bean.*;
import com.qa.framework.cache.JsonPairCache;
import com.qa.framework.library.database.DBHelper;
import com.qa.framework.library.httpclient.HttpMethod;
import com.qa.framework.verify.AssertTrueExpectResult;
import com.qa.framework.verify.ContainExpectResult;
import com.qa.framework.verify.IExpectResult;
import com.qa.framework.verify.PairExpectResult;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 处理param中value的变量
 * Created by apple on 15/11/23.
 */
public class ParamValueProcessor {
    private static final Logger logger = Logger.getLogger(ParamValueProcessor.class);

    /**
     * Process test data.
     *
     * @param testData the test data
     */
    public static void processTestData(TestData testData) {
        JsonPairCache jsonPairCache1 = new JsonPairCache();
        processBefore(testData);
        processSetupParam(testData, jsonPairCache1);
        processSetupResultParam(testData, jsonPairCache1);
        processTestDataParam(testData, jsonPairCache1);
    }

    /**
     * Process before.
     *
     * @param testData the test data
     */
    public static void processBefore(TestData testData) {
        if (testData.getBefore() != null)
            try {
                logger.info("Process Before in xml-" + testData.getCurrentFileName() + " TestData-" + testData.getName());
                Before before = testData.getBefore();
                if (before.getSqls() != null) {
                    List<Sql> sqls = before.getSqls();
                    for (Sql sql : sqls) {
                        logger.info("需更新语句：" + sql.getSqlStatement());
                        DBHelper.executeUpdate(sql.getSqlStatement());
                    }
                } else if (before.getFunctions() != null) {
                    List<Function> functions = before.getFunctions();
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


    /**
     * Process setup param.
     *
     * @param testData      the test data
     * @param jsonPairCache the json pair cache
     */
    public static void processSetupParam(TestData testData, JsonPairCache jsonPairCache) {
        List<Setup> setupList = testData.getSetupList();
        if (setupList != null) {
            for (Setup setup : setupList) {
                List<Param> setupParamList = setup.getParams();
                if (setupParamList != null) {
                    for (Param param : setupParamList) {
                        executeFunction(param, setup, testData, jsonPairCache);
                        executeSql(param, setup, testData, jsonPairCache);
                        processParamDate(param, setup, testData, jsonPairCache);
                        processParamFromSetup(testData, param, jsonPairCache);
                    }
                }
            }
        }
    }

    /**
     * Process test data param.
     *
     * @param testData      the test data
     * @param jsonPairCache the json pair cache
     */
    public static void processTestDataParam(TestData testData, JsonPairCache jsonPairCache) {
        List<Param> paramList = testData.getParams();
        if (paramList != null) {
            for (Param param : paramList) {
                executeFunction(param, null, testData, jsonPairCache);
                executeSql(param, null, testData, jsonPairCache);
                processParamDate(param, null, testData, jsonPairCache);
                processParamFromSetup(testData, param, jsonPairCache);
            }
        }
        processExpectResultBeforeExecute(testData, jsonPairCache);

    }

    /**
     * Execute function.
     *
     * @param param         the param
     * @param setup         the setup
     * @param testData      the test data
     * @param jsonPairCache the json pair cache
     */
    public static void executeFunction(Param param, Setup setup, TestData testData, JsonPairCache jsonPairCache) {
        if (param.getFunction() != null) {
            try {
                Class cls = Class.forName(param.getFunction().getClsName());
                Method method = cls.getDeclaredMethod(param.getFunction().getMethodName());
                Object object = cls.newInstance();
                Object value = method.invoke(object);
                param.setValue(value.toString());
                jsonPairCache.put(param.getName(), param.getValue());
                jsonPairCache.put(testData.getName() + "." + param.getName(), param.getValue());
                if (setup != null) {
                    jsonPairCache.put(setup.getName() + "." + param.getName(), param.getValue());
                    jsonPairCache.put(testData.getName() + "." + setup.getName() + "." + param.getName(), param.getValue());
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                logger.error(e.getMessage(),e);
            }
        }
    }

    public static void executeFunctionList(List<Function> functionList, JsonPairCache jsonPairCache){
        if(functionList != null){
            for(Function function : functionList){
                try {
                    Class cls = Class.forName(function.getClsName());
                    Method method = cls.getDeclaredMethod(function.getMethodName());
                    Object object = cls.newInstance();
                    Object value = method.invoke(object);
                    jsonPairCache.put(function.getName(), value.toString());
                } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    logger.error(e.getMessage(),e);
                }
            }
        }
    }

    /**
     * Process test data param.
     *
     * @param param         the param
     * @param setup         the setup
     * @param testData      the test data
     * @param jsonPairCache the json pair cache
     */
    public static void executeSql(Param param, Setup setup, TestData testData, JsonPairCache jsonPairCache) {
        if (param.getSqls() != null) {
            List<Sql> sqlList = param.getSqls();
            executeSql(sqlList, param, setup, testData, jsonPairCache);
            if (param.getValue().contains("#{")) {
                param.setValue(handleReservedKeyChars(param.getValue(), jsonPairCache));
            }

            jsonPairCache.put(param.getName(), param.getValue());
            jsonPairCache.put(testData.getName() + "." + param.getName(), param.getValue());
            if (setup != null) {
                jsonPairCache.put(setup.getName() + "." + param.getName(), param.getValue());
                jsonPairCache.put(testData.getName() + "." + setup.getName() + "." + param.getName(), param.getValue());
            }
        }
    }

    /**
     * Execute sql list
     *
     * @param sqlList       the sql list
     * @param jsonPairCache the json pair cache
     */
    public static void executeSqlList(List<Sql> sqlList, JsonPairCache jsonPairCache) {
        for (Sql sql : sqlList) {
            if (sql.getSqlStatement().contains("#{")) {
                sql.setSqlStatement(handleReservedKeyChars(sql.getSqlStatement(), jsonPairCache));
            }
            logger.debug("最终的SQL为:" + sql.getSqlStatement());
            Map<String, Object> recordInfo = DBHelper.queryOneRow(sql.getSqlStatement());
            for (int i = 0; i < sql.getReturnValues().length; i++) {
                String key = sql.getReturnValues()[i];
                String sqlkey = sql.getName() + "." + sql.getReturnValues()[i];
                Assert.assertNotNull(recordInfo, "sql为" + sql.getSqlStatement());
                String value = null;
                if (recordInfo.get(key) == null) {
                    value = "";
                } else {
                    value = recordInfo.get(key).toString();
                }
                jsonPairCache.put(sqlkey, value);
            }
        }

    }

    /**
     * Execute sql string. 处理sql中#{}, 以及执行相应的sql, 生成最后的结果
     *
     * @param sqlList       the sqlList  sql列表
     * @param param         the param      当前传入的参数
     * @param setup         the setup    param所属的setup
     * @param testData      the test data
     * @param jsonPairCache the json pair cache
     * @return the string
     */
    public static String executeSql(List<Sql> sqlList, Param param, Setup setup, TestData testData, JsonPairCache jsonPairCache) {
        for (Sql sql : sqlList) {
            if (sql.getSqlStatement().contains("#{")) {
                sql.setSqlStatement(handleReservedKeyChars(sql.getSqlStatement(), jsonPairCache));
            }
            logger.debug("最终的SQL为:" + sql.getSqlStatement());
            Map<String, Object> recordInfo = DBHelper.queryOneRow(sql.getSqlStatement());//查询数据库, 将返回值按照returnValues的值放入HashMap
            for (int i = 0; i < sql.getReturnValues().length; i++) {
                String key = sql.getReturnValues()[i];
                String sqlkey = sql.getName() + "." + sql.getReturnValues()[i];
                String paramSqlKey = param.getName() + "." + sqlkey;
                String testDatakey = testData.getName() + "." + paramSqlKey;
                Assert.assertNotNull(recordInfo, "sql为" + sql.getSqlStatement());
                String value = null;
                if (recordInfo == null || recordInfo.get(key) == null) {
                    value = " ";
                } else {
                    value = recordInfo.get(key).toString();
                }
                jsonPairCache.put(sqlkey, value);                         //将sql.属性的值存入缓存
                jsonPairCache.put(paramSqlKey, value);
                jsonPairCache.put(testDatakey, value);
                if (setup != null) {
                    String setupParamSqlKey = setup.getName() + "." + paramSqlKey;
                    String testDataSetupParamSqlKey = testData.getName() + "." + setupParamSqlKey;
                    jsonPairCache.put(setupParamSqlKey, value);
                    jsonPairCache.put(testDataSetupParamSqlKey, value);
                }
            }
        }
        //将值赋给paramValue
        String decodeValue = param.getValue();
        if (decodeValue == null) {
            throw new RuntimeException("请检查param:" + param + "是否有值");
        }
        decodeValue = decodeValue.substring(2, decodeValue.length() - 1);
        //param的值可能有3种情况setup, sql.id, param.sql.id, 但是实际上只会是sql.id一种, 因为setup的情况不处理, 若是param.sql.id则sqlstatement必为空
        return jsonPairCache.getValue(decodeValue);
    }

    /**
     * 处理param中需要接受setup中param值的问题
     *
     * @param testData      the test data
     * @param param         the param
     * @param jsonPairCache the json pair cache
     */
    public static void processParamFromSetup(TestData testData, Param param, JsonPairCache jsonPairCache) {
        if (testData.getSetupList() != null && param.getSqls() == null && param.getFunction() == null && param.getDateStamp() == null) {
            if (param.getValue().contains("#{")) {
                param.setValue(handleReservedKeyChars(param.getValue(), jsonPairCache));
            }
        }
    }

    /**
     * Process setup result param.
     *
     * @param testData      the test data
     * @param jsonPairCache the json pair cache
     */
    public static void processSetupResultParam(TestData testData, JsonPairCache jsonPairCache) {
        if (testData.getSetupList() != null) {
            testData.setUseCookie(true);
            for (Setup setup : testData.getSetupList()) {
                logger.info("Process Setup in xml-" + testData.getCurrentFileName() + " TestData-" + testData.getName() + " Setup-" + setup.getName());
                String response = HttpMethod.request(setup.getUrl(), setup.getHeaders(), setup.getParams(), setup.getHttpMethod(), setup.isStoreCookie(), setup.isUseCookie());
                Map<String, String> pairMaps = JsonHelper.parseJsonToPairs(response);
                if (pairMaps.size() > 0) {
                    for (Object o : pairMaps.entrySet()) {
                        Map.Entry entry = (Map.Entry) o;
                        String key = (String) entry.getKey();
                        String val = (String) entry.getValue();
                        jsonPairCache.put(key, val);
                        jsonPairCache.put(setup.getName() + "." + key, val);
                    }
                }
            }
        }
    }

    private static void processParamDate(Param param, Setup setup, TestData testData, JsonPairCache jsonPairCache) {
        if (param.getDateStamp() != null) {
            DateStamp dateStamp = param.getDateStamp();
            Calendar c = Calendar.getInstance();
            Field[] fields = dateStamp.getClass().getDeclaredFields(); //遍历成员变量, 寻找哪些属性不为空
            if (dateStamp.getBaseTime() == null || "".equalsIgnoreCase(dateStamp.getBaseTime())) {
                c.setTime(new Date());
            } else {
                SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                try {
                    Date date = dateFormater.parse(dateStamp.getBaseTime());
                    c.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            List<Field> setFields = new ArrayList<Field>();
            List<Field> addFields = new ArrayList<Field>();
            for (Field field : fields) {
                Object obj = ReflectHelper.getMethod(dateStamp, field.getName());
                if (obj != null && !"".equalsIgnoreCase(obj.toString())) {
                    if (field.getName().contains("set")) {
                        setFields.add(field);
                    } else if (field.getName().contains("add")) {
                        addFields.add(field);
                    }
                }
            }

            if (addFields != null) {
                for (Field field : addFields) {
                    Object obj = ReflectHelper.getMethod(dateStamp, field.getName());
                    if (field.getName().contains("Year")) {
                        c.add(Calendar.YEAR, Integer.parseInt(obj.toString()));
                    } else if (field.getName().contains("Month")) {
                        c.add(Calendar.MONTH, Integer.parseInt(obj.toString()));
                    } else if (field.getName().contains("Day")) {
                        c.add(Calendar.DAY_OF_MONTH, Integer.parseInt(obj.toString()));
                    } else if (field.getName().contains("Hour")) {
                        c.add(Calendar.HOUR_OF_DAY, Integer.parseInt(obj.toString()));
                    } else if (field.getName().contains("Min")) {
                        c.add(Calendar.MINUTE, Integer.parseInt(obj.toString()));
                    } else if (field.getName().contains("Second")) {
                        c.add(Calendar.SECOND, Integer.parseInt(obj.toString()));
                    }
                }
            }
            if (setFields != null) {
                for (Field field : setFields) {
                    Object obj = ReflectHelper.getMethod(dateStamp, field.getName());
                    if (obj != null && !"".equalsIgnoreCase(obj.toString())) {
                        if (field.getName().contains("Year")) {
                            c.set(Calendar.YEAR, Integer.parseInt(obj.toString()));
                        } else if (field.getName().contains("Month")) {
                            c.set(Calendar.MONTH, Integer.parseInt(obj.toString()) - 1);
                        } else if (field.getName().contains("Day")) {
                            c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(obj.toString()));
                        } else if (field.getName().contains("Hour")) {
                            c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(obj.toString()));
                        } else if (field.getName().contains("Min")) {
                            c.set(Calendar.MINUTE, Integer.parseInt(obj.toString()));
                        } else if (field.getName().contains("Second")) {
                            c.set(Calendar.SECOND, Integer.parseInt(obj.toString()));
                        }
                    }
                }
            }
            logger.info("设置的时间为:" + c.getTime());
            if (dateStamp.getDateFormat() != null) {
                param.setValue(new SimpleDateFormat(dateStamp.getDateFormat()).format(c.getTime()));
            } else {
                param.setValue(c.getTimeInMillis() / 1000 + "");
            }
            jsonPairCache.put(param.getName(), param.getValue());
            jsonPairCache.put(testData.getName() + "." + param.getName(), param.getValue());
            if (setup != null) {
                jsonPairCache.put(setup.getName() + "." + param.getName(), param.getValue());
                jsonPairCache.put(testData.getName() + "." + setup.getName() + "." + param.getName(), param.getValue());
            }
        }
    }


    /**
     * Process expect result.
     *
     * @param testData      the test data
     * @param jsonPairCache the json pair cache
     */
    public static void processExpectResultBeforeExecute(TestData testData, JsonPairCache jsonPairCache) {
        ExpectResults expectResult = testData.getExpectResults();
        for (IExpectResult result : expectResult.getExpectResults()) {
            if (result instanceof ContainExpectResult) {
                ContainExpectResult containExpectResult = (ContainExpectResult) result;
                if (containExpectResult.getTextStatement().contains("#{")) {
                    containExpectResult.setTextStatement(handleReservedKeyChars(containExpectResult.getTextStatement(), jsonPairCache));
                }
            } else if (result instanceof PairExpectResult) {
                PairExpectResult pairExpectResult = (PairExpectResult) result;
                    Pair pair = pairExpectResult.getPair();
                    if (pair.getValue().contains("#{")) {
                        pair.setValue(handleReservedKeyChars(pair.getValue(), jsonPairCache));
                    }
            } else if (result instanceof AssertTrueExpectResult) {
                AssertTrueExpectResult assertTrueExpectResult = (AssertTrueExpectResult) result;
                if (assertTrueExpectResult.getTextStatement().contains("#{")) {
                    assertTrueExpectResult.setTextStatement(handleReservedKeyChars(assertTrueExpectResult.getTextStatement(), jsonPairCache));
                }
            } else {
                throw new IllegalArgumentException("没有匹配的期望结果集！");
            }
        }
    }

    /**
     * Process expect result.
     *
     * @param testData      the test data
     */
    public static void processExpectResultAfterExecute(TestData testData, String response) {
        JsonPairCache jsonPairCache = new JsonPairCache();
        Map<String, String> pairMaps = JsonHelper.parseJsonToPairs(response);
        if (pairMaps.size() > 0) {
            for (Object o : pairMaps.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                String key = (String) entry.getKey();
                String val = (String) entry.getValue();
                jsonPairCache.put(key, val);
                jsonPairCache.put(testData.getName() + "." + key, val);
            }
        }
        ExpectResults expectResults = testData.getExpectResults();
        if (expectResults.getSqls() != null) {
            executeSqlList(expectResults.getSqls(), jsonPairCache);
        }
        if(expectResults.getFunctionList() != null){
            executeFunctionList(expectResults.getFunctionList(),jsonPairCache);
        }
        for (IExpectResult result : expectResults.getExpectResults()) {
            if (result instanceof ContainExpectResult) {
                ContainExpectResult containExpectResult = (ContainExpectResult) result;
                if (containExpectResult.getTextStatement().contains("#{")) {
                    containExpectResult.setTextStatement(handleReservedKeyChars(containExpectResult.getTextStatement(), jsonPairCache));
                }
            } else if (result instanceof PairExpectResult) {
                PairExpectResult pairExpectResult = (PairExpectResult) result;
                Pair pair = pairExpectResult.getPair();
                if (pair.getValue().contains("#{")) {
                    pair.setValue(handleReservedKeyChars(pair.getValue(), jsonPairCache));
                }
            } else if (result instanceof AssertTrueExpectResult) {
                AssertTrueExpectResult assertTrueExpectResult = (AssertTrueExpectResult) result;
                if (assertTrueExpectResult.getTextStatement().contains("#{")) {
                    assertTrueExpectResult.setTextStatement(handleReservedKeyChars(assertTrueExpectResult.getTextStatement(), jsonPairCache));
                }
            } else {
                throw new IllegalArgumentException("没有匹配的期望结果集！");
            }
        }
    }

    /**
     * 处理所有的保留关键字符#{}
     *
     * @param oriContent    the ori content
     * @param jsonPairCache the json pair cache
     * @return the string
     */
    public static String handleReservedKeyChars(String oriContent, JsonPairCache jsonPairCache) {
        String pattern = "#\\{.*?\\}";
        List<String> lists = StringHelper.find(oriContent, pattern);
        String[] replacedStr = new String[lists.size()];
        int k = 0;
        for (String list : lists) {
            String reservedChars = list.substring(2, list.length() - 1);
            if (reservedChars.contains("+") || reservedChars.contains("-") || reservedChars.contains("*") || reservedChars.contains("/")) {
                List<String> keyList = new ArrayList<String>();
                for (String key : jsonPairCache.getMap().keySet()) {
                    keyList.add(key);
                }
                Collections.sort(keyList, new Comparator<String>() {
                    public int compare(String key1, String key2) {
                        return key2.length() - key1.length();
                    }
                });
                for (String key : keyList) {
                    if (reservedChars.contains(key)) {
                        reservedChars = reservedChars.replace(key, jsonPairCache.getValue(key));
                    }
                }
                try {
                    replacedStr[k++] = DynamicCompileHelper.eval(reservedChars).toString();
                } catch (Exception e) {
                    logger.error(e.getMessage(),e);
                }
            }
            else {
                replacedStr[k++] = jsonPairCache.getValue(reservedChars);
            }
        }
        StringBuilder newContent = new StringBuilder();
        String[] unReservedKeyContentList = oriContent.split(pattern);
        if (replacedStr.length > 0 && replacedStr[0] != null) {
            if (unReservedKeyContentList.length == 0) {
                newContent.append(replacedStr[0]);
            } else {
                if (unReservedKeyContentList.length > replacedStr.length) {
                    newContent.append(unReservedKeyContentList[0]);
                    for (int i = 1; i < unReservedKeyContentList.length; i++) {
                        newContent.append(replacedStr[i - 1]);
                        newContent.append(unReservedKeyContentList[i]);
                    }
                } else {
                    for (int i = 0; i < unReservedKeyContentList.length; i++) {
                        newContent.append(unReservedKeyContentList[i]);
                        newContent.append(replacedStr[i]);
                    }
                }
            }
            return newContent.toString();
        } else {
            return oriContent;
        }
    }
}

