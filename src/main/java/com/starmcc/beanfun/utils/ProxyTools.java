package com.starmcc.beanfun.utils;

import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.entity.model.ConfigModel;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinReg;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.List;
import java.util.Objects;

/**
 * 代理工具
 *
 * @author starmcc
 * @date 2023/04/06
 */
@Slf4j
public class ProxyTools {

    public static HttpHost getProxy(URI uri) {
        // 如果有自定义配置的代理，优先使用配置代理
        ConfigModel.ProxyConfig proxyConfig = QsConstant.config.getProxyConfig();
        if (Objects.nonNull(proxyConfig)) {
            if (BooleanUtils.isTrue(proxyConfig.getBan())) {
                log.info("禁止使用代理");
                return null;
            }
            if (StringUtils.isNotBlank(proxyConfig.getIp()) && Objects.nonNull(proxyConfig.getPort())) {
                log.info("use proxy my custom value = {}", proxyConfig.toString());
                return new HttpHost(proxyConfig.getIp(), proxyConfig.getPort(), "http");
            }
        }
        return getPacProxy(uri);
    }


    /**
     * 获取系统级代理（TODO 暂时无用）
     *
     * @param uri uri
     * @return {@link HttpHost}
     */
    private static HttpHost getSystemProxy(URI uri) {
        List<Proxy> proxyList = ProxySelector.getDefault().select(uri);
        if (DataTools.collectionIsEmpty(proxyList)) {
            return null;
        }
        Proxy proxy = proxyList.get(0);
        if (Objects.nonNull(proxy) && proxy.type() == Proxy.Type.HTTP) {
            InetSocketAddress addr = (InetSocketAddress) proxy.address();
            if (Objects.isNull(addr)) {
                return null;
            }
            String host = addr.getHostString();
            int port = addr.getPort();
            return new HttpHost(host, port, "http");
        }
        return null;
    }


    private static HttpHost getPacProxy(URI uri) {
        String agent = getPACScriptAgent(uri.toString());
        if (StringUtils.isBlank(agent)) {
            return null;
        }
        String[] split = agent.split(":");
        if (ArrayUtils.isEmpty(split) || split.length < 2) {
            return null;
        }
        String ip = split[0];
        int port = Integer.parseInt(split[1]);
        return new HttpHost(ip, port, "http");
    }


    private static String getPACScriptAgent(String url) {
        try {
            String path = "Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings";
            boolean exists = Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, path, "AutoConfigURL", WinNT.KEY_READ);
            if (!exists) {
                return "";
            }
            String proxyUrl = Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, path, "AutoConfigURL", WinNT.KEY_READ);
            if (StringUtils.isBlank(proxyUrl)) {
                log.info("not pac proxy");
                return "";
            }
            HttpGet httpGet = new HttpGet(proxyUrl);
            CloseableHttpResponse execute = HttpClients.createDefault().execute(httpGet);
            String pacScript = EntityUtils.toString(execute.getEntity());
            if (StringUtils.isBlank(pacScript)) {
                log.info("read pac proxy file is null");
                return "";
            }
            // 直接使用代理
            List<List<String>> regex = RegexUtils.regex(RegexUtils.Constant.COMMON_IP_ADDRESS, pacScript);
            return RegexUtils.getIndex(0, 1, regex);

            //调用js中的方法 此处调用过慢 TODO 直接使用PAC路由
            /*ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
            engine.eval(pacScript);
            URI uri = new URI(url);
            Object runResult = ((Invocable) engine).invokeFunction("FindProxyForURL", uri.toString(), uri.getHost());
            if (Objects.isNull(runResult)) {
                log.info("url:{} runing FindProxyForURL is null result", url);
                return "";
            }
            String pac = String.valueOf(runResult);
            log.info("pac={}", pac);
            // DIRECT 不代理
            if (StringUtils.indexOf(pac, "DIRECT") >= 0) {
                return "";
            }
            String[] split = pac.split(";");
            for (String pxy : split) {
                String[] pxyArr = pxy.trim().split(" ");
                if (ArrayUtils.isEmpty(pxyArr) || pxyArr.length < 2) {
                    continue;
                }
                if (!StringUtils.equals(pxyArr[0].trim(), "PROXY")) {
                    continue;
                }
                return pxyArr[1].trim();
            }*/
        } catch (Exception e) {
            log.error("proxy error = {}", e.getMessage(), e);
        }
        return "";
    }

}
