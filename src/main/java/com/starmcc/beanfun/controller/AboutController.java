package com.starmcc.beanfun.controller;

import com.starmcc.beanfun.client.UpdateClient;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.model.UpdateModel;
import com.starmcc.beanfun.thread.ThreadPoolManager;
import com.starmcc.beanfun.windows.FrameService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class AboutController implements Initializable {


    @FXML
    private ImageView logoImg;
    @FXML
    private Hyperlink versionBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        versionBtn.setText(QsConstant.APP_VERSION);
    }

    @FXML
    public void contactQqAction(ActionEvent actionEvent) {
        FrameService.getInstance().openWebUrl("http://wpa.qq.com/msgrd?v=3&uin=1140526018&site=qq&menu=yes");
    }


    @FXML
    public void verifyVersionAction(ActionEvent actionEvent) {
        ThreadPoolManager.execute(() -> {
            UpdateModel versionModel = UpdateClient.getInstance().getVersionModel();
            FrameService.getInstance().runLater(() -> {
                switch (versionModel.getState()) {
                    case 有新版本:
                        if (QsConstant.confirmDialog("是否前往更新？", versionModel.getUpdateText())) {
                            FrameService.getInstance().openWebUrl(QsConstant.GITHUB_URL + "/releases");
                        }
                        break;
                    case 已是最新版本:
                        QsConstant.alert("已经是最新版本", Alert.AlertType.INFORMATION);
                        break;
                    case 获取失败:
                        QsConstant.alert("已经是最新版本", Alert.AlertType.INFORMATION);
                        break;
                    default:
                        break;
                }
            });
        });
    }
}
