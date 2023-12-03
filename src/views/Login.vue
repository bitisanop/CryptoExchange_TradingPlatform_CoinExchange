<template>
  <div class="page-web page_regi">
    <RegiHead :MyLocal="location" @newLocal="location = $event" />
    <div class="regi-bd">
      <div class="regi_name">{{ $t('account') }}</div>
      <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
        <el-tab-pane :label="$t('phone')" name="first">
          <el-form ref="form" :rules="rules" class="regi-from sign_form" :model="form">
            <el-form-item prop="phone">
              <div class="regi_group">
                <div class="regi_gr_t">{{ $t('phone') }}</div>
                <div class="regi_gr_b flex flex-ac">
                  <div class="regi_phone flex flex-ac flex-hc">
                    <img v-if="countryImageUrl" :src="countryImageUrl" alt="" />
                    <div v-if="countryImageUrl" class="regi_unit">+</div>
                    <el-select v-model="form.country" placeholder="" popper-class="select-drop">
                      <el-option
                        v-for="(item, index) in country"
                        :key="index"
                        :label="item.areaCode"
                        :value="item.zhName"
                      >
                        <div class="select-item" @click="countryItem(item.countryImageUrl)">
                          <img :src="item.countryImageUrl" alt="" />
                          <div class="select-item__bd flex_bd">
                            <span>{{ location == 'zh_CN' ? item.zhName : item.enName }}</span>
                            <span>+{{ item.areaCode }}</span>
                          </div>
                        </div>
                      </el-option>
                    </el-select>
                  </div>
                  <el-input v-model="form.phone" type="number" placeholder=""></el-input>
                </div>
              </div>
            </el-form-item>
            <el-form-item prop="pass">
              <div class="regi_group">
                <div class="regi_gr_t">{{ $t('password') }}</div>
                <div class="regi_gr_b">
                  <el-input v-model="form.pass" placeholder="" :type="pass ? 'password' : 'text'"> </el-input>
                  <div class="regi_eye" @click="eye">
                    <i class="iconfont icon-eye-close" v-if="Eye"></i>
                    <i class="iconfont icon-eye" v-else></i>
                  </div>
                </div>
              </div>
            </el-form-item>
            <el-button class="btn" :plain="true" @click="submitForm('form')">{{ $t('sign') }}</el-button>
          </el-form>
        </el-tab-pane>
        <el-tab-pane :label="$t('email')" name="second">
          <el-form ref="form2" :rules="rules2" class="regi-from sign_form" :model="form2">
            <el-form-item prop="email">
              <div class="regi_group">
                <div class="regi_gr_t">{{ $t('email') }}</div>
                <div class="regi_gr_b">
                  <el-input v-model="form2.email" placeholder=""></el-input>
                </div>
              </div>
            </el-form-item>
            <el-form-item prop="pass">
              <div class="regi_group">
                <div class="regi_gr_t">{{ $t('password2') }}</div>
                <div class="regi_gr_b">
                  <el-input v-model="form2.pass" placeholder="" :type="pass2 ? 'password' : 'text'"> </el-input>
                  <div class="regi_eye" @click="eye2">
                    <i class="iconfont icon-eye-close" v-if="Eye2"></i>
                    <i class="iconfont icon-eye" v-else></i>
                  </div>
                </div>
              </div>
            </el-form-item>
            <el-button class="btn" :plain="true" @click="submitForm2('form2')">{{ $t('sign') }}</el-button>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      <div class="login_tips">
        <router-link to="/forget">{{ $t('password3') }}？</router-link>
        <router-link to="/register">{{ $t('account2') }}</router-link>
      </div>
      <div class="regi_tips">Copyright ©2023 BITISAN All rights reserved.</div>
    </div>
  </div>
</template>
<script>
import { getCountry, login } from '@/api/api/user'
import RegiHead from '@/components/RegiHead.vue'
export default {
  components: {
    RegiHead,
  },
  data() {
    return {
      activeName: 'second',
      form: {
        country: '美国',
        phone: '',
        pass: '',
      },
      rules: {
        phone: [
          {
            required: true,
            message: this.$t('logErr'),
            trigger: 'blur',
          },
        ],
        pass: [
          {
            required: true,
            message: this.$t('logErr2'),
            trigger: 'blur',
          },
          {
            type: 'string',
            min: 6,
            message: this.$t('logErr3'),
            trigger: 'blur',
          },
        ],
      },
      form2: {
        email: '',
        pass: '',
      },
      rules2: {
        email: [
          {
            required: true,
            message: this.$t('logErr'),
            trigger: 'blur',
          },
        ],
        pass: [
          {
            required: true,
            message: this.$t('logErr2'),
            trigger: 'blur',
          },
          {
            type: 'string',
            min: 6,
            message: this.$t('logErr3'),
            trigger: 'blur',
          },
        ],
      },
      Eye: true,
      Eye2: true,
      pass: true,
      pass2: true,
      location: 'en_US',
      country: [],
      countryImageUrl: '',
    }
  },
  created() {
    this.location = localStorage.getItem('lang')
  },
  mounted() {
    this.countryList()
  },
  methods: {
    countryList() {
      getCountry().then(res => {
        if (res.code == 0) {
          this.country = res.data
          this.countryImageUrl = this.country[0].countryImageUrl
        }
      })
    },
    countryItem(url) {
      this.countryImageUrl = url
    },
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          login({
            code: '',
            country: this.form.country,
            password: this.form.pass,
            username: this.form.phone,
          }).then(res => {
            if (res.code == 0) {
              this.$message({
                message: this.$t('logSuccess'),
                type: 'success',
              })
              localStorage.setItem('token', res.data.token)
              this.$store.dispatch('setUser', res.data)
              this.$store.commit('SET_ISLOGIN', true)
              this.$store.commit('SET_MEMBER', res.data)
              this.$router.push({
                path: '/exchange/btc_usdt',
              })
            } else {
              this.$message.error(res.message)
            }
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    submitForm2(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          login({
            code: '',
            country: '美国',
            password: this.form2.pass,
            username: this.form2.email,
          }).then(res => {
            if (res.code == 0) {
              this.$message({
                message: this.$t('logSuccess'),
                type: 'success',
              })
              localStorage.setItem('token', res.data.token)
              this.$store.dispatch('setUser', res.data)
              this.$store.commit('SET_MEMBER', res.data)
              this.$store.commit('SET_ISLOGIN', true)
              this.$router.push({
                path: '/exchange/btc_usdt',
              })
            } else {
              this.$message.error(res.message)
            }
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    handleClick(tab, event) {
      console.log(tab, event)
    },
    eye() {
      this.Eye = !this.Eye
      this.pass = !this.pass
    },
    eye2() {
      this.Eye2 = !this.Eye2
      this.pass2 = !this.pass2
    },
  },
}
</script>
