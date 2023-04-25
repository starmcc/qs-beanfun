package com.starmcc.beanfun.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.entity.model.ConfigModel;
import com.starmcc.beanfun.manager.ThreadPoolManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 文件工具
 *
 * @author starmcc
 * @date 2022/09/21
 */
@Slf4j
public class FileTools {

    public synchronized static String readFile(File file) {
        Reader reader = null;
        try {
            if (!file.exists()) {
                return "";
            }
            FileReader fileReader = new FileReader(file);
            reader = new InputStreamReader(new FileInputStream(file), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            return sb.toString();
        } catch (IOException e) {
            log.error("文件读取异常 e={}", e.getMessage(), e);
        } finally {
            SystemTools.close(reader);
        }
        return "";
    }

    /**
     * 解压缩资源文件
     *
     * @param resourceFile 资源文件
     */
    public synchronized static void unzipResourceFile(QsConstant.PluginEnum resourceFile) {
        try {
            InputStream resourceAsStream = FileTools.class.getClassLoader().getResourceAsStream(resourceFile.getSourcePath());
            ZipInputStream zipInputStream = new ZipInputStream(resourceAsStream, Charset.forName("gbk"));
            //读取一个目录
            ZipEntry nextEntry = zipInputStream.getNextEntry();
            //不为空进入循环
            while (nextEntry != null) {
                String name = nextEntry.getName();
                File file = new File(resourceFile.getTargetPath() + "\\" + name);
                //如果是目录，创建目录
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                //文件则写入具体的路径中
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                int n;
                byte[] bytes = new byte[1024];
                while ((n = zipInputStream.read(bytes)) != -1) {
                    bufferedOutputStream.write(bytes, 0, n);
                }
                //关闭流
                bufferedOutputStream.close();
                fileOutputStream.close();
                //关闭当前布姆
                zipInputStream.closeEntry();
                //读取下一个目录，作为循环条件
                nextEntry = zipInputStream.getNextEntry();
            }
        } catch (IOException e) {
            log.error("IO error e={}", e.getMessage(), e);
        }
    }

    /**
     * 删除文件夹
     *
     * @param folder 文件夹
     * @return boolean
     */
    public synchronized static boolean deleteFolder(File folder) {
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
    public synchronized static boolean deleteFile(File file) {
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
     * 读取配置
     *
     * @return {@link String}
     */
    public synchronized static ConfigModel readConfig() {
        ConfigModel configModel = null;
        try {
            File file = new File(QsConstant.PATH_APP_CONFIG);
            String fileStr = readFile(file);
            if (StringUtils.isNotBlank(fileStr)) {
                configModel = JSON.parseObject(fileStr, new TypeReference<ConfigModel>() {
                });
            }
        } catch (Exception e) {
            log.error("文件读取异常 e={}", e.getMessage(), e);
        } finally {
            if (Objects.isNull(configModel)) {
                configModel = new ConfigModel();
                FileTools.saveConfig(configModel);
            }
        }
        return configModel;
    }


    /**
     * 保存配置
     *
     * @param model json数据
     * @return boolean
     */
    public synchronized static void saveConfig(ConfigModel model) {
        ThreadPoolManager.execute(() -> {
            String content = JSON.toJSONString(model, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
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
            } catch (Exception e) {
                log.error("文件写入异常 e={}", e.getMessage(), e);
            }
        });
    }


    /**
     * 写文件
     *
     * @param content  内容
     * @param filePath 文件路径
     */
    public synchronized static void writeFile(String content, String filePath) {
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
            SystemTools.close(write);
        }
    }
}
