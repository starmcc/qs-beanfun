package com.starmcc.beanfun.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.model.UpdateModel;
import com.starmcc.beanfun.model.client.QsHttpResponse;
import com.starmcc.beanfun.utils.DownloadTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 更新客户端
 *
 * @author starmcc
 * @date 2022/04/08
 */
@Slf4j
public class UpdateClient {
    private static final String GITHUB_URL = "https://api.github.com/repos/starmcc/qs-beanfun/releases/latest";
    private static UpdateClient instance = null;


    private UpdateClient() {
    }

    public static synchronized UpdateClient getInstance() {
        //先检查实例是否存在，如果不存在才进入下面的同步块
        if (instance == null) {
            instance = new UpdateClient();
        }
        return instance;
    }


    /**
     * 获得版本模型
     *
     * @return {@link UpdateModel}
     * @throws Exception 异常
     */
    public UpdateModel getVersionModel() {
        try {
            QsHttpResponse qsHttpResponse = HttpClient.get(GITHUB_URL, null);
            if (!qsHttpResponse.getSuccess()) {
                return UpdateModel.builder().state(UpdateModel.State.获取失败).build();
            }
            String json = qsHttpResponse.getContent();
            if (StringUtils.isBlank(json)) {
                return UpdateModel.builder().state(UpdateModel.State.获取失败).build();
            }

            log.debug("获取版本信息 json={}", json);
            JSONObject jsonObj = JSON.parseObject(json);
            String githubVersion = jsonObj.getString("tag_name");

            if (StringUtils.isBlank(githubVersion)) {
                return UpdateModel.builder().state(UpdateModel.State.获取失败).build();
            }

            if (!QsConstant.checkNewVersion(githubVersion)) {
                return UpdateModel.builder().state(UpdateModel.State.已是最新版本).build();
            }

            String body = jsonObj.getString("body");
            if (body.indexOf(UpdateModel.VERSION_PRE) != -1) {
                // 体验版不更新
                return UpdateModel.builder().state(UpdateModel.State.已是最新版本).build();
            }
            StringBuffer tipsBf = new StringBuffer();
            tipsBf.append("最新版本:").append(githubVersion).append("\n");
            tipsBf.append("--------------------\n");
            tipsBf.append(body);
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
            return UpdateModel.builder()
                    .state(UpdateModel.State.有新版本)
                    .nowVersion(githubVersion)
                    .downloadUrl(downloadUrl)
                    .tips(tipsBf.toString())
                    .build();
        } catch (Exception e) {
            log.error("获取版本异常 e={}", e.getMessage(), e);
            return UpdateModel.builder().state(UpdateModel.State.获取失败).build();
        }

    }


    /**
     * 更新 TODO 未完善
     *
     * @param downloadUrl 下载网址
     * @param process     过程
     */
    @Deprecated
    public void update(String downloadUrl, DownloadTools.Process process) {
        String path = QsConstant.APP_PATH + QsConstant.APP_NAME + ".exe.tmp";
        DownloadTools.getInstance().run(downloadUrl, path, process);
        // 执行bat脚本，结束当前程序，删除旧包，替换新包，运行新包
        // 脚本
        /*
            @echo off
            Taskkill /f /im test.exe
            Del test.exe
            ren test.exe.tmp test.exe
            start test.exe
            Del %0
            Exit
         */
    }

}
