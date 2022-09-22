# 编译相关

## 1. 下载 Jre 环境

下载`JRE1.8 32位`环境，注意必须是32位，保存到`deploy/jre`目录中。

[**JRE1.8 32位 Offline Installer**](https://www.oracle.com/java/technologies/downloads/#jre8-windows)

## 2. Maven 编译 Jar

此为基础中的基础，不会的请合理运用搜索引擎查阅资料。


## 3. exe4j 打包exe程序

先在项目中使用`Maven`打成`Jar`包。

安装`EXE4J`后运行`QsBeanfun.exe4j`，打包出`exe`文件。


## 4. Resource Hacker 赋予程序管理员权限

安装并运行[Resource Hacker](http://www.angusj.com/resourcehacker/)

拖拽刚才打包出来的`exe`文件（在`target`目录）

在`Resource Hacker`中找到`Manifest` - `1.0`。

找到下方配置中的`level="asInvoker"`

```xml
<security>
	<requestedPrivileges>
		<requestedExecutionLevel 
			level="asInvoker"
            uiAccess="false"/>
	</requestedPrivileges>
</security>
```

将值替换为：`requireAdministrator`

```xml
<security>
	<requestedPrivileges>
		<requestedExecutionLevel 
			level="requireAdministrator"
            uiAccess="false"/>
	</requestedPrivileges>
</security>
```

按`F5`或点击运行按钮，`Ctrl + S`或点击保存按钮，

会生成带有小盾牌的`exe`文件，到此完成给登录器赋予管理员权限。


## 5. Inno Setup打包安装程序

安裝`inno Setup`工具，运行`install-script.iss`，点击`Run`即可打包出安装包。

脚本内请自行查阅修改部分路径值。

安装包打包好后，同样使用`Resource Hacker`为安装包赋予管理员权限即可。