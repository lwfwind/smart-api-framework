package com.qa.framework.bean;

import com.qa.framework.library.base.StringHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 封装一个测试用例需要的一个数据
 * Created by apple on 15/11/18.
 */

public class Param {
    private String name;            //参数名字
    private String type;            //参数类型, 目前无用字段
    private String value;            //参数值
    private List<Sql> sqls;          //可能包含的sql
    private Map<String, Sql> sqlMap;    //建立sql名字和sql之间的映射
    private String multiple;        //执行sqls中的sql几次
    private DateStamp dateStamp;     //设置时间
    private List<Pair> pairs;     //Map
    private Function function;
    private boolean show = true;           //是否出现在链接中

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue(boolean useDecode) {
        if (useDecode) {
            return StringHelper.urlDecode(value);
        } else {
            return value;
        }
    }

    public String getValue() {
        return StringHelper.urlDecode(value);
    }

    public void setValue(String value) {
        this.value = StringHelper.urlEncode(value);
    }

    public List<Sql> getSqls() {
        return sqls;
    }

    public void setSqls(List<Sql> sqls) {
        this.sqls = sqls;
    }

    public void addSql(Sql sql) {
        if (sqls == null) {
            sqls = new ArrayList<Sql>();
        }
        sqls.add(sql);
    }

    public void fillParamMap() {
        if (sqls != null) {
            for (Sql sql : sqls) {
                if (sqlMap == null) {
                    sqlMap = new HashMap<String, Sql>();
                }
                sqlMap.put(sql.getName(), sql);
            }
        }
    }

    public String getMultiple() {
        return multiple;
    }

    public void setMultiple(String multipule) {
        this.multiple = multipule;
    }

    public DateStamp getDateStamp() {
        return dateStamp;
    }

    public void setDateStamp(DateStamp dateStamp) {
        this.dateStamp = dateStamp;
    }

    public List<Pair> getPairs() {
        return pairs;
    }

    public void setPair(List<Pair> pairs) {
        this.pairs = pairs;
    }

    public void addPair(Pair pair) {
        if (pairs == null) {
            pairs = new ArrayList<Pair>();
        }
        pairs.add(pair);
    }

    public Map<String, Sql> getSqlMap() {
        if (sqlMap == null) {
            fillParamMap();
        }
        return sqlMap;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = StringHelper.changeString2boolean(show);
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    @Override
    public String toString() {
        return name + "= " + value;
    }
}
