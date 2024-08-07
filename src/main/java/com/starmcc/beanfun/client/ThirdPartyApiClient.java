package com.starmcc.beanfun.client;

import com.starmcc.beanfun.entity.client.QsHttpResponse;
import com.starmcc.beanfun.entity.client.ReqParams;
import com.starmcc.beanfun.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 第三方api客户端
 *
 * @author starmcc
 * @date 2022/03/24
 */
@Slf4j
public class ThirdPartyApiClient {


    /**
     * 获取实时汇率（中台）
     *
     * @return {@link BigDecimal}
     */
    public static BigDecimal getCurrentRateChinaToTw() {
        try {
            ReqParams reqParams = ReqParams.getInstance()
                    .addParam("from", "CNY").addParam("to", "TWD").addParam("q", "1");
            QsHttpResponse qsHttpResponse = HttpClient.getInstance().get("https://qq.ip138.com/hl.asp", reqParams);
            if (!qsHttpResponse.getSuccess()) {
                return BigDecimal.ZERO;
            }
            String html = qsHttpResponse.getContent();
            List<List<String>> regex = RegexUtils.regex(RegexUtils.Constant.COMMON_RATE_POINTS, html);
            String current = RegexUtils.getIndex(0, 1, regex);
            if (StringUtils.isBlank(current)){
                return BigDecimal.ZERO;
            }
            return StringUtils.isNotBlank(current) ? new BigDecimal(current) : BigDecimal.ZERO;
        } catch (Exception e) {
            log.error("获取实时汇率异常 e={}", e.getMessage(), e);
            return BigDecimal.ZERO;
        }

    }


}
