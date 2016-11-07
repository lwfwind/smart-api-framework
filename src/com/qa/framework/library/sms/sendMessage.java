package com.qa.framework.library.sms;

import com.qa.framework.config.PropConfig;
import com.qa.framework.library.httpclient.HttpConnectionImp;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by Administrator on 2016/9/21.
 */
public class sendMessage {
    private static boolean useProxy = PropConfig.isUseProxy();

    public static String sendMsg(String mobile,String message) throws  IOException {
        HttpPost httpPost = new HttpPost("http://sdk2.entinfo.cn/webservice.asmx/SendSMS");
        RequestConfig requestConfig;
        int timeout=50000;
        if (useProxy) {
            HttpHost proxy = new HttpHost("127.0.0.1", 8888, "http");
            requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).setProxy(proxy).build();
        } else {
            requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).build();
        }
        httpPost.setConfig(requestConfig);
        Map<String,String> params=new LinkedHashMap<String,String>();
        params.put("sn",PropConfig.getSN());
        params.put("pwd",PropConfig.getSNPWD());
        params.put("mobile",mobile);
        params.put("content",message);
        Set<String> keys=params.keySet();
        List<BasicNameValuePair> basicNameValuePairs = new ArrayList<BasicNameValuePair>();
        for (String key : keys) {
            BasicNameValuePair basicNameValuePair = new BasicNameValuePair(key, params.get(key));
            basicNameValuePairs.add(basicNameValuePair);
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(basicNameValuePairs, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpConnectionImp imp = new HttpConnectionImp(httpPost);
        String result=imp.getResponseResult(false,false);
        if (result.contains("成功")){
            return mobile+"发送短信成功";
        }else{
            return mobile+"发送短信失败，错误代码为:"+result;
        }
    }
    public static String sendMsg(List<String> mobiles,String message) throws IOException {
        String afterSend="短信结果：\n";
        for(String mobile:mobiles){
            String reslut=sendMsg(mobile,message);
            if (reslut.contains("失败")){
                afterSend=afterSend+reslut+"\n";
            }
        }
        return afterSend;
    }
}
