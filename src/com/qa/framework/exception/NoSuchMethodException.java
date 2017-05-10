package com.qa.framework.exception;

public class NoSuchMethodException extends RuntimeException {

    public NoSuchMethodException(String className,String methodName,String parameters) {
        super("匹配不到function --- className:" + className + " methodName:"+methodName+" parameters:"+parameters);
    }
}
