# QsBeanfun

<p align="center">
    <a href="https://github.com/starmcc/qs-beanfun/blob/master/LICENSE">
        <img alt="LICENSE" src="https://img.shields.io/badge/License-MIT-lightgrey"/>
    </a>
    <a href="https://github.com/starmcc/qs-beanfun">
        <img alt="stars" src="https://img.shields.io/github/stars/starmcc/qs-beanfun?label=Stars"/>
    </a>
    <a href="https://github.com/starmcc/qs-beanfun/releases/latest">
        <img alt="Releases" src="https://img.shields.io/github/v/release/starmcc/qs-beanfun?display_name=tag&label=Latest&color=red"/>
    </a>
    <a href="https://www.qstms.com/">
        <img alt="QSTMS" src="https://img.shields.io/badge/HomePage-QSTMS-orange"/>
    </a>
</p>
<p align="center">
    <img alt="Downloads" src="https://img.shields.io/github/downloads/starmcc/qs-beanfun/total?label=Downloads"/>
    <img alt="GitHub last commit" src="https://img.shields.io/github/last-commit/starmcc/qs-beanfun?label=LastCommit">
    <img alt="JRE" src="https://img.shields.io/badge/JVM-1.8(64bit)-8d38dc"/>
</p>
<p align="center">
    <a href="./MEADME.md">简体中文</a>
    <span style="font-weight:bold;">繁體中文</span>
</p>

## 介紹

![logo](./src/main/resources/static/images/ico.png)

Please forgive me for not writing this document in English.

I don't have a lot of energy to do it.

秋水登錄器並不是遊戲橘子數位科技開發的官方登錄器

引用[LR區域模擬元件](https://github.com/InWILL/Locale_Remulator)，支持`32/64bit`臺服新楓之谷遊戲運行。

> 支持登錄方式

1. 香港賬號
2. 臺灣賬號
3. 臺灣賬號(二維碼/QR碼)

內置各種實用小功能

香港登錄渠道已替換為新版（不用需要安裝橘子插件）

**遵循MIT開源協議**，如遇問題或 Bug 歡迎提交 Issues。

## 安裝

[**Releases-點擊進入下載頁面**](https://github.com/starmcc/qs-beanfun/releases)

**請下載最新`QsBeanfun-install.exe`進行安裝即可。**

---

如果您是Java開發人員，擁有 **JRE1.8 x64** 支持。

可下載`QsBeanfun.exe`後創建同級目錄`JRE`，並將運行環境復製該目錄中即可運行。

[JDK1.8 64位 Offline Installer](https://www.oracle.com/java/technologies/downloads/#java8-windows)

---

秋水登錄器運行後會在當前運行目錄創建`.\qs-data`目錄。

以釋放依賴文件和日誌文件使用。

**特別註意：秋水登錄器安裝路徑不能存在中文，不然無法啟動遊戲**

## 更新

> 方法一

僅需下載`QaBeanfun.exe`覆蓋原安裝目錄。

> 方法二

使用秋水登錄器內置自動更新功能。

> 註意

`4.1.0`之前的版本更新需要卸載後重新安裝`install`

PS: 需更換JRE環境64位

## 實現

| 功能                  |      版本      |            描述            |
|---------------------|:------------:|:------------------------:|
| 香港/臺灣橘子 賬號登錄/二維碼登錄  | 臺號登錄需4.0.0以上 |  通過模擬網頁登錄，從此告別在網頁上啟動遊戲   |
| 模擬繁體環境啟動新楓之谷遊戲      |     ALL      |     告別入谷需要設置區域語言的麻煩      |
| 內置會員充值、客服中心、會員中心    |     ALL      |     方便充值、反饋問題、查閱賬號資料     |
| 裝備卷軸計算器             |     ALL      |         方便計算裝備價值         |
| 免輸入賬號密碼進遊戲模式        |     ALL      |       實現方式與網頁登錄相同        |
| 自動關閉遊戲Play開始窗口      |     ALL      |         加快進入遊戲速度         |
| 中臺實時匯率計算            |     ALL      |         一個實用的小功能         |
| 新楓之谷實用網站快捷導航        |     ALL      | 一些常用的網站整理，媽媽再也不用擔心找不到官網啦 |
| 免Beanfun插件安裝        |   3.0.1以上    |    登錄器在手,登錄->啟動一氣呵成~     |
| 遊戲自動錄像功能(需FFMPEG支持) |   4.0.0以上    |    避免交易/搶圖爭議、檢舉外掛/輔助     |
| JRE(64位)            |   4.1.0以上    |   4.1.0之前使用32位JRE運行環境    |
| 自動阻止遊戲自動更新          |   4.0.0以上    |    防止自動更新帶來的災難，比如文件損壞    |
| 其他內置功能              |   4.0.0以上    |           便捷功能           |

## 環境

- `Windows7`以上，以`Win10`環境為發布標準。
- [JRE1.8 x64-64位 (Install包已內置)](https://www.oracle.com/java/technologies/downloads/#jre8-windows)
- [Microsoft Visual C++ Redistributable VC環境](https://aka.ms/vs/17/release/vc_redist.x64.exe)

## 依賴

- JNA - Java 調用DLL支持
- FastJson - 阿裏JSON解析支持
- JavaFX - JavaFX-UI庫
- Httpclient - apache HTTP請求支持
- [LR區域模擬元件支持](https://github.com/InWILL/Locale_Remulator) 模擬繁體區域啟動遊戲
- JxBrowser 第三方瀏覽器插件 [來源](https://blog.csdn.net/weixin_43852094/article/details/121157752) [官網](https://jxbrowser-support.teamdev.com/)

## 開發/編譯

1. 導入項目後，需要手動將`lib`目錄下的第三方依賴打入本地倉庫，否則報紅。
```
mvn install:install-file -Dfile=./lib/jxbrowser-6.21.jar -DgroupId=com.teamdev.jxbrowser -DartifactId=jxbrowser -Dversion=6.21 -Dpackaging=jar
mvn install:install-file -Dfile=./lib/jxbrowser-win64-6.21.jar -DgroupId=com.teamdev.jxbrowser -DartifactId=jxbrowser-win64 -Dversion=6.21 -Dpackaging=jar
mvn install:install-file -Dfile=./lib/license-6.21.jar -DgroupId=com.teamdev.jxbrowser -DartifactId=license -Dversion=6.21 -Dpackaging=jar
```
請直接運行上述命令即可。

[編譯詳細教程](./build/README.md)

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

怎麼反編譯`class`文件？

[class文件的反編譯過程](https://blog.csdn.net/qq_39674002/article/details/109735298)

## 結語

1. 所有不懷好意的指責，都需要時間去驗證和打磨。
2. 能夠幫助別人，分享自己的技術實現方案是一件非常愉快的事情，也希望有一些會本系語言的朋友一起優化它，讓它更人性化，即使只是我的一廂情願~~
3. 凡是第三方工具都是橘子官方明令禁止使用的，最好的方式就是將系統轉為繁體語言後使用網頁登錄，望客官悉知。
4. 我只是茫茫人海中一個熱愛楓谷懂點皮毛技術的玩家，希望新楓之谷會一直運營下去，長盛不衰！
5. **工具僅供學習使用，下載後請24小時內刪除，遵循MIT開源協議**

最後奉勸那些指鹿為馬的家夥，請心存善念，人生才會充滿陽光，加油楓谷人。

> 楓谷作伴，瀟瀟灑灑...

# 贊賞

如果您也覺得本項目對您有所幫助，請慷慨的為作者送上一筆贊賞。

在此的每一筆犒勞都將讓作者銘記於心！

![Appreciate](./Appreciate.png)

> 打賞大佬名單，由近到遠依次排列~

名單中是微信名，如果想用遊戲名請在備註上填寫哦~

再次感謝各位大佬的贊賞，天使定會親吻善良的你~

如不想展示可單獨聯系我刪除名字，部分並未展示是實在找不到您的名字

> 名單更新時間：2023-3-12

|           名單           | 金額（RMB） |
|:----------------------:|:-------:|
|        Andr***         |   20    |
|    COSMOS(PS:喝杯奶茶)     |   30    |
|          咳咳溜           |   20    |
|         不再猶豫z          |   66    |
|          索德渃斯          |   50    |
|           九號           |   10    |
|          Mr·銘          |   20    |
|     A酷田照明-專業美縫-小陳      |   10    |
|         tiger          |   100   |
|     阿樑（PS：謝謝作者大大）      |   10    |
|          稻草人           |   50    |
|          JS.           |   30    |
|           Li           |  18.88  |
|          無所謂           |   20    |
|          潘治文           |    5    |
|         J-hard         |   10    |
|          李素雅           |   10    |
|           莫心           |   10    |
|           1            |   10    |
|      我。（PS：感謝感謝）       |   20    |
|         俾面嗌聲林生         |   10    |
|     今天雨下好大（PS：辛苦了）     |   10    |
|       不愛喝阿薩姆的薩滿        |   10    |
|          發條橙           |   10    |
|           晨輝           |   20    |
|        徐小姐的黑臉將         |   10    |
|         COOKIE         |   50    |
|          吹吹風           |   38    |
| like sunshine（PS:中杯奶茶） |   22    |
|          周小明           |   20    |
|       阿裏跨境^O^陳明初       |   110   |
|         Lydia          |   10    |
|          百年孤寂          |   10    |
|          Azu           |   10    |
|           Kk           |    5    |
|          鳴Zai          |   10    |
|         心（符號）          |   20    |
