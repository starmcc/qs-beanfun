package com.starmcc.beanfun.controller;

import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.manager.FrameManager;
import com.starmcc.beanfun.manager.ThreadPoolManager;
import com.starmcc.beanfun.manager.UpdateManager;
import com.starmcc.beanfun.manager.WindowManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 关于控制器
 *
 * @author starmcc
 * @date 2022/09/21
 */
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
        FrameManager.getInstance().openWebUrl("http://wpa.qq.com/msgrd?v=3&uin=1140526018&site=qq&menu=yes");
    }


    @FXML
    public void verifyVersionAction(ActionEvent actionEvent) {
        ThreadPoolManager.execute(() -> UpdateManager.getInstance().verifyAppVersion(false));
    }

    @FXML
    public void gitHubBtnAction(ActionEvent actionEvent) {
        FrameManager.getInstance().openWebUrl(QsConstant.GITHUB_URL);
    }
}
