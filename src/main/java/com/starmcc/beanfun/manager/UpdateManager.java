package com.starmcc.beanfun.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starmcc.beanfun.client.HttpClient;
import com.starmcc.beanfun.constant.FXPageEnum;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.controller.UpdateController;
import com.starmcc.beanfun.entity.client.QsHttpResponse;
import com.starmcc.beanfun.entity.model.ConfigModel;
import com.starmcc.beanfun.entity.model.UpdateModel;
import javafx.scene.control.Alert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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

        // 检查是否生产版本，如果不是，则不进行版本校验
        if (!StringUtils.equals(QsConstant.ENV.toLowerCase(), "prod")) {
            if (!quiet) {
                FrameManager.getInstance().message("当前版本不支持更新功能", Alert.AlertType.WARNING);
            }
            return;
        }

        // 获取更新内容
        manager.getUpdateModel(model);

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
            // 不是手动更新，则弹出下载页面
            UpdateController.model = model;
            FrameManager.getInstance().openWindow(FXPageEnum.更新页);
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
            switch (updateChannel) {
                case GITEE:
                    // 使用gitee
                    url = QsConstant.GITEE_API_LATSET;
                    break;
                default:
                    // 使用github
                    url = QsConstant.GITHUB_API_LATSET;
                    break;
            }
            QsHttpResponse qsHttpResponse = HttpClient.getInstance().get(url);
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

            if (StringUtils.equals(QsConstant.APP_VERSION, githubVersion)) {
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
        } catch (Exception e) {
            log.error("获取版本异常 e={}", e.getMessage(), e);
            updateModel.setState(UpdateModel.State.获取失败);
        }
    }

}
