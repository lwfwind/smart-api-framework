package com.qa.framework.bean;


import com.library.common.StringHelper;

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
    private ExpectResults expectResults;
    private Before before;
    private After after;
    private String currentFileName;
    private Headers headers;
    private int invocationCount = 1;

    /**
     * Gets invocation count.
     *
     * @return the invocation count
     */
    public int getInvocationCount() {
        return invocationCount;
    }

    /**
     * Sets invocation count.
     *
     * @param invocationCount the invocation count
     */
    public void setInvocationCount(int invocationCount) {
        this.invocationCount = invocationCount;
    }

    /**
     * Gets current file name.
     *
     * @return the current file name
     */
    public String getCurrentFileName() {
        return currentFileName;
    }

    /**
     * Sets current file name.
     *
     * @param currentFileName the current file name
     */
    public void setCurrentFileName(String currentFileName) {
        this.currentFileName = currentFileName;
    }

    /**
     * Gets params.
     *
     * @return the params
     */
    public List<Param> getParams() {
        return params;
    }

    /**
     * Sets params.
     *
     * @param params the params
     */
    public void setParams(List<Param> params) {
        this.params = params;
    }

    /**
     * Gets extra check.
     *
     * @return the extra check
     */
    public ExtraCheck getExtraCheck() {
        return extraCheck;
    }

    /**
     * Sets extra check.
     *
     * @param extraCheck the extra check
     */
    public void setExtraCheck(ExtraCheck extraCheck) {
        this.extraCheck = extraCheck;
    }

    /**
     * Is store cookie boolean.
     *
     * @return the boolean
     */
    public boolean isStoreCookie() {
        return storeCookie;
    }

    /**
     * Sets store cookie.
     *
     * @param storeCookie the store cookie
     */
    public void setStoreCookie(String storeCookie) {
        setStoreCookie(StringHelper.changeString2boolean(storeCookie));
    }

    /**
     * Sets store cookie.
     *
     * @param storeCookie the store cookie
     */
    public void setStoreCookie(boolean storeCookie) {
        this.storeCookie = storeCookie;
    }

    /**
     * Is use cookie boolean.
     *
     * @return the boolean
     */
    public boolean isUseCookie() {
        return useCookie;
    }

    /**
     * Sets use cookie.
     *
     * @param useCookie the use cookie
     */
    public void setUseCookie(boolean useCookie) {
        this.useCookie = useCookie;
    }

    /**
     * Sets use cookie.
     *
     * @param useCookie the use cookie
     */
    public void setUseCookie(String useCookie) {
        this.useCookie = StringHelper.changeString2boolean(useCookie);
    }

    /**
     * Add setup.
     *
     * @param setup the setup
     */
    public void addSetup(Setup setup) {
        if (setupList == null) {
            setupList = new ArrayList<Setup>();
        }
        setupList.add(setup);

    }

    /**
     * Add param.
     *
     * @param param the param
     */
    public void addParam(Param param) {
        if (params == null) {
            params = new ArrayList<Param>();
        }
        params.add(param);
    }

    /**
     * Fill setup map.
     */
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

    /**
     * Gets setup map.
     *
     * @return the setup map
     */
    public Map<String, Setup> getSetupMap() {
        if (setupMap == null) {
            fillSetupMap();
        }
        return setupMap;
    }

    /**
     * Sets setup map.
     *
     * @param setupMap the setup map
     */
    public void setSetupMap(Map<String, Setup> setupMap) {
        this.setupMap = setupMap;
    }

    /**
     * Gets expect result.
     *
     * @return the expect result
     */
    public ExpectResults getExpectResults() {
        return expectResults;
    }

    /**
     * Sets expect result.
     *
     * @param expectResults the expect result
     */
    public void setExpectResults(ExpectResults expectResults) {
        this.expectResults = expectResults;
    }

    /**
     * Gets setup list.
     *
     * @return the setup list
     */
    public List<Setup> getSetupList() {
        return setupList;
    }

    /**
     * Sets setup list.
     *
     * @param setupList the setup list
     */
    public void setSetupList(List<Setup> setupList) {
        this.setupList = setupList;
    }

    /**
     * Gets desc.
     *
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets desc.
     *
     * @param desc the desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets send time.
     *
     * @return the send time
     */
    public int getSendTime() {
        return sendTime;
    }

    /**
     * Sets send time.
     *
     * @param sendTime the send time
     */
    public void setSendTime(String sendTime) {
        this.sendTime = Integer.valueOf(sendTime);
    }

    @Override
    public String toString() {
        return "file=" + currentFileName + ", name=" + getName() + ", descripton=" + desc +
                ", params=" + params;
    }

    /**
     * Gets before.
     *
     * @return the before
     */
    public Before getBefore() {
        return before;
    }

    /**
     * Sets before.
     *
     * @param before the before
     */
    public void setBefore(Before before) {
        this.before = before;
    }

    /**
     * Gets after.
     *
     * @return the after
     */
    public After getAfter() {
        return after;
    }

    /**
     * Sets after.
     *
     * @param after the after
     */
    public void setAfter(After after) {
        this.after = after;
    }

    /**
     * Gets headers.
     *
     * @return the headers
     */
    public Headers getHeaders() {
        return headers;
    }

    /**
     * Sets headers.
     *
     * @param headers the headers
     */
    public void setHeaders(Headers headers) {
        this.headers = headers;
    }
}
