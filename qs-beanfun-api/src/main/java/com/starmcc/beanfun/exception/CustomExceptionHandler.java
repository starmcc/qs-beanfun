package com.starmcc.beanfun.exception;

import com.starmcc.qmframework.controller.QmResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
@Controller
@RequestMapping(value = "/error")
public class CustomExceptionHandler {

    @ExceptionHandler(LoginException.class)
    @ResponseBody
    public String httpRequestMethodNotSupportedException(HttpServletResponse response, Exception e) {
        response.setStatus(200);
        return QmResult.loginNotIn();
    }
}