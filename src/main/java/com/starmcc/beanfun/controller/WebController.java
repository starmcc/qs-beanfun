package com.starmcc.beanfun.controller;

import com.starmcc.beanfun.client.HttpClient;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.model.LoginType;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.CookieHandler;
import java.net.URI;
import java.net.URL;
import java.security.GeneralSecurityException;
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
        urlText.setText(jumpUrl);
        // 禁用ssl证书
        this.disableSslCertificate();

        WebEngine webEngine = webView.getEngine();
        if (Integer.compare(QsConstant.config.getLoginType(), LoginType.TypeEnum.HK_OLD.getType()) == 0) {
            webEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Safari/537.36Edge/13.10586");
        }
        try {
            Map<String, String> cookies = HttpClient.getCookies();
            List<String> list = new ArrayList<>();
            for (String key : cookies.keySet()) {
                list.add(key + "=" + cookies.get(key));
            }
            Map<String, List<String>> headers = new LinkedHashMap<String, List<String>>();
            headers.put("Set-Cookie", list);
            CookieHandler.getDefault().put(URI.create(jumpUrl), headers);
        } catch (Exception e) {
            log.error("设置cookie异常 e={}", e.getMessage(), e);
        }


        urlText.setOnKeyPressed(keyEvent -> {
            if (StringUtils.equals(keyEvent.getCode().getName(), "Enter")) {
                jumpUrl = urlText.getText();
                jumpUrl = jumpUrl.startsWith("http") ? jumpUrl.trim() : "http://" + jumpUrl.trim();
                webEngine.load(jumpUrl);
            }
        });

        webEngine.locationProperty().addListener((observable, oldValue, newValue) -> {
            urlText.setText(newValue);
        });

        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                changeTitle(webEngine.getTitle());
            }
        });

        webEngine.load(jumpUrl);
    }

    /**
     * 禁用ssl证书
     */
    private void disableSslCertificate() {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (GeneralSecurityException e) {
            log.error("异常 e={}", e.getMessage(), e);
        }
    }

    private void changeTitle(String title) {
        Stage stage = QsConstant.webJFXStage.getStage();
        stage.setTitle(title);
        BorderPane borderPane = (BorderPane) stage.getScene().getRoot();
        HBox hBox = (HBox) borderPane.getTop();
        ObservableList<Node> children = hBox.getChildren();
        for (Node child : children) {
            if (StringUtils.equals(child.getId(), "customTitle")) {
                Label titleLabl = (Label) child;
                titleLabl.setText(title);
                break;
            }
        }
    }


}
