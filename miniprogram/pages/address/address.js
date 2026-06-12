const { getAddressList, addAddress, updateAddress, deleteAddress } = require('../../api/address')

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
    form: {
      name: '',
      phone: '',
      province: '',
      city: '',
      district: '',
      detail: '',
      isDefault: 0
    },
    provinces: [
      { name: '北京市', cities: [{ name: '北京市', districts: ['东城区', '西城区', '朝阳区', '海淀区', '丰台区', '石景山区', '通州区', '顺义区', '大兴区', '昌平区', '房山区', '门头沟区', '怀柔区', '平谷区', '密云区', '延庆区'] }] },
      { name: '上海市', cities: [{ name: '上海市', districts: ['黄浦区', '徐汇区', '长宁区', '静安区', '普陀区', '虹口区', '杨浦区', '闵行区', '宝山区', '嘉定区', '浦东新区', '金山区', '松江区', '青浦区', '奉贤区', '崇明区'] }] },
      { name: '广东省', cities: [{ name: '广州市', districts: ['越秀区', '海珠区', '荔湾区', '天河区', '白云区', '黄埔区', '番禺区', '花都区', '南沙区', '从化区', '增城区'] }, { name: '深圳市', districts: ['罗湖区', '福田区', '南山区', '宝安区', '龙岗区', '盐田区', '龙华区', '坪山区', '光明区'] }, { name: '东莞市', districts: ['莞城街道', '南城街道', '东城街道', '万江街道'] }, { name: '佛山市', districts: ['禅城区', '南海区', '顺德区', '三水区', '高明区'] }] },
      { name: '浙江省', cities: [{ name: '杭州市', districts: ['上城区', '下城区', '江干区', '拱墅区', '西湖区', '滨江区', '萧山区', '余杭区', '富阳区', '临安区'] }, { name: '宁波市', districts: ['海曙区', '江北区', '北仑区', '镇海区', '鄞州区', '奉化区'] }, { name: '温州市', districts: ['鹿城区', '龙湾区', '瓯海区', '洞头区', '瑞安市', '乐清市'] }] },
      { name: '江苏省', cities: [{ name: '南京市', districts: ['玄武区', '秦淮区', '建邺区', '鼓楼区', '浦口区', '栖霞区', '雨花台区', '江宁区'] }, { name: '苏州市', districts: ['姑苏区', '虎丘区', '吴中区', '相城区', '吴江区', '昆山市', '太仓市'] }, { name: '无锡市', districts: ['梁溪区', '锡山区', '惠山区', '滨湖区', '新吴区', '江阴市', '宜兴市'] }] },
      { name: '四川省', cities: [{ name: '成都市', districts: ['锦江区', '青羊区', '金牛区', '武侯区', '成华区', '龙泉驿区', '青白江区', '新都区', '温江区', '双流区'] }, { name: '绵阳市', districts: ['涪城区', '游仙区', '安州区'] }, { name: '德阳市', districts: ['旌阳区', '罗江区', '广汉市', '什邡市', '绵竹市'] }] },
      { name: '山东省', cities: [{ name: '济南市', districts: ['历下区', '市中区', '槐荫区', '天桥区', '历城区', '长清区'] }, { name: '青岛市', districts: ['市南区', '市北区', '黄岛区', '崂山区', '李沧区', '城阳区', '即墨区'] }, { name: '烟台市', districts: ['芝罘区', '福山区', '牟平区', '莱山区'] }] },
      { name: '河南省', cities: [{ name: '郑州市', districts: ['中原区', '二七区', '管城回族区', '金水区', '上街区', '惠济区'] }, { name: '洛阳市', districts: ['老城区', '西工区', '瀍河回族区', '涧西区', '吉利区', '洛龙区'] }] },
      { name: '湖北省', cities: [{ name: '武汉市', districts: ['江岸区', '江汉区', '硚口区', '汉阳区', '武昌区', '青山区', '洪山区', '东西湖区', '蔡甸区', '江夏区'] }, { name: '宜昌市', districts: ['西陵区', '伍家岗区', '点军区', '猇亭区', '夷陵区'] }] },
      { name: '湖南省', cities: [{ name: '长沙市', districts: ['芙蓉区', '天心区', '岳麓区', '开福区', '雨花区', '望城区', '长沙县'] }, { name: '株洲市', districts: ['荷塘区', '芦淞区', '石峰区', '天元区'] }] },
      { name: '福建省', cities: [{ name: '福州市', districts: ['鼓楼区', '台江区', '仓山区', '马尾区', '晋安区'] }, { name: '厦门市', districts: ['思明区', '湖里区', '集美区', '海沧区', '同安区', '翔安区'] }] },
      { name: '云南省', cities: [{ name: '昆明市', districts: ['五华区', '盘龙区', '官渡区', '西山区', '东川区', '呈贡区'] }, { name: '大理白族自治州', districts: ['大理市', '祥云县', '宾川县'] }, { name: '丽江市', districts: ['古城区', '玉龙县', '永胜县'] }] },
      { name: '贵州省', cities: [{ name: '贵阳市', districts: ['南明区', '云岩区', '花溪区', '乌当区', '白云区', '观山湖区'] }, { name: '遵义市', districts: ['红花岗区', '汇川区', '播州区'] }] },
      { name: '陕西省', cities: [{ name: '西安市', districts: ['新城区', '碑林区', '莲湖区', '灞桥区', '未央区', '雁塔区'] }, { name: '咸阳市', districts: ['秦都区', '杨陵区', '渭城区'] }] },
      { name: '甘肃省', cities: [{ name: '兰州市', districts: ['城关区', '七里河区', '西固区', '安宁区', '红古区'] }] },
      { name: '黑龙江省', cities: [{ name: '哈尔滨市', districts: ['道里区', '南岗区', '道外区', '平房区', '松北区', '香坊区'] }] },
      { name: '吉林省', cities: [{ name: '长春市', districts: ['南关区', '宽城区', '朝阳区', '二道区', '绿园区', '双阳区'] }] },
      { name: '辽宁省', cities: [{ name: '沈阳市', districts: ['和平区', '沈河区', '大东区', '皇姑区', '铁西区', '苏家屯区', '浑南区'] }, { name: '大连市', districts: ['中山区', '西岗区', '沙河口区', '甘井子区', '旅顺口区'] }] },
      { name: '河北省', cities: [{ name: '石家庄市', districts: ['长安区', '桥西区', '新华区', '井陉矿区', '裕华区'] }, { name: '唐山市', districts: ['路南区', '路北区', '古冶区', '开平区', '丰南区', '丰润区'] }] },
      { name: '山西省', cities: [{ name: '太原市', districts: ['小店区', '迎泽区', '杏花岭区', '尖草坪区', '万柏林区', '晋源区'] }] },
      { name: '安徽省', cities: [{ name: '合肥市', districts: ['瑶海区', '庐阳区', '蜀山区', '包河区'] }, { name: '芜湖市', districts: ['镜湖区', '弋江区', '鸠江区', '湾沚区', '繁昌区'] }] },
      { name: '江西省', cities: [{ name: '南昌市', districts: ['东湖区', '西湖区', '青云谱区', '青山湖区', '新建区', '红谷滩区'] }] },
      { name: '海南省', cities: [{ name: '海口市', districts: ['秀英区', '龙华区', '琼山区', '美兰区'] }, { name: '三亚市', districts: ['海棠区', '吉阳区', '天涯区', '崖州区'] }] },
      { name: '广西壮族自治区', cities: [{ name: '南宁市', districts: ['兴宁区', '青秀区', '江南区', '西乡塘区', '良庆区', '邕宁区'] }] },
      { name: '内蒙古自治区', cities: [{ name: '呼和浩特市', districts: ['新城区', '回民区', '玉泉区', '赛罕区'] }] },
      { name: '新疆维吾尔自治区', cities: [{ name: '乌鲁木齐市', districts: ['天山区', '沙依巴克区', '新市区', '水磨沟区', '头屯河区', '达坂城区', '米东区'] }] },
      { name: '西藏自治区', cities: [{ name: '拉萨市', districts: ['城关区', '堆龙德庆区', '达孜区'] }] },
      { name: '宁夏回族自治区', cities: [{ name: '银川市', districts: ['兴庆区', '西夏区', '金凤区'] }] },
      { name: '青海省', cities: [{ name: '西宁市', districts: ['城东区', '城中区', '城西区', '城北区'] }] }
    ]
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
    this.setData({ showRegionPicker: true, currentStep: 0, selectedProvince: '', selectedCity: '', selectedDistrict: '' })
  },

  getCurrentCities() {
    const province = this.data.selectedProvince
    const found = this.data.provinces.find(p => p.name === province)
    return found ? found.cities : []
  },

  getCurrentDistricts() {
    const province = this.data.selectedProvince
    const city = this.data.selectedCity
    const found = this.data.provinces.find(p => p.name === province)
    if (found) {
      const cityObj = found.cities.find(c => c.name === city)
      return cityObj ? cityObj.districts : []
    }
    return []
  },

  onProvinceTap(e) {
    const name = e.currentTarget.dataset.name
    const cities = this.getCurrentCities()
    this.setData({ selectedProvince: name, currentStep: 1, currentCities: cities, currentDistricts: [] })
  },

  onCityTap(e) {
    const name = e.currentTarget.dataset.name
    const districts = this.getCurrentDistricts()
    this.setData({ selectedCity: name, currentStep: 2, currentDistricts: districts })
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
