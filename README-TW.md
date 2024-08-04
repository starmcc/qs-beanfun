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
        <img alt="童年小夢" src="https://img.shields.io/badge/HomePage-%E7%AB%A5%E5%B9%B4%E5%B0%8F%E6%A2%A6-orange"/>
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
    <a href="./README.md">简体中文</a>
    <span style="font-weight:bold;color:#F57C00;">繁體中字</span>
</p>

## 介紹

<image style="width: 120px; height: 120px;" src="./src/main/resources/static/images/ico.png"></image>

Please forgive me for not writing this document in English.

I don't have a lot of energy to do it.

秋水登錄器並不是遊戲橘子數位科技開發的官方登錄器

引用[LR區域模擬元件](https://github.com/InWILL/Locale_Remulator)，支持`32/64bit`臺服新楓之谷遊戲運行。

<p style="font-size: 20px;color: palevioletred">於2024年7月我做了個糊塗又明智的決定</p>
<p style="font-size: 20px;color: palevioletred">QsBeanfun5.x系列將采用Python語言重新開發！</p>
<p style="color: palevioletred">至於什麽時候完成，慢慢等吧，或許很快！</p>
<p style="font-size: 18px;color: #00a1ff">QsBeanfun 4.x.x 系列為永存維護版本</p>

> 支持登錄方式

1. 香港賬號 - (賬密/雙重驗證)
2. 臺灣賬號 - (賬密/二維碼/QR碼)
3. 內置各種實用小功能

**遵循MIT開源協議**，如遇問題或 Bug 亦或交流，請移步 Issues。

## 安裝

[**Releases-點擊進入下載頁面**](https://github.com/starmcc/qs-beanfun/releases)

**下載最新`QsBeanfun.zip`開箱即用。**

---

如果您是Java開發人員，擁有 **JRE1.8 x64** 支持。

可下載`QsBeanfun.exe`後創建同級目錄`JRE`，並將運行環境復製該目錄中即可運行。

[JDK1.8 64位 Offline Installer](https://www.oracle.com/java/technologies/downloads/#java8-windows)

---

秋水登錄器運行後會在當前運行目錄創建`.\qs-data`目錄。

以釋放依賴文件和日誌文件使用。

> **特別註意：**

`QsBeanfun.exe`目錄不能存在中文，否則無法啟動遊戲

## 更新

> 方法一

僅需下載`QaBeanfun.exe`覆蓋原安裝目錄。

> 方法二

使用秋水登錄器內置自動更新功能。

## 實現功能

| 功能                                              |
|-------------------------------------------------|
| 香港/臺灣橘子 <br/>普通登錄、雙重登錄、二維碼登錄<br/>Ps: 無需安裝遊戲橘子插件 |
| 模擬繁體操作系統環境運行[新楓之谷]                              |
| 內置會員充值、客服中心、會員中心                                |
| 免輸賬密啟動/進入遊戲                                     |
| 自動屏蔽遊戲啟動窗口（可選）                                  |
| 自動阻止遊戲自動更新（可選）                                  | 
| 一鍵跳過NGS進程                                       | 
| 新楓之谷實用網站快捷導航                                    |
| 遊戲自動錄像功能(需FFMPEG支持)                             |
| 其他內置功能                                          |

## 環境與依賴

> 環境
 
- `Windows7`以上，以`Win10`環境為發布標準。
- [JRE1.8 x64-64位(Zip已包含)](https://www.oracle.com/java/technologies/downloads/#jre8-windows)
- [Microsoft Visual C++ Redistributable VC環境](https://aka.ms/vs/17/release/vc_redist.x64.exe)

> 依賴

- JNA - Java 調用DLL支持
- FastJson - 阿裏JSON解析支持
- JavaFX - JavaFX-UI庫
- Httpclient - apache HTTP請求支持
- [LR區域模擬元件支持](https://github.com/InWILL/Locale_Remulator) 模擬繁體區域啟動遊戲
- JxBrowser
  第三方瀏覽器插件 [來源](https://blog.csdn.net/weixin_43852094/article/details/121157752) [官網](https://jxbrowser-support.teamdev.com/)

## 開發與編譯

> 開發

1. 導入項目後，需要手動將`lib`目錄下的第三方依賴打入本地倉庫，否則報紅。

```
mvn install:install-file -Dfile=./lib/jxbrowser-6.21.jar -DgroupId=com.teamdev.jxbrowser -DartifactId=jxbrowser -Dversion=6.21 -Dpackaging=jar
mvn install:install-file -Dfile=./lib/jxbrowser-win64-6.21.jar -DgroupId=com.teamdev.jxbrowser -DartifactId=jxbrowser-win64 -Dversion=6.21 -Dpackaging=jar
mvn install:install-file -Dfile=./lib/license-6.21.jar -DgroupId=com.teamdev.jxbrowser -DartifactId=license -Dversion=6.21 -Dpackaging=jar
```

請直接運行上述命令即可。

> 編譯 -> [編譯詳細教程](./build/README.md)

## 安全

每次發布`Release`都會貼出工具的(`Hash`)哈希值

請各位下載工具後校驗`Hash`值是否安全

怎麼查詢哈希值？

```
certutil -hashfile 該程序路徑
```

回車後會出現hash值。

> 關於自行編譯後與官方發布的Hash值不一致問題

您會發現每次編譯的jar包`hash`值並不相同，請以發布的`jar`包`hash`值為準。

如果不放心可自行查閱`jar`包中的源碼，`jar`包可用`RAR`軟件打開。

`com/starmcc`目錄下即是登錄器源碼。

> 怎麼反編譯`class`文件？ [class文件的反編譯過程](https://blog.csdn.net/qq_39674002/article/details/109735298)

## 結語

1. 所有惡意的指責，都需歷經時間的檢驗與雕琢。
2. 能幫助他人、分享自身的技術實現方案是極為愉悅之事，也期望能有一些朋友共同對其進行優化，哪怕這只是我的一己之願~~
3. 任何第三方工具皆被遊戲橘子官方嚴令禁止使用，最佳方式是將系統轉為繁體語言後使用網頁登錄，望客官知悉。
4. 我僅是茫茫人海中一位熱愛新楓之谷且略懂些許技術的玩家，衷心希望新楓之谷能夠一直運營，長盛不衰！

**<p style="font-size:18px">本軟件僅供學習使用，下載後請24小時內刪除</p>**
**<p style="font-size:22px">遵循MIT開源協議</p>**

最後奉勸那些指鹿為馬的家夥，請心存善念，人生才會充滿陽光。

> 楓谷作伴，瀟瀟灑灑...

# 贊賞

倘若您也認為本項目對您有益，煩請您慷慨地為作者給予一筆贊賞。

在此收到的每一筆贊賞，都將被作者深深銘記！

<image style="width: 200px; height: 200px;" src="./Appreciate.png"></image>

> 打賞大佬名單,請參照[簡中文档說明](./README.md)