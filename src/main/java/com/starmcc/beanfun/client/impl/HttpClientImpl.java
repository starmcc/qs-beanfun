package com.starmcc.beanfun.client.impl;

import com.starmcc.beanfun.client.HttpClient;
import com.starmcc.beanfun.entity.client.QsHttpResponse;
import com.starmcc.beanfun.entity.client.ReqParams;
import com.starmcc.beanfun.manager.WindowManager;
import com.starmcc.beanfun.utils.SystemTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * http客户端实现
 *
 * @author starmcc
 * @date 2022/09/23
 */
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
            if (StringUtils.isBlank(uri.getHost())) {
                cookieMap.put(cookie.getName(), cookie.getValue());
            } else if (StringUtils.equals(uri.getHost(), cookie.getDomain())) {
                cookieMap.put(cookie.getName(), cookie.getValue());
            }
        }
        return cookieMap;
    }

    @Override
    public CookieStore getCookieStore() {
        return COOKIE_STORE;
    }


    @Override
    public QsHttpResponse get(String url) throws Exception {
        return get(url, null);
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
        final ReqParams finalParams = Objects.isNull(reqParams) ? new ReqParams() : reqParams;
        return request(() -> {
            HttpGet httpGet = null;
            if (finalParams.getParams().size() == 0) {
                httpGet = new HttpGet(url);
            } else {
                // 参数
                StringBuffer params = new StringBuffer(url.replace("?", ""));
                int i = 0;
                for (Map.Entry<String, String> entry : finalParams.getParams().entrySet()) {
                    StringBuffer valParam = new StringBuffer();
                    valParam = i == 0 ? valParam.append("?") : valParam.append("&");
                    valParam.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8"));
                    params.append(valParam.toString());
                    i++;
                }
                httpGet = new HttpGet(params.toString());
            }
            for (Map.Entry<String, String> entry : finalParams.getHeaders().entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
            return httpGet;
        });
    }

    @Override
    public QsHttpResponse post(String url) throws Exception {
        return this.post(url, null);
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
    public QsHttpResponse post(String url, ReqParams params) throws Exception {
        final ReqParams finalParams = Objects.isNull(params) ? new ReqParams() : params;
        return request(() -> {
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            for (Map.Entry<String, String> entry : finalParams.getHeaders().entrySet()) {
                post.setHeader(entry.getKey(), entry.getValue());
            }
            List<BasicNameValuePair> list = new ArrayList<>();
            for (Map.Entry<String, String> entry : finalParams.getParams().entrySet()) {
                list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            post.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
            return post;
        });
    }

    /**
     * 请求
     *
     * @param supplier 供应
     * @return {@link String}
     */
    private QsHttpResponse request(SupplierCustom supplier) throws Exception {
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
        HttpUriRequest httpUriRequest = null;
        try {
            httpUriRequest = supplier.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        QsHttpResponse qsHttpResponse = new QsHttpResponse();
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Accept-Encoding", "identity"));
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                return true;
            }
        }).build();
        HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        httpClientBuilder.setSSLSocketFactory(sslsf);
        httpClientBuilder.setDefaultHeaders(headers);
        httpClientBuilder.setRedirectStrategy(new LaxRedirectStrategy());
        httpClientBuilder.setDefaultCookieStore(COOKIE_STORE);
        httpClientBuilder.setUserAgent(USER_AGENT);
        httpClientBuilder.setDefaultRequestConfig(RequestConfig.custom()
                .setConnectionRequestTimeout(30 * 1000)
                .setConnectTimeout(30 * 1000)
                .setSocketTimeout(30 * 1000)
                .build()
        );
        // 代理设置
        HttpHost proxy = WindowManager.getInstance().getProxy(httpUriRequest.getURI());
        httpClientBuilder.setProxy(proxy);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        final long time = System.currentTimeMillis();
        try {
            // 由客户端执行(发送)请求
            httpClient = httpClientBuilder.build();
            HttpClientContext context = HttpClientContext.create();
            response = httpClient.execute(httpUriRequest, context);
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
            SystemTools.close(httpClient, response);
            log.info("\n request url={} time={}/s", httpUriRequest.getURI().toString(), (System.currentTimeMillis() - time) / 1000D);
        }
        return qsHttpResponse.build();
    }


    @FunctionalInterface
    public static interface SupplierCustom {
        /**
         * 构建
         *
         * @return {@link HttpUriRequest}
         * @throws Exception 异常
         */
        HttpUriRequest build() throws Exception;
    }

}
