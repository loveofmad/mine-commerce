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
      const app = getApp()
      const baseUrl = app.globalData.baseUrl
      const fixImage = (img) => img && img.startsWith('/') ? baseUrl + img : img
      let images = [fixImage(product.mainImage)]
      if (product.images) {
        try {
          const parsed = JSON.parse(product.images)
          if (Array.isArray(parsed) && parsed.length > 0) {
            images = parsed.map(fixImage)
          }
        } catch (e) {
          images = product.images.split(',').filter(i => i).map(fixImage)
        }
      }
      this.setData({
        product: { ...product, mainImage: fixImage(product.mainImage), priceText: formatPrice(product.price) },
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
    const app = getApp()
    const product = this.data.product
    
    // 如果有SKU列表，需要选择规格
    if (this.data.skuList.length > 0 && !this.data.selectedSku) {
      wx.showToast({ title: '请选择规格', icon: 'none' })
      return
    }
    
    const sku = this.data.selectedSku || { id: 0, title: product.title, price: product.price, image: product.mainImage }
    
    try {
      await addToCart({
        userId: app.globalData.userId,
        spuId: product.id,
        skuId: sku.id,
        spuName: product.title,
        skuTitle: sku.title,
        skuImage: sku.image || product.mainImage,
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
    const product = this.data.product
    
    // 如果有SKU列表，需要选择规格
    if (this.data.skuList.length > 0 && !this.data.selectedSku) {
      wx.showToast({ title: '请选择规格', icon: 'none' })
      return
    }
    
    const sku = this.data.selectedSku || { id: 0, title: product.title, price: product.price, image: product.mainImage }
    
    const items = [{
      spuId: product.id,
      skuId: sku.id,
      spuName: product.title,
      skuTitle: sku.title,
      skuImage: sku.image || product.mainImage,
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
