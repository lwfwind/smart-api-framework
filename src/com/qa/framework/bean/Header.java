package com.qa.framework.bean;


import com.library.common.StringHelper;

import java.util.Arrays;


public class Header {
    private String key;
    private String value;

    /**
     * Instantiates a new Pair.
     *
     * @param key   the key
     * @param value the value
     */
    public Header(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Instantiates a new Pair.
     */
    public Header() {
    }

    /**
     * Gets key.
     *
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets key.
     *
     * @param key the key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets value.
     *
     * @param value the value
     */
    public void setValue(String value) {
        this.value = value;
    }

}
