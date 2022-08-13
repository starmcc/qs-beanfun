package com.starmcc.beanfun.handler;

import com.starmcc.beanfun.utils.FrameUtils;
import com.starmcc.beanfun.windows.MapleStoryAPIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 自动脚本处理程序
 *
 * @author starmcc
 * @date 2022/06/12
 */
@Slf4j
@Component
public class AutoScriptHandler {
    /**
     * 自动游戏输入账号密码
     *
     * @param account  账户
     * @param password 密码
     */
    public void autoGameInputActPwd(String account, String password) {
        FrameUtils.executeThread(() -> {
            try {
                MapleStoryAPIService.getInstance().autoInputActPwd(account, password);
            } catch (Exception e) {
                log.error("异常 e={}", e.getMessage(), e);
            }
        });
    }


}
