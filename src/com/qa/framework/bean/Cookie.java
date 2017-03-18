package com.qa.framework.bean;

import com.library.common.StringHelper;
import com.qa.framework.config.PropConfig;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2016/12/28.
 */
public class Cookie {
    private String name;
    private String value;
    private String domain;
    private String path;
    private Date expiry;

    private static Date getExpiryDate() {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, 1);
        date = calendar.getTime();
        return date;
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

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets domain.
     *
     * @return the domain
     */
    public String getDomain() {
        if (domain == null) {
            String webPath = PropConfig.getWebPath();
            if (StringHelper.startsWithIgnoreCase(webPath, "http://")) {
                if (webPath.substring(7).contains("/")) {
                    domain = StringHelper.getTokensList(webPath.substring(7), "/").get(0);
                } else {
                    domain = webPath.substring(7);
                }
            }
            if (domain.contains(":")) {
                domain = domain.split(":")[0].trim();
            }
        }
        return domain;
    }

    /**
     * Sets domain.
     *
     * @param domain the domain
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * Gets path.
     *
     * @return the path
     */
    public String getPath() {
        if (path == null) {
            path = "/";
        }
        return path;
    }

    /**
     * Sets path.
     *
     * @param path the path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Gets expiry.
     *
     * @return the expiry
     */
    public Date getExpiry() {
        if (expiry == null) {
            expiry = getExpiryDate();
        }
        return expiry;
    }

    /**
     * Sets expiry.
     *
     * @param expiry the expiry
     */
    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }
}
