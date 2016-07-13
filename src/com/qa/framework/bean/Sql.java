package com.qa.framework.bean;

import java.util.Arrays;
import java.util.Map;

/**
 * 封装测试用例的一个测试数据中的sql操作
 * Created by apple on 15/11/18.
 */
public class Sql {
    private String name;                  //sql的名字
    private String[] returnValues;        //sql语句返回的多个值
    private String sqlStatement;          //sql语句
    private Map<String, String> returnValueMap;    //储存returnValues及对应的值
    private String[] dependOnsql;                 //是否依赖其他sql语句

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getReturnValues() {
        return returnValues;
    }

    public void setReturnValues(String[] returnValues) {
        this.returnValues = returnValues;
    }

    public void setReturnValues(String returnValues) {
        String[] returnContents = null;
        if (returnValues.contains(",")) {
            returnContents = returnValues.split(",");
            for (int i = 0; i < returnContents.length; i++) {
                returnContents[i] = returnContents[i].trim();
            }
        } else {
            returnContents = new String[]{returnValues.trim()};
        }
        setReturnValues(returnContents);
    }

    public String getSqlStatement() {
        return sqlStatement;
    }

    public void setSqlStatement(String sqlstatement) {
        this.sqlStatement = sqlstatement;
        //设置returnValue
        int selectIndex = sqlstatement.indexOf("select ") + "select ".length();
        int fromIndex = sqlstatement.indexOf("from ");
        String returnString = sqlstatement.substring(selectIndex, fromIndex);
        String[] returnStringValues = returnString.split(",");
        for (int i = 0; i < returnStringValues.length; i++) {
            returnStringValues[i] = returnStringValues[i].trim();
            if (returnStringValues[i].contains(" as ")) {
                returnStringValues[i] = returnStringValues[i].split("as")[1].trim();
            }
        }

        //处理xml中的<=转义
        if (returnStringValues.length == 0) {
            throw new RuntimeException("Param:" + this.getName() + " 设置returnValues失败, 请检查sql语句中是否包含正确的返回值");
        }
        setReturnValues(returnStringValues);
    }


    public Map<String, String> getReturnValueMap() {
        return returnValueMap;
    }

    public void setReturnValueMap(Map<String, String> returnValueMap) {
        this.returnValueMap = returnValueMap;
    }


    public String[] getDependOnsql() {
        return dependOnsql;
    }

    public void setDependOnsql(String[] dependOnsql) {
        this.dependOnsql = dependOnsql;
    }

    @Override
    public String toString() {
        return "Sql{" +
                "name='" + name + '\'' +
                ", returnValues=" + Arrays.toString(returnValues) +
                ", sqlStatement='" + sqlStatement + '\'' +
                '}';
    }
}
