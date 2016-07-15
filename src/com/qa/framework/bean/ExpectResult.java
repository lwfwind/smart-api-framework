package com.qa.framework.bean;

import com.qa.framework.verify.IExpectResult;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 15/11/20.
 */
public class ExpectResult {
    /**
     * The constant logger.
     */
    protected static final Logger logger = Logger.getLogger(ExpectResult.class);
    private List<IExpectResult> expectResultImp;

    /**
     * Add expect result imp.
     *
     * @param iExpectResult the expect result
     */
    public void addExpectResultImp(IExpectResult iExpectResult) {
        if (expectResultImp == null) {
            expectResultImp = new ArrayList<IExpectResult>();
        }
        expectResultImp.add(iExpectResult);
    }

    /**
     * Gets expect result imp.
     *
     * @return the expect result imp
     */
    public List<IExpectResult> getExpectResultImp() {
        return expectResultImp;
    }

    /**
     * Sets expect result imp.
     *
     * @param expectResultImp the expect result imp
     */
    public void setExpectResultImp(List<IExpectResult> expectResultImp) {
        this.expectResultImp = expectResultImp;
    }


}
