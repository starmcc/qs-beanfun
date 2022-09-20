package com.starmcc.beanfun.client.impl;

import com.starmcc.beanfun.client.HttpClient;
import com.starmcc.beanfun.model.ReqParams;
import com.starmcc.beanfun.model.client.QsHttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
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
    public QsHttpResponse get(String url) throws Exception {
        return this.get(url, new ReqParams());
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
            int i = 0;
            for (Map.Entry<String, String> entry : reqParams.getParams().entrySet()) {
                StringBuilder valParam = new StringBuilder();
                valParam = i == 0 ? valParam.append("?") : valParam.append("&");
                valParam.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8"));
                params.append(valParam.toString());
                i++;
            }
            log.info("请求URL = {}", params.toString());
            HttpGet httpGet = new HttpGet(params.toString());
            for (Map.Entry<String, String> entry : reqParams.getHeaders().entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
            return httpGet;
        });
    }

    @Override
    public QsHttpResponse post(String url) throws Exception {
        return this.post(url, new ReqParams());
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
        return request(() -> {
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            for (Map.Entry<String, String> entry : params.getHeaders().entrySet()) {
                post.setHeader(entry.getKey(), entry.getValue());
            }
            List<BasicNameValuePair> list = new ArrayList<>();
            for (Map.Entry<String, String> entry : params.getParams().entrySet()) {
                list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            post.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
            return post;
        });
    }

    @Override
    public File downloadFile(String url, String savePath) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Accept-Encoding", "identity"));
        httpClientBuilder.setDefaultHeaders(headers);
        httpClientBuilder.setRedirectStrategy(new LaxRedirectStrategy());
        httpClientBuilder.setDefaultCookieStore(COOKIE_STORE);
        httpClientBuilder.setUserAgent(USER_AGENT);
        httpClientBuilder.setProxy(this.getPacProxy());
        CloseableHttpClient httpClient = httpClientBuilder.build();
        FileOutputStream outputStream = null;
        try {
            HttpClientContext context = HttpClientContext.create();
            CloseableHttpResponse response = httpClient.execute(new HttpGet(url), context);
            byte[] bytes = EntityUtils.toByteArray(response.getEntity());
            outputStream = new FileOutputStream(savePath);
            outputStream.write(bytes);
            outputStream.flush();

        } catch (Exception e) {
            log.error("下载文件异常 e={}", e.getMessage(), e);
            return null;
        } finally {
            try {
                outputStream.close();
            } catch (Exception e) {
                log.error("关闭文件异常 e={}", e.getMessage(), e);
                return null;
            }
        }
        return new File(savePath);
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
        httpClientBuilder.setProxy(this.getPacProxy());
        CloseableHttpClient httpClient = httpClientBuilder.build();
        try {
            // 由客户端执行(发送)Get请求
            HttpUriRequest httpUriRequest = supplier.build();
            HttpClientContext context = HttpClientContext.create();
            CloseableHttpResponse response = httpClient.execute(httpUriRequest, context);
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
        }
        return qsHttpResponse.build();
    }


    @FunctionalInterface
    public static interface SupplierCustom {

        HttpUriRequest build() throws Exception;
    }

    /**
     * 获取pac代理
     *
     * @return {@link HttpHost}
     */
    private HttpHost getPacProxy() {
        return new HttpHost("127.0.0.1", 9000);
    }


}
