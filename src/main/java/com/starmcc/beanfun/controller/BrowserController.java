package com.starmcc.beanfun.controller;

import com.starmcc.beanfun.client.HttpClient;
import com.starmcc.beanfun.manager.FrameManager;
import com.starmcc.beanfun.manager.ThreadPoolManager;
import com.starmcc.beanfun.utils.DataTools;
import com.sun.webkit.network.CookieManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

import java.net.CookieHandler;
import java.net.URI;
import java.net.URL;
import java.util.*;

/**
 * 浏览器控制器 TODO 已废弃，现使用JxBrowser浏览器
 *
 * @author starmcc
 * @date 2022/11/24
 */
@Slf4j
public class BrowserController implements Initializable {

    public static String url;

    @FXML
    private WebView browser;
    @FXML
    private TextField textUrl;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initEvent();
        ThreadPoolManager.execute(() -> {
            final long time = System.currentTimeMillis();
            saveCookieToBrowser();
            FrameManager.getInstance().runLater(() -> {
                WebEngine webEngine = browser.getEngine();
                textUrl.setText(url);
                webEngine.load(url);
            });
        }, false);
    }


    private void initEvent() {
        textUrl.setOnKeyPressed(event -> {
            if (event.getCode() != KeyCode.ENTER) {
                return;
            }
            FrameManager.getInstance().runLater(() -> {
                WebEngine webEngine = browser.getEngine();
                url = textUrl.getText();
                textUrl.setText(url);
                webEngine.load(url);
            });
        });
        browser.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> FrameManager.getInstance().runLater(() -> textUrl.setText(newValue)));
    }

    /**
     * Cookies保存到内置浏览器中
     */
    private void saveCookieToBrowser() {
        CookieStore cookieStore = HttpClient.getInstance().getCookieStore();
        List<Cookie> cookies = cookieStore.getCookies();
        Map<String, List<Cookie>> map = new HashMap<>();
        for (Cookie cookie : cookies) {
            List<Cookie> list = map.get(cookie.getDomain());
            list = DataTools.collectionIsEmpty(list) ? new ArrayList<>() : list;
            list.add(cookie);
            map.put(cookie.getDomain(), list);
        }
        CookieManager cookieManager = (CookieManager) CookieHandler.getDefault();
        for (Map.Entry<String, List<Cookie>> mm : map.entrySet()) {
            Map<String, List<String>> responseHeaders = new HashMap<>();
            List<String> list = new ArrayList<>();
            List<Cookie> vals = mm.getValue();
            for (Cookie val : vals) {
                list.add(val.getName() + "=" + val.getValue() + "; path=" + val.getPath() + "; domain=" + val.getDomain());
            }
            responseHeaders.put("Set-Cookie", list);
            cookieManager.put(URI.create("https://" + mm.getKey()), responseHeaders);
        }
    }
}