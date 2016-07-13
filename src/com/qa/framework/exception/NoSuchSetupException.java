package com.qa.framework.exception;

/**
 * Created by apple on 15/11/22.
 */
public class NoSuchSetupException extends RuntimeException {

    public NoSuchSetupException(String name) {
        super("找不到" + name + "这个Setup对象");
    }
}
