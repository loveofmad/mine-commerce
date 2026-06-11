const { getProductList } = require('../../api/product')
const { formatPrice } = require('../../utils/util')

Page({
  data: {
    categories: [
      { id: 0, name: '全部', icon: '/static/icon-fruit.png' },
      { id: 1, name: '水果', icon: '/static/icon-fruit.png' },
      { id: 2, name: '坚果', icon: '/static/icon-nuts.png' },
      { id: 3, name: '茶叶', icon: '/static/icon-tea.png' },
      { id: 4, name: '蜂蜜', icon: '/static/icon-honey.png' },
      { id: 5, name: '特产礼盒', icon: '/static/icon-gift.png' }
    ],
    currentCategoryId: 0,
    productList: [],
    keyword: '',
    pageNum: 1,
    pageSize: 10,
    hasMore: true,
    loading: false
  },

  onLoad() {
    this.loadProducts()
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
