import apiBase from '@/api/ApiBase'
import apiUser from '@/api/ApiUser'
import { dateDiffBetween } from '@/utils/DateUtils'
import ApiConfig from '@/api/ApiConfig'

const data = () => ({
  rateModel: {
    rate: 0,
    time: 0,
    rmb: 0,
    twd: 0,
  },
  accounts: [],
  accountNow: {
    name: '',
    status: true,
    createTime: null,
  },
  dynamics: {
    account: '',
    password: '',
  },
  gamePath: '',
  accountPoints: '',
  rateBtnStatus: false,
  dynamicPasswordBtnStatus: false,
  fileGamePathBtnStatus: false,
  updatePointsBtnStatus: false,
  loginOutBtnStatus: false,
  lunshaoStatus: false,
  lunshaoTypeBtn: 0,
  baseConfig: {
    gamePath: null,
    killStartPalyWindow: false,
    killGamePatcher: false,
    loginStatus: null,
    loginType: null,
    lunHuiKey: null,
    ranShaoKey: null,
    recordActPwd: null,
    version: null,
    passInput: false,
    lunShaoRunTime: 0,
  },
  superUrls: {
    mapleStory: 'https://maplestory.beanfun.com/main',
    hkBeanfun: 'https://bfweb.hk.beanfun.com/',
    twBeanfun: 'https://tw.beanfun.com/',
    qstms: 'https://www.qstms.com/',
    qsBeanfunGh: 'https://github.com/starmcc/qs-beanfun',
    qsToolsGh: 'https://github.com/starmcc/QsTools',
    techbang: 'http://gametsg.techbang.com/maplestory/',
    gamer: 'https://forum.gamer.com.tw/A.php?bsn=7650/',
    kkDownload: 'https://www.kk1.cn/codwz/index.html',
    deal8591: 'https://www.8591.com.tw/',
    punginBeanfun: 'https://github.com/pungin/Beanfun',
    localeRemulator: 'https://github.com/InWILL/Locale_Remulator',
    mapleStoryTieba:
      'https://tieba.baidu.com/f?kw=%E6%96%B0%E6%9E%AB%E4%B9%8B%E8%B0%B7',
    beanfunTieba: 'https://tieba.baidu.com/f?kw=beanfun',
    neoska: 'https://www.neoska.me/',
    xiaomingJS: 'https://xiaomingjiang.com/?a=DHERPGJM',
    kkJS: 'https://www.kk1.cn/',
    nnJS: 'https://www.nn.com/',
  },
})

const created = function () {
  this.getBaseConfig()
  this.refreshAccounts()
  this.getPoints()
  this.getRatesData()
}

const methods = {
  updateVersion() {
    apiBase.checkVersion({}).then(res => {
      if (res.code !== 1) {
        this.$message.error('获取失败!')
        return
      }
      if (res.data.state !== '有新版本') {
        this.$alert('当前已是最新版本', '检查版本')
        return
      }
      let text = '最新版本v' + res.data.updateVersion + '是否现在下载?'
      this.$confirm(res.data.updateText, text, {
        confirmButtonText: '是',
        cancelButtonText: '否',
        type: 'warning'
      }).then(() => {
        window.open(res.data.downloadUrl)
      })
    })
  },

  getBaseConfig() {
    ApiConfig.baseGetConfigs({}).then((res) => {
      if (res.code === 1) {
        this.baseConfig = res.data
        this.baseConfig.lunHuiKey = String.fromCharCode(
          this.baseConfig.lunHuiKey
        )
        this.baseConfig.ranShaoKey = String.fromCharCode(
          this.baseConfig.ranShaoKey
        )
        if (this.baseConfig.lunShaoRunTime) {
          this.lunshaoStatus = this.baseConfig.lunShaoRunTime !== 0
          this.lunshaoTypeBtn = this.baseConfig.lunShaoRunTime !== 0 ? 1 : 0
        }
        console.info('this.baseConfig', this.baseConfig)
      } else {
        this.$message.error(res.msg)
      }
    })
  },
  refreshAccounts() {
    apiUser.getAccountInfo().then((res) => {
      if (res.code === 1) {
        this.accounts = res.data
        if (!this.accounts || this.accounts.length < 1) {
          this.$message.error('获取账号信息失败!')
          return
        }
        this.accountNow = this.accounts[0]
        this.dynamics.account = this.accounts[0].id
      }
    })
  },
  getPoints() {
    this.updatePointsBtnStatus = true
    apiUser.getPoints().then((res) => {
      this.updatePointsBtnStatus = false
      if (res.code === 1) {
        this.accountPoints = res.data
      }
    })
  },
  loginOut() {
    this.loginOutBtnStatus = true
    apiUser.loginOut().then((res) => {
      this.loginOutBtnStatus = false
      this.$router.push('/')
    }).catch(() => {
      this.loginOutBtnStatus = false
    })
  },
  copySuccess() {
    this.$message.success('复制成功')
  },
  open(code) {
    apiBase.openWIndow({ code }).then((res) => { })
  },
  getRatesData() {
    this.rateBtnStatus = true
    apiBase.getRates().then((res) => {
      this.rateBtnStatus = false
      if (res.code === 1) {
        this.rateModel.rate = res.data
        this.rateModel.time = res.responseTime
      }
    }).catch(() => {
      this.rateBtnStatus = false
    })
  },
  rateUpdateRmb(val) {
    if (!this.rateModel.rate) {
      return
    }
    this.rateModel.twd = Math.floor(val * this.rateModel.rate * 100) / 100
  },
  rateUpdateTwd(val) {
    if (!this.rateModel.rate) {
      return
    }
    this.rateModel.rmb = Math.floor((val / this.rateModel.rate) * 100) / 100
  },
  openFileGamePath() {
    this.fileGamePathBtnStatus = true
    apiBase.setGamePath().then((res) => {
      this.getBaseConfig()
      this.fileGamePathBtnStatus = false
      if (res.code === 1) {
        this.$message.success(res.msg)
      } else {
        this.$message.warning(res.msg)
      }
    }).catch(() => {
      this.fileGamePathBtnStatus = false
    })
  },
  getDynamicPassword(fnc) {
    this.dynamicPasswordBtnStatus = true
    apiUser.getDynamicPassword().then((res) => {
      this.dynamics.password = res.data
      this.dynamicPasswordBtnStatus = false
      if (res.code === 1) {
        this.$message.success('获取动态密码成功!')
        fnc()
      } else {
        this.$message.error(res.msg)
      }
    }).catch(() => {
      this.dynamicPasswordBtnStatus = false
    })
  },
  startGame() {
    let password = null
    if (this.baseConfig.passInput) {
      if (!this.dynamics.password && this.dynamics.password === '') {

        this.$message.error('请先获取密码再使用免输入模式')
        return

      }
      password = this.dynamics.password
    }

    apiBase.startGame({ password }).then((res) => {
      if (res.code === 1) {
        this.$message.success(res.msg)
      } else {
        this.$message.error(res.msg)
      }
    })
  },
  bindKeyLunhui(key) {
    this.baseConfig.lunHuiKey = key.key.toUpperCase()
    this.setBaseConfig('lunHuiKey', key.keyCode)
  },
  bindKeyRanshao(key) {
    this.baseConfig.ranShaoKey = key.key.toUpperCase()
    this.setBaseConfig('ranShaoKey', key.keyCode)
  },
  autoLunShao() {
    let message = ''
    if (!this.lunshaoStatus) {
      message =
        '是否启动自动轮烧？<br>禁止商业用途<br>点击确定后，将在5秒后启动...<br>再次点击会停止,并显示使用时长!'
      this.$confirm(message, '自动轮烧', {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确定',
        cancelButtonText: '取消',
      }).then(() => {
        apiBase.autoLunshao({ status: true }).then((res) => {
          if (res.code === 1) {
            this.lunshaoStatus = !this.lunshaoStatus
            this.$message.success(res.msg)
            this.getBaseConfig()
          } else {
            this.$message.error(res.msg)
          }
        })
      })
    } else {
      console.info(this.baseConfig.lunShaoRunTime)
      message = '立刻停止自动轮烧?<br>已经运行：'
      message += dateDiffBetween(new Date().getTime() - this.baseConfig.lunShaoRunTime)
      this.$confirm(message, '自动轮烧', {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确定',
        cancelButtonText: '取消',
      }).then(() => {
        apiBase.autoLunshao({ status: false }).then((res) => {
          if (res.code === 1) {
            this.lunshaoStatus = !this.lunshaoStatus
            this.$message.success(res.msg)
          } else {
            this.$message.error(res.msg)
          }
        })
      })
    }
  },
  childrenAccountListSelect(val) {
    // 请求
    apiUser.selectAccount({ accountId: this.val.id }).then((res) => {
      if (res.code === 1) {
        this.$message.success('已选择[' + val.name + ']子账号')
        this.dynamics.account = this.val.id
        this.dynamics.password = ''
      }
    })
  },
  openOptAccount(type) {
    let prompt = this.$prompt(
      '请输入账号',
      type === 1 ? '创建账号' : '编辑账号',
      {
        closeOnClickModal: false,
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValue: this.accountNow ? this.accountNow.name : '',
      }
    )
    prompt.then(({ value }) => {
      let result
      if (type === 1) {
        result = apiUser.addAccount({ name: value })
      } else {
        result = apiUser.editAccount({ name: value })
      }
      result.then((res) => {
        if (res.code !== 1) {
          this.$message.error(res.msg)
          return
        }
        this.$message.success(res.msg)
        this.refreshAccounts()
      })
    })
  },
  gameAutoInputActPwd() {
    if (!this.dynamics.password || this.dynamics.password === '') {
      this.$message.error('请先获取密码')
      return
    }
    apiBase.autoGameInputActPwd({
      account: this.dynamics.account,
      password: this.dynamics.password,
    }).then((res) => {
      console.info(res)
    })
  },
  windowOpen(url) {
    window.open(url)
  },
  resetConfig() {
    this.$confirm('是否恢复默认配置?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(() => {
      ApiConfig.reset({}).then((res) => {
        if (res.code === 1) {
          this.$message.success(res.msg)
          this.$router.go(0)
        } else {
          this.$message.error(res.msg)
        }
      })
    })
  },
  setBaseConfig(key, val) {
    ApiConfig.setConfig(key, val)
  },
}

export default {
  data,
  created,
  methods,
}