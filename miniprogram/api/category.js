const { get } = require('./request')

const getCategoryList = () => {
  return get('/api/category/list')
}

const getCategoryDetail = (id) => {
  return get(`/api/category/${id}`)
}

module.exports = { getCategoryList, getCategoryDetail }
