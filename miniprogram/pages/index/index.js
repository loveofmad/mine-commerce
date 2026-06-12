const { getProductList } = require('../../api/product')
const { getCategoryList } = require('../../api/category')
const { formatPrice } = require('../../utils/util')

Page({
  data: {
    banners: [
      { id: 1, imageUrl: '/static/banner1.png', title: '精选土特产' },
      { id: 2, imageUrl: '/static/banner2.png', title: '新鲜水果' },
      { id: 3, imageUrl: '/static/banner3.png', title: '地道美味' }
    ],
    categories: [],
    productList: [],
    pageNum: 1,
    pageSize: 10,
    hasMore: true,
    loading: false
  },

  onLoad() {
    this.loadCategories()
    this.loadProducts()
  },

  async loadCategories() {
    try {
      console.log('首页开始加载分类...')
      const res = await getCategoryList()
      console.log('首页分类API返回:', res)
      const list = res || []
      const baseUrl = getApp().globalData.baseUrl
      const categories = []
      list.forEach(item => {
        categories.push({
          id: item.id,
          name: item.name,
          icon: item.icon && item.icon.startsWith('/') ? baseUrl + item.icon : item.icon
        })
      })
      console.log('首页最终分类列表:', categories)
      this.setData({ categories })
    } catch (e) {
      console.error('首页加载分类失败', e)
    }
  },

  onPullDownRefresh() {
    this.setData({ pageNum: 1, productList: [], hasMore: true })
    this.loadProducts().then(() => wx.stopPullDownRefresh())
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadProducts()
    }
  },

  async loadProducts() {
    if (this.data.loading) return
    this.setData({ loading: true })
    try {
      console.log('开始加载商品...')
      const res = await getProductList({
        pageNum: this.data.pageNum,
        pageSize: this.data.pageSize
      })
      console.log('API返回:', res)
      const list = res.records || []
      const app = getApp()
      const baseUrl = app.globalData.baseUrl
      console.log('商品列表:', list)
      this.setData({
        productList: [...this.data.productList, ...list.map(item => ({
          ...item,
          mainImage: item.mainImage && item.mainImage.startsWith('/') ? baseUrl + item.mainImage : item.mainImage,
          priceText: formatPrice(item.price)
        }))],
        pageNum: this.data.pageNum + 1,
        hasMore: list.length >= this.data.pageSize,
        loading: false
      })
    } catch (e) {
      console.error('加载商品失败:', e)
      this.setData({ loading: false })
    }
  },

  onBannerTap(e) {
    const id = e.currentTarget.dataset.id
    if (id) {
      wx.navigateTo({ url: `/pages/detail/detail?id=${id}` })
    }
  },

  onCategoryTap(e) {
    const id = e.currentTarget.dataset.id
    wx.switchTab({ url: '/pages/category/category' })
  },

  onProductTap(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/detail/detail?id=${id}` })
  },

  onSearchTap() {
    wx.navigateTo({ url: '/pages/category/category' })
  }
})
