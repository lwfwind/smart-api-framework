package com.qa.framework.core;

import com.qa.framework.bean.*;
import com.qa.framework.cache.StringCache;
import com.qa.framework.library.base.DynamicCompile;
import com.qa.framework.library.base.StringHelper;
import com.qa.framework.library.database.DBHelper;
import com.qa.framework.library.reflect.ReflectHelper;
import com.qa.framework.verify.ContainExpectResult;
import com.qa.framework.verify.IExpectResult;
import com.qa.framework.verify.PairExpectResult;
import org.apache.log4j.Logger;

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
public class ParamValueGenerator {
    private static final Logger logger = Logger.getLogger(ParamValueGenerator.class);
    private DataConfig dataConfig;
    private List<TestData> testDatas = new ArrayList<TestData>();
    private StringCache stringCache;

    public ParamValueGenerator(DataConfig dataConfig) {
        this.dataConfig = dataConfig;
        this.testDatas = dataConfig.getTestDataLists();
        stringCache = new StringCache();
        processBefore();
        processSetup();
        processParam();
    }

    public ParamValueGenerator(DataConfig dataConfig, String testDataName) {
        this.dataConfig = dataConfig;
        for (TestData testData : dataConfig.getTestDataLists()) {
            if (testData.getName().equals(testDataName)) {
                this.testDatas.add(testData);
            }
        }
        stringCache = new StringCache();
        processBefore();
        processSetup();
        processParam();
    }

    //处理sql中的#{}
    public static String handleSql(String sql, String[] replacedStr) {
        StringBuilder newSql = new StringBuilder();
        String[] sqlContent = sql.split("#\\{[a-zA-Z0-9._]*\\}");
        if (sqlContent.length == 0) {
            newSql.append(replacedStr[0]);
        } else {
            if (sqlContent.length > replacedStr.length) {
                newSql.append(sqlContent[0]);
                for (int i = 1; i < sqlContent.length; i++) {
                    newSql.append(replacedStr[i - 1]);
                    newSql.append(sqlContent[i]);
                }
            } else {
                for (int i = 0; i < sqlContent.length; i++) {
                    newSql.append(sqlContent[i]);
                    newSql.append(replacedStr[i]);
                }
            }
        }
        return newSql.toString();
    }

    /**
     * Sort sqls list. 按照sqls中的依赖关系排序, 没有依赖关系的sql排前面
     *
     * @return the list
     */
    public List<Sql> sortSqls(Collection<Sql> sqls) {
        List<Sql> sortedSqls = new ArrayList<Sql>();         //sql的执行顺序
        List<Sql> unSortedSqls = new ArrayList<Sql>();       //未排序的sql
        for (Sql sql : sqls) {
            if (sql.getSqlStatement() != null) {
                //按照依赖给sql语句进行排序, 没有依赖的排前面
                if (sql.getDependOnsql() != null) {
                    unSortedSqls.add(sql);
                } else {
                    sortedSqls.add(sql);
                }
            }
        }
        //给sql按照sql依赖排序
        while (unSortedSqls.size() != 0) {
            Iterator<Sql> unSortedSqlsIter = unSortedSqls.iterator();
            while (unSortedSqlsIter.hasNext()) {
                Sql unSortedsql = unSortedSqlsIter.next();
                List<String> unSortedSqlList = new ArrayList<String>(Arrays.asList(unSortedsql.getDependOnsql()));     //将数组临时转为list对象, 方便去除对象
                if (unSortedsql.getDependOnsql() == null) {           //如果没有依赖其他sql
                    sortedSqls.add(unSortedsql);                    //将该sql放入sortedSql中去
                    unSortedSqlsIter.remove();              //并在unsorted中除去该sql
                } else {
                    for (Sql sortedSql : sortedSqls) {
                        Iterator<String> unSortedSqlListIter = unSortedSqlList.iterator();
                        while (unSortedSqlListIter.hasNext()) {
                            String sqlName = unSortedSqlListIter.next();
                            if (sqlName.equals(sortedSql.getName())) {   //如果unsql的名字出现在sortedSql中的话,则除去
                                unSortedSqlListIter.remove();
                            }
                        }
                    }
                    if (unSortedSqlList.size() != 0) {
                        String[] newDependOnsql = new String[unSortedSqlList.size()];
                        unSortedSqlList.toArray(newDependOnsql);
                        unSortedsql.setDependOnsql(newDependOnsql);
                    } else {
                        unSortedsql.setDependOnsql(null);
                        sortedSqls.add(unSortedsql);
                        unSortedSqlsIter.remove();
                    }
                }
            }
        }
        return sortedSqls;
    }

    //处理setup中param的占位
    public void processSetup() {
        for (TestData testData : testDatas) {
            List<Setup> setupList = testData.getSetups();
            if (setupList != null) {
                for (Setup setup : setupList) {
                    List<Param> setupParamList = setup.getParams();
                    Map<String, Param> setupParamMap = setup.getParamMap();
                    if (setupParamList != null) {
                        for (Param param : setupParamList) {
                            processParamDate(param);
                            processFunction(param, setup, testData);
                            processParam(param, setup, testData);
                            processParamFromSetup(testData, param);
                        }
                    }
                }
            }
        }
    }

    public void processBefore() {
        logger.info("Process Before in Test");
        for (TestData testData : testDatas) {
            if (testData.getBefore() != null) {
                try {
                    Class cls = Class.forName(testData.getBefore().getClsName());
                    Method method = cls.getDeclaredMethod(testData.getBefore().getMethodName());
                    Object object = cls.newInstance();
                    Object value = method.invoke(object);
                } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //处理正常流程中的param的sql, 日期, 从其他函数和setup接受值问题
    public void processParam() {
        for (TestData testData : testDatas) {
            List<Param> paramList = testData.getParams();
            if (paramList != null) {
                Map<String, Param> paramMap = testData.getParamMap();
                for (Param param : paramList) {
                    processParamDate(param);
                    processFunction(param, null, testData);
                    processParam(param, null, testData);
                    processParamFromSetup(testData, param);
                    processParamFromOtherTestData(param);
                    processParamPair(param);
                    processNormalParam(param, testData);
                }
            }
        }
    }

    //处理param没有变量
    public void processNormalParam(Param param, TestData testData) {
        if (param.getValue() != null && !param.getValue().contains("#{")) { //处理没有变量的param
            stringCache.put(param.getName(), param.getValue());
            stringCache.put(testData.getName() + "." + param.getName(), param.getValue());
        }
    }

    //处理param中需要接受setup中param值的问题
    public void processParamFromSetup(TestData testData, Param param) {
        if (testData.getSetups() != null && param.getSqls() == null && param.getFunction() == null && param.getDateStamp() == null && param.getPairs() == null) {
            if (param.getValue().contains("#{") && param.getValue().contains(".")) {
                //remove #{}
                String setupNameAndParam = param.getValue().substring(2, param.getValue().length() - 1);
                param.setValue(stringCache.getValue(setupNameAndParam));

            }
        }
    }

    public void processParamFromOtherTestData(Param param) {
        if (param.getSqls() == null && param.getFunction() == null && param.getDateStamp() == null && param.getPairs() == null) {
            if (param.getValue() != null) {
                if (param.getValue().contains("#{") && param.getValue().contains(".")) {
                    //remove #{}
                    String testDataNameAndParam = param.getValue().substring(2, param.getValue().length() - 1);
                    if (testDataNameAndParam.contains("#{")) {
                        StringBuilder newParamValue = new StringBuilder();
                        String[] residues = testDataNameAndParam.split("\\}#\\{");
                        for (int i = 0; i < residues.length; i++) {
                            newParamValue.append(stringCache.getValue(residues[i]));
                        }
                        param.setValue(newParamValue.toString());
                    } else {
                        param.setValue(stringCache.getValue(testDataNameAndParam));
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void processFunction(Param param, Setup setup, TestData testData) {
        if (param.getFunction() != null) {
            try {
                Class cls = Class.forName(param.getFunction().getClsName());
                Method method = cls.getDeclaredMethod(param.getFunction().getMethodName());
                Object object = cls.newInstance();
                Object value = method.invoke(object);
                param.setValue(value.toString());
                stringCache.put(param.getName(), param.getValue());
                stringCache.put(testData.getName() + "." + param.getName(), param.getValue());
                if (setup != null) {
                    stringCache.put(setup.getName() + "." + param.getName(), param.getValue());
                    stringCache.put(testData.getName() + "." + setup.getName() + "." + param.getName(), param.getValue());
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Process param date. 处理时间格式
     *
     * @param param the param
     */
    public void processParamDate(Param param) {
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
        }
    }

    public void processParam(Param param, Setup setup, TestData testData) {
        if (param.getSqls() != null) {
            List<Sql> sortedSqls = sortSqls(param.getSqls());       //sql排序
            if (param.getMultiple() != null) {
                Set<String> paramValues = new HashSet<String>();
                //如果param包含multiple值,且param value中有#{sql1.id, sql2.id}
                if (param.getValue().contains(",")) {
                    String cloneValue = param.getValue();
                    //remove #{}
                    cloneValue = cloneValue.substring(2, cloneValue.length() - 1);
                    String[] cloneValues = cloneValue.split(",");
                    for (String newCloneValue : cloneValues) {
                        Param param1 = new Param();
                        param1.setName(param.getName());
                        param1.setValue("#{" + newCloneValue.trim() + "}");
                        for (Sql sql : sortedSqls) {
                            if (newCloneValue.trim().contains(sql.getName())) {
                                String paramValue = executeSql(Arrays.asList(new Sql[]{sql}), param1, setup, testData, paramValues.size());
                                stringCache.put(param1.getValue() + paramValues.size(), paramValue);
                                paramValues.add(paramValue);
                            }
                        }
                    }
                } else {
                    while (paramValues.size() < Integer.valueOf(param.getMultiple())) {
                        String paramValue = executeSql(sortedSqls, param, setup, testData, paramValues.size());
                        stringCache.put(param.getValue() + paramValues.size(), paramValue);
                        paramValues.add(paramValue);
                    }
                }
                StringBuilder finalValue = new StringBuilder();
                for (String paramValue : paramValues) {
                    finalValue.append(paramValue + ",");
                }
                finalValue.deleteCharAt(finalValue.length() - 1);
                param.setValue(finalValue.toString());
            } else {
                param.setValue(executeSql(sortedSqls, param, setup, testData, null));

            }
            stringCache.put(param.getName(), param.getValue());
            stringCache.put(testData.getName() + "." + param.getName(), param.getValue());
            if (setup != null) {
                stringCache.put(setup.getName() + "." + param.getName(), param.getValue());
                stringCache.put(testData.getName() + "." + setup.getName() + "." + param.getName(), param.getValue());
            }
        }
    }

    /**
     * Execute sql string. 处理sql中#{}, 以及执行相应的sql, 生成最后的结果
     *
     * @param sortedSqls the sorted sqls  排序后的sql对象
     * @param param      the param      当前传入的参数
     * @param setup      the setup    param所属的setup
     * @return the string
     */
    public String executeSql(List<Sql> sortedSqls, Param param, Setup setup, TestData testData, Integer size) {
        for (Sql sql : sortedSqls) {
            if (sql.getSqlStatement().contains("#{")) {
                //处理sql语句中的#{}问题
                //第一步将#{\\S+}的值找出来
                List<String> lists = StringHelper.find(sql.getSqlStatement(), "#\\{[a-zA-Z0-9._]*\\}");
                String[] replacedStr = new String[lists.size()];   //替换sql语句中的#{}
                int i = 0;
                for (String list : lists) {
                    //去掉#{}
                    String proStr = list.substring(2, list.length() - 1);
                    //从缓存中去取相应的值
                    replacedStr[i++] = stringCache.getValue(proStr);
                }
                sql.setSqlStatement(handleSql(sql.getSqlStatement(), replacedStr));
            }
            logger.debug("SQL为:" + sql.getSqlStatement());
            Map<String, Object> recordInfo = DBHelper.queryOneRow(sql.getSqlStatement());//查询数据库, 将返回值按照returnValues的值放入HashMap
            for (int i = 0; i < sql.getReturnValues().length; i++) {
                String key = sql.getReturnValues()[i];
                String sqlkey = sql.getName() + "." + sql.getReturnValues()[i];
                String paramSqlKey = param.getName() + "." + sqlkey;
                String testDatakey = testData.getName() + "." + paramSqlKey;
                String value = null;
                if (recordInfo.get(key) == null) {
                    value = "null";
                } else {
                    value = recordInfo.get(key).toString();
                }
                stringCache.put(sqlkey, value);                         //将sql.属性的值存入缓存
                stringCache.put(paramSqlKey, value);
                stringCache.put(testDatakey, value);
                String othersqlkey = null;
                String otherParamSqlKey = null;
                String otherTestDataKey = null;
                if (size != null) {
                    othersqlkey = sql.getName() + "." + sql.getReturnValues()[i] + size;
                    otherParamSqlKey = param.getName() + "." + othersqlkey;
                    otherTestDataKey = testData.getName() + "." + otherParamSqlKey;
                    stringCache.put(othersqlkey, value);                         //将sql.属性的值存入缓存
                    stringCache.put(otherParamSqlKey, value);
                    stringCache.put(otherTestDataKey, value);
                }
                if (setup != null) {
                    String setupParamSqlKey = setup.getName() + "." + paramSqlKey;
                    String testDataSetupParamSqlKey = testData.getName() + "." + setupParamSqlKey;
                    stringCache.put(setupParamSqlKey, value);
                    stringCache.put(testDataSetupParamSqlKey, value);
                    if (size != null) {
                        stringCache.put(setup.getName() + "." + otherParamSqlKey, value);
                        stringCache.put(testData.getName() + "." + setup.getName() + otherParamSqlKey, value);
                    }
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
        return stringCache.getValue(decodeValue);
    }

    /**
     * Execute expect sql. 处理sql中#{}, 以及执行相应的sql, 生成最后的结果
     *
     * @param sortedSqls          the sorted sqls  排序后的sql对象
     * @param containExpectResult the param      string的预期结果
     * @return the string
     */
    public String executeExpectSql(List<Sql> sortedSqls, ContainExpectResult containExpectResult) {
        //将值赋给paramValue
        String decodeValue = containExpectResult.getValue();
        decodeValue = decodeValue.substring(2, decodeValue.length() - 1);
        String paramKey = decodeValue.split("\\.")[0];
        for (int j = 0; j < sortedSqls.size(); j++) {
            Sql sql = sortedSqls.get(j);
            if (sql.getSqlStatement().contains("#{")) {
                List<String> lists = StringHelper.find(sql.getSqlStatement(), "#\\{[a-zA-Z0-9._]*\\}");
                String[] replacedStr = new String[lists.size()];   //替换sql语句中的#{}
                int i = 0;
                for (String list : lists) {
                    //去掉#{}
                    String proStr = list.substring(2, list.length() - 1);
                    replacedStr[i++] = stringCache.getValue(proStr);
                }
                sql.setSqlStatement(handleSql(sql.getSqlStatement(), replacedStr));
                logger.debug("SQL为:" + sql.getSqlStatement());
            }
            if (sql.getName().equalsIgnoreCase(paramKey) && "array".equalsIgnoreCase(containExpectResult.getType())) {
                List<Map<String, Object>> recordInfos = DBHelper.queryRows(sql.getSqlStatement());//查询数据库, 将返回值按照returnValues的值放入HashMap
                for (int i = 0; i < sql.getReturnValues().length; i++) {
                    String key = sql.getReturnValues()[i];
                    StringBuilder value = new StringBuilder();
                    for (Map<String, Object> recordInfo : recordInfos) {
                        value.append(recordInfo.get(key).toString() + ",");
                    }
                    value.setCharAt(value.toString().length() - 1, ' ');
                    stringCache.put(key, value.toString());
                    stringCache.put(sql.getName() + "." + key, value.toString());
                }
            } else {
                Map<String, Object> recordInfo = DBHelper.queryOneRow(sql.getSqlStatement());//查询数据库, 将返回值按照returnValues的值放入HashMap
                for (int i = 0; i < sql.getReturnValues().length; i++) {
                    String key = sql.getReturnValues()[i];
                    String value = recordInfo.get(key).toString();
                    stringCache.put(key, value);
                    stringCache.put(sql.getName() + "." + key, value.toString());
                }
            }
        }
        return stringCache.getValue(decodeValue);
    }

    /**
     * Process param pair. 处理Param中的键值对, 转化成url能识别的格式
     *
     * @param param the param
     */
    public void processParamPair(Param param) {
        if (param.getPairs() != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("{");
            for (Pair pair : param.getPairs()) {
                processPair(pair);
                stringBuilder.append("\"" + pair.getKey() + "\"" + pair.getValue() + ",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append("}");
            param.setValue(stringBuilder.toString());
        }
    }

    public void processExpect(TestData testData) {
        List<Param> paramList = testData.getParams();
        Map<String, Param> paramMap = testData.getParamMap();
        if (paramMap != null) {
            ExpectResult expectResult = testData.getExpectResult();
            for (IExpectResult iExpectResult : expectResult.getExpectResultImp()) {
                if (iExpectResult instanceof ContainExpectResult) {
                    ContainExpectResult containExpectResult = (ContainExpectResult) iExpectResult;
                    processStringExpectResult(containExpectResult);
                } else if (iExpectResult instanceof PairExpectResult) {
                    PairExpectResult pairExpectResult = (PairExpectResult) iExpectResult;
                    processMapExpectResult(pairExpectResult);
                }
            }
        }

    }

    public void processStringExpectResult(ContainExpectResult containExpectResult) {
        if (containExpectResult.getSqls() != null) {
            List<Sql> sortedSql = sortSqls(containExpectResult.getSqls());
            String stringValue = executeExpectSql(sortedSql, containExpectResult);
            containExpectResult.setValue(stringValue);
        } else {
            if (containExpectResult.getValue() != null) {
                if (containExpectResult.getValue().contains("#{")) {
                    processParam(containExpectResult.getValue());
                }
            }
        }
    }

    public void processMapExpectResult(PairExpectResult pairExpectResult) {
        if (pairExpectResult.getPairs() != null) {
            List<Pair> pairs = pairExpectResult.getPairs();
            for (Pair pair : pairs) {
                processPair(pair);
            }
        }
    }

    public void processPair(Pair pair) {
        if (pair.getValue().contains("#{")) {
            String OriginalString = pair.getValue();
            logger.info("original expect pair string:" + OriginalString);
            List<String> lists = StringHelper.find(OriginalString, "#\\{.*?\\}");
            String[] placeHolders = new String[lists.size()];
            String[] placeHoldersValue = new String[lists.size()];
            int count = 0;
            for (String list : lists) {
                if (list.contains("+") || list.contains("-") || list.contains("*") || list.contains("/")) {
                    String calculateStr = list.substring(2, list.length() - 1);
                    List<String> keyList = new ArrayList<String>();
                    for (String key : stringCache.mapCache.keySet()) {
                        keyList.add(key);
                    }
                    Collections.sort(keyList, new Comparator<String>() {
                        public int compare(String key1, String key2) {
                            return key2.length() - key1.length();
                        }
                    });
                    for (String key : keyList) {
                        if (calculateStr.contains(key)) {
                            calculateStr = calculateStr.replace(key, stringCache.getValue(key));
                        }
                    }
                    try {
                        Object cal = DynamicCompile.evalCalculate(calculateStr);
                        OriginalString = OriginalString.replace(list, cal.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String inner = list.substring(2, list.length() - 1);
                    OriginalString = OriginalString.replace(list, inner);
                    if (!pair.isSort()) {
                        for (String key : stringCache.mapCache.keySet()) {
                            if (inner.equals(key)) {
                                OriginalString = OriginalString.replace(key, stringCache.getValue(key));
                            }
                        }
                    } else {
                        String value = stringCache.getValue(inner);
                        placeHolders[count] = inner;
                        placeHoldersValue[count] = value;
                        count++;
                    }
                }
            }
            if (pair.isSort()) {
                Arrays.sort(placeHoldersValue);
                for (int i = 0; i < count; i++) {
                    OriginalString = OriginalString.replace(placeHolders[i], placeHoldersValue[i]);
                }
            }
            logger.info("after replace,expect pair string:" + OriginalString);
            pair.setValue(OriginalString);
        }
    }

    /**
     * Process param string. 处理在Setup中,或是ExpectResult调用params中的值
     *
     * @param value the value   待处理的字符串
     * @return the string
     */
    public String processParam(String value) {
        String key = value.substring(2, value.length() - 1);      //先去除#{}在进行"."的处理
        String finalValue = stringCache.getValue(key);
        logger.info("正在设置:" + finalValue);
        return finalValue;
    }

    @Override
    public String toString() {
        List<String> stringList = new ArrayList<String>();
        for (TestData testData : testDatas) {
            List<Param> paramList = testData.getParams();
            if (paramList != null) {
                Map<String, Param> paramMap = testData.getParamMap();
                stringList.add(testData.getDesc() + "--->" + paramMap);
            }
        }
        return stringList.toString();
    }

    public void getTestDataParam(Object next) {
        next.toString();

    }
}

