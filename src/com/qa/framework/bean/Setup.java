package com.qa.framework.bean;


import com.library.common.StringHelper;

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
    private boolean addParam=false;
    private Headers headers;

    /**
     * Gets url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets url.
     *
     * @param url the url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets http method.
     *
     * @return the http method
     */
    public String getHttpMethod() {
        return httpMethod;
    }

    /**
     * Sets http method.
     *
     * @param httpMethod the http method
     */
    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
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
     * Fill param map.
     */
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
    public void setUseCookie(String useCookie) {
        this.useCookie = StringHelper.changeString2boolean(useCookie);
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
        this.storeCookie = StringHelper.changeString2boolean(storeCookie);
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
     * Gets param map.
     *
     * @return the param map
     */
    public Map<String, Param> getParamMap() {
        if (paramMap == null) {
            fillParamMap();
        }
        return paramMap;
    }

    /**
     * Sets param map.
     *
     * @param paramMap the param map
     */
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


    public boolean isAddParam() {
        return addParam;
    }
    public void setAddParam(String addParam){
        this.addParam= StringHelper.changeString2boolean(addParam);
    }
    public void setAddParam(boolean addParam) {
        this.addParam = addParam;
    }

    public Headers getHeaders() {
        return headers;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

}
