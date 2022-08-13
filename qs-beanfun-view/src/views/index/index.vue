<template>
  <el-container>
    <el-header class="qs-header">
      <el-row type="flex" justify="space-between">
        <div>
          <el-image style="margin: 3px;max-width: 180px;" src="logo.png" fit="cover"></el-image>
        </div>
        <div>
          <el-menu mode="horizontal" background-color="#5975ad" text-color="#fff" active-text-color="#fff">
            <el-submenu index="1">
              <template slot="title">资料导航</template>
              <el-menu-item @click="windowOpen(superUrls.mapleStory)">台服新枫之谷官网</el-menu-item>
              <el-menu-item @click="windowOpen(superUrls.hkBeanfun)">新香港橘子官网</el-menu-item>
              <el-menu-item @click="windowOpen(superUrls.twBeanfun)">台湾橘子官网</el-menu-item>
              <el-menu-item @click="windowOpen(superUrls.qstms)">秋水文献库</el-menu-item>
              <el-menu-item @click="windowOpen(superUrls.techbang)">枫之谷透视镜</el-menu-item>
              <el-menu-item @click="windowOpen(superUrls.gamer)">巴哈姆特-新枫之谷</el-menu-item>
              <el-menu-item @click="windowOpen(superUrls.deal8591)">8591台湾宝物交易网</el-menu-item>
              <el-menu-item @click="windowOpen(superUrls.mapleStoryTieba)">新枫之谷 - 百度贴吧</el-menu-item>
              <el-menu-item @click="windowOpen(superUrls.beanfunTieba)">Beanfun - 百度贴吧</el-menu-item>
              <el-menu-item @click="windowOpen(superUrls.neoska)">最新Link表出处 - neoska</el-menu-item>
            </el-submenu>
            <el-submenu index="2">
              <template slot="title">工具导航</template>
              <el-menu-item @click="windowOpen(superUrls.kkDownload)">kk游戏加速下载地址</el-menu-item>
              <el-menu-item @click="windowOpen(superUrls.qsBeanfunGh)">QsBeanfun - GitHub</el-menu-item>
              <el-menu-item @click="windowOpen(superUrls.qsToolsGh)">QsTools - GitHub</el-menu-item>
              <el-menu-item @click="windowOpen(superUrls.punginBeanfun)">缤放Beanfun登录器 - GitHub</el-menu-item>
              <el-menu-item @click="windowOpen(superUrls.localeRemulator)">LR区域模拟元件 - GitHub</el-menu-item>
              <el-menu-item @click="windowOpen(superUrls.xiaomingJS)">小明加速器</el-menu-item>
              <el-menu-item @click="windowOpen(superUrls.kkJS)">kk加速器</el-menu-item>
              <el-menu-item @click="windowOpen(superUrls.nnJS)">nn加速器</el-menu-item>
            </el-submenu>
            <el-menu-item :disabled="loginOutBtnStatus" @click="loginOut">退出登录</el-menu-item>
          </el-menu>
        </div>
      </el-row>
    </el-header>
    <el-container>
      <el-aside width="300px" style="margin-top: 30px;">
        <el-menu class="el-menu-vertical-demo">
          <el-menu-item @click="startGame()">
            <i class="el-icon-menu"></i>
            <span slot="title">启动游戏(32/64位)</span>
          </el-menu-item>
          <el-menu-item @click="autoLunShao()">
            <i class="el-icon-menu"></i>
            <span slot="title">自动轮烧</span>
          </el-menu-item>
          <el-menu-item @click="gameAutoInputActPwd()">
            <i class="el-icon-setting"></i>
            <span slot="title">自动输入账号密码</span>
          </el-menu-item>
          <el-menu-item @click="open('memberCenter')">
            <i class="el-icon-setting"></i>
            <span slot="title">会员中心</span>
          </el-menu-item>
          <el-menu-item @click="open('memberTopUp')">
            <i class="el-icon-setting"></i>
            <span slot="title">充值中心</span>
          </el-menu-item>
          <el-menu-item @click="windowOpen('https://bfweb.hk.beanfun.com/newfaq/service_newBF.aspx')">
            <i class="el-icon-setting"></i>
            <span slot="title">客服中心</span>
          </el-menu-item>
          <el-menu-item @click="open('equipmentWindow')">
            <i class="el-icon-setting"></i>
            <span slot="title">装备计算器</span>
          </el-menu-item>
          <el-menu-item @click="resetConfig()">
            <i class="el-icon-setting"></i>
            <span slot="title">恢复默认配置</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <el-row type="flex">
          <el-col :span="12">
            <el-descriptions title="配置区" direction="vertical" :column="3" border>
              <el-descriptions-item label="免输入模式">
                <el-switch v-model="baseConfig.passInput" @change="setBaseConfig('passInput', baseConfig.passInput)">
                </el-switch>
              </el-descriptions-item>
              <el-descriptions-item label="免Play窗">
                <el-switch v-model="baseConfig.killStartPalyWindow"
                  @change="setBaseConfig('killStartPalyWindow', baseConfig.killStartPalyWindow)">
                </el-switch>
              </el-descriptions-item>
              <el-descriptions-item label="阻止自动更新">
                <el-switch v-model="baseConfig.killGamePatcher"
                  @change="setBaseConfig('killGamePatcher', baseConfig.killGamePatcher)">
                </el-switch>
              </el-descriptions-item>
              <el-descriptions-item :span="3">
                <template slot="label">
                  <el-button size="small" :disabled="fileGamePathBtnStatus" @click="openFileGamePath">设置游戏路径</el-button>
                </template>
                <el-input type="text" readonly :value="baseConfig.gamePath"></el-input>
              </el-descriptions-item>
              <el-descriptions-item label="自动轮烧介绍">
                <div style="max-width: 400px;">
                  <p>自动轮并不被官方许可，使用过程中造成任何问题或风险自行承担后果，Java虚拟机模拟按键，绕过NGS检测机制。</p>
                </div>
              </el-descriptions-item>
              <el-descriptions-item label="键位设置">
                <label>轮回
                  <el-input style="max-width: 100px;" type="text" readonly @keyup.native="bindKeyLunhui"
                    v-model="baseConfig.lunHuiKey"></el-input>
                </label>
                </br>
                <label>燃烧
                  <el-input style="max-width: 100px;" type="text" readonly @keyup.native="bindKeyRanshao"
                    v-model="baseConfig.ranShaoKey"></el-input>
                </label>
              </el-descriptions-item>
              <el-descriptions-item label="启动状态">
                <span v-if="lunshaoStatus" style="color: green;">运行中</span>
                <span v-if="!lunshaoStatus" style="color: red;">停止</span>
              </el-descriptions-item>
            </el-descriptions>
            </br>
            <el-descriptions title="功能区" direction="vertical" :column="3" border>
              <el-descriptions-item :span="3">
                <template slot="label">
                  <el-button size="small" @click="getRatesData()">汇率计算-更新汇率</el-button>
                </template>
                当前汇率
                <el-tag type="success" size="small">{{ rateModel.rate }}</el-tag>
                <span>更新时间:{{rateModel.time | dateFormat }}</span>
                </br>
                <label>软妹币
                  <el-input style="max-width: 200px;" @focus="rateModel.rmb = null"
                    @blur="!rateModel.rmb ? rateModel.rmb = 0 : rateModel.rmb = rateModel.rmb" @input="rateUpdateRmb"
                    type="text" v-model="rateModel.rmb"></el-input>
                </label>
                &lt;>
                <label>
                  <el-input style="max-width: 200px;" @focus="rateModel.twd = null"
                    @blur="!rateModel.twd ? rateModel.twd = 0 : rateModel.twd = rateModel.twd" @input="rateUpdateTwd"
                    type="text" v-model="rateModel.twd"></el-input>
                  新台币
                </label>
              </el-descriptions-item>
            </el-descriptions>
          </el-col>
          <el-col :span="1">
          </el-col>
          <el-col :span="10">
            <el-descriptions title="信息区" direction="vertical" :column="2" border>
              <template slot="extra">
                <el-button type="primary" v-if="!accounts || accounts.length === 0" @click="openOptAccount(1)">创建子账号
                </el-button>
                <el-button v-if="accounts && accounts.length != 0" @click="openOptAccount(2)">编辑子账号</el-button>
              </template>
              <el-descriptions-item>
                <template slot="label">
                  <el-select v-model="accountNow" @change="childrenAccountListSelect" placeholder="子账号列表">
                    <el-option v-for="item in accounts" :key="item.id" :label="item.name" :value="item"></el-option>
                  </el-select>
                  </el-form-item>
                </template>
                <span>{{ accountNow.name }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="账号状态">
                <el-tag v-if="accountNow.status" type="success" size="small">健康</el-tag>
                <el-tag v-if="!accountNow.status" type="error" size="small">异常</el-tag>
              </el-descriptions-item>
              <el-descriptions-item>
                <template slot="label">
                  <el-button :disabled="updatePointsBtnStatus" size="small" @click="getPoints">更新点数</el-button>
                </template>
                <span>{{ accountPoints }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="创建时间">
                <span>{{ accountNow.createTime | dateFormat }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="游戏账号">
                <el-input v-clipboard:copy="dynamics.account" v-clipboard:success="copySuccess" type="text" readonly
                  v-model="dynamics.account"></el-input>
              </el-descriptions-item>
              <el-descriptions-item label="游戏动态密码(10分钟有效)">
                <template slot="label">
                  <el-button size="small" :disabled="dynamicPasswordBtnStatus" @click="getDynamicPassword">
                    获取游戏动态密码</el-button>
                </template>
                <el-input type="password" v-clipboard:copy="dynamics.password" v-clipboard:success="copySuccess"
                  readonly v-model="dynamics.password">
                </el-input>
              </el-descriptions-item>
            </el-descriptions>
          </el-col>
          <el-col :span="1">
          </el-col>
        </el-row>
      </el-main>
    </el-container>
    <el-footer class="qs-footer">
      <el-link @click="updateVersion" class="footer-text">QsBeanfun - v<span v-text="baseConfig.version"></span></el-link>
    </el-footer>
  </el-container>
</template>
<script>
import indexjs from './index.js'
export default {
  ...indexjs,
}
</script>

<style scoped>
.qs-header {
  background-color: #5975ad;
  color: white;
}
.qs-footer {
  background-color: #5975ad;
  height: 30px !important;
  color: white;
  text-align: right;
  font-size: 15px;
}
.footer-text {
  margin: 5px;
  color: white;
}
.footer-text:hover {
  color: pink;
}
</style>