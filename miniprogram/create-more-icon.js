const { createCanvas } = require('canvas');
const fs = require('fs');
const path = require('path');

function createMoreIcon() {
  const size = 81;
  const canvas = createCanvas(size, size);
  const ctx = canvas.getContext('2d');
  
  // 绘制圆形背景
  ctx.beginPath();
  ctx.arc(size/2, size/2, size/2 - 2, 0, Math.PI * 2);
  ctx.fillStyle = '#FF6600';
  ctx.fill();
  
  // 绘制三个点（表示更多）
  ctx.fillStyle = '#FFFFFF';
  const dotRadius = 6;
  const dotY = size/2;
  
  // 左边的点
  ctx.beginPath();
  ctx.arc(size/2 - 15, dotY, dotRadius, 0, Math.PI * 2);
  ctx.fill();
  
  // 中间的点
  ctx.beginPath();
  ctx.arc(size/2, dotY, dotRadius, 0, Math.PI * 2);
  ctx.fill();
  
  // 右边的点
  ctx.beginPath();
  ctx.arc(size/2 + 15, dotY, dotRadius, 0, Math.PI * 2);
  ctx.fill();
  
  // 绘制箭头
  ctx.fillStyle = '#FFFFFF';
  ctx.font = 'bold 20px Arial';
  ctx.textAlign = 'center';
  ctx.textBaseline = 'middle';
  ctx.fillText('>', size/2, size/2 + 20);
  
  const buffer = canvas.toBuffer('image/png');
  fs.writeFileSync(path.join(__dirname, 'static', 'icon-more.png'), buffer);
  console.log('Created: icon-more.png');
}

createMoreIcon();
