package com.qa.framework.library.multithread;

import com.library.common.IOHelper;
import com.qa.framework.bean.DataConfig;
import com.qa.framework.bean.TestData;
import com.qa.framework.core.DataConvertor;
import com.qa.framework.core.ParamValueProcessor;
import com.qa.framework.core.TestXmlData;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/13.
 */
public class pocessXmlDataThread implements Runnable {
    protected static final Logger logger = Logger.getLogger(pocessXmlDataThread.class);
    private List<String> files = TestXmlData.getTestCaseFiles();
    private List<Object[]> xmldata = new ArrayList<Object[]>();

    @Override
    public void run() {
        String filePath;
        String fileName;
        synchronized (files) {
            filePath = this.files.get(0);
            fileName = IOHelper.getName(filePath);
            this.files.remove(0);
        }
        logger.info("---->" + Thread.currentThread().getName() + " Start. Command = " + fileName);
        DataConvertor dataConvertor = new DataConvertor(filePath);
        DataConfig dataConfig = dataConvertor.getDataConfig();
        ParamValueProcessor paramValueProcessor = new ParamValueProcessor(dataConfig);
        paramValueProcessor.process();
        for (TestData data : dataConfig.getTestDataList()) {
            Object[] d = {data, dataConfig.getUrl(), dataConfig.getHttpMethod()};
            this.xmldata.add(d);
        }
        logger.info("---->" + Thread.currentThread().getName() + fileName + " End.");
    }

    public List<Object[]> getXmlDate() {
        return xmldata;
    }

}
