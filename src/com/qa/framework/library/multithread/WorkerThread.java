package com.qa.framework.library.multithread;

import com.qa.framework.bean.DataConfig;
import com.qa.framework.bean.TestData;
import com.qa.framework.core.DataConvertor;
import com.qa.framework.core.ParamValueProcessor;
import com.qa.framework.core.TestXmlData;
import com.qa.framework.library.base.IOHelper;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Administrator on 2016/9/13.
 */
public class WorkerThread implements Runnable {
    protected static final Logger logger = Logger.getLogger(WorkerThread.class);
    private List<String> files = TestXmlData.getTestCaseFiles();
    private  List<Object[]> xmldata = new ArrayList<Object[]>();

    @Override
    public void run() {
        String filePath;
        String fileName;
        synchronized (files) {
               filePath=this.files.get(0);
               fileName=IOHelper.getName(filePath);
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
        logger.info("---->"+Thread.currentThread().getName()+fileName+" End.");
    }
    public static List<Object[]> handleXml() throws InterruptedException{
        int maxPoolSize;
        //Get the ThreadFactory implementation to use
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        //creating the ThreadPoolExecutor
        List<String> files = TestXmlData.getTestCaseFiles();
        if (Math.abs(files.size()-20)<15){
            maxPoolSize=15;
        }else{
            maxPoolSize=Math.abs(files.size()-20);
        }
        ThreadPoolExecutor executorPool = new ThreadPoolExecutor(10, maxPoolSize, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(20),
                threadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
      /*  ThreadPoolExecutor executorPool = new ThreadPoolExecutor(2, 4, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(3),
                new ThreadPoolExecutor.CallerRunsPolicy());*/
        //start the monitoring thread
        MonitorThread monitor = new MonitorThread(executorPool, 1);
        Thread monitorThread = new Thread(monitor);
        monitorThread.start();
        //submit work to the thread pool
        WorkerThread workerThread=new WorkerThread();
        for(int i=0;i<files.size();i++){
            Thread t=new Thread(workerThread);
            executorPool.execute(t);
        }
        try{
            monitorThread.join();
        }catch (Exception e){
            e.printStackTrace();
        }
        return workerThread.getXmlDate();
    }
    public  List<Object[]> getXmlDate(){
        return xmldata;
    }

}
