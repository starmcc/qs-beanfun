package com.starmcc.beanfun.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starmcc.beanfun.client.HttpClient;
import com.starmcc.beanfun.constant.FXPageEnum;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.controller.UpdateController;
import com.starmcc.beanfun.entity.client.QsHttpResponse;
import com.starmcc.beanfun.entity.model.UpdateModel;
import javafx.scene.control.Alert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
        final UpdateModel model = new UpdateModel();
        model.setType(manager.requestUpdateType());
        if (model.getType() == UpdateModel.Type.无数据) {
            if (quiet) {
                return;
            }
            FrameManager.getInstance().message("网络超时,无法获取最新版本信息", Alert.AlertType.WARNING);
            return;
        }

        if (model.getType() == UpdateModel.Type.未知类型) {
            model.setType(UpdateModel.Type.GITHUB手动更新);
        }
        // 使用github更新
        manager.useGithubUpdate(model);

        if (model.getState() != UpdateModel.State.有新版本) {
            if (quiet) {
                return;
            }
            if (model.getState() == UpdateModel.State.获取失败) {
                FrameManager.getInstance().message("网络超时,无法获取最新版本信息", Alert.AlertType.WARNING);
            } else if (model.getState() == UpdateModel.State.已是最新版本) {
                FrameManager.getInstance().message("当前已经是最新版本", Alert.AlertType.INFORMATION);
            }
            return;
        }

        // 有新版本
        FrameManager.getInstance().runLater(() -> {
            String msg = model.getContent() + "\n是否更新?";
            if (!FrameManager.getInstance().dialogConfirm("有新版本", msg)) {
                return;
            }
            if (model.getType() == UpdateModel.Type.GITHUB手动更新) {
                // 手动更新，则跳转浏览器页面
                FrameManager.getInstance().openWebUrl(QsConstant.GITHUB_URL);
                return;
            }
            // 不是手动更新，则弹出下载页面
            UpdateController.model = model;
            FrameManager.getInstance().openWindow(FXPageEnum.更新页);
        });
    }


    /**
     * 请求更新类型
     *
     * @return {@link UpdateModel.Type}
     */
    private synchronized UpdateModel.Type requestUpdateType() {
        try {
            QsHttpResponse qsHttpResponse = HttpClient.getInstance().get(QsConstant.APP_UPDATE_CONFIG);
            if (!qsHttpResponse.getSuccess()) {
                return UpdateModel.Type.无数据;
            }
            String content = qsHttpResponse.getContent();
            content = Objects.nonNull(content) ? content.trim() : content;
            if (StringUtils.isBlank(content)) {
                return UpdateModel.Type.无数据;
            }
            UpdateModel.Type type = UpdateModel.Type.get(Short.valueOf(content));
            return Objects.nonNull(type) ? type : UpdateModel.Type.未知类型;
        } catch (Exception e) {
            log.error("异常 e={}", e.getMessage(), e);
        }
        return UpdateModel.Type.无数据;

    }

    /**
     * 使用github自动更新
     *
     * @param updateModel 更新模型
     * @param quiet       静默
     */
    private void useGithubUpdate(UpdateModel updateModel) {
        try {
            QsHttpResponse qsHttpResponse = HttpClient.getInstance().get(QsConstant.GITHUB_API_LATSET);
            if (!qsHttpResponse.getSuccess()) {
                updateModel.setState(UpdateModel.State.获取失败);
                return;
            }
            String json = qsHttpResponse.getContent();
            if (StringUtils.isBlank(json)) {
                updateModel.setState(UpdateModel.State.获取失败);
                return;
            }
            JSONObject jsonObj = JSON.parseObject(json);
            String githubVersion = jsonObj.getString("tag_name");

            if (StringUtils.isBlank(githubVersion)) {
                updateModel.setState(UpdateModel.State.获取失败);
                return;
            }

            Integer targetVersion = getNumberVersion(githubVersion);
            if (targetVersion == 0) {
                log.error("版本解析为0，解析失败 targetVersion={}", githubVersion);
                updateModel.setState(UpdateModel.State.已是最新版本);
                return;
            }
            Integer nowVersion = getNumberVersion(QsConstant.APP_VERSION);
            if (nowVersion.compareTo(targetVersion) != -1) {
                updateModel.setState(UpdateModel.State.已是最新版本);
                return;
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
            updateModel.setState(UpdateModel.State.有新版本);
            updateModel.setVersion(githubVersion);
            updateModel.setUrl(downloadUrl);
            updateModel.setContent(tipsBf.toString());
            updateModel.setVersionInt(targetVersion.intValue());
        } catch (Exception e) {
            log.error("获取版本异常 e={}", e.getMessage(), e);
            updateModel.setState(UpdateModel.State.获取失败);
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
        int minNum = 3;
        while (versionList.size() < minNum) {
            versionList.add("0");
        }
        StringBuffer version = new StringBuffer();
        for (String s : versionList) {
            version.append(s);
        }
        return Integer.valueOf(version.toString());
    }
}
