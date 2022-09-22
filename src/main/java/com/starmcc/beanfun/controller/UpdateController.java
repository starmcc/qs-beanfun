package com.starmcc.beanfun.controller;

import com.starmcc.beanfun.client.HttpClient;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.manager.ThreadPoolManager;
import com.starmcc.beanfun.model.client.UpdateModel;
import com.starmcc.beanfun.utils.FileTools;
import com.starmcc.beanfun.windows.FrameService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * 进度控制器
 *
 * @author starmcc
 * @date 2022/09/22
 */
@Slf4j
public class UpdateController implements Initializable {

    public static UpdateModel model;
    @FXML
    private TextArea textAreaContent;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label labelProcess;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textAreaContent.setText(model.getContent());
        labelProcess.setText("0%");
        try {
            this.download();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 下载
     *
     * @throws Exception 异常
     */
    private void download() throws Exception {
        String fileName = QsConstant.PATH_APP + "\\" + QsConstant.APP_NAME + ".exe.tmp";
        ThreadPoolManager.execute(() -> {
            HttpClient.getInstance().downloadFile(new URL(model.getUrl()), new File(fileName), (state, file, process, e) -> {
                if (!state.isNormal()) {
                    FrameService.getInstance().runLater(() -> {
                        QsConstant.alert("更新失败-" + state.toString(), Alert.AlertType.WARNING);
                    });
                    return;
                }
                progressBar.setProgress(process / 100D);
                labelProcess.setText(process + "%");
                if (state != HttpClient.Process.State.下载完毕) {
                    return;
                }
                this.downloadComplete(file);
            });
        });

    }


    /**
     * 下载完成
     *
     * @param file 文件
     */
    private void downloadComplete(File file) {
        // 构建Bat文件
        String appExe = QsConstant.APP_NAME + ".exe";
        StringBuffer bat = new StringBuffer();
        bat.append("@echo off").append("\n");
        // 先刪除旧的
        bat.append("del /f ").append(QsConstant.APP_NAME).append(".exe.old").append("\n");
        // 在将当前运行的改成旧的
        bat.append("ren ").append(appExe).append(" ").append(QsConstant.APP_NAME).append(".exe.old").append("\n");
        // 在新下载的改成QsBeanfun.exe
        bat.append("ren ").append(file.getName()).append(" ").append(appExe).append("\n");
        // 启动新的
        bat.append("start .\\").append(appExe).append("\n");
        // 删除自身
        bat.append("del %0");
        //  bat.append("taskkill /f /im ").append(appExe).append("\n");

        String batPath = QsConstant.PATH_APP + "\\update.bat";
        // 写入bat
        FileTools.writeFile(bat.toString(), batPath);
        try {
            // 执行update.bat  /b隐藏运行
            String[] cmd = {"cmd", "/c", "start", "/b", batPath};
            Runtime.getRuntime().exec(cmd, null, new File(batPath).getParentFile());
        } catch (Exception e) {
            log.error("Runtime error e={}", e.getMessage(), e);
        }
    }
}
