package com.qa.framework.bean;

import com.qa.framework.library.base.StringHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by apple on 15/11/18.
 */
public class Setup {
    private Map<String, Param> paramMap;
    private List<Param> params; //可能需要的数据
    private boolean useCookie = false;      //设置cookie
    private boolean storeCookie = true;    //保存cookie
    private String name;
    private String url;
    private String httpMethod = "get";

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public List<Param> getParams() {
        return params;
    }

    public void setParams(List<Param> params) {
        this.params = params;
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

    public boolean isUseCookie() {
        return useCookie;
    }

    public void setUseCookie(String useCookie) {
        this.useCookie = StringHelper.changeString2boolean(useCookie);
    }

    public void setUseCookie(boolean useCookie) {
        this.useCookie = useCookie;
    }

    public boolean isStoreCookie() {
        return storeCookie;
    }

    public void setStoreCookie(String storeCookie) {
        this.storeCookie = StringHelper.changeString2boolean(storeCookie);
    }

    public void setStoreCookie(boolean storeCookie) {
        this.storeCookie = storeCookie;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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


    @Override
    public String toString() {
        return "Setup{" +
                "url='" + url + '\'' +
                ", httpMothed='" + httpMethod + '\'' +
                ", params=" + params +
                '}';
    }
}
