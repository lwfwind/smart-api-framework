package com.qa.framework.exception;


public class TestCaseNameDuplicatedException extends RuntimeException {
    /**
     * Instantiates a new No such param exception.
     *
     * @param fileName the file name
     * @param name     the name
     */
    public TestCaseNameDuplicatedException(String fileName, String name) {
        super("The Setup name-" + name + " have duplicated in file-" + fileName);
    }
}
