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
    private Boolean patternMatch = false;

    public Pair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Pair() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setSort(String sortStr) {
        sort = StringHelper.changeString2boolean(sortStr);
    }

    public boolean isSort() {
        return sort;
    }

    public void setSort(boolean sort) {
        this.sort = sort;
    }

    public String findValue(String msg) {
        if (msg.equalsIgnoreCase(key)) {
            return getValue();
        }
        return null;
    }

    public String getMapStatement() {
        return mapStatement;
    }

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

    public Boolean getPatternMatch() {
        return patternMatch;
    }

    public void setPatternMatch(String patternMatch) {
        this.patternMatch = StringHelper.changeString2boolean(patternMatch);
    }

    public void setPatternMatch(Boolean patternMatch) {
        this.patternMatch = patternMatch;
    }

    @Override
    public String toString() {
        return key + ":=" + value;
    }

}
