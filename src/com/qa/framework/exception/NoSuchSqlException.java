package com.qa.framework.exception;

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
