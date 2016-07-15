package com.qa.framework.cache;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by apple on 15/11/25.
 */
public class StringCache {
    /**
     * The constant mapCache.
     */
//private static ThreadLocal<Map<String, String>> threadLocal = new ThreadLocal<Map<String, String>>();
    public Map<String, String> mapCache = new Hashtable<String, String>();

    /**
     * Put.
     *
     * @param key   the key
     * @param value the value
     */
    public void put(String key, String value) {
        mapCache.put(key, value);
    }

    /**
     * Gets value.
     *
     * @param key the key
     * @return the value
     */
    public String getValue(String key) {
        return mapCache.get(key);
    }

}
