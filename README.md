# QsBeanfun 4

<p align="center">
    <a target="_blank" href="https://github.com/starmcc/qs-beanfun/blob/master/LICENSE">
        <img alt="LICENSE" src="https://img.shields.io/badge/License-MIT-lightgrey"/>
    </a>
    <a target="_blank" href="https://github.com/starmcc/qs-beanfun">
        <img alt="stars" src="https://img.shields.io/github/stars/starmcc/qs-beanfun?label=Stars"/>
    </a>
    <a target="_blank" href="https://github.com/starmcc/qs-beanfun/releases/latest">
        <img alt="Releases" src="https://img.shields.io/github/v/release/starmcc/qs-beanfun?display_name=tag&label=Latest&color=red"/>
    </a>
    <a target="_blank" href="https://www.qstms.com/">
        <img alt="童年小梦" src="https://img.shields.io/badge/HomePage-%E7%AB%A5%E5%B9%B4%E5%B0%8F%E6%A2%A6-orange"/>
    </a>
  </p>
<p align="center">
    <a target="_blank" href="https://github.com/starmcc/qs-beanfun/releases/latest">
        <img alt="Downloads" src="https://img.shields.io/github/downloads/starmcc/qs-beanfun/total?label=Downloads"/>
    </a>
    <a target="_blank" href="https://github.com/starmcc/qs-beanfun/commits/master">
        <img alt="GitHub last commit" src="https://img.shields.io/github/last-commit/starmcc/qs-beanfun?label=LastCommit">
    </a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/downloads/#jre8-windows">
        <img alt="JRE" src="https://img.shields.io/badge/JVM-1.8(64bit)-8d38dc"/>
    </a>
</p>
<p align="center">
    <span style="font-weight:bold; color:#F57C00;">简体中文</span>
    <a href="./README-TW.md">繁體中字</a>
</p>

## 介绍

<image style="width: 120px; height: 120px;" src="./src/main/resources/static/images/ico.png"></image>

Please forgive me for not writing this document in English.

I don't have a lot of energy to do it.

秋水登录器并不是游戏橘子数位科技开发的官方登录器

引用[LR区域模拟元件](https://github.com/InWILL/Locale_Remulator)，支持`32/64bit`台服新枫之谷游戏运行。

<p style="font-size: 20px;color: palevioletred">于2024年7月我做了个糊涂又明智的决定</p>
<p style="font-size: 20px;color: palevioletred">QsBeanfun5.x系列将采用Python语言重新开发！</p>
<p style="color: palevioletred">至于什么时候完成，慢慢等吧，或许很快！</p>
<p style="font-size: 18px;color: #00a1ff">QsBeanfun 4.x.x 系列为永存维护版本</p>

> 支持登录方式

1. 香港账号 - (账密/双重验证)
2. 台湾账号 - (账密/二维码/QR码)
3. 内置各种实用小功能

**遵循MIT开源协议**，如遇问题或 Bug 亦或交流，请移步 Issues。

## 安装

[**Releases-点击进入下载页面**](https://github.com/starmcc/qs-beanfun/releases)

**下载最新`QsBeanfun.zip`开箱即用。**

---

如果您是Java开发人员，拥有 **JRE1.8 x64** 支持。

可下载`QsBeanfun.exe`后创建同级目录`JRE`，并将运行环境复制该目录中即可运行。

[JDK1.8 64位 Offline Installer](https://www.oracle.com/java/technologies/downloads/#java8-windows)

---

秋水登录器运行后会在当前运行目录创建`.\qs-data`目录。

以释放依赖文件和日志文件使用。

> **特别注意：**

`QsBeanfun.exe`目录不能存在中文，否则无法启动游戏

## 更新

> 方法一

仅需下载`QaBeanfun.exe`覆盖原安装目录。

> 方法二

使用秋水登录器内置自动更新功能。

## 实现功能

| 功能                                              |
|-------------------------------------------------|
| 香港/台湾橘子 <br/>普通登录、双重登录、二维码登录<br/>Ps: 无需安裝游戏橘子插件 |
| 模拟繁体操作系统环境运行[新枫之谷]                              |
| 内置会员充值、客服中心、会员中心                                |
| 免输账密启动/进入游戏                                     |
| 自动屏蔽游戏启动窗口（可选）                                  |
| 自动阻止游戏自动更新（可选）                                  | 
| 一键跳过NGS进程                                       | 
| 新枫之谷实用网站快捷导航                                    |
| 游戏自动录像功能(需FFMPEG支持)                             |
| 其他内置功能                                          |

## 环境与依赖

> 环境

- `Windows7`以上，以`Win10`环境为发布标准。
- [JRE1.8 x64-64位(Zip已包含)](https://www.oracle.com/java/technologies/downloads/#jre8-windows)
- [Microsoft Visual C++ Redistributable VC环境](https://aka.ms/vs/17/release/vc_redist.x64.exe)

> 依赖

- JNA - Java 调用DLL支持
- FastJson - 阿里JSON解析支持
- JavaFX - JavaFX-UI库
- Httpclient - apache HTTP请求支持
- [LR区域模拟元件支持](https://github.com/InWILL/Locale_Remulator) 模拟繁体区域启动游戏
- JxBrowser
  第三方浏览器插件 [来源](https://blog.csdn.net/weixin_43852094/article/details/121157752) [官网](https://jxbrowser-support.teamdev.com/)

## 开发与编译

> 开发

1. 导入项目后，需要手动将`lib`目录下的第三方依赖打入本地仓库，否则报红。

```
mvn install:install-file -Dfile=./lib/jxbrowser-6.21.jar -DgroupId=com.teamdev.jxbrowser -DartifactId=jxbrowser -Dversion=6.21 -Dpackaging=jar
mvn install:install-file -Dfile=./lib/jxbrowser-win64-6.21.jar -DgroupId=com.teamdev.jxbrowser -DartifactId=jxbrowser-win64 -Dversion=6.21 -Dpackaging=jar
mvn install:install-file -Dfile=./lib/license-6.21.jar -DgroupId=com.teamdev.jxbrowser -DartifactId=license -Dversion=6.21 -Dpackaging=jar
```

请直接运行上述命令即可。

> 编译 -> [编译详细教程](./build/README.md)

## 安全

每次发布`Release`都会贴出工具的(`Hash`)哈希值

请各位下载工具后校验`Hash`值是否安全

怎麽查询哈希值？

```
certutil -hashfile 该程序路径
```

回车后会出现hash值。

> 关于自行编译后与官方发布的Hash值不一致问题

您会发现每次编译的jar包`hash`值并不相同，请以发布的`jar`包`hash`值为准。

如果不放心可自行查阅`jar`包中的源码，`jar`包可用`RAR`软件打开。

`com/starmcc`目录下即是登录器源码。

> 怎麽反编译`class`文件？ [class文件的反编译过程](https://blog.csdn.net/qq_39674002/article/details/109735298)

## 结语

1. 所有恶意的指责，都需历经时间的检验与雕琢。
2. 能帮助他人、分享自身的技术实现方案是极为愉悦之事，也期望能有一些朋友共同对其进行优化，哪怕这只是我的一己之愿~~
3. 任何第三方工具皆被游戏橘子官方严令禁止使用，最佳方式是将系统转为繁体语言后使用网页登录，望客官知悉。
4. 我仅是茫茫人海中一位热爱新枫之谷且略懂些许技术的玩家，衷心希望新枫之谷能够一直运营，长盛不衰！

**<p style="font-size:18px">本软件仅供学习使用，下载后请24小时内删除</p>**
**<p style="font-size:22px">遵循MIT开源协议</p>**

最后奉劝那些指鹿为马的家伙，请心存善念，人生才会充满阳光。

> 枫谷作伴，潇潇洒洒...

# 赞赏

倘若您也认为本项目对您有益，烦请您慷慨地为作者给予一笔赞赏。

在此收到的每一笔赞赏，都将被作者深深铭记！

<image style="width: 200px; height: 200px;" src="./Appreciate.png"></image>

> 打赏大佬名单，由近到远依次排列~

名单中的是微信名，若想用游戏名，请在备注中填写哟~

再次衷心感谢各位大佬的赞赏，天使定会轻吻善良的您~

若不想展示，可单独联系我删除名字，部分未展示是实在未能找到您的名字

> 名单更新时间：2024-7-29

|           名单           | 金额（RMB） |
|:----------------------:|:-------:|
|          无名氏           |   200   |
|          李素雅           |  20.24  |
|          泡泡茶壶          |    1    |
|          基泥胎美          |   20    |
|         华(中国)          |   20    |
|          奎秃子           |   10    |
|        Andr***         |   20    |
|    COSMOS(PS:喝杯奶茶)     |   30    |
|          咳咳溜           |   20    |
|         不再犹豫z          |   66    |
|          索德渃斯          |   50    |
|           九号           |   10    |
|          Mr·铭          |   20    |
|     A酷田照明-专业美缝-小陈      |   10    |
|         tiger          |   100   |
|     阿樑（PS：谢谢作者大大）      |   10    |
|          稻草人           |   50    |
|          JS.           |   30    |
|           Li           |  18.88  |
|          无所谓           |   20    |
|          潘治文           |    5    |
|         J-hard         |   10    |
|          李素雅           |   10    |
|           莫心           |   10    |
|           1            |   10    |
|      我。（PS：感谢感谢）       |   20    |
|         俾面嗌声林生         |   10    |
|     今天雨下好大（PS：辛苦了）     |   10    |
|       不爱喝阿萨姆的萨满        |   10    |
|          发条橙           |   10    |
|           晨辉           |   20    |
|        徐小姐的黑脸将         |   10    |
|         COOKIE         |   50    |
|          吹吹风           |   38    |
| like sunshine（PS:中杯奶茶） |   22    |
|          周小明           |   20    |
|       阿里跨境^O^陈明初       |   110   |
|         Lydia          |   10    |
|          百年孤寂          |   10    |
|          Azu           |   10    |
|           Kk           |    5    |
|          鸣Zai          |   10    |
|         心（符号）          |   20    |

