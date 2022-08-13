import Axios from '@/plugins/axios'


// 基础配置接口
const login = (data) => Axios('POST', data, '/api/user/login')

const getAccountInfo = (data) => Axios('GET', data, '/api/user/get_account_info')

const selectAccount = (data) => Axios('POST', data, '/api/user/select_account')

const addAccount = (data) => Axios('POST', data, '/api/user/add_account')

const editAccount = (data) => Axios('POST', data, '/api/user/edit_account')

const getPoints = (data) => Axios('GET', data, '/api/user/get_points')

const getDynamicPassword = (data) => Axios('POST', data, '/api/user/get_dynamic_password')

const loginOut = (data) => Axios('GET', data, '/api/user/login_out')

export default {
  login,
  getAccountInfo,
  selectAccount,
  addAccount,
  editAccount,
  getPoints,
  getDynamicPassword,
  loginOut,
}