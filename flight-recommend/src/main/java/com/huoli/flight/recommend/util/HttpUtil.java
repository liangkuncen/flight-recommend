package com.huoli.flight.recommend.util;

import java.io.IOException;
import java.nio.charset.Charset;


import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class HttpUtil {
	public static final Charset UTF8 = Charset.forName("UTF-8");

    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    
    public static PoolingHttpClientConnectionManager connectionManager;

    static {
        initHttpConnectionM();
    }

    private static void initHttpConnectionM() {
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(500);
        connectionManager.setDefaultMaxPerRoute(100); // 同一个路由最多100个链接，防止一个渠道把其他渠道堵死

    }
    public static final CloseableHttpClient httpClient = HttpClients.createMinimal(connectionManager);

	private static final int NO_CUSTOM_TIMEOUT = 0;
	
	public static RequestConfig COMMON_REQ_CONFIG = RequestConfig.custom()
            .setConnectionRequestTimeout(2000)
            .setSocketTimeout(2000)
            .setConnectTimeout(500)
            .build();
    
    public static String getStringResponseAndClose(CloseableHttpResponse response) throws IOException {
        try {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else {
                logger.error("http status:{},body:{}", status, EntityUtils.toString(response.getEntity()));
                if (status == 301 || status == 302)
                    logger.error("redirect url:{}", response.getFirstHeader("Location").getValue());
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        } finally {
            response.close();
        }
    }

    public static String getStringResponseAndClose(CloseableHttpResponse response, Charset code) throws IOException {
        try {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity, code) : null;
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        } finally {
            response.close();
        }
    }

    public static <T> T get(String requestUrl, Class<T> tClass, int timeout) throws IOException {
        return JsonUtil.objFJson(get(requestUrl, timeout), tClass);
    }


    private static RequestConfig getReqConfig(int timeout) {
        if (timeout == NO_CUSTOM_TIMEOUT)
            return COMMON_REQ_CONFIG;

        return RequestConfig.custom()
                .setConnectionRequestTimeout(2000)
                .setConnectTimeout(timeout)
                .setSocketTimeout(timeout).build();
    }

    public static String get(String url, int timeout) throws IOException {
        logger.info("get from url:{}", url);
        HttpGet get = new HttpGet(url);
        get.setConfig(getReqConfig(timeout));
        get.setHeader("Connection", "keep-alive");
        long begin = System.currentTimeMillis();
        CloseableHttpResponse response = httpClient.execute(get);
        String result = getStringResponseAndClose(response);
        long end = System.currentTimeMillis();
        logger.info("get string:{}, cost_time:{}", result, end - begin);
        return result;
    }
    
}
