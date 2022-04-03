# QsBeanfun

![image](./src/main/resources/static/images/logo.png)

本工具不是游戏橘子数位科技开发的官方工具

引用LR区域模拟元件，支持32/64位台服新枫之谷游戏。

开源遵循MIT协议，如遇到问题或 Bug 欢迎提交 Issues。

## 下载

[Releases](https://github.com/starmcc/qs-beanfun/releases)

点击上方进入下载页面

如果您拥有Java环境

直接下载`QsBeanfun.exe`即可。

如果你没有Java环境，可以去下载

[**JRE1.8 x86 Offline Installer**](https://www.oracle.com/java/technologies/downloads/#jre8-windows)

傻瓜式安装，请下载`QsBeanfun-install.exe`进行安装即可。

## 工具更新

只需要下载`QaBeanfun.exe`覆盖原安装目录即可。

## 实现功能

1. 香港橘子账号登录，快捷获取动态密码(无需IE网页操作)
2. 模拟繁体环境启动新枫之谷游戏
3. 装备卷轴计算器 （包含荣耀卷轴计算）
4. 免输入账号密码进游戏模式（实现方式与网页登录相同）
5. 中台实时汇率计算
6. 自动轮烧（2分钟自动按B键，3分钟自动按N键）
   - 使用Java虚拟机进行模拟按键，绕过NGS检测
7. 新枫之谷相关网站快捷访问
8. 香港橘子IE一键优化功能

## 运行环境

- Windows7 以上
- [**JRE1.8 x86 Offline Installer**](https://www.oracle.com/java/technologies/downloads/#jre8-windows)
- [Microsoft Visual C++ Redistributable VC环境](https://aka.ms/vs/17/release/vc_redist.x64.exe)
- [橘子官方Beanfun插件](http://hk.download.beanfun.com/beanfun20/beanfun_2_0_93_170_hk.exe)

## 使用

下载安装后，直接运行即可。

第一次运行如未安装橘子官方Beanfun插件则会提示下载。

游戏会在系统用户目录释放依赖文件

目录一般为`C:\Users\用户名\QsBeanfun`

## 依赖

- JNA - Java 调用DLL支持
- FastJson - JSON解析支持
- JavaFX - UI库
- apache - httpclient HTTP请求支持
- eclipse.swt 内置IE浏览器支持
- [LR区域模拟元件支持](https://github.com/InWILL/Locale_Remulator)
- 易语言调用`Beanfun`插件支持[BFService]

## 打包

1. maven 打包 Jar
2. exe4j 打包 exe
3. Resource Hacker 修改 exe 清单管理员模式 
   - `level=requireAdministrator`
4. inno Setup 打包安装 install.exe 安装包