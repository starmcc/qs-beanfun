package com.starmcc.beanfun.manager;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.starmcc.beanfun.client.HttpClient;
import com.starmcc.beanfun.constant.FXPageEnum;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.controller.UpdateController;
import com.starmcc.beanfun.entity.client.QsHttpResponse;
import com.starmcc.beanfun.entity.model.ConfigModel;
import com.starmcc.beanfun.entity.model.UpdateModel;
import com.starmcc.beanfun.entity.model.Version;
import javafx.scene.control.Alert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

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
    public static UpdateManager getInstance() {
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
    public void verifyAppVersion(boolean quiet) {
        final UpdateModel model = new UpdateModel();

        // 检查是否生产版本，如果不是，则不进行版本校验
        if (!StringUtils.equals(QsConstant.ENV.toLowerCase(), "prod")) {
            if (!quiet) {
                FrameManager.getInstance().messageMaster("当前版本不支持更新功能", Alert.AlertType.WARNING);
            }
            return;
        }

        // 获取更新内容
        manager.getUpdateModel(model);

        if (model.getState() != UpdateModel.State.NEW_VERSION) {
            if (quiet) {
                return;
            }
            if (model.getState() == UpdateModel.State.GET_ERROR) {
                FrameManager.getInstance().messageMaster("网络超时,无法获取最新版本信息", Alert.AlertType.WARNING);
            } else if (model.getState() == UpdateModel.State.LATEST_VERSION) {
                FrameManager.getInstance().messageMaster("当前已经是最新版本", Alert.AlertType.INFORMATION);
            }
            return;
        }

        // 有新版本
        FrameManager.getInstance().runLater(() -> {
            String msg = model.getContent() + "\n是否更新?";
            if (!FrameManager.getInstance().dialogConfirm("有新版本", msg)) {
                return;
            }
            // 不是手动更新，则弹出下载页面
            UpdateController.model = model;
            FrameManager.getInstance().openWindow(FXPageEnum.UPDATE);
        });
    }


    /**
     * 获取更新模型
     * 使用gitee或GitHub渠道
     *
     * @param updateModel 更新模型
     */
    private void getUpdateModel(UpdateModel updateModel) {
        try {
            ConfigModel.UpdateChannel updateChannel = ConfigModel.UpdateChannel.get(QsConstant.config.getUpdateChannel());
            String url = "";
            if (Objects.requireNonNull(updateChannel) == ConfigModel.UpdateChannel.GITEE) {
                // 使用gitee
                url = QsConstant.GITEE_API_LATSET;
            } else {
                // 使用github
                url = QsConstant.GITHUB_API_LATSET;
            }
            QsHttpResponse qsHttpResponse = HttpClient.getInstance().get(url);
            if (!qsHttpResponse.getSuccess()) {
                updateModel.setState(UpdateModel.State.GET_ERROR);
                return;
            }
            String json = qsHttpResponse.getContent();
            if (StringUtils.isBlank(json)) {
                updateModel.setState(UpdateModel.State.GET_ERROR);
                return;
            }
            JSONObject jsonObj = JSON.parseObject(json);
            String githubVersion = jsonObj.getString("tag_name");

            if (StringUtils.isBlank(githubVersion)) {
                updateModel.setState(UpdateModel.State.GET_ERROR);
                return;
            }
            githubVersion = "v5.1";
            Version version = new Version(githubVersion);

            if (QsConstant.APP_VERSION.compareTo(version) >= 0) {
                updateModel.setState(UpdateModel.State.LATEST_VERSION);
                return;
            }
            if (version.getMajor() > 4) {
                // 检查大版本，如果大于4，则不继续更新
                updateModel.setState(UpdateModel.State.LATEST_VERSION);
                return;
            }

            StringBuffer tipsBf = new StringBuffer();
            tipsBf.append("最新版本:").append(version).append("\n");
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
            updateModel.setState(UpdateModel.State.NEW_VERSION);
            updateModel.setVersion(version);
            updateModel.setUrl(downloadUrl);
            updateModel.setContent(tipsBf.toString());
        } catch (Exception e) {
            log.error("获取版本异常 e={}", e.getMessage(), e);
            updateModel.setState(UpdateModel.State.GET_ERROR);
        }
    }

}
