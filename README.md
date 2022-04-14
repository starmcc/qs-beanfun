# QsBeanfun

![image](./src/main/resources/static/images/logo.png)

本工具不是游戏橘子数位科技开发的官方工具

引用LR区域模拟元件，支持32/64位台服新枫之谷游戏。

**遵循MIT开源协议**，如遇到问题或 Bug 欢迎提交 Issues。

## 下载安装使用

[Releases-点击进入下载页面](https://github.com/starmcc/qs-beanfun/releases)

**傻瓜式安装，请下载`QsBeanfun-install.exe`进行安装即可。**

**第一次运行如未安装橘子官方Beanfun插件则会提示下载。**

---

拥有Java环境的使用方法：

直接下载`QsBeanfun.exe`运行就可以了。

这里可以下载环境哦

[**JRE1.8 32位 Offline Installer**](https://www.oracle.com/java/technologies/downloads/#jre8-windows)

---

游戏会在系统用户目录释放依赖文件

目录一般为`C:\Users\用户名\QsBeanfun`

## 工具更新

只需要下载`QaBeanfun.exe`覆盖原安装目录。

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

## 依赖

- JNA - Java 调用DLL支持
- FastJson - JSON解析支持
- JavaFX - UI库
- apache - httpclient HTTP请求支持
- eclipse.swt 内置IE浏览器支持
- [LR区域模拟元件支持](https://github.com/InWILL/Locale_Remulator)
- 易语言调用`Beanfun`插件支持 **BFService**

## 打包

1. maven 打包 Jar
2. exe4j 打包 exe
3. Resource Hacker 修改 exe 清单管理员模式 
   - `level=requireAdministrator`
4. inno Setup 打包安装 install.exe 安装包

## 结语

1. 所有不怀好意的指责，都需要时间去验证。
2. 能够帮助别人，分享自己的技术实现方案是一件非常愉快的事情，也希望有一些会本系语言的朋友一起学习，一起优化它，让它更人性化。
3. 凡是登录器、第三方工具，都是官方明令不允许使用的，最好就是将系统转为繁体语言后使用IE进行网页登录，望客官悉知。
4. 关于自动轮烧：鉴于不属于升级挂机、破坏游戏环境等才放出来的，在此也强烈谴责那些为了一己私欲写脚本辅助等严重影响游戏环境的家伙，后续如有问题会直接干掉这个功能。
5. 我只是茫茫人海中一个热爱枫谷懂点皮毛技术的玩家，希望新枫之谷会一直运营下去，长盛不衰！
6. **工具仅供学习使用，下载后请24小时内删除，遵从MIT开源协议**

>  枫谷作伴，潇潇洒洒..