<template>
  <div class="page-web page_regi">
    <RegiHead :MyLocal="location" @newLocal="location = $event" />
    <div class="regi-bd">
      <div class="regi_name">{{ $t('account3') }}</div>
      <el-form ref="form2" :rules="rules2" class="regi-from" :model="form2">
        <el-form-item prop="email">
          <div class="regi_group">
            <div class="regi_gr_t">{{ $t('email2') }}</div>
            <div class="regi_gr_b">
              <el-input v-model="form2.email" placeholder=""></el-input>
            </div>
          </div>
        </el-form-item>
        <el-form-item prop="code2">
          <div class="regi_group">
            <div class="regi_gr_t">{{ $t('code') }}</div>
            <div class="regi_gr_b flex flex-ac">
              <el-input v-model="form2.code2" placeholder=""></el-input>
              <el-button v-if="disabled2" class="btn btn_yzm" :plain="true" @click="sendEmail">
                {{ $t('code2') }}
              </el-button>
              <el-button v-else class="btn btn_yzm" :plain="true" :disabled="!disabled2">
                {{ count2 }}
              </el-button>
            </div>
          </div>
        </el-form-item>
        <el-form-item prop="password2">
          <div class="regi_group">
            <div class="regi_gr_t">{{ $t('password') }}</div>
            <div class="regi_gr_b">
              <el-input v-model="form2.password2" placeholder="" :type="pass2 ? 'password' : 'text'"> </el-input>
              <div class="regi_eye" @click="eye2">
                <i class="iconfont icon-eye-close" v-if="Eye2"></i>
                <i class="iconfont icon-eye" v-else></i>
              </div>
            </div>
          </div>
        </el-form-item>
        <el-form-item prop="checkpassword2">
          <div class="regi_group">
            <div class="regi_gr_t">{{ $t('password4') }}</div>
            <div class="regi_gr_b">
              <el-input v-model="form2.checkpassword2" placeholder="" :type="pass3 ? 'password' : 'text'"> </el-input>
              <div class="regi_eye" @click="eye3">
                <i class="iconfont icon-eye-close" v-if="Eye3"></i>
                <i class="iconfont icon-eye" v-else></i>
              </div>
            </div>
          </div>
        </el-form-item>
        <el-form-item>
          <div class="regi_group">
            <div class="regi_gr_t">{{ $t('invite') }}</div>
            <div class="regi_gr_b">
              <el-input v-model="form2.invite2" placeholder=""></el-input>
            </div>
          </div>
        </el-form-item>
        <el-form-item prop="check">
          <div class="regi_group regi_group_check flex">
            <el-checkbox-group v-model="form2.check">
              <el-checkbox name="type">{{ $t('agree') }} </el-checkbox>
            </el-checkbox-group>
            <router-link :to="'/helplist?cate=5&cateTitle=' + $t('footmeun3_3')" target="_blank">
              《{{ $t('agreement') }}》</router-link
            >
          </div>
        </el-form-item>
        <el-button class="btn" :plain="true" @click="submitForm2('form2')">{{ $t('register') }} </el-button>
      </el-form>
      <div class="regi_tips">Copyright ©2023 BITISAN All rights reserved.</div>
      <el-dialog
        :visible.sync="dialogVisible"
        :show-close="false"
        custom-class="custom-dialog2"
        :close-on-click-modal="false"
      >
        <dragVerify
          ref="captcha"
          :formPhone="form"
          @success="success"
          :formEmail="form2"
          :activeName="activeName"
          @close="onCloseCaptcha"
        >
        </dragVerify>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import dragVerify from '@/components/dragVerify'
import RegiHead from '@/components/RegiHead'
import { getCountry, checkYZMPic, checkUsername, mobileCode, emailCode, regEmail, regPhone } from '@/api/api/user'
export default {
  components: {
    RegiHead,
    dragVerify,
  },
  data() {
    const validateUser = (rule, value, callback) => {
      var reg = /^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$/
      reg = /^(\w)+(\.\w+)*@(\w)+((\.\w{2,3}){1,3})$/
      if (value == '') {
        callback(new Error(this.$t('mailtip')))
      } else if (!reg.test(this.form2.email)) {
        callback(new Error(this.$t('emailerr2')))
      } else {
        callback()
      }
    }
    var validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error(this.$t('logErr2')))
      } else {
        if (this.form.checkpassword !== '') {
          this.$refs.form.validateField('checkpassword')
        }
        callback()
      }
    }
    var validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error(this.$t('confirmpwdtip')))
      } else if (value !== this.form.password) {
        callback(new Error(this.$t('confirmpwderr')))
      } else {
        callback()
      }
    }
    var validatePass3 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error(this.$t('logErr2')))
      } else {
        if (this.form2.checkpassword2 !== '') {
          this.$refs.form2.validateField('checkpassword2')
        }
        callback()
      }
    }
    var validatePass4 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error(this.$t('confirmpwdtip')))
      } else if (value !== this.form2.password2) {
        callback(new Error(this.$t('confirmpwderr')))
      } else {
        callback()
      }
    }
    return {
      show: false,
      activeName: 'second',
      Eye: true,
      Eye1: true,
      Eye2: true,
      Eye3: true,
      pass: true,
      pass1: true,
      pass2: true,
      pass3: true,
      country: [],
      countryImageUrl: '',
      form: {
        country: '美国',
        phone: '',
        code: '',
        password: '',
        checkpassword: '',
        invite: '',
        check: [],
      },
      rules: {
        phone: [
          {
            required: true,
            message: this.$t('logErr'),
            trigger: 'blur',
          },
        ],
        code: [
          {
            required: true,
            message: this.$t('regErr'),
          },
        ],
        password: [
          {
            validator: validatePass,
            trigger: 'blur',
          },
          {
            type: 'string',
            min: 6,
            message: this.$t('logErr3'),
            trigger: 'blur',
          },
        ],
        checkpassword: [
          {
            validator: validatePass2,
            trigger: 'blur',
          },
        ],
        check: [
          {
            type: 'array',
            required: true,
            message: this.$t('agreementtip'),
            trigger: 'change',
          },
        ],
      },
      form2: {
        email: '',
        code2: '',
        password2: '',
        checkpassword2: '',
        invite2: '',
        check: [],
      },
      rules2: {
        email: [
          {
            required: true,
            validator: validateUser,
            trigger: 'blur',
          },
          {
            type: 'email',
            message: this.$t('emailerr2'),
            trigger: ['blur', 'change'],
          },
        ],
        code2: [
          {
            required: true,
            message: this.$t('regErr'),
          },
        ],
        password2: [
          {
            validator: validatePass3,
            trigger: 'blur',
          },
          {
            type: 'string',
            min: 6,
            message: this.$t('logErr3'),
            trigger: 'blur',
          },
        ],
        checkpassword2: [
          {
            validator: validatePass4,
            trigger: 'blur',
          },
        ],
        check: [
          {
            type: 'array',
            required: true,
            message: this.$t('agreementtip'),
            trigger: 'change',
          },
        ],
      },
      location: 'en_US',
      dialogVisible: false,
      waitTime: 60,
      count: '60s',
      disabled: true,
      waitTime2: 60,
      count2: '60s',
      disabled2: true,
      screenWidth: null,
    }
  },
  created() {
    this.location = localStorage.getItem('lang')
    let invite = this.$route.query.code
    if (invite != undefined) {
      this.form.invite = invite
      this.form2.invite2 = invite
    }
  },
  watch: {},
  mounted() {
    this.countryList()
  },
  methods: {
    onCloseCaptcha(data) {
      this.dialogVisible = data
    },
    success(code) {
      if (this.activeName == 'first') {
        mobileCode({
          code: code,
          country: this.form.country,
          phone: this.form.phone,
          type: 'REGISTER',
        }).then(res => {
          if (res.code == 0) {
            this.$message({
              message: res.message,
              type: 'success',
            })
            this.dialogVisible = false
            this.disabled = false
            let clock = window.setInterval(() => {
              this.count = this.waitTime + 's'
              this.waitTime--
              if (this.waitTime <= 0) {
                this.waitTime = 60
                this.disabled = true
                window.clearInterval(clock)
              }
            }, 1000)
          } else {
            this.$message({
              message: res.message,
              type: 'error',
            })
          }
        })
      } else {
        emailCode({
          code: code,
          email: this.form2.email,
          type: 'REGISTER',
        }).then(res => {
          if (res.code == 0) {
            this.$message({
              message: res.message,
              type: 'success',
            })
            this.dialogVisible = false
            this.disabled2 = false
            let clock = window.setInterval(() => {
              this.count2 = this.waitTime2 + 's'
              this.waitTime2--
              if (this.waitTime2 <= 0) {
                this.waitTime2 = 60
                this.disabled2 = true
                window.clearInterval(clock)
              }
            }, 1000)
          } else {
            this.$message({
              message: res.message,
              type: 'error',
            })
          }
        })
      }
    },
    dragstop(object) {
      var code = object.left / (this.parentW - this.width)
      if (this.activeName == 'first') {
        checkYZMPic({
          code: code,
          type: 'REGISTER',
          username: this.form.phone,
        }).then(res => {
          if (res.code == 0) {
            this.$message({
              message: res.message,
              type: 'success',
            })
            this.dialogVisible = false
            this.$refs.dragPlug.left = 0
          }
        })
      }
      if (this.activeName == 'second') {
        checkYZMPic({
          code: code,
          type: 'REGISTER',
          username: this.form2.email,
        }).then(res => {
          if (res.code == 0) {
            this.$message({
              message: res.message,
              type: 'success',
            })
            this.dialogVisible = false
            this.$refs.dragPlug.left = 0
          }
        })
      }
    },
    countryList() {
      getCountry().then(res => {
        if (res.code == 0) {
          console.log(res)
          this.country = res.data
          this.countryImageUrl = this.country[0].countryImageUrl
        }
      })
    },
    countryItem(url) {
      this.countryImageUrl = url
    },
    sendEmail() {
      var reg = /^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$/
      reg = /^(\w)+(\.\w+)*@(\w)+((\.\w{2,3}){1,3})$/
      if (this.form2.email == '') {
        this.$message.error(this.$t('mailtip'))
      } else if (!reg.test(this.form2.email)) {
        this.$message.error(this.$t('emailerr2'))
      } else {
        this.dialogVisible = true
      }
    },
    sendMobile() {
      if (this.form.phone == '') {
        this.$message.error(this.$t('chtip9'))
      } else {
        this.dialogVisible = true
      }
    },
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          checkUsername({
            username: this.form.phone,
          }).then(res => {
            if (res.code == 0) {
              regPhone({
                code: this.form.code,
                country: this.form.country,
                password: this.form.password,
                phone: this.form.phone,
                promotion: this.form.invite,
                randStr: '',
                superPartner: '',
                ticket: '',
                username: this.form.phone,
                validate: '',
              }).then(res => {
                if (res.code == 0) {
                  this.$message({
                    message: res.message,
                    type: 'success',
                  })
                  this.$refs[formName].resetFields()
                  this.$router.push({
                    path: '/login',
                  })
                } else {
                  this.$message({
                    message: res.message,
                    type: 'error',
                  })
                }
              })
            }
          })

          console.log('submit!!')
        } else {
          console.log('error submit!!')
          console.log(this.form)
          return false
        }
      })
    },
    submitForm2(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          checkUsername({
            username: this.form2.email,
          }).then(res => {
            if (res.code == 0) {
              regEmail({
                code: this.form2.code2,
                country: '美国',
                email: this.form2.email,
                password: this.form2.password2,
                promotion: this.form2.invite2,
                superPartner: '',
                username: this.form2.email,
              }).then(res => {
                if (res.code == 0) {
                  this.$message({
                    message: res.message,
                    type: 'success',
                  })
                  this.$refs[formName].resetFields()
                  this.$router.push({
                    path: '/login',
                  })
                } else {
                  this.$message({
                    message: res.message,
                    type: 'error',
                  })
                }
              })
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
    eye1() {
      this.Eye1 = !this.Eye1
      this.pass1 = !this.pass1
    },
    eye2() {
      this.Eye2 = !this.Eye2
      this.pass2 = !this.pass2
    },
    eye3() {
      this.Eye3 = !this.Eye3
      this.pass3 = !this.pass3
    },
  },
}
</script>
