package com.qa.framework.bean;

import com.qa.framework.library.base.StringHelper;

import java.util.Arrays;

/**
 * Created by apple on 15/11/20.
 */
public class Pair {
    private String key;
    private String value;
    private String mapStatement;
    private boolean sort;
    private static Boolean patternMatch=true;

    /**
     * Instantiates a new Pair.
     *
     * @param key   the key
     * @param value the value
     */
    public Pair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Instantiates a new Pair.
     */
    public Pair() {
    }

    public static Boolean getPatternMatch() {
        return patternMatch;
    }

    public static void setPatternMatch(Boolean patternMatch) {
        Pair.patternMatch = patternMatch;
    }
    public static void setPatternMatch(String patternMatch) {
        Pair.patternMatch = StringHelper.changeString2boolean(patternMatch);;
    }
    /**
     * Gets key.
     *
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets key.
     *
     * @param key the key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets value.
     *
     * @param value the value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Sets sort.
     *
     * @param sortStr the sort str
     */
    public void setSort(String sortStr) {
        sort = StringHelper.changeString2boolean(sortStr);
    }

    /**
     * Is sort boolean.
     *
     * @return the boolean
     */
    public boolean isSort() {
        return sort;
    }

    /**
     * Sets sort.
     *
     * @param sort the sort
     */
    public void setSort(boolean sort) {
        this.sort = sort;
    }

    /**
     * Find value string.
     *
     * @param msg the msg
     * @return the string
     */
    public String findValue(String msg) {
        if (msg.equalsIgnoreCase(key)) {
            return getValue();
        }
        return null;
    }

    /**
     * Gets map statement.
     *
     * @return the map statement
     */
    public String getMapStatement() {
        return mapStatement;
    }

    /**
     * Sets map statement.
     *
     * @param mapStatement the map statement
     */
    public void setMapStatement(String mapStatement) {
        if (mapStatement != null && !"".equalsIgnoreCase(mapStatement)) {
            if (!mapStatement.contains(":")) {
                throw new IllegalArgumentException("请重新设值,参照格式key:value");
            }
            String[] statements = mapStatement.split(":");
            if (statements.length == 1) {
                setKey(statements[0].trim());
                setValue("");
            } else if (statements.length == 2) {
                setKey(statements[0].trim());
                setValue(statements[1].trim());
            } else {
                setKey(statements[0].trim());
                String[] newStatementsValue = Arrays.copyOfRange(statements, 1, statements.length);
                setValue(StringHelper.arrayToString(newStatementsValue, ":"));
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Pair) {
            Pair pair = (Pair) obj;
            if (this.getValue().equalsIgnoreCase(pair.getValue()) && this.getKey().equalsIgnoreCase(pair.getKey())) {
                return true;
            }
        }
        return false;
    }





    @Override
    public String toString() {
        return key + ":=" + value;
    }

}
