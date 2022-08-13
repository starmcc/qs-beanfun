package com.starmcc.beanfun.utils;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;

@Slf4j
public class MD5 {


    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};


    private static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }


    public static String to32(String source) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(source.getBytes());
            byte messageDigest[] = digest.digest();
            return toHexString(messageDigest);
        } catch (Exception e) {
            log.error("异常 e={}", e.getMessage(), e);
            return source;
        }
    }

    public static String to16(String source) {
        return to32(source).substring(8, 24);
    }


}