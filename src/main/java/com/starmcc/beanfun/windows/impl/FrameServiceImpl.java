package com.starmcc.beanfun.windows.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starmcc.beanfun.client.HttpClient;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.model.JFXStage;
import com.starmcc.beanfun.utils.ThreadUtils;
import com.starmcc.beanfun.windows.FrameService;
import com.starmcc.beanfun.windows.dll.EService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

import java.awt.*;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author starmcc
 * @date 2022/9/14 17:55
 */
@Slf4j
public class FrameServiceImpl implements FrameService {
    @Override
    public void openWindow(QsConstant.Page page, Stage parentStage) throws Exception {
        openWindow(page, parentStage, page.getBuildMethod());
    }

    @Override
    public void openWindow(QsConstant.Page page, Consumer<JFXStage> build) throws Exception {
        openWindow(page, null, build);
    }

    @Override
    public void openWindow(QsConstant.Page page) throws Exception {
        openWindow(page, null, page.getBuildMethod());
    }

    @Override
    public boolean closeWindow(JFXStage jfxStage) {
        if (Objects.isNull(jfxStage) || Objects.isNull(jfxStage.getStage())) {
            return false;
        }
        jfxStage.getStage().close();
        return true;
    }

    @Override
    public void openWebUrl(String url) {
        try {
            Desktop desktop = Desktop.getDesktop();
            if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
                URI uri = new URI(url);
                desktop.browse(uri);
            }
        } catch (Exception e) {
            log.error("执行发生异常 e={}", e.getMessage(), e);
        }
    }


    void openWindow(QsConstant.Page page, Stage parentStage, Consumer<JFXStage> build) throws Exception {
        Stage stage = new Stage();
        if (Objects.nonNull(parentStage)) {
            stage.initOwner(parentStage);
        }
        Parent parent = FXMLLoader.load(ThreadUtils.class.getResource(page.getUrl()));
        JFXStage jfxStage = JFXStage.of(stage, parent);
        jfxStage.setTitle(page.getTitle());
        // 构建外部方法
        if (Objects.nonNull(build)) {
            build.accept(jfxStage);
        }
        if (page.getShowTitle()) {
            jfxStage.build(() -> {
                if (Objects.nonNull(ThreadUtils.THREAD_POOL)) {
                    ThreadUtils.THREAD_POOL.shutdownNow();
                    ThreadUtils.THREAD_POOL = null;
                }
            });
        }else {
            jfxStage.buildSimple();
        }
        stage.getIcons().add(new Image("static/images/ico.png"));
        stage.setResizable(false);
        stage.setTitle(page.getTitle());
        stage.show();
    }

    @Override
    public void openWebBrowser(String url) {
        CookieStore cookieStore = HttpClient.getInstance().getCookieStore();
        List<Cookie> cookies = cookieStore.getCookies();
        JSONArray jsonArr = new JSONArray();
        for (Cookie cookie : cookies) {
            JSONObject obj = new JSONObject();
            obj.put("domain", cookie.getDomain());
            obj.put("key", cookie.getName());
            obj.put("val", cookie.getValue());
            System.out.println(cookie.getDomain() + "=" + cookie.getValue());
            jsonArr.add(obj);
        }
        EService.INSTANCE.openBrowser(url, "", jsonArr.toJSONString());
    }
}
