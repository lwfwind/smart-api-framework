package com.qa.framework.library.httpclient;


import org.apache.commons.io.input.BOMInputStream;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * Created by apple on 15/11/20.
 */
public class HttpConnectionImp {
    private final Logger logger = Logger
            .getLogger(this.getClass());
    private HttpRequestBase baseRequest;

    public HttpConnectionImp(HttpRequestBase baseRequest) {
        this.baseRequest = baseRequest;
    }

    public static String removeBOM(String responseBody) {
        BufferedReader reader = null;
        // convert String into InputStream
        InputStream is = new ByteArrayInputStream(responseBody.getBytes());

        // read it with BufferedReader
        reader = new BufferedReader(new InputStreamReader(new BOMInputStream(is)));

        String line = null;
        try {
            line = reader.readLine();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return line;

    }

    public String getResponseResult(boolean storeCookie, boolean useCookie) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpClientContext clientContext = HttpClientContext.create();
        if (useCookie) {
            CookieStore cookieStore = CookieCache.get();
            if (cookieStore == null) {
                throw new RuntimeException("cookieStore没有在缓存中");
            }
            clientContext.setCookieStore(cookieStore);
        }
        String responseBody = null;
        try {
            CloseableHttpResponse httpResponse;
            httpResponse = httpClient.execute(baseRequest, clientContext);
            //统一处理为utf-8
            int status;
            status = httpResponse.getStatusLine().getStatusCode();
            logger.info("------------" + status);

            if (status >= 200 && status < 300) {
                logger.info("expected response status");
            } else {
                logger.debug("unexpected response status");
                httpResponse = httpClient.execute(baseRequest, clientContext);
                //统一处理为utf-8
                status = httpResponse.getStatusLine().getStatusCode();
                logger.info("再一次连接------------" + status);
            }
            if (storeCookie) {
                CookieStore cookieStore = clientContext.getCookieStore();
                CookieCache.set(cookieStore);
                if (cookieStore == null) {
                    logger.info("cookieStore 是空的" + cookieStore);
                }
            }
            HttpEntity entity = httpResponse.getEntity();
            responseBody = entity != null ? EntityUtils.toString(entity) : null;
            if (responseBody != null) {
                responseBody = new String(responseBody.getBytes("UTF-8"), "UTF-8");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                //logger.error(e.getMessage());
            }
        }
        return removeBOM(responseBody);
    }

}
