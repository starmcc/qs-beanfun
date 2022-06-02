package com.starmcc.beanfun.controller;

import com.starmcc.beanfun.client.HttpClient;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import lombok.extern.slf4j.Slf4j;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URI;
import java.net.URL;
import java.util.*;

@Slf4j
public class WebController implements Initializable {

    @FXML
    private WebView webView;
    @FXML
    private TextField urlText;

    public static String jumpUrl = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        WebEngine webEngine = webView.getEngine();
        webEngine.load(jumpUrl);
        try {
            CookieManager cookieManager = (CookieManager) CookieHandler.getDefault();
            Map<String, List<String>> responseHeaders = new HashMap<>(2);
            List<String> list = new ArrayList<>();
            Map<String, String> cookies = HttpClient.getCookies();
            StringBuilder cookieBf = new StringBuilder();
            for (String key : cookies.keySet()) {
                cookieBf.append(key).append("=").append(cookies.get(key)).append("; ");
            }
            list.add(cookieBf.toString());
            responseHeaders.put("Set-Cookie", list);
            URI uri = URI.create(webEngine.getLocation());
            cookieManager.put(uri, responseHeaders);
        } catch (Exception e) {
            log.error("设置cookie异常 e={}", e.getMessage(), e);
        }
    }


}
