const { createCanvas } = require('canvas');
const fs = require('fs');
const path = require('path');

const staticDir = path.join(__dirname, 'static');

// 创建圆形图标
function createCircleIcon(filename, bgColor, iconText) {
  const size = 81;
  const canvas = createCanvas(size, size);
  const ctx = canvas.getContext('2d');
  
  // 绘制圆形背景
  ctx.beginPath();
  ctx.arc(size/2, size/2, size/2 - 2, 0, Math.PI * 2);
  ctx.fillStyle = bgColor;
  ctx.fill();
  
  // 绘制图标文字
  ctx.fillStyle = '#FFFFFF';
  ctx.font = 'bold 36px Arial';
  ctx.textAlign = 'center';
  ctx.textBaseline = 'middle';
  ctx.fillText(iconText, size/2, size/2);
  
  // 保存为PNG
  const buffer = canvas.toBuffer('image/png');
  fs.writeFileSync(path.join(staticDir, filename), buffer);
  console.log(`Created: ${filename}`);
}

// 创建方形图标
function createSquareIcon(filename, bgColor, iconText) {
  const size = 81;
  const canvas = createCanvas(size, size);
  const ctx = canvas.getContext('2d');
  
  // 绘制圆角矩形背景
  const radius = 12;
  ctx.beginPath();
  ctx.moveTo(radius, 0);
  ctx.lineTo(size - radius, 0);
  ctx.quadraticCurveTo(size, 0, size, radius);
  ctx.lineTo(size, size - radius);
  ctx.quadraticCurveTo(size, size, size - radius, size);
  ctx.lineTo(radius, size);
  ctx.quadraticCurveTo(0, size, 0, size - radius);
  ctx.lineTo(0, radius);
  ctx.quadraticCurveTo(0, 0, radius, 0);
  ctx.closePath();
  ctx.fillStyle = bgColor;
  ctx.fill();
  
  // 绘制图标文字
  ctx.fillStyle = '#FFFFFF';
  ctx.font = 'bold 28px Arial';
  ctx.textAlign = 'center';
  ctx.textBaseline = 'middle';
  ctx.fillText(iconText, size/2, size/2);
  
  const buffer = canvas.toBuffer('image/png');
  fs.writeFileSync(path.join(staticDir, filename), buffer);
  console.log(`Created: ${filename}`);
}

// TabBar图标 - 使用圆形+文字
const tabBarIcons = [
  { name: 'tab-home.png', bg: '#CCCCCC', text: '首' },
  { name: 'tab-home-active.png', bg: '#FF6600', text: '首' },
  { name: 'tab-category.png', bg: '#CCCCCC', text: '分' },
  { name: 'tab-category-active.png', bg: '#FF6600', text: '分' },
  { name: 'tab-cart.png', bg: '#CCCCCC', text: '购' },
  { name: 'tab-cart-active.png', bg: '#FF6600', text: '购' },
  { name: 'tab-user.png', bg: '#CCCCCC', text: '我' },
  { name: 'tab-user-active.png', bg: '#FF6600', text: '我' },
];

// 分类图标 - 使用圆形+emoji
const categoryIcons = [
  { name: 'icon-fruit.png', bg: '#FF6B6B', text: '🍎' },
  { name: 'icon-nuts.png', bg: '#8B6914', text: '🥜' },
  { name: 'icon-tea.png', bg: '#228B22', text: '🍵' },
  { name: 'icon-honey.png', bg: '#FFB300', text: '🍯' },
  { name: 'icon-gift.png', bg: '#FF69B4', text: '🎁' },
];

// 功能图标 - 使用方形
const functionIcons = [
  { name: 'icon-search.png', bg: '#999999', text: '搜' },
  { name: 'icon-arrow-right.png', bg: 'transparent', text: '>' },
  { name: 'icon-check.png', bg: '#DCDFE6', text: '✓' },
  { name: 'icon-check-active.png', bg: '#FF6600', text: '✓' },
  { name: 'icon-cart-empty.png', bg: '#F5F5F5', text: '🛒' },
  { name: 'icon-delete.png', bg: '#F56C6C', text: '×' },
  { name: 'icon-plus.png', bg: '#FF6600', text: '+' },
  { name: 'icon-minus.png', bg: '#909399', text: '-' },
  { name: 'icon-location.png', bg: '#E6A23C', text: '📍' },
  { name: 'icon-phone.png', bg: '#409EFF', text: '📞' },
  { name: 'icon-user.png', bg: '#909399', text: '👤' },
  { name: 'icon-order-payment.png', bg: '#FF6600', text: '💰' },
  { name: 'icon-order-shipping.png', bg: '#409EFF', text: '📦' },
  { name: 'icon-order-delivery.png', bg: '#67C23A', text: '🚚' },
  { name: 'icon-order-completed.png', bg: '#909399', text: '✅' },
  { name: 'icon-address.png', bg: '#E6A23C', text: '📍' },
  { name: 'icon-coupon.png', bg: '#F56C6C', text: '🎫' },
  { name: 'icon-help.png', bg: '#909399', text: '❓' },
  { name: 'icon-settings.png', bg: '#606266', text: '⚙' },
];

// 生成所有图标
console.log('Generating tab bar icons...');
tabBarIcons.forEach(icon => createCircleIcon(icon.name, icon.bg, icon.text));

console.log('\nGenerating category icons...');
categoryIcons.forEach(icon => createCircleIcon(icon.name, icon.bg, icon.text));

console.log('\nGenerating function icons...');
functionIcons.forEach(icon => createSquareIcon(icon.name, icon.bg, icon.text));

// 创建默认头像
function createDefaultAvatar() {
  const size = 120;
  const canvas = createCanvas(size, size);
  const ctx = canvas.getContext('2d');
  
  // 灰色圆形背景
  ctx.beginPath();
  ctx.arc(size/2, size/2, size/2, 0, Math.PI * 2);
  ctx.fillStyle = '#E0E0E0';
  ctx.fill();
  
  // 白色用户图标
  ctx.fillStyle = '#FFFFFF';
  ctx.font = 'bold 60px Arial';
  ctx.textAlign = 'center';
  ctx.textBaseline = 'middle';
  ctx.fillText('👤', size/2, size/2);
  
  const buffer = canvas.toBuffer('image/png');
  fs.writeFileSync(path.join(staticDir, 'default-avatar.png'), buffer);
  console.log('Created: default-avatar.png');
}

// 创建轮播图
function createBanner(filename, color1, color2, text) {
  const width = 750;
  const height = 300;
  const canvas = createCanvas(width, height);
  const ctx = canvas.getContext('2d');
  
  // 渐变背景
  const gradient = ctx.createLinearGradient(0, 0, width, height);
  gradient.addColorStop(0, color1);
  gradient.addColorStop(1, color2);
  ctx.fillStyle = gradient;
  ctx.fillRect(0, 0, width, height);
  
  // 文字
  ctx.fillStyle = '#FFFFFF';
  ctx.font = 'bold 48px Arial';
  ctx.textAlign = 'center';
  ctx.textBaseline = 'middle';
  ctx.fillText(text, width/2, height/2);
  
  const buffer = canvas.toBuffer('image/png');
  fs.writeFileSync(path.join(staticDir, filename), buffer);
  console.log(`Created: ${filename}`);
}

console.log('\nGenerating default avatar...');
createDefaultAvatar();

console.log('\nGenerating banners...');
createBanner('banner1.png', '#FF6600', '#FF8C00', '精选土特产');
createBanner('banner2.png', '#4CAF50', '#8BC34A', '新鲜水果');
createBanner('banner3.png', '#2196F3', '#03A9F4', '地道美味');

console.log('\nAll icons generated successfully!');
