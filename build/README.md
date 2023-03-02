# 编译相关

本项目使用`exe4j`进行exe打包

## 1. Maven 编译 package

此为基础中的基础，不会的请合理运用搜索引擎查阅资料。

执行`package`后，`Maven`将自动打包为`QsBeanfun.jar`

## 2. 使用exe4j打包

> exe4j版本为5.1版本

机器安装exe4j后直接运行`exe4j/QsBeanfun.exe4j`，点`Finish`按钮即可生成。

**PS: 如果其他版本无法直接运行请自行根据`QsBeanfun.exe4j`的配置自行配置**

## 3. Inno Setup 打包安装程序

安裝`inno Setup`工具，运行`install\install-script.iss`，点击`Run`即可打包出安装包。

脚本内请查阅官方自行修改值，如果是没变动过目录情况下一般不需要变更，直接执行即可。

安装包打包好后，可使用`Resource Hacker`修改`manifest`为安装包赋予管理员权限。

## launch4j 编译

若要使用 `launch4j` 打包，可将 `pom.xml`中的 `build` 节点的 `launch4j` 节点注释解开

执行`package`后，`Maven`将自动使用`launch4j`打包为`QsBeanfun.exe`。