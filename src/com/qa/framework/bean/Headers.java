package com.qa.framework.bean;

import java.util.ArrayList;
import java.util.List;


public class Headers {
    private List<Cookie> cookieList;

    private List<Header> headerList;

    public List<Header> getHeaderList() {
        return headerList;
    }

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

    public void addHeader(Header header) {
        if (headerList == null) {
            headerList = new ArrayList<Header>();
        }
        headerList.add(header);
    }
}
