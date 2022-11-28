package com.starmcc.beanfun.utils;

import com.starmcc.beanfun.client.HttpClient;
import com.starmcc.beanfun.manager.FrameManager;
import com.starmcc.beanfun.manager.ThreadPoolManager;
import com.starmcc.beanfun.manager.WindowManager;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.CookieStorage;
import com.teamdev.jxbrowser.chromium.ba;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * jx浏览器
 *
 * @author starmcc
 * @date 2022/11/28
 */
@Slf4j
public class JxBrowser {

    private final static String DEFAULT_TITLE = "QsBrowser";
    private final static int DEFAULT_WIDTH = 800;
    private final static int DEFAULT_HEIGHT = 700;

    static {
        try {
            // 授权
            Field e = ba.class.getDeclaredField("e");
            e.setAccessible(true);
            Field f = ba.class.getDeclaredField("f");
            f.setAccessible(true);
            Field modifersField = Field.class.getDeclaredField("modifiers");
            modifersField.setAccessible(true);
            modifersField.setInt(e, e.getModifiers() & ~Modifier.FINAL);
            modifersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
            e.set(null, new BigInteger("1"));
            f.set(null, new BigInteger("1"));
            modifersField.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获得实例
     *
     * @return {@link JxBrowser}
     */
    public synchronized static JxBrowser getInstance() {
        return new JxBrowser();
    }

    /**
     * 打开浏览器
     *
     * @param url url
     */
    public void open(String url) {
        // 开始构建javafx窗体
        Stage stage = new Stage();
        Browser browser = new Browser();
        BrowserView view = new BrowserView(browser);
        view.setPrefWidth(DEFAULT_WIDTH);
        view.setPrefHeight(DEFAULT_HEIGHT);
        VBox box = new VBox();
        TextField textUrl = new TextField();
        textUrl.setText(url);
        textUrl.setPrefWidth(DEFAULT_WIDTH);
        textUrl.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                browser.loadURL(textUrl.getText());
            }
        });
        box.getChildren().add(textUrl);
        box.getChildren().add(view);
        stage.setScene(new Scene(box));
        stage.setTitle(DEFAULT_TITLE);
        stage.setWidth(DEFAULT_WIDTH);
        stage.setHeight(DEFAULT_HEIGHT);
        stage.setOnCloseRequest(event -> {
            // 使用线程异步关闭引擎
            ThreadPoolManager.execute(() -> browser.dispose());
            // 关闭窗口
            stage.close();
        });
        // 显示窗口
        stage.show();
        // 初始化代理信息
        this.initProxyInfo(url);
        // 初始化Cookies
        initBrowserCookies(url, browser.getCookieStorage());
        // 设置链接和标题的实时跟踪
        browser.addTitleListener((titleEvent) -> FrameManager.getInstance().runLater(() -> {
            stage.setTitle(DEFAULT_TITLE + " - " + titleEvent.getTitle());
            textUrl.setText(titleEvent.getBrowser().getURL());
        }));
        // 设置新窗口只在本窗口中打开
        browser.setPopupHandler(popupParams -> (b2, rectangle) -> browser.loadURL(popupParams.getURL()));
        // 加载地址
        browser.loadURL(url);
    }

    /**
     * 初始化浏览器cookie
     *
     * @param url           url
     * @param cookieStorage cookie存储
     */
    private static void initBrowserCookies(String url, CookieStorage cookieStorage) {
        CookieStore cookieStore = HttpClient.getInstance().getCookieStore();
        List<Cookie> cookies = cookieStore.getCookies();
        for (Cookie cookie : cookies) {
            // url,  name, value,  domain, path,  expirationTimeInMicroseconds,  secure,  httpOnly
            long exp = 0;
            if (Objects.nonNull(cookie.getExpiryDate())) {
                exp = cookie.getExpiryDate().getTime();
            }
            cookieStorage.setCookie(url,
                    cookie.getName(),
                    cookie.getValue(),
                    cookie.getDomain(),
                    cookie.getPath(),
                    exp, true, false);
            cookieStorage.save();
        }
    }


    /**
     * 初始化代理信息
     *
     * @param url url
     */
    private void initProxyInfo(String url) {
        try {
            URI uri = new URI(url);
            HttpHost proxy = WindowManager.getInstance().getPacScriptProxy(uri);
            String ip = Objects.nonNull(proxy) ? proxy.getHostName() : null;
            String port = Objects.nonNull(proxy) ? String.valueOf(proxy.getPort()) : null;
            if (StringUtils.isNotBlank(ip) && StringUtils.isNotBlank(port)) {
                System.setProperty("http.proxyHost", ip);
                System.setProperty("http.proxyPort", port);
                System.setProperty("https.proxyHost", ip);
                System.setProperty("https.proxyPort", port);
            } else {
                Properties properties = System.getProperties();
                properties.remove("http.proxyHost");
                properties.remove("http.proxyPort");
                properties.remove("https.proxyHost");
                properties.remove("https.proxyPort");
            }
        } catch (Exception e) {
            log.error("设置代理失败, 恢复默认状态 e={}", e.getMessage(), e);
            Properties properties = System.getProperties();
            properties.remove("http.proxyHost");
            properties.remove("http.proxyPort");
            properties.remove("https.proxyHost");
            properties.remove("https.proxyPort");
        }
    }
}