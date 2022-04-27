# 打包相关

## 1. 下载JRE环境

下载`JRE1.8 32位`环境，注意，必须是32位，因为引用的dll属于32位程序，只能使用32位`JRE`环境运行，保存到`deploy/jre`目录中。

[**JRE1.8 32位 Offline Installer**](https://www.oracle.com/java/technologies/downloads/#jre8-windows)

## 2. EXE4J打包exe程序

先在项目中使用`Maven`打成`Jar`包。

安装`EXE4J`后运行`QsBeanfun.exe4j`，打包出`exe`文件。



## 3. Resource Hacker赋予程序管理员权限

安装并运行`Resource Hacker` 

拖拽刚才打包出来的`exe`文件（在`target`目录），找到`Manifest` - `1.0`。

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

按`F5`或点击运行按钮，然后`Ctrl + S`或点击保存按钮，

会生成带有小盾牌的`exe`文件，让程序在运行时使用管理员模式运行。



## 4. Inno Setup打包安装程序

安裝`inno Setup`工具，运行`install-script.iss`，点击`Run`即可打包出安装包。

安装包打包好后，同样使用`Resource Hacker`为安装包赋予管理员权限。

