package com.qa.framework.exception;

public class TestCaseDescDuplicatedException extends RuntimeException {
    /**
     * Instantiates a new No such param exception.
     *
     * @param fileName the file name
     * @param name     the name
     */
    public TestCaseDescDuplicatedException(String fileName, String name) {
        super("The Setup desc-" + name + " have duplicated in file-" + fileName);
    }
}
