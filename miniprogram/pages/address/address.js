const { getAddressList, addAddress, updateAddress, deleteAddress } = require('../../api/address')
const regions = require('../../utils/regions')

Page({
  data: {
    addressList: [],
    loading: false,
    showEdit: false,
    editType: 'add',
    editId: null,
    showRegionPicker: false,
    currentStep: 0,
    selectedProvince: '',
    selectedCity: '',
    selectedDistrict: '',
    currentCities: [],
    currentDistricts: [],
    regionList: [],
    form: {
      name: '',
      phone: '',
      province: '',
      city: '',
      district: '',
      detail: '',
      isDefault: 0
    },
    provinces: regions
  },

  onLoad() {
    this.loadAddresses()
  },

  onShow() {
    this.loadAddresses()
  },

  async loadAddresses() {
    const app = getApp()
    if (!app.globalData.userId) return

    this.setData({ loading: true })
    try {
      const res = await getAddressList(app.globalData.userId)
      this.setData({ addressList: res || [] })
    } catch (e) {
      console.error('加载地址失败', e)
    } finally {
      this.setData({ loading: false })
    }
  },

  onAddTap() {
    this.setData({
      showEdit: true,
      editType: 'add',
      editId: null,
      form: { name: '', phone: '', province: '', city: '', district: '', detail: '', isDefault: 0 }
    })
  },

  onEditTap(e) {
    const address = e.currentTarget.dataset.address
    this.setData({
      showEdit: true,
      editType: 'edit',
      editId: address.id,
      form: {
        name: address.name,
        phone: address.phone,
        province: address.province,
        city: address.city,
        district: address.district,
        detail: address.detail,
        isDefault: address.isDefault
      }
    })
  },

  onCloseEdit() {
    this.setData({ showEdit: false })
  },

  onNameInput(e) {
    this.setData({ 'form.name': e.detail.value })
  },

  onPhoneInput(e) {
    this.setData({ 'form.phone': e.detail.value })
  },

  onDetailInput(e) {
    this.setData({ 'form.detail': e.detail.value })
  },

  onChooseRegion() {
    this.setData({
      showRegionPicker: true,
      currentStep: 0,
      selectedProvince: '',
      selectedCity: '',
      selectedDistrict: '',
      regionList: this.data.provinces,
      currentCities: [],
      currentDistricts: []
    })
  },

  getProvinces() {
    return this.data.provinces || []
  },

  getCities(provinceName) {
    const found = this.data.provinces.find(p => p.name === provinceName)
    return found ? found.cities : []
  },

  getDistricts(provinceName, cityName) {
    const found = this.data.provinces.find(p => p.name === provinceName)
    if (found) {
      const cityObj = found.cities.find(c => c.name === cityName)
      return cityObj ? cityObj.districts : []
    }
    return []
  },

  onProvinceTap(e) {
    const name = e.currentTarget.dataset.name
    const cities = this.getCities(name)
    this.setData({
      selectedProvince: name,
      currentStep: 1,
      regionList: cities,
      currentDistricts: [],
      selectedCity: '',
      selectedDistrict: ''
    })
  },

  onCityTap(e) {
    const name = e.currentTarget.dataset.name
    const districts = this.getDistricts(this.data.selectedProvince, name)
    this.setData({
      selectedCity: name,
      currentStep: 2,
      regionList: districts,
      selectedDistrict: ''
    })
  },

  onDistrictTap(e) {
    const name = e.currentTarget.dataset.name
    this.setData({ selectedDistrict: name, showRegionPicker: false })
    this.setData({
      'form.province': this.data.selectedProvince,
      'form.city': this.data.selectedCity,
      'form.district': name
    })
  },

  onCloseRegionPicker() {
    this.setData({ showRegionPicker: false })
  },

  onDefaultChange(e) {
    this.setData({ 'form.isDefault': e.detail.value ? 1 : 0 })
  },

  async onSubmit() {
    const { form, editType, editId } = this.data
    if (!form.name) {
      wx.showToast({ title: '请输入收货人姓名', icon: 'none' })
      return
    }
    if (!form.phone) {
      wx.showToast({ title: '请输入手机号', icon: 'none' })
      return
    }
    // 验证手机号格式
    const phoneReg = /^1[3-9]\d{9}$/
    if (!phoneReg.test(form.phone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' })
      return
    }
    if (!form.detail) {
      wx.showToast({ title: '请输入详细地址', icon: 'none' })
      return
    }

    const app = getApp()
    const addressData = {
      ...form,
      userId: app.globalData.userId
    }

    try {
      if (editType === 'add') {
        await addAddress(addressData)
        wx.showToast({ title: '添加成功', icon: 'success' })
      } else {
        await updateAddress(editId, addressData)
        wx.showToast({ title: '修改成功', icon: 'success' })
      }
      this.setData({ showEdit: false })
      this.loadAddresses()
    } catch (e) {
      wx.showToast({ title: '操作失败', icon: 'none' })
    }
  },

  onDeleteTap(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '提示',
      content: '确定删除该地址吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await deleteAddress(id)
            wx.showToast({ title: '删除成功', icon: 'success' })
            this.loadAddresses()
          } catch (e) {
            wx.showToast({ title: '删除失败', icon: 'none' })
          }
        }
      }
    })
  },

  onSelectTap(e) {
    const address = e.currentTarget.dataset.address
    const pages = getCurrentPages()
    const prevPage = pages[pages.length - 2]
    if (prevPage) {
      prevPage.setData({ address })
      wx.setStorageSync('defaultAddress', address)
    }
    wx.navigateBack()
  }
})
