package com.qa.framework.cache;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Json pair cache.
 */
public class JsonPairCache {
    /**
     * The Logger.
     */
    protected Logger logger = Logger.getLogger(JsonPairCache.class);
    /**
     * The constant mapCache.
     */
    private Map<String, String> pairMaps = new HashMap<String, String>();

    /**
     * Gets map.
     *
     * @return the map
     */
    public Map<String, String> getMap() {
        return pairMaps;
    }

    /**
     * Put.
     *
     * @param key   the key
     * @param value the value
     */
    public void put(String key, String value) {
        if (pairMaps == null) {
            pairMaps = new HashMap<String, String>();
        }
        pairMaps.put(key, value);
    }

    /**
     * Gets value.
     *
     * @param key the key
     * @return the value
     */
    public String getValue(String key) {
        logger.info("取出的Key为" + key + " value为" + pairMaps.get(key));
        return pairMaps.get(key);
    }

}
