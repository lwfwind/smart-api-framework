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
    private List<Setup> setups;
    private Map<String, Setup> setupMap;
    private List<Param> params;  //包含的数据
    private Map<String, Param> paramMap;
    private ExtraCheck extraCheck;  //自定义的检查方法
    private int sendTime = 1;
    private boolean storeCookie;
    private boolean useCookie;
    private ExpectResult expectResult;
    private Before before;
    private After after;

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

    public void setStoreCookie(String storeCookie) {
        setStoreCookie(StringHelper.changeString2boolean(storeCookie));
    }

    public void setStoreCookie(boolean storeCookie) {
        this.storeCookie = storeCookie;
    }

    public boolean isUseCookie() {
        return useCookie;
    }

    public void setUseCookie(boolean useCookie) {
        this.useCookie = useCookie;
    }

    public void setUseCookie(String useCookie) {
        this.useCookie = StringHelper.changeString2boolean(useCookie);
    }

    public void addSetup(Setup setup) {
        if (setups == null) {
            setups = new ArrayList<Setup>();
        }
        setups.add(setup);

    }

    public void addParam(Param param) {
        if (params == null) {
            params = new ArrayList<Param>();
        }
        params.add(param);
    }

    public void fillParamMap() {
        if (params != null) {
            for (Param param : params) {
                if (paramMap == null) {
                    paramMap = new HashMap<String, Param>();
                }
                paramMap.put(param.getName(), param);
            }
        }
    }

    public void fillSetupMap() {
        if (setups != null) {
            for (Setup setup : setups) {
                if (setupMap == null) {
                    setupMap = new HashMap<String, Setup>();
                }
                setupMap.put(setup.getName(), setup);
            }
        }
    }

    public Map<String, Param> getParamMap() {
        if (paramMap == null) {
            fillParamMap();
        }
        return paramMap;
    }

    public void setParamMap(Map<String, Param> paramMap) {
        this.paramMap = paramMap;
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

    public List<Setup> getSetups() {
        return setups;
    }

    public void setSetups(List<Setup> setups) {
        this.setups = setups;
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
        return "descripton=" + desc +
                ", " + params + ", " + "name = " + getName();
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
