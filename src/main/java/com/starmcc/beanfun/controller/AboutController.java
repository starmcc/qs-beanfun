package com.starmcc.beanfun.controller;

import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.entity.model.ConfigModel;
import com.starmcc.beanfun.manager.FrameManager;
import com.starmcc.beanfun.manager.ThreadPoolManager;
import com.starmcc.beanfun.manager.UpdateManager;
import com.starmcc.beanfun.utils.FileTools;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;

import java.io.File;
import java.net.URL;
import java.util.Objects;
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
    private CheckBox checkBoxPacSwitch;
    @FXML
    private Hyperlink versionBtn;
    @FXML
    private ToggleGroup toggleGroupUpdateChannel;
    @FXML
    private Button buttonOpenSource;
    @FXML
    private Tooltip tooltipPac;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Tooltip
//        tooltipPac.setText("abc\nabc");
        ConfigModel.UpdateChannel updateChannel = ConfigModel.UpdateChannel.get(QsConstant.config.getUpdateChannel());
        if (updateChannel == ConfigModel.UpdateChannel.GITHUB) {
            buttonOpenSource.setText("开源GitHub");
        } else if (updateChannel == ConfigModel.UpdateChannel.GITEE) {
            buttonOpenSource.setText("开源Gitee");
        }

        for (Toggle toggle : toggleGroupUpdateChannel.getToggles()) {
            toggle.setSelected(Objects.equals(Short.valueOf(String.valueOf(toggle.getUserData())), updateChannel.getChannel()));
        }
        toggleGroupUpdateChannel.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            QsConstant.config.setUpdateChannel(Short.valueOf(String.valueOf(newValue.getUserData())));
            FileTools.saveConfig(QsConstant.config);
        });

        versionBtn.setText(QsConstant.APP_VERSION);
        if (Objects.nonNull(QsConstant.config.getProxyConfig())) {
            checkBoxPacSwitch.setSelected(!BooleanUtils.isTrue(QsConstant.config.getProxyConfig().getBan()));
        }
        checkBoxPacSwitch.setOnAction(event -> {
            ConfigModel.ProxyConfig proxyConfig = null;
            if (Objects.isNull(QsConstant.config.getProxyConfig())) {
                proxyConfig = new ConfigModel.ProxyConfig();
            } else {
                proxyConfig = QsConstant.config.getProxyConfig();
            }
            proxyConfig.setBan(!checkBoxPacSwitch.isSelected());
            QsConstant.config.setProxyConfig(proxyConfig);
            FileTools.saveConfig(QsConstant.config);
        });
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
        ConfigModel.UpdateChannel updateChannel = ConfigModel.UpdateChannel.get(QsConstant.config.getUpdateChannel());
        if (updateChannel == ConfigModel.UpdateChannel.GITHUB) {
            FrameManager.getInstance().openWebUrl(QsConstant.GITHUB_URL);
        } else if (updateChannel == ConfigModel.UpdateChannel.GITEE) {
            FrameManager.getInstance().openWebUrl(QsConstant.GITEE_URL);
        }
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
