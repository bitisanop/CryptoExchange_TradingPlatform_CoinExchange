<template>
  <div>
    <div :id="id" :style="{ height: height + 'px', width: width + 'px' }"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
export default {
  name: 'chart',
  components: {},
  props: {
    id: {
      type: String,
      default: 'chart',
    },
    width: {
      type: Number,
      default: 100,
    },
    height: {
      type: Number,
      default: 100,
    },
    seriesData: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      chart: null,
    }
  },
  created() {
    this.$nextTick(() => {
      this.initChart()
    })
  },
  watch: {
    data: {
      deep: true,
      handler() {
        this.initChart()
      },
    },
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    initChart() {
      this.chart = echarts.init(document.getElementById(this.id))
      let max = Math.max.apply(Math, this.seriesData)
      let min = Math.min.apply(Math, this.seriesData)
      let xAxisData = []
      for (let i = 0; i < this.seriesData.length; i++) {
        xAxisData.push(i)
      }
      this.chart.setOption({
        grid: {
          left: '0%',
          right: '0%',
          bottom: '0%',
          top: '0%',
          containLabel: true,
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: xAxisData,
          axisLabel: {
            interval: 0,
            show: false,
          },
          axisLine: {
            show: false,
          },
          splitLine: {
            show: false,
          },
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            show: false,
          },
          axisLine: {
            show: false,
          },
          splitLine: {
            show: false,
          },
          max: max,
          min: min,
        },
        series: [
          {
            data: this.seriesData,
            type: 'line',
            symbol: 'none',
            lineStyle: {
              width: 1,
              color: '#08A742',
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {
                  offset: 0,
                  color: 'rgba(96, 222, 101, 0.35)',
                },
                {
                  offset: 1,
                  color: 'rgba(151, 243, 148, 0)',
                },
              ]),
            },
          },
        ],
      })
    },
  },
}
</script>
<style></style>
