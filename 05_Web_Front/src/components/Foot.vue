<template>
  <div class="footer">
    <div class="container flex">
      <div class="foot-left">
        <div class="foot-logo">
          <img src="../assets/img/logo.svg" alt="" />
          <b>BITISAN</b>
        </div>
        <div class="copyright">
          {{ $t('foottext') }}<br />
          Copyright ©2023 BITISAN All rights reserved.
        </div>
        <ul class="foot-share clearfix">
          <li>
            <a href="https://t.me/bitisanex" class="share-link" target="_blank">
              <el-tooltip class="item" effect="dark" content="https://t.me/bitisanex" placement="top">
                <div class="share-link">
                  <span class="iconfont icon-fasongx"></span>
                </div>
              </el-tooltip>
            </a>
          </li>
          <li>
            <a href="https://twitter.com/bitisan" class="share-link" target="_blank">
              <el-tooltip class="item" effect="dark" content="https://twitter.com/bitisan" placement="top">
                <div class="share-link">
                  <span class="iconfont icon-tuite"></span>
                </div>
              </el-tooltip>
            </a>
          </li>
        </ul>
      </div>
      <div class="foot-nav flex clearfix">
        <ul class="foot-list">
          <li>
            <span class="foot-hd">{{ $t('footmeun1') }}</span>
          </li>
          <li>
            <router-link to="/about">{{ $t('footmeun1_1') }}</router-link>
          </li>
          <li>
            <router-link target="_blank" :to="{ path: '/helplist', query: { cate: 6, cateTitle: $t('footmeun1_2') } }">
              {{ $t('footmeun1_2') }}
            </router-link>
          </li>
          <li>
            <router-link to="/announcement/0">{{ $t('footmeun1_3') }}</router-link>
          </li>
          <li>
            <router-link to="/whitepaper">{{ $t('footmeun1_4') }}</router-link>
          </li>
        </ul>
        <ul class="foot-list">
          <li>
            <span class="foot-hd">{{ $t('footmeun2') }}</span>
          </li>
          <li>
            <router-link target="_blank" :to="{ path: '/helplist', query: { cate: 0, cateTitle: $t('footmeun2_1') } }">
              {{ $t('footmeun2_1') }}
            </router-link>
          </li>
          <li>
            <router-link target="_blank" :to="{ path: '/helplist', query: { cate: 1, cateTitle: $t('footmeun2_2') } }">
              {{ $t('footmeun2_2') }}
            </router-link>
          </li>
          <li>
            <router-link target="_blank" :to="{ path: '/helplist', query: { cate: 2, cateTitle: $t('footmeun2_3') } }">
              {{ $t('footmeun2_3') }}
            </router-link>
          </li>
          <li>
            <router-link target="_blank" :to="{ path: '/helplist', query: { cate: 3, cateTitle: $t('footmeun2_4') } }">
              {{ $t('footmeun2_4') }}
            </router-link>
          </li>
        </ul>
        <ul class="foot-list">
          <li>
            <span class="foot-hd">{{ $t('footmeun3') }}</span>
          </li>
          <li>
            <router-link target="_blank" :to="{ path: '/helplist', query: { cate: 5, cateTitle: $t('footmeun3_1') } }">
              {{ $t('footmeun3_1') }}
            </router-link>
          </li>
          <li>
            <router-link target="_blank" :to="{ path: '/helplist', query: { cate: 5, cateTitle: $t('footmeun3_2') } }">
              {{ $t('footmeun3_2') }}
            </router-link>
          </li>
          <li>
            <router-link target="_blank" :to="{ path: '/helplist', query: { cate: 5, cateTitle: $t('footmeun3_3') } }">
              {{ $t('footmeun3_3') }}
            </router-link>
          </li>
          <li>
            <router-link target="_blank" :to="{ path: '/helplist', query: { cate: 5, cateTitle: $t('footmeun3_4') } }">
              {{ $t('footmeun3_4') }}
            </router-link>
          </li>
        </ul>
        <ul class="foot-list">
          <li>
            <span class="foot-hd">{{ $t('footmeun4') }}</span>
          </li>
          <li>
            <el-tooltip class="item" effect="dark" content="service@bitisan.biz" placement="right">
              <div class="foot-link">{{ $t('footmeun4_1') }}</div>
            </el-tooltip>
          </li>
          <li>
            <el-tooltip class="item" effect="dark" content="support@bitisan.biz" placement="right">
              <div class="foot-link">{{ $t('footmeun4_2') }}</div>
            </el-tooltip>
          </li>
          <li>
            <el-tooltip class="item" effect="dark" content="list@bitisan.biz" placement="right">
              <div class="foot-link">{{ $t('footmeun4_3') }}</div>
            </el-tooltip>
          </li>
          <li>
            <el-tooltip class="item" effect="dark" content="ceo@bitisan.biz" placement="right">
              <div class="foot-link">{{ $t('footmeun4_4') }}</div>
            </el-tooltip>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Foot',
}
</script>
