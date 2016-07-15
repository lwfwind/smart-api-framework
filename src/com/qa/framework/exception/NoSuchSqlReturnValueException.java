package com.qa.framework.exception;

/**
 * Created by apple on 15/11/23.
 */
public class NoSuchSqlReturnValueException extends RuntimeException {
    /**
     * Instantiates a new No such sql return value exception.
     *
     * @param sqlName the sql name
     * @param str     the str
     */
    public NoSuchSqlReturnValueException(String sqlName, String str) {
        super(sqlName + "这个sql中的returnValue中没有" + str + "这个值");
    }
}
