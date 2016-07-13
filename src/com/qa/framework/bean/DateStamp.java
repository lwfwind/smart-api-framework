package com.qa.framework.bean;

/**
 * Created by apple on 15/11/19.
 */
public class DateStamp {
    private String baseTime;        //格式必须是yyyy-MM-dd hh:mm:ss
    private String addYear;         //增加年
    private String addMonth;        //增加月
    private String addDay;          //增加天数
    private String addHour;         //增加小时
    private String addMin;          //增加分钟
    private String addSecond;       //增加秒数
    private String setYear;         //设置年
    private String setMonth;        //设置月
    private String setDay;          //设置天
    private String setHour;         //设置小时
    private String setMin;          //设置分钟
    private String setSecond;       //设置秒数
    private String dateFormat;      //设置返回格式

    public String getBaseTime() {
        return baseTime;
    }

    public void setBasetime(String basetime) {
        this.baseTime = basetime;
    }

    public String getAddYear() {
        return addYear;
    }

    public void setAddYear(String addYear) {
        this.addYear = addYear;
    }

    public String getAddMonth() {
        return addMonth;
    }

    public void setAddMonth(String addMonth) {
        this.addMonth = addMonth;
    }

    public String getAddDay() {
        return addDay;
    }

    public void setAddDay(String addDay) {
        this.addDay = addDay;
    }

    public String getAddHour() {
        return addHour;
    }

    public void setAddHour(String addHour) {
        this.addHour = addHour;
    }

    public String getAddMin() {
        return addMin;
    }

    public void setAddMin(String addMin) {
        this.addMin = addMin;
    }

    public String getAddSecond() {
        return addSecond;
    }

    public void setAddSecond(String addSecond) {
        this.addSecond = addSecond;
    }

    public String getSetYear() {
        return setYear;
    }

    public void setSetYear(String setYear) {
        this.setYear = setYear;
    }

    public String getSetMonth() {
        return setMonth;
    }

    public void setSetMonth(String setMonth) {
        this.setMonth = setMonth;
    }

    public String getSetDay() {
        return setDay;
    }

    public void setSetDay(String setDay) {
        this.setDay = setDay;
    }

    public String getSetHour() {
        return setHour;
    }

    public void setSetHour(String setHour) {
        this.setHour = setHour;
    }

    public String getSetMin() {
        return setMin;
    }

    public void setSetMin(String setMin) {
        this.setMin = setMin;
    }

    public String getSetSecond() {
        return setSecond;
    }

    public void setSetSecond(String setSecond) {
        this.setSecond = setSecond;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public String toString() {
        return "DateStamp{" +
                "basetime='" + baseTime + '\'' +
                ", addYear='" + addYear + '\'' +
                ", addMonth='" + addMonth + '\'' +
                ", addDay='" + addDay + '\'' +
                ", addHour='" + addHour + '\'' +
                ", addMin='" + addMin + '\'' +
                ", addSecond='" + addSecond + '\'' +
                ", setYear='" + setYear + '\'' +
                ", setMonth='" + setMonth + '\'' +
                ", setDay='" + setDay + '\'' +
                ", setHour='" + setHour + '\'' +
                ", setMin='" + setMin + '\'' +
                ", setSecond='" + setSecond + '\'' +
                '}';
    }
}
