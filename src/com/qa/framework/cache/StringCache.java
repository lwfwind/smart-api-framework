package com.qa.framework.cache;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by apple on 15/11/25.
 */
public class StringCache {
    //private static ThreadLocal<Map<String, String>> threadLocal = new ThreadLocal<Map<String, String>>();
    public Map<String, String> mapCache = new Hashtable<String, String>();

    public void put(String key, String value) {
        mapCache.put(key, value);
    }

    public String getValue(String key) {
        return mapCache.get(key);
    }

}
