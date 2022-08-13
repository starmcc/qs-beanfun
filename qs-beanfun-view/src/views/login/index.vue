<template>
  <div class="bg-box">
    <el-form v-loading="loading" ref="loginForm" :model="form" label-width="60px" class="login-box">
      <h3 class="login-title">QsBeanfun</h3>
      <p class="login-description">仅支持港号登录</p>
      <el-form-item label="账号" prop="account" :rules="[
      { required: true, message: '请输入邮箱地址', trigger: 'blur' },
      { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
    ]">
        <el-autocomplete class="inline-input" style="width: 280px;" v-model="form.account"
          :fetch-suggestions="queryAccountSearch" placeholder="输入邮箱账号.." @select="handleSelectAccount">
        </el-autocomplete>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input type="password" :show-password="true" placeholder="输入密码.." style="width: 280px;"
          v-model="form.password" />
      </el-form-item>
      <el-row type="flex" class="margin-bottom-15" justify="space-between">
        <el-link
          @click="windowOpen('https://bfweb.hk.beanfun.com/beanfun_web_ap/signup/preregistration.aspx?service=999999_T0')"
          target="_blank">注册账号</el-link>
        <el-checkbox label="记住账号" v-model="recordActPwd" @change="updateRecordActPwd"></el-checkbox>
        <el-link @click="windowOpen('https://bfweb.hk.beanfun.com/member/forgot_pwd.aspx')" target="_blank">忘记密码
        </el-link>
      </el-row>
      <el-button type="primary" style="width: 100%;" :disabled="loginBtnStatus" v-on:click="submitLogin()">登录
      </el-button>
    </el-form>

  </div>
</template>
<script>
import apiUser from '@/api/ApiUser'
import apiBase from '@/api/ApiBase'
import config from '@/api/ApiConfig'
export default {
  name: 'Login',
  data: () => ({
    loading: false,
    form: {
      account: '',
      password: '',
    },
    acts: [],
    recordActPwd: false,
    loginBtnStatus: false,
    restaurants: [],
  }),
  created() {
    // 请求配置
    let _this = this
    config.getConfig('recordActPwd', (status) => {
      _this.recordActPwd = status
      // 请求账号数据
      config.getAccounts(function (acts) {
        _this.acts = acts
        for (const item of acts) {
          _this.restaurants.push({ value: item.act })
        }

        if (status && acts.length > 0) {
          _this.form.account = acts[0].act
          _this.form.password = acts[0].pwd
        }
      })
    })
  },
  methods: {
    queryAccountSearch(queryString, cb) {
      var restaurants = this.restaurants
      var results = queryString
        ? restaurants.filter(
            (restaurant) =>
              restaurant.value
                .toLowerCase()
                .indexOf(queryString.toLowerCase()) === 0
          )
        : restaurants
      // 调用 callback 返回建议列表的数据
      cb(results)
    },
    handleSelectAccount(val) {
      let actObj = this.acts.find((item) => item.act === val.value)
      if (actObj) {
        this.form.password = actObj.pwd
      }
    },
    submitLogin() {
      this.$refs['loginForm'].validate((valid) => {
        if (valid) {
          this.loginBtnStatus = true
          this.loading = true
          apiUser
            .login(this.form)
            .then((res) => {
              this.loginBtnStatus = false
              this.loading = false
              if (res.code === 1) {
                this.$message.success('登录成功')
                this.$router.push('/index')
                return
              }
              this.$message.error(res.msg)
            })
            .catch((e) => {
              console.info('请求异常', e)
              this.loading = false
            })
        } else {
          return false
        }
      })
    },
    open(code) {
      apiBase.openWIndow({ code }).then((res) => {
        console.info(res)
      })
    },
    updateRecordActPwd(state) {
      config.setConfig('recordActPwd', state)
    },
    windowOpen(url) {
      window.open(url)
    },
  },
}
</script>
<style scoped>
.bg-box {
  background-color: #5975ad;
  height: 100%;
}

.login-box {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border: 1px solid #dcdfe6;
  background-color: #fff;
  width: 360px;
  padding: 35px 35px 15px 35px;
  border-radius: 5px;
  -webkit-border-radius: 5px;
  -moz-border-radius: 5px;
  box-shadow: 0 0 25px #909399;
}

.login-title {
  text-align: center;
  margin: 0 auto 10px auto;
  font-size: 24px;
}

.login-description {
  text-align: center;
}

.margin-bottom-15 {
  margin-bottom: 15px;
}
</style>