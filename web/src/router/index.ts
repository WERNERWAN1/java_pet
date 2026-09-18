import {createRouter, createWebHistory} from 'vue-router';
import root from './root';

import { ADMIN_USER_TOKEN, USER_TOKEN } from '/@/store/constants'

// 路由权限白名单
const allowList = ['adminLogin', 'login', 'register', 'portal', 'search', 'detail', '403', '404', 'animalList', 'animalDetail', 'goodsList', 'fosterApply', 'submitReport']
// 前台登录地址
const loginRoutePath = '/index/login'
// 后台登录地址
const adminLoginRoutePath = '/adminLogin'


const router = createRouter({
  history: createWebHistory(),
  routes: root,
});

router.beforeEach(async (to, from, next) => {
  /** 后台路由 **/
  if (to.path.startsWith('/admin')) {
    if (localStorage.getItem(ADMIN_USER_TOKEN)) {
      if (to.path === adminLoginRoutePath) {
        return next({ path: '/' })
      }
      return next()
    }
    if (allowList.includes(to.name as string)) {
      return next()
    }
    return next({ path: adminLoginRoutePath, query: { redirect: to.fullPath } })
  }

  /** 前台路由 **/
  if (to.path.startsWith('/index')) {
    if (localStorage.getItem(USER_TOKEN)) {
      if (to.path === loginRoutePath) {
        return next({ path: '/' })
      }
      return next()
    }
    if (allowList.includes(to.name as string)) {
      return next()
    }
    return next({ path: loginRoutePath, query: { redirect: to.fullPath } })
  }

  /** 其他路由直接放行 **/
  return next()
});

router.afterEach((_to) => {
  // 回到顶部
  document.getElementById("html")?.scrollTo(0, 0)
});

export default router;
