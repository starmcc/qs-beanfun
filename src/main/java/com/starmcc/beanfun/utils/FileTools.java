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
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 文件工具
 *
 * @author starmcc
 * @date 2022/09/21
 */
@Slf4j
public class FileTools {

    /**
     * 解压缩资源文件
     *
     * @param resourceFile 资源文件
     * @return {@link String}
     */
    public static String unzipResourceFile(QsConstant.PluginEnum resourceFile) {
        final int buffer = 1024;
        String name = "";
        BufferedOutputStream dest = null;
        BufferedInputStream is = null;
        try {
            ZipEntry entry = null;
            Enumeration<URL> resources = QsBeanfunApplication.class.getClassLoader().getResources(resourceFile.getSourcePath());
            String path = resources.nextElement().getPath();
            ZipFile zipfile = new ZipFile(path);
            File targetFile = new File(resourceFile.getTargetPath());
            if (!targetFile.exists()) {
                targetFile.mkdirs();
            }
            Enumeration dir = zipfile.entries();
            // 检查文件夹是否存在
            while (dir.hasMoreElements()) {
                entry = (ZipEntry) dir.nextElement();
                String abc = targetFile.getPath() + "\\" + entry.getName();
                File file = new File(abc).getParentFile();
                if (!file.exists()) {
                    file.mkdirs();
                }
            }
            Enumeration e = zipfile.entries();
            while (e.hasMoreElements()) {
                entry = (ZipEntry) e.nextElement();
                if (entry.isDirectory()) {
                    continue;
                } else {
                    is = new BufferedInputStream(zipfile.getInputStream(entry));
                    int count;
                    byte[] dataByte = new byte[buffer];
                    FileOutputStream fos = new FileOutputStream(targetFile.getPath() + "\\" + entry.getName());
                    dest = new BufferedOutputStream(fos, buffer);
                    while ((count = is.read(dataByte, 0, buffer)) != -1) {
                        dest.write(dataByte, 0, count);
                    }
                    dest.flush();

                }
            }
        } catch (Exception e) {
            log.error("unzip error={}", e.getMessage(), e);
        } finally {
            close(dest, is);
        }
        return name;
    }

    /**
     * 删除文件夹
     *
     * @param folder 文件夹
     * @return boolean
     */
    public static boolean deleteFolder(File folder) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if ((!folder.exists()) || (!folder.isDirectory())) {
            return false;
        }
        boolean flag = true;
        File[] files = folder.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i]);
                if (!flag) {
                    break;
                }
            }
            // 删除子文件夹
            else if (files[i].isDirectory()) {
                flag = deleteFolder(files[i]);
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            return false;
        }
        // 删除当前文件夹
        if (folder.delete()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 删除文件
     *
     * @param file 文件
     * @return boolean
     */
    public static boolean deleteFile(File file) {
        // 如果文件路径只有单个文件
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 关闭
     *
     * @param acArr ac加勒比海盗
     */
    private static void close(AutoCloseable... acArr) {
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
        String content = JSON.toJSONString(jsonData, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
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

    /**
     * 写文件
     *
     * @param content  内容
     * @param filePath 文件路径
     */
    public static void writeFile(String content, String filePath) {
        Writer write = null;
        try {
            // 写到文件
            File file = new File(filePath);
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
