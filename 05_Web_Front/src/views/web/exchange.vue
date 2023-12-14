<template>
  <div class="page-web page-bg">
    <Head />
    <div class="ustandard-section">
      <div class="ustandard-body">
        <div class="mb-ui">
          <div class="ustandard-head">
            <div class="ustandard-head__hd">
              <el-popover placement="top-start" popper-class="custom-popper2" width="425" trigger="click">
                <div class="popper2-content">
                  <div class="popper2-search">
                    <el-input
                      :placeholder="$t('trplaceholder')"
                      prefix-icon="el-icon-search"
                      @input="search"
                      v-model="keyword"
                    ></el-input>
                  </div>
                  <div class="popper2-tab">
                    <el-tabs v-model="activeName4">
                      <el-tab-pane v-if="isLogin == true" :label="$t('mTab')" name="first">
                        <div class="popper2-table" :class="{ right: tableData4.length < 8 }">
                          <el-table :data="tableData4" @row-click="rowClick" style="width: 100%" height="220">
                            <el-table-column prop="symbol" :label="$t('czTxt2')" width="150">
                              <template slot-scope="scope">
                                <el-button
                                  type="text"
                                  class="favor-btn"
                                  @click.stop="delFavor(scope.row, scope.$index)"
                                  v-if="scope.row.isFavor"
                                >
                                  <span class="iconfont icon-star"></span>
                                </el-button>
                                <span>{{ scope.row.symbol }}</span>
                              </template>
                            </el-table-column>
                            <el-table-column prop="close" sortable align="left" :label="$t('mth1')"> </el-table-column>
                            <el-table-column prop="chg" sortable align="right" :label="$t('mth2')">
                              >
                              <template slot-scope="scope">
                                <span class="text-green" v-if="scope.row.chg > 0">+{{ scope.row.chg }}%</span>
                                <span class="text-green" v-if="scope.row.chg == 0">{{ scope.row.chg }}%</span>
                                <span class="text-red" v-if="scope.row.chg < 0">{{ scope.row.chg }}%</span>
                              </template>
                            </el-table-column>
                          </el-table>
                        </div>
                      </el-tab-pane>
                      <el-tab-pane label="USDT" name="second">
                        <div class="popper2-table" :class="{ right: thumb.data1.length < 8 }">
                          <el-table :data="thumb.data1" @row-click="rowClick" style="width: 100%" height="220">
                            <el-table-column prop="symbol" :label="$t('czTxt2')" width="150">
                              <template slot-scope="scope">
                                <el-button
                                  type="text"
                                  class="favor-btn"
                                  @click.stop="delFavor(scope.row, scope.$index)"
                                  v-if="scope.row.isFavor"
                                >
                                  <span class="iconfont icon-star"></span>
                                </el-button>
                                <el-button @click.stop="addFavor(scope.row)" type="text" class="favor-btn" v-else>
                                  <span class="iconfont icon-star_off"></span>
                                </el-button>
                                <span>{{ scope.row.symbol }}</span>
                              </template>
                            </el-table-column>
                            <el-table-column prop="close" sortable align="left" :label="$t('mth1')"> </el-table-column>
                            <el-table-column prop="chg" sortable align="right" :label="$t('mth2')">
                              <template slot-scope="scope">
                                <span class="text-green" v-if="scope.row.chg > 0">+{{ scope.row.chg }}%</span>
                                <span class="text-green" v-if="scope.row.chg == 0">{{ scope.row.chg }}%</span>
                                <span class="text-red" v-if="scope.row.chg < 0">{{ scope.row.chg }}%</span>
                              </template>
                            </el-table-column>
                          </el-table>
                        </div>
                      </el-tab-pane>
                      <el-tab-pane label="BTC" name="third">
                        <div class="popper2-table" :class="{ right: thumb.data2.length < 8 }">
                          <el-table :data="thumb.data2" @row-click="rowClick" style="width: 100%" height="220">
                            <el-table-column prop="symbol" :label="$t('czTxt2')" width="150">
                              <template slot-scope="scope">
                                <el-button
                                  type="text"
                                  class="favor-btn"
                                  @click.stop="delFavor(scope.row, scope.$index)"
                                  v-if="scope.row.isFavor"
                                >
                                  <span class="iconfont icon-star"></span>
                                </el-button>
                                <el-button @click.stop="addFavor(scope.row)" type="text" class="favor-btn" v-else>
                                  <span class="iconfont icon-star_off"></span>
                                </el-button>
                                <span>{{ scope.row.symbol }}</span>
                              </template>
                            </el-table-column>
                            <el-table-column prop="close" sortable align="left" :label="$t('mth1')"> </el-table-column>
                            <el-table-column prop="chg" sortable align="right" :label="$t('mth2')">
                              <template slot-scope="scope">
                                <span class="text-green" v-if="scope.row.chg > 0">+{{ scope.row.chg }}%</span>
                                <span class="text-green" v-if="scope.row.chg == 0">{{ scope.row.chg }}%</span>
                                <span class="text-red" v-if="scope.row.chg < 0">{{ scope.row.chg }}%</span>
                              </template>
                            </el-table-column>
                          </el-table>
                        </div>
                      </el-tab-pane>
                      <el-tab-pane label="ETH" name="fourth">
                        <div class="popper2-table" :class="{ right: thumb.data3.length < 8 }">
                          <el-table :data="thumb.data3" @row-click="rowClick" style="width: 100%" height="220">
                            <el-table-column prop="symbol" :label="$t('czTxt2')" width="150">
                              <template slot-scope="scope">
                                <el-button
                                  type="text"
                                  class="favor-btn"
                                  @click.stop="delFavor(scope.row, scope.$index)"
                                  v-if="scope.row.isFavor"
                                >
                                  <span class="iconfont icon-star"></span>
                                </el-button>
                                <el-button @click.stop="addFavor(scope.row)" type="text" class="favor-btn" v-else>
                                  <span class="iconfont icon-star_off"></span>
                                </el-button>
                                <span>{{ scope.row.symbol }}</span>
                              </template>
                            </el-table-column>
                            <el-table-column prop="close" sortable align="left" :label="$t('mth1')"> </el-table-column>
                            <el-table-column prop="chg" sortable align="right" :label="$t('mth2')">
                              <template slot-scope="scope">
                                <span class="text-green" v-if="scope.row.chg > 0">+{{ scope.row.chg }}%</span>
                                <span class="text-green" v-if="scope.row.chg == 0">{{ scope.row.chg }}%</span>
                                <span class="text-red" v-if="scope.row.chg < 0">{{ scope.row.chg }}%</span>
                              </template>
                            </el-table-column>
                          </el-table>
                        </div>
                      </el-tab-pane>
                    </el-tabs>
                  </div>
                </div>
                <el-button slot="reference" class="popper-btn">
                  <div class="el-dropdown-select">
                    <div class="el-dropdown-value flex_bd">{{ symbol }}</div>
                    <i class="el-icon-caret-bottom el-icon--right"></i>
                  </div>
                </el-button>
              </el-popover>
            </div>
            <div class="u-amount">
              <div class="u-amount-top" :class="{ buy: currentCoin.change > 0, sell: currentCoin.change < 0 }">
                {{ currentCoin.close | toFixed(baseCoinScale) }}
              </div>
            </div>
            <div class="ustandard-head__bd">
              <div class="u-item">
                <div class="u-item__label">{{ $t('mth2') }}</div>
                <div class="u-item__value" :class="{ buy: currentCoin.change > 0, sell: currentCoin.change < 0 }">
                  {{ currentCoin.rose }}
                </div>
              </div>
              <div class="u-item">
                <div class="u-item__label">{{ $t('mTxt') }}</div>
                <div class="u-item__value">{{ currentCoin.high | toFixed(baseCoinScale) }}</div>
              </div>
              <div class="u-item">
                <div class="u-item__label">{{ $t('mTxt2') }}</div>
                <div class="u-item__value">{{ currentCoin.low | toFixed(baseCoinScale) }}</div>
              </div>
              <div class="u-item">
                <div class="u-item__label">{{ $t('mTxt3') }}</div>
                <div class="u-item__value">{{ currentCoin.volume }} {{ currentCoin.coin }}</div>
              </div>
            </div>
          </div>
        </div>
        <div class="ustandard-tab">
          <div class="ustandard-tab-item" :class="{ active: smCurrent == 1 }" @click="smCurrent = 1">
            {{ $t('chart') }}
          </div>
          <div class="ustandard-tab-item" :class="{ active: smCurrent == 2 }" @click="smCurrent = 2">
            {{ $t('book') }}
          </div>
          <div class="ustandard-tab-item" :class="{ active: smCurrent == 3 }" @click="smCurrent = 3">
            {{ $t('trade') }}
          </div>
        </div>
        <div class="ustandard-row ustandard-mb">
          <div class="ustandard-col1 flex_bd" :class="{ show: smCurrent == 1 }">
            <div class="u4-right handler">
              <el-button type="text" :class="current == 1 ? 'el-button--active' : ''" @click="currentClick(1)">
                {{ $t('echat1') }}
              </el-button>
              <el-button type="text" :class="current == 2 ? 'el-button--active' : ''" @click="currentClick(2)">
                {{ $t('echat2') }}
              </el-button>
            </div>
            <div class="pc-ui">
              <div class="ustandard-head">
                <div class="ustandard-head__hd">
                  <el-popover
                    placement="top-start"
                    v-model="popover"
                    popper-class="custom-popper2"
                    width="425"
                    trigger="hover"
                  >
                    <div class="popper2-content">
                      <div class="popper2-search">
                        <el-input
                          :placeholder="$t('trplaceholder')"
                          prefix-icon="el-icon-search"
                          @input="search"
                          v-model="keyword"
                        >
                        </el-input>
                      </div>
                      <div class="popper2-tab">
                        <el-tabs v-model="activeName4">
                          <el-tab-pane v-if="isLogin == true" :label="$t('mTab')" name="first">
                            <div class="popper2-table" :class="{ right: tableData4.length < 8 }">
                              <el-table :data="tableData4" @row-click="rowClick" style="width: 100%" height="220">
                                <el-table-column prop="symbol" :label="$t('czTxt2')" width="150">
                                  <template slot-scope="scope">
                                    <el-button
                                      type="text"
                                      class="favor-btn"
                                      @click.stop="delFavor(scope.row, scope.$index)"
                                      v-if="scope.row.isFavor"
                                    >
                                      <span class="iconfont icon-star"></span>
                                    </el-button>
                                    <span>{{ scope.row.symbol }}</span>
                                  </template>
                                </el-table-column>
                                <el-table-column prop="close" sortable align="left" :label="$t('mth1')">
                                </el-table-column>
                                <el-table-column prop="chg" sortable align="right" :label="$t('mth2')">
                                  >
                                  <template slot-scope="scope">
                                    <span class="text-green" v-if="scope.row.chg > 0">+{{ scope.row.chg }}%</span>
                                    <span class="text-green" v-if="scope.row.chg == 0">{{ scope.row.chg }}%</span>
                                    <span class="text-red" v-if="scope.row.chg < 0">{{ scope.row.chg }}%</span>
                                  </template>
                                </el-table-column>
                              </el-table>
                            </div>
                          </el-tab-pane>
                          <el-tab-pane label="USDT" name="second">
                            <div class="popper2-table" :class="{ right: thumb.data1.length < 8 }">
                              <el-table :data="thumb.data1" @row-click="rowClick" style="width: 100%" height="220">
                                <el-table-column prop="symbol" :label="$t('czTxt2')" width="150">
                                  <template slot-scope="scope">
                                    <el-button
                                      type="text"
                                      class="favor-btn"
                                      @click.stop="delFavor(scope.row, scope.$index)"
                                      v-if="scope.row.isFavor"
                                    >
                                      <span class="iconfont icon-star"></span>
                                    </el-button>
                                    <el-button @click.stop="addFavor(scope.row)" type="text" class="favor-btn" v-else>
                                      <span class="iconfont icon-star_off"></span>
                                    </el-button>
                                    <span>{{ scope.row.symbol }}</span>
                                  </template>
                                </el-table-column>
                                <el-table-column prop="close" sortable align="left" :label="$t('mth1')">
                                </el-table-column>
                                <el-table-column prop="chg" sortable align="right" :label="$t('mth2')">
                                  <template slot-scope="scope">
                                    <span class="text-green" v-if="scope.row.chg > 0">+{{ scope.row.chg }}%</span>
                                    <span class="text-green" v-if="scope.row.chg == 0">{{ scope.row.chg }}%</span>
                                    <span class="text-red" v-if="scope.row.chg < 0">{{ scope.row.chg }}%</span>
                                  </template>
                                </el-table-column>
                              </el-table>
                            </div>
                          </el-tab-pane>
                          <el-tab-pane label="BTC" name="third">
                            <div class="popper2-table" :class="{ right: thumb.data2.length < 8 }">
                              <el-table :data="thumb.data2" @row-click="rowClick" style="width: 100%" height="220">
                                <el-table-column prop="symbol" :label="$t('czTxt2')" width="150">
                                  <template slot-scope="scope">
                                    <el-button
                                      type="text"
                                      class="favor-btn"
                                      @click.stop="delFavor(scope.row, scope.$index)"
                                      v-if="scope.row.isFavor"
                                    >
                                      <span class="iconfont icon-star"></span>
                                    </el-button>
                                    <el-button @click.stop="addFavor(scope.row)" type="text" class="favor-btn" v-else>
                                      <span class="iconfont icon-star_off"></span>
                                    </el-button>
                                    <span>{{ scope.row.symbol }}</span>
                                  </template>
                                </el-table-column>
                                <el-table-column prop="close" sortable align="left" :label="$t('mth1')">
                                </el-table-column>
                                <el-table-column prop="chg" sortable align="right" :label="$t('mth2')">
                                  <template slot-scope="scope">
                                    <span class="text-green" v-if="scope.row.chg > 0">+{{ scope.row.chg }}%</span>
                                    <span class="text-green" v-if="scope.row.chg == 0">{{ scope.row.chg }}%</span>
                                    <span class="text-red" v-if="scope.row.chg < 0">{{ scope.row.chg }}%</span>
                                  </template>
                                </el-table-column>
                              </el-table>
                            </div>
                          </el-tab-pane>
                          <el-tab-pane label="ETH" name="fourth">
                            <div class="popper2-table" :class="{ right: thumb.data3.length < 8 }">
                              <el-table :data="thumb.data3" @row-click="rowClick" style="width: 100%" height="220">
                                <el-table-column prop="symbol" :label="$t('czTxt2')" width="150">
                                  <template slot-scope="scope">
                                    <el-button
                                      type="text"
                                      class="favor-btn"
                                      @click.stop="delFavor(scope.row, scope.$index)"
                                      v-if="scope.row.isFavor"
                                    >
                                      <span class="iconfont icon-star"></span>
                                    </el-button>
                                    <el-button @click.stop="addFavor(scope.row)" type="text" class="favor-btn" v-else>
                                      <span class="iconfont icon-star_off"></span>
                                    </el-button>
                                    <span>{{ scope.row.symbol }}</span>
                                  </template>
                                </el-table-column>
                                <el-table-column prop="close" sortable align="left" :label="$t('mth1')">
                                </el-table-column>
                                <el-table-column prop="chg" sortable align="right" :label="$t('mth2')">
                                  <template slot-scope="scope">
                                    <span class="text-green" v-if="scope.row.chg > 0">+{{ scope.row.chg }}%</span>
                                    <span class="text-green" v-if="scope.row.chg == 0">{{ scope.row.chg }}%</span>
                                    <span class="text-red" v-if="scope.row.chg < 0">{{ scope.row.chg }}%</span>
                                  </template>
                                </el-table-column>
                              </el-table>
                            </div>
                          </el-tab-pane>
                        </el-tabs>
                      </div>
                    </div>
                    <el-button slot="reference" class="popper-btn">
                      <div class="el-dropdown-select">
                        <div class="el-dropdown-value flex_bd">{{ symbol }}</div>
                        <i class="el-icon-caret-bottom el-icon--right"></i>
                      </div>
                    </el-button>
                  </el-popover>
                </div>
                <div class="u-amount">
                  <div class="u-amount-top" :class="{ buy: currentCoin.change > 0, sell: currentCoin.change < 0 }">
                    {{ currentCoin.close | toFixed(baseCoinScale) }}
                  </div>
                </div>
                <div class="ustandard-head__bd">
                  <div class="u-item">
                    <div class="u-item__label">{{ $t('mth2') }}</div>
                    <div class="u-item__value" :class="{ buy: currentCoin.change > 0, sell: currentCoin.change < 0 }">
                      {{ currentCoin.rose }}
                    </div>
                  </div>
                  <div class="u-item">
                    <div class="u-item__label">{{ $t('mTxt') }}</div>
                    <div class="u-item__value">{{ currentCoin.high | toFixed(baseCoinScale) }}</div>
                  </div>
                  <div class="u-item">
                    <div class="u-item__label">{{ $t('mTxt2') }}</div>
                    <div class="u-item__value">{{ currentCoin.low | toFixed(baseCoinScale) }}</div>
                  </div>
                  <div class="u-item">
                    <div class="u-item__label">{{ $t('mTxt3') }}</div>
                    <div class="u-item__value">{{ currentCoin.volume }} {{ currentCoin.coin }}</div>
                  </div>
                </div>
              </div>
            </div>
            <div class="u4-panel" v-show="current == 1">
              <div class="panel-body">
                <div class="u4-box" style="padding-left:25px">
                  <div class="u4-line" style="height:574px;">
                    <TV :symbol="symbol" ref="tv"></TV>
                  </div>
                </div>
              </div>
            </div>
            <div class="u4-panel" v-show="current == 2">
              <div class="depth-group flex">
                <div class="depth flex_bd" id="depth" v-if="current == 2"></div>
                <div class="depth flex_bd" id="depth2" v-if="current == 2"></div>
              </div>
            </div>
          </div>
          <div class="ustandard-col2 " :class="{ show: smCurrent == 2 }">
            <div class="u1-panel">
              <el-tabs v-model="activeName5" @tab-click="handleClick">
                <el-tab-pane name="all">
                  <span slot="label">
                    <img src="../../assets/photo/web/block1.png" alt="" />
                  </span>
                  <div class="ui-row clearfix">
                    <div class="ui-table">
                      <div class="ui-table-head">
                        <div class="ui-table-tr">
                          <div class="ui-table-th col1">{{ $t('wth3') }}(USDT)</div>
                          <div class="ui-table-th col2">{{ $t('wtth4') }}({{ currentCoin.coin }})</div>
                          <div class="ui-table-th col3">{{ $t('total') }}(USDT)</div>
                        </div>
                      </div>
                      <div class="ui-table-body">
                        <div class="ui-table-up ui-table-height">
                          <div class="ui-table-cell" v-for="(item, index) in plate.askRows" :key="index">
                            <div class="ui-table-tr">
                              <div class="ui-table-td">
                                {{ item.price | toFixed(baseCoinScale) }}
                              </div>
                              <div class="ui-table-td">
                                {{ item.amount | toFixed(coinScale) }}
                              </div>
                              <div class="ui-table-td">
                                {{ item.totalAmount | toFixed(coinScale) }}
                              </div>
                            </div>
                            <div
                              class="ui-table-progress"
                              :style="{ width: (item.totalAmount / plate.askTotle).toFixed(4) * 100 + '%' }"
                            ></div>
                          </div>
                        </div>
                        <div
                          class="ui-table-subtitle"
                          :class="{ buy: currentCoin.change > 0, sell: currentCoin.change < 0 }"
                        >
                          {{ currentCoin.close }}
                          <i class="el-icon-top" v-if="currentCoin.change > 0"></i>
                          <i class="el-icon-bottom" v-else></i>
                        </div>
                      </div>
                    </div>
                    <div class="ui-table ui-table2">
                      <div class="ui-table-head">
                        <div class="ui-table-tr">
                          <div class="ui-table-th col1">{{ $t('wth3') }}(USDT)</div>
                          <div class="ui-table-th col2">{{ $t('wtth4') }}({{ currentCoin.coin }})</div>
                          <div class="ui-table-th col3">{{ $t('total') }}(USDT)</div>
                        </div>
                      </div>
                      <div class="ui-table-body">
                        <div class="ui-table-down ui-table-height">
                          <div class="ui-table-cell" v-for="(item, index) in plate.bidRows" :key="index">
                            <div class="ui-table-tr">
                              <div class="ui-table-td">
                                {{ item.price | toFixed(baseCoinScale) }}
                              </div>
                              <div class="ui-table-td">
                                {{ item.amount | toFixed(coinScale) }}
                              </div>
                              <div class="ui-table-td">
                                {{ item.totalAmount | toFixed(coinScale) }}
                              </div>
                            </div>
                            <div
                              class="ui-table-progress"
                              :style="{ width: (item.totalAmount / plate.bidTotle).toFixed(4) * 100 + '%' }"
                            ></div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </el-tab-pane>
                <el-tab-pane name="buy">
                  <span slot="label">
                    <img src="../../assets/photo/web/block2.png" alt="" />
                  </span>
                  <div class="ui-table">
                    <div class="ui-table-head">
                      <div class="ui-table-tr">
                        <div class="ui-table-th col1">{{ $t('wth3') }}(USDT)</div>
                        <div class="ui-table-th col2">{{ $t('wtth4') }}({{ currentCoin.coin }})</div>
                        <div class="ui-table-th col3">{{ $t('total') }}(USDT)</div>
                      </div>
                    </div>
                    <div class="ui-table-body">
                      <div
                        class="ui-table-subtitle"
                        :class="{ buy: currentCoin.change > 0, sell: currentCoin.change < 0 }"
                      >
                        {{ currentCoin.close }}
                        <i class="el-icon-top" v-if="currentCoin.change > 0"></i>
                        <i class="el-icon-bottom" v-else></i>
                      </div>
                      <div class="ui-table-down">
                        <div class="ui-table-cell" v-for="(item, index) in plate.bidRows" :key="index">
                          <div class="ui-table-tr">
                            <div class="ui-table-td">
                              {{ item.price | toFixed(baseCoinScale) }}
                            </div>
                            <div class="ui-table-td">
                              {{ item.amount | toFixed(coinScale) }}
                            </div>
                            <div class="ui-table-td">
                              {{ item.totalAmount | toFixed(coinScale) }}
                            </div>
                          </div>
                          <div
                            class="ui-table-progress"
                            :style="{ width: (item.totalAmount / plate.bidTotle).toFixed(4) * 100 + '%' }"
                          ></div>
                        </div>
                      </div>
                    </div>
                  </div>
                </el-tab-pane>
                <el-tab-pane name="sell">
                  <span slot="label">
                    <img src="../../assets/photo/web/block3.png" alt="" />
                  </span>
                  <div class="ui-table">
                    <div class="ui-table-head">
                      <div class="ui-table-tr">
                        <div class="ui-table-th col1">{{ $t('wth3') }}(USDT)</div>
                        <div class="ui-table-th col2">{{ $t('wtth4') }}({{ currentCoin.coin }})</div>
                        <div class="ui-table-th col3">{{ $t('total') }}(USDT)</div>
                      </div>
                    </div>
                    <div class="ui-table-body">
                      <div class="ui-table-up">
                        <div class="ui-table-cell" v-for="(item, index) in plate.askRows" :key="index">
                          <div class="ui-table-tr">
                            <div class="ui-table-td">
                              {{ item.price | toFixed(baseCoinScale) }}
                            </div>
                            <div class="ui-table-td">
                              {{ item.amount | toFixed(coinScale) }}
                            </div>
                            <div class="ui-table-td">
                              {{ item.totalAmount | toFixed(coinScale) }}
                            </div>
                          </div>
                          <div
                            class="ui-table-progress"
                            :style="{ width: (item.totalAmount / plate.askTotle).toFixed(4) * 100 + '%' }"
                          ></div>
                        </div>
                      </div>
                      <div
                        class="ui-table-subtitle"
                        :class="{ buy: currentCoin.change > 0, sell: currentCoin.change < 0 }"
                      >
                        {{ currentCoin.close }}
                        <i class="el-icon-top" v-if="currentCoin.change > 0"></i>
                        <i class="el-icon-bottom" v-else></i>
                      </div>
                    </div>
                  </div>
                </el-tab-pane>
              </el-tabs>
            </div>
          </div>
          <div class="ustandard-col3 " :class="{ show: smCurrent == 3 }">
            <div class="ustandard-table">
              <el-table :data="tableData" style="width: 100%">
                <el-table-column prop="price" :label="$t('wth3') + '(USDT)'" width="95">
                  <template slot-scope="scope">
                    <span v-if="scope.row.direction == 'BUY'" class="text-green">{{ scope.row.price }}</span>
                    <span v-if="scope.row.direction == 'SELL'" class="text-red">{{ scope.row.price }}</span>
                  </template>
                </el-table-column>
                <el-table-column
                  prop="amount"
                  :label="$t('wtth4') + '(' + currentCoin.coin + ')'"
                  width="105"
                  align="right"
                >
                </el-table-column>
                <el-table-column prop="time" :label="$t('wth1')" align="right">
                  <template slot-scope="scope">
                    <span>{{ scope.row.time | datefmt('HH:mm:ss') }}</span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
        <div class="ustandard-row">
          <div class="ustandard-col1 flex_bd">
            <div class="u3-panel" style="width: 100%;overflow: hidden;">
              <div class="u2-tab">
                <el-tabs v-model="activeName3">
                  <el-tab-pane :label="$t('wTxt2')" name="first">
                    <div class="u3-table">
                      <el-table :data="order.content" style="width: 100%" height="270px">
                        <el-table-column prop="time" :label="$t('wth1')">
                          <template slot-scope="scope">
                            <span>{{ scope.row.time | datefmt('YYYY-MM-DD HH:mm:ss') }}</span>
                          </template>
                        </el-table-column>
                        <el-table-column prop="symbol" :label="$t('wTxt3')"> </el-table-column>
                        <el-table-column prop="type" :label="$t('wtth2')">
                          <template slot-scope="scope">
                            <span v-if="scope.row.type == 'LIMIT_PRICE'">{{ $t('dhTxt2') }}</span>
                            <span v-if="scope.row.type == 'MARKET_PRICE'">{{ $t('trTxt2') }}</span>
                          </template>
                        </el-table-column>
                        <el-table-column prop="direction" :label="$t('wth2')">
                          <template slot-scope="scope">
                            <span class="text-green" v-if="scope.row.direction == 'BUY'">{{ $t('dhTxt4') }}</span>
                            <span class="text-red" v-if="scope.row.direction == 'SELL'">{{ $t('dhTxt3') }}</span>
                          </template>
                        </el-table-column>
                        <el-table-column prop="price" :label="$t('wth3')"> </el-table-column>
                        <el-table-column prop="amount" :label="$t('wtth4')"> </el-table-column>
                        <el-table-column prop="tradedAmount" :label="$t('wth4')"> </el-table-column>
                        <el-table-column prop="turnover" :label="$t('wth5')"> </el-table-column>
                        <el-table-column align="center" :label="$t('th5')">
                          <template slot-scope="scope">
                            <div class="turnover-btns">
                              <el-button type="text" @click="handleCancel(scope.row.orderId)">
                                {{ $t('wbtn') }}
                              </el-button>
                            </div>
                          </template>
                        </el-table-column>
                      </el-table>
                    </div>
                    <el-dialog
                      :visible.sync="dialogVisible"
                      :show-close="false"
                      width="380px"
                      custom-class="custom-dialog custom-dialog-tip"
                    >
                      <div slot="title" class="dialog-title">
                        <b>{{ $t('wtip') }}</b>
                      </div>
                      <div class="dialog-content">
                        <div class="dialog-text">{{ $t('wtiptxt') }}？</div>
                      </div>
                      <div slot="footer" class="dialog-footer">
                        <el-row :gutter="14">
                          <el-col :span="12">
                            <el-button @click="dialogVisible = false">{{ $t('cancel') }} </el-button>
                          </el-col>
                          <el-col :span="12">
                            <el-button type="warning" @click="orderConfirm">
                              {{ $t('confirm2') }}
                            </el-button>
                          </el-col>
                        </el-row>
                      </div>
                    </el-dialog>
                  </el-tab-pane>
                  <el-tab-pane :label="$t('hisTxt')" name="second">
                    <div class="u3-table">
                      <el-table :data="order2.content" style="width: 100%" height="270px">
                        <el-table-column prop="time" :label="$t('wth1')">
                          <template slot-scope="scope">
                            <span>{{ scope.row.time | datefmt('YYYY-MM-DD HH:mm:ss') }}</span>
                          </template>
                        </el-table-column>
                        <el-table-column prop="symbol" :label="$t('wTxt3')"> </el-table-column>
                        <el-table-column prop="type" :label="$t('wtth2')">
                          <template slot-scope="scope">
                            <span v-if="scope.row.type == 'LIMIT_PRICE'">{{ $t('dhTxt2') }}</span>
                            <span v-if="scope.row.type == 'MARKET_PRICE'">{{ $t('trTxt2') }}</span>
                          </template>
                        </el-table-column>
                        <el-table-column prop="direction" :label="$t('wth2')">
                          <template slot-scope="scope">
                            <span class="text-green" v-if="scope.row.direction == 'BUY'">{{ $t('dhTxt4') }}</span>
                            <span class="text-red" v-if="scope.row.direction == 'SELL'">{{ $t('dhTxt3') }}</span>
                          </template>
                        </el-table-column>
                        <el-table-column prop="price" :label="$t('wth3')"> </el-table-column>
                        <el-table-column prop="amount" :label="$t('wtth4')"> </el-table-column>
                        <el-table-column prop="tradedAmount" :label="$t('wth4')"> </el-table-column>
                        <el-table-column prop="turnover" :label="$t('wth5')"> </el-table-column>
                        <el-table-column align="center" :label="$t('th5')">
                          <template>
                            <div class="turnover-btns">
                              <div class="text-orange">
                                {{ $t('labTab5') }}
                              </div>
                            </div>
                          </template>
                        </el-table-column>
                      </el-table>
                    </div>
                  </el-tab-pane>
                </el-tabs>
              </div>
            </div>
          </div>

          <div class="ustandard-col4" style="background-color: var(--range-block);">
            <div class="u2-panel">
              <div class="u2-content u2-tab">
                <el-tabs v-model="activeName2">
                  <el-tab-pane :label="$t('dhTxt2')" name="first">
                    <div class="u2-body u2-row" style="padding-bottom: 0px;">
                      <el-row :gutter="20">
                        <el-col :xs="12" :sm="12">
                          <div class="u2-value">
                            <div class="u2-balance">
                              {{ $t('redTxt5') }}
                              <span>{{ wallet.base | toFloor(baseCoinScale) }}</span>
                              {{ currentCoin.base }}
                            </div>
                            <div class="u2-btns">
                              <el-button type="text" @click="recharge(currentCoin.base)">
                                {{ $t('btn4') }}
                              </el-button>
                            </div>
                          </div>
                          <el-form :model="form" ref="form" class="u2-form" style="margin-bottom: 0px;">
                            <el-form-item>
                              <div class="u2-form-item">
                                <div class="u2-form-item__hd">
                                  {{ $t('wth3') }}
                                </div>
                                <div class="u2-form-item__bd flex_bd">
                                  <el-input
                                    type="number"
                                    :placeholder="$t('trplaceholder3')"
                                    v-model.number="form.buy.limitPrice"
                                    @input="buyLimitChange"
                                    :min="0"
                                  >
                                  </el-input>
                                </div>
                                <div class="u2-form-item__ft">
                                  {{ currentCoin.base }}
                                </div>
                              </div>
                            </el-form-item>
                            <el-form-item>
                              <div class="u2-form-item">
                                <div class="u2-form-item__hd">
                                  {{ $t('wtth4') }}
                                </div>
                                <div class="u2-form-item__bd flex_bd">
                                  <el-input type="number" v-model.number="form.buy.limitAmount" :min="0"> </el-input>
                                </div>
                                <div class="u2-form-item__ft">
                                  {{ currentCoin.coin }}
                                </div>
                              </div>
                            </el-form-item>
                            <el-form-item>
                              <div class="u2-step">
                                <div class="u2-step-inner">
                                  <el-slider
                                    v-model="form.buy.sliderBuyLimitPercent"
                                    :marks="marks"
                                    :step="25"
                                    show-stops
                                    :show-tooltip="false"
                                    :disabled="sliderBuyDisabled"
                                  >
                                  </el-slider>
                                </div>
                              </div>
                            </el-form-item>
                            <el-form-item>
                              <div class="u2-form">
                                <div class="u2-form-item">
                                  <div class="u2-form-item__hd">
                                    {{ $t('trTxt') }}
                                  </div>
                                  <div class="u2-form-item__bd flex_bd">
                                    <el-input-number
                                      v-model="form.buy.limitTurnover"
                                      :controls="false"
                                      :precision="2"
                                      :disabled="true"
                                    >
                                    </el-input-number>
                                  </div>
                                  <div class="u2-form-item__ft">
                                    {{ currentCoin.base }}
                                  </div>
                                </div>
                              </div>
                            </el-form-item>
                            <el-form-item>
                              <div class="u2-foot">
                                <el-button class="el-button--light" @click="buyLimit">
                                  {{ $t('dhTxt4') }}{{ currentCoin.coin }}
                                </el-button>
                              </div>
                            </el-form-item>
                          </el-form>
                        </el-col>
                        <el-col :xs="12" :sm="12">
                          <div class="u2-value">
                            <div class="u2-balance">
                              {{ $t('redTxt5') }}
                              <span>{{ wallet.coin | toFloor(coinScale) }}</span
                              >{{ currentCoin.coin }}
                            </div>
                            <div class="u2-btns">
                              <el-button type="text" @click="recharge(currentCoin.coin)">
                                {{ $t('btn4') }}
                              </el-button>
                            </div>
                          </div>
                          <el-form :model="form" ref="form" class="u2-form" style="margin-bottom: 0px;">
                            <el-form-item>
                              <div class="u2-form-item">
                                <div class="u2-form-item__hd">
                                  {{ $t('wth3') }}
                                </div>
                                <div class="u2-form-item__bd flex_bd">
                                  <el-input
                                    type="number"
                                    :placeholder="$t('trplaceholder4')"
                                    v-model.number="form.sell.limitPrice"
                                    @input="sellLimitChange"
                                    :min="0"
                                  >
                                  </el-input>
                                  <!-- <el-input-number v-model="form.sell.limitPrice"
																		@change="sellLimitChange"
																		:placeholder="$t('trplaceholder4')"
																		:controls="false" :precision="2">
																	</el-input-number> -->
                                </div>
                                <div class="u2-form-item__ft">
                                  {{ currentCoin.base }}
                                </div>
                              </div>
                            </el-form-item>
                            <el-form-item>
                              <div class="u2-form-item">
                                <div class="u2-form-item__hd">
                                  {{ $t('wtth4') }}
                                </div>
                                <div class="u2-form-item__bd flex_bd">
                                  <el-input
                                    type="number"
                                    v-model.number="form.sell.limitAmount"
                                    @input="sellLimitAmount"
                                    :min="0"
                                  >
                                  </el-input>
                                </div>
                                <div class="u2-form-item__ft">
                                  {{ currentCoin.coin }}
                                </div>
                              </div>
                            </el-form-item>
                            <el-form-item>
                              <div class="u2-step">
                                <div class="u2-step-inner">
                                  <el-slider
                                    v-model="form.sell.sliderSellLimitPercent"
                                    :marks="marks"
                                    :step="25"
                                    show-stops
                                    :show-tooltip="false"
                                    :disabled="sliderSellDisabled"
                                  >
                                  </el-slider>
                                </div>
                              </div>
                            </el-form-item>
                            <el-form-item>
                              <div class="u2-form">
                                <div class="u2-form-item">
                                  <div class="u2-form-item__hd">
                                    {{ $t('trTxt') }}
                                  </div>
                                  <div class="u2-form-item__bd flex_bd">
                                    <el-input-number
                                      v-model="form.sell.limitTurnover"
                                      :controls="false"
                                      :precision="2"
                                      :disabled="true"
                                    >
                                    </el-input-number>
                                  </div>
                                  <div class="u2-form-item__ft">
                                    {{ currentCoin.base }}
                                  </div>
                                </div>
                              </div>
                            </el-form-item>
                            <el-form-item>
                              <div class="u2-foot">
                                <el-button type="danger" @click="sellLimit">
                                  {{ $t('dhTxt3') }}{{ currentCoin.coin }}
                                </el-button>
                              </div>
                            </el-form-item>
                          </el-form>
                        </el-col>
                      </el-row>
                    </div>
                  </el-tab-pane>
                  <el-tab-pane :label="$t('trTxt2')" name="second">
                    <div class="u2-body u2-row">
                      <el-row :gutter="20">
                        <el-col :xs="12" :sm="12">
                          <div class="u2-value">
                            <div class="u2-balance">
                              {{ $t('redTxt5') }}
                              <span>{{ wallet.base | toFloor(baseCoinScale) }}</span>
                              {{ currentCoin.base }}
                            </div>
                            <div class="u2-btns">
                              <el-button type="text" @click="recharge(currentCoin.base)">
                                {{ $t('btn4') }}
                              </el-button>
                            </div>
                          </div>
                          <div class="u2-form">
                            <div class="u2-form-item">
                              <div class="u2-form-item__hd">
                                {{ $t('wth3') }}
                              </div>
                              <div class="u2-form-item__bd flex_bd">
                                <el-input
                                  v-model="form.buy.marketPrice"
                                  :placeholder="$t('trplaceholder3')"
                                  class="placeholder-right"
                                  readonly
                                >
                                </el-input>
                              </div>
                              <div class="u2-form-item__ft">
                                {{ currentCoin.base }}
                              </div>
                            </div>
                            <div class="u2-form-item">
                              <div class="u2-form-item__hd">
                                {{ $t('mTxt13') }}
                              </div>
                              <div class="u2-form-item__bd flex_bd">
                                <el-input type="number" :min="0" v-model.number="form.buy.marketAmount"> </el-input>
                              </div>
                              <div class="u2-form-item__ft">
                                {{ currentCoin.base }}
                              </div>
                            </div>
                          </div>
                          <div class="u2-step">
                            <div class="u2-step-inner">
                              <el-slider
                                v-model="form.buy.sliderBuyMarketPercent"
                                :marks="marks"
                                :step="25"
                                show-stops
                                :show-tooltip="false"
                                :disabled="sliderBuyDisabled"
                              >
                              </el-slider>
                            </div>
                          </div>
                          <div class="u2-foot">
                            <el-button class="el-button--light" @click="buyMarket">
                              {{ $t('dhTxt4') }}{{ currentCoin.coin }}
                            </el-button>
                          </div>
                        </el-col>
                        <el-col :xs="12" :sm="12">
                          <div class="u2-value">
                            <div class="u2-balance">
                              {{ $t('redTxt5') }}
                              <span>{{ wallet.coin | toFloor(coinScale) }}</span>
                              {{ currentCoin.coin }}
                            </div>
                            <div class="u2-btns">
                              <el-button type="text" @click="recharge(currentCoin.coin)">
                                {{ $t('btn4') }}
                              </el-button>
                            </div>
                          </div>
                          <div class="u2-form">
                            <div class="u2-form-item">
                              <div class="u2-form-item__hd">
                                {{ $t('wth3') }}
                              </div>
                              <div class="u2-form-item__bd flex_bd">
                                <el-input
                                  v-model="form.sell.marketPrice"
                                  :placeholder="$t('trplaceholder4')"
                                  class="placeholder-right"
                                  readonly
                                >
                                </el-input>
                              </div>
                              <div class="u2-form-item__ft">
                                {{ currentCoin.base }}
                              </div>
                            </div>
                            <div class="u2-form-item">
                              <div class="u2-form-item__hd">
                                {{ $t('trTxt3') }}
                              </div>
                              <div class="u2-form-item__bd flex_bd">
                                <el-input type="number" :min="0" v-model.number="form.sell.marketAmount"> </el-input>
                              </div>
                              <div class="u2-form-item__ft">
                                {{ currentCoin.coin }}
                              </div>
                            </div>
                          </div>
                          <div class="u2-step">
                            <div class="u2-step-inner">
                              <el-slider
                                v-model="form.sell.sliderSellMarketPercent"
                                :marks="marks"
                                :step="25"
                                show-stops
                                :show-tooltip="false"
                                :disabled="sliderSellDisabled"
                              >
                              </el-slider>
                            </div>
                          </div>
                          <div class="u2-foot">
                            <el-button type="danger" @click="sellMarket">
                              {{ $t('dhTxt3') }}{{ currentCoin.coin }}
                            </el-button>
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                  </el-tab-pane>
                </el-tabs>
                <div class="u2-content-mask" v-show="!isLogin">
                  <div class="u2-isLogin">
                    {{ $t('please') }}
                    <router-link to="/login">{{ $t('meun8') }}</router-link
                    >/<router-link to="/register">
                      {{ $t('meun9') }}
                    </router-link>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="mb-foot-height">
          <div class="mb-foot">
            <el-row :gutter="15">
              <el-col :span="12">
                <div class="u2-foot">
                  <el-button class="el-button--light" @click="drawer = true">
                    {{ $t('dhTxt4') }}{{ currentCoin.coin }}
                  </el-button>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="u2-foot">
                  <el-button type="danger" @click="drawer = true"> {{ $t('dhTxt3') }}{{ currentCoin.coin }} </el-button>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
        <el-drawer :visible.sync="drawer" direction="btt" size="64%">
          <div class="u2-panel" style="background-color: unset;">
            <div class="u2-content u2-tab">
              <el-tabs v-model="activeName2">
                <el-tab-pane :label="$t('dhTxt2')" name="first">
                  <div class="u2-body u2-row" style="padding-bottom: 0px;">
                    <el-row :gutter="20">
                      <el-col :xs="12" :sm="12">
                        <div class="u2-value">
                          <div class="u2-balance">
                            {{ $t('redTxt5') }}
                            <span>{{ wallet.base | toFloor(baseCoinScale) }}</span>
                            {{ currentCoin.base }}
                          </div>
                          <div class="u2-btns">
                            <el-button type="text" @click="recharge(currentCoin.base)">
                              {{ $t('btn4') }}
                            </el-button>
                          </div>
                        </div>
                        <el-form :model="form" ref="form" class="u2-form" style="margin-bottom: 0px;">
                          <el-form-item>
                            <div class="u2-form-item">
                              <div class="u2-form-item__hd">
                                {{ $t('wth3') }}
                              </div>
                              <div class="u2-form-item__bd flex_bd">
                                <el-input
                                  type="number"
                                  :min="0"
                                  :placeholder="$t('trplaceholder3')"
                                  v-model.number="form.buy.limitPrice"
                                  @input="buyLimitChange"
                                >
                                </el-input>
                              </div>
                              <div class="u2-form-item__ft">
                                {{ currentCoin.base }}
                              </div>
                            </div>
                          </el-form-item>
                          <el-form-item>
                            <div class="u2-form-item">
                              <div class="u2-form-item__hd">
                                {{ $t('wtth4') }}
                              </div>
                              <div class="u2-form-item__bd flex_bd">
                                <el-input type="number" :min="0" v-model.number="form.buy.limitAmount"> </el-input>
                              </div>
                              <div class="u2-form-item__ft">
                                {{ currentCoin.coin }}
                              </div>
                            </div>
                          </el-form-item>
                          <el-form-item>
                            <div class="u2-step">
                              <div class="u2-step-inner">
                                <el-slider
                                  v-model="form.buy.sliderBuyLimitPercent"
                                  :marks="marks"
                                  :step="25"
                                  show-stops
                                  :show-tooltip="false"
                                  :disabled="sliderBuyDisabled"
                                >
                                </el-slider>
                              </div>
                            </div>
                          </el-form-item>
                          <el-form-item>
                            <div class="u2-form">
                              <div class="u2-form-item">
                                <div class="u2-form-item__hd">
                                  {{ $t('trTxt') }}
                                </div>
                                <div class="u2-form-item__bd flex_bd">
                                  <el-input-number
                                    v-model="form.buy.limitTurnover"
                                    :controls="false"
                                    :precision="2"
                                    :disabled="true"
                                  >
                                  </el-input-number>
                                </div>
                                <div class="u2-form-item__ft">
                                  {{ currentCoin.base }}
                                </div>
                              </div>
                            </div>
                          </el-form-item>
                          <el-form-item>
                            <div class="u2-foot">
                              <el-button class="el-button--light" @click="buyLimit">
                                {{ $t('dhTxt4') }}{{ currentCoin.coin }}
                              </el-button>
                            </div>
                          </el-form-item>
                        </el-form>
                      </el-col>
                      <el-col :xs="12" :sm="12">
                        <div class="u2-value">
                          <div class="u2-balance">
                            {{ $t('redTxt5') }}
                            <span>{{ wallet.coin | toFloor(coinScale) }}</span
                            >{{ currentCoin.coin }}
                          </div>
                          <div class="u2-btns">
                            <el-button type="text" @click="recharge(currentCoin.coin)">
                              {{ $t('btn4') }}
                            </el-button>
                          </div>
                        </div>
                        <el-form :model="form" ref="form" class="u2-form" style="margin-bottom: 0px;">
                          <el-form-item>
                            <div class="u2-form-item">
                              <div class="u2-form-item__hd">
                                {{ $t('wth3') }}
                              </div>
                              <div class="u2-form-item__bd flex_bd">
                                <el-input
                                  type="number"
                                  :min="0"
                                  :placeholder="$t('trplaceholder4')"
                                  v-model.number="form.sell.limitPrice"
                                  @input="sellLimitChange"
                                >
                                </el-input>
                              </div>
                              <div class="u2-form-item__ft">
                                {{ currentCoin.base }}
                              </div>
                            </div>
                          </el-form-item>
                          <el-form-item>
                            <div class="u2-form-item">
                              <div class="u2-form-item__hd">
                                {{ $t('wtth4') }}
                              </div>
                              <div class="u2-form-item__bd flex_bd">
                                <el-input type="number" :min="0" v-model.number="form.sell.limitAmount"> </el-input>
                              </div>
                              <div class="u2-form-item__ft">
                                {{ currentCoin.coin }}
                              </div>
                            </div>
                          </el-form-item>
                          <el-form-item>
                            <div class="u2-step">
                              <div class="u2-step-inner">
                                <el-slider
                                  v-model="form.sell.sliderSellLimitPercent"
                                  :marks="marks"
                                  :step="25"
                                  show-stops
                                  :show-tooltip="false"
                                  :disabled="sliderSellDisabled"
                                >
                                </el-slider>
                              </div>
                            </div>
                          </el-form-item>
                          <el-form-item>
                            <div class="u2-form">
                              <div class="u2-form-item">
                                <div class="u2-form-item__hd">
                                  {{ $t('trTxt') }}
                                </div>
                                <div class="u2-form-item__bd flex_bd">
                                  <el-input-number
                                    v-model="form.sell.limitTurnover"
                                    :controls="false"
                                    :precision="2"
                                    :disabled="true"
                                  >
                                  </el-input-number>
                                </div>
                                <div class="u2-form-item__ft">
                                  {{ currentCoin.base }}
                                </div>
                              </div>
                            </div>
                          </el-form-item>
                          <el-form-item>
                            <div class="u2-foot">
                              <el-button type="danger" @click="sellLimit">
                                {{ $t('dhTxt3') }}{{ currentCoin.coin }}
                              </el-button>
                            </div>
                          </el-form-item>
                        </el-form>
                      </el-col>
                    </el-row>
                  </div>
                </el-tab-pane>
                <el-tab-pane :label="$t('trTxt2')" name="second">
                  <div class="u2-body u2-row">
                    <el-row :gutter="20">
                      <el-col :xs="12" :sm="12">
                        <div class="u2-value">
                          <div class="u2-balance">
                            {{ $t('redTxt5') }}
                            <span>{{ wallet.base | toFloor(baseCoinScale) }}</span>
                            {{ currentCoin.base }}
                          </div>
                          <div class="u2-btns">
                            <el-button type="text" @click="recharge(currentCoin.base)">
                              {{ $t('btn4') }}
                            </el-button>
                          </div>
                        </div>
                        <div class="u2-form">
                          <div class="u2-form-item">
                            <div class="u2-form-item__hd">
                              {{ $t('wth3') }}
                            </div>
                            <div class="u2-form-item__bd flex_bd">
                              <el-input :placeholder="$t('trplaceholder3')" class="placeholder-right" readonly>
                              </el-input>
                            </div>
                            <div class="u2-form-item__ft">
                              {{ currentCoin.base }}
                            </div>
                          </div>
                          <div class="u2-form-item">
                            <div class="u2-form-item__hd">
                              {{ $t('mTxt13') }}
                            </div>
                            <div class="u2-form-item__bd flex_bd">
                              <el-input type="number" :min="0" v-model.number="form.buy.marketAmount"> </el-input>
                            </div>
                            <div class="u2-form-item__ft">
                              {{ currentCoin.base }}
                            </div>
                          </div>
                        </div>
                        <div class="u2-step">
                          <div class="u2-step-inner">
                            <el-slider
                              v-model="form.buy.sliderBuyMarketPercent"
                              :disabled="sliderBuyDisabled"
                              :marks="marks"
                              :step="25"
                              show-stops
                              :show-tooltip="false"
                            >
                            </el-slider>
                          </div>
                        </div>
                        <div class="u2-foot">
                          <el-button class="el-button--light" @click="buyMarket">
                            {{ $t('dhTxt4') }}{{ currentCoin.coin }}
                          </el-button>
                        </div>
                      </el-col>
                      <el-col :xs="12" :sm="12">
                        <div class="u2-value">
                          <div class="u2-balance">
                            {{ $t('redTxt5') }}
                            <span>{{ wallet.coin | toFloor(coinScale) }}</span>
                            {{ currentCoin.coin }}
                          </div>
                          <div class="u2-btns">
                            <el-button type="text" @click="recharge(currentCoin.coin)">
                              {{ $t('btn4') }}
                            </el-button>
                          </div>
                        </div>
                        <div class="u2-form">
                          <div class="u2-form-item">
                            <div class="u2-form-item__hd">
                              {{ $t('wth3') }}
                            </div>
                            <div class="u2-form-item__bd flex_bd">
                              <el-input :placeholder="$t('trplaceholder4')" class="placeholder-right" readonly>
                              </el-input>
                            </div>
                            <div class="u2-form-item__ft">
                              {{ currentCoin.base }}
                            </div>
                          </div>
                          <div class="u2-form-item">
                            <div class="u2-form-item__hd">
                              {{ $t('trTxt3') }}
                            </div>
                            <div class="u2-form-item__bd flex_bd">
                              <el-input type="number" :min="0" v-model.number="form.sell.marketAmount"> </el-input>
                            </div>
                            <div class="u2-form-item__ft">
                              {{ currentCoin.coin }}
                            </div>
                          </div>
                        </div>
                        <div class="u2-step">
                          <div class="u2-step-inner">
                            <el-slider
                              v-model="form.sell.sliderSellMarketPercent"
                              :disabled="sliderSellDisabled"
                              :marks="marks"
                              :step="25"
                              show-stops
                              :show-tooltip="false"
                            >
                            </el-slider>
                          </div>
                        </div>
                        <div class="u2-foot">
                          <el-button type="danger" @click="sellMarket">
                            {{ $t('dhTxt3') }}{{ currentCoin.coin }}
                          </el-button>
                        </div>
                      </el-col>
                    </el-row>
                  </div>
                </el-tab-pane>
              </el-tabs>
              <div class="u2-content-mask" v-show="!isLogin">
                <div class="u2-isLogin">
                  {{ $t('please') }}
                  <router-link to="/login">{{ $t('meun8') }}</router-link
                  >/<router-link to="/register">
                    {{ $t('meun9') }}
                  </router-link>
                </div>
              </div>
            </div>
          </div>
        </el-drawer>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import TV from '@/components/tv'
import { symbolInfo, plateMini, plateFull, symbolThumb, latestTrade } from '@/api/api/market'

import {
  favorFind,
  orderCurrent,
  orderHistory,
  orderCancel,
  exchangeAdd,
  favorAdd,
  favorDelete,
} from '@/api/api/exchange'
import { assetWallet } from '@/api/api/user'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'
import Head from '@/components/Head.vue'
export default {
  name: 'Home',
  components: {
    Head,
    TV,
  },
  data() {
    return {
      drawer: false,
      drawer2: false,
      tableData: [],
      form: {
        buy: {
          limitPrice: 0,
          limitAmount: 0,
          marketAmount: 0,
          limitTurnover: 0,
          sliderBuyLimitPercent: 0,
          sliderBuyMarketPercent: 0,
        },
        sell: {
          limitPrice: 0,
          limitAmount: 0,
          marketAmount: 0,
          limitTurnover: 0,
          sliderSellLimitPercent: 0,
          sliderSellMarketPercent: 0,
        },
      },
      activeName2: 'first',
      activeName3: 'first',
      activeName4: 'second',
      activeName5: 'all',
      buy: '',
      sell: '',
      marks: {
        0: '0%',
        25: '25%',
        50: '50%',
        75: '70%',
        100: '100%',
      },
      value3: 0,
      value4: 0,
      symbol: 'BTC/USDT',
      order: {},
      order2: {},
      newPrice: '',
      type: '',
      value1: new Date(),
      radio1: 'Time',
      info: {},
      plate: {
        maxPostion: 10,
        askRows: [],
        bidRows: [],
      },
      currentCoin: {},
      full: {},
      tableData4: [],
      oldTableData4: [],
      thumb: {
        data: [],
        oldData1: [],
        oldData2: [],
        oldData3: [],
        data1: [],
        data2: [],
        data3: [],
      },
      isLogin: false,
      searchVal: '',
      popover: false,
      orderId: '',
      dialogVisible: false,
      coinScale: 6,
      baseCoinScale: 6,
      wallet: {
        base: 0,
        coin: 0,
      },
      current: 1,
      resolution: '5',
      resolution2: '',
      screenWidth: null,
      smCurrent: 1,
    }
  },
  created() {
    this.isLogin = this.$store.state.isLogin
  },
  mounted() {
    this.getInit()
    this.screenWidth = document.body.clientWidth
    window.onresize = () => {
      return () => {
        this.screenWidth = document.body.clientWidth
      }
    }
  },
  computed: {
    keyword: {
      get() {
        return this.searchVal
      },
      set(val) {
        this.searchVal = val.toUpperCase()
      },
    },
    member: function() {
      return this.$store.getters.member
    },
    sliderBuyDisabled() {
      let account = this.wallet.base,
        min = this.toFloor(1 / Math.pow(10, this.baseCoinScale))
      return account < min
    },
    sliderSellDisabled() {
      let account = this.wallet.coin,
        min = this.toFloor(1 / Math.pow(10, this.coinScale))
      return account < min
    },
  },
  watch: {
    'form.buy.limitAmount': function(val) {
      this.form.buy.limitTurnover = this.toFloor(val * this.form.buy.limitPrice, this.baseCoinScale)
    },
    'form.buy.sliderBuyLimitPercent': function(val) {
      let price = this.form.buy.limitPrice,
        account = this.wallet.base,
        amount = 0
      if (price > 0) {
        amount = this.toFloor(((account / price) * val) / 100, this.coinScale)
      }
      this.form.buy.limitAmount = amount
    },
    'form.sell.sliderSellLimitPercent': function(val) {
      let account = this.wallet.coin
      let price = this.form.sell.limitPrice

      this.form.sell.limitAmount = this.toFloor((account * val) / 100, this.coinScale)
      this.form.sell.limitTurnover = this.toFloor(price * ((account * val) / 100), this.coinScale)
    },
    'form.buy.sliderBuyMarketPercent': function(val) {
      let account = this.wallet.base
      this.form.buy.marketAmount = this.toFloor((account * val) / 100, this.baseCoinScale)
    },
    'form.sell.sliderSellMarketPercent': function(val) {
      let account = this.wallet.coin
      this.form.sell.marketAmount = this.toFloor((account * val) / 100, this.baseCoinScale)
    },
    $route() {
      this.getInit()
    },
    screenWidth: function(n, o) {
      if (n <= 768) {
      } else {
        this.smCurrent = 1
      }
    },
  },
  methods: {
    buyLimitChange(value) {
      if (value != '') {
        if (value.indexOf('.') > -1) {
          this.form.buy.limitPrice = value.slice(0, value.indexOf('.') + 3)
        } else {
          this.form.buy.limitPrice = value
        }
      }
      this.form.buy.limitAmount = 0
      let price = this.form.buy.limitPrice,
        account = this.wallet.base,
        amount = 0
      if (value > 0) {
        if (account / price == 0) {
          amount = 0
          this.form.buy.limitAmount = amount
        } else {
          amount = this.toFloor(((account / price) * this.form.buy.sliderBuyLimitPercent) / 100, this.coinScale)
        }
      }
      this.form.buy.limitTurnover = this.toFloor(price * amount, this.baseCoinScale)
    },
    sellLimitChange(value) {
      if (value != '') {
        if (value.indexOf('.') > -1) {
          this.form.sell.limitPrice = value.slice(0, value.indexOf('.') + 3)
        } else {
          this.form.sell.limitPrice = value
        }
      }
      this.form.sell.limitTurnover = this.toFloor(value * this.form.sell.limitAmount, this.coinScale)
    },
    sellLimitAmount(value) {
      this.form.sell.limitTurnover = this.toFloor(value * this.form.sell.limitPrice, this.coinScale)
    },
    getInit() {
      var str = this.$route.params.id.toUpperCase()
      var arr = str.split('_')
      var symbol = arr[0] + '/' + arr[1]
      this.symbol = symbol
      this.currentCoin.coin = arr[0]
      this.currentCoin.base = arr[1]

      this.getSymbolScale()
      this.getSymbol()
      this.getTrade()
      this.getPlateFull()
      this.getPlate()
      if (this.isLogin) {
        this.getWallet()
        this.getCurrentOrder()
        this.getHistoryOrder()
      }
    },
    getSymbol() {
      symbolThumb().then(res => {
        var arr = []
        res.forEach(item => {
          item.chg = this.$math.format(this.$math.multiply(this.$math.bignumber(100), this.$math.bignumber(item.chg)))
          item.isFavor = false
          return arr.push(item)
        })
        this.thumb.data = arr
        var usdt = arr.filter(item => {
          return item.symbol.split('/')[1] == 'USDT'
        })
        this.thumb.data1 = this.thumb.oldData1 = usdt
        var btc = arr.filter(item => {
          return item.symbol.split('/')[1] == 'BTC'
        })
        this.thumb.data2 = this.thumb.oldData2 = btc
        var eth = arr.filter(item => {
          return item.symbol.split('/')[1] == 'ETH'
        })
        this.thumb.data3 = this.thumb.oldData3 = eth
        var str = this.$route.params.id.toUpperCase()
        var arr1 = str.split('_')
        var symbol = arr1[0] + '/' + arr1[1]
        var coinInfo = this.thumb.data.filter(item => {
          return item.symbol == symbol
        })
        this.currentCoin = coinInfo[0]
        this.currentCoin.rose = coinInfo[0].chg > 0 ? '+' + coinInfo[0].chg + '%' : coinInfo[0].chg + '%'
        this.currentCoin.coin = coinInfo[0].symbol.split('/')[0]
        this.currentCoin.base = coinInfo[0].symbol.split('/')[1]
        this.form.buy.limitPrice = this.form.sell.limitPrice = coinInfo[0].close

        if (this.isLogin) {
          this.getFavor()
        }
        this.startWebsock()
      })
    },
    getPlateFull() {
      plateFull({
        symbol: this.symbol,
      }).then(res => {
        this.full = res
        if (this.current == 2) {
          var arr1 = []
          var arr2 = []
          var total = 0
          var total2 = 0
          res.ask.items.forEach(item => {
            total += item.amount
            let obj = {
              value: item.price,
              total: total,
            }
            arr1.push(obj)
          })
          res.bid.items.forEach(item => {
            total2 += item.amount
            let obj = {
              value: item.price,
              total: total2,
            }
            arr2.push(obj)
          })
          let max = Math.max.apply(Math, arr1)
          let min = Math.min.apply(Math, arr1)
          let max2 = Math.max.apply(Math, arr2)
          let min2 = Math.min.apply(Math, arr2)
          this.linechart(arr2, max2, min2)
          this.linechart2(arr1, max, min)
        }
      })
    },
    linechart(arr, max, min) {
      var label2 = this.$t('deptotal')
      var label = this.$t('uTxt11')
      var myChart = echarts.init(document.getElementById('depth'))
      var options = {
        tooltip: {
          trigger: 'axis',
          formatter: function(params) {
            return label + ' ' + params[0].data.value + '<br />' + label2 + ' ' + params[0].data.total
          },
          axisPointer: {
            type: 'none',
          },
        },
        grid: {
          left: '0%',
          right: '0%',
          bottom: '0%',
          containLabel: true,
        },
        xAxis: [
          {
            type: 'category',
            boundaryGap: false,
            axisTick: {
              show: false,
            },
            axisLine: {
              lineStyle: {
                color: '#ffffff',
              },
            },
            axisLabel: {
              show: false,
            },
            show: false,
          },
        ],
        yAxis: [
          {
            type: 'value',
            splitLine: {
              show: false,
            },
            axisTick: {
              show: false,
            },
            axisLabel: {
              inside: true,
              verticalAlign: 'bottom',
            },
            show: false,
            max: max,
            min: min,
          },
        ],
        series: [
          {
            name: 'SELL',
            type: 'line',
            showSymbol: false,
            lineStyle: {
              color: 'rgba(255, 255, 255, 0.2)',
              width: 0,
            },
            areaStyle: {
              color: 'rgba(77, 184, 114, 0.4)',
            },
            symbol: 'none',
            label: {
              show: false,
            },
            data: arr,
          },
        ],
      }
      myChart.setOption(options)
      window.addEventListener('resize', () => {
        myChart.resize()
      })
    },
    linechart2(arr, max, min) {
      var label2 = this.$t('deptotal')
      var label = this.$t('uTxt11')
      var myChart2 = echarts.init(document.getElementById('depth2'))
      var options2 = {
        tooltip: {
          trigger: 'axis',
          formatter: function(params) {
            return label + ' ' + params[0].data.value + '<br />' + label2 + ' ' + params[0].data.total
          },
          axisPointer: {
            type: 'none',
          },
        },
        grid: {
          left: '0%',
          right: '0%',
          bottom: '0%',
          containLabel: true,
        },
        xAxis: [
          {
            type: 'category',
            boundaryGap: false,
            axisTick: {
              show: false,
            },
            axisLine: {
              lineStyle: {
                color: '#ffffff',
              },
            },
            axisLabel: {
              show: false,
            },
            show: false,
          },
        ],
        yAxis: [
          {
            type: 'value',
            splitLine: {
              show: false,
            },
            axisTick: {
              show: false,
            },
            axisLabel: {
              inside: true,
              verticalAlign: 'bottom',
            },
            show: false,
            max: max,
            min: min,
          },
        ],
        series: [
          {
            name: 'buy',
            type: 'line',
            showSymbol: false,
            areaStyle: {
              color: 'rgba(246, 70, 93, 0.3)',
            },
            lineStyle: {
              color: 'rgba(255, 255, 255, 0.2)',
              width: 0,
            },
            symbol: 'none',
            label: {
              show: false,
            },
            data: arr,
          },
        ],
      }
      myChart2.setOption(options2)
      window.addEventListener('resize', () => {
        myChart2.resize()
      })
    },
    getTrade() {
      latestTrade({
        symbol: this.symbol,
        size: 20,
      }).then(res => {
        this.tableData = res
      })
    },
    getFavor() {
      favorFind().then(res => {
        this.tableData4 = []
        this.oldTableData4 = []
        this.thumb.data.forEach(item => {
          res.data.forEach(itx => {
            if (item.symbol == itx.symbol) {
              item.isFavor = true
              this.tableData4.push(item)
              this.oldTableData4.push(item)
            }
          })
        })
      })
    },
    addFavor(row) {
      if (this.isLogin == false) {
        this.$message.info(this.$t('loginFirst'))
      } else {
        favorAdd({
          symbol: row.symbol,
        }).then(res => {
          if (res.code == 0) {
            this.$message.info(this.$t('do_favorite'))
            row.isFavor = true
            this.tableData4.push(row)
          }
        })
      }
    },
    delFavor(row, index) {
      favorDelete({
        symbol: row.symbol,
      }).then(res => {
        if (res.code == 0) {
          this.$message.info(this.$t('cancel_favorite'))
          if (this.activeName4 == 'first') {
            this.tableData4.splice(index, 1)
          } else {
            row.isFavor = false
          }
        }
      })
    },
    getWallet() {
      assetWallet({
        symbol: this.currentCoin.base,
      }).then(res => {
        this.wallet.base = res.data.balance || ''
      })
      assetWallet({
        symbol: this.currentCoin.coin,
      }).then(res => {
        this.wallet.coin = res.data.balance
      })
    },
    getCurrentOrder() {
      orderCurrent({
        symbol: this.symbol,
        pageNo: 0,
        pageSize: 10,
      }).then(res => {
        this.order = res
      })
    },
    getHistoryOrder() {
      orderHistory({
        symbol: this.symbol,
        pageNo: 1,
        pageSize: 10,
      }).then(res => {
        this.order2 = res
      })
    },
    getSymbolScale() {
      symbolInfo({
        symbol: this.symbol,
      }).then(res => {
        this.info = res
        this.baseCoinScale = res.baseCoinScale
        this.coinScale = res.coinScale
      })
    },
    getPlate(str = '') {
      plateMini({
        symbol: this.symbol,
      }).then(res => {
        this.plate.askRows = []
        this.plate.bidRows = []
        let data = res
        if (data.ask && data.ask.items) {
          for (let i = 0; i < data.ask.items.length; i++) {
            if (i == 0) {
              data.ask.items[i].totalAmount = data.ask.items[i].amount
            } else {
              data.ask.items[i].totalAmount = data.ask.items[i - 1].totalAmount + data.ask.items[i].amount
            }
          }
          if (data.ask.items.length >= this.plate.maxPostion) {
            for (let i = this.plate.maxPostion; i > 0; i--) {
              let ask = data.ask.items[i - 1]
              ask.direction = 'SELL'
              ask.position = i
              this.plate.askRows.push(ask)
              let rows = this.plate.askRows,
                totle = rows[0].totalAmount
              this.plate.askTotle = totle
            }
          } else {
            for (let i = this.plate.maxPostion; i > data.ask.items.length; i--) {
              let ask = {
                price: 0,
                amount: 0,
              }
              ask.direction = 'SELL'
              ask.position = i
              ask.totalAmount = ask.amount
              this.plate.askRows.push(ask)
            }
            for (let i = data.ask.items.length; i > 0; i--) {
              let ask = data.ask.items[i - 1]
              ask.direction = 'SELL'
              ask.position = i
              this.plate.askRows.push(ask)
              let askItemIndex = data.ask.items.length - 1 > 0 ? data.ask.items.length - 1 : 0
              let rows = this.plate.askRows,
                totle = rows[askItemIndex].totalAmount
              this.plate.askTotle = totle
            }
          }
        }
        if (data.bid && data.bid.items) {
          for (let i = 0; i < data.bid.items.length; i++) {
            if (i == 0) data.bid.items[i].totalAmount = data.bid.items[i].amount
            else data.bid.items[i].totalAmount = data.bid.items[i - 1].totalAmount + data.bid.items[i].amount
          }
          for (let i = 0; i < data.bid.items.length; i++) {
            let bid = data.bid.items[i]
            bid.direction = 'BUY'
            bid.position = i + 1
            this.plate.bidRows.push(bid)
            if (i == this.plate.maxPostion - 1) break
          }
          if (data.bid.items.length < this.plate.maxPostion) {
            for (let i = data.bid.items.length; i < this.plate.maxPostion; i++) {
              let bid = {
                price: 0,
                amount: 0,
              }
              bid.direction = 'BUY'
              bid.position = i + 1
              bid.totalAmount = 0
              this.plate.bidRows.push(bid)
            }
            let bidItemIndex = data.bid.items.length - 1 > 0 ? data.bid.items.length - 1 : 0
            let rows = this.plate.bidRows,
              totle = rows[bidItemIndex].totalAmount
            this.plate.bidTotle = totle
          } else {
            let rows = this.plate.bidRows,
              len = rows.length,
              totle = rows[len - 1].totalAmount
            this.plate.bidTotle = totle
          }
        }
        if (str != '') {
          this.activeName5 = str
        }
      })
    },
    handleClick(tab) {
      var str = tab.name
      if (str == 'all') {
        this.plate.maxPostion = 10
      } else {
        this.plate.maxPostion = 20
      }
      this.getPlate(str)
    },
    search(value) {
      let keyWord = value.toUpperCase()
      if (this.activeName4 == 'first') {
        if (value == '') {
          this.tableData4 = this.oldTableData4
        } else {
          this.tableData4 = this.oldTableData4.filter(item => item.symbol.indexOf(keyWord) >= 0)
        }
      } else if (this.activeName4 == 'second') {
        if (value == '') {
          this.thumb.data1 = this.thumb.oldData1
        } else {
          this.thumb.data1 = this.thumb.oldData1.filter(item => item.symbol.indexOf(keyWord) >= 0)
        }
      } else if (this.activeName4 == 'third') {
        if (value == '') {
          this.thumb.data2 = this.thumb.oldData2
        } else {
          this.thumb.data2 = this.thumb.oldData2.filter(item => item.symbol.indexOf(keyWord) >= 0)
        }
      } else {
        if (value == '') {
          this.thumb.data3 = this.thumb.oldData3
        } else {
          this.thumb.data3 = this.thumb.oldData3.filter(item => item.symbol.indexOf(keyWord) >= 0)
        }
      }
    },
    successCallback(that, stompClient) {
      that.$refs.tv.getKline({
        a: that.host + '/market',
        b: that.currentCoin,
        c: stompClient,
        d: that.baseCoinScale,
      })
      stompClient.subscribe('/topic/market/thumb', function(msg) {
        var resp = JSON.parse(msg.body)
        if (that.currentCoin != null && that.currentCoin.symbol == resp.symbol) {
          that.currentCoin.close = resp.close
          that.currentCoin.rose =
            resp.chg > 0 ? '+' + (resp.chg * 100).toFixed(2) + '%' : (resp.chg * 100).toFixed(2) + '%'
          that.currentCoin.close = resp.close
          that.currentCoin.high = resp.high
          that.currentCoin.low = resp.low
          that.currentCoin.turnover = parseInt(resp.volume)
          that.currentCoin.volume = resp.volume
          that.currentCoin.usdRate = resp.usdRate
        }
      })

      stompClient.subscribe('/topic/market/trade/' + that.currentCoin.symbol, function(msg) {
        var resp = JSON.parse(msg.body)
        if (resp.length > 0) {
          for (var i = 0; i < resp.length; i++) {
            if (resp[i].symbol == that.currentCoin.symbol) {
              that.tableData.unshift(resp[i])
            }
          }
        }
        if (that.tableData.length > 20) {
          that.tableData = that.tableData.slice(0, 20)
        }
      })

      if (that.isLogin) {
        stompClient.subscribe(
          '/topic/market/order-canceled/' + that.currentCoin.symbol + '/' + that.member.id,
          function() {
            that.refreshAccount()
          }
        )
        stompClient.subscribe(
          '/topic/market/order-completed/' + that.currentCoin.symbol + '/' + that.member.id,
          function() {
            that.refreshAccount()
          }
        )
        stompClient.subscribe(
          '/topic/market/order-trade/' + that.currentCoin.symbol + '/' + that.member.id,
          function() {
            that.refreshAccount()
          }
        )
      }

      stompClient.subscribe('/topic/market/trade-plate/' + that.currentCoin.symbol, function(msg) {
        var resp = JSON.parse(msg.body)
        if (resp.symbol != that.currentCoin.symbol) {
          return
        }
        if (resp.direction == 'SELL') {
          var asks = resp.items
          that.plate.askRows = []
          let totle = 0
          for (let i = that.plate.maxPostion - 1; i >= 0; i--) {
            var ask = {}
            if (i < asks.length) {
              ask = asks[i]
            } else {
              ask['price'] = 0
              ask['amount'] = 0
            }
            ask.direction = 'SELL'
            ask.position = i + 1
            that.plate.askRows.push(ask)
          }
          for (let i = that.plate.askRows.length - 1; i >= 0; i--) {
            if (i == that.plate.askRows.length - 1 || that.plate.askRows[i].price == 0) {
              that.plate.askRows[i].totalAmount = that.plate.askRows[i].amount
            } else {
              that.plate.askRows[i].totalAmount = that.plate.askRows[i + 1].totalAmount + that.plate.askRows[i].amount
            }
            totle += that.plate.askRows[i].amount
          }
          that.plate.askTotle = totle
        } else {
          var bids = resp.items
          that.plate.bidRows = []
          let totle = 0
          for (let i = 0; i < that.plate.maxPostion; i++) {
            var bid = {}
            if (i < bids.length) {
              bid = bids[i]
            } else {
              bid['price'] = 0
              bid['amount'] = 0
            }
            bid.direction = 'BUY'
            bid.position = i + 1
            that.plate.bidRows.push(bid)
          }
          for (let i = 0; i < that.plate.bidRows.length; i++) {
            if (i == 0 || that.plate.bidRows[i].amount == 0) {
              that.plate.bidRows[i].totalAmount = that.plate.bidRows[i].amount
            } else {
              that.plate.bidRows[i].totalAmount = that.plate.bidRows[i - 1].totalAmount + that.plate.bidRows[i].amount
            }
            totle += that.plate.bidRows[i].amount
          }
          that.plate.bidTotle = totle
        }
        if (that.current == 2) {
          that.getPlateFull()
        }
      })
    },
    startWebsock() {
      if (this.stompClient) {
        this.stompClient.ws.close()
      }
      var stompClient = null
      var that = this
      var socket = new SockJS(this.host + '/market/market-ws')
      stompClient = Stomp.over(socket)
      this.stompClient = stompClient
      stompClient.debug = false

      stompClient.connect(
        {},
        function(frame) {
          that.successCallback(that, stompClient)
        },
        () => {
          that.reconnect(that)
        }
      )
      socket.onclose = () => {
        that.reconnect(that)
      }
      socket.onerror = error => {
        that.reconnect(that)
      }
    },
    reconnect(that) {
      let connected = false
      const reconInv = setInterval(() => {
        if (that.stompClient) {
          clearInterval(reconInv)
          connected = true
        }
        var stompClient = null
        var socket = new SockJS(this.host + '/market/market-ws')
        stompClient = Stomp.over(socket)
        that.stompClient = stompClient
        stompClient.debug = false
        stompClient.connect(
          {},
          frame => {
            clearInterval(reconInv)
            connected = true
            that.successCallback(that, stompClient)
          },
          () => {
            that.reconnect(that)
          }
        )
      }, 2000)
    },
    oninput(num) {
      var str = num
      var len1 = str.substr(0, 1)
      var len2 = str.substr(1, 1)
      if (str.length > 1 && len1 == 0 && len2 != '.') {
        str = str.substr(1, 1)
      }
      if (len1 == '.') {
        str = ''
      }
      if (str.indexOf('.') != -1) {
        var str_ = str.substr(str.indexOf('.') + 1)
        if (str_.indexOf('.') != -1) {
          str = str.substr(0, str.indexOf('.') + str_.indexOf('.') + 1)
        }
      }
      return str
    },
    refreshAccount: function() {
      this.getCurrentOrder()
      this.getHistoryOrder()
      this.getWallet()
    },
    rowClick(row) {
      var str = row.symbol.toLowerCase()
      var arr = str.split('/')
      var unit = arr[0] + '_' + arr[1]
      this.$router.push({
        params: {
          id: unit,
        },
      })
      this.popover = false
      this.getInit()
    },
    handleCancel(orderId) {
      this.orderId = orderId
      this.dialogVisible = true
    },
    orderConfirm() {
      orderCancel({
        orderId: this.orderId,
      }).then(res => {
        if (res.code == 0) {
          this.$message({
            message: this.$t('cancelsuccess'),
            type: 'success',
          })
          this.dialogVisible = false
        }
      })
    },
    buyLimit() {
      if (this.form.buy.limitAmount == '') {
        this.$notify.error({
          title: this.$t('apiTxt2'),
          message: this.$t('buyamounttip'),
        })
        return
      }
      var maxAmount = this.wallet.base / this.form.buy.limitPrice
      if (this.form.buy.limitAmount > maxAmount) {
        this.$notify.error({
          title: this.$t('apiTxt2'),
          message: this.$t('buyamounttipwarning') + this.toFloor(maxAmount),
        })
        return
      }
      exchangeAdd({
        amount: this.form.buy.limitAmount,
        direction: 'BUY',
        price: this.form.buy.limitPrice,
        symbol: this.currentCoin.symbol,
        type: 'LIMIT_PRICE',
      }).then(res => {
        if (res.code == 0) {
          this.$notify.success({
            title: this.$t('apiTxt2'),
            message: this.$t('trSuccess'),
          })
          this.getWallet()
          this.getCurrentOrder()
          this.getHistoryOrder()
          this.form.buy.limitAmount = 0
          this.form.buy.sliderBuyLimitPercent = 0
          this.drawer = false
        } else {
          this.$notify.error({
            title: this.$t('apiTxt2'),
            message: res.message,
          })
        }
      })
    },
    buyMarket() {
      if (this.form.buy.marketAmount == '') {
        this.$notify.error({
          title: this.$t('apiTxt2'),
          message: this.$t('buyamounttip'),
        })
        return
      }
      if (this.form.buy.marketAmount > parseFloat(this.wallet.base)) {
        this.$notify.error({
          title: this.$t('apiTxt2'),
          message: this.$t('pricetipwarning') + this.wallet.base,
        })
        return
      }
      exchangeAdd({
        amount: this.form.buy.marketAmount,
        direction: 'BUY',
        price: 0,
        symbol: this.currentCoin.symbol,
        type: 'MARKET_PRICE',
      }).then(res => {
        if (res.code == 0) {
          this.$notify.success({
            title: this.$t('apiTxt2'),
            message: this.$t('trSuccess'),
          })
          this.refreshAccount()
          this.form.buy.sliderBuyMarketPercent = 0
          this.drawer = false
        } else {
          this.$notify.error({
            title: this.$t('apiTxt2'),
            message: res.message,
          })
        }
      })
    },
    sellLimit() {
      if (this.form.sell.limitAmount == '') {
        this.$notify.error({
          title: this.$t('apiTxt2'),
          message: this.$t('sellamounttip'),
        })
        return
      }
      if (this.form.sell.limitPrice == '') {
        this.$notify.error({
          title: this.$t('apiTxt2'),
          message: this.$t('sellpricetip'),
        })
        return
      }
      if (this.form.sell.limitAmount > parseFloat(this.wallet.coin)) {
        this.$notify.error({
          title: this.$t('apiTxt2'),
          message: this.$t('sellmore') + this.wallet.coin + this.$t('sellunit'),
        })
        return
      }
      exchangeAdd({
        amount: this.form.sell.limitAmount,
        direction: 'SELL',
        price: this.form.sell.limitPrice,
        symbol: this.currentCoin.symbol,
        type: 'LIMIT_PRICE',
      }).then(res => {
        if (res.code == 0) {
          this.$notify.success({
            title: this.$t('apiTxt2'),
            message: this.$t('trSuccess'),
          })
          this.refreshAccount()
          this.form.sell.limitAmount = 0
          this.form.sell.sliderSellLimitPercent = 0
          this.drawer = false
        } else {
          this.$notify.error({
            title: this.$t('apiTxt2'),
            message: res.message,
          })
        }
      })
    },
    sellMarket() {
      if (this.form.sell.marketAmount == '') {
        this.$notify.error({
          title: this.$t('apiTxt2'),
          message: this.$t('sellamounttip'),
        })
        return
      }
      if (this.form.sell.marketAmount > parseFloat(this.wallet.coin)) {
        this.$notify.error({
          title: this.$t('apiTxt2'),
          message: this.$t('sellmore') + this.wallet.coin + this.$t('sellunit'),
        })
        return
      }
      exchangeAdd({
        amount: this.form.sell.marketAmount,
        direction: 'SELL',
        price: 0,
        symbol: this.currentCoin.symbol,
        type: 'MARKET_PRICE',
      }).then(res => {
        if (res.code == 0) {
          this.$notify.success({
            title: this.$t('apiTxt2'),
            message: this.$t('trSuccess'),
          })
          this.refreshAccount()
          this.form.sell.sliderSellMarketPercent = 0
          this.drawer = false
        } else {
          this.$notify.error({
            title: this.$t('apiTxt2'),
            message: res.message,
          })
        }
      })
    },
    recharge(coin) {
      this.$router.push({
        path: '/recharge',
        query: {
          name: coin,
        },
      })
    },
    currentClick(index) {
      this.current = index
    },
  },
}
</script>
