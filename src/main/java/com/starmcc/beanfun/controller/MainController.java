package com.starmcc.beanfun.controller;

import com.starmcc.beanfun.client.BeanfunClient;
import com.starmcc.beanfun.client.ThirdPartyApiClient;
import com.starmcc.beanfun.constant.FXPageEnum;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.entity.client.Account;
import com.starmcc.beanfun.entity.client.BeanfunAccountResult;
import com.starmcc.beanfun.entity.client.BeanfunStringResult;
import com.starmcc.beanfun.entity.model.ConfigModel;
import com.starmcc.beanfun.entity.model.LoadingPage;
import com.starmcc.beanfun.entity.model.QsTray;
import com.starmcc.beanfun.handler.*;
import com.starmcc.beanfun.manager.AdvancedTimerMamager;
import com.starmcc.beanfun.manager.FrameManager;
import com.starmcc.beanfun.manager.ThreadPoolManager;
import com.starmcc.beanfun.manager.WindowManager;
import com.starmcc.beanfun.manager.impl.AdvancedTimerTask;
import com.starmcc.beanfun.utils.FileTools;
import com.starmcc.beanfun.utils.RegexUtils;
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
import org.apache.commons.lang3.ArrayUtils;
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
 * 主页面控制器
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
    private ToggleButton buttonRecordVideo;
    @FXML
    private CheckBox checkBoxAutoInput;
    @FXML
    private RadioButton radioButtonGame;
    @FXML
    private RadioButton radioButtonScreen;
    @FXML
    private TextField textFieldFFmpegPath;
    @FXML
    private MenuItem menuItemEquipment;
    @FXML
    private MenuItem menuItemNgs;
    @FXML
    private MenuItem menuItemSystemCalc;
    @FXML
    private MenuItem menuItemPaperDoll;
    @FXML
    private MenuItem menuItemAlliance;
    @FXML
    private MenuItem menuItemExit;
    @FXML
    private CheckBox checkBoxCheckAppUpdate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 加载页面
        FrameManager.getInstance().runLater(() -> {
            // 托盘菜单
            QsConstant.trayIcon = QsTray.init(QsConstant.JFX_STAGE_DATA.get(FXPageEnum.主页).getStage());
            QsTray.show(QsConstant.trayIcon);
            // 获取账号数据
            LoadingPage.task(FXPageEnum.主页, label -> {
                label.setText("加载账号信息..");
                refeshAccounts(null);
            });
            try {
                this.initEvent();
                this.initData();
            } catch (Exception e) {
                log.error("error={}", e.getMessage(), e);
            }
            // 开始心跳 5分钟心跳一次保持登录状态
            final int delay = 1000 * 60 * 5;
            // 10分钟后开始
            final int waitTime = 1000 * 60 * 10;
            AdvancedTimerMamager.getInstance().addTask(new AdvancedTimerTask() {
                @Override
                public void start() throws Exception {
                    BeanfunClient.run().heartbeat();
                }
            }, waitTime, delay);
        });


    }

    /**
     * 初始化数据
     *
     * @throws Exception 异常
     */
    private void initData() throws Exception {
        // 基础状态设置
        checkBoxPassInput.setSelected(BooleanUtils.isTrue(QsConstant.config.getPassInput()));
        checkBoxKillPlayStartWindow.setSelected(BooleanUtils.isTrue(QsConstant.config.getKillStartPalyWindow()));
        textFieldGamePath.setText(QsConstant.config.getGamePath());
        checkBoxKillGamePatcher.setSelected(BooleanUtils.isTrue(QsConstant.config.getKillGamePatcher()));
        checkBoxAutoInput.setSelected(BooleanUtils.isTrue(QsConstant.config.getAutoInput()));
        checkBoxCheckAppUpdate.setSelected(BooleanUtils.isTrue(QsConstant.config.getCheckAppUpdate()));


        // 轮烧配置
        Integer lunHuiKey = QsConstant.config.getLunHuiKey();
        Integer ranShaoKey = QsConstant.config.getRanShaoKey();
        textFieldLunHui.setText(KeyEvent.getKeyText(lunHuiKey));
        textFieldRanShao.setText(KeyEvent.getKeyText(ranShaoKey));

        // 录像配置
        ConfigModel.RecordVideo recordVideo = QsConstant.config.getRecordVideo();
        ObservableList<Integer> fpsItems = FXCollections.observableArrayList();
        fpsItems.add(30);
        fpsItems.add(60);
        fpsItems.add(90);
        choiceBoxVideoFps.setItems(fpsItems);
        choiceBoxVideoFps.getSelectionModel().select(recordVideo.getFps());
        textFieldVideoPath.setText(recordVideo.getFolder());

        ObservableList<String> codeRateItems = FXCollections.observableArrayList();
        codeRateItems.add("1800");
        codeRateItems.add("2500");
        codeRateItems.add("3500");
        comboBoxVideoCodeRate.setItems(codeRateItems);
        comboBoxVideoCodeRate.getSelectionModel().select(recordVideo.getCodeRate());
        comboBoxVideoCodeRate.setValue(String.valueOf(recordVideo.getCodeRate()));

        Integer captureType = recordVideo.getCaptureType();
        if (Objects.equals(captureType, ConfigModel.RecordVideo.CaptureTypeEnum.游戏窗口.getType())) {
            radioButtonGame.setSelected(true);
        } else {
            radioButtonScreen.setSelected(true);
        }
        textFieldFFmpegPath.setText(recordVideo.getFfmpegPath());
    }


    /**
     * 初始化事件
     */
    private void initEvent() {
        // =========================== 配置事件 ===========================
        checkBoxKillGamePatcher.setOnAction(event -> {
            QsConstant.config.setKillGamePatcher(checkBoxKillGamePatcher.isSelected());
            FileTools.saveConfig(QsConstant.config);
        });
        checkBoxPassInput.setOnAction(event -> {
            QsConstant.config.setPassInput(checkBoxPassInput.isSelected());
            FileTools.saveConfig(QsConstant.config);
        });
        checkBoxKillPlayStartWindow.setOnAction(event -> {
            QsConstant.config.setKillStartPalyWindow(checkBoxKillPlayStartWindow.isSelected());
            FileTools.saveConfig(QsConstant.config);
        });
        checkBoxCheckAppUpdate.setOnAction(event -> {
            QsConstant.config.setCheckAppUpdate(checkBoxCheckAppUpdate.isSelected());
            FileTools.saveConfig(QsConstant.config);
        });
        // =========================== 导航菜单控件事件 ========================
        menuItemOfficialTmsUrl.setOnAction(event -> FrameManager.getInstance().openWebUrl("https://maplestory.beanfun.com/main"));
        menuItemHkNewBeanfunUrl.setOnAction(event -> FrameManager.getInstance().openWebUrl("https://bfweb.hk.beanfun.com/"));
        menuItemTwBeanfunUrl.setOnAction(event -> FrameManager.getInstance().openWebUrl("https://tw.beanfun.com"));
        menuItemTechbangUrl.setOnAction(event -> FrameManager.getInstance().openWebUrl("http://gametsg.techbang.com/maplestory/"));
        menuItemBahamuteUrl.setOnAction(event -> FrameManager.getInstance().openWebUrl("https://forum.gamer.com.tw/A.php?bsn=7650/"));
        qstmsUrlMenu.setOnAction(event -> FrameManager.getInstance().openWebUrl("https://www.qstms.com"));
        tmsTieBaUrlMenu.setOnAction(event -> FrameManager.getInstance().openWebUrl("https://tieba.baidu.com/f?kw=%E6%96%B0%E6%9E%AB%E4%B9%8B%E8%B0%B7"));
        qsbiliUrlMenu.setOnAction(event -> FrameManager.getInstance().openWebUrl("https://space.bilibili.com/391919722"));
        menuItemEquipment.setOnAction(event -> {
            try {
                FrameManager.getInstance().openWindow(FXPageEnum.装备计算器);
            } catch (Exception e) {
                log.error("equipment open error e={}", e.getMessage(), e);
                FrameManager.getInstance().message("打开装备窗失败!", Alert.AlertType.ERROR);
            }
        });
        menuItemNgs.setOnAction(event -> {
            if (FrameManager.getInstance().dialogConfirm("结束NGS", "是否结束NGS进程?")) {
                WindowManager.getInstance().killBlackXchg();
            }
        });
        menuItemSystemCalc.setOnAction(event -> WindowManager.getInstance().openSystemCalc());

        menuItemExit.setOnAction(event -> {
            try {
                BeanfunClient.run().loginOut(QsConstant.beanfunModel.getToken());
            } catch (Exception e) {
                log.error("退出登录异常 e={}", e.getMessage(), e);
            }
            FrameManager.getInstance().exit();
        });

        menuItemPaperDoll.setOnAction(event -> QsConstant.PluginEnum.MAPLESTORY_EMULATOR.run());

        menuItemAlliance.setOnAction(event -> QsConstant.PluginEnum.WAR_ALLIANCE_HTML.run());


        // =========================== 汇率控件事件 ========================
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
        ThreadPoolManager.execute(() -> {
            QsConstant.currentRateChinaToTw = ThirdPartyApiClient.getCurrentRateChinaToTw();
            FrameManager.getInstance().runLater(() -> labelExchangeNow.setText(QsConstant.currentRateChinaToTw.toString()));
        });

        // =================== 轮烧按键配置控件事件 =====================

        textFieldLunHui.setOnKeyPressed((keyEvent) -> {
            int code = keyEvent.getCode().impl_getCode();
            if (code == 0) {
                textFieldLunHui.setText("");
                return;
            }
            textFieldLunHui.setText(KeyEvent.getKeyText(code));
            // 记录设置
            QsConstant.config.setLunHuiKey(code);
            FileTools.saveConfig(QsConstant.config);
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
            FileTools.saveConfig(QsConstant.config);
        });


        // =================== 录像控件事件 =====================
        comboBoxVideoCodeRate.valueProperty().addListener((obsVal, oldVal, newVal) -> {
            // 只能输入数字
            if (!RegexUtils.test(RegexUtils.Constant.COMMON_NUMBER, newVal)) {
                comboBoxVideoCodeRate.setValue(oldVal);
                return;
            }
            ConfigModel.RecordVideo recordVideoTemp = QsConstant.config.getRecordVideo();
            int number = Integer.valueOf(newVal);
            if (number == 0) {
                number = recordVideoTemp.getCodeRate();
            }
            recordVideoTemp.setCodeRate(number);
            comboBoxVideoCodeRate.setValue(String.valueOf(number));
            QsConstant.config.setRecordVideo(recordVideoTemp);
            FileTools.saveConfig(QsConstant.config);
        });

        ToggleGroup group = new ToggleGroup();
        radioButtonGame.setToggleGroup(group);
        radioButtonScreen.setToggleGroup(group);
        radioButtonGame.setOnAction(event -> {
            QsConstant.config.getRecordVideo().setCaptureType(ConfigModel.RecordVideo.CaptureTypeEnum.游戏窗口.getType());
            FileTools.saveConfig(QsConstant.config);
        });
        radioButtonScreen.setOnAction(event -> {
            QsConstant.config.getRecordVideo().setCaptureType(ConfigModel.RecordVideo.CaptureTypeEnum.全屏.getType());
            FileTools.saveConfig(QsConstant.config);
        });
    }


    @FXML
    public void exitLoginAction() {
        LoadingPage.task(FXPageEnum.主页, label -> {
            label.setText("正在退出登录..");
            BeanfunClient.run().loginOut(QsConstant.beanfunModel.getToken());
            FrameManager.getInstance().runLater(() -> {
                FrameManager.getInstance().openWindow(FXPageEnum.登录页);
                FrameManager.getInstance().closeWindow(FXPageEnum.主页);
            });
        });
    }

    @FXML
    public void changeAccountNowAction() {
        LoadingPage.task(FXPageEnum.主页, label -> {
            label.setText("变更账户..");
            this.accountInfoRefresh();
        });
    }


    /**
     * 获取密码操作
     */
    @FXML
    public void getDynamicPasswordAction() {
        LoadingPage.task(FXPageEnum.主页, label -> {
            label.setText("获取动态密码..");
            AccountHandler.getDynamicPassword(QsConstant.nowAccount, (id, password) -> {
                FrameManager.getInstance().runLater(() -> textFieldDynamicPwd.setText(password));
                // 自动输入 并检查游戏是否存在
                if (BooleanUtils.isTrue(QsConstant.config.getAutoInput())
                        && StringUtils.isNotBlank(password)
                        && WindowManager.getInstance().checkMapleStoryRunning()) {
                    try {
                        WindowManager.getInstance().autoInputActPwd(id, password);
                    } catch (Exception e) {
                        log.error("error={}", e, e.getMessage());
                        FrameManager.getInstance().message("自动输入异常", Alert.AlertType.ERROR);
                    }
                }
            });
        });
    }


    @FXML
    public void startGameAction(ActionEvent actionEvent) {
        if (StringUtils.isBlank(textFieldGamePath.getText())) {
            FrameManager.getInstance().messageSync("请配置游戏路径!", Alert.AlertType.INFORMATION);
            gamePathOpenAction(actionEvent);
            return;
        } else if (!new File(textFieldGamePath.getText()).exists()) {
            // 文件不存在重新选择目录
            gamePathOpenAction(actionEvent);
            return;
        }
        // 检查VC环境是否安装
        if (!WindowManager.getInstance().checkVcRuntimeEnvironment()) {
            FrameManager.getInstance().messageSync("请安装VC环境!", Alert.AlertType.INFORMATION);
            boolean goDownload = FrameManager.getInstance().dialogConfirm("VcRuntime Error", "模拟繁体环境需要拥有VC环境,是否前往下载并安装?");
            if (goDownload) {
                FrameManager.getInstance().openWebUrl("https://aka.ms/vs/17/release/vc_redist.x64.exe");
            }
            return;
        }
        // 启动游戏 如果免输入模式，组装账密
        if (BooleanUtils.isTrue(QsConstant.config.getPassInput())) {
            LoadingPage.task(FXPageEnum.主页, label -> {
                label.setText("正在获取动态密码...");
                try {
                    AccountHandler.getDynamicPassword(QsConstant.nowAccount, (id, password) -> {
                        GameHandler.runGame(textFieldGamePath.getText(), id, password);
                        FrameManager.getInstance().runLater(() -> {
                            textFieldDynamicPwd.setText(password);
                        });
                    });
                } catch (Exception e) {
                    log.error("获取密码失败 e={}", e.getMessage(), e);
                    FrameManager.getInstance().message("获取动态密码异常:" + e.getMessage(), Alert.AlertType.ERROR);
                }
            });
        } else {
            GameHandler.runGame(textFieldGamePath.getText(), null, null);
        }
    }

    @FXML
    public void gamePathOpenAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("新枫之谷启动程序(MapleStory.exe)", "MapleStory.exe");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(QsConstant.JFX_STAGE_DATA.get(FXPageEnum.主页).getStage());
        if (Objects.isNull(file)) {
            return;
        }
        String path = file.getPath();
        if (StringUtils.isBlank(path)) {
            return;
        }
        // 判断中文路径
        if (RegexUtils.test(RegexUtils.Constant.COMMON_CHINA_STRING, path)) {
            FrameManager.getInstance().messageSync("路径中不能包含中文!", Alert.AlertType.WARNING);
            return;
        }

        textFieldGamePath.setText(path);
        QsConstant.config.setGamePath(path);
        FileTools.saveConfig(QsConstant.config);
    }


    /**
     * 更新游戏点数事件
     */
    @FXML
    public void updatePointsAction(ActionEvent actionEvent) {
        LoadingPage.task(FXPageEnum.主页, label -> {
            label.setText("获取游戏点数...");
            // 获取游戏点数
            String pointsText = getPointsText();
            FrameManager.getInstance().runLater(() -> labelActPoint.setText(pointsText));
        });
    }


    /**
     * 添加账号事件
     */
    @FXML
    public void addActAction(ActionEvent actionEvent) {
        String name = FrameManager.getInstance().dialogText("添加账号", "");
        if (StringUtils.isBlank(name)) {
            return;
        }
        LoadingPage.task(FXPageEnum.主页, label -> {
            label.setText("添加账号..");
            try {
                BeanfunStringResult result = BeanfunClient.run().addAccount(name);
                if (result.isSuccess()) {
                    refeshAccounts(() -> FrameManager.getInstance().runLater(() -> buttonAddAct.setVisible(false)));
                } else {
                    FrameManager.getInstance().message(result.getMsg(), Alert.AlertType.WARNING);
                }
            } catch (Exception e) {
                log.error("添加账号异常 e={}", e.getMessage(), e);
                FrameManager.getInstance().message("创建失败!", Alert.AlertType.WARNING);
            }
        });
    }

    /**
     * 编辑账号事件
     */
    @FXML
    public void editActAction(ActionEvent actionEvent) {
        String newName = FrameManager.getInstance().dialogText("编辑账号", QsConstant.nowAccount.getName());
        if (StringUtils.isBlank(newName)) {
            return;
        }
        LoadingPage.task(FXPageEnum.主页, label -> {
            label.setText("编辑账号..");
            try {
                BeanfunStringResult result = BeanfunClient.run().changeAccountName(QsConstant.nowAccount.getId(), newName);
                if (!result.isSuccess()) {
                    FrameManager.getInstance().message(result.getMsg(), Alert.AlertType.WARNING);
                    return;
                }
                refeshAccounts(null);
            } catch (Exception e) {
                log.error("编辑账号异常 e={}", e.getMessage(), e);
            }
        });
    }

    @FXML
    public void memberTopUpAction(ActionEvent actionEvent) throws Exception {
        String jumpUrl = BeanfunClient.run().getWebUrlMemberTopUp(QsConstant.beanfunModel.getToken());
        FrameManager.getInstance().openWebBrowser(jumpUrl);
    }


    @FXML
    public void memberCenterAction(ActionEvent actionEvent) throws Exception {
        String jumpUrl = BeanfunClient.run().getWebUrlMemberCenter(QsConstant.beanfunModel.getToken());
        FrameManager.getInstance().openWebBrowser(jumpUrl);
    }

    @FXML
    public void serviceCenterAction(ActionEvent actionEvent) throws Exception {
        String jumpUrl = BeanfunClient.run().getWebUrlServiceCenter();
        FrameManager.getInstance().openWebBrowser(jumpUrl);
    }


    /**
     * 窗口置顶
     *
     * @param actionEvent 行动事件
     */
    @FXML
    public void alwaysOnTopAction(ActionEvent actionEvent) {
        QsConstant.JFX_STAGE_DATA.get(FXPageEnum.主页).getStage().setAlwaysOnTop(checkMenuItemAlwaysOnTop.isSelected());
    }


    /**
     * 更新汇率
     *
     * @param actionEvent 行动事件
     */
    @FXML
    public void updateRateAction(ActionEvent actionEvent) {
        // 获取汇率
        LoadingPage.task(FXPageEnum.主页, label -> {
            label.setText("更新汇率..");
            QsConstant.currentRateChinaToTw = ThirdPartyApiClient.getCurrentRateChinaToTw();
            FrameManager.getInstance().runLater(() -> labelExchangeNow.setText(QsConstant.currentRateChinaToTw.toString()));
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
                FrameManager.getInstance().runLater(() -> checkMenuItemAutoLunShao.setSelected(false));
            }
        } else {
            // 需要停止
            if (!AutoLunShaoHandler.stop()) {
                FrameManager.getInstance().runLater(() -> checkMenuItemAutoLunShao.setSelected(true));
            }
        }
    }

    /**
     * 打开工具窗口操作
     *
     * @param actionEvent 行动事件
     */
    @FXML
    public void openToolsWindowAction(ActionEvent actionEvent) throws Exception {
        FrameManager.getInstance().openWindow(FXPageEnum.关于我, FXPageEnum.主页);
    }

    @FXML
    public void videoPathOpenAction(ActionEvent actionEvent) throws Exception {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("录像目录");
        File selectedfolder = directoryChooser.showDialog(QsConstant.JFX_STAGE_DATA.get(FXPageEnum.主页).getStage());
        if (Objects.isNull(selectedfolder)) {
            return;
        }
        String path = selectedfolder.getPath();
        if (StringUtils.isBlank(path)) {
            return;
        }
        textFieldVideoPath.setText(path);
        QsConstant.config.getRecordVideo().setFolder(path);
        FileTools.saveConfig(QsConstant.config);
    }

    @FXML
    public void selectVideoFpsAction(ActionEvent actionEvent) {
        Integer value = choiceBoxVideoFps.getValue();
        QsConstant.config.getRecordVideo().setFps(value);
        FileTools.saveConfig(QsConstant.config);
    }

    @FXML
    public void selectVideoCodeRateAction(ActionEvent actionEvent) {
        String value = comboBoxVideoCodeRate.getValue();
        QsConstant.config.getRecordVideo().setCodeRate(Integer.valueOf(value));
        FileTools.saveConfig(QsConstant.config);
    }


    @FXML
    public void recordVideoAction(ActionEvent actionEvent) {
        if (Objects.equals(QsConstant.config.getRecordVideo().getCaptureType(), ConfigModel.RecordVideo.CaptureTypeEnum.游戏窗口.getType())
                && buttonRecordVideo.isSelected()) {
            // 如果是录制游戏窗口，且是开始录制状态，则检查游戏运行状态
            boolean is = WindowManager.getInstance().checkMapleStoryRunning();
            if (!is) {
                // 如果游戏不存在，则不进行录制
                buttonRecordVideo.setSelected(!buttonRecordVideo.isSelected());
                FrameManager.getInstance().messageSync("游戏并未运行,请运行游戏后再尝试录制!", Alert.AlertType.WARNING);
                return;
            }
        }

        // 检查ffmpeg.exe是否存在
        File file = new File(QsConstant.config.getRecordVideo().getFfmpegPath());
        if (!file.exists()) {
            buttonRecordVideo.setSelected(!buttonRecordVideo.isSelected());
            if (FrameManager.getInstance().dialogConfirm("缺少依赖", "自动录像功能需要Ffmpeg.exe支持\n是否前往下载?")) {
                FrameManager.getInstance().openWebUrl("https://ffmpeg.org/download.html");
            }
            return;
        }

        // 开始/结束录像
        if (buttonRecordVideo.isSelected()) {
            // 开始录制
            buttonRecordVideo.setText("录像中");
        } else {
            buttonRecordVideo.setText("开始录像");
        }

        RecordVideoHandler.run(buttonRecordVideo.isSelected());
    }

    @FXML
    public void autoInputAction(ActionEvent actionEvent) {
        QsConstant.config.setAutoInput(checkBoxAutoInput.isSelected());
        FileTools.saveConfig(QsConstant.config);
    }

    @FXML
    public void clearRecordVideoAction(ActionEvent actionEvent) {
        String folder = QsConstant.config.getRecordVideo().getFolder();
        File file = new File(folder);
        if (!file.exists()) {
            return;
        }
        File[] files = file.listFiles(pathname -> pathname.getName().endsWith(".mp4"));
        if (ArrayUtils.isEmpty(files)) {
            return;
        }
        int delNum = 0;
        for (File f : files) {
            delNum = f.delete() ? delNum++ : delNum;
        }
        FrameManager.getInstance().messageSync("已清理" + delNum + "个录像", Alert.AlertType.INFORMATION);
    }


    @FXML
    public void ffmpegOpenAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("FFmpeg.exe(*.exe)", "*.exe");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(QsConstant.JFX_STAGE_DATA.get(FXPageEnum.主页).getStage());
        if (Objects.isNull(file)) {
            return;
        }
        String path = file.getPath();
        if (StringUtils.isBlank(path)) {
            return;
        }
        textFieldFFmpegPath.setText(path);
        QsConstant.config.getRecordVideo().setFfmpegPath(path);
        FileTools.saveConfig(QsConstant.config);
    }

    @FXML
    public void downloadFFmpegAction(ActionEvent actionEvent) {
        FrameManager.getInstance().openWebUrl("https://ffmpeg.org/download.html");
    }
    // =============================================== 私有方法 =================================


    /**
     * 初始化账户组合框
     */
    private void refeshAccounts(Runnable runnable) throws Exception {
        BeanfunAccountResult actResult = BeanfunClient.run().getAccountList(QsConstant.beanfunModel.getToken());
        if (actResult.isSuccess()) {
            QsConstant.beanfunModel.build(actResult);
        } else {
            FrameManager.getInstance().message(actResult.getMsg(), Alert.AlertType.ERROR);
            return;
        }

        QsConstant.beanfunModel.build(actResult);

        if (!QsConstant.beanfunModel.isCertStatus()) {
            // 需要进阶认证
            FrameManager.getInstance().message("请前往用户中心 -> 会员中心进行进阶认证!\n" + "做完进阶认证后请重新退出重新登录!", Alert.AlertType.INFORMATION);
        } else if (QsConstant.beanfunModel.isNewAccount()) {
            // 需要创建账号
            FrameManager.getInstance().message("新账号请点击创建账号!", Alert.AlertType.INFORMATION);
        }

        buttonAddAct.setVisible(QsConstant.beanfunModel.isNewAccount());
        menuItemAddAct.setVisible(QsConstant.beanfunModel.isNewAccount());

        FrameManager.getInstance().runLater(() -> {
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
        QsConstant.nowAccount = choiceBoxActList.getSelectionModel().getSelectedItem();
        if (Objects.isNull(QsConstant.nowAccount)) {
            return;
        }
        // 设置账号状态
        String statusText = BooleanUtils.isTrue(QsConstant.nowAccount.getStatus()) ? "正常" : "禁止";
        Color statusColor = BooleanUtils.isTrue(QsConstant.nowAccount.getStatus()) ? Color.GREEN : Color.RED;
        labelActStatus.setText(statusText);
        labelActStatus.setTextFill(statusColor);
        if (Objects.nonNull(QsConstant.nowAccount.getCreateTime())) {
            String createTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(QsConstant.nowAccount.getCreateTime());
            labelActCreateTime.setText(createTimeStr);
        }
        textFieldActId.setText(QsConstant.nowAccount.getId());
        // 获取游戏点数
        String pointsText = this.getPointsText();
        FrameManager.getInstance().runLater(() -> labelActPoint.setText(pointsText));
    }

}
