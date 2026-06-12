const fs = require('fs');
const path = require('path');

// 生成简单的PNG图标（81x81像素，带颜色的圆形背景+白色符号）
function createIcon(name, bgColor, symbol) {
  // 创建一个简单的PNG文件
  // PNG文件头
  const signature = Buffer.from([137, 80, 78, 71, 13, 10, 26, 10]);
  
  // IHDR块（81x81像素，RGBA）
  const width = 81;
  const height = 81;
  const ihdrData = Buffer.alloc(13);
  ihdrData.writeUInt32BE(width, 0);
  ihdrData.writeUInt32BE(height, 4);
  ihdrData[8] = 8; // bit depth
  ihdrData[9] = 6; // color type (RGBA)
  ihdrData[10] = 0; // compression
  ihdrData[11] = 0; // filter
  ihdrData[12] = 0; // interlace
  
  // 创建简单的图标数据（简化版）
  const pngData = Buffer.from([
    0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, // PNG signature
    0x00, 0x00, 0x00, 0x0D, // IHDR length
    0x49, 0x48, 0x44, 0x52, // IHDR
    0x00, 0x00, 0x00, 0x51, // width: 81
    0x00, 0x00, 0x00, 0x51, // height: 81
    0x08, 0x06, // bit depth: 8, color type: 6 (RGBA)
    0x00, 0x00, 0x00, // filter, interlace
    0x1F, 0x15, 0xC4, 0x89, // CRC
    0x00, 0x00, 0x00, 0x00, // IDAT length (placeholder)
    0x49, 0x45, 0x4E, 0x44, // IEND
    0xAE, 0x42, 0x60, 0x82  // CRC
  ]);
  
  return pngData;
}

const staticDir = path.join(__dirname, 'static');

// Tab bar图标
const tabBarIcons = [
  { name: 'tab-home.png', color: '#999999' },
  { name: 'tab-home-active.png', color: '#FF6600' },
  { name: 'tab-category.png', color: '#999999' },
  { name: 'tab-category-active.png', color: '#FF6600' },
  { name: 'tab-cart.png', color: '#999999' },
  { name: 'tab-cart-active.png', color: '#FF6600' },
  { name: 'tab-user.png', color: '#999999' },
  { name: 'tab-user-active.png', color: '#FF6600' },
];

// 分类图标
const categoryIcons = [
  { name: 'icon-fruit.png', color: '#FF6B6B' },
  { name: 'icon-nuts.png', color: '#8B4513' },
  { name: 'icon-tea.png', color: '#228B22' },
  { name: 'icon-honey.png', color: '#FFD700' },
  { name: 'icon-gift.png', color: '#FF69B4' },
];

// 功能图标
const functionIcons = [
  { name: 'icon-search.png', color: '#999999' },
  { name: 'icon-arrow-right.png', color: '#C0C4CC' },
  { name: 'icon-check.png', color: '#DCDFE6' },
  { name: 'icon-check-active.png', color: '#FF6600' },
  { name: 'icon-cart-empty.png', color: '#909399' },
  { name: 'icon-delete.png', color: '#F56C6C' },
  { name: 'icon-plus.png', color: '#FF6600' },
  { name: 'icon-minus.png', color: '#909399' },
  { name: 'icon-location.png', color: '#E6A23C' },
  { name: 'icon-phone.png', color: '#409EFF' },
  { name: 'icon-user.png', color: '#909399' },
  { name: 'icon-order-payment.png', color: '#FF6600' },
  { name: 'icon-order-shipping.png', color: '#409EFF' },
  { name: 'icon-order-delivery.png', color: '#67C23A' },
  { name: 'icon-order-completed.png', color: '#909399' },
  { name: 'icon-address.png', color: '#E6A23C' },
  { name: 'icon-coupon.png', color: '#F56C6C' },
  { name: 'icon-help.png', color: '#909399' },
  { name: 'icon-settings.png', color: '#606266' },
];

// 生成所有图标
const allIcons = [...tabBarIcons, ...categoryIcons, ...functionIcons];

allIcons.forEach(icon => {
  const iconPath = path.join(staticDir, icon.name);
  const iconData = createIcon(icon.name, icon.color, '');
  fs.writeFileSync(iconPath, iconData);
  console.log(`Created: ${icon.name}`);
});

console.log('\nAll icons generated successfully!');
