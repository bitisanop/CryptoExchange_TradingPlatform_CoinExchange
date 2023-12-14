import request from '../request'
export function favorFind(data) {
  return request({
    url: '/exchange/favor/find',
    method: 'POST',
    data: data,
  })
}
export function orderHistory(data) {
  return request({
    url: '/exchange/order/history',
    method: 'POST',
    data: data,
  })
}
export function orderCurrent(data) {
  return request({
    url: '/exchange/order/current',
    method: 'POST',
    data: data,
  })
}
export function orderCancel(data) {
  return request({
    url: '/exchange/order/cancel/' + data.orderId,
    method: 'POST',
    data: data,
  })
}
export function perCurrent(data) {
  return request({
    url: '/exchange/order/personal/current',
    method: 'POST',
    data: data,
  })
}
export function perHistory(data) {
  return request({
    url: '/exchange/order/personal/history',
    method: 'POST',
    data: data,
  })
}
export function exchangeAdd(data) {
  return request({
    url: '/exchange/order/add',
    method: 'POST',
    data: data,
  })
}
export function favorAdd(data) {
  return request({
    url: '/exchange/favor/add',
    method: 'POST',
    data: data,
  })
}
export function favorDelete(data) {
  return request({
    url: '/exchange/favor/delete',
    method: 'POST',
    data: data,
  })
}
