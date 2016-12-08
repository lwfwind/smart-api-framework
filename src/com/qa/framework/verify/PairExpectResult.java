package com.qa.framework.verify;

import com.library.common.JsonHelper;
import com.library.common.StringHelper;
import com.qa.framework.bean.Pair;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by apple on 15/11/20.
 */
public class PairExpectResult implements IExpectResult {
    /**
     * The constant logger.
     */
    protected static final Logger logger = Logger.getLogger(PairExpectResult.class);
    private String pairsStatement;
    private List<Pair> pairs;
    private String[] containKeys;

    /**
     * Gets pairs.
     *
     * @return the pairs
     */
    public List<Pair> getPairs() {
        return pairs;
    }

    /**
     * Sets pairs.
     *
     * @param pairs the pairs
     */
    public void setPairs(List<Pair> pairs) {
        this.pairs = pairs;
    }

    /**
     * Sets contain keys.
     *
     * @param containKeysString the contain keys string
     */
    public void setContainKeys(String containKeysString) {
        this.containKeys = StringHelper.getTokensArray(containKeysString, ",");

    }

    /**
     * Add pair.
     *
     * @param pair the pair
     */
    public void addPair(Pair pair) {
        if (pairs == null) {
            pairs = new ArrayList<Pair>();
        }
        pairs.add(pair);
    }

    /**
     * Sets pairs statement.
     *
     * @param pairsStatement the pairs statement
     */
    public void setPairsStatement(String pairsStatement) {
        this.pairsStatement = pairsStatement;
        Pair pair = new Pair();
        pair.setMapStatement(pairsStatement);
        addPair(pair);
    }

    /**
     * Sets contain keys.
     *
     * @param containKeys the contain keys
     */
    public void setContainKeys(String[] containKeys) {
        this.containKeys = containKeys;
    }

    @SuppressWarnings("unchecked")
    public void compareReal(String content) {
        Map<String, Object> jsonObject = JsonHelper.getJsonMapString(content);
        String expectCode = null;
        String code = null;
        for (Pair pair : pairs) {
            expectCode = pair.findValue(pair.getKey());
            Object data = jsonObject.get(pair.getKey());
            if (data instanceof String) {
                code = (String) data;
            } else if (data instanceof Map) {
                Map<String, Object> contentMap = (Map<String, Object>) data;
                compareMap(contentMap, expectCode);
                continue;
            } else if (data instanceof List) {
                List<Map<String, Object>> contentList = (List<Map<String, Object>>) data;
                for (Map<String, Object> contentMap : contentList) {
                    compareMap(contentMap, true);
                }
            }
            if (pair.getPatternMatch()) {
                logger.debug("需验证的正则表达式：" + pair.getValue());
                Pattern pattern = Pattern.compile(pair.getValue());
                Matcher matcher = pattern.matcher(code);
                Assert.assertTrue(matcher.matches(), String.format("期望返回:%s, 实际返回:%s", expectCode, code));
                logger.info(matcher.matches() + "====" + expectCode.equals(code));
            }else {
                Assert.assertTrue(expectCode.trim().equals(code.trim()), String.format("期望返回:%s, 实际返回:%s", expectCode, code));
            }

        }
    }

    /**
     * Compare map.
     *
     * @param objectMap the object map
     * @param expectMsg the expect msg
     */
    @SuppressWarnings("unchecked")
    public void compareMap(Map<String, Object> objectMap, String expectMsg) {
        Map<String, Object> expectMap = JsonHelper.getJsonMapString(expectMsg);
        Set<String> expectMapSet = expectMap.keySet();
        for (String key : expectMapSet) {
            Assert.assertTrue(objectMap.containsKey(key), String.format("期望包含：%s,实际结果未包含", key));
            Assert.assertEquals(objectMap.get(key), expectMap.get(key), String.format("实际返回:%s, 期望返回:%s" + key + "->, ", objectMap.get(key), expectMap.get(key)));
            Object object = objectMap.get(key);
            if (object instanceof Map) {
                Map<String, Object> contentMap = (Map<String, Object>) object;
                compareMap(contentMap, expectMap.get(key).toString());   //进行递归比较的是, compareKey就需要设置成true
            } else if (object instanceof List) {
                List<Map<String, Object>> contentList = (List<Map<String, Object>>) object;
                for (Map<String, Object> contetMap : contentList) {
                    compareMap(contetMap, true);
                }
            }
        }
    }

    /**
     * Compare map.
     *
     * @param objectMap  the object map
     * @param keyCompare the key compare
     */
    @SuppressWarnings("unchecked")
    public void compareMap(Map<String, Object> objectMap, boolean keyCompare) {
        if (keyCompare) {
            if (containKeys != null) {
                for (String containKey : containKeys) {
                    Assert.assertTrue(objectMap.containsKey(containKey), String.format("期望包含字段%s", containKey));
                }
            }
          /*  if (notContainKeys != null) {
                for (int i = 0; i < notContainKeys.length; i++) {
                    String notContainKey = notContainKeys[i];
                    Assert.assertFalse(objectMap.containsKey(notContainKey), String.format("期望不应该包含字段%s", notContainKey));
                }
            }*/
        } else {
            Set<String> objectMapSet = objectMap.keySet();
            for (String key : objectMapSet) {
                Object object = objectMap.get(key);
                if (object instanceof Map) {
                    Map<String, Object> contentMap = (Map<String, Object>) object;
                    compareMap(contentMap, true);

                } else if (object instanceof List) {
                    List<Map<String, Object>> contentList = (List<Map<String, Object>>) object;
                    for (Map<String, Object> contetMap : contentList) {
                        compareMap(contetMap, true);
                    }
                }
            }

        }
    }
}


