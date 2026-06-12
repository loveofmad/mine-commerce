import { createRouter, createWebHistory } from 'vue-router'
import NProgress from 'nprogress'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '仪表盘', icon: 'Odometer' }
      },
      {
        path: 'product',
        name: 'ProductList',
        component: () => import('@/views/product/list.vue'),
        meta: { title: '商品管理', icon: 'Goods' }
      },
      {
        path: 'order',
        name: 'OrderList',
        component: () => import('@/views/order/list.vue'),
        meta: { title: '订单管理', icon: 'List' }
      },
      {
        path: 'user',
        name: 'UserList',
        component: () => import('@/views/user/list.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'coupon',
        name: 'CouponList',
        component: () => import('@/views/coupon/list.vue'),
        meta: { title: '优惠券管理', icon: 'Ticket' }
      },
      {
        path: 'banner',
        name: 'BannerList',
        component: () => import('@/views/banner/list.vue'),
        meta: { title: '轮播图管理', icon: 'Picture' }
      },
      {
        path: 'category',
        name: 'CategoryList',
        component: () => import('@/views/category/list.vue'),
        meta: { title: '分类管理', icon: 'Menu' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  NProgress.start()
  document.title = to.meta.title ? `${to.meta.title} - 土特产商城后台` : '土特产商城后台'
  const token = localStorage.getItem('admin_token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

router.afterEach(() => {
  NProgress.done()
})

export default router
