package com.qa.framework.core;

import com.library.common.DynamicCompileHelper;
import com.library.common.JsonHelper;
import com.library.common.ReflectHelper;
import com.library.common.StringHelper;
import com.qa.framework.InstanceFactory;
import com.qa.framework.bean.*;
import com.qa.framework.cache.JsonPairCache;
import com.qa.framework.exception.NoSuchMethodException;
import com.qa.framework.exception.TestCaseParamException;
import com.qa.framework.library.database.DBHelper;
import com.qa.framework.library.httpclient.HttpMethod;
import com.qa.framework.verify.AssertTrueExpectResult;
import com.qa.framework.verify.ContainExpectResult;
import com.qa.framework.verify.IExpectResult;
import com.qa.framework.verify.PairExpectResult;
import org.apache.commons.lang3.reflect.MethodUtils;
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
     * @param testCase the test data
     */
    public static void processTestCase(TestCase testCase, TestSuite testSuite) {
        JsonPairCache jsonPairCache = new JsonPairCache();
        processBefore(testCase, jsonPairCache);
        processSetupParam(testCase, jsonPairCache);
        processSetupResultParam(testCase, jsonPairCache);
        processTestCaseParam(testCase, testSuite, jsonPairCache);
        processExpectResultBeforeExecute(testCase, jsonPairCache);
    }

    /**
     * Process before.
     *
     * @param testCase the test data
     */
    public static void processBefore(TestCase testCase, JsonPairCache jsonPairCache) {
        if (testCase.getBefore() != null) {
            logger.info("Process Before in xml-" + testCase.getCurrentFileName() + " TestCase-" + testCase.getName());
            Before before = testCase.getBefore();
            if (before.getSqls() != null) {
                List<Sql> sqls = before.getSqls();
                for (Sql sql : sqls) {
                    logger.info("需更新语句：" + sql.getSqlStatement());
                    DBHelper.executeUpdate(sql.getSqlStatement());
                }
            } else if (before.getFunctions() != null) {
                List<Function> functions = before.getFunctions();
                executeFunctionList(functions, jsonPairCache);
            }
        }
    }


    /**
     * Process setup param.
     *
     * @param testCase      the test data
     * @param jsonPairCache the json pair cache
     */
    public static void processSetupParam(TestCase testCase, JsonPairCache jsonPairCache) {
        List<Setup> setupList = testCase.getSetupList();
        if (setupList != null) {
            for (Setup setup : setupList) {
                List<Param> setupParamList = setup.getParams();
                if (setupParamList != null) {
                    for (Param param : setupParamList) {
                        executeFunction(param, setup, testCase, jsonPairCache);
                        executeSql(param, setup, testCase, jsonPairCache);
                        processParamDate(param, setup, testCase, jsonPairCache);
                        processParamFromBefore(testCase, param, jsonPairCache);
                    }
                }
            }
        }
    }

    /**
     * Process test data param.
     *
     * @param testCase      the test data
     * @param jsonPairCache the json pair cache
     */
    public static void processTestCaseParam(TestCase testCase, TestSuite testSuite, JsonPairCache jsonPairCache) {
        List<Param> paramList = testCase.getParams();
        if (paramList != null) {
            for (Param param : paramList) {
                executeFunction(param, null, testCase, jsonPairCache);
                executeSql(param, null, testCase, jsonPairCache);
                processParamDate(param, null, testCase, jsonPairCache);
                processParamFromSetupOrBefore(testCase, param, jsonPairCache);
                processParamFromTestSuite(testSuite, param);
                processParamFromGlobal(param);
                if (param.getValue().contains("#") || param.getValue().contains("@")) {
                    throw new TestCaseParamException(testCase.getName(), param.getName(), param.getValue());
                }
            }
        }
    }

    /**
     * Execute function.
     *
     * @param param         the param
     * @param setup         the setup
     * @param testCase      the test data
     * @param jsonPairCache the json pair cache
     */
    public static void executeFunction(Param param, Setup setup, TestCase testCase, JsonPairCache jsonPairCache) {
        List<Function> functionList = param.getFunctions();
        if (functionList != null) {
            for (Function function : functionList) {
                Object value = executeFunction(function);
                param.setValue(value.toString());
                jsonPairCache.put(param.getName(), param.getValue());
                jsonPairCache.put(testCase.getName() + "." + param.getName(), param.getValue());
                if (setup != null) {
                    jsonPairCache.put(setup.getName() + "." + param.getName(), param.getValue());
                    jsonPairCache.put(testCase.getName() + "." + setup.getName() + "." + param.getName(), param.getValue());
                }
            }
        }
    }

    public static void executeFunctionList(List<Function> functionList, JsonPairCache jsonPairCache) {
        if (functionList != null) {
            for (Function function : functionList) {
                Object value = executeFunction(function);
                if (value != null && function.getName() != null && jsonPairCache != null) {
                    if (value instanceof Map) {
                        Map<String, String> valueMap = (Map<String, String>) value;
                        if (valueMap.size() > 0) {
                            for (Object o : valueMap.entrySet()) {
                                Map.Entry entry = (Map.Entry) o;
                                String key = (String) entry.getKey();
                                String val = (String) entry.getValue();
                                jsonPairCache.put(function.getName() + "." + key, val);
                            }
                        }
                    } else {
                        jsonPairCache.put(function.getName(), value.toString());
                    }
                }
            }
        }
    }

    public static synchronized Object executeFunction(Function function) {
        try {
            Object value = null;
            String arguments = function.getArguments();
            if (arguments != null && !arguments.trim().equalsIgnoreCase("")) {
                String[] argumentsArray = arguments.split(",");
                int matchedMethodSize = 0;
                Method matchedMethod = null;
                final Method[] methods = Class.forName(function.getClsName()).getMethods();
                for (final Method method : methods) {
                    if (method.getName().equals(function.getMethodName()) && argumentsArray.length==method.getParameterTypes().length) {
                        matchedMethodSize++;
                        matchedMethod = method;
                    }
                }
                if (matchedMethodSize == 0) {
                    throw new NoSuchMethodException(function.getClsName(), function.getMethodName(), function.getArguments());
                } else if (matchedMethodSize == 1 && !function.getArguments().contains("(")) {
                    Object[] argumentsObjectArray = new Object[argumentsArray.length];
                    Class<?>[] parameterTypes = matchedMethod.getParameterTypes();
                    if(argumentsArray.length != parameterTypes.length){
                        throw new NoSuchMethodException(function.getClsName(), function.getMethodName(), function.getArguments());
                    }
                    for (int i = 0; i < parameterTypes.length; i++) {
                        Class<?> parameterType = parameterTypes[i];
                        if (String.class.equals(parameterType)) {
                            argumentsObjectArray[i] = argumentsArray[i];
                        } else if (byte.class.equals(parameterType)) {
                            argumentsObjectArray[i] = Byte.parseByte(argumentsArray[i]);
                        } else if (Byte.class.equals(parameterType)) {
                            argumentsObjectArray[i] = Byte.valueOf(argumentsArray[i]);
                        } else if (boolean.class.equals(parameterType)) {
                            argumentsObjectArray[i] = Boolean.parseBoolean(argumentsArray[i]);
                        } else if (Boolean.class.equals(parameterType)) {
                            argumentsObjectArray[i] = Boolean.valueOf(argumentsArray[i]);
                        } else if (short.class.equals(parameterType)) {
                            argumentsObjectArray[i] = Short.parseShort(argumentsArray[i]);
                        } else if (Short.class.equals(parameterType)) {
                            argumentsObjectArray[i] = Short.valueOf(argumentsArray[i]);
                        } else if (int.class.equals(parameterType)) {
                            argumentsObjectArray[i] = Integer.parseInt(argumentsArray[i]);
                        } else if (Integer.class.equals(parameterType)) {
                            argumentsObjectArray[i] = Integer.valueOf(argumentsArray[i]);
                        } else if (long.class.equals(parameterType)) {
                            argumentsObjectArray[i] = Long.parseLong(argumentsArray[i]);
                        } else if (Long.class.equals(parameterType)) {
                            argumentsObjectArray[i] = Long.valueOf(argumentsArray[i]);
                        } else if (float.class.equals(parameterType)) {
                            argumentsObjectArray[i] = Float.parseFloat(argumentsArray[i]);
                        } else if (Float.class.equals(parameterType)) {
                            argumentsObjectArray[i] = Float.valueOf(argumentsArray[i]);
                        } else if (double.class.equals(parameterType)) {
                            argumentsObjectArray[i] = Double.parseDouble(argumentsArray[i]);
                        } else if (Double.class.equals(parameterType)) {
                            argumentsObjectArray[i] = Double.valueOf(argumentsArray[i]);
                        }
                    }
                    value = MethodUtils.invokeStaticMethod(Class.forName(function.getClsName()), function.getMethodName(), argumentsObjectArray);

                } else {
                    Object[] argumentsObjectArray = new Object[argumentsArray.length];
                    for (int i = 0; i < argumentsArray.length; i++) {
                        if (argumentsArray[i].contains("(") && argumentsArray[i].contains(")")) {
                            String type = StringHelper.getBetweenString(argumentsArray[i], "(", ")");
                            String argumentsValue = argumentsArray[i].substring(0, argumentsArray[i].indexOf("("));
                            if (type.equalsIgnoreCase("int")) {
                                argumentsObjectArray[i] = Integer.parseInt(argumentsValue);
                            } else if (type.equalsIgnoreCase("long")) {
                                argumentsObjectArray[i] = Long.parseLong(argumentsValue);
                            } else if (type.equalsIgnoreCase("double")) {
                                argumentsObjectArray[i] = Double.parseDouble(argumentsValue);
                            } else if (type.equalsIgnoreCase("float")) {
                                argumentsObjectArray[i] = Float.parseFloat(argumentsValue);
                            } else if (type.equalsIgnoreCase("short")) {
                                argumentsObjectArray[i] = Short.parseShort(argumentsValue);
                            } else if (type.equalsIgnoreCase("boolean")) {
                                argumentsObjectArray[i] = Boolean.parseBoolean(argumentsValue);
                            } else {
                                argumentsObjectArray[i] = argumentsArray[i];
                            }
                        } else {
                            argumentsObjectArray[i] = argumentsArray[i];
                        }
                    }
                    value = MethodUtils.invokeStaticMethod(Class.forName(function.getClsName()), function.getMethodName(), argumentsObjectArray);
                }
            } else {
                value = MethodUtils.invokeStaticMethod(Class.forName(function.getClsName()), function.getMethodName());
            }
            if (value != null) {
                function.setValue(value);
                return value;
            }
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | java.lang.NoSuchMethodException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Process test data param.
     *
     * @param param         the param
     * @param setup         the setup
     * @param testCase      the test data
     * @param jsonPairCache the json pair cache
     */
    public static void executeSql(Param param, Setup setup, TestCase testCase, JsonPairCache jsonPairCache) {
        if (param.getSqls() != null) {
            List<Sql> sqlList = param.getSqls();
            executeSql(sqlList, param, setup, testCase, jsonPairCache);
            if (param.getValue().contains("#") || param.getValue().contains("@")) {
                param.setValue(handleReservedKeyChars(param.getValue(), jsonPairCache));
            }

            jsonPairCache.put(param.getName(), param.getValue());
            jsonPairCache.put(testCase.getName() + "." + param.getName(), param.getValue());
            if (setup != null) {
                jsonPairCache.put(setup.getName() + "." + param.getName(), param.getValue());
                jsonPairCache.put(testCase.getName() + "." + setup.getName() + "." + param.getName(), param.getValue());
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
            Map<String, Object> recordInfo = handleSql(sql, jsonPairCache);
            for (int i = 0; i < sql.getReturnValues().length; i++) {
                String key = sql.getReturnValues()[i];
                String sqlKey = sql.getName() + "." + sql.getReturnValues()[i];
                Assert.assertNotNull(recordInfo, "sql为" + sql.getSqlStatement());
                String value = null;
                if (recordInfo.get(key) == null) {
                    value = "";
                } else {
                    value = recordInfo.get(key).toString();
                }
                jsonPairCache.put(sqlKey, value);
            }
        }

    }

    /**
     * Execute sql string. 处理sql中#[}, 以及执行相应的sql, 生成最后的结果
     *
     * @param sqlList       the sqlList  sql列表
     * @param param         the param      当前传入的参数
     * @param setup         the setup    param所属的setup
     * @param testCase      the test data
     * @param jsonPairCache the json pair cache
     * @return the string
     */
    public static String executeSql(List<Sql> sqlList, Param param, Setup setup, TestCase testCase, JsonPairCache jsonPairCache) {
        for (Sql sql : sqlList) {
            Map<String, Object> recordInfo = handleSql(sql, jsonPairCache);
            for (int i = 0; i < sql.getReturnValues().length; i++) {
                String key = sql.getReturnValues()[i];
                String sqlKey = sql.getName() + "." + sql.getReturnValues()[i];
                String paramSqlKey = param.getName() + "." + sqlKey;
                String testDataKey = testCase.getName() + "." + paramSqlKey;
                Assert.assertNotNull(recordInfo, "sql为" + sql.getSqlStatement());
                String value = null;
                if (recordInfo.get(key) == null) {
                    value = " ";
                } else {
                    value = recordInfo.get(key).toString();
                }
                jsonPairCache.put(sqlKey, value);                         //将sql.属性的值存入缓存
                jsonPairCache.put(paramSqlKey, value);
                jsonPairCache.put(testDataKey, value);
                if (setup != null) {
                    String setupParamSqlKey = setup.getName() + "." + paramSqlKey;
                    String testDataSetupParamSqlKey = testCase.getName() + "." + setupParamSqlKey;
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

    private static Map<String, Object> handleSql(Sql sql, JsonPairCache jsonPairCache) {
        if (sql.getSqlStatement().contains("#") || sql.getSqlStatement().contains("@")) {
            sql.setSqlStatement(handleReservedKeyChars(sql.getSqlStatement(), jsonPairCache));
        }
        logger.debug("最终的SQL为:" + sql.getSqlStatement());
        if (sql.getDelay() != null) {
            try {
                Thread.sleep(Long.parseLong(sql.getDelay()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Map<String, Object> recordInfo;
        if (sql.getDb() == null) {
            recordInfo = DBHelper.queryOneRow(sql.getSqlStatement());
        } else {
            recordInfo = DBHelper.queryOneRow(sql.getDb(), sql.getSqlStatement());
        }
        return recordInfo;
    }

    public static void processParamFromSetupOrBefore(TestCase testCase, Param param, JsonPairCache jsonPairCache) {
        if (testCase.getSetupList() != null || testCase.getBefore() != null) {
            if (param.getValue().contains("#") || param.getValue().contains("@")) {
                param.setValue(handleReservedKeyChars(param.getValue(), jsonPairCache));
            }
        }
    }

    public static void processParamFromTestSuite(TestSuite testSuite, Param param) {
        if (testSuite.getFunctionList() != null) {
            //寻找suite级别的function
            if (param.getValue().contains("#") || param.getValue().contains("@")) {
                if (testSuite.getFunctionList().size() > 0) {
                    for (Function function : testSuite.getFunctionList()) {
                        if (function.getName() != null && param.getValue().contains(function.getName())) {
                            Object value = function.getValue();
                            if (value != null) {
                                if (value instanceof Map) {
                                    Map<String, String> valueMap = (Map<String, String>) value;
                                    if (valueMap.size() > 0) {
                                        for (Object o : valueMap.entrySet()) {
                                            Map.Entry entry = (Map.Entry) o;
                                            String key = (String) entry.getKey();
                                            String val = (String) entry.getValue();
                                            param.setValue(handleReservedKeyChars(param.getValue(), function.getName() + "." + key, val));
                                        }
                                    }
                                } else {
                                    param.setValue(handleReservedKeyChars(param.getValue(), function.getName(), value.toString()));
                                }
                            } else {
                                executeFunction(function);
                                if (function.getValue() != null) {
                                    if (function.getValue() instanceof Map) {
                                        Map<String, String> valueMap = (Map<String, String>) function.getValue();
                                        if (valueMap.size() > 0) {
                                            for (Object o : valueMap.entrySet()) {
                                                Map.Entry entry = (Map.Entry) o;
                                                String key = (String) entry.getKey();
                                                String val = (String) entry.getValue();
                                                param.setValue(handleReservedKeyChars(param.getValue(), function.getName() + "." + key, val));
                                            }
                                        }
                                    } else {
                                        param.setValue(handleReservedKeyChars(param.getValue(), function.getName(), function.getValue().toString()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    public static void processParamFromGlobal(Param param) {
        if (InstanceFactory.getGlobal().getFunctionList() != null) {
            //寻找Global级别的function
            if (param.getValue().contains("#") || param.getValue().contains("@")) {
                for (Function function : InstanceFactory.getGlobal().getFunctionList()) {
                    if (function.getName() != null && param.getValue().contains(function.getName())) {
                        Object value = function.getValue();
                        if (value != null) {
                            if (value instanceof Map) {
                                Map<String, String> valueMap = (Map<String, String>) value;
                                if (valueMap.size() > 0) {
                                    for (Object o : valueMap.entrySet()) {
                                        Map.Entry entry = (Map.Entry) o;
                                        String key = (String) entry.getKey();
                                        String val = (String) entry.getValue();
                                        param.setValue(handleReservedKeyChars(param.getValue(), function.getName() + "." + key, val));
                                    }
                                }
                            } else {
                                param.setValue(handleReservedKeyChars(param.getValue(), function.getName(), value.toString()));
                            }
                        } else {
                            executeFunction(function);
                            if (function.getValue() != null) {
                                if (function.getValue() instanceof Map) {
                                    Map<String, String> valueMap = (Map<String, String>) function.getValue();
                                    if (valueMap.size() > 0) {
                                        for (Object o : valueMap.entrySet()) {
                                            Map.Entry entry = (Map.Entry) o;
                                            String key = (String) entry.getKey();
                                            String val = (String) entry.getValue();
                                            param.setValue(handleReservedKeyChars(param.getValue(), function.getName() + "." + key, val));
                                        }
                                    }
                                } else {
                                    param.setValue(handleReservedKeyChars(param.getValue(), function.getName(), function.getValue().toString()));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void processParamFromBefore(TestCase testCase, Param param, JsonPairCache jsonPairCache) {
        if (testCase.getBefore() != null) {
            if (param.getValue().contains("#") || param.getValue().contains("@")) {
                param.setValue(handleReservedKeyChars(param.getValue(), jsonPairCache));
            }
        }
    }

    /**
     * Process setup result param.
     *
     * @param testCase      the test data
     * @param jsonPairCache the json pair cache
     */
    public static void processSetupResultParam(TestCase testCase, JsonPairCache jsonPairCache) {
        if (testCase.getSetupList() != null) {
            testCase.setUseCookie(true);
            for (Setup setup : testCase.getSetupList()) {
                logger.info("Process Setup in xml-" + testCase.getCurrentFileName() + " TestCase-" + testCase.getName() + " Setup-" + setup.getName());
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

    private static void processParamDate(Param param, Setup setup, TestCase testCase, JsonPairCache jsonPairCache) {
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
            jsonPairCache.put(testCase.getName() + "." + param.getName(), param.getValue());
            if (setup != null) {
                jsonPairCache.put(setup.getName() + "." + param.getName(), param.getValue());
                jsonPairCache.put(testCase.getName() + "." + setup.getName() + "." + param.getName(), param.getValue());
            }
        }
    }


    /**
     * Process expect result.
     *
     * @param testCase      the test data
     * @param jsonPairCache the json pair cache
     */
    public static void processExpectResultBeforeExecute(TestCase testCase, JsonPairCache jsonPairCache) {
        ExpectResults expectResults = testCase.getExpectResults();
        for (IExpectResult result : expectResults.getExpectResults()) {
            if (result instanceof ContainExpectResult) {
                ContainExpectResult containExpectResult = (ContainExpectResult) result;
                if (containExpectResult.getTextStatement().contains("#") || containExpectResult.getTextStatement().contains("@")) {
                    containExpectResult.setTextStatement(handleReservedKeyChars(containExpectResult.getTextStatement(), jsonPairCache));
                }
            } else if (result instanceof PairExpectResult) {
                PairExpectResult pairExpectResult = (PairExpectResult) result;
                Pair pair = pairExpectResult.getPair();
                if (pair.getValue().contains("#") || pair.getValue().contains("@")) {
                    pair.setValue(handleReservedKeyChars(pair.getValue(), jsonPairCache));
                }
            } else if (result instanceof AssertTrueExpectResult) {
                AssertTrueExpectResult assertTrueExpectResult = (AssertTrueExpectResult) result;
                if (assertTrueExpectResult.getTextStatement().contains("#") || assertTrueExpectResult.getTextStatement().contains("@")) {
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
     * @param testCase the test data
     */
    public static void processExpectResultAfterExecute(TestCase testCase, String response) {
        JsonPairCache jsonPairCache = new JsonPairCache();
        Map<String, String> pairMaps = JsonHelper.parseJsonToPairs(response);
        if (pairMaps.size() > 0) {
            for (Object o : pairMaps.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                String key = (String) entry.getKey();
                String val = (String) entry.getValue();
                jsonPairCache.put(key, val);
                jsonPairCache.put(testCase.getName() + "." + key, val);
            }
        }
        ExpectResults expectResults = testCase.getExpectResults();
        if (expectResults.getSqls() != null) {
            executeSqlList(expectResults.getSqls(), jsonPairCache);
        }
        if (expectResults.getFunctionList() != null) {
            executeFunctionList(expectResults.getFunctionList(), jsonPairCache);
        }
        for (IExpectResult result : expectResults.getExpectResults()) {
            if (result instanceof ContainExpectResult) {
                ContainExpectResult containExpectResult = (ContainExpectResult) result;
                if (containExpectResult.getTextStatement().contains("#") || containExpectResult.getTextStatement().contains("@")) {
                    containExpectResult.setTextStatement(handleReservedKeyChars(containExpectResult.getTextStatement(), jsonPairCache));
                }
            } else if (result instanceof PairExpectResult) {
                PairExpectResult pairExpectResult = (PairExpectResult) result;
                Pair pair = pairExpectResult.getPair();
                if (pair.getValue().contains("#") || pair.getValue().contains("@")) {
                    pair.setValue(handleReservedKeyChars(pair.getValue(), jsonPairCache));
                }
            } else if (result instanceof AssertTrueExpectResult) {
                AssertTrueExpectResult assertTrueExpectResult = (AssertTrueExpectResult) result;
                if (assertTrueExpectResult.getTextStatement().contains("#") || assertTrueExpectResult.getTextStatement().contains("@")) {
                    assertTrueExpectResult.setTextStatement(handleReservedKeyChars(assertTrueExpectResult.getTextStatement(), jsonPairCache));
                }
            } else {
                throw new IllegalArgumentException("没有匹配的期望结果集！");
            }
        }
    }

    /**
     * Process after.
     *
     * @param testCase the test data
     */
    @SuppressWarnings("unchecked")
    public static void processAfter(TestCase testCase) {
        if (testCase.getAfter() != null) {
            logger.info("Process After in xml-" + testCase.getCurrentFileName() + " TestCase-" + testCase.getName());
            After after = testCase.getAfter();
            if (after.getSqls() != null) {
                List<Sql> sqls = after.getSqls();
                for (Sql sql : sqls) {
                    logger.info("需更新语句：" + sql.getSqlStatement());
                    DBHelper.executeUpdate(sql.getSqlStatement());
                }
            } else if (after.getFunctions() != null) {
                List<Function> functions = after.getFunctions();
                executeFunctionList(functions, null);
            }
        }
    }

    /**
     * 处理所有的保留关键字符##
     *
     * @param oriContent    the ori content
     * @param jsonPairCache the json pair cache
     * @return the string
     */
    public static String handleReservedKeyChars(String oriContent, JsonPairCache jsonPairCache) {
        String pattern;
        if (oriContent.contains("#")) {
            pattern = "#.*?#";
        } else {
            pattern = "@.*?@";
        }
        List<String> reservedKeyList = StringHelper.find(oriContent, pattern);
        List<String> unReservedKeyList = Arrays.asList(oriContent.split(pattern));
        List<String> newList = new ArrayList<>();
        int k = 0;
        List<String> cacheList = new ArrayList<String>();
        for (String key : jsonPairCache.getMap().keySet()) {
            cacheList.add(key);
        }
        Collections.sort(cacheList, new Comparator<String>() {
            public int compare(String key1, String key2) {
                return key2.length() - key1.length();
            }
        });
        int i = 0;
        if (reservedKeyList.size() > 0) {
            if (unReservedKeyList.size() > 0) {
                for (String key : unReservedKeyList) {
                    newList.add(key);
                    if (i < reservedKeyList.size()) {
                        newList.add(reservedKeyList.get(i));
                        i++;
                    }
                }
            } else {
                newList = reservedKeyList;
            }
            List<String> finalList = new ArrayList<>();
            for (String str : newList) {
                if (str.startsWith("#") || str.startsWith("@")) {
                    String reservedChars = str.substring(1, str.length() - 1);
                    boolean isFound = false;
                    for (String cache : cacheList) {
                        if (!StringHelper.isInteger(cache) && reservedChars.contains(cache)) {
                            reservedChars = reservedChars.replace(cache, jsonPairCache.getValue(cache));
                            isFound = true;
                        }
                    }
                    if (isFound) {
                        try {
                            String result = DynamicCompileHelper.eval(reservedChars).toString();
                            finalList.add(result);
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    } else {
                        finalList.add(str);
                    }
                } else {
                    finalList.add(str);
                }
            }
            return StringHelper.join(finalList, "");
        }
        return oriContent;
    }

    public static String handleReservedKeyChars(String oriContent, String functionName, String functionValue) {
        String pattern;
        if (oriContent.contains("#")) {
            pattern = "#.*?#";
        } else {
            pattern = "@.*?@";
        }
        List<String> reservedKeyList = StringHelper.find(oriContent, pattern);
        List<String> unReservedKeyList = Arrays.asList(oriContent.split(pattern));
        List<String> newList = new ArrayList<>();
        int k = 0;
        int i = 0;
        if (reservedKeyList.size() > 0) {
            if (unReservedKeyList.size() > 0) {
                for (String key : unReservedKeyList) {
                    newList.add(key);
                    if (i < reservedKeyList.size()) {
                        newList.add(reservedKeyList.get(i));
                        i++;
                    }
                }
            } else {
                newList = reservedKeyList;
            }
            List<String> finalList = new ArrayList<>();
            for (String str : newList) {
                if (str.startsWith("#") || str.startsWith("@")) {
                    String reservedChars = str.substring(1, str.length() - 1);
                    if (reservedChars.contains(functionName)) {
                        reservedChars = reservedChars.replace(functionName, functionValue);
                        try {
                            String result = DynamicCompileHelper.eval(reservedChars).toString();
                            finalList.add(result);
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    } else {
                        finalList.add(str);
                    }
                } else {
                    finalList.add(str);
                }
            }
            return StringHelper.join(finalList, "");
        }
        return oriContent;
    }
}

