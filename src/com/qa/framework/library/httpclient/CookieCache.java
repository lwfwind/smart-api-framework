package com.qa.framework.library.httpclient;

import org.apache.http.client.CookieStore;

/**
 * Created by apple on 15/11/9.
 */
public class CookieCache {
    private static ThreadLocal<CookieStore> cookieStoreThreadLocal = new ThreadLocal<CookieStore>();

    public static void set(CookieStore cookie) {
        cookieStoreThreadLocal.set(cookie);
    }

    public static CookieStore get() {
        return cookieStoreThreadLocal.get();
    }

    public static void clear() {
        set(null);
    }

}
