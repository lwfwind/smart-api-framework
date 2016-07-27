package com.qa.framework.verify;

import com.qa.framework.bean.Sql;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by apple on 15/11/20.
 */
public class ContainExpectResult implements IExpectResult {
    /**
     * The constant logger.
     */
    protected static final Logger logger = Logger.getLogger(ContainExpectResult.class);
    private String textStatement;
    private List<Sql> sqls;
    private String patten;
    private Map<String, Sql> stringSqlMap;


    /**
     * Gets key statement.
     *
     * @return the key statement
     */
    public String getTextStatement() {
        return textStatement;
    }

    /**
     * Sets key statement.
     *
     * @param textStatement the key statement
     */
    public void setTextStatement(String textStatement) {
        this.textStatement = textStatement;
    }

    @SuppressWarnings("unchecked")
    public void compareReal(String content) {
        Assert.assertTrue(Pattern.matches(this.textStatement, content), String.format("实际返回:%s, 期望返回:%s", content, this.textStatement));
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

    /**
     * Gets patten.
     *
     * @return the patten
     */
    public String getPatten() {
        return patten;
    }

    /**
     * Sets patten.
     *
     * @param patten the patten
     */
    public void setPatten(String patten) {
        this.patten = patten;
    }
}
