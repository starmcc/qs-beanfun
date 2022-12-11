package com.starmcc.beanfun.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starmcc.beanfun.client.HttpClient;
import com.starmcc.beanfun.constant.FXPageEnum;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.controller.UpdateController;
import com.starmcc.beanfun.entity.client.QsHttpResponse;
import com.starmcc.beanfun.entity.client.UpdateModel;
import javafx.scene.control.Alert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
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
        final UpdateModel model = manager.useGithubUpdate();
        if (model.getState() != UpdateModel.State.有新版本) {
            if (quiet) {
                return;
            }
            if (model.getState() == UpdateModel.State.获取失败) {
                FrameManager.getInstance().message("Github网络连接超时,无法自动检查更新.", Alert.AlertType.WARNING);
            } else if (model.getState() == UpdateModel.State.已是最新版本) {
                FrameManager.getInstance().message("已经是最新版本", Alert.AlertType.INFORMATION);
            }
            return;
        }

        // 有新版本
        FrameManager.getInstance().runLater(() -> {
            String msg = model.getContent() + "\n是否立刻更新?";
            if (!FrameManager.getInstance().dialogConfirm("有新版本", msg)) {
                return;
            }
            UpdateController.model = model;
            FrameManager.getInstance().openWindow(FXPageEnum.更新页);
        });
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

            Integer targetVersion = getNumberVersion(githubVersion);
            if (targetVersion == 0) {
                log.error("版本解析为0，解析失败 targetVersion={}", githubVersion);
                return UpdateModel.build(UpdateModel.State.已是最新版本);
            }
            Integer nowVersion = getNumberVersion(QsConstant.APP_VERSION);
            if (nowVersion.compareTo(targetVersion) != -1) {
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
            model.setVersionInt(targetVersion.intValue());
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
