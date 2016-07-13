package com.qa.framework.bean;

import com.qa.framework.verify.IExpectResult;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 15/11/20.
 */
public class ExpectResult {
    protected static final Logger logger = Logger.getLogger(ExpectResult.class);
    private List<IExpectResult> expectResultImp;

    public void addExpectResultImp(IExpectResult iExpectResult) {
        if (expectResultImp == null) {
            expectResultImp = new ArrayList<IExpectResult>();
        }
        expectResultImp.add(iExpectResult);
    }

    public List<IExpectResult> getExpectResultImp() {
        return expectResultImp;
    }

    public void setExpectResultImp(List<IExpectResult> expectResultImp) {
        this.expectResultImp = expectResultImp;
    }


}
