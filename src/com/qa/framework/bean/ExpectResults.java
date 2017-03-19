package com.qa.framework.bean;

import com.qa.framework.verify.IExpectResult;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ExpectResults {

    /**
     * The constant logger.
     */
    protected static final Logger logger = Logger.getLogger(ExpectResults.class);
    private List<IExpectResult> expectResults;
    private List<Function> functionList;
    private List<Sql> sqls;
    private Map<String, Sql> stringSqlMap;

    public List<Function> getFunctionList() {
        return functionList;
    }

    public void addFunction(Function function) {
        if (functionList == null) {
            functionList = new ArrayList<Function>();
        }
        functionList.add(function);
    }

    /**
     * Add expect result
     *
     * @param iExpectResult the expect result
     */
    public void addExpectResult(IExpectResult iExpectResult) {
        if (expectResults == null) {
            expectResults = new ArrayList<IExpectResult>();
        }
        expectResults.add(iExpectResult);
    }

    /**
     * Gets expect result.
     *
     * @return the expect results
     */
    public List<IExpectResult> getExpectResults() {
        return expectResults;
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
     * Gets string sql map.
     *
     * @return the string sql map
     */
    public Map<String, Sql> getStringSqlMap() {
        if (stringSqlMap == null) {
            fillMap();
        }
        return stringSqlMap;
    }

    /**
     * Sets string sql map.
     *
     * @param stringSqlMap the string sql map
     */
    public void setStringSqlMap(Map<String, Sql> stringSqlMap) {
        this.stringSqlMap = stringSqlMap;
    }

    /**
     * Fill map.
     */
    public void fillMap() {
        for (Sql sql : sqls) {
            if (stringSqlMap == null) {
                stringSqlMap = new HashMap<String, Sql>();
            }
            stringSqlMap.put(sql.getName(), sql);
        }
    }

}
