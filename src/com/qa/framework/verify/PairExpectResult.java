package com.qa.framework.verify;

import com.qa.framework.bean.Pair;
import com.qa.framework.library.base.JsonHelper;
import com.qa.framework.library.base.StringHelper;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.util.*;
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
        Map<String, String> resultMap = new HashMap<String, String>();
        Map<String, Object> jsonObject = JsonHelper.getJsonMapString(content);
        if (jsonObject.size() > 0) {
            Set<String> Set = jsonObject.keySet();
            for (String key : Set) {
                Object object = jsonObject.get(key);
                if (object instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) object;
                    for (String subKey : map.keySet()) {
                        resultMap.put(subKey, map.get(subKey).toString());
                    }
                } else if (object instanceof List) {
                    List<Map<String, Object>> listMap = (List<Map<String, Object>>) object;
                    for (Map<String, Object> map : listMap) {
                        for (String subKey : map.keySet()) {
                            resultMap.put(subKey, map.get(subKey).toString());
                        }
                    }
                } else {
                    resultMap.put(key, object.toString());
                }
            }
        }
        for (Pair pair : pairs) {
            String actualVal = resultMap.get(pair.getKey());
            Assert.assertTrue(Pattern.matches(pair.getValue(), actualVal), String.format("期望返回:%s, 实际返回:%s", pairsStatement, content));
        }
    }
}

