package com.qa.framework.verify;

/**
 * 预期结果类
 * Created by apple on 15/11/18.
 */
public interface IExpectResult {

    /**
     * Compare real.
     *
     * @param content the content
     */
    public void compareReal(String content);

}
