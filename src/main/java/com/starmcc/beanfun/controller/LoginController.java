package com.starmcc.beanfun.controller;

import com.starmcc.beanfun.client.BeanfunClient;
import com.starmcc.beanfun.constant.FXPageEnum;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.entity.LoginType;
import com.starmcc.beanfun.entity.client.BeanfunModel;
import com.starmcc.beanfun.entity.client.BeanfunStringResult;
import com.starmcc.beanfun.entity.model.ComBoBoxListCell;
import com.starmcc.beanfun.entity.model.ConfigModel;
import com.starmcc.beanfun.entity.model.LoadingPage;
import com.starmcc.beanfun.handler.AccountHandler;
import com.starmcc.beanfun.manager.FrameManager;
import com.starmcc.beanfun.utils.AesTools;
import com.starmcc.beanfun.utils.DataTools;
import com.starmcc.beanfun.utils.FileTools;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.HttpHostConnectException;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;


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
    private CheckBox checkBoxRemember;
    @FXML
    private Hyperlink hyperlinkRegister;
    @FXML
    private Hyperlink hyperlinkForgetPwd;
    @FXML
    private ImageView imageViewQrCode;
    private double loginProcess = 0D;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initBasic();
        refeshAccounts();
        // 初始化完后如果密码框有内容则获取焦点
        if (StringUtils.isNotBlank(passwordFieldPassword.getText())) {
            passwordFieldPassword.requestFocus();
        }
    }

    private void initBasic() {
        comboBoxAccount.setCellFactory((view) -> new ComBoBoxListCell(this::whetherToDeleteAccountRecord));
        passwordFieldPassword.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                this.loginAction();
            }
        });
        checkBoxRemember.setSelected(BooleanUtils.isTrue(QsConstant.config.getRecordActPwd()));
        hyperlinkRegister.setFocusTraversable(false);
        hyperlinkForgetPwd.setFocusTraversable(false);
        imageViewQrCode.setFocusTraversable(false);
        choiceBoxLoginType.setFocusTraversable(false);
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
        passwordFieldPassword.requestFocus();
    }

    @FXML
    public void loginAction() {
        LoadingPage.taskAsync(FXPageEnum.登录页, "正在登录..", () -> {
            try {
                // 执行登录方法
                String act = comboBoxAccount.getValue();
                String pwd = passwordFieldPassword.getText();
                BeanfunStringResult loginResult = BeanfunClient.run().login(act, pwd, process -> loginProcess = process);
                if (!loginResult.isSuccess()) {
                    FrameManager.getInstance().message(loginResult.getMsg(), Alert.AlertType.ERROR);
                    return;
                }
                loginProcess = 1;
                BeanfunModel beanfunModel = new BeanfunModel();
                beanfunModel.setToken(loginResult.getData());
                QsConstant.beanfunModel = beanfunModel;
                // 登录成功后操作
                FrameManager.getInstance().runLater(() -> loginSuccessGoMain());
            } catch (HttpHostConnectException e) {
                log.info("login error e={}", e.getMessage(), e);
                FrameManager.getInstance().message("连接超时,请检查网络环境", Alert.AlertType.ERROR);
            } catch (Exception e) {
                log.info("login error e={}", e.getMessage(), e);
                FrameManager.getInstance().message("error:" + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    public void registerAction() throws Exception {
        String jumpUrl = BeanfunClient.run().getWebUrlRegister();
        FrameManager.getInstance().openWebBrowser(jumpUrl);
    }


    @FXML
    public void forgotPwdAction() throws Exception {
        String jumpUrl = BeanfunClient.run().getWebUrlForgotPwd();
        FrameManager.getInstance().openWebBrowser(jumpUrl);
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

        FrameManager.getInstance().openWindow(FXPageEnum.二维码登录, FXPageEnum.登录页);
    }

    @FXML
    public void closeApplication() {
        FrameManager.getInstance().exit();
    }

    @FXML
    public void aboutAction(MouseEvent mouseEvent) throws Exception {
        FrameManager.getInstance().openWindow(FXPageEnum.关于我, FXPageEnum.登录页);
    }


    // ==================================================================

    /**
     * 登录成功前往主界面
     */
    private void loginSuccessGoMain() {
        // 记录账密 如果点了记住密码，则会记录密码，否则只记录账号
        String act = comboBoxAccount.getValue();
        String pwd = "";
        if (checkBoxRemember.isSelected()) {
            pwd = passwordFieldPassword.getText();
        }
        AccountHandler.recordActPwd(act, pwd, choiceBoxLoginType.getValue());
        try {
            // 窗口显示
            FrameManager.getInstance().openWindow(FXPageEnum.主页);
            FrameManager.getInstance().closeWindow(FXPageEnum.登录页);
            FrameManager.getInstance().closeWindow(FXPageEnum.二维码登录);
        } catch (Exception e) {
            log.error("loginSuccessGoMain e={}", e.getMessage(), e);
        }
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

    /**
     * 是否要删除账户记录
     */
    private void whetherToDeleteAccountRecord(String account) {
        String msg = "是否删除该账号[" + account + "]\n删除后无法恢复!";
        boolean is = FrameManager.getInstance().dialogConfirm("删除账号记录", msg);
        if (!is) {
            return;
        }
        AccountHandler.delActPwd(account, choiceBoxLoginType.getValue());

        refeshAccounts();
    }
}
