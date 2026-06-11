// 创建小程序所有图标的脚本
// 运行方式: node create-icons.js

const fs = require('fs');
const path = require('path');

// 基础PNG文件头 (1x1 像素透明PNG)
const basePng = Buffer.from([
    0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,  // PNG signature
    0x00, 0x00, 0x00, 0x0D,  // IHDR length
    0x49, 0x48, 0x44, 0x52,  // IHDR
    0x00, 0x00, 0x00, 0x01,  // width: 1
    0x00, 0x00, 0x00, 0x01,  // height: 1
    0x08, 0x06,  // bit depth: 8, color type: 6 (RGBA)
    0x00, 0x00, 0x00,  // filter, interlace
    0x1F, 0x15, 0xC4, 0x89,  // CRC
    0x00, 0x00, 0x00, 0x0A,  // IDAT length
    0x49, 0x44, 0x41, 0x54,  // IDAT
    0x78, 0x9C, 0x62, 0x00, 0x00, 0x00, 0x02, 0x00, 0x01,  // compressed data
    0xE5, 0x27, 0xDE, 0xFC,  // CRC
    0x00, 0x00, 0x00, 0x00,  // IEND length
    0x49, 0x45, 0x4E, 0x44,  // IEND
    0xAE, 0x42, 0x60, 0x82   // CRC
]);

// 需要创建的图标列表
const icons = [
    // 分类图标
    { name: 'icon-fruit.png', color: '#FF6B6B' },
    { name: 'icon-nuts.png', color: '#8B4513' },
    { name: 'icon-tea.png', color: '#228B22' },
    { name: 'icon-honey.png', color: '#FFD700' },
    { name: 'icon-gift.png', color: '#FF69B4' },
    
    // 用户页面图标
    { name: 'icon-order-payment.png', color: '#FF6600' },
    { name: 'icon-order-shipping.png', color: '#409EFF' },
    { name: 'icon-order-delivery.png', color: '#67C23A' },
    { name: 'icon-order-completed.png', color: '#909399' },
    { name: 'icon-address.png', color: '#E6A23C' },
    { name: 'icon-coupon.png', color: '#F56C6C' },
    { name: 'icon-help.png', color: '#909399' },
    { name: 'icon-settings.png', color: '#606266' },
    
    // 购物车图标
    { name: 'icon-cart-empty.png', color: '#909399' },
    { name: 'icon-check.png', color: '#DCDFE6' },
    { name: 'icon-check-active.png', color: '#FF6600' },
    
    // 其他图标
    { name: 'icon-search.png', color: '#909399' },
    { name: 'icon-arrow-right.png', color: '#C0C4CC' },
    { name: 'icon-delete.png', color: '#F56C6C' },
    { name: 'icon-plus.png', color: '#FF6600' },
    { name: 'icon-minus.png', color: '#909399' },
    { name: 'icon-location.png', color: '#E6A23C' },
    { name: 'icon-phone.png', color: '#409EFF' },
    { name: 'icon-user.png', color: '#909399' },
];

// 创建图标目录
const staticDir = path.join(__dirname, 'static');
if (!fs.existsSync(staticDir)) {
    fs.mkdirSync(staticDir, { recursive: true });
}

// 创建所有图标
icons.forEach(icon => {
    const iconPath = path.join(staticDir, icon.name);
    fs.writeFileSync(iconPath, basePng);
    console.log(`Created: ${icon.name}`);
});

console.log('\nAll icons created successfully!');
