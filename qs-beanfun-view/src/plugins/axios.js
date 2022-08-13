import Axios from "axios"
import NProgress from 'nprogress'
Axios.defaults.timeout = 6 * 1000
//设置cross跨域 并设置访问权限 允许跨域携带cookie信息
Axios.defaults.withCredentials = true

// request拦截器
Axios.interceptors.request.use(config => {
  NProgress.start()
  config.headers['Accept'] = "application/json"
  config.headers['Content-Type'] = "application/json"
  config.headers['Cache-Control'] = "no-cache"
  return config
}, error => {
  Promise.reject(error)
})

// respone拦截器
Axios.interceptors.response.use(
  response => {
    NProgress.done()
    return response.data
  },
  error => {
    NProgress.done()
    return Promise.reject(error)
  }
)

Axios.defaults.timeout = 1000 * 60 * 10

export default async function (method = 'GET', data = {}, url = '', headers = {}) {
  let params = method.toLowerCase() === 'get' ? 'params' : 'data'
  if (headers["Content-Type"] != "multipart/form-data") {
    data = method.toLowerCase() === 'post' ? JSON.stringify(data) : data
  }
  if (headers['Content-Type'] === undefined) headers['Content-Type'] = "application/json"
  if (headers['Cache-Control'] === undefined) headers['Cache-Control'] = "no-cache";
  url = "http://localhost:54321" + url
  return Axios({ url, method, [params]: data, headers })
}