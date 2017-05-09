package com.qa.framework.bean;

import java.util.ArrayList;
import java.util.List;

public class Global {
    private List<Sql> sqlList;
    private List<Function> functionList;

    private Before before;
    private After after;

    public Global() {
    }

    public Before getBefore() {
        return before;
    }

    public void setBefore(Before before) {
        this.before = before;
    }

    public After getAfter() {
        return after;
    }

    public void setAfter(After after) {
        this.after = after;
    }

    public List<Sql> getSqlList() {
        return sqlList;
    }

    public List<Function> getFunctionList() {
        return functionList;
    }


    public void addSql(Sql sql) {
        if (sqlList == null) {
            sqlList = new ArrayList<Sql>();
        }
        sqlList.add(sql);
    }

    public void addFunction(Function function) {
        if (functionList == null) {
            functionList = new ArrayList<Function>();
        }
        functionList.add(function);
    }

}
