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

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get return values string [ ].
     *
     * @return the string [ ]
     */
    public String[] getReturnValues() {
        return returnValues;
    }

    /**
     * Sets return values.
     *
     * @param returnValues the return values
     */
    public void setReturnValues(String[] returnValues) {
        this.returnValues = returnValues;
    }

    /**
     * Gets sql statement.
     *
     * @return the sql statement
     */
    public String getSqlStatement() {
        return sqlStatement;
    }

    /**
     * Sets sql statement.
     *
     * @param sqlstatement the sqlstatement
     */
    public void setSqlStatement(String sqlstatement) {
        this.sqlStatement = sqlstatement;
        //设置returnValue
        int selectIndex = sqlstatement.indexOf("select ") + "select ".length();
        int fromIndex = sqlstatement.indexOf("from ");
        if(fromIndex < 0){
            fromIndex = sqlstatement.length();
        }
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


    @Override
    public String toString() {
        return "Sql{" +
                "name='" + name + '\'' +
                ", returnValues=" + Arrays.toString(returnValues) +
                ", sqlStatement='" + sqlStatement + '\'' +
                '}';
    }
}
