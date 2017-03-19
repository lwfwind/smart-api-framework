package com.qa.framework.verify;

import com.library.common.DynamicCompileHelper;
import com.library.common.StringHelper;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.util.regex.Pattern;

public class AssertTrueExpectResult implements IExpectResult {
    /**
     * The constant logger.
     */
    protected static final Logger logger = Logger.getLogger(AssertTrueExpectResult.class);
    private String textStatement = "";


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
        String ret = "false";
        try {
            ret = DynamicCompileHelper.eval(this.textStatement).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("eval:"+this.textStatement+" return "+ret);
        Assert.assertEquals(ret,"true");
    }
}
