<template>
  <div id="app">
    <router-view v-if="isRouterAlive" />
  </div>
</template>
<script>
import { checkLogin } from '@/api/api/user'
export default {
  name: 'App',
  provide() {
    return {
      reload: this.reload,
    }
  },
  components: {},
  data() {
    return {
      isRouterAlive: true,
    }
  },
  created() {
    let theme = localStorage.getItem('theme')
    if (theme == 'black') {
      window.document.documentElement.setAttribute('data-theme', theme)
    } else {
      window.document.documentElement.setAttribute('data-theme', 'white')
    }
    this.getLogin()
  },
  methods: {
    reload() {
      this.isRouterAlive = false
      this.$nextTick(function() {
        this.isRouterAlive = true
      })
    },
    getLogin() {
      checkLogin().then(res => {
        if (res.code == 0) {
          this.$store.commit('SET_ISLOGIN', res.data)
          if (res.data === false) {
            this.$store.commit('SET_MEMBER', null)
          }
        }
      })
    },
  },
}
</script>
<style></style>
