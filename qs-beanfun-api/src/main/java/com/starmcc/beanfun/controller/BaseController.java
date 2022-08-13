package com.starmcc.beanfun.controller;

import com.starmcc.beanfun.api.ThirdPartyApiClient;
import com.starmcc.beanfun.client.BeanfunClient;
import com.starmcc.beanfun.client.UpdateClient;
import com.starmcc.beanfun.config.PassToken;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.controller.page.WebController;
import com.starmcc.beanfun.event.UpdateProgramEvent;
import com.starmcc.beanfun.handler.AutoLunShaoHandler;
import com.starmcc.beanfun.handler.AutoScriptHandler;
import com.starmcc.beanfun.handler.GameHandler;
import com.starmcc.beanfun.model.UpdateModel;
import com.starmcc.beanfun.utils.FileUtils;
import com.starmcc.beanfun.utils.FrameUtils;
import com.starmcc.beanfun.utils.RegexUtils;
import com.starmcc.qmframework.body.QmBody;
import com.starmcc.qmframework.controller.QmResult;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api")
public class BaseController {

    @Autowired
    private GameHandler gameHandler;
    @Autowired
    private AutoLunShaoHandler autoLunShaoHandler;
    @Autowired
    private AutoScriptHandler autoScriptHandler;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


    @GetMapping("/base/get_rates")
    public String getRates() {
        QsConstant.currentRateChinaToTw = ThirdPartyApiClient.getCurrentRateChinaToTw();
        return QmResult.success(QsConstant.currentRateChinaToTw);
    }


    @GetMapping("/base/check_version")
    public String checkVersion() {
        // 检查版本，返回最新版本号
        UpdateModel versionModel = UpdateClient.getInstance().getVersionModel();
        return QmResult.success(versionModel);
    }

    @PostMapping("/base/update_program")
    public String updateProgram(@QmBody String downloadUrl) {
        // 获取下载地址，开始更新,异步执行
        applicationEventPublisher.publishEvent(new UpdateProgramEvent(downloadUrl));
        return QmResult.success();
    }


    @PassToken
    @GetMapping("/base/open_window")
    public String openWindow(@RequestParam String code) throws Exception {
        if (StringUtils.equals(code, "register")) {
            WebController.jumpUrl = BeanfunClient.run().getWebUrlRegister();
        } else if (StringUtils.equals(code, "forgotPwd")) {
            WebController.jumpUrl = BeanfunClient.run().getWebUrlForgotPwd();
        } else if (StringUtils.equals(code, "memberCenter")) {
            if (Objects.isNull(QsConstant.beanfunModel)
                    || StringUtils.isBlank(QsConstant.beanfunModel.getToken())) {
                return QmResult.loginNotIn();
            }
            WebController.jumpUrl = BeanfunClient.run().getWebUrlMemberCenter(QsConstant.beanfunModel.getToken());
        } else if (StringUtils.equals(code, "memberTopUp")) {
            if (Objects.isNull(QsConstant.beanfunModel)
                    || StringUtils.isBlank(QsConstant.beanfunModel.getToken())) {
                return QmResult.loginNotIn();
            }
            WebController.jumpUrl = BeanfunClient.run().getWebUrlMemberTopUp(QsConstant.beanfunModel.getToken());
        } else if (StringUtils.equals(code, "serviceCenter")) {
            WebController.jumpUrl = BeanfunClient.run().getWebUrlServiceCenter();
        } else if (StringUtils.equals(code, "equipmentWindow")) {
            Platform.runLater(() -> {
                try {
                    FrameUtils.openWindow(QsConstant.Page.装备计算器);
                } catch (Exception e) {
                    log.error("异常 e={}", e.getMessage(), e);
                }
            });
            return QmResult.success();
        } else {
            return QmResult.fail();
        }
        Platform.runLater(() -> {
            try {
                FrameUtils.openWindow(QsConstant.Page.网页客户端);
            } catch (Exception e) {
                log.error("浏览器打开异常 e={}", e.getMessage(), e);
            }
        });
        return QmResult.success();
    }

    @GetMapping("/base/set_game_path")
    public String setGamePath() {
        String path = null;
        try {
            path = this.openFileDialog();
        } catch (Exception e) {
            log.error("发生错误 e={}", e.getMessage(), e);
            return QmResult.error("发生错误", null);
        }

        if (StringUtils.isBlank(path)) {
            return QmResult.fail("没有选择!", null);
        }

        if (RegexUtils.test(RegexUtils.PTN_CHINA_PATH, path)) {
            return QmResult.fail("路径中不能包含中文");
        }

        QsConstant.config.setGamePath(path);
        FileUtils.writeConfig(QsConstant.config);
        return QmResult.success("保存成功!", null);
    }

    @PostMapping("/base/auto_lunshao")
    public String autoLunshao(@QmBody Boolean status) {
        if (status) {
            final Integer lunHuiKey = QsConstant.config.getLunHuiKey();
            final Integer ranShaoKey = QsConstant.config.getRanShaoKey();
            if (Objects.isNull(lunHuiKey) || Objects.isNull(ranShaoKey)) {
                return QmResult.fail("没有设置键位");
            }
            if (!autoLunShaoHandler.start(lunHuiKey, ranShaoKey)) {
                return QmResult.fail("启动失败");
            }
        } else {
            autoLunShaoHandler.stop();
        }
        return QmResult.success(status ? "开始运行" : "已停止运行");
    }

    @PostMapping("/base/start_game")
    public String startGame(@QmBody(required = false) String password) {
        String gamePath = QsConstant.config.getGamePath();
        if (StringUtils.isBlank(gamePath)) {
            return QmResult.fail("请配置游戏路径", null);
        }
        // 启动游戏 如果免输入模式，组装账密
        if (StringUtils.isNotBlank(password)) {
            gameHandler.runGame(gamePath, QsConstant.nowAccount.getId(), password);
        } else {
            gameHandler.runGame(gamePath, null, null);
        }
        return QmResult.success("游戏正在启动,请稍后..", null);
    }

    @PassToken
    @PostMapping("/base/auto_game_input_act_pwd")
    public String autoGameInputActPwd(@QmBody String account, @QmBody String password) {
        autoScriptHandler.autoGameInputActPwd(account, password);
        return QmResult.success();
    }

    /**
     * 打开文件对话框
     *
     * @return {@link String}
     */
    private String openFileDialog() throws Exception {
        // 这里是因为必须要在 Platform.runLater 这个线程里，所以主线程必须阻塞，等待获取到执行结果后才进行返回。
        final Map<String, String> runing = new HashMap<>();
        runing.put("run", "1");
        Platform.runLater(() -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("寻找新枫之谷启动程序");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("MapleStory.exe", "MapleStory.exe"));
            File file = fileChooser.showOpenDialog(new Stage());
            runing.put("run", "0");
            if (Objects.isNull(file)) {
                return;
            }
            String path = file.getPath();
            runing.put("path", path);

        });

        while (true) {
            String run = runing.get("run");
            if (!StringUtils.equals(run, "0")) {
                Thread.sleep(100);
                continue;
            }
            return runing.get("path");
        }
    }
}
