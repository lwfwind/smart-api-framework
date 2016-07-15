package com.qa.framework.bean;

import com.qa.framework.library.base.StringHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 封装一个测试用例的一组数据
 * Created by apple on 15/11/18.
 */
public class TestData {
    private String name;
    private String desc;
    private List<Setup> setupList;
    private Map<String, Setup> setupMap;
    private List<Param> params;  //包含的数据
    private ExtraCheck extraCheck;  //自定义的检查方法
    private int sendTime = 1;
    private boolean storeCookie;
    private boolean useCookie;
    private ExpectResult expectResult;
    private Before before;
    private After after;
    private String currentFileName;

    public String getCurrentFileName() {
        return currentFileName;
    }

    public void setCurrentFileName(String currentFileName) {
        this.currentFileName = currentFileName;
    }

    public List<Param> getParams() {
        return params;
    }

    public void setParams(List<Param> params) {
        this.params = params;
    }

    public ExtraCheck getExtraCheck() {
        return extraCheck;
    }

    public void setExtraCheck(ExtraCheck extraCheck) {
        this.extraCheck = extraCheck;
    }

    public boolean isStoreCookie() {
        return storeCookie;
    }

    public void setStoreCookie(boolean storeCookie) {
        this.storeCookie = storeCookie;
    }

    public void setStoreCookie(String storeCookie) {
        setStoreCookie(StringHelper.changeString2boolean(storeCookie));
    }

    public boolean isUseCookie() {
        return useCookie;
    }

    public void setUseCookie(String useCookie) {
        this.useCookie = StringHelper.changeString2boolean(useCookie);
    }

    public void setUseCookie(boolean useCookie) {
        this.useCookie = useCookie;
    }

    public void addSetup(Setup setup) {
        if (setupList == null) {
            setupList = new ArrayList<Setup>();
        }
        setupList.add(setup);

    }

    public void addParam(Param param) {
        if (params == null) {
            params = new ArrayList<Param>();
        }
        params.add(param);
    }

    public void fillSetupMap() {
        if (setupList != null) {
            for (Setup setup : setupList) {
                if (setupMap == null) {
                    setupMap = new HashMap<String, Setup>();
                }
                setupMap.put(setup.getName(), setup);
            }
        }
    }

    public Map<String, Setup> getSetupMap() {
        if (setupMap == null) {
            fillSetupMap();
        }
        return setupMap;
    }

    public void setSetupMap(Map<String, Setup> setupMap) {
        this.setupMap = setupMap;
    }

    public ExpectResult getExpectResult() {
        return expectResult;
    }

    public void setExpectResult(ExpectResult expectResult) {
        this.expectResult = expectResult;
    }

    public List<Setup> getSetupList() {
        return setupList;
    }

    public void setSetupList(List<Setup> setupList) {
        this.setupList = setupList;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = Integer.valueOf(sendTime);
    }

    @Override
    public String toString() {
        return "file=" + currentFileName + ", name=" + getName() + ", descripton=" + desc +
                ", params=" + params;
    }

    public Before getBefore() {
        return before;
    }

    public void setBefore(Before before) {
        this.before = before;
    }

    public After getAfter() {
        return after;
    }

    public void setAfter(After after) {
        this.after = after;
    }
}
