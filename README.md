# 土特产商城微信小程序

企业级土特产商城微信小程序 + SpringBoot + Vue后台管理系统

## 项目介绍

这是一个完整的电商解决方案，包含微信小程序前端、SpringBoot后端API和Vue3后台管理系统。支持商品管理、订单管理、用户管理、优惠券、轮播图、分类管理等完整电商功能。

## 技术栈

| 模块 | 技术 |
|------|------|
| 小程序前端 | 微信小程序原生开发 |
| 后台管理前端 | Vue3 + Element Plus |
| 后端框架 | Spring Boot 2.7.18 |
| ORM框架 | MyBatis Plus |
| 数据库 | MySQL / MariaDB |
| 缓存 | Redis |
| 构建工具 | Maven |

## 项目结构

```
mine-commerce/
├── mine-commerce-common/       # 公共模块（工具类、异常处理）
├── mine-commerce-model/        # 实体类模块
├── mine-commerce-mapper/       # Mapper接口模块
├── mine-commerce-service/      # Service业务逻辑层
├── mine-commerce-api/          # 小程序API接口
├── mine-commerce-admin/        # 后台管理API接口
├── mine-commerce-app/          # 启动模块
├── admin/                      # Vue3后台管理前端
├── miniprogram/                # 微信小程序
└── docs/                       # 项目文档
```

## 功能特性

### 微信小程序端
- **首页**：轮播图、分类导航、推荐商品
- **分类页**：多级分类、商品筛选
- **商品详情**：规格选择、加入购物车
- **购物车**：商品管理、数量修改、全选
- **订单**：创建订单、支付、取消、确认收货
- **个人中心**：用户信息、订单管理、收货地址
- **微信登录**：一键登录

### 后台管理系统
- **商品管理**：商品CRUD、图片上传、分类管理
- **订单管理**：订单列表、状态筛选、发货操作
- **用户管理**：用户列表、状态管理
- **优惠券管理**：优惠券CRUD
- **轮播图管理**：轮播图CRUD
- **分类管理**：多级分类CRUD

## 快速开始

### 环境要求
- JDK 11+
- Maven 3.6+
- Node.js 14+
- MySQL 8.0+ 或 MariaDB 10.5+
- Redis

### 1. 数据库初始化

```sql
-- 创建数据库
CREATE DATABASE mine_commerce DEFAULT CHARACTER SET utf8mb4;

-- 执行初始化脚本
mysql -u root -p mine_commerce < mine-commerce-app/src/main/resources/schema.sql

-- 插入测试数据
mysql -u root -p mine_commerce < restore-data.sql
```

### 2. 启动后端服务

```bash
cd mine-commerce

# 修改数据库配置（mine-commerce-app/src/main/resources/application.yml）
# spring.datasource.url: jdbc:mysql://localhost:3306/mine_commerce
# spring.datasource.username: root
# spring.datasource.password: 你的密码

# 构建并启动
mvn clean package -DskipTests
java -jar mine-commerce-app/target/mine-commerce-app-1.0.0-SNAPSHOT.jar
```

后端服务默认运行在 `http://localhost:8080`

### 3. 启动后台管理前端

```bash
cd admin
npm install
npm run dev
```

后台管理默认运行在 `http://localhost:3001`

**默认账号：**
- 用户名：admin
- 密码：admin123

### 4. 小程序配置

1. 使用微信开发者工具打开 `miniprogram` 目录
2. 在 `app.js` 中修改 `baseUrl` 为你的后端地址
3. 在微信开发者工具中勾选「不校验合法域名」
4. 编译运行

## 数据库表结构

| 表名 | 说明 |
|------|------|
| tb_user | 用户表 |
| tb_address | 收货地址表 |
| tb_category | 商品分类表 |
| tb_brand | 品牌表 |
| tb_spu | 商品SPU表 |
| tb_sku | 商品SKU表 |
| tb_order | 订单表 |
| tb_order_item | 订单明细表 |
| tb_coupon | 优惠券表 |
| tb_cart | 购物车表 |
| tb_evaluate | 评价表 |
| tb_banner | 轮播图表 |
| tb_admin | 管理员表 |

## API接口

### 小程序端
| 接口 | 方法 | 说明 |
|------|------|------|
| /api/user/wx-login | POST | 微信登录 |
| /api/product/list | GET | 商品列表 |
| /api/product/{id} | GET | 商品详情 |
| /api/category/list | GET | 分类列表 |
| /api/banner/list | GET | 轮播图列表 |
| /api/cart/list | GET | 购物车列表 |
| /api/order | POST | 创建订单 |
| /api/address/list | GET | 地址列表 |

### 后台管理端
| 接口 | 方法 | 说明 |
|------|------|------|
| /admin/auth/login | POST | 管理员登录 |
| /admin/product/spu/list | GET | 商品列表 |
| /admin/order/list | GET | 订单列表 |
| /admin/user/list | GET | 用户列表 |
| /admin/banner/list | GET | 轮播图列表 |

## 常见问题

### Q: 小程序无法连接后端？
A: 在微信开发者工具中勾选「不校验合法域名」

### Q: 图片无法显示？
A: 确保后端已重启，且图片URL路径正确

### Q: 数据库连接失败？
A: 检查 `application.yml` 中的数据库配置

## 开发说明

- 后端每次修改代码后需要重新构建：`mvn clean package -DskipTests`
- 小程序图片URL需要拼接完整地址（如 `http://localhost:8080/upload/xxx.png`）
- 开发环境模拟器无法调用 `wx.login()`，使用默认账号登录

## License

MIT
