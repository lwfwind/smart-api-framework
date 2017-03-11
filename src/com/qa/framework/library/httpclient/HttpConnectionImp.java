package com.qa.framework.library.httpclient;


import com.qa.framework.bean.Cookie;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.List;

/**
 * The type Http connection imp.
 */
public class HttpConnectionImp {
    private final Logger logger = Logger
            .getLogger(this.getClass());
    private HttpRequestBase baseRequest;
    private List<Cookie> cookieList;

    /**
     * Instantiates a new Http connection imp.
     *
     * @param baseRequest the base request
     */
    public HttpConnectionImp(HttpRequestBase baseRequest) {
        this.baseRequest = baseRequest;
    }

    public HttpConnectionImp(HttpRequestBase baseRequest,List<Cookie> cookieList) {
        this.baseRequest = baseRequest;
        this.cookieList = cookieList;
    }
    /**
     * Remove bom string.
     *
     * @param responseBody the response body
     * @return the string
     */
    public static String removeBOM(String responseBody) {
        BufferedReader reader = null;
        // convert String into InputStream
        InputStream is = new ByteArrayInputStream(responseBody.getBytes());

        // read it with BufferedReader
        reader = new BufferedReader(new InputStreamReader(new BOMInputStream(is)));

        String line = null;
        String lines = "";
        try {
            while ((line = reader.readLine()) != null) {
                lines = lines + line;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;

    }

    /**
     * Gets response result.
     *
     * @param storeCookie the store cookie
     * @param useCookie   the use cookie
     * @return the response result
     */
    public String getResponseResult(boolean storeCookie, boolean useCookie) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpClientContext clientContext = HttpClientContext.create();
        if (useCookie) {
            CookieStore cookieStore = CookieCache.get();
            if (cookieStore == null) {
                throw new RuntimeException("cookieStore没有在缓存中");
            }
            clientContext.setCookieStore(cookieStore);
            logger.info("useCookie:" + cookieStore.toString());
        }
        if(cookieList != null){
            for (Cookie cookie : cookieList) {
                BasicClientCookie basicCookie = new BasicClientCookie(cookie.getName(), cookie.getValue());
                basicCookie.setDomain(cookie.getDomain());
                basicCookie.setPath(cookie.getPath());
                basicCookie.setExpiryDate(cookie.getExpiry());
                clientContext.getCookieStore().addCookie(basicCookie);
            }
        }
        String responseBody = null;
        try {
            CloseableHttpResponse httpResponse;
            httpResponse = httpClient.execute(baseRequest, clientContext);
            int status;
            status = httpResponse.getStatusLine().getStatusCode();
            logger.debug("response status:" + status);
            if (storeCookie) {
                CookieStore cookieStore = clientContext.getCookieStore();
                CookieCache.set(cookieStore);
                if (cookieStore == null) {
                    logger.debug("cookieStore 是空的");
                } else {
                    logger.debug("storeCookie:" + cookieStore.toString());
                }
            }
            HttpEntity entity = httpResponse.getEntity();
            responseBody = entity != null ? EntityUtils.toString(entity) : null;
            if (responseBody != null) {
                responseBody = new String(responseBody.getBytes("UTF-8"), "UTF-8");
            }
        } catch (IOException e) {
            logger.error(e);
            return null;
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        logger.debug("response result:" + responseBody);
        return removeBOM(responseBody);
    }


}
