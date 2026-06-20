const buildUrl = (url, params) => {
  if (!params || Object.keys(params).length === 0) return url
  const query = Object.entries(params)
    .filter(([, v]) => v !== undefined && v !== null)
    .map(([k, v]) => `${encodeURIComponent(k)}=${encodeURIComponent(v)}`)
    .join('&')
  return query ? `${url}?${query}` : url
}

const request = (url, method = 'GET', data = {}, params = {}, header = {}) => {
  const baseUrl = getApp().globalData.baseUrl
  const fullUrl = baseUrl + buildUrl(url, params)
  console.log('请求URL:', fullUrl)
  return new Promise((resolve, reject) => {
    wx.request({
      url: fullUrl,
      method,
      data,
      header: {
        'Content-Type': 'application/json',
        ...header
      },
      success(res) {
        console.log('响应状态码:', res.statusCode)
        console.log('响应数据:', res.data)
        if (res.statusCode === 200) {
          const result = res.data
          if (result.code === 200 || result.code === 0) {
            resolve(result.data)
          } else {
            wx.showToast({ title: result.message || '请求失败', icon: 'none' })
            reject(result)
          }
        } else {
          wx.showToast({ title: '网络错误', icon: 'none' })
          reject(res)
        }
      },
      fail(err) {
        console.error('请求失败:', err)
        wx.showToast({ title: '网络连接失败', icon: 'none' })
        reject(err)
      }
    })
  })
}

const get = (url, data, params) => request(url, 'GET', data, params)

const post = (url, data, params) => request(url, 'POST', data, params)

const put = (url, data, params) => request(url, 'PUT', data, params)

const del = (url, data, params) => request(url, 'DELETE', data, params)

module.exports = { request, get, post, put, del }
