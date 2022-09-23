package com.starmcc.beanfun.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starmcc.beanfun.client.HttpClient;
import com.starmcc.beanfun.constant.FXPageEnum;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.controller.UpdateController;
import com.starmcc.beanfun.model.JFXStage;
import com.starmcc.beanfun.model.client.QsHttpResponse;
import com.starmcc.beanfun.model.client.UpdateModel;
import javafx.scene.control.Alert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 更新客户端
 *
 * @author starmcc
 * @date 2022/04/08
 */
@Slf4j
public class UpdateManager {
    private static UpdateManager manager = null;


    private UpdateManager() {
    }

    /**
     * 获得实例
     *
     * @return {@link UpdateManager}
     */
    public static synchronized UpdateManager getInstance() {
        //先检查实例是否存在，如果不存在才进入下面的同步块
        if (manager == null) {
            manager = new UpdateManager();
        }
        return manager;
    }

    /**
     * 验证应用程序版本
     *
     * @param quiet 静默
     */
    public synchronized void verifyAppVersion(boolean quiet) {
        UpdateModel model = manager.useServerUpdate();
        if (model.getState() == UpdateModel.State.获取失败) {
            // 如果服务器更新失败,使用github更新
            model = manager.useServerUpdate();
        }
        if (!quiet && model.getState() == UpdateModel.State.已是最新版本) {
            QsConstant.alert("已经是最新版本", Alert.AlertType.INFORMATION);
        }

        if (!quiet && model.getState() == UpdateModel.State.获取失败) {
            QsConstant.alert("获取失败", Alert.AlertType.WARNING);
        }

        if (model.getState() != UpdateModel.State.有新版本) {
            return;
        }

        final UpdateModel finalModel = model;
        FrameManager.getInstance().runLater(() -> {
            String msg = finalModel.getContent() + "\n是否立刻更新?";
            if (!QsConstant.confirmDialog("有新版本-" + finalModel.getVersion(), msg)) {
                return;
            }
            UpdateController.model = finalModel;
            FrameManager.getInstance().openWindow(FXPageEnum.更新页);
            for (Map.Entry<String, JFXStage> entry : QsConstant.JFX_STAGE_DATA.entrySet()) {
                if (StringUtils.equals(entry.getKey(), FXPageEnum.更新页.getFileName())) {
                    continue;
                }
                entry.getValue().getStage().close();
            }
        });
    }

    /**
     * 使用服务器更新
     *
     * @return {@link UpdateModel}
     */
    private UpdateModel useServerUpdate() {
        try {
            QsHttpResponse qsHttpResponse = HttpClient.getInstance().get(QsConstant.UPDATE_API_SERVER);
            if (!qsHttpResponse.getSuccess()) {
                return UpdateModel.build(UpdateModel.State.获取失败);
            }
            String content = qsHttpResponse.getContent();
            if (StringUtils.isBlank(content)) {
                return UpdateModel.build(UpdateModel.State.获取失败);
            }
            UpdateModel model = JSON.parseObject(content, UpdateModel.class);
            if (QsConstant.APP_VERSION_INT.compareTo(model.getVersionInt()) != -1) {
                return UpdateModel.build(UpdateModel.State.已是最新版本);
            }
            model.setState(UpdateModel.State.有新版本);
            return model;
        } catch (Exception e) {
            log.error("获取版本异常 e={}", e.getMessage(), e);
        }
        return UpdateModel.build(UpdateModel.State.获取失败);
    }


    /**
     * 使用github更新
     *
     * @return {@link UpdateModel}
     */
    private UpdateModel useGithubUpdate() {
        try {
            QsHttpResponse qsHttpResponse = HttpClient.getInstance().get(QsConstant.UPDATE_API_GITHUB);
            if (!qsHttpResponse.getSuccess()) {
                return UpdateModel.build(UpdateModel.State.获取失败);
            }
            String json = qsHttpResponse.getContent();
            if (StringUtils.isBlank(json)) {
                return UpdateModel.build(UpdateModel.State.获取失败);
            }
            JSONObject jsonObj = JSON.parseObject(json);
            String githubVersion = jsonObj.getString("tag_name");

            if (StringUtils.isBlank(githubVersion)) {
                return UpdateModel.build(UpdateModel.State.获取失败);
            }

            Integer numberVersionTarget = getNumberVersion(githubVersion);
            if (numberVersionTarget == 0) {
                log.error("版本解析为0，解析失败 targetVersion={}", githubVersion);
                return UpdateModel.build(UpdateModel.State.已是最新版本);
            }

            if (QsConstant.APP_VERSION_INT.compareTo(numberVersionTarget) != -1) {
                return UpdateModel.build(UpdateModel.State.已是最新版本);
            }

            StringBuffer tipsBf = new StringBuffer();
            tipsBf.append("最新版本:").append(githubVersion).append("\n");
            tipsBf.append("--------------------\n");
            tipsBf.append(jsonObj.getString("body"));
            JSONArray assets = jsonObj.getJSONArray("assets");
            String downloadUrl = "";
            for (int i = 0; i < assets.size(); i++) {
                JSONObject obj = assets.getJSONObject(i);
                if (StringUtils.equals(obj.getString("name"), QsConstant.APP_NAME + ".exe")) {
                    downloadUrl = obj.getString("browser_download_url");
                    break;
                }
            }
            log.info("新版本下载地址: {}", downloadUrl);
            UpdateModel model = UpdateModel.build(UpdateModel.State.有新版本);
            model.setVersion(githubVersion);
            model.setUrl(downloadUrl);
            model.setContent(tipsBf.toString());
            model.setVersionInt(numberVersionTarget.intValue());
            return model;
        } catch (Exception e) {
            log.error("获取版本异常 e={}", e.getMessage(), e);
            return UpdateModel.build(UpdateModel.State.获取失败);
        }
    }


    /**
     * 获得数字版本
     *
     * @param name 名字
     * @return {@link Long}
     */
    private static Integer getNumberVersion(String name) {
        if (StringUtils.isBlank(name)) {
            return 0;
        }
        String[] split = name.split("\\.");
        List<String> versionList = Arrays.stream(split).collect(Collectors.toList());
        while (versionList.size() < 3) {
            versionList.add("0");
        }
        StringBuffer version = new StringBuffer();
        for (String s : versionList) {
            version.append(s);
        }
        return Integer.valueOf(version.toString());
    }
}
