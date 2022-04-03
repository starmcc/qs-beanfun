package com.starmcc.beanfun.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.utils.FrameUtils;
import com.starmcc.beanfun.utils.HttpUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class AboutController implements Initializable {

    private static final String GITHUB_URL = "https://api.github.com/repos/starmcc/qs-beanfun/releases/latest";

    @FXML
    private ImageView logoImg;
    @FXML
    private Hyperlink versionBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image image = new Image("static/images/logo.png");
        logoImg.setImage(image);
        versionBtn.setText(QsConstant.APP_VERSION);
    }

    @FXML
    public void contactQqAction(ActionEvent actionEvent) {
        FrameUtils.openWebUrl("http://wpa.qq.com/msgrd?v=3&uin=1140526018&site=qq&menu=yes");
    }


    /**
     * 获取github版本
     *
     * @return {@link String}
     */
    private String getGithubVersion() {
        try {
            String json = HttpUtils.get(GITHUB_URL, null);
            if (StringUtils.isBlank(json)) {
                return "";
            }
            JSONObject jsonObj = JSON.parseObject(json);
            return jsonObj.getString("tag_name");
        } catch (Exception e) {
            log.error("获取版本发生异常 e={}", e.getMessage(), e);
            return "";
        }
    }

    @FXML
    public void verifyVersionAction(ActionEvent actionEvent) {
        String githubVersion = this.getGithubVersion();
        if (StringUtils.isBlank(githubVersion)) {
            QsConstant.alert("获取版本失败!", Alert.AlertType.WARNING);
            FrameUtils.openWebUrl("https://github.com/starmcc/qs-beanfun/releases");
            return;
        }
        if (StringUtils.equals(githubVersion, QsConstant.APP_VERSION)) {
            QsConstant.alert("当前已是最新版本", Alert.AlertType.INFORMATION);
            return;
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append("有新的版本~~\n");
        buffer.append("最新版本:").append(githubVersion).append("\n");
        buffer.append("是否前往下载?");

        boolean is = QsConstant.confirmDialog(buffer.toString());
        if (is) {
            FrameUtils.openWebUrl("https://github.com/starmcc/qs-beanfun/releases");
        }
    }
}
