package com.starmcc.beanfun.windows;

import com.starmcc.beanfun.controller.LoginController;
import lombok.Data;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.*;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * swt浏览器
 *
 * @author starmcc
 * @date 2022/03/23
 */
@Data
public class SwtWebBrowser {

    private Display display;
    private Shell shell;
    private Browser browser;
    private Text text;

    public static SwtWebBrowser getInstance(final String url) {
        return new SwtWebBrowser(url);
    }

    private SwtWebBrowser(final String url) {
        display = new Display();
        shell = new Shell(display);
        shell.setSize(880, 600);
        center(display, shell);
        shell.setLayout(new GridLayout(1, true));
        Image image = new Image(shell.getDisplay(), LoginController.class.getClassLoader().getResourceAsStream("static/images/ico.png"));
        shell.setImage(image);

        text = new Text(shell, SWT.NONE);
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        text.setLayoutData(gridData);
        text.setText(url);


        browser = new Browser(shell, SWT.NONE);
        GridData gridData2 = new GridData();
        gridData2.grabExcessHorizontalSpace = true;
        gridData2.grabExcessVerticalSpace = true;
        gridData2.horizontalAlignment = GridData.FILL;
        gridData2.verticalAlignment = GridData.FILL;
        browser.setLayoutData(gridData2);

        browser.addTitleListener(titleEvent -> shell.setText(titleEvent.title));
        browser.addOpenWindowListener(new OpenWindowListener() {
            @Override
            public void open(WindowEvent paramWindowEvent) {
                final Shell shell = new Shell(display);
                final Browser browserTemp = new Browser(shell, SWT.NONE);
                paramWindowEvent.browser = browserTemp;
                //将事件用我的浏览器打开
                paramWindowEvent.display.asyncExec(new Runnable() {
                    //swt对外观部件的更改必须在SWT异步线程中进行
                    @Override
                    public void run() {
                        String url = browserTemp.getUrl();
                        browser.setUrl(url);
                        shell.close();
                    }
                });
            }
        });

        browser.addLocationListener(new LocationListener() {
            @Override
            public void changing(LocationEvent locationEvent) {
                text.setText(locationEvent.location);
            }

            @Override
            public void changed(LocationEvent locationEvent) {
                text.setText(locationEvent.location);
            }
        });

        text.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {

            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                System.out.println(keyEvent.keyCode);
                if (keyEvent.keyCode == 13) {
                    browser.setUrl(text.getText());
                }
            }
        });

        browser.setUrl(url);
    }

    /**
     * 设置窗口位于屏幕中间
     *
     * @param display 设备
     * @param shell   要调整位置的窗口对象
     */

    public static void center(Display display, Shell shell) {
        Rectangle bounds = display.getPrimaryMonitor().getBounds();

        Rectangle rect = shell.getBounds();

        int x = bounds.x + (bounds.width - rect.width) / 2;

        int y = bounds.y + (bounds.height - rect.height) / 2;

        shell.setLocation(x, y);

    }


    public void open() {
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        display.dispose();
    }

}