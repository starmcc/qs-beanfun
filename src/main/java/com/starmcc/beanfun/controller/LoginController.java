package com.starmcc.beanfun.controller;

import com.starmcc.beanfun.client.BeanfunClient;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.handler.AccountHandler;
import com.starmcc.beanfun.model.ConfigJson;
import com.starmcc.beanfun.model.LoginType;
import com.starmcc.beanfun.model.QsTray;
import com.starmcc.beanfun.model.client.AbstractBeanfunResult;
import com.starmcc.beanfun.model.client.BeanfunAccountResult;
import com.starmcc.beanfun.model.client.BeanfunModel;
import com.starmcc.beanfun.model.client.BeanfunStringResult;
import com.starmcc.beanfun.utils.ConfigFileUtils;
import com.starmcc.beanfun.utils.DataTools;
import com.starmcc.beanfun.utils.FrameUtils;
import com.starmcc.beanfun.windows.SwtWebBrowser;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.http.conn.HttpHostConnectException;

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
    private ChoiceBox<LoginType> loginTypeBox;
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
    @FXML
    private Button resetBtn;


    private boolean loginTask = false;
    private double loginProcess = 0D;

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

        Integer configLoginType = QsConstant.config.getLoginType();
        LoginType selectLoginType = new LoginType(LoginType.TypeEnum.HK_NEW);
        ObservableList<LoginType> loginTypeItems = loginTypeBox.getItems();
        for (LoginType.TypeEnum typeEnum : LoginType.TypeEnum.values()) {
            LoginType loginType = new LoginType(typeEnum);
            loginTypeItems.add(loginType);
            if (Integer.compare(configLoginType, loginType.getType()) == 0) {
                selectLoginType = loginType;
            }
        }
        loginTypeBox.getSelectionModel().select(selectLoginType);

    }

    @FXML
    public void resetAction(ActionEvent actionEvent) {
        QsConstant.config = new ConfigJson();
        ConfigFileUtils.writeConfig(QsConstant.config);
        account.getItems().clear();
        password.setText("");
        remember.setSelected(false);
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
            if (progressBar.getProgress() < 1 && progressBar.getProgress() < loginProcess) {
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
                BeanfunStringResult loginResult = BeanfunClient.run().login(account.getValue(), password.getText(), process -> loginProcess = process);
                if (!loginResult.isSuccess()) {
                    if (Objects.equals(loginResult.getCode(), AbstractBeanfunResult.CodeEnum.PLUGIN_NOT_INSTALL.getCode())) {
                        // 没安装beanfun插件 提示一下，并前往下载
                        Platform.runLater(() -> {
                            if (!QsConstant.confirmDialog("初始化失败", "初始化失败!没有安装Beanfun插件!是否前往下载?")) {
                                return;
                            }
                            SwtWebBrowser.getInstance("https://hk.beanfun.com/locales/HK/contents/beanfun_block/help/plugin_install.html").open();
                        });
                        return;
                    }
                    Platform.runLater(() -> QsConstant.alert(loginResult.getMsg(), Alert.AlertType.ERROR));
                    return;
                }
                BeanfunModel beanfunModel = new BeanfunModel();
                beanfunModel.setToken(loginResult.getData());
                BeanfunAccountResult actResult = BeanfunClient.run().getAccountList(loginResult.getData());
                if (!actResult.isSuccess()) {
                    Platform.runLater(() -> QsConstant.alert(actResult.getMsg(), Alert.AlertType.ERROR));
                    return;
                }
                beanfunModel.setAccountList(actResult.getAccountList());
                beanfunModel.setNewAccount(actResult.getNewAccount());
                // 登录成功后操作
                QsConstant.beanfunModel = beanfunModel;
                Platform.runLater(() -> loginSuccessGoMain());
            } catch (HttpHostConnectException e) {
                log.info("login error e={}", e.getMessage(), e);
                Platform.runLater(() -> QsConstant.alert("连接超时,请检查网络环境", Alert.AlertType.ERROR));
            } catch (Exception e) {
                log.info("login error e={}", e.getMessage(), e);
                Platform.runLater(() -> QsConstant.alert("异常:" + e.getMessage(), Alert.AlertType.ERROR));
            } finally {
                loginning(false);
            }

        });
    }

    @FXML
    public void registerAction() throws Exception {
        String jumpUrl = BeanfunClient.run().getWebUrlRegister();
        if (Integer.compare(QsConstant.config.getLoginType(), LoginType.TypeEnum.HK_OLD.getType()) == 0) {
            FrameUtils.executeThread(() -> SwtWebBrowser.getInstance(jumpUrl).open());
            return;
        }
        WebController.jumpUrl = jumpUrl;
        FrameUtils.openWindow(QsConstant.Page.网页客户端);
    }


    @FXML
    public void forgotPwdAction() throws Exception {
        String jumpUrl = BeanfunClient.run().getWebUrlForgotPwd();
        if (Integer.compare(QsConstant.config.getLoginType(), LoginType.TypeEnum.HK_OLD.getType()) == 0) {
            FrameUtils.executeThread(() -> SwtWebBrowser.getInstance(jumpUrl).open());
            return;
        }
        WebController.jumpUrl = jumpUrl;
        FrameUtils.openWindow(QsConstant.Page.网页客户端);
    }

    /**
     * 记住账密 点击事件
     */
    @FXML
    public void rememberClickAction() {
        QsConstant.config.setRecordActPwd(remember.isSelected());
        ConfigFileUtils.writeConfig(QsConstant.config);
    }


    @FXML
    public void selectLoginTypeAction(ActionEvent actionEvent) {
        LoginType selectedItem = loginTypeBox.getSelectionModel().getSelectedItem();
        QsConstant.config.setLoginType(selectedItem.getType());
        ConfigFileUtils.writeConfig(QsConstant.config);
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
                boolean heartbeat = BeanfunClient.run().heartbeat(QsConstant.beanfunModel.getToken());
                log.info("心跳 heart={}", heartbeat);
            }, 0, 5, TimeUnit.MINUTES);
            // 窗口显示
            FrameUtils.openWindow(QsConstant.Page.主界面);
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
        resetBtn.setDisable(state);
        loginTypeBox.setDisable(state);
    }


}
