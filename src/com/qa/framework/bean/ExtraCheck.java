package com.qa.framework.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装自定义的检查方法
 * 需要在用例里面实现
 * Created by apple on 15/11/18.
 */
public class ExtraCheck {
    private List<Function> functionList;

    public List<Function> getFunctionList() {
        return functionList;
    }

    public void setFunctionList(List<Function> functionList) {
        this.functionList = functionList;
    }

    public void addFunction(Function function) {
        if (functionList == null) {
            functionList = new ArrayList<Function>();
        }
        functionList.add(function);
    }
}
