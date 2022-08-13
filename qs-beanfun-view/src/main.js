import Vue from 'vue'
import ElementUI from 'element-ui'
import App from './App.vue'
import router from './router'
import { dateFormat } from '@/utils/DateUtils'
import VueClipboard from 'vue-clipboard2'
import 'element-ui/lib/theme-chalk/index.css'
import 'nprogress/nprogress.css'
import "@/commons/commons.css"
Vue.use(ElementUI)
Vue.use(VueClipboard)

Vue.config.productionTip = false

// 全局过滤器
Vue.filter('dateFormat', function (time, fmt = 'yyyy/MM/dd hh:mm:ss') {
  if (!time) {
    return ''
  }
  let date = new Date(time)
  return dateFormat(date, fmt)
})


export default new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
