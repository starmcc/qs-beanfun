package com.starmcc.beanfun.client.impl;

import com.starmcc.beanfun.client.HttpClient;
import com.starmcc.beanfun.manager.WindowManager;
import com.starmcc.beanfun.model.client.QsHttpResponse;
import com.starmcc.beanfun.model.client.ReqParams;
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
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
            log.info("请求URL = {}", httpGet.getURI().toString());
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

    @Override
    public void downloadFile(URL url, File saveFile, HttpClient.Process process) {
        process.call(HttpClient.Process.State.准备开始, null, 0, null);
        InputStream inputStream = null;
        RandomAccessFile randomAccessFile = null;
        HttpURLConnection urlConnection = null;
        int responseCode = 0;
        int unitProgress = 0;

        try {
            process.call(HttpClient.Process.State.正在连接, null, unitProgress, null);
            HttpHost httpHost = WindowManager.getInstance().getPacScriptProxy(url.toString());
            if (Objects.nonNull(httpHost)) {
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpHost.getHostName(), httpHost.getPort()));
                urlConnection = (HttpURLConnection) url.openConnection(proxy);
            } else {
                urlConnection = (HttpURLConnection) url.openConnection();
            }
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10 * 1000);
            responseCode = urlConnection.getResponseCode();
        } catch (Exception e) {
            process.call(HttpClient.Process.State.连接超时, null, unitProgress, e);
            return;
        }

        try {
            boolean is = responseCode >= 200 && responseCode < 300;
            if (!is) {
                process.call(HttpClient.Process.State.请求状态码异常, null, unitProgress, null);
                return;
            }
            process.call(HttpClient.Process.State.创建文件, null, unitProgress, null);
            inputStream = urlConnection.getInputStream();
            int len = 0;
            byte[] data = new byte[4096];
            //用于保存当前进度（具体进度）
            int progres = 0;
            //获取文件
            int maxProgres = urlConnection.getContentLength();
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdir();
            }
            if (saveFile.exists()) {
                saveFile.delete();
            }
            saveFile.createNewFile();
            randomAccessFile = new RandomAccessFile(saveFile, "rwd");
            //设置文件大小
            randomAccessFile.setLength(maxProgres);
            //将文件大小分成100分，每一分的大小为unit
            int unit = maxProgres / 100;
            //用于保存当前进度(1~100%)
            while (-1 != (len = inputStream.read(data))) {
                randomAccessFile.write(data, 0, len);
                //保存当前具体进度
                progres += len;
                //计算当前百分比进度
                int temp = progres / unit;
                //如果下载过程出现百分比变化
                if (temp >= 1 && temp > unitProgress) {
                    //保存当前百分比
                    unitProgress = temp;
                    process.call(HttpClient.Process.State.下载中, null, unitProgress, null);
                }
            }
            inputStream.close();
            process.call(HttpClient.Process.State.下载完毕, saveFile, unitProgress, null);
        } catch (Exception e) {
            process.call(HttpClient.Process.State.未知异常, null, unitProgress, e);
        } finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
                if (null != randomAccessFile) {
                    randomAccessFile.close();
                }
            } catch (Exception e) {
                process.call(HttpClient.Process.State.未知异常, null, unitProgress, e);
            }
        }
    }

    /**
     * 请求(不会自动重定向)
     *
     * @param supplier 供应
     * @return {@link String}
     */
    private QsHttpResponse request(SupplierCustom supplier) throws Exception {
        QsHttpResponse qsHttpResponse = new QsHttpResponse();
        ConnectionKeepAliveStrategy keepAliveStrategy = (response, context) -> 10;
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Accept-Encoding", "identity"));
        httpClientBuilder.setDefaultHeaders(headers);
        //设置这两项，会开启定时任务清理过期和闲置的连接
        httpClientBuilder.evictExpiredConnections();
        //只能空闲10秒
        httpClientBuilder.evictIdleConnections(10, TimeUnit.SECONDS);
        httpClientBuilder.setKeepAliveStrategy(keepAliveStrategy);
        httpClientBuilder.setRedirectStrategy(new LaxRedirectStrategy());
        httpClientBuilder.setDefaultCookieStore(COOKIE_STORE);
        httpClientBuilder.setUserAgent(USER_AGENT);
        try {
            // 由客户端执行(发送)Get请求
            HttpUriRequest httpUriRequest = supplier.build();
            HttpHost proxy = WindowManager.getInstance().getPacScriptProxy(httpUriRequest.getURI().toString());
            httpClientBuilder.setProxy(proxy);
            HttpClientContext context = HttpClientContext.create();
            CloseableHttpClient httpClient = httpClientBuilder.build();
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


    @Override
    public String readHttpFile(String urlAddress) {
        BufferedReader reader = null;
        StringBuffer content = new StringBuffer();
        try {
            URL url = new URL(urlAddress);
            String line = null;
//            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10 * 1000);
            connection.setReadTimeout(10 * 1000);
            connection.connect();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            while ((line = reader.readLine()) != null) {
                content.append(line + "\n");
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content.toString();
    }


    @FunctionalInterface
    public static interface SupplierCustom {
        HttpUriRequest build() throws Exception;
    }

}
