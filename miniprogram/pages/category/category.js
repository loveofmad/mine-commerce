const { getProductList } = require('../../api/product')
const { getCategoryList } = require('../../api/category')
const { formatPrice } = require('../../utils/util')

Page({
  data: {
    categories: [{ id: 0, name: '全部', icon: '/static/icon-fruit.png' }],
    currentCategoryId: 0,
    productList: [],
    keyword: '',
    pageNum: 1,
    pageSize: 10,
    hasMore: true,
    loading: false
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ currentCategoryId: parseInt(options.id) })
    }
    this.loadCategories()
    this.loadProducts()
  },

  async loadCategories() {
    try {
      console.log('开始加载分类...')
      const res = await getCategoryList()
      console.log('分类API返回:', res)
      const list = res || []
      const baseUrl = getApp().globalData.baseUrl
      const categories = [{ id: 0, name: '全部', icon: '/static/icon-fruit.png' }]
      list.forEach(item => {
        categories.push({
          id: item.id,
          name: item.name,
          icon: item.icon && item.icon.startsWith('/') ? baseUrl + item.icon : item.icon
        })
      })
      console.log('最终分类列表:', categories)
      this.setData({ categories })
    } catch (e) {
      console.error('加载分类失败', e)
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
      const params = {
        pageNum: this.data.pageNum,
        pageSize: this.data.pageSize
      }
      if (this.data.currentCategoryId > 0) {
        params.categoryId = this.data.currentCategoryId
      }
      if (this.data.keyword) {
        params.keyword = this.data.keyword
      }
      const res = await getProductList(params)
      const list = res.records || []
      this.setData({
        productList: [...this.data.productList, ...list.map(item => ({
          ...item,
          priceText: formatPrice(item.price)
        }))],
        pageNum: this.data.pageNum + 1,
        hasMore: list.length >= this.data.pageSize,
        loading: false
      })
    } catch (e) {
      this.setData({ loading: false })
    }
  },

  onCategoryTap(e) {
    const id = e.currentTarget.dataset.id
    this.setData({
      currentCategoryId: id,
      pageNum: 1,
      productList: [],
      hasMore: true
    })
    this.loadProducts()
  },

  onSearchInput(e) {
    this.setData({ keyword: e.detail.value })
  },

  onSearchConfirm() {
    this.setData({ pageNum: 1, productList: [], hasMore: true })
    this.loadProducts()
  },

  onProductTap(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/detail/detail?id=${id}` })
  }
})
