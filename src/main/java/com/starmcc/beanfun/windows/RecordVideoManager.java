package com.starmcc.beanfun.windows;

import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.model.ConfigModel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 录像管理器
 *
 * @author starmcc
 * @date 2022/09/22
 */
@Getter
@Slf4j
public class RecordVideoManager {

    private Process process = null;
    private String nowFileName = "";

    private static RecordVideoManager recordVideoManager;

    public synchronized static RecordVideoManager getInstance() {
        if (recordVideoManager == null) {
            recordVideoManager = new RecordVideoManager();
        }
        return recordVideoManager;
    }


    /**
     * 开始
     *
     * @param path     路径
     * @param fps      帧/秒
     * @param codeRate 编码速率
     * @param callback 回调
     */
    public void start(ConfigModel.RecordVideo config, Consumer<String> callback) {
        String command = recordVideoManager.buildFFmpegScript(config);
        recordVideoManager.exec(command, callback);
    }

    /**
     * 停止
     *
     * @throws IOException ioexception
     */
    public void stop() {
        try {
            if (Objects.isNull(recordVideoManager) || Objects.isNull(recordVideoManager.process)) {
                return;
            }
            OutputStream outputStream = recordVideoManager.process.getOutputStream();
            if (recordVideoManager.process.isAlive()) {
                outputStream.write("q\r\n".getBytes());
                outputStream.flush();
            }
            outputStream.close();
        } catch (IOException e) {
            log.error("ffmpeg e={}", e.getMessage(), e);
        }
    }

    // ========================== 基础支持 ===============================


    /**
     * 构建ffmpeg脚本
     *
     * @param config 配置
     * @return {@link String}
     */
    private String buildFFmpegScript(ConfigModel.RecordVideo config) {
        recordVideoManager.nowFileName = recordVideoManager.buildFileName(config.getFolder());
        // ffmpeg 命令
        // ffmpeg.exe -f gdigrab -framerate 30 -i title="MapleStory" -pix_fmt yuv420p -c:v h264 -b:v 2500k -preset veryfast -y out.mp4
        StringBuilder sbf = new StringBuilder();
        sbf.append(QsConstant.PATH_EXE_FFMPEG);
        sbf.append(" -f gdigrab -framerate ").append(config.getFps());
        if (Objects.equals(config.getCaptureType(), ConfigModel.RecordVideo.CaptureTypeEnum.游戏窗口.getType())) {
            sbf.append(" -i title=\"MapleStory\"");
        } else {
            sbf.append(" -i desktop");
        }
        sbf.append(" -pix_fmt yuv420p -c:v h264 -b:v ").append(config.getCodeRate()).append("k");
        sbf.append(" -preset veryfast -y ").append(recordVideoManager.nowFileName);
        return sbf.toString();
    }

    /**
     * 构建文件名字
     *
     * @param path 路径
     * @return {@link String}
     */
    private String buildFileName(String path) {
        File file = new File(path + "\\" + QsConstant.APP_NAME + "-" + System.currentTimeMillis() + ".mp4");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return file.getPath();
    }

    // ========================== 控制台支持 =============================

    /**
     * 执行
     *
     * @param command  命令
     * @param callback 回调
     */
    private void exec(String command, Consumer<String> callback) {
        try {
            ProcessBuilder pb = new ProcessBuilder().command("cmd", "/c", command);
            //标准和错误流合并输出
            pb.redirectErrorStream(true);
            recordVideoManager.process = pb.start();
            output(callback);
            recordVideoManager.process.waitFor();
        } catch (Exception e) {
            log.error("ffmpeg e={}", e.getMessage(), e);
        }
    }

    /**
     * 输出
     *
     * @param callback 回调
     */
    private void output(Consumer<String> callback) {
        if (Objects.isNull(callback)) {
            return;
        }
        InputStream inputStream = recordVideoManager.process.getInputStream();
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            isr = new InputStreamReader(inputStream, "gbk");
            br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                callback.accept(line);
            }
        } catch (IOException e) {
            log.error("ffmpeg e={}", e.getMessage(), e);
        } finally {
            recordVideoManager.close(br, isr, inputStream);
        }
    }


    /**
     * 关闭
     *
     * @param acArr ac加勒比海盗
     */
    private void close(AutoCloseable... acArr) {
        for (AutoCloseable ac : acArr) {
            if (Objects.isNull(ac)) {
                continue;
            }
            try {
                ac.close();
            } catch (Exception e) {
                log.error("ffmpeg e={}", e.getMessage(), e);
            }
        }
    }

}
