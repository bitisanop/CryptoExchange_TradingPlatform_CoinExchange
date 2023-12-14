<template>
  <div>
    <div class="header">
      <div class="container-fluid flex flex-ac">
        <div class="logo shake-slow">
          <router-link to="/">
            <img src="../assets/img/logo.svg" alt="" />
            <b>BITISAN</b>
          </router-link>
        </div>
        <div class="flex flex_bd head-body hidden-xs">
          <el-menu
            :default-active="$route.path"
            @select="handleSelect"
            class="el-menu-nav"
            mode="horizontal"
            :router="true"
          >
            <el-menu-item index="/">{{ $t('meun1') }}</el-menu-item>
            <el-menu-item index="/exchange/btc_usdt">{{ $t('meun3_1') }}</el-menu-item>
            <el-submenu index="1" popper-class="legal-submenu">
              <template slot="title">{{ $t('meun3') }}</template>
              <el-menu-item index="/trade/USDT"
                ><img src="../assets/photo/in1.svg" alt="" />{{ $t('meun2') }}
              </el-menu-item>
              <el-menu-item index="/convert"
                ><img src="../assets/photo/in2.svg" alt="" />{{ $t('meun3_2') }}
              </el-menu-item>
            </el-submenu>
            <el-submenu index="2" popper-class="legal-submenu">
              <template slot="title">{{ $t('meun4') }}</template>
              <el-menu-item index="/swap/btc_usdt"
                ><img src="../assets/photo/in3.svg" alt="" />{{ $t('meun4_1') }}
              </el-menu-item>
              <el-menu-item index="/coinswap/btc_usdt"
                ><img src="../assets/photo/in4.svg" alt="" />{{ $t('meun4_2') }}
              </el-menu-item>
              <el-menu-item index="/option"
                ><img src="../assets/photo/in5.svg" alt="" />{{ $t('meun4_3') }}
              </el-menu-item>
              <el-menu-item index="/second/btc_usdt"
                ><img src="../assets/photo/in6.svg" alt="" />{{ $t('meun4_4') }}
              </el-menu-item>
            </el-submenu>
            <el-submenu index="3" popper-class="legal-submenu">
              <template slot="title">{{ $t('meun5') }}</template>
              <el-menu-item index="/lab"><img src="../assets/photo/in7.svg" alt="" />{{ $t('meun5_1') }} </el-menu-item>
              <el-menu-item index="/invite"
                ><img src="../assets/photo/in8.svg" alt="" />{{ $t('meun5_2') }}
              </el-menu-item>
            </el-submenu>
            <el-menu-item index="/earn">{{ $t('meun6') }}</el-menu-item>
            <el-menu-item index="/announcement/0">{{ $t('meun7') }}</el-menu-item>
          </el-menu>
        </div>
        <div class="head-right flex flex-ac hidden-sm">
          <div v-if="isLogin == true">
            <el-menu class="el-menu-nav" mode="horizontal" :router="true">
              <el-submenu index="1" popper-class="legal-submenu">
                <template slot="title">{{ $t('meun10') }}</template>
                <el-menu-item index="/money"
                  ><img src="../assets/photo/in1.svg" alt="" />{{ $t('meun10_1') }}
                </el-menu-item>
                <el-menu-item index="/swapAssets"
                  ><img src="../assets/photo/in3.svg" alt="" />{{ $t('meun10_2') }}
                </el-menu-item>
                <el-menu-item index="/coinSwapAssets"
                  ><img src="../assets/photo/in4.svg" alt="" />{{ $t('meun10_3') }}
                </el-menu-item>
                <el-menu-item index="/secondAssets"
                  ><img src="../assets/photo/in6.svg" alt="" />{{ $t('meun10_4') }}
                </el-menu-item>
                <el-menu-item index="/financeAssets"
                  ><img src="../assets/photo/in9.svg" alt="" />{{ $t('meun10_5') }}
                </el-menu-item>
                <el-menu-item index="/walletHistory"
                  ><img src="../assets/photo/in10.svg" alt="" />{{ $t('meun10_6') }}
                </el-menu-item>
              </el-submenu>
              <el-submenu index="2" popper-class="legal-submenu">
                <template slot="title">{{ $t('meun11') }}</template>
                <el-menu-item index="/spotOrder"
                  ><img src="../assets/photo/in11.svg" alt="" />{{ $t('meun11_1') }}
                </el-menu-item>
                <el-menu-item index="/ad"
                  ><img src="../assets/photo/in12.svg" alt="" />{{ $t('meun11_2') }}
                </el-menu-item>
                <el-menu-item index="/earnOrder"
                  ><img src="../assets/photo/in13.svg" alt="" />{{ $t('meun11_3') }}
                </el-menu-item>
                <el-menu-item index="/convertHistory"
                  ><img src="../assets/photo/in14.svg" alt="" />{{ $t('meun11_4') }}
                </el-menu-item>
                <el-menu-item index="/innovation/myorders"
                  ><img src="../assets/photo/in15.svg" alt="" />{{ $t('meun11_5') }}
                </el-menu-item>
              </el-submenu>
              <el-submenu index="3" popper-class="legal-submenu">
                <template slot="title">
                  <div class="user-avatar">
                    <img :src="avatar" alt="" />
                  </div>
                </template>
                <el-menu-item index="/admin"
                  ><img src="../assets/photo/in11.svg" alt="" />{{ $t('meun13_1') }}
                </el-menu-item>
                <el-menu-item index="/collection"
                  ><img src="../assets/photo/in12.svg" alt="" />{{ $t('meun13_2') }}
                </el-menu-item>
                <el-menu-item index="/promotion"
                  ><img src="../assets/photo/in13.svg" alt="" />{{ $t('meun13_3') }}
                </el-menu-item>
                <li class="el-menu-item" @click="logOutClick">
                  <img src="../assets/photo/in20.svg" alt="" />{{ $t('meun13_5') }}
                </li>
              </el-submenu>
            </el-menu>
          </div>
          <div v-else class="head-nav">
            <el-menu class="el-menu-nav" mode="horizontal" :router="true">
              <el-menu-item index="/login">{{ $t('meun8') }}</el-menu-item>
              <el-menu-item index="/register">{{ $t('meun9') }}</el-menu-item>
            </el-menu>
          </div>
          <div class="head-download">
            <el-popover placement="bottom" width="120" trigger="hover">
              <el-button slot="reference" class="download-btn">{{ $t('meun12') }}</el-button>
              <div class="ios">
                <img src="../assets/photo/web/appdownload.png" alt="" />
                <div class="tips">
                  <span>{{ $t('scandownload') }}</span>
                </div>
              </div>
            </el-popover>
          </div>
          <el-menu class="el-menu-nav" mode="horizontal" :router="true">
            <el-menu-item @click="langClick">
              <div class="menu-local">
                {{ diaList[current].name }}
              </div>
            </el-menu-item>
          </el-menu>
        </div>
        <div class="menu-nav flex flex-ac hidden-sm">
          <div class="unit-span" @click="currentClick">{{ currentInfo.fullName }}</div>
          <div class="menu-lang">
            <el-button v-if="theme" @click="themeClick('black')"><span class="iconfont icon-sun"></span> </el-button>
            <el-button v-else @click="themeClick('white')"><span class="iconfont icon-moon"></span> </el-button>
          </div>
        </div>
        <div class="menu-icon" @click="drawer = true">
          <span class="iconfont icon-menu"></span>
        </div>
      </div>
    </div>
    <el-drawer :visible.sync="drawer" :direction="direction" size="60%" custom-class="custom-drawer">
      <div class="head-body head-drawer-body">
        <div class="menu-nav flex flex-ac">
          <el-menu
            v-if="isLogin == true"
            class="el-menu-nav el-menu-user"
            mode="horizontal"
            :router="true"
            menu-trigger="click"
          >
            <el-submenu index="1">
              <template slot="title">
                <div class="user-avatar">
                  <img src="../assets/photo/user.png" alt="" />
                </div>
              </template>
              <el-menu-item index="/admin"
                ><img src="../assets/photo/in11.svg" alt="" />{{ $t('meun13_1') }}
              </el-menu-item>
              <el-menu-item index="/collection"
                ><img src="../assets/photo/in12.svg" alt="" />{{ $t('meun13_2') }}
              </el-menu-item>
              <el-menu-item index="/promotion"
                ><img src="../assets/photo/in13.svg" alt="" />{{ $t('meun13_3') }}
              </el-menu-item>
              <li class="el-menu-item" @click="logOutClick">
                <img src="../assets/photo/in20.svg" alt="" />{{ $t('meun13_5') }}
              </li>
            </el-submenu>
          </el-menu>
          <el-menu v-else class="el-menu-nav el-menu-btns" mode="horizontal" :router="true">
            <el-menu-item index="/login">{{ $t('meun8') }}</el-menu-item>
            <el-menu-item index="/register">{{ $t('meun9') }}</el-menu-item>
          </el-menu>
          <div class="unit-span" @click="currentClick">{{ currentInfo.fullName }}</div>
          <div class="menu-lang">
            <el-button v-if="theme" @click="themeClick('black')"><span class="iconfont icon-sun"></span> </el-button>
            <el-button v-else @click="themeClick('white')"><span class="iconfont icon-moon"></span> </el-button>
          </div>
        </div>
        <el-menu :default-active="activeIndex" class="el-menu-nav" :router="true">
          <el-menu-item index="/">{{ $t('meun1') }}</el-menu-item>
          <el-menu-item index="/trade/USDT">{{ $t('meun2') }} </el-menu-item>
          <el-submenu index="1">
            <template slot="title">{{ $t('meun3') }}</template>
            <el-menu-item index="/exchange/btc_usdt"
              ><img src="../assets/photo/in1.svg" alt="" />{{ $t('meun3_1') }}
            </el-menu-item>
            <el-menu-item index="/convert"
              ><img src="../assets/photo/in2.svg" alt="" />{{ $t('meun3_2') }}
            </el-menu-item>
          </el-submenu>
          <el-submenu index="2">
            <template slot="title">{{ $t('meun4') }}</template>
            <el-menu-item index="/swap/btc_usdt"
              ><img src="../assets/photo/in3.svg" alt="" />{{ $t('meun4_1') }}
            </el-menu-item>
            <el-menu-item index="/coinswap/btc_usdt"
              ><img src="../assets/photo/in4.svg" alt="" />{{ $t('meun4_2') }}
            </el-menu-item>
            <el-menu-item index="/option"
              ><img src="../assets/photo/in5.svg" alt="" />{{ $t('meun4_3') }}
            </el-menu-item>
            <el-menu-item index="/second/btc_usdt"
              ><img src="../assets/photo/in6.svg" alt="" />{{ $t('meun4_4') }}
            </el-menu-item>
          </el-submenu>
          <el-submenu index="3">
            <template slot="title">{{ $t('meun5') }}</template>
            <el-menu-item index="/lab"><img src="../assets/photo/in7.svg" alt="" />{{ $t('meun5_1') }} </el-menu-item>
            <el-menu-item index="/invite"
              ><img src="../assets/photo/in8.svg" alt="" />{{ $t('meun5_2') }}
            </el-menu-item>
          </el-submenu>
          <el-menu-item index="/earn">{{ $t('meun6') }}</el-menu-item>
          <el-menu-item index="/orderEntrust-1">{{ $t('meun7') }}</el-menu-item>
        </el-menu>
        <div class="el-line"></div>
        <el-menu class="el-menu-nav" :router="true">
          <el-submenu index="1" v-if="isLogin">
            <template slot="title">{{ $t('meun10') }}</template>
            <el-menu-item index="/money"
              ><img src="../assets/photo/in1.svg" alt="" />{{ $t('meun10_1') }}
            </el-menu-item>
            <el-menu-item index="/swapAssets"
              ><img src="../assets/photo/in17.svg" alt="" />{{ $t('meun10_2') }}
            </el-menu-item>
            <el-menu-item index="/coinSwapAssets"
              ><img src="../assets/photo/in18.svg" alt="" />{{ $t('meun10_3') }}
            </el-menu-item>
            <el-menu-item index="/secondAssets"
              ><img src="../assets/photo/in19.svg" alt="" />{{ $t('meun10_4') }}</el-menu-item
            >
            <el-menu-item index="/financeAssets"
              ><img src="../assets/photo/in9.svg" alt="" />{{ $t('meun10_5') }}
            </el-menu-item>
            <el-menu-item index="/walletHistory"
              ><img src="../assets/photo/in10.svg" alt="" />{{ $t('meun10_6') }}
            </el-menu-item>
          </el-submenu>
          <el-submenu index="2" v-if="isLogin">
            <template slot="title">{{ $t('meun11') }}</template>
            <el-menu-item index="/spotOrder"
              ><img src="../assets/photo/in11.svg" alt="" />{{ $t('meun11_1') }}
            </el-menu-item>
            <el-menu-item index="/ad"><img src="../assets/photo/in12.svg" alt="" />{{ $t('meun11_2') }} </el-menu-item>
            <el-menu-item index="/earnOrder"
              ><img src="../assets/photo/in13.svg" alt="" />{{ $t('meun11_3') }}
            </el-menu-item>
            <el-menu-item index="/convertHistory"
              ><img src="../assets/photo/in14.svg" alt="" />{{ $t('meun11_4') }}
            </el-menu-item>
            <el-menu-item index="/innovation/myorders"
              ><img src="../assets/photo/in15.svg" alt="" />{{ $t('meun11_5') }}
            </el-menu-item>
          </el-submenu>
          <el-menu-item index="">{{ $t('meun12') }}</el-menu-item>
          <el-menu-item @click="langClick">
            {{ diaList[current].name }}
          </el-menu-item>
        </el-menu>
      </div>
    </el-drawer>
    <div class="language">
      <el-dialog :visible.sync="dialogVisible" width="840px" top="18vh" :close-on-click-modal="false">
        <el-tabs v-model="activeName" @tab-click="handleClick">
          <el-tab-pane :label="$t('meun14_1')" name="first">
            <div class="dialog_name">{{ $t('meun14_3') }}</div>
            <div class="dialog_list">
              <el-row>
                <el-col :lg="6" :xs="8" v-for="(item, index) in diaList" :key="index">
                  <div class="dia_col" :class="{ active: index == current }" @click="toggle(item, index)">
                    {{ item.name }}
                  </div>
                </el-col>
              </el-row>
            </div>
          </el-tab-pane>
          <el-tab-pane :label="$t('meun14_2')" name="second">
            <div class="dialog_name">{{ $t('meun14_4') }}</div>
            <div class="dialog_list">
              <el-row>
                <el-col :lg="6" :xs="8" v-for="(item, index) in currentList" :key="index">
                  <div class="dia_col" :class="{ active: index == current2 }" @click="toggleCurrent(item, index)">
                    {{ item.fullName }}-{{ item.symbol }}
                  </div>
                </el-col>
              </el-row>
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { loginOut, currencyFindAll } from '@/api/api/user'
export default {
  name: 'Header',
  inject: ['reload'],
  data() {
    return {
      activeIndex: '1',
      navinput: '',
      drawer: false,
      direction: 'ltr',
      theme: false,
      dialogVisible: false,
      activeName: 'first',
      diaList: [
        {
          name: 'English',
          value: 'en_US',
        },
        {
          name: '简体中文',
          value: 'zh_CN',
        },
        {
          name: '繁體中文',
          value: 'zh_HK',
        },
        {
          name: '日本語',
          value: 'ja_JP',
        },
        {
          name: '한국인',
          value: 'ko_KR',
        },
        {
          name: 'Deutsch',
          value: 'de_DE',
        },
        {
          name: 'Français',
          value: 'fr_FR',
        },
        {
          name: 'Italiano',
          value: 'it_IT',
        },
        {
          name: 'español',
          value: 'es_ES',
        },
      ],
      currentList: [],
      current: 0,
      current2: 0,
      avatar: require('../assets/photo/user.png'),
      symbol: '',
    }
  },
  created() {
    this.symbol = localStorage.getItem('setSymbol')
  },
  computed: {
    isLogin: function() {
      return this.$store.getters.isLogin
    },
    currentInfo: function() {
      return this.$store.getters.current
    },
  },
  mounted() {
    let theme = localStorage.getItem('theme')
    if (theme == 'black') {
      this.theme = false
    } else {
      this.theme = true
    }
    let isZh = localStorage.getItem('lang')
    if (isZh == 'en_US') {
      this.current = 0
    }
    if (isZh == 'zh_CN') {
      this.current = 1
    }
    if (isZh == 'zh_HK') {
      this.current = 2
    }
    if (isZh == 'ja_JP') {
      this.current = 3
    }
    if (isZh == 'ko_KR') {
      this.current = 4
    }
    if (isZh == 'de_DE') {
      this.current = 5
    }
    if (isZh == 'fr_FR') {
      this.current = 6
    }
    if (isZh == 'it_IT') {
      this.current = 7
    }
    if (isZh == 'es_ES') {
      this.current = 8
    }
    this.getCurrent()
  },
  methods: {
    getCurrent() {
      currencyFindAll().then(res => {
        if (res.code == 0) {
          this.currentList = res.data
        }
      })
    },
    handleSelect(key, keyPath) {
      console.log(key, keyPath)
    },
    logOutClick() {
      loginOut().then(res => {
        if (res.code == 0) {
          localStorage.removeItem('token')
          this.$store.commit('SET_ISLOGIN', false)
          this.$store.commit('SET_MEMBER', null)
          this.isLogin = false
          this.$message({
            message: 'Login out successfully',
            type: 'success',
          })
          this.$router.push({
            path: '/',
          })
        }
      })
    },
    themeClick(theme) {
      this.theme = !this.theme
      this.$store.dispatch('setTheme', theme)
      window.document.documentElement.setAttribute('data-theme', theme)
    },
    handleClick(tab, event) {
      console.log(tab, event)
    },
    toggle(item, index) {
      this.current = index
      this.dialogVisible = false
      this.$i18n.locale = item.value
      this.$emit('newLocal', item.value)
      localStorage.setItem('lang', item.value)
      this.reload()
    },
    toggleCurrent(item, index) {
      this.dialogVisible = false
      this.current2 = index
      // this.currentList.splice(index, 1)
      // this.currentList.unshift(item)
      // this.$emit('setCurrent', item)
      this.$store.commit('SET_CURRENT', item)
    },
    langClick() {
      this.activeName = 'first'
      this.dialogVisible = true
    },
    currentClick() {
      this.dialogVisible = true
      this.activeName = 'second'
    },
  },
  watch: {},
}
</script>
<style>
@media (max-width: 1300px) {
  .hidden-sm {
    display: none !important;
  }
}

@media (max-width: 1200px) {
  .hidden-xs {
    display: none !important;
  }

  .el-menu-item,
  .el-submenu__title {
    height: 42px !important;
    line-height: 42px !important;
  }

  .el-drawer__header {
    margin-bottom: 0 !important;
  }

  .menu-nav {
    width: 100% !important;
    padding: 0 20px !important;
  }

  .el-menu-user .el-submenu__title {
    padding: 0 5px;
  }
  .head-body .el-menu--horizontal > .el-submenu .el-submenu__icon-arrow {
    margin-left: 8px;
    margin-top: 0;
  }

  .el-drawer__body {
    overflow-y: auto;
  }

  .el-menu-btns.el-menu-nav > .el-menu-item {
    padding: 0 15px !important;
  }

  .el-menu-btns.el-menu-nav > .el-menu-item:first-child {
    padding-left: 0 !important;
  }
}

@media (max-width: 768px) {
  .el-menu-btns.el-menu-nav > .el-menu-item {
    padding: 0 !important;
    max-width: 50px;
    margin-right: 7px !important;
  }

  .menu-nav .unit-span {
    margin: 0;
    padding: 0 10px;
  }

  .menu-nav {
    padding: 0 0 0 20px !important;
  }
}

.custom-drawer .el-drawer__close-btn {
  padding: 0;
}
.logo {
  margin-right: 60px;
}
.logo a {
  display: flex;
  justify-content: center;
  align-items: center;
}
.logo img {
  width: 22px;
}
.logo b {
  color: #689AFF;
  padding-left: 6px;
  font-size: 20px;
  letter-spacing: 1px;
}
</style>