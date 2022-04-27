package com.starmcc.beanfun.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.starmcc.beanfun.constant.QsConstant;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ConfigFileUtils {


    //读取json文件
    public static String readConfig() {
        String jsonStr = "";
        try {
            File jsonFile = new File(QsConstant.APP_CONFIG);
            if (!jsonFile.exists()) {
                return jsonStr;
            }
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            log.error("文件读取异常 e={}", e.getMessage(), e);
            return jsonStr;
        }
    }

    public static boolean writeConfig(Object jsonData) {
        String content = JSON.toJSONString(jsonData, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
        try {
            File file = new File(QsConstant.APP_CONFIG);
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
            Writer write = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            write.write(content);
            write.flush();
            write.close();
            return true;
        } catch (Exception e) {
            log.error("文件写入异常 e={}", e.getMessage(), e);
            return false;
        }
    }
}
