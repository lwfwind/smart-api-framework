package com.qa.framework.library.base;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.*;

public class JsonHelper {

    public static final int Error = 0;
    public static final int ObjectJson = 1;
    public static final int ArrayJson = 2;
    public static final int Number = 3;
    public static final int Str = 4;
    public static final int phpArray = 5;

    /**
     * 简单判断是否是json格式的字符串
     *
     * @param strJson
     * @return str 的类型
     */
    public static int JudgeStringJson(String strJson) {
        int flag = Error;
        String str = strJson.trim();
        if (str.startsWith("{") && str.contains(":") && str.endsWith("}")) {
            flag = ObjectJson;
            return flag;
        }

        if (str.startsWith("[{") && str.endsWith("}]")) {
            flag = ArrayJson;
            return flag;
        }

        if (str.startsWith("[") && str.endsWith("]")) {
            flag = phpArray;
            return flag;
        }

        if (str.matches("\\d*")) {
            flag = Number;
            return flag;
        } else
            return flag = Str;
    }

    /**
     * 简单判断是否是json格式的字符串
     *
     * @param str
     */
    public static boolean BooleanJudgeStringJson(String str) {
        return str.startsWith("{") && str.contains(":") && str.endsWith("}") || str.startsWith("[") && str.endsWith("]");
    }


    /**
     * Get json object string map. 将json转换成map
     *
     * @param jsonStr the json str
     * @return the map
     */
    public static Map<String, Object> getJsonMapString(String jsonStr) {
        Map<String, Object> map = new HashMap<String, Object>();
        JSONObject jsonObject = JSONObject.fromObject(jsonStr);
        Iterator it = jsonObject.keys();
        while (it.hasNext()) {
            String key = (String) it.next();
            Object value = getObject(jsonObject.get(key).toString());
            map.put(key, value);
        }
        return map;
    }

    public static Object getObject(String str) {
        int type = JudgeStringJson(str);
        switch (type) {
            case 1: //如果返回的格式符合 {}, 则返回一个map<String, Object>
                return getJsonMapString(str);
            case 2: //如果返回的格式符合[{}], 则返回一个List
                List<Map<Object, Object>> lists = new ArrayList<>();
                JSONArray jsonArray = JSONArray.fromObject(str);
                for (Object aJsonArray : jsonArray) {
                    Map<Object, Object> map1 = new HashMap<>();
                    JSONObject jsonObject1 = (JSONObject) aJsonArray;
                    Set set = jsonObject1.keySet();
                    for (Object key : set) {
                        Object value = getObject(jsonObject1.get(key).toString()); //如果数组中仍有嵌套,则继续递归
                        map1.put(key, value);
                    }
                    lists.add(map1);
                }
                return lists;
            default:
                return str;
        }
    }

    public static String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }

                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }
}
