const { getProductList } = require('../../api/product')
const { formatPrice } = require('../../utils/util')

const HISTORY_KEY = 'search_history'
const MAX_HISTORY = 10

Page({
  data: {
    keyword: '',
    historyList: [],
    hotKeywords: ['苹果', '蜂蜜', '茶叶', '坚果', '礼盒', '核桃', '花生', '普洱'],
    searched: false,
    productList: [],
    total: 0,
    pageNum: 1,
    pageSize: 10,
    hasMore: true,
    loading: false,
    autoFocus: true
  },

  onLoad() {
    this.loadHistory()
  },

  loadHistory() {
    const history = wx.getStorageSync(HISTORY_KEY) || []
    this.setData({ historyList: history })
  },

  saveHistory(keyword) {
    if (!keyword || !keyword.trim()) return
    let history = wx.getStorageSync(HISTORY_KEY) || []
    history = history.filter(item => item !== keyword.trim())
    history.unshift(keyword.trim())
    if (history.length > MAX_HISTORY) history = history.slice(0, MAX_HISTORY)
    wx.setStorageSync(HISTORY_KEY, history)
    this.setData({ historyList: history })
  },

  onInput(e) {
    this.setData({ keyword: e.detail.value })
  },

  onClear() {
    this.setData({ keyword: '', searched: false, productList: [] })
  },

  onSearch() {
    const keyword = this.data.keyword.trim()
    if (!keyword) return
    this.saveHistory(keyword)
    this.setData({ searched: true, pageNum: 1, productList: [], hasMore: true })
    this.doSearch()
  },

  onHistoryTap(e) {
    const keyword = e.currentTarget.dataset.keyword
    this.setData({ keyword })
    this.onSearch()
  },

  onClearHistory() {
    wx.showModal({
      title: '提示',
      content: '确定清空搜索历史？',
      success: (res) => {
        if (res.confirm) {
          wx.removeStorageSync(HISTORY_KEY)
          this.setData({ historyList: [] })
        }
      }
    })
  },

  onDeleteHistory(e) {
    const index = e.currentTarget.dataset.index
    const history = this.data.historyList.filter((_, i) => i !== index)
    wx.setStorageSync(HISTORY_KEY, history)
    this.setData({ historyList: history })
  },

  async doSearch() {
    if (this.data.loading) return
    this.setData({ loading: true })
    try {
      const res = await getProductList({
        keyword: this.data.keyword,
        pageNum: this.data.pageNum,
        pageSize: this.data.pageSize
      })
      const list = (res.records || []).map(item => {
        const baseUrl = getApp().globalData.baseUrl
        const mainImage = item.mainImage && item.mainImage.startsWith('/') ? baseUrl + item.mainImage : item.mainImage
        return { ...item, mainImage, priceText: formatPrice(item.price) }
      })
      this.setData({
        productList: this.data.pageNum === 1 ? list : [...this.data.productList, ...list],
        total: res.total || 0,
        hasMore: list.length >= this.data.pageSize
      })
    } catch (e) {
      console.error('搜索失败', e)
    } finally {
      this.setData({ loading: false })
    }
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading && this.data.searched) {
      this.setData({ pageNum: this.data.pageNum + 1 })
      this.doSearch()
    }
  },

  onProductTap(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/detail/detail?id=${id}` })
  }
})
