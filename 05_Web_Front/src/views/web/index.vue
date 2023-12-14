<template>
	<div class="page-web page-home">
		<Head />
		<div v-html="$t('testText1')"></div>
		<div class="index-banner">
			<div class="container">
				<div class="index-grid flex">
					<div class="grid-photo">
						<img src="../../assets/photo/web/banner.svg" alt="" class="wow fadeInRight animated" data-wow-duration="1s" data-wow-delay="0.2s">
					</div>
					<div class="flex_bd">
						<div class="grid-title" id="element"></div>
						<div class="grid-desc">{{$t('indexTxt2')}}</div>
					</div>
				</div>
				<div class="index-intro u-m-b-40">
					<div class="h1">{{$t('indexTxt3')}}
						<div class="index-intro-line"></div> {{$t('indexTxt4')}}
					</div>
					<div class="p">{{$t('indexTxt5')}}</div>
				</div>
				<div class="index-attr flex">
					<div @click="toUrl('/exchange/btc_usdt')" class="wow fadeInUp animated col hvr-wobble-top" data-wow-duration="1s" data-wow-delay="0.2s">
						<h3 class="shake-chunk">{{$t('indexLabel1')}}</h3>
						<p>{{$t('indexVal1')}}</p>
					</div>
					<div class="line"></div>
					<div @click="toUrl('/option')" class="wow fadeInUp animated col hvr-wobble-horizontal" data-wow-duration="1s" data-wow-delay="0.3s">
						<h3>{{$t('indexLabel2')}}</h3>
						<p>{{$t('indexVal2')}}</p>
					</div>
					<div class="line"></div>
					<div @click="toUrl('/second/btc_usdt')" class="wow fadeInUp animated col hvr-wobble-vertical" data-wow-duration="1s" data-wow-delay="0.4s">
						<h3>{{$t('indexLabel3')}}</h3>
						<p>{{$t('indexVal3')}}</p>
					</div>
					<div class="line"></div>
					<div @click="toUrl('/convert')" class="wow fadeInUp animated col hvr-wobble-bottom" data-wow-duration="1s" data-wow-delay="0.5s">
						<h3>{{$t('indexLabel4')}}</h3>
						<p>{{$t('indexVal4')}}</p>
					</div>
				</div>
			</div>
		</div>
		<div class="index-section">
			<div class="container">
				<div class="index-bk u-m-b-60">
					<el-row :gutter="10">
						<el-col :xs="24" :sm="12" :md="6" v-for="(item,index) in adList" :key="index">
							<router-link :to="item.linkUrl">
								<div class="bk-card bk-card-1 hvr-pulse-grow">
									<img :src="item.url" alt="">
								</div>
							</router-link>
						</el-col>
					</el-row>
				</div>
				<div class="ec-line-box">
					<div class="ec-line-hd">
						<div class="ec-line-label">BTC/USDT</div>
						<div class="ec-line-num">{{lineData.close}}</div>
					</div>
					<div class="ec-line-bd" id="echart"></div>
					<div class="ec-line-ft">Highest price: {{lineData.high}} Lowest price: {{lineData.low}} Change(24H):
						<span class="text-green" v-if="lineData.chg > 0">+{{lineData.chg}}%</span>
						<span class="text-green" v-if="lineData.chg == 0">{{lineData.chg}}%</span>
						<span class="text-red" v-if="lineData.chg < 0">{{ lineData.chg}}%</span>
						Volume(24H): {{lineData.volume}}
					</div>
				</div>
				<div class="index-tab-main">
					<div class="tab-seach">
						<el-input :placeholder="$t('mplaceholder')" v-model.trim="searchInput" @input="search">
							<i slot="suffix" class="el-input__icon el-icon-search"></i>
						</el-input>
					</div>
					<el-tabs v-model="activeName">
						<el-tab-pane v-if="isLogin == true" :label="$t('mTab')" name="find">
							<div class="bz-table">
								<el-table :data="findData" style="width: 100%">
									<el-table-column width="60" :label="$t('mth')">
										<template slot-scope="scope">
											<el-button type="text" class="favor-btn"
												@click="delFavor(scope.row,scope.$index)">
												<span class="iconfont icon-star"></span>
											</el-button>
										</template>
									</el-table-column>
									<el-table-column width="160" prop="symbol" :label="$t('czTxt2')">
									</el-table-column>
									<el-table-column width="130" prop="close" :label="$t('mth1')">
									</el-table-column>
									<el-table-column width="130" prop="chg" :label="$t('mth2')">
										<template slot-scope="scope">
											<span class="text-green" v-if="scope.row.chg > 0">+{{scope.row.chg}}%</span>
											<span class="text-green" v-if="scope.row.chg == 0">{{scope.row.chg}}%</span>
											<span class="text-red" v-if="scope.row.chg < 0">{{ scope.row.chg}}%</span>
										</template>
									</el-table-column>
									<el-table-column width="130" prop="high" :label="$t('mTxt')">
									</el-table-column>
									<el-table-column width="130" prop="low" :label="$t('mTxt2')">
									</el-table-column>
									<el-table-column width="130" prop="volume" :label="$t('mTxt3')">
									</el-table-column>
									<el-table-column width="160" prop="trend" :label="$t('indexth')">
										<template slot-scope="scope">
											<div v-if="scope.row.chg > 0">
												<line-chart :width="120" :height="40" :id="'find'+scope.$index"
													:seriesData="scope.row.trend">
												</line-chart>
											</div>
											<div v-if="scope.row.chg < 0">
												<line-chart2 :width="120" :height="40" :id="'find'+scope.$index"
													:seriesData="scope.row.trend">
												</line-chart2>
											</div>
										</template>
									</el-table-column>
									<el-table-column :label="$t('th5')">
										<template slot-scope="scope">
											<el-button type="text" @click="toExchange(scope.row.symbol)">
												{{$t('indexbtn')}}
											</el-button>
										</template>
									</el-table-column>
								</el-table>
							</div>
						</el-tab-pane>
						<el-tab-pane label="USDT" name="USDT">
							<div class="bz-table">
								<el-table :data="tableData" style="width: 100%">
									<el-table-column prop="isFavor" width="60">
										<template slot-scope="scope">
											<el-button type="text" class="favor-btn"
												@click="delFavor(scope.row,scope.$index)" v-if="scope.row.isFavor">
												<span class="iconfont icon-star"></span>
											</el-button>
											<el-button @click="addFavor(scope.row)" type="text" class="favor-btn"
												v-else>
												<span class="iconfont icon-star_off"></span>
											</el-button>
										</template>
									</el-table-column>
									<el-table-column width="160" prop="symbol" :label="$t('czTxt2')">
									</el-table-column>
									<el-table-column width="130" prop="close" :label="$t('mth1')">
									</el-table-column>
									<el-table-column width="130" prop="chg" :label="$t('mth2')">
										<template slot-scope="scope">
											<span class="text-green" v-if="scope.row.chg > 0">+{{scope.row.chg}}%</span>
											<span class="text-green" v-if="scope.row.chg == 0">{{scope.row.chg}}%</span>
											<span class="text-red" v-if="scope.row.chg < 0">{{ scope.row.chg}}%</span>
										</template>
									</el-table-column>
									<el-table-column width="130" prop="high" :label="$t('mTxt')">
									</el-table-column>
									<el-table-column width="130" prop="low" :label="$t('mTxt2')">
									</el-table-column>
									<el-table-column width="130" prop="volume" :label="$t('mTxt3')">
									</el-table-column>
									<el-table-column width="160" prop="trend" :label="$t('indexth')">
										<template slot-scope="scope">
											<div v-if="scope.row.chg > 0">
												<line-chart :width="120" :height="40" :id="'chart'+scope.$index"
													:seriesData="scope.row.trend">
												</line-chart>
											</div>
											<div v-if="scope.row.chg < 0">
												<line-chart2 :width="120" :height="40" :id="'chart'+scope.$index"
													:seriesData="scope.row.trend">
												</line-chart2>
											</div>
										</template>
									</el-table-column>
									<el-table-column :label="$t('th5')">
										<template slot-scope="scope">
											<el-button type="text" @click="toExchange(scope.row.symbol)">
												{{$t('indexbtn')}}
											</el-button>
										</template>
									</el-table-column>
								</el-table>
							</div>
						</el-tab-pane>
						<el-tab-pane label="BTC" name="BTC">
							<div class="bz-table">
								<el-table :data="tableData1" style="width: 100%">
									<el-table-column prop="isFavor" width="60">
										<template slot-scope="scope">
											<el-button type="text" class="favor-btn"
												@click="delFavor(scope.row,scope.$index)" v-if="scope.row.isFavor">
												<span class="iconfont icon-star"></span>
											</el-button>
											<el-button @click="addFavor(scope.row)" type="text" class="favor-btn"
												v-else>
												<span class="iconfont icon-star_off"></span>
											</el-button>
										</template>
									</el-table-column>
									<el-table-column width="160" prop="symbol" :label="$t('czTxt2')">
									</el-table-column>
									<el-table-column width="130" prop="close" :label="$t('mth1')">
									</el-table-column>
									<el-table-column width="130" prop="chg" :label="$t('mth2')">
										<template slot-scope="scope">
											<span class="text-green" v-if="scope.row.chg > 0">+{{scope.row.chg}}%</span>
											<span class="text-green" v-if="scope.row.chg == 0">{{scope.row.chg}}%</span>
											<span class="text-red" v-if="scope.row.chg < 0">{{ scope.row.chg}}%</span>
										</template>
									</el-table-column>
									<el-table-column width="130" prop="high" :label="$t('mTxt')">
									</el-table-column>
									<el-table-column width="130" prop="low" :label="$t('mTxt2')">
									</el-table-column>
									<el-table-column width="130" prop="volume" :label="$t('mTxt3')">
									</el-table-column>
									<el-table-column width="160" prop="trend" :label="$t('indexth')">
										<template slot-scope="scope">
											<div v-if="scope.row.chg > 0">
												<line-chart :width="120" :height="40" :id="'BTC'+scope.$index"
													:seriesData="scope.row.trend">
												</line-chart>
											</div>
											<div v-if="scope.row.chg < 0">
												<line-chart2 :width="120" :height="40" :id="'BTC'+scope.$index"
													:seriesData="scope.row.trend">
												</line-chart2>
											</div>
										</template>
									</el-table-column>
									<el-table-column :label="$t('th5')">
										<template slot-scope="scope">
											<el-button type="text" @click="toExchange(scope.row.symbol)">
												{{$t('indexbtn')}}
											</el-button>
										</template>
									</el-table-column>
								</el-table>
							</div>
						</el-tab-pane>
						<el-tab-pane label="ETH" name="ETH">
							<div class="bz-table">
								<el-table :data="tableData2" style="width: 100%">
									<el-table-column prop="isFavor" width="60">
										<template slot-scope="scope">
											<el-button type="text" class="favor-btn"
												@click="delFavor(scope.row,scope.$index)" v-if="scope.row.isFavor">
												<span class="iconfont icon-star"></span>
											</el-button>
											<el-button @click="addFavor(scope.row)" type="text" class="favor-btn"
												v-else>
												<span class="iconfont icon-star_off"></span>
											</el-button>
										</template>
									</el-table-column>
									<el-table-column width="160" prop="symbol" :label="$t('czTxt2')">
									</el-table-column>
									<el-table-column width="130" prop="close" :label="$t('mth1')">
									</el-table-column>
									<el-table-column width="130" prop="chg" :label="$t('mth2')">
										<template slot-scope="scope">
											<span class="text-green" v-if="scope.row.chg > 0">+{{scope.row.chg}}%</span>
											<span class="text-green" v-if="scope.row.chg == 0">{{scope.row.chg}}%</span>
											<span class="text-red" v-if="scope.row.chg < 0">{{ scope.row.chg}}%</span>
										</template>
									</el-table-column>
									<el-table-column width="130" prop="high" :label="$t('mTxt')">
									</el-table-column>
									<el-table-column width="130" prop="low" :label="$t('mTxt2')">
									</el-table-column>
									<el-table-column width="130" prop="volume" :label="$t('mTxt3')">
									</el-table-column>
									<el-table-column width="160" prop="trend" :label="$t('indexth')">
										<template slot-scope="scope">
											<div v-if="scope.row.chg > 0">
												<line-chart :width="120" :height="40" :id="'ETH'+scope.$index"
													:seriesData="scope.row.trend">
												</line-chart>
											</div>
											<div v-if="scope.row.chg < 0">
												<line-chart2 :width="120" :height="40" :id="'ETH'+scope.$index"
													:seriesData="scope.row.trend">
												</line-chart2>
											</div>
										</template>
									</el-table-column>
									<el-table-column :label="$t('th5')">
										<template slot-scope="scope">
											<el-button type="text" @click="toExchange(scope.row.symbol)">
												{{$t('indexbtn')}}
											</el-button>
										</template>
									</el-table-column>
								</el-table>
							</div>
						</el-tab-pane>
					</el-tabs>
				</div>
			</div>
		</div>
		<div class="index-section bg-section">
			<div class="container">
				<div class="index-grid flex">
					<div class="grid-photo">
						<img src="../../assets/photo/web/pic.png" alt="" class="wow fadeInRight animated" data-wow-duration="1s" data-wow-delay="0.3s">
					</div>
					<div class="flex_bd">
						<div class="index-intro index-intro2 u-m-b-100">
							<div class="wow fadeInLeft animated h1" data-wow-duration="1s" data-wow-delay="0.2s">{{$t('indexh1')}} | {{$t('indexh2')}} | {{$t('indexh3')}} |
								{{$t('indexh4')}}
							</div>
							<div class="wow fadeInLeft animated h1-lg" data-wow-duration="1s" data-wow-delay="0.3s">{{$t('footmeun1')}} BITISAN</div>
						</div>
						<div class="wow fadeInLeft animated intro-desc u-m-b-40" data-wow-duration="1s" data-wow-delay="0.4s">
							{{$t('indexp1')}}
						</div>
						<div class="wow fadeInLeft animated intro-desc" data-wow-duration="1s" data-wow-delay="0.5s">
							{{$t('indexp2')}}
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="index-section">
			<div class="container">
				<div class="index-grid flex">
					<div class="grid-photo">
						<img src="../../assets/photo/web/pic2.png" alt="" class="wow fadeInRight animated" data-wow-duration="1s" data-wow-delay="0.3s">
					</div>
					<div class="flex_bd">
						<ul class="bk-list">
							<li class="wow fadeInUp animated" data-wow-duration="1s" data-wow-delay="0.3s">
								<div class="bk-ico"><img src="../../assets/photo/web/bk1.svg" alt=""></div>
								<div class="flex_bd">
									<h3>{{$t('indexh5')}}</h3>
									<p>{{$t('indexp3')}}</p>
								</div>
							</li>
							<li class="wow fadeInUp animated" data-wow-duration="1s" data-wow-delay="0.4s">
								<div class="bk-ico"><img src="../../assets/photo/web/bk2.svg" alt=""></div>
								<div class="flex_bd">
									<h3>{{$t('indexh6')}}</h3>
									<p>{{$t('indexp4')}}</p>
								</div>
							</li>
							<li class="wow fadeInUp animated" data-wow-duration="1s" data-wow-delay="0.5s">
								<div class="bk-ico"><img src="../../assets/photo/web/bk3.svg" alt=""></div>
								<div class="flex_bd">
									<h3>{{$t('indexh7')}}</h3>
									<p>{{$t('indexp5')}}</p>
								</div>
							</li>
							<li class="wow fadeInUp animated" data-wow-duration="1s" data-wow-delay="0.6s">
								<div class="bk-ico"><img src="../../assets/photo/web/bk4.svg" alt=""></div>
								<div class="flex_bd">
									<h3>{{$t('indexh8')}}</h3>
									<p>{{$t('indexp6')}}</p>
								</div>
							</li>
						</ul>
					</div>
				</div>
			</div>
		</div>
		<div class="index-section">
			<div class="container">
				<div class="index-grid u-m-b-100">
					<div class="flex_bd">
						<div class="index-intro index-intro3">
							<div class="h1">{{$t('openTrade')}}</div>
							<div class="h1-sm">{{$t('indexp7')}}</div>
						</div>
					</div>
				</div>
				<div class="index-download flex">
					<div class="flex_bd">
						<div class="wow fadeInLeft animated dw-photo" data-wow-duration="1s" data-wow-delay="0.4s"><img src="../../assets/photo/web/dw.svg" alt=""></div>
					</div>
					<div class="wow fadeInRight animated dw-right" data-wow-duration="1s" data-wow-delay="0.4s">
						<div class="wx-panel flex flex-ac u-m-b-40">
							<div class="wx-code"><img src="../../assets/photo/web/appdownload.png" alt=""></div>
							<div class="dw-grid">
								<div class="dw-title" style="font-weight:700;">Download APP</div>
								<div class="dw-info">{{$t('scan')}}</div>
							</div>
						</div>
						<ul class="dw-list">
							<li>
								<span class="iconfont icon-ios"></span>
								<p>iOS</p>
							</li>
							<li>
								<span class="iconfont icon-android"></span>
								<p>Android</p>
							</li>
						</ul>
					</div>
				</div>
			</div>
		</div>
		<Foot />
	</div>
</template>

<script>
	import * as echarts from 'echarts'
	import {
		symbolTrend,
		marketBtcTrend
	} from '@/api/api/market'
	import {
		ucAdvertise
	} from '@/api/api/user'
	import {
		favorFind,
		favorAdd,
		favorDelete,
	} from '@/api/api/exchange'
	import SockJS from 'sockjs-client';
	import Stomp from 'stompjs';
	import Head from '@/components/Head.vue'
	import lineChart from '@/components/lineChart.vue'
	import lineChart2 from '@/components/lineChart2.vue'
	import Foot from '@/components/Foot.vue'
	import { WOW } from 'wowjs'
	import Typed from 'typed.js'
	export default {
		name: 'Home',
		components: {
			Head,
			Foot,
			lineChart,
			lineChart2
		},
		data() {
			return {
				activeName: 'USDT',
				searchVal: '',
				findData: [],
				tableData: [],
				tableData1: [],
				tableData2: [],
				old: [],
				old2: [],
				old3: [],
				old4: [],
				copy: {
					findData: [],
					tableData: [],
					tableData1: [],
					tableData2: []
				},
				lineData: {},
				user: {},
				isLogin: false,
				allData: [],
				adList: [],
				timer:null
			};
		},
		created() {
			this.isLogin = this.$store.state.isLogin
		},
		mounted() {
			this.getUser()
			this.symbolList()
			this.getAdv()
			
			setTimeout(()=>{
			 let wow = new WOW({
				boxClass: 'wow',
				animateClass: 'animated',
				offset: 0,
				mobile: true,
				live: false,
			})
			wow.init()
			new Typed('#element', {
				strings: [this.$t('indexTxt2')],
				typeSpeed: 100,
			})
			})
			
		},
		computed: {
			searchInput: {
				get() {
					return this.searchVal;
				},
				set(val) {
					this.searchVal = val.toUpperCase();
				}
			}
		},
		methods: {
			getAdv() {
				ucAdvertise({
					sysAdvertiseLocation: 1,
					lang: localStorage.getItem('lang')
				}).then(res => {
					if (res.code == 0) {
						this.adList = res.data
					}
				})
			},
			getUser() {
				this.user = this.$store.state.user
				console.log(this.user)
			},
			getFavor() {
				favorFind().then(res => {
					this.findData = []
					this.old4 = []
					this.allData.forEach(item => {
						res.data.forEach(itx => {
							if (item.symbol == itx.symbol) {
								item.isFavor = true
								this.findData.push(item)
								this.old4.push(item)
							}
						})
					})
				})
			},
			symbolList() {
				symbolTrend().then(res => {
					var arr = []
					res.forEach(item => {
						item.chg = this.$math.format(
							this.$math.multiply(
								this.$math.bignumber(100), this.$math.bignumber(item.chg)
							)
						)
						item.isFavor = false
						return arr.push(item)
					})
					this.allData = arr
					var usdt = arr.filter(item => {
						return item.symbol.split('/')[1] == 'USDT'
					})
					this.tableData = this.old = usdt
					var btc = arr.filter(item => {
						return item.symbol.split('/')[1] == 'BTC'
					})
					this.tableData1 = this.old1 = btc
					var eth = arr.filter(item => {
						return item.symbol.split('/')[1] == 'ETH'
					})
					this.tableData2 = this.old2 = eth
					this.lineData = this.tableData[0]
					var str = this.lineData.symbol.toLowerCase()
					var newArr = str.split('/')
					var unit = newArr[0] + '_' + newArr[1]
					localStorage.setItem('setSymbol', unit)
					this.loadTrendData()
					if (this.isLogin == true) {
						this.getFavor()
					}
					this.startWebsock()
				})
			},
			loadTrendData() {
				marketBtcTrend().then(res => {
					let max = Math.max.apply(Math, res.data);
					let min = Math.min.apply(Math, res.data);
					let xAxisData = []
					for (let i = 0; i < res.data.length; i++) {
						xAxisData.push(i)
					}
					let myChart = echarts.init(document.getElementById('echart'))
					let options = {
						grid: {
							left: '0%',
							right: '0%',
							bottom: '0%',
							top: '20%',
							containLabel: true
						},
						xAxis: {
							type: 'category',
							boundaryGap: false,
							data: xAxisData,
							axisLabel: {
								interval: 0,
								show: false
							},
							axisLine: {
								show: false
							},
							splitLine: {
								show: false
							}
						},
						yAxis: {
							type: 'value',
							min: min,
							max: max,
							axisLabel: {
								show: false
							},
							axisLine: {
								show: false
							},
							splitLine: {
								show: false
							}
						},
						series: [{
							data: res.data,
							type: 'line',
							symbol: "none",
							lineStyle: {
								width: 2,
								color: '#689AFF',
							},
							areaStyle: {
								color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
										offset: 0,
										color: 'rgba(104,154,255, 0.18)'
									},
									{
										offset: 1,
										color: 'rgba(104,154,255, 0)'
									}
								])
							}
						}]
					}
					myChart.setOption(options)
					window.addEventListener('resize', () => {
						myChart.resize()
					})
				})
			},
			toExchange(val) {
				var str = val.toLowerCase()
				var arr = str.split('/')
				var unit = arr[0] + '_' + arr[1]
				this.$router.push({
					path: '/exchange/' + unit
				})
			},
			search(value) {
				let keyWord = value.toUpperCase()
				if (this.activeName == 'find') {
					if (value == '') {
						this.findData = this.old4
					} else {
						this.findData = this.old4.filter(item => item.symbol.indexOf(keyWord) >= 0)
					}
				}
				if (this.activeName == 'USDT') {
					if (value == '') {
						this.tableData = this.old
					} else {
						this.tableData = this.old.filter(item => item.symbol.indexOf(keyWord) >= 0)
					}
				}
				if (this.activeName == 'BTC') {
					if (value == '') {
						this.tableData1 = this.old1
					} else {
						this.tableData1 = this.old1.filter(item => item.symbol.indexOf(keyWord) >= 0)
					}
				}
				if (this.activeName == 'ETH') {
					if (value == '') {
						this.tableData2 = this.old2
					} else {
						this.tableData2 = this.old2.filter(item => item.symbol.indexOf(keyWord) >= 0)
					}
				}
			},
			addFavor(row) {
				if (this.isLogin == false) {
					this.$message.info(this.$t('loginFirst'))
				} else {
					favorAdd({
						symbol: row.symbol
					}).then(res => {
						if (res.code == 0) {
							this.$message.info(this.$t('do_favorite'))
							row.isFavor = true
							this.findData.push(row)
						} else {
							this.$message.error(this.$t('fail_favorite'))
						}
					})
				}
			},
			delFavor(row, index) {
				favorDelete({
					symbol: row.symbol
				}).then(res => {
					if (res.code == 0) {
						this.$message.info(this.$t('cancel_favorite'))
						if (this.activeName == 'find') {
							this.findData.splice(index, 1)
						} else {
							row.isFavor = false
						}
					} else {
						this.$message.error(this.$t('fail_favorite'))
					}
				})
			},
			toUrl(url) {
				this.$router.push(url)
			},
			startWebsock() {
				var stompClient = null
				var that = this
				var socket = new SockJS(that.host + '/market/market-ws')
				stompClient = Stomp.over(socket)
				stompClient.debug = false
				stompClient.connect({}, function(frame) {
					console.log(frame)
					stompClient.subscribe('/topic/market/thumb', function(msg) {
						var resp = JSON.parse(msg.body)
						resp.chg = that.$math.format(
							that.$math.multiply(
								that.$math.bignumber(100), that.$math.bignumber(resp.chg)
							)
						)
						if (that.lineData.symbol == resp.symbol) {
							that.lineData.baseUsdRate = resp.baseUsdRate
							that.lineData.change = resp.change
							that.lineData.chg = resp.chg
							that.lineData.close = resp.close
							that.lineData.high = resp.high
							that.lineData.lastDayClose = resp.lastDayClose
							that.lineData.low = resp.low
							that.lineData.open = resp.open
							that.lineData.time = resp.time
							that.lineData.turnover = resp.turnover
							that.lineData.usdRate = resp.usdRate
							that.lineData.volume = resp.volume
							that.lineData.zone = resp.zone
						}
						that.findData.forEach(item => {
							if (item.symbol == resp.symbol) {
								item.baseUsdRate = resp.baseUsdRate
								item.change = resp.change
								item.chg = resp.chg
								item.close = resp.close
								item.high = resp.high
								item.lastDayClose = resp.lastDayClose
								item.low = resp.low
								item.open = resp.open
								item.time = resp.time
								item.turnover = resp.turnover
								item.usdRate = resp.usdRate
								item.volume = resp.volume
								item.zone = resp.zone
							}
						})
						that.tableData.forEach(item => {
							if (item.symbol == resp.symbol) {
								item.baseUsdRate = resp.baseUsdRate
								item.change = resp.change
								item.chg = resp.chg
								item.close = resp.close
								item.high = resp.high
								item.lastDayClose = resp.lastDayClose
								item.low = resp.low
								item.open = resp.open
								item.time = resp.time
								item.turnover = resp.turnover
								item.usdRate = resp.usdRate
								item.volume = resp.volume
								item.zone = resp.zone
							}
						})
						that.tableData1.forEach(item => {
							if (item.symbol == resp.symbol) {
								item.baseUsdRate = resp.baseUsdRate
								item.change = resp.change
								item.chg = resp.chg
								item.close = resp.close
								item.high = resp.high
								item.lastDayClose = resp.lastDayClose
								item.low = resp.low
								item.open = resp.open
								item.time = resp.time
								item.turnover = resp.turnover
								item.usdRate = resp.usdRate
								item.volume = resp.volume
								item.zone = resp.zone
							}
						})
						that.tableData2.forEach(item => {
							if (item.symbol == resp.symbol) {
								item.baseUsdRate = resp.baseUsdRate
								item.change = resp.change
								item.chg = resp.chg
								item.close = resp.close
								item.high = resp.high
								item.lastDayClose = resp.lastDayClose
								item.low = resp.low
								item.open = resp.open
								item.time = resp.time
								item.turnover = resp.turnover
								item.usdRate = resp.usdRate
								item.volume = resp.volume
								item.zone = resp.zone
							}
						})
					})
				})
			},
		},
		watch: {

		}
	}
</script>
<style scoped>
::v-deep .typed-cursor{
	display: none !important;
}
.index-download .flex_bd{
	padding-right: 80px;
}
@media (max-width: 700px) {
	.index-download .flex_bd{
	padding-right: 0;
}
}
</style>
