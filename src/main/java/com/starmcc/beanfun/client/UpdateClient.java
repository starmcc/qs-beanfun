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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

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
            QsHttpResponse qsHttpResponse = HttpClient.getInstance().get(GITHUB_URL, null);
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

            if (!QsConstant.checkNewVersion(githubVersion)) {
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
            model.setUpdateVersion(githubVersion);
            model.setDownloadUrl(downloadUrl);
            model.setUpdateText(tipsBf.toString());
            return model;
        } catch (Exception e) {
            log.error("获取版本异常 e={}", e.getMessage(), e);
            return UpdateModel.build(UpdateModel.State.获取失败);
        }

    }


    /**
     * 更新 TODO 未完善
     *
     * @param downloadUrl 下载网址
     * @param process     过程
     */
    public void update(String downloadUrl) {
        String path = QsConstant.APP_PATH + QsConstant.APP_NAME + ".exe";
        DownloadTools.Process process = new DownloadTools.Process() {
            @Override
            public void call(State state, Integer process, Exception e) {

            }
        };

        // 下载进度窗口提示

        // 下载
        DownloadTools.getInstance().run(downloadUrl, path + ".tmp", process);


        // 构建Bat文件
        /*
            @echo off
            taskkill /f /im ${appName}
            ping 1.1.1.1 -n 1 -w 1500
            del /f "${fileName}"
            ren "${fileName}.tmp" "${fileName}"
            start "${fileName}"
            del %0
         */
        StringBuffer sbf = new StringBuffer();
        sbf.append("@echo off\n");
        sbf.append("taskkill /f /im ${appName}\n");
        sbf.append("ping 1.1.1.1 -n 1 -w 1500\n");
        sbf.append("del /f \"${fileName}\"\n");
        sbf.append("ren \"${fileName}.tmp\" \"${fileName}\"\n");
        sbf.append("start \"${fileName}\"\n");
        sbf.append("del %0");
        String bat = sbf.toString();

        bat = StringUtils.replace(bat, "${appName}",
                QsConstant.APP_NAME + ".exe");
        bat = StringUtils.replace(bat, "${fileName}", path);
        // 写入bat
        this.writeBatFile(bat);
        // 执行bat
        String command = "cmd /c " + QsConstant.APP_PATH + "update.bat";
        try {
            Runtime.getRuntime().exec(command);
        } catch (Exception e) {
            log.error("运行异常 e={}", e.getMessage(), e);
        }
    }


    /**
     * write Bat文件
     *
     * @param content 内容
     */
    private void writeBatFile(String content) {
        Writer write = null;
        try {
            // 写到文件
            File file = new File(QsConstant.APP_PATH + "update.bat");
            // 创建上级目录
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            // 如果文件存在，则删除文件
            if (file.exists()) {
                file.delete();
            }
            // 创建文件
            file.createNewFile();
            // 写入文件
            write = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            write.write(content);
            write.flush();
        } catch (Exception e) {
            log.error("写入文件异常 e={}", e.getMessage(), e);
        } finally {
            try {
                if (Objects.nonNull(write)) {
                    write.close();
                }
            } catch (IOException e) {
                log.error("关闭异常 e={}", e.getMessage(), e);
            }
        }
    }

}
