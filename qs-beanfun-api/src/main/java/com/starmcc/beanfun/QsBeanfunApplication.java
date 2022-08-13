package com.starmcc.beanfun;

import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.qmframework.EnableQmFramework;
import com.starmcc.qmframework.cros.EnableQmCros;
import com.starmcc.qmframework.exception.EnableQmExceptionHandle;
import javafx.application.Application;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.awt.*;


/**
 * 应用程序
 *
 * @author starmcc
 * @date 2022/03/19
 */
@Slf4j
@EnableAsync
@EnableQmCros
@EnableScheduling
@EnableQmFramework
@SpringBootApplication
@EnableQmExceptionHandle
public class QsBeanfunApplication implements CommandLineRunner {

    @Autowired
    Environment environment;

    /**
     * 启动应用程序
     *
     * @param args args
     */
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(QsBeanfunApplication.class)
                .headless(false).run(args);
        EventQueue.invokeLater(() -> ctx.getBean(QsBeanfunApplication.class));
    }


    @Override
    public void run(String... args) throws Exception {
        log.info("..QsBeanfun启动成功..");
        QsConstant.port = Integer.valueOf(environment.getProperty("local.server.port"));
        QsConstant.serverAddress = String.format("http://localhost:%d", QsConstant.port);
        Application.launch(InitApp.class, args);
    }


    // ============================== app 关闭事件监听 ==============================

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
            }
        });
    }
    // ============================== app 关闭事件监听 end==============================


}
