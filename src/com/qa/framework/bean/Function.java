package com.qa.framework.bean;

public class Function {
    private String name;
    private String clsName;
    private String methodName;
    private String value;
    private String arguments;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets cls name.
     *
     * @return the cls name
     */
    public String getClsName() {
        return clsName;
    }

    /**
     * Sets cls name.
     *
     * @param clsName the cls name
     */
    public void setClsName(String clsName) {
        this.clsName = clsName;
    }

    /**
     * Gets method name.
     *
     * @return the method name
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Sets method name.
     *
     * @param methodName the method name
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
