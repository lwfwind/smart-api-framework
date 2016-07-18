package com.qa.framework.exception;

/**
 * Created by apple on 15/11/23.
 */
public class TestDataDescDuplicatedException extends RuntimeException {
    /**
     * Instantiates a new No such param exception.
     *
     * @param name the name
     */
    public TestDataDescDuplicatedException(String fileName, String name) {
        super("The Setup desc-" + name + " have duplicated in file-" + fileName);
    }
}
