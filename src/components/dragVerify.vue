<template>
  <div>
    <div class="captcha">
      <div class="captcha-title">
        <div class="title-left">{{ $t('witdia') }}</div>
        <div class="title-right">
          <i class="el-icon-refresh-right" @click="onClickRefresh" title="Refresh"></i>
          <i class="el-icon-close" @click="onClickClose" title="Close"></i>
        </div>
      </div>
      <div class="captcha-image" :style="{ width: bgW + 'px', height: bgH + 'px' }">
        <img :src="bgImg" :width="bgW" :height="bgH" @mousedown="onPreventDefault($event)" />
        <img
          class="image-slice"
          :src="sliceImg"
          :width="sliceW"
          :height="sliceH"
          :style="{ left: sliceX + 'px' }"
          @mousedown="onMouseDown($event)"
          @touchstart="onTouchStart($event)"
        />
      </div>

      <div class="captcha-drag">
        <div class="drag-bg" :style="{ width: msgBgW + 'px', backgroundColor: msgBgColor }"></div>
        <div class="drag-text" :style="{ color: msgColor }" @mousedown="onPreventDefault($event)">{{ msg }}</div>
        <div
          class="drag-div"
          :class="dragIcon"
          :style="{ width: dragW + 'px', left: dragLeft + 'px' }"
          @mousedown="onMouseDown($event)"
          @touchstart="onTouchStart($event)"
        ></div>
      </div>
    </div>
  </div>
</template>

<script>
import { getYZMPic, checkYZMPic } from '@/api/api/user'
export default {
  name: 'Validation',
  props: {
    formPhone: {
      type: Object,
    },
    formEmail: {
      type: Object,
    },
    activeName: {
      type: String,
    },
  },
  data() {
    return {
      titleMsg: '',
      bgW: 560,
      bgH: 360,
      bgImg: '',
      sliceImg: '',
      sliceW: 110,
      sliceH: 360,
      dragW: 40,
      dragLeft: 0,
      dragIcon: 'drag-icon',
      msg: 'Drag the slider to complete the puzzle above',
      msgColor: '#2c3e50',
      msgBgColor: '#67c23a',
      isMoving: false,
      canMove: true,
      beginClientX: 0,
      screenWidth: null,
    }
  },
  watch: {
    screenWidth: function(n, o) {
      console.log(n, o)
      if (n <= 768) {
        this.sliceW = 55
        this.sliceH = 170
        this.bgW = 280
        this.bgH = 170
      } else {
        this.sliceW = 110
        this.sliceH = 360
        this.bgW = 560
        this.bgH = 360
      }
    },
  },
  computed: {
    maxW() {
      return this.bgW - this.dragW
    },
    msgBgW() {
      return this.dragLeft + this.dragW / 2
    },
    sliceX: {
      get() {
        return (this.dragLeft / (this.bgW - this.dragW)) * (this.bgW - this.sliceW)
      },
      set(val) {
        return val
      },
    },
  },
  mounted: function() {
    this.registerCaptcha()
    this.screenWidth = document.body.clientWidth
    window.onresize = () => {
      return () => {
        this.screenWidth = document.body.clientWidth
      }
    }
  },
  methods: {
    registerCaptcha() {
      getYZMPic({
        type: 'REGISTER',
        username: this.activeName == 'first' ? this.formPhone.phone : this.formEmail.email,
      }).then(res => {
        if (res.code == 0) {
          this.sliceImg = res.data.sliderImage
          this.bgImg = res.data.backgroundImage
          this.sliceX = 0
        } else {
          this.$message({
            type: 'error',
            showClose: true,
            message: 'Registration failed！',
          })
          this.errorStyle('Registration failed！')
        }
      })
    },
    checkCaptcha() {
      var sliceX = parseInt(this.sliceX)
      var code = sliceX / (this.bgW - this.sliceW)
      checkYZMPic({
        code: code,
        type: 'REGISTER',
        username: this.activeName == 'first' ? this.formPhone.phone : this.formEmail.email,
      }).then(res => {
        if (res.code === 0) {
          this.successStyle()
          this.$emit('success', res.data)
          this.restoreCaptcha()
        } else if (res.code === 500) {
          this.errorStyle('verification failed!')
          let _this = this
          this.animateCSS('.captcha', 'shake', function() {
            _this.refreshCaptcha()
          })
        } else {
          this.$emit('close')
        }
      })
    },
    restoreCaptcha() {
      this.dragLeft = 0
      this.isMoving = false
      this.canMove = true
      this.beginClientX = 0
      this.defaultStyle()
    },
    refreshCaptcha() {
      this.restoreCaptcha()
      this.registerCaptcha()
    },
    onClickRefresh() {
      this.refreshCaptcha()
    },
    onClickClose() {
      this.$emit('close', false)
    },
    onPreventDefault(e) {
      e.preventDefault && e.preventDefault()
    },
    onMouseDown(e) {
      if (this.canMove) {
        this.addEventListener('mouse')
        e.preventDefault && e.preventDefault()
        this.isMoving = true
        this.beginClientX = e.clientX
      }
    },
    onTouchStart(e) {
      if (this.canMove) {
        this.addEventListener('touch')
        e.preventDefault && e.preventDefault()
        this.isMoving = true
        this.beginClientX = e.changedTouches[0].clientX
      }
    },
    addEventListener(type) {
      let html = document.getElementsByTagName('html')[0]
      if (type === 'mouse') {
        html.addEventListener('mousemove', this.onMouseMove)
        html.addEventListener('mouseup', this.onMouseUp)
      } else if (type === 'touch') {
        html.addEventListener('touchmove', this.onTouchMove)
        html.addEventListener('touchend', this.onTouchEnd)
      }
    },
    onMouseMove(e) {
      if (this.isMoving) {
        let width = e.clientX - this.beginClientX
        if (width > 0 && width < this.maxW) {
          this.dragLeft = width
        } else if (width >= this.maxW) {
          this.dragLeft = this.maxW
        }
      }
    },
    onTouchMove(e) {
      if (this.isMoving) {
        let width = e.changedTouches[0].clientX - this.beginClientX
        if (width > 0 && width < this.maxW) {
          this.dragLeft = width
        } else if (width >= this.maxW) {
          this.dragLeft = this.maxW
        }
      }
    },
    onMouseUp(e) {
      console.log(e)
      this.removeEventListener('mouse')
      this.canMove = false
      this.isMoving = false
      this.checkCaptcha()
    },
    onTouchEnd(e) {
      console.log(e)
      this.removeEventListener('touch')
      this.canMove = false
      this.isMoving = false
      this.checkCaptcha()
    },
    removeEventListener(type) {
      let html = document.getElementsByTagName('html')[0]
      if (type === 'mouse') {
        if (window.addEventListener) {
          html.removeEventListener('mousemove', this.onMouseMove)
          html.removeEventListener('mouseup', this.onMouseUp)
        } else {
          html.removeEventListener('mouseup', () => {})
        }
      } else if (type === 'touch') {
        if (window.addEventListener) {
          html.removeEventListener('touchmove', this.onTouchMove)
          html.removeEventListener('touchend', this.onTouchEnd)
        } else {
          html.removeEventListener('touchend', () => {})
        }
      }
    },
    defaultStyle() {
      this.dragIcon = 'drag-icon'
      this.msgBgColor = '#67c23a'
      this.msgColor = '#2c3e50'
      this.msg = 'Drag the slider to complete the puzzle above'
      this.titleMsg = ''
    },
    successStyle() {
      this.dragIcon = 'drag-success-icon'
      this.msgBgColor = '#67c23a'
      this.msgColor = '#fff'
      this.msg = ''
      this.titleMsg = ''
    },
    errorStyle(errorMsg) {
      this.dragIcon = 'drag-error-icon'
      this.msgBgColor = '#f56c6c'
      this.msgColor = '#fff'
      this.msg = ''
      this.titleMsg = errorMsg
    },
    animateCSS(element, animationName, callback) {
      const node = document.querySelector(element)
      node.classList.add('animated', animationName)

      function handleAnimationEnd() {
        node.classList.remove('animated', animationName)
        node.removeEventListener('animationend', handleAnimationEnd)
        if (typeof callback === 'function') callback()
      }
      node.addEventListener('animationend', handleAnimationEnd)
    },
  },
}
</script>

<style scoped>
.captcha {
  overflow: hidden;
}

.captcha .captcha-title {
  width: 100%;
  height: 25px;
  margin-bottom: 20px;
}

.captcha .captcha-title .title-left {
  float: left;
  height: 25px;
  line-height: 25px;
  padding-left: 2px;
}

.captcha .captcha-title .title-right {
  float: right;
  height: 25px;
  line-height: 25px;
}

.captcha .captcha-title .title-right i {
  font-size: 24px;
  cursor: pointer;
  color: #7f7f7f;
  margin-left: 10px;
}

.captcha .captcha-title .title-right i:hover {
  color: #000000;
  font-weight: 500;
}

.captcha .captcha-image {
  position: relative;
}

.captcha .captcha-image .image-slice {
  position: absolute;
  display: block;
  cursor: pointer;
  top: 0;
}

.captcha .captcha-drag {
  margin: 10px 0 0 0;
  width: 100%;
  height: 40px;
  line-height: 40px;
  position: relative;
  background-color: #e8e8e8;
  text-align: center;
  border-radius: 2px;
}

.captcha .captcha-drag .drag-bg {
  height: 40px;
  border-radius: 2px;
}

.captcha .captcha-drag .drag-text {
  position: absolute;
  top: 0;
  width: 100%;
  text-align: center;
  user-select: none;
}

.captcha .captcha-drag .drag-div {
  height: 40px;
  border: 1px solid #ccc;
  position: absolute;
  top: 0;
  border-radius: 2px;
  box-shadow: 0 0 2px rgba(0, 0, 0, 0.3);
  cursor: pointer;
}

.captcha .captcha-drag .drag-icon {
  background: #fff
    url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAA3hpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNS1jMDIxIDc5LjE1NTc3MiwgMjAxNC8wMS8xMy0xOTo0NDowMCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtcE1NOk9yaWdpbmFsRG9jdW1lbnRJRD0ieG1wLmRpZDo0ZDhlNWY5My05NmI0LTRlNWQtOGFjYi03ZTY4OGYyMTU2ZTYiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6NTEyNTVEMURGMkVFMTFFNEI5NDBCMjQ2M0ExMDQ1OUYiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6NTEyNTVEMUNGMkVFMTFFNEI5NDBCMjQ2M0ExMDQ1OUYiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTQgKE1hY2ludG9zaCkiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo2MTc5NzNmZS02OTQxLTQyOTYtYTIwNi02NDI2YTNkOWU5YmUiIHN0UmVmOmRvY3VtZW50SUQ9InhtcC5kaWQ6NGQ4ZTVmOTMtOTZiNC00ZTVkLThhY2ItN2U2ODhmMjE1NmU2Ii8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+YiRG4AAAALFJREFUeNpi/P//PwMlgImBQkA9A+bOnfsIiBOxKcInh+yCaCDuByoswaIOpxwjciACFegBqZ1AvBSIS5OTk/8TkmNEjwWgQiUgtQuIjwAxUF3yX3xyGIEIFLwHpKyAWB+I1xGSwxULIGf9A7mQkBwTlhBXAFLHgPgqEAcTkmNCU6AL9d8WII4HOvk3ITkWJAXWUMlOoGQHmsE45ViQ2KuBuASoYC4Wf+OUYxz6mQkgwAAN9mIrUReCXgAAAABJRU5ErkJggg==')
    no-repeat center;
}

.captcha .captcha-drag .drag-success-icon {
  background: #fff
    url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAA3hpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNS1jMDIxIDc5LjE1NTc3MiwgMjAxNC8wMS8xMy0xOTo0NDowMCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtcE1NOk9yaWdpbmFsRG9jdW1lbnRJRD0ieG1wLmRpZDo0ZDhlNWY5My05NmI0LTRlNWQtOGFjYi03ZTY4OGYyMTU2ZTYiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6NDlBRDI3NjVGMkQ2MTFFNEI5NDBCMjQ2M0ExMDQ1OUYiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6NDlBRDI3NjRGMkQ2MTFFNEI5NDBCMjQ2M0ExMDQ1OUYiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTQgKE1hY2ludG9zaCkiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDphNWEzMWNhMC1hYmViLTQxNWEtYTEwZS04Y2U5NzRlN2Q4YTEiIHN0UmVmOmRvY3VtZW50SUQ9InhtcC5kaWQ6NGQ4ZTVmOTMtOTZiNC00ZTVkLThhY2ItN2U2ODhmMjE1NmU2Ii8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+k+sHwwAAASZJREFUeNpi/P//PwMyKD8uZw+kUoDYEYgloMIvgHg/EM/ptHx0EFk9I8wAoEZ+IDUPiIMY8IN1QJwENOgj3ACo5gNAbMBAHLgAxA4gQ5igAnNJ0MwAVTsX7IKyY7L2UNuJAf+AmAmJ78AEDTBiwGYg5gbifCSxFCZoaBMCy4A4GOjnH0D6DpK4IxNSVIHAfSDOAeLraJrjgJp/AwPbHMhejiQnwYRmUzNQ4VQgDQqXK0ia/0I17wJiPmQNTNBEAgMlQIWiQA2vgWw7QppBekGxsAjIiEUSBNnsBDWEAY9mEFgMMgBk00E0iZtA7AHEctDQ58MRuA6wlLgGFMoMpIG1QFeGwAIxGZo8GUhIysmwQGSAZgwHaEZhICIzOaBkJkqyM0CAAQDGx279Jf50AAAAAABJRU5ErkJggg==')
    no-repeat center;
}

.captcha .captcha-drag .drag-error-icon {
  background: #fff
    url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABNUlEQVQ4T6VTwU3DQBCcxf6gy4MSoAKgAtIBoQLgF8mWEjpxJJ+UZ+jA6SB0YFcAJeSBxcdm0J18jn22okTcb29v53ZmdwTe+ZnPr+sgWAgwhcidTZM5gV1Q16vL9fqrWyLd4DuOEwEWPmg3JplMtH5zdy1AGcc5gNtjxW2OzJXW9ya2AKf87AMTWE3SdCmG828YfroHBD6E3EPksVdEbilyJcCDu7+oqhsZ+T1TafpURtEGIs+NiO9K65cyirIusOlCRrmTG6X1qwUB0BQfANt2mRsAjgrXgJhcrxvv8TEA2/YpAMPxkY6zocCWjtPk0EUxFJHcKq1nnohOk6GII2PcCbAHMPPoZgTMGKe9Mf57kRzaWasMFCpNrdHON1OzwgMzuQtr5zBcNlyduQpr56pKfDv/AUZmrmbsIMGcAAAAAElFTkSuQmCC')
    no-repeat center;
}
</style>
