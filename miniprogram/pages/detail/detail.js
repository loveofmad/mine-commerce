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
    images: [],
    displayPrice: '',
    displayStock: 0
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
            images = [fixImage(product.mainImage), ...parsed.filter(img => img && img !== product.mainImage).map(fixImage)]
          }
        } catch (e) {
          const extraImages = product.images.split(',').filter(i => i && i !== product.mainImage)
          images = [fixImage(product.mainImage), ...extraImages.map(fixImage)]
        }
      }
      this.setData({
        product: { ...product, mainImage: fixImage(product.mainImage), priceText: formatPrice(product.price) },
        images
      })
      wx.setNavigationBarTitle({ title: product.title })

      const skuList = await getSkuList(id)
      const firstSku = skuList.length > 0 ? skuList[0] : null
      this.setData({
        skuList: skuList.map(sku => ({ ...sku, priceText: formatPrice(sku.price) })),
        selectedSku: firstSku,
        displayPrice: firstSku ? formatPrice(firstSku.price) : formatPrice(product.price),
        displayStock: firstSku ? firstSku.stock : product.stock
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
    const sku = this.data.skuList[index]
    this.setData({
      selectedSku: sku,
      displayPrice: formatPrice(sku.price),
      displayStock: sku.stock
    })
  },

  onQuantityMinus() {
    if (this.data.quantity > 1) {
      this.setData({ quantity: this.data.quantity - 1 })
    }
  },

  onQuantityPlus() {
    if (this.data.displayStock <= 0) {
      wx.showToast({ title: '库存不足', icon: 'none' })
      return
    }
    if (this.data.quantity < this.data.displayStock) {
      this.setData({ quantity: this.data.quantity + 1 })
    }
  },

  async onAddToCart() {
    if (!checkLogin()) return
    const app = getApp()
    const product = this.data.product
    const baseUrl = app.globalData.baseUrl
    
    // 如果有SKU列表，需要选择规格
    if (this.data.skuList.length > 0 && !this.data.selectedSku) {
      wx.showToast({ title: '请选择规格', icon: 'none' })
      return
    }

    // 检查库存
    if (this.data.displayStock <= 0) {
      wx.showToast({ title: '库存不足，无法购买', icon: 'none' })
      return
    }

    const sku = this.data.selectedSku || { id: 0, title: product.title, price: product.price, image: product.mainImage }
    
    // 处理图片URL：相对路径拼接baseUrl
    const fixImageUrl = (url) => {
      if (!url) return ''
      if (url.startsWith('http')) return url
      if (url.startsWith('/')) return baseUrl + url
      return url
    }
    
    try {
      await addToCart({
        userId: app.globalData.userId,
        spuId: product.id,
        skuId: sku.id,
        spuName: product.title,
        skuTitle: sku.title,
        skuImage: fixImageUrl(sku.image || product.mainImage),
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
    const app = getApp()
    const product = this.data.product
    const baseUrl = app.globalData.baseUrl
    
    // 如果有SKU列表，需要选择规格
    if (this.data.skuList.length > 0 && !this.data.selectedSku) {
      wx.showToast({ title: '请选择规格', icon: 'none' })
      return
    }

    // 检查库存
    if (this.data.displayStock <= 0) {
      wx.showToast({ title: '库存不足，无法购买', icon: 'none' })
      return
    }
    
    const sku = this.data.selectedSku || { id: 0, title: product.title, price: product.price, image: product.mainImage }
    
    // 处理图片URL：相对路径拼接baseUrl
    const fixImageUrl = (url) => {
      if (!url) return ''
      if (url.startsWith('http')) return url
      if (url.startsWith('/')) return baseUrl + url
      return url
    }
    
    const items = [{
      spuId: product.id,
      skuId: sku.id,
      spuName: product.title,
      skuTitle: sku.title,
      skuImage: fixImageUrl(sku.image || product.mainImage),
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
