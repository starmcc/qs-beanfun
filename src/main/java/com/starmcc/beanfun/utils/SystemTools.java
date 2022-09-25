package com.starmcc.beanfun.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 系统工具
 *
 * @author starmcc
 * @date 2022/09/26
 */
@Slf4j
public class SystemTools {

    public synchronized static void close(AutoCloseable... acArr) {
        for (AutoCloseable ac : acArr) {
            if (Objects.isNull(ac)) {
                continue;
            }
            try {
                ac.close();
            } catch (Exception e) {
                log.error("close error {}", e.getMessage(), e);
            }
        }
    }


    public synchronized static void closeThrow(AutoCloseable... acArr) throws Exception {
        Exception throwExc = null;
        for (AutoCloseable ac : acArr) {
            if (Objects.isNull(ac)) {
                continue;
            }
            try {
                ac.close();
            } catch (Exception e) {
                log.error("close error {}", e.getMessage(), e);
                throwExc = e;
            }
        }
        if (Objects.nonNull(throwExc)) {
            throw new RuntimeException(throwExc);
        }
    }

}
