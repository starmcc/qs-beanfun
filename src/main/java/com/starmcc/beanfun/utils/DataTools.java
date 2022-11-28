package com.starmcc.beanfun.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Scanner;

/**
 * 数据工具
 *
 * @author starmcc
 * @date 2022/09/21
 */
@Slf4j
public class DataTools {

    private static final String AES_DEFAULT_KEY = "2022KEYQSBEANFUNACTPWDAESENC";

    public static boolean collectionIsEmpty(final Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    public static boolean collectionIsNotEmpty(final Collection<?> coll) {
        return !collectionIsEmpty(coll);
    }


    /**
     * 获得计算机惟一id
     *
     * @return {@link String}
     */
    public static String getComputerUniqueId() {
        String result = getCpuId() + getHardDiskId();
        if (StringUtils.isBlank(result)) {
            return AES_DEFAULT_KEY;
        }
        return DigestUtils.md5Hex(result);
    }

    /**
     * 获取cpuId
     *
     * @return {@link String}
     */
    public static String getCpuId() {
        try {
            // wmic CPU get ProcessorID
            return runCmd(2, "wmic", "CPU", "get", "ProcessorID");
        } catch (Exception e) {
            log.error("异常 e={}", e.getMessage(), e);
        }
        return "";
    }

    /**
     * 获取硬盘id
     *
     * @return {@link String}
     */
    public static String getHardDiskId() {
        try {
            return runCmd(2, "wmic", "path", "win32_physicalmedia", "get", "serialnumber");
        } catch (Exception e) {
            log.error("异常 e={}", e.getMessage(), e);
        }
        return "";
    }

    /**
     * 运行命令
     *
     * @param line 返回第几行结果，0返回所有
     * @param cmd  命令
     * @return 结果
     */
    public static String runCmd(int line, String... cmd) throws Exception {
        Process process;
        Scanner sc = null;
        StringBuffer sb = new StringBuffer();
        try {
            process = Runtime.getRuntime().exec(cmd);
            process.getOutputStream().close();
            sc = new Scanner(process.getInputStream());
            int i = 0;
            while (sc.hasNext()) {
                i++;
                String str = sc.next();
                if (line <= 0) {
                    sb.append(str).append("\r\n");
                } else if (i == line) {
                    return str.trim();
                }
            }
            sc.close();
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            try {
                SystemTools.closeThrow(sc);
            } catch (Exception e) {
                throw new Exception(e);
            }
        }
        return sb.toString();
    }

}
