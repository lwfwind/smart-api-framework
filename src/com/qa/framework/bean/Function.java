package com.qa.framework.bean;

public class Function {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
    private String clsName;
    private String methodName;

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
