package com.starmcc.beanfun.controller;

import com.starmcc.beanfun.api.ThirdPartyApiClient;
import com.starmcc.beanfun.client.BeanfunClient;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.handler.AutoLunShaoHandler;
import com.starmcc.beanfun.handler.CellHandler;
import com.starmcc.beanfun.handler.GameHandler;
import com.starmcc.beanfun.model.Account;
import com.starmcc.beanfun.model.QsTray;
import com.starmcc.beanfun.utils.ConfigFileUtils;
import com.starmcc.beanfun.utils.FrameUtils;
import com.starmcc.beanfun.utils.RegexUtils;
import com.starmcc.beanfun.windows.SwtWebBrowser;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.awt.event.KeyEvent;
import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.ResourceBundle;


/**
 * 主控制器
 *
 * @author starmcc
 * @date 2022/03/19
 */
@Slf4j
public class MainController implements Initializable {


    @FXML
    private ComboBox<Account> actList;
    @FXML
    private Label actPoints;
    @FXML
    private Label actStatus;
    @FXML
    private Label actCreateTime;
    @FXML
    private TextField actId;
    @FXML
    private TextField actDynamicPwd;
    @FXML
    private CheckBox passInput;
    @FXML
    private TextField gamePath;
    @FXML
    private TextField rmbInput;
    @FXML
    private TextField xtbInput;
    @FXML
    private Button getPassword;
    @FXML
    private Button updatePointsBtn;
    @FXML
    private Button addActBtn;
    @FXML
    private MenuItem addActMenu;
    @FXML
    private Label exchangeNow;
    @FXML
    private CheckMenuItem alwaysOnTopMenu;
    @FXML
    private CheckMenuItem autoLunShaoMenu;
    @FXML
    private MenuItem officialTmsUrlMenu;
    @FXML
    private MenuItem hkBeanfunUrlMenu;
    @FXML
    private MenuItem twBeanfunUrlMenu;
    @FXML
    private MenuItem techbangUrlMenu;
    @FXML
    private MenuItem bahamuteUrlMenu;
    @FXML
    private MenuItem qstmsUrlMenu;
    @FXML
    private MenuItem tmsTieBaUrlMenu;
    @FXML
    private MenuItem qsbiliUrlMenu;
    @FXML
    private TextField lunHuiText;
    @FXML
    private TextField ranShaoText;

    private Account nowAccount;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        passInput.setSelected(QsConstant.config.getPassInput());
        gamePath.setText(QsConstant.config.getGamePath());
        addActBtn.setVisible(BeanfunClient.isNewAccount);
        addActMenu.setVisible(BeanfunClient.isNewAccount);
        if (BeanfunClient.isNewAccount) {
            // 新用户
            QsConstant.alert("新账号请点击创建账号!", Alert.AlertType.INFORMATION);
        } else {
            // 旧用户
            initAccountComboBox(null);
        }
        this.initEvent();
    }


    private void initEvent() {
        officialTmsUrlMenu.setOnAction(event -> FrameUtils.openWebUrl("https://maplestory.beanfun.com/main"));
        hkBeanfunUrlMenu.setOnAction(event -> SwtWebBrowser.getInstance("hk.beanfun.com").open());
        twBeanfunUrlMenu.setOnAction(event -> FrameUtils.openWebUrl("https://tw.beanfun.com"));
        techbangUrlMenu.setOnAction(event -> FrameUtils.openWebUrl("http://gametsg.techbang.com/maplestory/"));
        bahamuteUrlMenu.setOnAction(event -> FrameUtils.openWebUrl("https://forum.gamer.com.tw/A.php?bsn=7650/"));
        qstmsUrlMenu.setOnAction(event -> FrameUtils.openWebUrl("https://www.qstms.com"));
        tmsTieBaUrlMenu.setOnAction(event -> FrameUtils.openWebUrl("https://tieba.baidu.com/f?kw=%E6%96%B0%E6%9E%AB%E4%B9%8B%E8%B0%B7"));
        qsbiliUrlMenu.setOnAction(event -> FrameUtils.openWebUrl("https://space.bilibili.com/391919722"));


        // =========================== 初始化汇率相关 ========================
        rmbInput.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (rmbInput.isFocused()) {
                xtbInput.setText(CellHandler.cellHuiLv(newValue, 1).toString());
            }
        });
        xtbInput.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (xtbInput.isFocused()) {
                rmbInput.setText(CellHandler.cellHuiLv(newValue, 2).toString());
            }
        });

        // 获取汇率
        FrameUtils.executeThread(() -> {
            QsConstant.currentRateChinaToTw = ThirdPartyApiClient.getCurrentRateChinaToTw();
            Platform.runLater(() -> exchangeNow.setText(QsConstant.currentRateChinaToTw.toString()));
        });

        // =================== 初始化轮烧按键 =====================

        lunHuiText.setOnKeyPressed((keyEvent) -> {
            int code = keyEvent.getCode().impl_getCode();
            if (code == 0) {
                lunHuiText.setText("");
                return;
            }
            lunHuiText.setText(KeyEvent.getKeyText(code));
            // 记录设置
            QsConstant.config.setLunHuiKey(code);
            ConfigFileUtils.writeConfig(QsConstant.config);
        });
        ranShaoText.setOnKeyPressed((keyEvent) -> {
            int code = keyEvent.getCode().impl_getCode();
            if (code == 0) {
                lunHuiText.setText("");
                return;
            }
            ranShaoText.setText(KeyEvent.getKeyText(code));
            // 记录设置
            QsConstant.config.setRanShaoKey(code);
            ConfigFileUtils.writeConfig(QsConstant.config);
        });

        // 读取配置
        Integer lunHuiKey = QsConstant.config.getLunHuiKey();
        Integer ranShaoKey = QsConstant.config.getRanShaoKey();
        lunHuiText.setText(KeyEvent.getKeyText(lunHuiKey));
        ranShaoText.setText(KeyEvent.getKeyText(ranShaoKey));

    }


    @FXML
    public void exitLoginAction() {
        Platform.runLater(() -> {
            try {
                BeanfunClient.loginOut();
                FrameUtils.openWindow(QsConstant.Page.登录页面, (jfxStage) -> {
                    jfxStage.setCloseEvent(() -> {
                        Platform.exit();
                    });
                    jfxStage.setMiniSupport(false);
                    QsConstant.loginJFXStage = jfxStage;
                });
                QsTray.remove(QsConstant.trayIcon);
                FrameUtils.closeWindow(QsConstant.mainJFXStage);
            } catch (Exception e) {
                log.error("登出异常 e={}", e.getMessage(), e);
            }
        });
    }

    @FXML
    public void changeAccountNowAction() {
        selectAccount();
    }


    /**
     * 获取密码操作
     */
    @FXML
    public void getPasswordAction() {
        getDynamicPassword(null);
    }


    @FXML
    public void startGameAction(ActionEvent actionEvent) {
        if (StringUtils.isBlank(gamePath.getText())) {
            QsConstant.alert("请配置游戏路径!", Alert.AlertType.INFORMATION);
            gamePathOpen(actionEvent);
            return;
        }
        // 启动游戏 如果免输入模式，组装账密
        if (BooleanUtils.isTrue(QsConstant.config.getPassInput())) {
            getDynamicPassword(() -> GameHandler.runGame(gamePath.getText(), this.nowAccount.getId(), actDynamicPwd.getText()));
        } else {
            GameHandler.runGame(gamePath.getText(), null, null);
        }
    }

    @FXML
    public void gamePathOpen(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("新枫之谷启动程序(MapleStory.exe)", "MapleStory.exe");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(QsConstant.mainJFXStage.getStage());
        if (Objects.isNull(file)) {
            return;
        }
        String path = file.getPath();
        if (StringUtils.isBlank(path)) {
            return;
        }
        // 判断中文路径
        if (RegexUtils.test(RegexUtils.PTN_CHINA_PATH, path)) {
            QsConstant.alert("路径中不能包含中文", Alert.AlertType.WARNING);
            return;
        }

        gamePath.setText(path);
        QsConstant.config.setGamePath(path);
        ConfigFileUtils.writeConfig(QsConstant.config);
    }


    /**
     * 更新游戏点数事件
     */
    @FXML
    public void updatePointsAction(ActionEvent actionEvent) {
        updatePointsBtn.setDisable(true);
        // 获取游戏点数
        FrameUtils.executeThread(() -> {
            String pointsText = getPointsText();
            Platform.runLater(() -> {
                actPoints.setText(pointsText);
                updatePointsBtn.setDisable(false);
            });
        });
    }


    /**
     * 添加账号事件
     */
    @FXML
    public void addActAction(ActionEvent actionEvent) {
        String name = QsConstant.textDialog("添加账号", "");
        if (StringUtils.isBlank(name)) {
            return;
        }
        addActBtn.setDisable(true);
        FrameUtils.executeThread(() -> {
            try {
                if (BeanfunClient.addAccount(name)) {
                    BeanfunClient.getAccountList();
                    initAccountComboBox(() -> {
                        QsConstant.alert("创建成功!", Alert.AlertType.INFORMATION);
                        addActBtn.setVisible(false);
                    });
                    return;
                }
            } catch (Exception e) {
                log.error("添加账号异常 e={}", e.getMessage(), e);
            }
            Platform.runLater(() -> QsConstant.alert("创建失败!", Alert.AlertType.WARNING));

            addActBtn.setDisable(false);
        });


    }

    /**
     * 编辑账号事件
     */
    @FXML
    public void editActAction(ActionEvent actionEvent) {
        String newName = QsConstant.textDialog("编辑账号", this.nowAccount.getName());
        if (StringUtils.isBlank(newName)) {
            return;
        }
        String id = this.nowAccount.getId();
        FrameUtils.executeThread(() -> {
            try {
                if (BeanfunClient.changeAccountName(id, newName)) {
                    BeanfunClient.getAccountList();
                    initAccountComboBox(() -> QsConstant.alert("编辑成功!", Alert.AlertType.INFORMATION));
                    return;
                }
            } catch (Exception e) {
                log.error("编辑账号异常 e={}", e.getMessage(), e);
            }
            Platform.runLater(() -> QsConstant.alert("编辑失败!", Alert.AlertType.WARNING));
        });
    }

    @FXML
    public void memberTopUpAction(ActionEvent actionEvent) {
        FrameUtils.executeThread(() -> SwtWebBrowser.getInstance(BeanfunClient.getWebUrlMemberTopUp()).open());
    }


    @FXML
    public void memberCenterAction(ActionEvent actionEvent) {
        FrameUtils.executeThread(() -> SwtWebBrowser.getInstance(BeanfunClient.getWebUrlMemberCenter()).open());
    }

    @FXML
    public void serviceCenterAction(ActionEvent actionEvent) {
        FrameUtils.executeThread(() -> SwtWebBrowser.getInstance(BeanfunClient.getWebUrlServiceCenter()).open());
    }

    /**
     * 退出应用程序操作
     *
     * @param actionEvent 行动事件
     */
    @FXML
    public void exitApplicationAction(ActionEvent actionEvent) {
        try {
            BeanfunClient.loginOut();
        } catch (Exception e) {
            log.error("退出登录异常 e={}", e.getMessage(), e);
        }
        Platform.exit();
    }

    /**
     * 窗口置顶
     *
     * @param actionEvent 行动事件
     */
    @FXML
    public void alwaysOnTopAction(ActionEvent actionEvent) {
        QsConstant.mainJFXStage.getStage().setAlwaysOnTop(alwaysOnTopMenu.isSelected());
    }


    /**
     * 打开装备计算窗口菜单
     *
     * @param actionEvent 行动事件
     */
    @FXML
    public void openEquipmentCalcWindowMenu(ActionEvent actionEvent) throws Exception {
        FrameUtils.openWindow(QsConstant.Page.装备计算器, jfxStage -> {
            jfxStage.setMiniSupport(false);
            jfxStage.setCloseEvent(() -> FrameUtils.closeWindow(jfxStage));
            QsConstant.equippingJFXStage = jfxStage;
        });
    }

    @FXML
    public void passInputAction(ActionEvent actionEvent) {
        QsConstant.config.setPassInput(passInput.isSelected());
        ConfigFileUtils.writeConfig(QsConstant.config);
    }


    /**
     * 更新汇率菜单
     *
     * @param actionEvent 行动事件
     */
    @FXML
    public void updateRateMenu(ActionEvent actionEvent) {
        // 获取汇率
        FrameUtils.executeThread(() -> {
            QsConstant.currentRateChinaToTw = ThirdPartyApiClient.getCurrentRateChinaToTw();
            Platform.runLater(() -> exchangeNow.setText(QsConstant.currentRateChinaToTw.toString()));
        });
    }


    /**
     * 设置ie兼容视图操作
     *
     * @param iEcompatibilityAction 我ecompatibility行动
     */
    @FXML
    public void setIEcompatibilityViewAction(ActionEvent iEcompatibilityAction) {
        try {
            String regisFilePath = QsConstant.Resources.REGISTER_REG.getTargetPath();
            String[] cmd = {"regedit", "/s", regisFilePath};
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            QsConstant.alert("设置成功!", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            log.error("设置ie兼容视图 发生异常 e={}", e.getMessage(), e);
            QsConstant.alert(e.getMessage(), Alert.AlertType.ERROR);
        }


    }


    /**
     * 自动轮烧
     *
     * @param actionEvent 行动事件
     */
    @FXML
    public void autoLunShaoAction(ActionEvent actionEvent) {
        // 获取当前状态
        if (autoLunShaoMenu.isSelected()) {
            // 需要启动
            if (!AutoLunShaoHandler.start()) {
                Platform.runLater(() -> autoLunShaoMenu.setSelected(false));
            }
        } else {
            // 需要停止
            if (!AutoLunShaoHandler.stop()) {
                Platform.runLater(() -> autoLunShaoMenu.setSelected(true));
            }
        }
    }


    /**
     * 打开工具窗口操作
     *
     * @param actionEvent 行动事件
     */
    public void openToolsWindowAction(ActionEvent actionEvent) throws Exception {
        FrameUtils.openWindow(QsConstant.Page.关于我, QsConstant.mainJFXStage.getStage(), jfxStage -> {
            jfxStage.setCloseEvent(() -> FrameUtils.closeWindow(jfxStage));
            jfxStage.setMiniSupport(false);
            QsConstant.aboutJFXStage = jfxStage;
        });
    }

    // =============================================== 私有方法 =================================

    /**
     * 初始化账户组合框
     */
    private void initAccountComboBox(Runnable runnable) {
        Platform.runLater(() -> {
            ObservableList<Account> options = FXCollections.observableArrayList();
            BeanfunClient.accountList.forEach(account -> options.add(account));
            actList.setItems(options);
            actList.getSelectionModel().selectFirst();
            if (Objects.nonNull(runnable)) {
                runnable.run();
            }
        });
    }


    /**
     * 获取游戏点数文本
     *
     * @return {@link String}
     */
    private String getPointsText() {
        log.debug("正在获取游戏点数...");
        String template = "{0}点[游戏内:{1}]";
        int gamePoints = 0;
        try {
            gamePoints = BeanfunClient.getGamePoints();
        } catch (Exception e) {
            log.error("获取游戏点数异常 e={}", e.getMessage(), e);
        }
        if (gamePoints == 0) {
            return MessageFormat.format(template, 0, 0);
        }
        BigDecimal points = new BigDecimal(gamePoints);
        BigDecimal divide = points.divide(new BigDecimal("2.5"), 2, BigDecimal.ROUND_DOWN);
        return MessageFormat.format(template, gamePoints, divide.intValue());
    }


    /**
     * 选择账户
     */
    private void selectAccount() {
        SingleSelectionModel<Account> selectionModel = actList.getSelectionModel();
        // 保存当前账号
        this.nowAccount = selectionModel.getSelectedItem();
        if (Objects.isNull(this.nowAccount)) {
            return;
        }
        // 设置账号状态
        String statusText = BooleanUtils.isTrue(this.nowAccount.getStatus()) ? "正常" : "禁止";
        Color statusColor = BooleanUtils.isTrue(this.nowAccount.getStatus()) ? Color.GREEN : Color.RED;
        actStatus.setText(statusText);
        actStatus.setTextFill(statusColor);
        if (Objects.nonNull(this.nowAccount.getCreateTime())) {
            String createTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.nowAccount.getCreateTime());
            actCreateTime.setText(createTimeStr);
        }
        actId.setText(this.nowAccount.getId());
        updatePointsBtn.setDisable(true);
        // 获取游戏点数
        FrameUtils.executeThread(() -> {
            try {
                // 2秒后再执行
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.error("线程异常 e={}", e.getMessage(), e);
            }
            String pointsText = getPointsText();
            Platform.runLater(() -> {
                actPoints.setText(pointsText);
                updatePointsBtn.setDisable(false);
            });
        });
    }


    /**
     * 获取动态密码
     *
     * @param runnable 可运行
     */
    private void getDynamicPassword(Runnable runnable) {
        getPassword.setDisable(true);
        FrameUtils.executeThread(() -> {
            try {
                String dynamicPassword = BeanfunClient.getDynamicPassword(this.nowAccount);
                if (StringUtils.isBlank(dynamicPassword)) {
                    Platform.runLater(() -> QsConstant.alert(BeanfunClient.errorMsg, Alert.AlertType.ERROR));
                    return;
                }
                actDynamicPwd.setText(dynamicPassword);
                log.debug("动态密码 ={}", dynamicPassword);
                Platform.runLater(() -> getPassword.setDisable(false));
                if (Objects.nonNull(runnable)) {
                    runnable.run();
                }
            } catch (Exception e) {
                log.error("获取密码失败 e={}", e.getMessage(), e);
                Platform.runLater(() -> QsConstant.alert("获取动态密码异常:" + e.getMessage(), Alert.AlertType.ERROR));
            }
        });
    }

}
