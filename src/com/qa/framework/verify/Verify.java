package com.qa.framework.verify;


import com.qa.framework.bean.ExpectResults;
import com.qa.framework.bean.TestData;
import com.qa.framework.core.ParamValueProcessor;

public class Verify {
    public static void verifyResult(TestData testData, String response) {
        ParamValueProcessor.processExpectResultAfterExecute(testData, response);
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
}
