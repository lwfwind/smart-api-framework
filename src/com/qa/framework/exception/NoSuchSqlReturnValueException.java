package com.qa.framework.exception;

/**
 * Created by apple on 15/11/23.
 */
public class NoSuchSqlReturnValueException extends RuntimeException {
    public NoSuchSqlReturnValueException(String sqlName, String str) {
        super(sqlName + "这个sql中的returnValue中没有" + str + "这个值");
    }
}
