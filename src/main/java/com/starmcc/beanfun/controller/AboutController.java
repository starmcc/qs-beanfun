package com.starmcc.beanfun.controller;

import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.manager.FrameManager;
import com.starmcc.beanfun.manager.ThreadPoolManager;
import com.starmcc.beanfun.manager.UpdateManager;
import com.starmcc.beanfun.utils.FileTools;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
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

    @FXML
    public void resetConfigBtnAction(ActionEvent actionEvent) {
        boolean is = FrameManager.getInstance().dialogConfirm("重置所有配置", "是否重置所有配置？\n注意：\n该操作会清空所有账号记录!");
        if (!is) {
            return;
        }
        File file = new File(QsConstant.PATH_APP_PLUGINS);
        if (!file.exists()) {
            return;
        }
        is = FileTools.deleteFolder(file);
        if (!is) {
            FrameManager.getInstance().messageSync("所有配置重置失败!", Alert.AlertType.ERROR);
            return;
        }
        FrameManager.getInstance().exit();
    }
}
