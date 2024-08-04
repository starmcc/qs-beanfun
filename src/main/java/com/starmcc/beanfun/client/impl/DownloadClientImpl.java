package com.starmcc.beanfun.client.impl;

import com.starmcc.beanfun.client.DownloadClient;
import com.starmcc.beanfun.manager.AdvancedTimerManager;
import com.starmcc.beanfun.manager.WindowManager;
import com.starmcc.beanfun.manager.impl.AdvancedTimerTask;
import com.starmcc.beanfun.utils.SystemTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Objects;

/**
 * 下载客户端实现
 *
 * @author starmcc
 * @date 2022/12/11
 */
@Slf4j
public class DownloadClientImpl extends DownloadClient {
    private long progres = 0L;

    @Override

    public void execute(URL url, File saveFile, DownloadClient.Process process) {
        process.call(DownloadClient.Process.State.READY_START, null, 0, null, null);
        FileOutputStream fos = null;
        try {
            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
            httpClientBuilder.setSSLSocketFactory(sslsf);
            // 代理
            HttpHost proxy = WindowManager.getInstance().getProxy(url.toURI());
            httpClientBuilder.setProxy(proxy);
            httpClientBuilder.setDefaultRequestConfig(RequestConfig.custom()
                    .setConnectionRequestTimeout(10 * 1000)
                    .setConnectTimeout(10 * 1000)
                    .setSocketTimeout(10 * 1000)
                    .build()
            );
            CloseableHttpClient httpClient = httpClientBuilder.build();
            process.call(DownloadClient.Process.State.CONNECT_RUNNING, null, null, null, null);
            CloseableHttpResponse response = httpClient.execute(new HttpGet(url.toString()));
            if (response.getStatusLine().getStatusCode() != 200) {
                process.call(DownloadClient.Process.State.REQUEST_STATUS_ERR, null, null, null, null);
                return;
            }
            process.call(DownloadClient.Process.State.CREATE_FILE, null, null, null, null);
            byte[] bytes = readDownloadFile(process, response);
            if (Objects.isNull(bytes)) {
                return;
            }
            fos = new FileOutputStream(saveFile);
            fos.write(bytes);
            process.call(DownloadClient.Process.State.DOWNLOAD_OK, saveFile, null, null, null);
        } catch (Exception e) {
            log.error("下载异常 e={}", e.getMessage(), e);
            process.call(DownloadClient.Process.State.CONNECT_TIMEOUT, null, null, null, e);
        } finally {
            SystemTools.close(fos);
        }
    }

    /**
     * 读下载文件
     *
     * @param process  过程
     * @param response 响应
     * @return {@link byte[]}
     */
    private byte[] readDownloadFile(DownloadClient.Process process, CloseableHttpResponse response) {
        ByteArrayOutputStream bos = null;
        String taskName = null;
        boolean error = false;
        try {
            HttpEntity entity = response.getEntity();
            InputStream inputStream = entity.getContent();
            int contentLength = (int) entity.getContentLength();
            byte[] buffer = new byte[1024];
            int unitProgress = 0;
            int len = 0;
            long unit = contentLength / 100;
            bos = new ByteArrayOutputStream();
            taskName = AdvancedTimerManager.getInstance().addTask(new AdvancedTimerTask() {
                private long temp = 0;
                private BigDecimal speed = BigDecimal.ZERO;

                @Override
                public void start() throws Exception {
                    speed = new BigDecimal(progres - temp);
                    temp = progres;
                    process.call(Process.State.SPEED_ECHO, null, null, speed.divide(new BigDecimal(1000), 2, RoundingMode.HALF_UP), null);
                }
            }, 0, 1000);
            while ((len = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
                //保存当前具体进度
                progres += len;
                //计算当前百分比进度
                int temp = (int) (progres / unit);
                //如果下载过程出现百分比变化
                if (temp >= 1 && temp > unitProgress) {
                    //保存当前百分比
                    unitProgress = temp;
                    process.call(DownloadClient.Process.State.DOWNLOAD_RUNNING, null, unitProgress, null, null);
                }
            }
        } catch (Exception e) {
            log.error("下载异常 e={}", e.getMessage(), e);
            process.call(DownloadClient.Process.State.UNKNOWN_ERR, null, null, null, e);
            error = true;
        } finally {
            SystemTools.close(bos);
            AdvancedTimerManager.getInstance().removeTask(taskName);
        }
        return Objects.nonNull(bos) && !error ? bos.toByteArray() : null;
    }
}
