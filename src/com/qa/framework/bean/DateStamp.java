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

    /**
     * Gets base time.
     *
     * @return the base time
     */
    public String getBaseTime() {
        return baseTime;
    }

    /**
     * Sets basetime.
     *
     * @param basetime the basetime
     */
    public void setBasetime(String basetime) {
        this.baseTime = basetime;
    }

    /**
     * Gets add year.
     *
     * @return the add year
     */
    public String getAddYear() {
        return addYear;
    }

    /**
     * Sets add year.
     *
     * @param addYear the add year
     */
    public void setAddYear(String addYear) {
        this.addYear = addYear;
    }

    /**
     * Gets add month.
     *
     * @return the add month
     */
    public String getAddMonth() {
        return addMonth;
    }

    /**
     * Sets add month.
     *
     * @param addMonth the add month
     */
    public void setAddMonth(String addMonth) {
        this.addMonth = addMonth;
    }

    /**
     * Gets add day.
     *
     * @return the add day
     */
    public String getAddDay() {
        return addDay;
    }

    /**
     * Sets add day.
     *
     * @param addDay the add day
     */
    public void setAddDay(String addDay) {
        this.addDay = addDay;
    }

    /**
     * Gets add hour.
     *
     * @return the add hour
     */
    public String getAddHour() {
        return addHour;
    }

    /**
     * Sets add hour.
     *
     * @param addHour the add hour
     */
    public void setAddHour(String addHour) {
        this.addHour = addHour;
    }

    /**
     * Gets add min.
     *
     * @return the add min
     */
    public String getAddMin() {
        return addMin;
    }

    /**
     * Sets add min.
     *
     * @param addMin the add min
     */
    public void setAddMin(String addMin) {
        this.addMin = addMin;
    }

    /**
     * Gets add second.
     *
     * @return the add second
     */
    public String getAddSecond() {
        return addSecond;
    }

    /**
     * Sets add second.
     *
     * @param addSecond the add second
     */
    public void setAddSecond(String addSecond) {
        this.addSecond = addSecond;
    }

    /**
     * Gets set year.
     *
     * @return the set year
     */
    public String getSetYear() {
        return setYear;
    }

    /**
     * Sets set year.
     *
     * @param setYear the set year
     */
    public void setSetYear(String setYear) {
        this.setYear = setYear;
    }

    /**
     * Gets set month.
     *
     * @return the set month
     */
    public String getSetMonth() {
        return setMonth;
    }

    /**
     * Sets set month.
     *
     * @param setMonth the set month
     */
    public void setSetMonth(String setMonth) {
        this.setMonth = setMonth;
    }

    /**
     * Gets set day.
     *
     * @return the set day
     */
    public String getSetDay() {
        return setDay;
    }

    /**
     * Sets set day.
     *
     * @param setDay the set day
     */
    public void setSetDay(String setDay) {
        this.setDay = setDay;
    }

    /**
     * Gets set hour.
     *
     * @return the set hour
     */
    public String getSetHour() {
        return setHour;
    }

    /**
     * Sets set hour.
     *
     * @param setHour the set hour
     */
    public void setSetHour(String setHour) {
        this.setHour = setHour;
    }

    /**
     * Gets set min.
     *
     * @return the set min
     */
    public String getSetMin() {
        return setMin;
    }

    /**
     * Sets set min.
     *
     * @param setMin the set min
     */
    public void setSetMin(String setMin) {
        this.setMin = setMin;
    }

    /**
     * Gets set second.
     *
     * @return the set second
     */
    public String getSetSecond() {
        return setSecond;
    }

    /**
     * Sets set second.
     *
     * @param setSecond the set second
     */
    public void setSetSecond(String setSecond) {
        this.setSecond = setSecond;
    }

    /**
     * Gets date format.
     *
     * @return the date format
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * Sets date format.
     *
     * @param dateFormat the date format
     */
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
