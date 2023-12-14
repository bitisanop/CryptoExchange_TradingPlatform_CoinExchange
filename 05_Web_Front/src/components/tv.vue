<template>
  <div id="tv_chart_container" style="width: 100%; height: 100%; margin: auto"></div>
</template>
<script>
import Datafeeds from '@/assets/js/charting_library/datafeed/swaptrade.js'
import $ from '@/assets/js/jquery.min'
export default {
  components: {},
  props: ['symbol', 'interval', 'type'],
  data() {
    return {
      datafeed: null,
      dataParam:null,
    }
  },
  created() {},
  computed:{
    theme() {
      return this.$store.state.theme;
    }
  },
  watch:{
    theme() {
      this.getKline(this.dataParam);
    }
  },
  methods: {
    getKline(data) {
      const { a, b, c, d } = data
      this.dataParam = data;
      this.datafeed = new Datafeeds.WebsockFeed(a, b, c, d)
      var that = this
      let config = {
        autosize: true,
        height: 400,
        symbol: that.symbol,
        interval: '5',
        timezone: 'Asia/Shanghai',
        toolbar_bg: '#161A1E',
        container_id: 'tv_chart_container',
        datafeed: that.datafeed,
        library_path: '/charting_library/',
        locale: 'en',
        debug: false,
        drawings_access: {
          type: 'black',
          tools: [{ name: 'Regression Trend' }],
        },
         hide_top_toolbar: true,
         hide_side_toolbar: false,
        disabled_features: [
          'header_resolutions',
          'timeframes_toolbar',
          'header_symbol_search',
          'header_chart_type',
          'header_compare',
          'header_undo_redo',
          'header_screenshot',
          'header_saveload',
          'volume_force_overlay',
          'widget_logo',
          'compare_symbol',
          'display_market_status',
          'go_to_date',
          'header_interval_dialog_button',
          'legend_context_menu',
          'show_hide_button_in_legend',
          'show_interval_dialog_on_key_press',
          'snapshot_trading_drawings',
          'symbol_info',
          'edit_buttons_in_legend',
          'context_menus',
          'control_bar',
          'border_around_the_chart',
        ],
        enabled_features: ["hide_left_toolbar_by_default"],
        studies_overrides: {
          'volume.volume.color.0': '#00b275',
          'volume.volume.color.1': '#f15057',
          'volume.volume.transparency': 25,
        },
        custom_css_url: 'bundles/common.css',
        supported_resolutions: ['1', '5', '15', '30', '60', '4H', '1D', '1W', '1M'],
        charts_storage_url: 'http://saveload.tradingview.com',
        charts_storage_api_version: '1.1',
        client_id: 'tradingview.com',
        user_id: 'public_user_id',
        overrides: {
          'paneProperties.background': '#161A1E',
          'paneProperties.vertGridProperties.color': 'rgba(0,0,0,.1)',
          'paneProperties.horzGridProperties.color': 'rgba(0,0,0,.1)',
          'scalesProperties.textColor': '#61688A',
          'mainSeriesProperties.candleStyle.upColor': '#00b275',
          'mainSeriesProperties.candleStyle.downColor': '#f15057',
          'mainSeriesProperties.candleStyle.drawBorder': false,
          'mainSeriesProperties.candleStyle.wickUpColor': '#589065',
          'mainSeriesProperties.candleStyle.wickDownColor': '#AE4E54',
          'paneProperties.legendProperties.showLegend': false,
          'mainSeriesProperties.areaStyle.color1': 'rgba(71, 78, 112, 0.5)',
          'mainSeriesProperties.areaStyle.color2': 'rgba(71, 78, 112, 0.5)',
          'mainSeriesProperties.areaStyle.linecolor': '#9194a4',
          volumePaneSize: 'small',
        },
        time_frames: [
          {
            text: '1min',
            resolution: '1',
            description: 'realtime',
            title: that.$t('swap.realtime'),
          },
          { text: '1min', resolution: '1', description: '1min' },
          { text: '5min', resolution: '5', description: '5min' },
          { text: '15min', resolution: '15', description: '15min' },
          { text: '30min', resolution: '30', description: '30min' },
          { text: '1hour', resolution: '60', description: '1hour', title: '1hour' },
          { text: '4hour', resolution: '240', description: '4hour', title: '4hour' },
          { text: '1day', resolution: '1D', description: '1day', title: '1day' },
          { text: '1week', resolution: '1W', description: '1week', title: '1week' },
          { text: '1mon', resolution: '1M', description: '1mon' },
        ],
      }
      let theme = localStorage.getItem('theme')
      console.log(theme);
      if (theme === 'white' || theme === null) {
        config.toolbar_bg = '#fff'
        config.custom_css_url = 'bundles/common_day.css'
        config.overrides['paneProperties.background'] = '#fff'
        config.overrides['mainSeriesProperties.candleStyle.upColor'] = '#a6d3a5'
        config.overrides['mainSeriesProperties.candleStyle.downColor'] = '#ffa5a6'
      }
      window.tvWidget = new TradingView.widget(config)
      require(['@/assets/js/charting_library/charting_library.min.js'], () => {
        var widget = (window.tvWidget = new TradingView.widget(config))
        widget.onChartReady(() => {
          widget.chart().executeActionById('drawingToolbarAction')
        })
        widget.onChartReady(()=> {
          widget
              .chart()
              .createStudy("Moving Average", false, false, [5], null, {
                "plot.color": "#EDEDED"
              });
          widget
              .chart()
              .createStudy("Moving Average", false, false, [10], null, {
                "plot.color": "#ffe000"
              });
          widget
              .chart()
              .createStudy("Moving Average", false, false, [30], null, {
                "plot.color": "#ce00ff"
              });
          widget
              .chart()
              .createStudy("Moving Average", false, false, [60], null, {
                "plot.color": "#00adff"
              });
          widget
              .createButton()
              .attr("title", "realtime")
              .on("click", function() {
                if ($(this).hasClass("selected")) return;
                $(this)
                    .addClass("selected")
                    .parent(".group")
                    .siblings(".group")
                    .find(".button.selected")
                    .removeClass("selected");
                widget.chart().setChartType(3);
                widget.setSymbol("", "1");
              })
              .append("<span>Time</span>");

          widget
              .createButton()
              .attr("title", "M1")
              .on("click", function() {
                if ($(this).hasClass("selected")) return;
                $(this)
                    .addClass("selected")
                    .parent(".group")
                    .siblings(".group")
                    .find(".button.selected")
                    .removeClass("selected");
                widget.chart().setChartType(1);
                widget.setSymbol("", "1");
              })
              .append("<span>M1</span>");

          widget
              .createButton()
              .attr("title", "M5")
              .on("click", function() {
                if ($(this).hasClass("selected")) return;
                $(this)
                    .addClass("selected")
                    .parent(".group")
                    .siblings(".group")
                    .find(".button.selected")
                    .removeClass("selected");
                widget.chart().setChartType(1);
                widget.setSymbol("", "5");
              })
              .append("<span>M5</span>")
              .addClass("selected");

          widget
              .createButton()
              .attr("title", "M15")
              .on("click", function() {
                if ($(this).hasClass("selected")) return;
                $(this)
                    .addClass("selected")
                    .parent(".group")
                    .siblings(".group")
                    .find(".button.selected")
                    .removeClass("selected");
                widget.chart().setChartType(1);
                widget.setSymbol("", "15");
              })
              .append("<span>M15</span>");

          widget
              .createButton()
              .attr("title", "M30")
              .on("click", function() {
                if ($(this).hasClass("selected")) return;
                $(this)
                    .addClass("selected")
                    .parent(".group")
                    .siblings(".group")
                    .find(".button.selected")
                    .removeClass("selected");
                widget.chart().setChartType(1);
                widget.setSymbol("", "30");
              })
              .append("<span>M30</span>");

          widget
              .createButton()
              .attr("title", "H1")
              .on("click", function() {
                if ($(this).hasClass("selected")) return;
                $(this)
                    .addClass("selected")
                    .parent(".group")
                    .siblings(".group")
                    .find(".button.selected")
                    .removeClass("selected");
                widget.chart().setChartType(1);
                widget.setSymbol("", "60");
              })
              .append("<span>H1</span>");

          widget
              .createButton()
              .attr("title", "H4")
              .on("click", function() {
                if ($(this).hasClass("selected")) return;
                $(this)
                    .addClass("selected")
                    .parent(".group")
                    .siblings(".group")
                    .find(".button.selected")
                    .removeClass("selected");
                widget.chart().setChartType(1);
                widget.setSymbol("", "240");
              })
              .append("<span>H4</span>");

          widget
              .createButton()
              .attr("title", "D1")
              .on("click", function() {
                if ($(this).hasClass("selected")) return;
                $(this)
                    .addClass("selected")
                    .parent(".group")
                    .siblings(".group")
                    .find(".button.selected")
                    .removeClass("selected");
                widget.chart().setChartType(1);
                widget.setSymbol("", "1D");
              })
              .append("<span>D1</span>");
        });
      })
    },
  },
}
</script>
