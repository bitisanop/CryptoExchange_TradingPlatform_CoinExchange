import Vue from 'vue'
import VueRouter from 'vue-router'
Vue.use(VueRouter)
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}
const routes = [
  {
    path: '/',
    name: 'index',
    component: () => import('../views/web/index'),
  },
  {
    path: '/exchange/:id',
    component: () => import('../views/web/exchange'),
  },
  {
    path: '/Login',
    name: 'Login',
    component: () => import('../views/Login'),
  },
  {
    path: '/notFound',
    name: 'notFound',
    component: () => import('../views/notFound'),
  },
  { path: '*', redirect: '/notFound', hidden: true, meta: { keepAlive: false } },
]
const router = new VueRouter({
  routes,
})
export default router
