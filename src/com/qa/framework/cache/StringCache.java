package com.qa.framework.cache;

import org.apache.log4j.Logger;

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
    protected Logger logger = Logger.getLogger(StringCache.class);

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
        logger.info("取出的Key为" + key + " value为" + mapCache.get(key));
        return mapCache.get(key);
    }

}
