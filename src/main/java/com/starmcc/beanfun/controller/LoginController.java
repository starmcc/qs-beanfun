package com.starmcc.beanfun.controller;

import com.starmcc.beanfun.client.BeanfunClient;
import com.starmcc.beanfun.constant.FXPageEnum;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.handler.AccountHandler;
import com.starmcc.beanfun.manager.ThreadPoolManager;
import com.starmcc.beanfun.model.ConfigModel;
import com.starmcc.beanfun.model.LoginType;
import com.starmcc.beanfun.model.client.BeanfunModel;
import com.starmcc.beanfun.model.client.BeanfunStringResult;
import com.starmcc.beanfun.utils.AesTools;
import com.starmcc.beanfun.utils.DataTools;
import com.starmcc.beanfun.utils.FileTools;
import com.starmcc.beanfun.windows.FrameService;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
    private ChoiceBox<LoginType> choiceBoxLoginType;
    @FXML
    private ComboBox<String> comboBoxAccount;
    @FXML
    private PasswordField passwordFieldPassword;
    @FXML
    private Button buttonLogin;
    @FXML
    private CheckBox checkBoxRemember;
    @FXML
    private Hyperlink hyperlinkRegister;
    @FXML
    private Hyperlink hyperlinkForgetPwd;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private ImageView imageViewQrCode;


    private boolean loginTask = false;
    private double loginProcess = 0D;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initBasic();
        refeshAccounts();
    }

    private void initBasic() {
        checkBoxRemember.setSelected(QsConstant.config.getRecordActPwd());
        hyperlinkRegister.setFocusTraversable(false);
        hyperlinkForgetPwd.setFocusTraversable(false);
        checkBoxRemember.setFocusTraversable(false);
        Integer configLoginType = QsConstant.config.getLoginType();
        LoginType selectLoginType = new LoginType();
        ObservableList<LoginType> loginTypeItems = choiceBoxLoginType.getItems();
        for (LoginType.TypeEnum typeEnum : LoginType.TypeEnum.values()) {
            LoginType loginType = new LoginType(typeEnum);
            loginTypeItems.add(loginType);
            if (Integer.compare(configLoginType, loginType.getType()) == 0) {
                selectLoginType = loginType;
            }
        }
        choiceBoxLoginType.getSelectionModel().select(selectLoginType);
    }

    private void refeshAccounts() {
        ObservableList<String> items = comboBoxAccount.getItems();
        items.clear();
        List<ConfigModel.ActPwd> actPwds = QsConstant.config.getActPwds();
        final String key = DataTools.getComputerUniqueId();
        LoginType selectedItem = choiceBoxLoginType.getSelectionModel().getSelectedItem();
        // 解密
        for (ConfigModel.ActPwd item : actPwds) {
            if (!Objects.equals(item.getType(), selectedItem.getType())) {
                continue;
            }
            try {
                items.add(AesTools.dncode(key, item.getAct()));
            } catch (Exception e) {
                log.error("解密异常 e={}", e.getMessage(), e);
            }
        }
        if (DataTools.collectionIsNotEmpty(items)) {
            comboBoxAccount.getSelectionModel().selectFirst();
            String act = comboBoxAccount.getSelectionModel().getSelectedItem();
            passwordFieldPassword.setText(this.getPasswordByAccount(act));
        }
    }


    @FXML
    public void selectAccountAction() {
        String selectedItem = comboBoxAccount.getSelectionModel().getSelectedItem();
        passwordFieldPassword.setText(this.getPasswordByAccount(selectedItem));
    }

    @FXML
    public void loginAction() {
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
        ThreadPoolManager.execute(() -> {
            try {
                String act = comboBoxAccount.getValue();
                String pwd = passwordFieldPassword.getText();
                BeanfunStringResult loginResult = BeanfunClient.run().login(act, pwd, process -> loginProcess = process);
                if (!loginResult.isSuccess()) {
                    FrameService.getInstance().runLater(() -> QsConstant.alert(loginResult.getMsg(), Alert.AlertType.ERROR));
                    return;
                }
                loginProcess = 1;
                BeanfunModel beanfunModel = new BeanfunModel();
                beanfunModel.setToken(loginResult.getData());
                QsConstant.beanfunModel = beanfunModel;
                // 登录成功后操作
                FrameService.getInstance().runLater(() -> loginSuccessGoMain());
            } catch (HttpHostConnectException e) {
                log.info("login error e={}", e.getMessage(), e);
                FrameService.getInstance().runLater(() -> QsConstant.alert("连接超时,请检查网络环境", Alert.AlertType.ERROR));
            } catch (Exception e) {
                log.info("login error e={}", e.getMessage(), e);
                FrameService.getInstance().runLater(() -> QsConstant.alert("异常:" + e.getMessage(), Alert.AlertType.ERROR));
            } finally {
                loginning(false);
            }

        });
    }

    @FXML
    public void registerAction() throws Exception {
        String jumpUrl = BeanfunClient.run().getWebUrlRegister();
        FrameService.getInstance().openWebBrowser(jumpUrl);
    }


    @FXML
    public void forgotPwdAction() throws Exception {
        String jumpUrl = BeanfunClient.run().getWebUrlForgotPwd();
        FrameService.getInstance().openWebBrowser(jumpUrl);
    }

    /**
     * 记住账密 点击事件
     */
    @FXML
    public void rememberClickAction() {
        QsConstant.config.setRecordActPwd(checkBoxRemember.isSelected());
        FileTools.saveConfig(QsConstant.config);
    }


    @FXML
    public void selectLoginTypeAction(ActionEvent actionEvent) {
        LoginType selectedItem = choiceBoxLoginType.getSelectionModel().getSelectedItem();
        QsConstant.config.setLoginType(selectedItem.getType());
        FileTools.saveConfig(QsConstant.config);
        LoginType.TypeEnum typeEnum = LoginType.TypeEnum.getData(QsConstant.config.getLoginType());
        imageViewQrCode.setVisible(typeEnum == LoginType.TypeEnum.TW);

        refeshAccounts();
    }

    @FXML
    public void qrCodeClick() throws Exception {

        FrameService.getInstance().openWindow(FXPageEnum.二维码登录, FXPageEnum.登录页);
    }

    @FXML
    public void closeApplication() {
        FrameService.getInstance().exit();
    }

    @FXML
    public void aboutAction(MouseEvent mouseEvent) throws Exception {
        FrameService.getInstance().openWindow(FXPageEnum.关于我, FXPageEnum.登录页);
    }


    // ==================================================================

    /**
     * 登录成功前往主界面
     */
    private void loginSuccessGoMain() {
        loginning(false);
        // 记录账密
        if (checkBoxRemember.isSelected()) {
            String act = comboBoxAccount.getValue();
            String pwd = passwordFieldPassword.getText();
            AccountHandler.recordActPwd(act, pwd, choiceBoxLoginType.getValue());
        }
        try {
            // 窗口显示
            FrameService.getInstance().openWindow(FXPageEnum.主页);
            FrameService.getInstance().closeWindow(FXPageEnum.登录页);
            FrameService.getInstance().closeWindow(FXPageEnum.二维码登录);
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
        hyperlinkRegister.setDisable(state);
        hyperlinkForgetPwd.setDisable(state);
        checkBoxRemember.setDisable(state);
        comboBoxAccount.setDisable(state);
        passwordFieldPassword.setDisable(state);
        imageViewQrCode.setDisable(state);
        buttonLogin.setDisable(state);
        if (state) {
            progressBar.setProgress(0);
        }
        loginTask = state;
        choiceBoxLoginType.setDisable(state);
    }


    private String getPasswordByAccount(final String act) {
        List<ConfigModel.ActPwd> actPwds = QsConstant.config.getActPwds();
        if (DataTools.collectionIsEmpty(actPwds)) {
            return "";
        }
        final String key = DataTools.getComputerUniqueId();
        Optional<ConfigModel.ActPwd> optional = actPwds.stream().filter(actPwd -> {
            if (!Objects.equals(QsConstant.config.getLoginType(), actPwd.getType())) {
                return false;
            }
            return StringUtils.equals(AesTools.dncode(key, actPwd.getAct()), act);
        }).findFirst();
        return optional.isPresent() ? AesTools.dncode(key, optional.get().getPwd()) : "";
    }
}
