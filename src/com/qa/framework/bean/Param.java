package com.qa.framework.bean;


import com.library.common.StringHelper;

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
    private boolean isHaveValue=false;
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
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets value.
     *
     * @param useDecode the use decode
     * @return the value
     */
    public String getValue(boolean useDecode) {
        if (useDecode) {
            return StringHelper.urlDecode(value);
        } else {
            return value;
        }
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public String getValue() {
        return StringHelper.urlDecode(value);
    }

    /**
     * Sets value.
     *
     * @param value the value
     */
    public void setValue(String value) {
        this.value = StringHelper.urlEncode(value);
        isHaveValue=true;
    }

    /**
     * Gets sqls.
     *
     * @return the sqls
     */
    public List<Sql> getSqls() {
        return sqls;
    }

    /**
     * Sets sqls.
     *
     * @param sqls the sqls
     */
    public void setSqls(List<Sql> sqls) {
        this.sqls = sqls;
    }

    /**
     * Add sql.
     *
     * @param sql the sql
     */
    public void addSql(Sql sql) {
        if (sqls == null) {
            sqls = new ArrayList<Sql>();
        }
        sqls.add(sql);
    }

    /**
     * Fill param map.
     */
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

    /**
     * Gets multiple.
     *
     * @return the multiple
     */
    public String getMultiple() {
        return multiple;
    }

    /**
     * Sets multiple.
     *
     * @param multipule the multipule
     */
    public void setMultiple(String multipule) {
        this.multiple = multipule;
    }

    /**
     * Gets date stamp.
     *
     * @return the date stamp
     */
    public DateStamp getDateStamp() {
        return dateStamp;
    }

    /**
     * Sets date stamp.
     *
     * @param dateStamp the date stamp
     */
    public void setDateStamp(DateStamp dateStamp) {
        this.dateStamp = dateStamp;
    }

    /**
     * Gets pairs.
     *
     * @return the pairs
     */
    public List<Pair> getPairs() {
        return pairs;
    }

    /**
     * Sets pair.
     *
     * @param pairs the pairs
     */
    public void setPair(List<Pair> pairs) {
        this.pairs = pairs;
    }

    /**
     * Add pair.
     *
     * @param pair the pair
     */
    public void addPair(Pair pair) {
        if (pairs == null) {
            pairs = new ArrayList<Pair>();
        }
        pairs.add(pair);
    }

    /**
     * Gets sql map.
     *
     * @return the sql map
     */
    public Map<String, Sql> getSqlMap() {
        if (sqlMap == null) {
            fillParamMap();
        }
        return sqlMap;
    }

    /**
     * Gets function.
     *
     * @return the function
     */
    public Function getFunction() {
        return function;
    }

    /**
     * Sets function.
     *
     * @param function the function
     */
    public void setFunction(Function function) {
        this.function = function;
    }

    /**
     * Is show boolean.
     *
     * @return the boolean
     */
    public boolean isShow() {
        return show;
    }

    /**
     * Sets show.
     *
     * @param show the show
     */
    public void setShow(String show) {
        this.show = StringHelper.changeString2boolean(show);
    }

    /**
     * Sets show.
     *
     * @param show the show
     */
    public void setShow(boolean show) {
        this.show = show;
    }

    @Override
    public String toString() {
        return name + "= " + value;
    }

    public boolean isHaveValue() {
        return isHaveValue;
    }
}
