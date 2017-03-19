package com.qa.framework.verify;

import com.library.common.StringHelper;
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
    private String textStatement = "";
    private Boolean patternMatch = true;


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
    public void compareReal(String response) {
        if (patternMatch) {
            Assert.assertTrue(Pattern.matches("[\\s\\S]*" + this.textStatement + "[\\s\\S]*", response), String.format("实际返回:%s, 期望返回:%s", response, this.textStatement));
        } else {
            Assert.assertTrue(response.contains(this.textStatement), String.format("实际返回:%s, 期望包含:%s", response, this.textStatement));
        }
    }

    /**
     * Gets pattern match.
     *
     * @return the pattern match
     */
    public Boolean getPatternMatch() {
        return patternMatch;
    }

    /**
     * Sets pattern match.
     *
     * @param patten the patten
     */
    public void setPatternMatch(String patten) {
        patternMatch = StringHelper.changeString2boolean(patten);
    }

    /**
     * Sets pattern match.
     *
     * @param patten the patten
     */
    public void setPatternMatch(Boolean patten) {
        patternMatch = patten;
    }
}
