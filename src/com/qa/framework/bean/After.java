package com.qa.framework.bean;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/22.
 */
public class After {
    private List<Function> functions;
    private List<Sql> sqls;


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

    public List<Function> getFunctions() {
        return functions;
    }

    public void setFunctions(List<Function> functions) {
        this.functions = functions;
    }
    public void addFunction(Function function) {
        if (functions == null) {
            functions = new ArrayList<Function>();
        }
        functions.add(function);
    }

}
