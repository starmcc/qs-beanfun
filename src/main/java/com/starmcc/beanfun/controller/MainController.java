package com.starmcc.beanfun.controller;

import com.starmcc.beanfun.api.ThirdPartyApiClient;
import com.starmcc.beanfun.client.BeanfunClient;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.handler.*;
import com.starmcc.beanfun.model.ConfigJson;
import com.starmcc.beanfun.model.QsTray;
import com.starmcc.beanfun.model.client.Account;
import com.starmcc.beanfun.model.client.BeanfunAccountResult;
import com.starmcc.beanfun.model.client.BeanfunStringResult;
import com.starmcc.beanfun.utils.ConfigFileUtils;
import com.starmcc.beanfun.utils.RegexUtils;
import com.starmcc.beanfun.utils.ThreadUtils;
import com.starmcc.beanfun.windows.FrameService;
import com.starmcc.beanfun.windows.WindowService;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
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
    private ChoiceBox<Account> choiceBoxActList;
    @FXML
    private Label labelActPoint;
    @FXML
    private Label labelActStatus;
    @FXML
    private Label labelActCreateTime;
    @FXML
    private TextField textFieldActId;
    @FXML
    private TextField textFieldDynamicPwd;
    @FXML
    private CheckBox checkBoxPassInput;
    @FXML
    private CheckBox checkBoxKillPlayStartWindow;
    @FXML
    private TextField textFieldGamePath;
    @FXML
    private TextField textFieldRmbInput;
    @FXML
    private TextField textFieldXtbInput;
    @FXML
    private Button buttonGetPassword;
    @FXML
    private Button buttonUpdatePoints;
    @FXML
    private Button buttonAddAct;
    @FXML
    private MenuItem menuItemAddAct;
    @FXML
    private Label labelExchangeNow;
    @FXML
    private CheckMenuItem checkMenuItemAlwaysOnTop;
    @FXML
    private CheckMenuItem checkMenuItemAutoLunShao;
    @FXML
    private MenuItem menuItemOfficialTmsUrl;
    @FXML
    private MenuItem menuItemHkNewBeanfunUrl;
    @FXML
    private MenuItem menuItemTwBeanfunUrl;
    @FXML
    private MenuItem menuItemTechbangUrl;
    @FXML
    private MenuItem menuItemBahamuteUrl;
    @FXML
    private MenuItem qstmsUrlMenu;
    @FXML
    private MenuItem tmsTieBaUrlMenu;
    @FXML
    private MenuItem qsbiliUrlMenu;
    @FXML
    private TextField textFieldLunHui;
    @FXML
    private TextField textFieldRanShao;
    @FXML
    private TextField textFieldVideoPath;
    @FXML
    private ChoiceBox<Integer> choiceBoxVideoFps;
    @FXML
    private ComboBox<String> comboBoxVideoCodeRate;
    @FXML
    private CheckBox checkBoxKillGamePatcher;
    @FXML
    private ToggleButton buttonVideo;
    @FXML
    private CheckBox checkBoxAutoInput;

    private Account nowAccount;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ThreadUtils.executeThread(() -> {
            try {
                // 获取账号数据
                refeshAccount(null);
                // 获取游戏点数
                this.updatePoints();
            } catch (Exception e) {
                log.error("error={}", e.getMessage(), e);
            }
        });

        try {
            this.initData();
            this.initEvent();
        } catch (Exception e) {
            log.error("error={}", e.getMessage(), e);
        }
    }

    private void initData() throws Exception {
        // 基础状态设置
        checkBoxPassInput.setSelected(QsConstant.config.getPassInput());
        checkBoxKillPlayStartWindow.setSelected(QsConstant.config.getKillStartPalyWindow());
        textFieldGamePath.setText(QsConstant.config.getGamePath());
        buttonAddAct.setVisible(QsConstant.beanfunModel.isNewAccount());
        menuItemAddAct.setVisible(QsConstant.beanfunModel.isNewAccount());
        checkBoxKillGamePatcher.setVisible(QsConstant.config.getKillGamePatcher());
        checkBoxAutoInput.setSelected(QsConstant.config.getAutoInput());

        // 轮烧配置
        Integer lunHuiKey = QsConstant.config.getLunHuiKey();
        Integer ranShaoKey = QsConstant.config.getRanShaoKey();
        textFieldLunHui.setText(KeyEvent.getKeyText(lunHuiKey));
        textFieldRanShao.setText(KeyEvent.getKeyText(ranShaoKey));

        // 录像配置
        ConfigJson.Video video = QsConstant.config.getVideo();
        ObservableList<Integer> fpsItems = FXCollections.observableArrayList();
        fpsItems.add(30);
        fpsItems.add(60);
        fpsItems.add(90);
        choiceBoxVideoFps.setItems(fpsItems);
        choiceBoxVideoFps.getSelectionModel().select(video.getVideoFps());
        textFieldVideoPath.setText(video.getVideoPath());

        ObservableList<String> codeRateItems = FXCollections.observableArrayList();
        codeRateItems.add("1800");
        codeRateItems.add("2500");
        codeRateItems.add("3500");
        comboBoxVideoCodeRate.setItems(codeRateItems);
        comboBoxVideoCodeRate.getSelectionModel().select(video.getVideoCodeRate());
        comboBoxVideoCodeRate.setValue(String.valueOf(video.getVideoCodeRate()));
    }


    private void initEvent() {
        // =========================== 导航菜单事件 ========================
        FrameService frameService = FrameService.getInstance();
        menuItemOfficialTmsUrl.setOnAction(event -> frameService.openWebUrl("https://maplestory.beanfun.com/main"));
        menuItemHkNewBeanfunUrl.setOnAction(event -> frameService.openWebUrl("https://bfweb.hk.beanfun.com/"));
        menuItemTwBeanfunUrl.setOnAction(event -> frameService.openWebUrl("https://tw.beanfun.com"));
        menuItemTechbangUrl.setOnAction(event -> frameService.openWebUrl("http://gametsg.techbang.com/maplestory/"));
        menuItemBahamuteUrl.setOnAction(event -> frameService.openWebUrl("https://forum.gamer.com.tw/A.php?bsn=7650/"));
        qstmsUrlMenu.setOnAction(event -> frameService.openWebUrl("https://www.qstms.com"));
        tmsTieBaUrlMenu.setOnAction(event -> frameService.openWebUrl("https://tieba.baidu.com/f?kw=%E6%96%B0%E6%9E%AB%E4%B9%8B%E8%B0%B7"));
        qsbiliUrlMenu.setOnAction(event -> frameService.openWebUrl("https://space.bilibili.com/391919722"));


        // =========================== 汇率事件 ========================
        textFieldRmbInput.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (textFieldRmbInput.isFocused()) {
                textFieldXtbInput.setText(CellHandler.cellHuiLv(newValue, 1).toString());
            }
        });
        textFieldXtbInput.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (textFieldXtbInput.isFocused()) {
                textFieldRmbInput.setText(CellHandler.cellHuiLv(newValue, 2).toString());
            }
        });

        // 获取汇率
        ThreadUtils.executeThread(() -> {
            QsConstant.currentRateChinaToTw = ThirdPartyApiClient.getCurrentRateChinaToTw();
            Platform.runLater(() -> labelExchangeNow.setText(QsConstant.currentRateChinaToTw.toString()));
        });

        // =================== 轮烧按键配置事件 =====================

        textFieldLunHui.setOnKeyPressed((keyEvent) -> {
            int code = keyEvent.getCode().impl_getCode();
            if (code == 0) {
                textFieldLunHui.setText("");
                return;
            }
            textFieldLunHui.setText(KeyEvent.getKeyText(code));
            // 记录设置
            QsConstant.config.setLunHuiKey(code);
            ConfigFileUtils.writeConfig(QsConstant.config);
        });

        textFieldRanShao.setOnKeyPressed((keyEvent) -> {
            int code = keyEvent.getCode().impl_getCode();
            if (code == 0) {
                textFieldLunHui.setText("");
                return;
            }
            textFieldRanShao.setText(KeyEvent.getKeyText(code));
            // 记录设置
            QsConstant.config.setRanShaoKey(code);
            ConfigFileUtils.writeConfig(QsConstant.config);
        });


        // =================== 录像事件 =====================
        comboBoxVideoCodeRate.valueProperty().addListener((obsVal, oldVal, newVal) -> {
            // 只能输入数字
            if (!RegexUtils.test(RegexUtils.PTN_NUMBER, newVal)) {
                comboBoxVideoCodeRate.setValue(oldVal);
                return;
            }
            ConfigJson.Video videoTemp = QsConstant.config.getVideo();
            int number = Integer.valueOf(newVal);
            if (number == 0) {
                number = videoTemp.getVideoCodeRate();
            }
            videoTemp.setVideoCodeRate(number);
            comboBoxVideoCodeRate.setValue(String.valueOf(number));
            QsConstant.config.setVideo(videoTemp);
            ConfigFileUtils.writeConfig(QsConstant.config);
        });
    }


    @FXML
    public void exitLoginAction() {
        Platform.runLater(() -> {
            try {
                BeanfunClient.run().loginOut(QsConstant.beanfunModel.getToken());
                FrameService.getInstance().openWindow(QsConstant.Page.登录页面, (jfxStage) -> {
                    jfxStage.setCloseEvent(() -> {
                        Platform.exit();
                    });
                    jfxStage.setMiniSupport(false);
                    QsConstant.loginJFXStage = jfxStage;
                });
                QsTray.remove(QsConstant.trayIcon);
                FrameService.getInstance().closeWindow(QsConstant.mainJFXStage);
            } catch (Exception e) {
                log.error("登出异常 e={}", e.getMessage(), e);
            }
        });
    }

    @FXML
    public void changeAccountNowAction() {
        accountInfoRefresh();
    }


    /**
     * 获取密码操作
     */
    @FXML
    public void getPasswordAction() {
        buttonGetPassword.setDisable(true);
        AccountHandler.getDynamicPassword(this.nowAccount, (id, password) -> {
            Platform.runLater(() -> {
                textFieldDynamicPwd.setText(password);
                buttonGetPassword.setDisable(false);
            });
            // 自动输入
            if (QsConstant.config.getAutoInput() && StringUtils.isNotBlank(password)) {
                try {
                    WindowService.getInstance().autoInputActPwd(id, password);
                } catch (Exception e) {
                    log.error("error={}", e, e.getMessage());
                    Platform.runLater(() -> QsConstant.alert("自动输入异常", Alert.AlertType.ERROR));
                }
            }
        });
    }


    @FXML
    public void startGameAction(ActionEvent actionEvent) {
        if (StringUtils.isBlank(textFieldGamePath.getText())) {
            QsConstant.alert("请配置游戏路径!", Alert.AlertType.INFORMATION);
            gamePathOpenAction(actionEvent);
            return;
        }
        // 启动游戏 如果免输入模式，组装账密

        boolean killStartPalyWindow = BooleanUtils.isTrue(QsConstant.config.getKillStartPalyWindow());
        if (BooleanUtils.isTrue(QsConstant.config.getPassInput())) {
            buttonGetPassword.setDisable(true);
            AccountHandler.getDynamicPassword(this.nowAccount, (id, password) -> {
                GameHandler.runGame(textFieldGamePath.getText(), id, password, killStartPalyWindow);
                Platform.runLater(() -> {
                    textFieldDynamicPwd.setText(password);
                    buttonGetPassword.setDisable(false);
                });
            });
        } else {
            GameHandler.runGame(textFieldGamePath.getText(), null, null, killStartPalyWindow);
        }
    }

    @FXML
    public void gamePathOpenAction(ActionEvent actionEvent) {
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
        if (RegexUtils.test(RegexUtils.PTN_CHINA_STRING, path)) {
            QsConstant.alert("路径中不能包含中文", Alert.AlertType.WARNING);
            return;
        }

        textFieldGamePath.setText(path);
        QsConstant.config.setGamePath(path);
        ConfigFileUtils.writeConfig(QsConstant.config);
    }


    /**
     * 更新游戏点数事件
     */
    @FXML
    public void updatePointsAction(ActionEvent actionEvent) {
        this.updatePoints();
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
        buttonAddAct.setDisable(true);
        ThreadUtils.executeThread(() -> {
            try {
                BeanfunStringResult result = BeanfunClient.run().addAccount(name);
                if (!result.isSuccess()) {
                    Platform.runLater(() -> QsConstant.alert(result.getMsg(), Alert.AlertType.WARNING));
                    return;
                }
                refeshAccount(() -> {
                    QsConstant.alert("创建成功!", Alert.AlertType.INFORMATION);
                    buttonAddAct.setVisible(false);
                });
            } catch (Exception e) {
                log.error("添加账号异常 e={}", e.getMessage(), e);
                Platform.runLater(() -> QsConstant.alert("创建失败!", Alert.AlertType.WARNING));
            } finally {
                buttonAddAct.setDisable(false);
            }
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
        ThreadUtils.executeThread(() -> {
            try {
                BeanfunStringResult result = BeanfunClient.run().changeAccountName(id, newName);
                if (!result.isSuccess()) {
                    Platform.runLater(() -> QsConstant.alert(result.getMsg(), Alert.AlertType.WARNING));
                    return;
                }
                refeshAccount(() -> QsConstant.alert("编辑成功!", Alert.AlertType.INFORMATION));
            } catch (Exception e) {
                log.error("编辑账号异常 e={}", e.getMessage(), e);
            }
        });
    }

    @FXML
    public void memberTopUpAction(ActionEvent actionEvent) throws Exception {
        String jumpUrl = BeanfunClient.run().getWebUrlMemberTopUp(QsConstant.beanfunModel.getToken());
        FrameService.getInstance().openWebBrowser(jumpUrl);
    }


    @FXML
    public void memberCenterAction(ActionEvent actionEvent) throws Exception {
        String jumpUrl = BeanfunClient.run().getWebUrlMemberCenter(QsConstant.beanfunModel.getToken());
        FrameService.getInstance().openWebBrowser(jumpUrl);
    }

    @FXML
    public void serviceCenterAction(ActionEvent actionEvent) throws Exception {
        String jumpUrl = BeanfunClient.run().getWebUrlServiceCenter();
        FrameService.getInstance().openWebBrowser(jumpUrl);
    }

    /**
     * 退出应用程序操作
     *
     * @param actionEvent 行动事件
     */
    @FXML
    public void exitApplicationAction(ActionEvent actionEvent) {
        try {
            BeanfunClient.run().loginOut(QsConstant.beanfunModel.getToken());
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
        QsConstant.mainJFXStage.getStage().setAlwaysOnTop(checkMenuItemAlwaysOnTop.isSelected());
    }


    /**
     * 打开装备计算窗口菜单
     *
     * @param actionEvent 行动事件
     */
    @FXML
    public void openEquipmentCalcWindowMenu(ActionEvent actionEvent) throws Exception {
        FrameService.getInstance().openWindow(QsConstant.Page.装备计算器);
    }

    @FXML
    public void passInputAction(ActionEvent actionEvent) {
        QsConstant.config.setPassInput(checkBoxPassInput.isSelected());
        ConfigFileUtils.writeConfig(QsConstant.config);
    }


    /**
     * 更新汇率
     *
     * @param actionEvent 行动事件
     */
    @FXML
    public void updateRateAction(ActionEvent actionEvent) {
        // 获取汇率
        ThreadUtils.executeThread(() -> {
            QsConstant.currentRateChinaToTw = ThirdPartyApiClient.getCurrentRateChinaToTw();
            Platform.runLater(() -> labelExchangeNow.setText(QsConstant.currentRateChinaToTw.toString()));
        });
    }

    /**
     * 自动轮烧
     *
     * @param actionEvent 行动事件
     */
    @FXML
    public void autoLunShaoAction(ActionEvent actionEvent) {
        // 获取当前状态
        if (checkMenuItemAutoLunShao.isSelected()) {
            // 需要启动
            if (!AutoLunShaoHandler.start()) {
                Platform.runLater(() -> checkMenuItemAutoLunShao.setSelected(false));
            }
        } else {
            // 需要停止
            if (!AutoLunShaoHandler.stop()) {
                Platform.runLater(() -> checkMenuItemAutoLunShao.setSelected(true));
            }
        }
    }

    @FXML
    public void killPlayWindowAction(ActionEvent actionEvent) {
        QsConstant.config.setKillStartPalyWindow(checkBoxKillPlayStartWindow.isSelected());
        ConfigFileUtils.writeConfig(QsConstant.config);
    }

    /**
     * 打开工具窗口操作
     *
     * @param actionEvent 行动事件
     */
    @FXML
    public void openToolsWindowAction(ActionEvent actionEvent) throws Exception {
        FrameService.getInstance().openWindow(QsConstant.Page.关于我, QsConstant.mainJFXStage.getStage());
    }

    @FXML
    public void videoPathOpenAction(ActionEvent actionEvent) throws Exception {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("录像目录");
        File selectedfolder = directoryChooser.showDialog(QsConstant.mainJFXStage.getStage());
        if (Objects.isNull(selectedfolder)) {
            return;
        }
        String path = selectedfolder.getPath();
        if (StringUtils.isBlank(path)) {
            return;
        }
        textFieldVideoPath.setText(path);
        QsConstant.config.getVideo().setVideoPath(path);
        ConfigFileUtils.writeConfig(QsConstant.config);
    }

    @FXML
    public void selectVideoFpsAction(ActionEvent actionEvent) {
        Integer value = choiceBoxVideoFps.getValue();
        QsConstant.config.getVideo().setVideoFps(value);
        ConfigFileUtils.writeConfig(QsConstant.config);
    }

    @FXML
    public void selectVideoCodeRateAction(ActionEvent actionEvent) {
        String value = comboBoxVideoCodeRate.getValue();
        QsConstant.config.getVideo().setVideoCodeRate(Integer.valueOf(value));
        ConfigFileUtils.writeConfig(QsConstant.config);
    }


    @FXML
    public void killGamePatcherAction(ActionEvent actionEvent) {
        QsConstant.config.setKillGamePatcher(checkBoxKillGamePatcher.isSelected());
        ConfigFileUtils.writeConfig(QsConstant.config);
    }

    @FXML
    public void videoAction(ActionEvent actionEvent) {
        // 开始/结束录像
        if (buttonVideo.isSelected()) {
            buttonVideo.setText("录像中");
        } else {
            buttonVideo.setText("开始录像");
        }
        if (!VideoHandler.run(buttonVideo.isSelected())) {
            buttonVideo.setSelected(!buttonVideo.isSelected());
        }
    }


    public void autoInputAction(ActionEvent actionEvent) {
        QsConstant.config.setAutoInput(checkBoxAutoInput.isSelected());
        ConfigFileUtils.writeConfig(QsConstant.config);
    }


    // =============================================== 私有方法 =================================

    /**
     * 初始化账户组合框
     */
    private void refeshAccount(Runnable runnable) throws Exception {
        BeanfunAccountResult actResult = BeanfunClient.run().getAccountList(QsConstant.beanfunModel.getToken());
        if (actResult.isSuccess()) {
            QsConstant.beanfunModel.build(actResult);
        } else {
            Platform.runLater(() -> QsConstant.alert(actResult.getMsg(), Alert.AlertType.ERROR));
            return;
        }

        QsConstant.beanfunModel.build(actResult);

        if (!QsConstant.beanfunModel.isCertStatus()) {
            // 需要进阶认证
            Platform.runLater(() -> QsConstant.alert("请前往用户中心 -> 会员中心进行进阶认证!\n"
                    + "做完进阶认证后请重新退出重新登录!", Alert.AlertType.INFORMATION));
        } else if (QsConstant.beanfunModel.isNewAccount()) {
            // 需要创建账号
            Platform.runLater(() -> QsConstant.alert("新账号请点击创建账号!", Alert.AlertType.INFORMATION));
        }

        Platform.runLater(() -> {
            ObservableList<Account> options = FXCollections.observableArrayList();
            options.clear();
            QsConstant.beanfunModel.getAccountList().forEach(account -> options.add(account));
            choiceBoxActList.setItems(options);
            choiceBoxActList.getSelectionModel().selectFirst();
            this.accountInfoRefresh();
            if (Objects.nonNull(runnable)) {
                runnable.run();
            }
        });
    }


    /**
     * 更新点数
     */
    private void updatePoints() {
        Platform.runLater(() -> {
            buttonUpdatePoints.setDisable(true);
            // 获取游戏点数
            ThreadUtils.executeThread(() -> {
                String pointsText = getPointsText();
                Platform.runLater(() -> {
                    labelActPoint.setText(pointsText);
                    buttonUpdatePoints.setDisable(false);
                });
            });
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
            gamePoints = BeanfunClient.run().getGamePoints(QsConstant.beanfunModel.getToken());
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
    private void accountInfoRefresh() {
        // 保存当前账号
        this.nowAccount = choiceBoxActList.getSelectionModel().getSelectedItem();
        if (Objects.isNull(this.nowAccount)) {
            return;
        }
        // 设置账号状态
        String statusText = BooleanUtils.isTrue(this.nowAccount.getStatus()) ? "正常" : "禁止";
        Color statusColor = BooleanUtils.isTrue(this.nowAccount.getStatus()) ? Color.GREEN : Color.RED;
        labelActStatus.setText(statusText);
        labelActStatus.setTextFill(statusColor);
        if (Objects.nonNull(this.nowAccount.getCreateTime())) {
            String createTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.nowAccount.getCreateTime());
            labelActCreateTime.setText(createTimeStr);
        }
        textFieldActId.setText(this.nowAccount.getId());
        // 获取游戏点数
        this.updatePoints();
    }

}
