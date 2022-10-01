# 编译相关

## 1. 下载 Jre 环境

下载`JRE1.8 32位`环境，注意必须是32位，保存到`deploy/jre`目录中。

[**JRE1.8 32位 Offline Installer**](https://www.oracle.com/java/technologies/downloads/#jre8-windows)

## 2. Maven 编译 package

此为基础中的基础，不会的请合理运用搜索引擎查阅资料。

在Maven配置中已自动设置打包为exe。

## 3. Inno Setup打包安装程序

安裝`inno Setup`工具，运行`install-script.iss`，点击`Run`即可打包出安装包。

脚本内请自行查阅修改部分路径值。

安装包打包好后，可使用`Resource Hacker`修改`manifest`为安装包赋予管理员权限。