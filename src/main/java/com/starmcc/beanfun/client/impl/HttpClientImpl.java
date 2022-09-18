package com.starmcc.beanfun.client.impl;

import com.starmcc.beanfun.client.HttpClient;
import com.starmcc.beanfun.model.ReqParams;
import com.starmcc.beanfun.model.client.QsHttpResponse;
import com.starmcc.beanfun.utils.DataTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.*;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;

@Slf4j
public class HttpClientImpl extends HttpClient {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";
    private static final CookieStore COOKIE_STORE = new BasicCookieStore();


    @Override
    public void setCookie(URI uri, Map<String, String> cookieMap) {
        if (Objects.isNull(uri) || Objects.isNull(cookieMap)) {
            return;
        }
        for (Map.Entry<String, String> entry : cookieMap.entrySet()) {
            BasicClientCookie basicClientCookie = new BasicClientCookie(entry.getKey(), entry.getValue());
            basicClientCookie.setDomain(uri.getHost());
            COOKIE_STORE.addCookie(basicClientCookie);
        }
    }

    @Override
    public Map<String, String> getCookie(URI uri) {
        if (Objects.isNull(uri)) {
            return new HashMap<>();
        }
        Map<String, String> cookieMap = new HashMap<>();
        List<Cookie> cookies = COOKIE_STORE.getCookies();
        for (Cookie cookie : cookies) {
            if (StringUtils.isNotBlank(uri.getHost())) {
                if (uri.getHost() == cookie.getDomain()) {
                    cookieMap.put(cookie.getName(), cookie.getValue());
                }
            } else {
                cookieMap.put(cookie.getName(), cookie.getValue());
            }
        }
        return cookieMap;
    }

    @Override
    public CookieStore getCookieStore() {
        return COOKIE_STORE;
    }


    /**
     * GET
     *
     * @param url       url
     * @param reqParams 要求参数
     * @return {@link String}
     */
    @Override
    public QsHttpResponse get(String url, ReqParams reqParams) throws Exception {
        return request(() -> {
            // 参数
            StringBuilder params = new StringBuilder(url.replace("?", ""));
            if (Objects.nonNull(reqParams) && DataTools.collectionIsNotEmpty(reqParams.getParams())) {
                for (int i = 0; i < reqParams.getParams().size(); i++) {
                    ReqParams.Param param = reqParams.getParams().get(i);
                    StringBuilder valParam = new StringBuilder();
                    valParam = i < 1 ? valParam.append("?") : valParam.append("&");
                    valParam.append(param.getKey()).append("=").append(URLEncoder.encode(param.getVal(), "utf-8"));
                    params.append(valParam.toString());
                }
            }
            log.info("请求URL = {}", params.toString());
            return new HttpGet(params.toString());
        });
    }

    /**
     * POST
     *
     * @param url    url
     * @param params 参数
     * @return {@link String}
     * @throws Exception 异常
     */
    @Override
    public QsHttpResponse post(String url, Map<String, String> params) throws Exception {
        return request(() -> {
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            ArrayList<BasicNameValuePair> list = new ArrayList<>();
            params.forEach((key, value) -> list.add(new BasicNameValuePair(key, value)));
            post.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
            return post;
        });
    }

    /**
     * 请求(不会自动重定向)
     *
     * @param supplier 供应
     * @return {@link String}
     */
    private QsHttpResponse request(SupplierCustom supplier) throws Exception {
        QsHttpResponse qsHttpResponse = new QsHttpResponse();
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Accept-Encoding", "identity"));
        httpClientBuilder.setDefaultHeaders(headers);
        httpClientBuilder.setRedirectStrategy(new LaxRedirectStrategy());
        httpClientBuilder.setDefaultCookieStore(COOKIE_STORE);
        httpClientBuilder.setUserAgent(USER_AGENT);
//        HttpHost httpHost = new HttpHost("127.0.0.1", 9000, "http");
//        httpClientBuilder.setProxy(httpHost);
        CloseableHttpClient httpClient = httpClientBuilder.build();
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Get请求
            HttpUriRequest httpUriRequest = supplier.build();
            HttpClientContext context = HttpClientContext.create();
            response = httpClient.execute(httpUriRequest, context);
            DefaultHttpClient httpclient = new DefaultHttpClient();
            // 从响应模型中获取响应实体
            qsHttpResponse.setRedirectLocations(context.getRedirectLocations());
            qsHttpResponse.setCode(response.getStatusLine().getStatusCode());
            HttpEntity responseEntity = response.getEntity();
            if (Objects.isNull(responseEntity)) {
                return qsHttpResponse.build();
            }
            qsHttpResponse.setCookieMap(this.getCookie(httpUriRequest.getURI()));
            qsHttpResponse.setContentLength(responseEntity.getContentLength());
            String content = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
            qsHttpResponse.setContent(StringEscapeUtils.unescapeHtml4(content));
        } catch (Exception e) {
            log.error("请求异常 e={}", e.getMessage(), e);
            qsHttpResponse.setCode(500);
            qsHttpResponse.setContent(e.getMessage());
        } finally {
            try {
                // 释放资源
                if (Objects.nonNull(response)) {
                    response.close();
                }
            } catch (Exception e) {
                log.error("工具关闭异常 e={}", e.getMessage(), e);
            }
        }
        return qsHttpResponse.build();
    }


    @FunctionalInterface
    public static interface SupplierCustom {

        HttpUriRequest build() throws Exception;
    }


}
