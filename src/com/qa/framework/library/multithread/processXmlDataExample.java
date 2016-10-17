package com.qa.framework.library.multithread;

import com.qa.framework.core.TestXmlData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/19.
 */
public class processXmlDataExample {
    public static void main(String[] args) {
        List<Object[]> xmldata = new ArrayList<Object[]>();
        List<String> files = TestXmlData.getTestCaseFiles();
        pocessXmlDataThread workerThread=new pocessXmlDataThread();
        multiThreadHandle.buildThreadPool(workerThread, files.size());
        xmldata=workerThread.getXmlDate();
//
//        final ExecutorService executorService=Executors.newCachedThreadPool();
//        final List<Object[]> xmldata = new ArrayList<Object[]>();
//        List<Object[]> xmldata1 = new ArrayList<Object[]>();
//        for (final String filePath : files) {
//           Runnable e= new Runnable(){
//               String fileName;
//                @Override
//                public void run() {
//                    DataConvertor dataConvertor = new DataConvertor(filePath);
//                    DataConfig dataConfig = dataConvertor.getDataConfig();
//                    ParamValueProcessor paramValueProcessor = new ParamValueProcessor(dataConfig);
//                    paramValueProcessor.process();
//                    for (TestData data : dataConfig.getTestDataList()) {
//                        Object[] d = {data, dataConfig.getUrl(), dataConfig.getHttpMethod()};
//                        xmldata.add(d);
//                    }
//                }
//            };
//            multiThreadHandle.buildThreadPool(e);
//        }
        if (multiThreadHandle.isEnd()) {
            System.out.println(xmldata);
            System.out.println("+++++++++++++>结束了");
        }

    }
    }
