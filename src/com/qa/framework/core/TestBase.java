package com.qa.framework.core;

import com.qa.framework.bean.*;
import com.qa.framework.library.database.DBHelper;
import com.qa.framework.verify.AssertTrueExpectResult;
import com.qa.framework.verify.ContainExpectResult;
import com.qa.framework.verify.IExpectResult;
import com.qa.framework.verify.PairExpectResult;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 提供一个模板类
 * Created by apple on 15/11/19.
 */
public abstract class TestBase {
    /**
     * The constant logger.
     */
    protected static final Logger logger = Logger.getLogger(TestBase.class);

    /**
     * Verify result. 验证结果
     *
     * @param testData the test data
     * @param response  the response
     */
    public void verifyResult(TestData testData, String response) {
        ParamValueProcessor.processExpectResultAfterExecute(testData,response);
        ExpectResults expectResult = testData.getExpectResults();
        for (IExpectResult iExpectResult : expectResult.getExpectResults()) {
            if (iExpectResult instanceof ContainExpectResult) {
                ContainExpectResult containKeyExpectResult = (ContainExpectResult) iExpectResult;
                containKeyExpectResult.compareReal(response);
            } else if (iExpectResult instanceof PairExpectResult) {
                PairExpectResult mapExpectResult = (PairExpectResult) iExpectResult;
                mapExpectResult.compareReal(response);
            } else if (iExpectResult instanceof AssertTrueExpectResult) {
                AssertTrueExpectResult assertTrueExpectResult = (AssertTrueExpectResult) iExpectResult;
                assertTrueExpectResult.compareReal(response);
            } else {
                throw new IllegalArgumentException("没有匹配的期望结果集！");
            }
        }
    }

    /**
     * Process after.
     *
     * @param testData the test data
     */
    @SuppressWarnings("unchecked")
    public void processAfter(TestData testData) {
        if (testData.getAfter() != null) {
            try {
                logger.info("Process After in xml-" + testData.getCurrentFileName() + " TestData-" + testData.getName());
                After after = testData.getAfter();
                if (after.getSqls() != null) {
                    List<Sql> sqls = after.getSqls();
                    for (Sql sql : sqls) {
                        logger.info("需更新语句：" + sql.getSqlStatement());
                        DBHelper.executeUpdate(sql.getSqlStatement());
                    }
                } else if (after.getFunctions() != null) {
                    List<Function> functions = after.getFunctions();
                    for (Function function : functions) {
                        Class cls = Class.forName(function.getClsName());
                        Method method = cls.getDeclaredMethod(function.getMethodName());
                        Object object = cls.newInstance();
                        method.invoke(object);
                    }
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
