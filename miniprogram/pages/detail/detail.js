const { getProductDetail, getSkuList } = require('../../api/product')
const { addToCart } = require('../../api/cart')
const { formatPrice, checkLogin } = require('../../utils/util')

Page({
  data: {
    product: null,
    skuList: [],
    selectedSku: null,
    quantity: 1,
    currentImage: 0,
    images: []
  },

  onLoad(options) {
    if (options.id) {
      this.loadProduct(options.id)
    }
  },

  async loadProduct(id) {
    try {
      const product = await getProductDetail(id)
      const images = product.images ? product.images.split(',') : [product.mainImage]
      this.setData({
        product: { ...product, priceText: formatPrice(product.price) },
        images
      })
      wx.setNavigationBarTitle({ title: product.title })

      const skuList = await getSkuList(id)
      this.setData({
        skuList: skuList.map(sku => ({ ...sku, priceText: formatPrice(sku.price) })),
        selectedSku: skuList.length > 0 ? skuList[0] : null
      })
    } catch (e) {
      wx.showToast({ title: '加载失败', icon: 'none' })
    }
  },

  onImageTap(e) {
    const index = e.currentTarget.dataset.index
    wx.previewImage({
      urls: this.data.images,
      current: this.data.images[index]
    })
  },

  onSwiperChange(e) {
    this.setData({ currentImage: e.detail.current })
  },

  onSkuTap(e) {
    const index = e.currentTarget.dataset.index
    this.setData({ selectedSku: this.data.skuList[index] })
  },

  onQuantityMinus() {
    if (this.data.quantity > 1) {
      this.setData({ quantity: this.data.quantity - 1 })
    }
  },

  onQuantityPlus() {
    const maxStock = this.data.selectedSku ? this.data.selectedSku.stock : 99
    if (this.data.quantity < maxStock) {
      this.setData({ quantity: this.data.quantity + 1 })
    }
  },

  async onAddToCart() {
    if (!checkLogin()) return
    if (!this.data.selectedSku) {
      wx.showToast({ title: '请选择规格', icon: 'none' })
      return
    }
    const app = getApp()
    const sku = this.data.selectedSku
    try {
      await addToCart({
        userId: app.globalData.userId,
        spuId: this.data.product.id,
        skuId: sku.id,
        spuName: this.data.product.title,
        skuTitle: sku.title,
        skuImage: sku.image || this.data.product.mainImage,
        price: sku.price,
        quantity: this.data.quantity,
        checked: 1
      })
      wx.showToast({ title: '已加入购物车', icon: 'success' })
    } catch (e) {
      wx.showToast({ title: '加入失败', icon: 'none' })
    }
  },

  onBuyNow() {
    if (!checkLogin()) return
    if (!this.data.selectedSku) {
      wx.showToast({ title: '请选择规格', icon: 'none' })
      return
    }
    const sku = this.data.selectedSku
    const items = [{
      spuId: this.data.product.id,
      skuId: sku.id,
      spuName: this.data.product.title,
      skuTitle: sku.title,
      skuImage: sku.image || this.data.product.mainImage,
      price: sku.price,
      quantity: this.data.quantity
    }]
    wx.navigateTo({
      url: `/pages/order/order?items=${encodeURIComponent(JSON.stringify(items))}`
    })
  },

  onGoCart() {
    wx.switchTab({ url: '/pages/cart/cart' })
  }
})
