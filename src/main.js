import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store/index.js'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import VueAwesomeSwiper from 'vue-awesome-swiper'
import 'swiper/swiper-bundle.css'
import './assets/fonts/iconfont.css'
import './assets/css/common.css'
import './assets/css/css.css'
import './assets/css/web.css'
import './assets/css/animate.css'
import moment from 'moment'
import VueI18n from 'vue-i18n'
import animated from 'animate.css'
import zh_CN from './assets/local/zh.js'
import en_US from './assets/local/en.js'
import zh_HK from './assets/local/hk.js'
import ja_JP from './assets/local/ja.js'
import ko_KR from './assets/local/ko.js'
import de_DE from './assets/local/de.js'
import fr_FR from './assets/local/fr.js'
import it_IT from './assets/local/it.js'
import es_ES from './assets/local/es.js'
import * as math from 'mathjs'
import VueClipboard from 'vue-clipboard2'
Vue.filter('datefmt', function(input, fmtstring) {
  if (input) {
    return moment(input).format(fmtstring)
  } else {
    return ''
  }
})

Vue.filter('toFixed', function(number, scale) {
  return new Number(number).toFixed(scale)
})

function toFloor(number, scale = 8) {
  if (new Number(number) == 0) {
    return 0
  }
  var __str = number + ''
  if (__str.indexOf('e') > -1 || __str.indexOf('E') > -1) {
    let __num = new Number(number).toFixed(scale + 1),
      __str = __num + ''
    return __str.substring(0, __str.length - 1)
  } else if (__str.indexOf('.') > -1) {
    if (scale == 0) {
      return __str.substring(0, __str.indexOf('.'))
    }
    return __str.substring(0, __str.indexOf('.') + scale + 1)
  } else {
    return __str
  }
}
Vue.filter('toFloor', (number, scale) => {
  return toFloor(number, scale)
})
Vue.prototype.host = process.env.VUE_APP_API_URL
Vue.prototype.toFloor = toFloor
Vue.prototype.$math = math
Vue.use(VueI18n)
  .use(animated)
  .use(VueClipboard)
  .use(ElementUI)
  .use(VueAwesomeSwiper)
const locale = localStorage.getItem('lang')
const i18n = new VueI18n({
  locale: locale || 'en_US',
  messages: {
    zh_CN: zh_CN,
    en_US: en_US,
    zh_HK: zh_HK,
    ja_JP: ja_JP,
    ko_KR: ko_KR,
    de_DE: de_DE,
    fr_FR: fr_FR,
    it_IT: it_IT,
    es_ES: es_ES,
  },
})
Vue.config.productionTip = false

new Vue({
  i18n,
  store,
  router,
  render: h => h(App),
}).$mount('#app')
