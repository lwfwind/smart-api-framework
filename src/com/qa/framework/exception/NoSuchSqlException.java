package com.qa.framework.exception;

/**
 * Created by apple on 15/11/23.
 */
public class NoSuchSqlException extends RuntimeException {
    /**
     * Instantiates a new No such sql exception.
     *
     * @param name the name
     */
    public NoSuchSqlException(String name) {
        super("找不到" + name + "这个Sql对象");
    }
}
