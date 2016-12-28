package com.qa.framework.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/28.
 */
public class Header {
    private List<Cookie> cookieList;

    public List<Cookie> getCookieList() {
        return cookieList;
    }

    public void setCookieList(List<Cookie> cookieList) {
        this.cookieList = cookieList;
    }
    public void addCookie(Cookie cookie) {
        if (cookieList == null) {
            cookieList = new ArrayList<Cookie>();
        }
        cookieList.add(cookie);
    }
}
