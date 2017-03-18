package com.qa.framework.cache;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class JsonPairCache {
    /**
     * The constant mapCache.
     */
    private static ThreadLocal<Map<String, String>> threadLocal = new ThreadLocal<Map<String, String>>();
    protected Logger logger = Logger.getLogger(JsonPairCache.class);


    public Map<String, String> getMap() {
        return threadLocal.get();
    }

    /**
     * Put.
     *
     * @param key   the key
     * @param value the value
     */
    public void put(String key, String value) {
        Map<String, String> mapCache = threadLocal.get();
        if(mapCache == null){
            mapCache = new HashMap<String, String>();
        }
        mapCache.put(key, value);
        threadLocal.set(mapCache);
    }

    /**
     * Gets value.
     *
     * @param key the key
     * @return the value
     */
    public String getValue(String key) {
        logger.info("取出的Key为" + key + " value为" + threadLocal.get().get(key));
        return threadLocal.get().get(key);
    }

}
