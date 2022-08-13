package com.starmcc.beanfun.handler;

import com.starmcc.beanfun.constant.QsConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class CellHandler {


    public BigDecimal cellHuiLv(String val, int type) {
        try {
            if (StringUtils.isEmpty(val)) {
                return BigDecimal.ZERO;
            }

            if (!val.matches("[0-9]+(\\.[0-9]+)?")) {
                return BigDecimal.ZERO;
            }
            BigDecimal bigDecimal = new BigDecimal(val);
            if (type == 1) {
                bigDecimal = bigDecimal.multiply(QsConstant.currentRateChinaToTw).setScale(2, BigDecimal.ROUND_HALF_EVEN);
            } else {
                bigDecimal = bigDecimal.divide(QsConstant.currentRateChinaToTw, 2, BigDecimal.ROUND_HALF_EVEN);
            }
            return bigDecimal.stripTrailingZeros();
        } catch (Exception e) {
            log.error("汇率计算异常 e={}", e.getMessage(), e);
            return BigDecimal.ZERO;
        }
    }
}
