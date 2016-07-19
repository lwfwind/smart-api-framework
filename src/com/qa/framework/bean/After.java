package com.qa.framework.bean;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/22.
 */
public class After {
    private List<Function> functions;
    private List<Sql> sqls;


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
     * Gets functions.
     *
     * @return the functions
     */
    public List<Function> getFunctions() {
        return functions;
    }

    /**
     * Sets functions.
     *
     * @param functions the functions
     */
    public void setFunctions(List<Function> functions) {
        this.functions = functions;
    }

    /**
     * Add function.
     *
     * @param function the function
     */
    public void addFunction(Function function) {
        if (functions == null) {
            functions = new ArrayList<Function>();
        }
        functions.add(function);
    }

}
