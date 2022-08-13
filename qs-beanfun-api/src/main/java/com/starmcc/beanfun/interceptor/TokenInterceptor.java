package com.starmcc.beanfun.interceptor;

import com.starmcc.beanfun.config.PassToken;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.exception.LoginException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Token拦截器
 *
 * @author starmcc
 * @date 2022/06/09
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (Objects.isNull(passToken)) {
                if (Objects.isNull(QsConstant.beanfunModel) || StringUtils.isBlank(QsConstant.beanfunModel.getToken())) {
                    throw new LoginException();
                }
            }
        }
        return true;
    }
}
