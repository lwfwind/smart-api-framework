package com.qa.framework.bean;

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



    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        if (domain==null){
            domain= PropConfig.getWebPath().split("/")[2].trim();
        }
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        if (path==null){
            path="/";
        }
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public Date getExpiry() {
        if (expiry==null){
            expiry=getExpiryDate();
        }
        return expiry;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }
    private static Date getExpiryDate() {
        Calendar calendar=Calendar.getInstance();
        Date date=new Date();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR,1);
        date=calendar.getTime();
        return date;
    }
}
