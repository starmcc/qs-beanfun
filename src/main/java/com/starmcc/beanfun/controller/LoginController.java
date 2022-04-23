package com.starmcc.beanfun.controller;

import com.starmcc.beanfun.client.BeanfunClient;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.exception.BFServiceNotFondException;
import com.starmcc.beanfun.handler.AccountHandler;
import com.starmcc.beanfun.model.ConfigJson;
import com.starmcc.beanfun.model.QsTray;
import com.starmcc.beanfun.utils.ConfigFileUtils;
import com.starmcc.beanfun.utils.DataTools;
import com.starmcc.beanfun.utils.FrameUtils;
import com.starmcc.beanfun.windows.SwtWebBrowser;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 登录控制器
 *
 * @author starmcc
 * @date 2022/03/19
 */
@Slf4j
public class LoginController implements Initializable {


    @FXML
    private ComboBox<String> account;
    @FXML
    private PasswordField password;
    @FXML
    private Button loginBtn;
    @FXML
    private CheckBox remember;
    @FXML
    private ImageView logo;
    @FXML
    private Hyperlink register;
    @FXML
    private Hyperlink forgetPwd;
    @FXML
    private ProgressBar progressBar;


    private boolean loginTask = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> items = account.getItems();
        List<ConfigJson.ActPwd> actPwds = QsConstant.config.getActPwds();
        actPwds.forEach((actPwd) -> items.add(actPwd.getAct()));
        if (DataTools.collectionIsNotEmpty(actPwds)) {
            account.getSelectionModel().selectFirst();
            password.setText(actPwds.get(0).getPwd());
        }
        remember.setSelected(QsConstant.config.getRecordActPwd());
        Image image = new Image("static/images/logo.png");
        logo.setImage(image);

        register.setFocusTraversable(false);
        forgetPwd.setFocusTraversable(false);
        remember.setFocusTraversable(false);
    }

    @FXML
    public void selectAccountAction() {
        String act = account.getSelectionModel().getSelectedItem();
        List<ConfigJson.ActPwd> actPwds = QsConstant.config.getActPwds();
        if (DataTools.collectionIsEmpty(actPwds)) {
            password.setText("");
            return;
        }
        Optional<ConfigJson.ActPwd> queryOpt = actPwds.stream().filter(x -> StringUtils.equals(x.getAct(), act)).findFirst();
        if (queryOpt.isPresent()) {
            password.setText(queryOpt.get().getPwd());
        } else {
            password.setText("");
        }
    }

    @FXML
    public void login() {
        loginning(true);
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("LoginController-schedule-pool-%d").daemon(true).build());
        executorService.scheduleAtFixedRate(() -> {
            if (progressBar.getProgress() < 1 && progressBar.getProgress() < BeanfunClient.loginProcess) {
                progressBar.setProgress(progressBar.getProgress() + 0.01D);
            }
            if (loginTask) {
                return;
            }
            executorService.shutdown();
        }, 0, 50, TimeUnit.MILLISECONDS);

        // 执行登录方法
        FrameUtils.executeThread(() -> {
            try {
                if (BeanfunClient.getInstance().login(account.getValue(), password.getText())
                        && BeanfunClient.getInstance().getAccountList()) {
                    Platform.runLater(() -> loginSuccessGoMain());
                    return;
                }
                Platform.runLater(() -> QsConstant.alert(BeanfunClient.errorMsg, Alert.AlertType.ERROR));
            } catch (BFServiceNotFondException e) {
                // 没安装beanfun插件 提示一下，并前往下载
                Platform.runLater(() -> {
                    if (QsConstant.confirmDialog("初始化失败", "初始化失败!没有安装Beanfun插件!是否前往下载?")) {
                        SwtWebBrowser.getInstance("https://hk.beanfun.com/locales/HK/contents/beanfun_block/help/plugin_install.html").open();
                    }
                });
            } catch (Exception e) {
                log.info("login error e={}", e.getMessage(), e);
                Platform.runLater(() -> QsConstant.alert("异常:" + e.getMessage(), Alert.AlertType.ERROR));
            }
            loginning(false);
        });
    }

    @FXML
    public void registerAction() {
        FrameUtils.executeThread(() -> SwtWebBrowser.getInstance(BeanfunClient.getInstance().getWebUrlRegister()).open());
    }


    @FXML
    public void forgotPwdAction() {
        FrameUtils.executeThread(() -> SwtWebBrowser.getInstance(BeanfunClient.getInstance().getWebUrlForgotPwd()).open());
    }

    /**
     * 记住账密 点击事件
     */
    @FXML
    public void rememberClickAction() {
        QsConstant.config.setRecordActPwd(remember.isSelected());
        ConfigFileUtils.writeJsonFile(QsConstant.config, QsConstant.APP_CONFIG);
    }


    /**
     * 登录成功前往主界面
     */
    private void loginSuccessGoMain() {
        loginning(false);
        // 记录账密
        if (remember.isSelected()) {
            AccountHandler.recordActPwd(account.getValue(), password.getText());
        }
        try {
            // 定时心跳设置
            QsConstant.heartExecutorService = new ScheduledThreadPoolExecutor(1,
                    new BasicThreadFactory.Builder().namingPattern("MainController-heartbeat-schedule-pool-%d").daemon(true).build());
            QsConstant.heartExecutorService.scheduleAtFixedRate(() -> {
                boolean heartbeat = BeanfunClient.getInstance().heartbeat();
                log.info("心跳 heart={}", heartbeat);
            }, 0, 5, TimeUnit.MINUTES);
            // 窗口显示
            FrameUtils.openWindow(QsConstant.Page.主界面, jfxStage -> {
                jfxStage.setCloseEvent(() -> {
                    if (Objects.nonNull(QsConstant.heartExecutorService)) {
                        QsConstant.heartExecutorService.shutdown();
                    }
                    Platform.exit();
                    QsTray.remove(QsConstant.trayIcon);
                });
                jfxStage.setMinEvent(() -> {
                    if (jfxStage.getStage().isIconified()) {
                        jfxStage.getStage().setIconified(false);
                    }
                    jfxStage.getStage().hide();
                });
                QsConstant.mainJFXStage = jfxStage;
            });
            QsConstant.trayIcon = QsTray.init(QsConstant.mainJFXStage.getStage());
            QsTray.show(QsConstant.trayIcon);
            FrameUtils.closeWindow(QsConstant.loginJFXStage);
        } catch (Exception e) {
            log.error("loginSuccessGoMain e={}", e.getMessage(), e);
        }
    }


    /**
     * 登录中
     *
     * @param state 状态
     */
    private void loginning(boolean state) {
        register.setDisable(state);
        forgetPwd.setDisable(state);
        remember.setDisable(state);
        account.setDisable(state);
        password.setDisable(state);
        loginBtn.setVisible(!state);
        progressBar.setVisible(state);
        if (state) {
            progressBar.setProgress(0);
        }
        loginTask = state;

    }


}
