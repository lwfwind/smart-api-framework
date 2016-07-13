package com.qa.framework.verify;

import com.qa.framework.bean.Sql;
import com.qa.framework.library.base.JsonHelper;
import com.qa.framework.library.base.StringHelper;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.util.*;

/**
 * Created by apple on 15/11/20.
 */
public class ContainExpectResult implements IExpectResult {
    protected static final Logger logger = Logger.getLogger(ContainExpectResult.class);
    private String[] containKeys;
    private String[] notContainKeys;
    private boolean comparaKey = true;
    private String keyStatement;
    private String comKey = null;
    private String value;
    private String type;
    private List<Sql> sqls;
    private String patten;
    private Map<String, Sql> stringSqlMap;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isComparaKey() {
        return comparaKey;
    }

    public void setComparaKey(boolean comparaKey) {
        this.comparaKey = comparaKey;
    }

    public void setCompareKey(String compareKey) {
        this.comparaKey = StringHelper.changeString2boolean(compareKey);
    }

    public String[] getContainKeys() {
        return containKeys;
    }

    public void setContainKeys(String[] containKeys) {
        this.containKeys = containKeys;
    }

    public void setContainKeys(String containKeysString) {
        this.containKeys = StringHelper.getTokensArray(containKeysString, ",");

    }

    public String[] getNotContainKeys() {
        return notContainKeys;
    }

    public void setNotContainKeys(String[] notContainKeys) {
        this.notContainKeys = notContainKeys;
    }

    public void setNotContainKeys(String notContainKeysString) {
        this.notContainKeys = StringHelper.getTokensArray(notContainKeysString, ",");
    }

    public String getKeyStatement() {
        return keyStatement;
    }

    public void setKeyStatement(String keyStatement) {
        if (keyStatement != null && !"".equalsIgnoreCase(keyStatement)) {
            if (keyStatement.contains(":")) {
                String[] statements = keyStatement.split(":");
                if (statements.length == 1) {
                    setComKey(statements[0].trim());
                } else if (statements.length == 2) {
                    setComKey(statements[0].trim());
                    setContainKeys(statements[1].trim());
                } else {
                    setComKey(statements[0].trim());
                    String[] newStatementsValue = Arrays.copyOfRange(statements, 1, statements.length);
                    setContainKeys(StringHelper.arrayToString(newStatementsValue, ":"));
                }
            } else if (keyStatement.contains(",")) {
                setContainKeys(keyStatement);
            } else {
                throw new IllegalArgumentException("请重新设值,参照格式key:value或value,value");
            }

        }
    }

    @SuppressWarnings("unchecked")
    public void compareReal(String content) {
        Object object = JsonHelper.getObject(content);
        if (object instanceof Map) {
            Map<String, Object> contentMap = (Map<String, Object>) object;
            compareMap(contentMap, comparaKey);
        } else if (object instanceof List) {
            List<Map<String, Object>> contentList = (List<Map<String, Object>>) object;
            for (Map<String, Object> contentMap : contentList) {
                compareMap(contentMap, comparaKey);
            }
        } else if (object instanceof String) {
            if (containKeys != null) {
                for (String containKey : containKeys) {
                    Assert.assertTrue(content.contains(containKey), String.format("%s期望包含字段%s", content, containKey));
                }
            }
            if (notContainKeys != null) {
                for (int i = 0; i < notContainKeys.length; i++) {
                    Assert.assertTrue(content.contains(notContainKeys[i]), String.format("%s期望不应该包含字段%s", content, containKeys[i]));
                }
            }
        }
        content = content.split("\n")[0].trim();
        if ("number".equalsIgnoreCase(type)) {
            if (content.contains("\"")) {
                Assert.assertEquals(Integer.valueOf(value), Integer.valueOf(content.split("\"")[1]), String.format("实际返回:%s, 期望返回:%s", content, value));
            } else {
                Assert.assertEquals(Integer.valueOf(value), Integer.valueOf(content), String.format("实际返回:%s, 期望返回:%s", content, value));
            }
        } else if ("not null".equals(type)) {
            Assert.assertTrue(!"".equalsIgnoreCase(content), String.format("期望值不为空"));
        } else if ("not match".equalsIgnoreCase(type)) {
            Assert.assertFalse(content.matches(patten), "不符合规定的返回的格式");
        } else if ("match".equalsIgnoreCase(type)) {
            Assert.assertTrue(content.matches(patten), "不符合规定的返回的格式");
        } else if ("array".equalsIgnoreCase(type)) {
            String[] values = value.split(",");
            logger.info("期望返回：" + value.toString());
            for (String arrayValue : values) {
                Assert.assertTrue(content.contains(arrayValue.trim()), String.format("实际返回:%s, 没有包含%s", content, arrayValue));
            }
        } else if ("containKeys".equalsIgnoreCase(type)) {
            for (String containKey : containKeys) {
                Assert.assertTrue(content.contains(containKey), String.format("%s期望包含字段%s", content, containKey));
            }
        } else if ("notContainKeys".equalsIgnoreCase(type)) {
            for (int i = 0; i < notContainKeys.length; i++) {
                Assert.assertTrue(content.contains(notContainKeys[i]), String.format("%s期望不应该包含字段%s", content, containKeys[i]));
            }
        } else {
            Assert.assertEquals(value, content, String.format("实际返回:%s, 期望返回:%s", content, value));
        }
    }

    @SuppressWarnings("unchecked")
    public void comparereal(String content) {
        if (comKey != null) {
            compareRealInitResult(content);

        } else {
            compareReal(content);
        }
    }

    @SuppressWarnings("unchecked")
    public void compareRealInitResult(String content) {
        Object object = JsonHelper.getObject(content);
        if (object instanceof Map) {
            Map<String, Object> contentMap = (Map<String, Object>) object;
            Object keyContent = contentMap.get(comKey);
            if (keyContent instanceof Map) {
                Map<String, Object> keyContentMap = (Map<String, Object>) keyContent;
                compareMap(keyContentMap, comparaKey);
            } else if (keyContent instanceof List) {
                List<Map<String, Object>> keyContentList = (List<Map<String, Object>>) keyContent;
                for (Map<String, Object> kcontentMap : keyContentList) {
                    compareMap(kcontentMap, comparaKey);
                }
            } else if (keyContent instanceof String) {
                if (containKeys != null) {
                    for (String containKey : containKeys) {
                        Assert.assertTrue(content.contains(containKey), String.format("%s期望包含字段%s", content, containKey));
                    }
                }
                if (notContainKeys != null) {
                    for (int i = 0; i < notContainKeys.length; i++) {
                        Assert.assertTrue(content.contains(notContainKeys[i]), String.format("%s期望不应该包含字段%s", content, containKeys[i]));
                    }
                }
            }

        }
    }

    @SuppressWarnings("unchecked")
    public void compareMap(Map<String, Object> objectMap, boolean keyCompare) {
        if (keyCompare) {
            if (containKeys != null) {
                for (String containKey : containKeys) {
                    Assert.assertTrue(objectMap.containsKey(containKey), String.format("期望包含字段%s", containKey));
                }
            }
            if (notContainKeys != null) {
                for (String notContainKey : notContainKeys) {
                    Assert.assertFalse(objectMap.containsKey(notContainKey), String.format("期望不应该包含字段%s", notContainKey));
                }
            }
        } else {
            Set<String> objectMapSet = objectMap.keySet();
            for (String key : objectMapSet) {
                Object object = objectMap.get(key);
                if (object instanceof Map) {
                    Map<String, Object> contentMap = (Map<String, Object>) object;
                    compareMap(contentMap, true);   //进行递归比较的是, compareKey就需要设置成true
                } else if (object instanceof List) {
                    List<Map<String, Object>> contentList = (List<Map<String, Object>>) object;
                    for (Map<String, Object> contetMap : contentList) {
                        compareMap(contetMap, true);
                    }
                }

            }
        }
    }

    public String getComKey() {
        return comKey;
    }

    public void setComKey(String comKey) {
        this.comKey = comKey;
    }

    public void addSql(Sql sql) {
        if (sqls == null) {
            sqls = new ArrayList<Sql>();
        }
        sqls.add(sql);
    }

    public List<Sql> getSqls() {
        return sqls;
    }

    public void setSqls(List<Sql> sqls) {
        this.sqls = sqls;
    }

    public Map<String, Sql> getStringSqlMap() {
        if (stringSqlMap == null) {
            fillMap();
        }
        return stringSqlMap;
    }

    public void setStringSqlMap(Map<String, Sql> stringSqlMap) {
        this.stringSqlMap = stringSqlMap;
    }

    public void fillMap() {
        for (Sql sql : sqls) {
            if (stringSqlMap == null) {
                stringSqlMap = new HashMap<String, Sql>();
            }
            stringSqlMap.put(sql.getName(), sql);
        }
    }

    public String getPatten() {
        return patten;
    }

    public void setPatten(String patten) {
        this.patten = patten;
    }
}
