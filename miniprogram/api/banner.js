const { get } = require('./request')

const getBannerList = () => {
  return get('/api/banner/list')
}

module.exports = { getBannerList }
