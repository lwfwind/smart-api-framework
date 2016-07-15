package com.qa.framework.exception;

/**
 * Created by apple on 15/11/23.
 */
public class NoSuchParamException extends RuntimeException {
    /**
     * Instantiates a new No such param exception.
     *
     * @param name the name
     */
    public NoSuchParamException(String name) {
        super("找不到" + name + "这个Param对象");
    }
}
