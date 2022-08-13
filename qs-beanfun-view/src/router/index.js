import Vue from 'vue'
import VueRouter from 'vue-router'
import apiConfig from "@/api/ApiConfig"


Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    component: () => import('@/views/login/index.vue'),
    meta: {
      title: "QsBeanfun - 登入页"
    }
  },
  {
    path: '/index',
    component: () => import('@/views/index/index.vue'),
    meta: {
      title: "QsBeanfun - 已登入"
    }
  }
]


const router = new VueRouter({
  mode: 'hash',
  base: process.env.BASE_URL,
  routes
})

router.beforeEach((to, from, next) => {
  // 在routes中的每个路由配置 meta: { title: '标题' }
  /* 路由发生变化修改页面title */
  if (to.meta.title) {
    document.title = to.meta.title
  }

  next()
})

// 路由跳转完成后进入该钩子函数
router.afterEach((to, from) => {
  if (to.path === '/') {
    apiConfig.getConfig('loginStatus', status => {
      if (status) {
        router.push('/index')
      }
    })
  } else {
    apiConfig.getConfig('loginStatus', status => {
      if (!status) {
        router.push('/')
      }
    })
  }
})

export default router
