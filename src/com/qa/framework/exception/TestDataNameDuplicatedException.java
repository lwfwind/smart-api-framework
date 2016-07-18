package com.qa.framework.exception;

/**
 * Created by apple on 15/11/23.
 */
public class TestDataNameDuplicatedException extends RuntimeException {
    /**
     * Instantiates a new No such param exception.
     *
     * @param name the name
     */
    public TestDataNameDuplicatedException(String fileName,String name) {
        super("The Setup name-" + name + " have duplicated in file-"+fileName);
    }
}
