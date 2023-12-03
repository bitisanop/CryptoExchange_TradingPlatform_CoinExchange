<template>
  <div>
    <div class="header">
      <div class="container-fluid">
        <div class="regi_hd flex flex-ac flex-zBetween">
          <div class="logo">
            <router-link to="/">
              <img src="../assets/img/logo.svg" alt="" />
              <b>BITISAN</b>
            </router-link>
          </div>
          <div class="regi-cn flex flex-ac">
            <el-menu class="el-menu-nav" mode="horizontal">
              <el-menu-item index="5" @click="dialogVisible = true">{{ diaList[current].name }} </el-menu-item>
            </el-menu>
            <div class="menu-lang">
              <el-button v-if="theme" @click="themeClick('black')"><span class="iconfont icon-sun"></span> </el-button>
              <el-button v-else @click="themeClick('white')"><span class="iconfont icon-moon"></span> </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="language">
      <el-dialog :visible.sync="dialogVisible" width="840px" top="18vh">
        <el-tabs v-model="activeName" @tab-click="handleClick">
          <el-tab-pane :label="$t('meun14_1')" name="first">
            <div class="dialog_name">{{ $t('meun14_3') }}</div>
            <div class="dialog_list">
              <el-row>
                <el-col :lg="6" :xs="12" v-for="(item, index) in diaList" :key="index">
                  <div class="dia_col" :class="{ active: index == current }" @click="toggle(item, index)">
                    {{ item.name }}
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
export default {
  name: 'RegiHead',
  inject: ['reload'],
  data() {
    return {
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
      current: 0,
    }
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
  },
  methods: {
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
  },
}
</script>
<style>
@media (max-width: 1200px) {
  .container-fluid {
    padding: 0 15px;
  }

  .regi_hd {
    height: 70px;
  }
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
  color: #689aff;
  padding-left: 6px;
  font-size: 20px;
  letter-spacing: 1px;
}
</style>
