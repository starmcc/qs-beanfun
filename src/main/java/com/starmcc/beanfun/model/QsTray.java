package com.starmcc.beanfun.model;

import com.starmcc.beanfun.QsBeanfunApplication;
import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.manager.FrameManager;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

@Slf4j
public class QsTray {


    public static TrayIcon init(Stage stage) {
        // 判断系统是否托盘
        if (!SystemTray.isSupported()) {
            return null;
        }
        // 创建一个托盘图标对象
        Image image = Toolkit.getDefaultToolkit().getImage(QsBeanfunApplication.class.getClassLoader().getResource("static/images/ico.png"));
        TrayIcon trayIcon = new TrayIcon(image);
        trayIcon.setToolTip(QsConstant.APP_NAME);
        trayIcon.setImageAutoSize(true);
        // 创建弹出菜单
        PopupMenu menu = new PopupMenu();
        // 添加一个用于打开的按钮
        MenuItem openItem = new MenuItem("open");
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FrameManager.getInstance().runLater(() -> {
                    if (stage.isIconified()) {
                        stage.setIconified(false);
                    }
                    if (!stage.isShowing()) {
                        stage.show();
                    }
                    stage.toFront();
                });
            }
        });
        // 添加弹出菜单到托盘图标
        menu.add(openItem);
        // 添加一个用于退出的按钮
        MenuItem exitOpen = new MenuItem("exit");
        exitOpen.addActionListener(actionEvent -> {
            Platform.exit();
            remove(trayIcon);
        });
        // 添加弹出菜单到托盘图标
        menu.add(exitOpen);
        trayIcon.setPopupMenu(menu);
        // 将托盘图表添加到系统托盘
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                // 处理鼠标双击
                if (evt.getClickCount() == 2) {
                    FrameManager.getInstance().runLater(() -> {
                        if (stage.isIconified()) {
                            stage.setIconified(false);
                        }
                        if (stage.isShowing()) {
                            stage.hide();
                        } else {
                            stage.show();
                            stage.toFront();
                        }
                    });
                }
                super.mouseClicked(evt);
            }
        });
        return trayIcon;
    }


    public static void show(TrayIcon trayIcon) {
        // 判断系统是否托盘
        if (!SystemTray.isSupported() || Objects.isNull(trayIcon)) {
            return;
        }
        // 获取系统托盘
        SystemTray tray = SystemTray.getSystemTray();
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            log.error("添加系统托盘发生异常 e={}", e.getMessage(), e);
        }
    }

    public static void remove(TrayIcon trayIcon) {
        // 判断系统是否托盘
        if (!SystemTray.isSupported() || Objects.isNull(trayIcon)) {
            return;
        }
        // 获取系统托盘
        SystemTray tray = SystemTray.getSystemTray();
        tray.remove(trayIcon);
    }
}