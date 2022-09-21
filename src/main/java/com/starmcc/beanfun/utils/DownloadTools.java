package com.starmcc.beanfun.utils;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * 下载工具
 *
 * @author starmcc
 * @date 2022/09/21
 */
public class DownloadTools {

    private static DownloadTools instance = null;

    private DownloadTools() {

    }

    public static synchronized DownloadTools getInstance() {
        //先检查实例是否存在，如果不存在才进入下面的同步块
        if (instance == null) {
            instance = new DownloadTools();
        }
        return instance;
    }

    @FunctionalInterface
    public interface Process {

        public static enum State {
            请求状态码异常(-3),
            连接超时(-2),
            未知异常(-1),
            准备开始(0),
            正在连接(1),
            创建文件(2),
            下载中(3),
            下载完毕(4),
            ;
            private final int state;

            State(int state) {
                this.state = state;
            }

            public int getState() {
                return state;
            }
        }

        /**
         * 调用
         *
         * @param state   状态
         * @param process 过程
         * @param e       e
         */
        void call(State state, Integer process, Exception e);
    }


    /**
     * 单线程下载文件
     *
     * @param url  文件的网络地址
     * @param path 保存的文件地址
     */
    public void run(String url, String path, Process process) {
        process.call(Process.State.准备开始, 0, null);
        InputStream inputStream = null;
        RandomAccessFile randomAccessFile = null;
        HttpURLConnection urlConnection = null;
        int responseCode = 0;
        int unitProgress = 0;

        try {
            process.call(Process.State.正在连接, 0, null);
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10 * 1000);
            responseCode = urlConnection.getResponseCode();
        } catch (Exception e) {
            process.call(Process.State.连接超时, 0, e);
            return;
        }

        try {
            boolean is = responseCode >= 200 && responseCode < 300;
            if (!is) {
                process.call(Process.State.请求状态码异常, unitProgress, null);
                return;
            }
            process.call(Process.State.创建文件, unitProgress, null);
            inputStream = urlConnection.getInputStream();
            int len = 0;
            byte[] data = new byte[4096];
            //用于保存当前进度（具体进度）
            int progres = 0;
            //获取文件
            int maxProgres = urlConnection.getContentLength();
            File file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();
            }
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            randomAccessFile = new RandomAccessFile(file, "rwd");
            //设置文件大小
            randomAccessFile.setLength(maxProgres);
            //将文件大小分成100分，每一分的大小为unit
            int unit = maxProgres / 100;
            //用于保存当前进度(1~100%)
            while (-1 != (len = inputStream.read(data))) {
                randomAccessFile.write(data, 0, len);
                //保存当前具体进度
                progres += len;
                //计算当前百分比进度
                int temp = progres / unit;
                //如果下载过程出现百分比变化
                if (temp >= 1 && temp > unitProgress) {
                    //保存当前百分比
                    unitProgress = temp;
                    process.call(Process.State.下载中, unitProgress, null);
                }
            }
            inputStream.close();
            process.call(Process.State.下载完毕, unitProgress, null);
        } catch (Exception e) {
            process.call(Process.State.未知异常, 0, e);
        } finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
                if (null != randomAccessFile) {
                    randomAccessFile.close();
                }
            } catch (Exception e) {
                process.call(Process.State.未知异常, 0, e);
            }
        }
    }
}
