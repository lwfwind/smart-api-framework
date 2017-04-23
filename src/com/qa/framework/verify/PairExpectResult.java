package com.qa.framework.verify;

import com.library.common.JsonHelper;
import com.qa.framework.bean.Pair;
import org.apache.log4j.Logger;
import org.testng.Assert;

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
    private String pairStatement;
    private Pair pair;

    /**
     * Gets pairs.
     *
     * @return the pairs
     */
    public Pair getPair() {
        return pair;
    }

    /**
     * Sets pairs.
     *
     * @param pair the pairs
     */
    public void setPair(Pair pair) {
        this.pair = pair;
    }


    /**
     * Sets pairs statement.
     *
     * @param pairStatement the pairs statement
     */
    public void setPairStatement(String pairStatement) {
        this.pairStatement = pairStatement;
        this.pair = new Pair();
        pair.setMapStatement(pairStatement);
    }


    @SuppressWarnings("unchecked")
    public void compareReal(String content) {
        Map<String, String> maps = JsonHelper.parseJsonToPairs(content);
        String expectValue = pair.getValue();
        String actualValue = maps.get(pair.getKey());
        Assert.assertNotNull(actualValue, String.format("the key \"%s\" in pair is not existed in pair json map %s", pair.getKey(), maps.toString()));
        if (pair.getPatternMatch()) {
            logger.debug("需验证的正则表达式：" + expectValue);
            Pattern pattern = Pattern.compile(expectValue);
            Matcher matcher = pattern.matcher(actualValue);
            Assert.assertTrue(matcher.matches(), String.format("期望返回:%s, 实际返回:%s", expectValue, actualValue));
        } else {
            Assert.assertTrue(expectValue.trim().equals(actualValue.trim()), String.format("期望返回:%s, 实际返回:%s", expectValue, actualValue));
        }

    }
}


