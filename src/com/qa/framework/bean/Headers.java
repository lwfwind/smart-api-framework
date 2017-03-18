package com.qa.framework.bean;

import java.util.ArrayList;
import java.util.List;


/**
 * The type Headers.
 */
public class Headers {
    private List<Cookie> cookieList;

    private List<Header> headerList;

    /**
     * Gets header list.
     *
     * @return the header list
     */
    public List<Header> getHeaderList() {
        return headerList;
    }

    /**
     * Gets cookie list.
     *
     * @return the cookie list
     */
    public List<Cookie> getCookieList() {
        return cookieList;
    }

    /**
     * Sets cookie list.
     *
     * @param cookieList the cookie list
     */
    public void setCookieList(List<Cookie> cookieList) {
        this.cookieList = cookieList;
    }

    /**
     * Add cookie.
     *
     * @param cookie the cookie
     */
    public void addCookie(Cookie cookie) {
        if (cookieList == null) {
            cookieList = new ArrayList<Cookie>();
        }
        cookieList.add(cookie);
    }

    /**
     * Add header.
     *
     * @param header the header
     */
    public void addHeader(Header header) {
        if (headerList == null) {
            headerList = new ArrayList<Header>();
        }
        headerList.add(header);
    }
}
