import Axios from '@/plugins/axios'


// 基础配置接口
const openWIndow = (data) => Axios('GET', data, '/api/base/open_window')

// 获取汇率
const getRates = (data) => Axios('GET', data, '/api/base/get_rates')

// 设置游戏路径
const setGamePath = (data) => Axios('GET', data, '/api/base/set_game_path')

// 开始游戏
const startGame = (data) => Axios('POST', data, '/api/base/start_game')

// 自动轮烧
const autoLunshao = (data) => Axios('POST', data, '/api/base/auto_lunshao')

// 自动输入
const autoGameInputActPwd = (data) => Axios('POST', data, '/api/base/auto_game_input_act_pwd')

// 打开装备窗
const openEquipmentWindow = (data) => Axios('GET', data, '/api/base/open_equipment_window')

// 检查版本
const checkVersion = (data) => Axios('GET', data, '/api/base/check_version')

// 更新程序
const updateProgram = (data) => Axios('POST', data, '/api/base/update_program')

export default {
  openWIndow,
  getRates,
  setGamePath,
  startGame,
  autoLunshao,
  autoGameInputActPwd,
  openEquipmentWindow,
  checkVersion,
  updateProgram,
}