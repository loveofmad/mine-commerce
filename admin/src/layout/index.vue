<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '72px' : '240px'" class="aside">
      <div class="logo" :class="{ 'logo-collapse': isCollapse }">
        <div class="logo-icon">M</div>
        <transition name="fade">
          <span v-show="!isCollapse" class="logo-text">土特产商城</span>
        </transition>
      </div>
      <el-scrollbar>
        <el-menu
          :default-active="route.path"
          :collapse="isCollapse"
          background-color="transparent"
          text-color="rgba(255,255,255,0.7)"
          active-text-color="#fff"
          router
          :collapse-transition="false"
          class="side-menu"
        >
          <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path" class="menu-item">
            <el-icon><component :is="item.icon" /></el-icon>
            <template #title>{{ item.title }}</template>
          </el-menu-item>
        </el-menu>
      </el-scrollbar>
    </el-aside>
    <el-container class="main-container">
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="route.meta.title">{{ route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-badge :value="3" class="notification-badge">
            <div class="header-icon-btn">
              <el-icon><Bell /></el-icon>
            </div>
          </el-badge>
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="user-info">
              <el-avatar :size="36" class="user-avatar">
                {{ (userStore.userInfo.username || 'A')[0].toUpperCase() }}
              </el-avatar>
              <span class="username">{{ userStore.userInfo.username || '管理员' }}</span>
              <el-icon class="arrow"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main">
        <router-view v-slot="{ Component }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/index'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)

const menuItems = [
  { path: '/dashboard', title: '仪表盘', icon: 'Odometer' },
  { path: '/product', title: '商品管理', icon: 'Goods' },
  { path: '/category', title: '分类管理', icon: 'Menu' },
  { path: '/banner', title: '轮播图管理', icon: 'Picture' },
  { path: '/order', title: '订单管理', icon: 'List' },
  { path: '/user', title: '用户管理', icon: 'User' },
  { path: '/coupon', title: '优惠券管理', icon: 'Ticket' }
]

function handleCommand(cmd) {
  if (cmd === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside {
  background: linear-gradient(180deg, #1E293B 0%, #0F172A 100%);
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.logo {
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  padding: 0 20px;
}

.logo-collapse {
  padding: 0;
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
  font-weight: 700;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(74, 144, 164, 0.4);
}

.logo-text {
  color: #fff;
  font-size: 17px;
  font-weight: 600;
  white-space: nowrap;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

.side-menu {
  border-right: none;
  padding: 12px 0;
}

.menu-item {
  margin: 4px 12px;
  border-radius: 10px;
  height: 48px;
  line-height: 48px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
}

.menu-item:hover {
  background: rgba(255, 255, 255, 0.08) !important;
}

.menu-item.is-active {
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%) !important;
  box-shadow: 0 4px 12px rgba(74, 144, 164, 0.4);
}

.main-container {
  background: var(--bg-page);
}

.header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 0 0 var(--border);
  padding: 0 28px;
  height: 64px;
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: var(--text-muted);
  transition: color 0.2s;
  padding: 8px;
  border-radius: var(--radius);
}

.collapse-btn:hover {
  color: var(--primary);
  background: var(--primary-bg);
}

.breadcrumb {
  font-size: 14px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-icon-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius);
  cursor: pointer;
  color: var(--text-secondary);
  transition: all 0.2s;
}

.header-icon-btn:hover {
  background: var(--bg-page);
  color: var(--primary);
}

.notification-badge {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: var(--radius);
  transition: background 0.2s;
}

.user-info:hover {
  background: var(--bg-page);
}

.user-avatar {
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%);
  color: #fff;
  font-weight: 600;
}

.username {
  font-size: 14px;
  color: var(--text-primary);
  font-weight: 500;
}

.arrow {
  font-size: 12px;
  color: var(--text-muted);
}

.main {
  padding: 24px;
  min-height: calc(100vh - 64px);
}

.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.2s, transform 0.2s;
}

.page-fade-enter-from {
  opacity: 0;
  transform: translateY(8px);
}

.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
