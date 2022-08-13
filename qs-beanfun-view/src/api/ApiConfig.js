import Axios from '@/plugins/axios'
import Vm from '@/main'

const baseGetConfigs = (data) => Axios('GET', data, '/api/config/get_configs')

const reset = (data) => Axios('GET', data, '/api/config/reset')

const baseSetConfigs = (config) => Axios('POST', { ...config }, '/api/config/set_configs')

const baseGetAccounts = (data) => Axios('GET', data, '/api/config/get_accounts')

const getAccounts = (fnc) => baseGetAccounts().then(res => fnc(res.code === 1 ? res.data : []))

const getConfig = (key, fnc) => baseGetConfigs().then(res => fnc(res.code === 1 && res.data[key]))

// ============================SET

const setConfig = (key, val) => {
  baseGetConfigs().then(res => {
    if (res.code === 1) {
      res.data[key] = val
      baseSetConfigs({ config: res.data }).then(res2 => Vm.$message({ message: res2.msg, type: res2.code === 1 ? 'success' : 'error' }))
    } else {
      Vm.$message.error(res.msg)
    }
  })
}

export default {
  baseGetConfigs,
  reset,
  getAccounts,
  getConfig,
  setConfig,
}