package com.qa.framework.util;

/**
 * Created by kcgw001 on 2016/7/15.
 */
public class StringUtil {
    /**
     * Handle special char string.
     *
     * @param sql         the sql
     * @param replacedStr the replaced str
     * @return the string
     */
//处理sql/value中的#{}
    public static String handleSpecialChar(String sql, String[] replacedStr) {
        StringBuilder newSql = new StringBuilder();
        String[] sqlContent = sql.split("#\\{[a-zA-Z0-9._]*\\}");
        if (replacedStr.length>0&&replacedStr[0]!=null) {
            if (sqlContent.length == 0) {
                newSql.append(replacedStr[0]);
            } else {
                if (sqlContent.length > replacedStr.length) {
                    newSql.append(sqlContent[0]);
                    for (int i = 1; i < sqlContent.length; i++) {
                        newSql.append(replacedStr[i - 1]);
                        newSql.append(sqlContent[i]);
                    }
                } else {
                    for (int i = 0; i < sqlContent.length; i++) {
                        newSql.append(sqlContent[i]);
                        newSql.append(replacedStr[i]);
                    }
                }
            }
            return newSql.toString();
        }else {
            return sql;
        }
    }

}
