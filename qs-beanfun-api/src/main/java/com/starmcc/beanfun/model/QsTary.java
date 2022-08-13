package com.starmcc.beanfun.model;

import com.starmcc.beanfun.constant.QsConstant;
import com.starmcc.beanfun.utils.FrameUtils;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;

@Slf4j
public class QsTary {

    /**
     * 初始化
     */
    public static void init() {
        // 判断系统是否托盘
        if (!SystemTray.isSupported()) {
            return;
        }
        // 创建一个托盘图标对象
        URL icoUrl = FrameUtils.class.getClassLoader().getResource("static/images/ico.png");
        Image image = Toolkit.getDefaultToolkit().getImage(icoUrl);
        TrayIcon icon = new TrayIcon(image);
        icon.setToolTip(QsConstant.APP_NAME + " v" + QsConstant.APP_VERSION);
        icon.setImageAutoSize(true);
        // 创建弹出菜单
        PopupMenu menu = new PopupMenu();
        // 添加一个用于打开的按钮
        MenuItem openItem = new MenuItem("open");
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Runtime.getRuntime().exec("cmd /c start " + QsConstant.serverAddress);
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        });
        // 添加弹出菜单到托盘图标
        menu.add(openItem);
        // 添加一个用于退出的按钮
        MenuItem exitOpen = new MenuItem("exit");
        exitOpen.addActionListener(actionEvent -> System.exit(0));
        // 添加弹出菜单到托盘图标
        menu.add(exitOpen);
        icon.setPopupMenu(menu);
        // 获取系统托盘
        SystemTray tray = SystemTray.getSystemTray();
        try {
            tray.add(icon);
        } catch (AWTException e) {
            log.error("添加系统托盘发生异常 e={}", e.getMessage(), e);
        }
        // 将托盘图表添加到系统托盘
        icon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                // 处理鼠标双击
                if (evt.getClickCount() == 2) {
                    try {
                        Runtime.getRuntime().exec("cmd /c start " + QsConstant.serverAddress);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                super.mouseClicked(evt);
            }
        });
    }


}
