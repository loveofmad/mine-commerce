const { get } = require('./request')

const getProductList = (params = {}) => {
  return get('/api/product/list', {}, params)
}

const getProductDetail = (id) => {
  return get(`/api/product/${id}`)
}

const getSkuList = (spuId) => {
  return get(`/api/product/${spuId}/sku`)
}

module.exports = { getProductList, getProductDetail, getSkuList }
