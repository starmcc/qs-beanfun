# QsBeanfun

## 介绍

![logo](./src/main/resources/static/images/ico.png)

Please forgive me for not writing this document in English.

I don't have a lot of energy to do it.

秋水登录器并不是游戏橘子数位科技开发的官方登录器

引用[LR区域模拟元件](https://github.com/InWILL/Locale_Remulator)，支持`32/64bit`台服新枫之谷游戏运行。

支持港号/台号/二维码的方式登录,内置各种实用小功能!

**遵循MIT开源协议**，如遇到问题或 Bug 欢迎提交 Issues。

## 安装

[**Releases-点击进入下载页面**](https://github.com/starmcc/qs-beanfun/releases)

**请下载最新`QsBeanfun-install.exe`进行安装即可。**

---

如果您是Java开发人员，拥有 **JRE1.8 x64** 支持。

可下载`QsBeanfun.exe`后创建同级目录`JRE`，并将运行环境复制该目录中即可运行。

[JDK1.8 64位 Offline Installer](https://www.oracle.com/java/technologies/downloads/#java8-windows)

---

秋水登录器运行后会在当前运行目录创建`.\qs-data`目录。

以释放依赖文件和日志文件使用。

**特别注意：秋水登录器安装路径不能存在中文，不然无法启动游戏**

## 更新

> 方法一

仅需下载`QaBeanfun.exe`覆盖原安装目录。

> 方法二

或使用秋水登录器内置自动更新功能。

> 注意

`4.1.0`之前的版本更新需要卸载后重新安装`install`

PS: 需更换JRE环境64位

## 实现

| 功能                  |      版本      |            描述             |
|---------------------|:------------:|:-------------------------:|
| 香港/台湾橘子 账号登录/二维码登录  | 台号登录需4.0.0以上 |   通过模拟网页登录，从此告别在网页上启动游戏   |
| 模拟繁体环境启动新枫之谷游戏      |     ALL      |      告别入谷需要设置区域语言的麻烦      |
| 装备卷轴计算器             |     ALL      |         方便计算装备价值          |
| 免输入账号密码进游戏模式        |     ALL      |        实现方式与网页登录相同        |
| 自动关闭游戏Play开始窗口      |     ALL      |         加快进入游戏速度          |
| 中台实时汇率计算            |     ALL      |         一个实用的小功能          |
| 自动轮烧                |     ALL      |   使用Java虚拟机模拟按键，绕过NGS检测   |
| 新枫之谷实用网站快捷导航        |     ALL      | 一些常用的网站整理，妈妈再也不用担心找不到官网啦~ |
| 港号免beanfun插件        |   3.0.1以上    |     登录器在手,登录->启动一气呵成~     |
| 游戏自动录像功能(需FFMPEG支持) |   4.0.0以上    |     避免交易/抢图争议、检举外挂/辅助     |
| JRE(64位)            |   4.1.0以上    |    4.1.0之前使用32位JRE运行环境    |
| 自动阻止游戏自动更新          |   4.0.0以上    |    防止自动更新带来的灾难，比如文件损坏~    |
| 其他内置功能              |   4.0.0以上    |           便捷功能            |

## 环境

- `Windows7`以上，以`Win10`环境为发布标准。
- [JRE1.8 x64-64位 (Install包已内置)](https://www.oracle.com/java/technologies/downloads/#jre8-windows)
- [Microsoft Visual C++ Redistributable VC环境](https://aka.ms/vs/17/release/vc_redist.x64.exe)

## 依赖

- JNA - Java 调用DLL支持
- FastJson - 阿里JSON解析支持
- JavaFX - JavaFX-UI库
- Httpclient - apache HTTP请求支持
- [LR区域模拟元件支持](https://github.com/InWILL/Locale_Remulator) 模拟繁体区域启动游戏
- JxBrowser 第三方浏览器插件 [来源](https://blog.csdn.net/weixin_43852094/article/details/121157752) [官网](https://jxbrowser-support.teamdev.com/)

## 开发/编译

1. 导入项目后，需要手动将`lib`目录下的第三方依赖打入本地仓库，否则报红。
```
mvn install:install-file -Dfile=./lib/jxbrowser-6.21.jar -DgroupId=com.teamdev.jxbrowser -DartifactId=jxbrowser -Dversion=6.21 -Dpackaging=jar
mvn install:install-file -Dfile=./lib/jxbrowser-win64-6.21.jar -DgroupId=com.teamdev.jxbrowser -DartifactId=jxbrowser-win64 -Dversion=6.21 -Dpackaging=jar
mvn install:install-file -Dfile=./lib/license-6.21.jar -DgroupId=com.teamdev.jxbrowser -DartifactId=license -Dversion=6.21 -Dpackaging=jar
```
请直接运行上述命令即可。

[编译详细教程](./build/README.md)

## 安全

每次发布Release都会贴出工具的(Hash)哈希值

请各位下载工具后校验Hash值是否安全

怎么查询哈希值？

```
certutil -hashfile 该程序路径
```

回车后会出现hash值。

## 结语

1. 所有不怀好意的指责，都需要时间去验证和打磨。
2.
能够帮助别人，分享自己的技术实现方案是一件非常愉快的事情，也希望有一些会本系语言的朋友一起优化它，让它更人性化，即使只是我的一厢情愿~~
3. 凡是第三方工具都是橘子官方明令禁止使用的，最好的方式就是将系统转为繁体语言后使用网页登录，望客官悉知。
4. 关于自动轮烧：鉴于不属于升级挂机/破坏游戏环境等因素才开放的，在此也强烈谴责那些为了一己私欲写脚本辅助等严重影响游戏环境的家伙。
5. 我只是茫茫人海中一个热爱枫谷懂点皮毛技术的玩家，希望新枫之谷会一直运营下去，长盛不衰！
6. **工具仅供学习使用，下载后请24小时内删除，遵循MIT开源协议**

最后奉劝那些指鹿为马的家伙，请心存善念，人生才会充满阳光，加油枫谷人。

> 枫谷作伴，潇潇洒洒...

# 赞赏

如果您也觉得本项目对您有所帮助，请慷慨的为作者送上一笔赞赏。

在此的每一笔犒劳都将让作者铭记于心！

![Appreciate](./Appreciate.png)
