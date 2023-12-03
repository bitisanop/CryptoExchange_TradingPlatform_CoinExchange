import request from '../request'
export function symbolTrend(data) {
  return request({
    url: '/market/symbol-thumb-trend',
    method: 'POST',
    data: data,
  })
}
export function symbolThumb(data) {
  return request({
    url: '/market/symbol-thumb',
    method: 'POST',
    data: data,
  })
}
export function symbolInfo(data) {
  return request({
    url: '/market/symbol-info',
    method: 'POST',
    data: data,
  })
}
export function plateMini(data) {
  return request({
    url: '/market/exchange-plate-mini',
    method: 'POST',
    data: data,
  })
}
export function plateFull(data) {
  return request({
    url: '/market/exchange-plate-full',
    method: 'POST',
    data: data,
  })
}
export function latestTrade(data) {
  return request({
    url: '/market/latest-trade',
    method: 'POST',
    data: data,
  })
}
export function marketK(data) {
  return request({
    url: '/market/history',
    method: 'POST',
    data: data,
  })
}
export function exchangeRate(data) {
  return request({
    url: '/market/exchange-rate/usd-cny',
    method: 'POST',
    data: data,
  })
}
export function marketCoinInfo(data) {
  return request({
    url: '/market/coin-info',
    method: 'POST',
    data: data,
  })
}
export function marketBtcTrend(data) {
  return request({
    url: '/market/btc/trend',
    method: 'POST',
    data: data,
  })
}
