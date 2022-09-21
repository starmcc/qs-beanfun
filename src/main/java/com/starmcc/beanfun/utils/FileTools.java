package com.starmcc.beanfun.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.starmcc.beanfun.QsBeanfunApplication;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.model.ConfigModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * 文件工具
 *
 * @author starmcc
 * @date 2022/09/21
 */
@Slf4j
public class FileTools {

    /**
     * 资源文件生成(会重刷)
     *
     * @param rconstantResource rconstant资源
     */
    public static void copyResourceFile(QsConstant.Resources rconstantResource) {
        try {
            File targetFile = new File(rconstantResource.getTargetPath());
            // 创建上级目录
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
            // 如果文件存在，则删除文件
            if (targetFile.exists()) {
                if (targetFile.delete()) {
                    copyResourceFile(rconstantResource);
                    return;
                }
            }
            InputStream resourceAsStream = QsBeanfunApplication.class.getClassLoader().getResourceAsStream(rconstantResource.getSourcePath());
            Files.copy(resourceAsStream, targetFile.toPath());
        } catch (Exception e) {
            log.error("输出文件发生异常 e={}", e.getMessage(), e);
        }
    }

    /**
     * 读取配置
     *
     * @return {@link String}
     */
    public static ConfigModel readConfig() {
        ConfigModel configModel = new ConfigModel();
        try {
            File jsonFile = new File(QsConstant.PATH_APP_CONFIG);
            if (!jsonFile.exists()) {
                return configModel;
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
            String jsonStr = sb.toString();
            if (StringUtils.isNotEmpty(jsonStr)) {
                configModel = JSON.parseObject(jsonStr, new TypeReference<ConfigModel>() {
                });
            }
            if (DataTools.collectionIsEmpty(configModel.getActPwds())) {
                configModel.setActPwds(new ArrayList<>());
            }
            FileTools.saveConfig(configModel);
        } catch (IOException e) {
            log.error("文件读取异常 e={}", e.getMessage(), e);
        }
        return configModel;
    }

    /**
     * 保存配置
     *
     * @param jsonData json数据
     * @return boolean
     */
    public static boolean saveConfig(Object jsonData) {
        String content = JSON.toJSONString(jsonData, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
        try {
            File file = new File(QsConstant.PATH_APP_CONFIG);
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
