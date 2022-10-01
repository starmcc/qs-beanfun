# QsBeanfun

![logo](./src/main/resources/static/images/ico.png)

本登录器不是游戏橘子数位科技开发的官方工具

引用[LR区域模拟元件](https://github.com/InWILL/Locale_Remulator)，支持`32/64bit`台服新枫之谷游戏。

支持港号/台号/二维码的方式登录,内置各种有用的功能!

**遵循MIT开源协议**，如遇到问题或 Bug 欢迎提交 Issues。

Please forgive me for not writing this document in English

I don't have a lot of energy to do it

## 下载安装使用

[**Releases-点击进入下载页面**](https://github.com/starmcc/qs-beanfun/releases)

**请下载最新`QsBeanfun-install.exe`进行安装即可。**

---

如果您是Java开发人员，拥有 **JRE1.8 x86(32bit)** 或 `JDK` 支持。

可下载`QsBeanfun.exe`直接运行或自行编译运行`jar`即可。

这里可以下载运行环境哦

[JRE1.8 32位 Offline Installer](https://www.oracle.com/java/technologies/downloads/#jre8-windows)

---

登录器会在当前运行目录创建`.\qs-data`以释放依赖文件和日志文件

**特别注意：登录器存放路径不能存在中文，不然无法启动游戏**

## 工具更新

只需要下载`QaBeanfun.exe`覆盖原安装目录，或使用登录器内的更新功能即可。

## 实现功能表

| 功能                 |      版本      |            描述             |
|--------------------|:------------:|:-------------------------:|
| 香港/台湾橘子 账号登录/二维码登录 | 台号登录需4.0.0以上 |   通过模拟网页登录，从此告别在网页上启动游戏   |
| 模拟繁体环境启动新枫之谷游戏     |     ALL      |      告别入谷需要设置区域语言的麻烦      |
| 装备卷轴计算器            |     ALL      |         方便计算装备价值          |
| 免输入账号密码进游戏模式       |     ALL      |        实现方式与网页登录相同        |
| 自动关闭游戏Play开始窗口     |     ALL      |         加快进入游戏速度          |
| 自动阻止游戏自动更新         |    4.0.0以上     |    防止自动更新带来的灾难，比如文件损坏~    |
| 中台实时汇率计算           |     ALL      |         一个实用的小功能          |
| 自动轮烧               |     ALL      |   使用Java虚拟机模拟按键，绕过NGS检测   |
| 新枫之谷实用网站快捷导航       |     ALL      | 一些常用的网站整理，妈妈再也不用担心找不到官网啦~ |
| 新港号接口免beanfun插件    |   3.0.1以上    |     登录器在手,登录->启动一气呵成~     |
| 游戏自动录像功能(FFMPEG支持) |   4.0.0以上    |     避免交易/抢图争议、检举外挂/辅助     |
| 其他内置功能             |   4.0.0以上    |           便捷功能            |

## 运行环境

- `Windows7`以上，以`Win10`为上线标准。
- [JRE1.8 x86-32位 (Install包已内置)](https://www.oracle.com/java/technologies/downloads/#jre8-windows)
- [Microsoft Visual C++ Redistributable VC环境](https://aka.ms/vs/17/release/vc_redist.x64.exe)

## 依赖

- JNA - Java 调用DLL支持
- FastJson - 阿里JSON解析支持
- JavaFX - JavaFX-UI库
- Httpclient - apache HTTP请求支持
- MiniBlink - 内置MiniBlink浏览器
- 易语言自编译DLL - 部分微软API调用以易语言编写DLL作基础支持
- [LR区域模拟元件支持](https://github.com/InWILL/Locale_Remulator) 模拟繁体区域启动游戏

## 编译

1. maven 编译 exe
2. inno Setup 打包 qs-beanfun-install.exe 安装包(将Jre一并打包)

[详见编译教程](./build/README.md)

## 安全

每次发布Release都会贴出工具的(Hash)哈希值

请各位下载工具后校验Hash值是否安全

怎么查询哈希值？

```
certutil -hashfile 该程序路径
```

回车后会出现hash值。

## 结语

1. 所有不怀好意的指责，都需要时间去验证。
2. 能够帮助别人，分享自己的技术实现方案是一件非常愉快的事情，也希望有一些会本系语言的朋友一起优化它，让它更人性化，即使只是我的一厢情愿~~
3. 凡是第三方工具都是橘子官方明令不允许使用的，最好的方式就是将系统转为繁体语言后使用网页登录，望客官悉知。
4. 关于自动轮烧：鉴于不属于升级挂机、破坏游戏环境等才放出来的，在此也强烈谴责那些为了一己私欲写脚本辅助等严重影响游戏环境的家伙。
5. 我只是茫茫人海中一个热爱枫谷懂点皮毛技术的玩家，希望游戏会一直运营下去，长盛不衰！
6. **工具仅供学习使用，下载后请24小时内删除，遵循MIT开源协议**

> 枫谷作伴，潇潇洒洒...