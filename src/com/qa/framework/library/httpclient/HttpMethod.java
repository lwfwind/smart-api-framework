package com.qa.framework.library.httpclient;


import com.qa.framework.bean.Header;
import com.qa.framework.bean.Headers;
import com.qa.framework.bean.Param;
import com.qa.framework.config.PropConfig;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Http method.
 */
public class HttpMethod {
    /**
     * The constant logger.
     */
    protected final static Logger logger = Logger.getLogger(HttpMethod.class);
    private static boolean useProxy = PropConfig.isUseProxy();
    private static String localhost = PropConfig.getLocalhost();
    private static Integer localport = Integer.valueOf(PropConfig.getLocalport());
    private static Integer timeout = Integer.valueOf(PropConfig.getTimeout());

    /**
     * Gets url.
     *
     * @param url    the url
     * @param params the params
     * @return the url
     */
    public static String getUrl(String url, List<Param> params) {
        StringBuilder webPath = new StringBuilder();
        if (!(url.startsWith("http://") || url.startsWith("HTTP://"))) {
            webPath.append(PropConfig.getWebPath());
        }
        if (url.endsWith("?")){
            webPath.append(url);
            if (params != null) {
                for (Param param : params) {
                    if (param.isShow()) {
                        webPath.append(param.getName()).append("=").append(param.getValue(false)).append("&");
                    }
                }
            }
            return webPath.toString();
        }else {
            if (url.contains("/")) {
                webPath.append(url);
            } else {
                webPath.append(url).append("/");
            }
            if (params != null) {
                for (Param param : params) {
                    if (param.isShow()) {
                        webPath.append(param.getName()).append("/").append(param.getValue(false)).append("/");
                    }
                }
            }
            if (webPath.substring(webPath.length() - 1).equals("/")) {
                return webPath.substring(0, webPath.length() - 1);
            }

            return webPath.toString();
        }

    }

    /**
     * Post url string.
     *
     * @param url the url
     * @return the string
     */
    public static String postUrl(String url) {
        if (url.startsWith("http://") || url.startsWith("HTTP://")) {
            return url;
        } else {
            return PropConfig.getWebPath() + url;
        }

    }

    /**
     * Use get method string.
     *
     * @param url         the url
     * @param params      the params
     * @param storeCookie the store cookie
     * @param useCookie   the use cookie
     * @return the string
     */
    public static String useGetMethod(String url, Headers headers, List<Param> params, boolean storeCookie, boolean useCookie) {
        String uri = getUrl(url, params);
        logger.info("拼接后的web地址为:" + uri);
        HttpGet get = new HttpGet(uri);
        if (useProxy) {
            HttpHost proxy = new HttpHost(localhost, localport, "http");
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).setProxy(proxy).build();
            get.setConfig(requestConfig);
        } else {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).build();
            get.setConfig(requestConfig);
        }
        if(headers != null && headers.getHeaderList() != null) {
            for (Header header : headers.getHeaderList()) {
                get.addHeader(header.getName(), header.getValue());
            }
        }
        if(headers != null && headers.getCookieList() != null) {
            HttpConnectionImp imp = new HttpConnectionImp(get, headers.getCookieList());
            return imp.getResponseResult(storeCookie, useCookie);
        }
        else {
            HttpConnectionImp imp = new HttpConnectionImp(get);
            return imp.getResponseResult(storeCookie, useCookie);
        }
    }

    /**
     * Use post method string.
     *
     * @param url         the url
     * @param params      the params
     * @param storeCookie the store cookie
     * @param useCookie   the use cookie
     * @return the string
     */
    public static String usePostMethod(String url, Headers headers, List<Param> params, boolean storeCookie, boolean useCookie) {
        String uri=postUrl(url);
        logger.info("拼接后的web地址为:" + uri);
        HttpPost httpPost = new HttpPost(uri);
        RequestConfig requestConfig = null;
        if (useProxy) {
            HttpHost proxy = new HttpHost(localhost, localport, "http");
            requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).setProxy(proxy).build();
        } else {
            requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).build();
        }
        httpPost.setConfig(requestConfig);
        if(headers != null && headers.getHeaderList() != null) {
            for (Header header : headers.getHeaderList()) {
                httpPost.addHeader(header.getName(), header.getValue());
            }
        }
        List<BasicNameValuePair> basicNameValuePairs = new ArrayList<BasicNameValuePair>();
        if (params != null) {
            for (Param param : params) {
                BasicNameValuePair basicNameValuePair = new BasicNameValuePair(param.getName(), param.getValue(true));
                basicNameValuePairs.add(basicNameValuePair);
            }
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(basicNameValuePairs, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(headers != null && headers.getCookieList() != null) {
            HttpConnectionImp imp = new HttpConnectionImp(httpPost, headers.getCookieList());
            return imp.getResponseResult(storeCookie, useCookie);
        }
        else {
            HttpConnectionImp imp = new HttpConnectionImp(httpPost);
            return imp.getResponseResult(storeCookie, useCookie);
        }
    }

    /**
     * Use put method string.
     *
     * @param url         the url
     * @param params      the params
     * @param storeCookie the store cookie
     * @param useCookie   the use cookie
     * @return the string
     */
    public static String usePutMethod(String url, Headers headers, List<Param> params, boolean storeCookie, boolean useCookie) {
        String uri = postUrl(url);
        logger.info("拼接后的web地址为:" + uri);
        HttpPut httpPut = new HttpPut(uri);
        RequestConfig requestConfig = null;
        if (useProxy) {
            HttpHost proxy = new HttpHost(localhost, localport, "http");
            requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).setProxy(proxy).build();
        } else {
            requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).build();
        }
        httpPut.setConfig(requestConfig);
        if(headers != null && headers.getHeaderList() != null) {
            for (Header header : headers.getHeaderList()) {
                httpPut.addHeader(header.getName(), header.getValue());
            }
        }
        List<BasicNameValuePair> basicNameValuePairs = new ArrayList<BasicNameValuePair>();
        if (params != null) {
            for (Param param : params) {
                BasicNameValuePair basicNameValuePair = new BasicNameValuePair(param.getName(), param.getValue(true));
                basicNameValuePairs.add(basicNameValuePair);
            }
        }
        try {
            httpPut.setEntity(new UrlEncodedFormEntity(basicNameValuePairs, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(headers != null && headers.getCookieList() != null) {
            HttpConnectionImp imp = new HttpConnectionImp(httpPut, headers.getCookieList());
            return imp.getResponseResult(storeCookie, useCookie);
        }
        else {
            HttpConnectionImp imp = new HttpConnectionImp(httpPut);
            return imp.getResponseResult(storeCookie, useCookie);
        }
    }

    public static String useDeleteMethod(String url, Headers headers, List<Param> params, boolean storeCookie, boolean useCookie) {
        String uri = getUrl(url, params);
        logger.info("拼接后的web地址为:" + uri);
        HttpDelete httpDelete = new HttpDelete(uri);
        if (useProxy) {
            HttpHost proxy = new HttpHost(localhost, localport, "http");
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).setProxy(proxy).build();
            httpDelete.setConfig(requestConfig);
        } else {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).build();
            httpDelete.setConfig(requestConfig);
        }
        if(headers != null && headers.getHeaderList() != null) {
            for (Header header : headers.getHeaderList()) {
                httpDelete.addHeader(header.getName(), header.getValue());
            }
        }
        if(headers != null && headers.getCookieList() != null) {
            HttpConnectionImp imp = new HttpConnectionImp(httpDelete, headers.getCookieList());
            return imp.getResponseResult(storeCookie, useCookie);
        }
        else {
            HttpConnectionImp imp = new HttpConnectionImp(httpDelete);
            return imp.getResponseResult(storeCookie, useCookie);
        }
    }
}
