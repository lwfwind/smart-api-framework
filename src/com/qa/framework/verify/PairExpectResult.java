package com.qa.framework.verify;

import com.library.common.JsonHelper;
import com.qa.framework.bean.Pair;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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


    @SuppressWarnings("unchecked")
    public void compareReal(String content) {
        Map<String, String> maps = JsonHelper.parseJsonToPairs(content);
        for (Pair pair : pairs) {
            String expectCode = pair.findValue(pair.getKey());
            String actualData = maps.get(pair.getKey());
            if (pair.getPatternMatch()) {
                logger.debug("需验证的正则表达式：" + pair.getValue());
                Pattern pattern = Pattern.compile(pair.getValue());
                Matcher matcher = pattern.matcher(actualData);
                Assert.assertTrue(matcher.matches(), String.format("期望返回:%s, 实际返回:%s", expectCode, actualData));
                logger.info(matcher.matches() + "====" + expectCode.equals(actualData));
            } else {
                Assert.assertTrue(expectCode.trim().equals(actualData.trim()), String.format("期望返回:%s, 实际返回:%s", expectCode, actualData));
            }
        }
    }
}


