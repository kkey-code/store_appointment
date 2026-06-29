# 门店预约管理系统

这是一个门店预约管理项目，包含 Spring Boot 后端、管理端前端、客户/库存前端，以及便携运行配置。

## 项目结构

```text
store_appointment/
├─ store_appointment/                 # 后端 Spring Boot 项目
├─ frontend/                          # 管理端前端
├─ frontend_customer_inventory/       # 客户档案 + 库存管理前端
├─ delivery/                          # 便携交付相关文件
└─ README.md                          # 整个项目说明
```

## 技术栈

后端：

- Java 17
- Spring Boot 3.5
- Spring MVC
- MyBatis
- PageHelper
- MySQL
- H2 便携数据库
- Redis
- Spring Cache
- JWT

前端：

- Vue 3
- TypeScript
- Vite
- Element Plus
- Axios
- ECharts

## 功能模块

管理端 `frontend`：

- 登录
- 工作台
- 员工管理
- 客户管理
- 服务项目
- 预约管理
- 订单管理
- 数据统计

客户/库存端 `frontend_customer_inventory`：

- 登录
- 客户档案
- 库存管理

后端接口模块：

- `/api/login`：登录
- `/api/employees`：员工管理
- `/admin/customer`：客户管理
- `/admin/serviceItem`：服务项目管理
- `/admin/appointment`：预约管理
- `/admin/order`：订单管理
- `/admin/inventory`：库存管理
- `/admin/statistics`：数据统计

## 环境要求

本地开发建议准备：

- JDK 17
- Maven
- Node.js
- MySQL 8
- Redis

如果使用便携模式，可以不用 MySQL，后端会用 H2 文件数据库；但 Redis 仍然建议启动，因为项目启用了 Spring Cache。

## 配置文件

后端配置在：

```text
store_appointment/src/main/resources/application.yml
store_appointment/src/main/resources/application-portable.yml
```

现在配置已经整合：

- `application.yml`：公共配置，包括端口、MySQL、Redis、JWT、OSS、微信等。
- `application-portable.yml`：只放便携模式差异，主要是 H2 数据库和初始化 SQL。


## 启动后端

### 方式一：MySQL 模式

先确认 MySQL 和 Redis 已启动，并创建数据库：

```sql
create database if not exists store_appointment default charset utf8mb4;
```

然后启动后端：

```bash
cd store_appointment
mvn spring-boot:run
```

默认后端地址：

```text
http://localhost:8080
```

### 方式二：便携 H2 模式

便携模式默认初始化账号：

```text
admin / 123456
readonly / 123456
```

`admin` 是正常管理员账号，`readonly` 是只读演示账号。

## 启动前端

### 管理端

```bash
cd frontend
npm install
npm run dev
```

访问：

```text
http://localhost:5173
```

### 客户/库存端

如果单独启动：

```bash
cd frontend_customer_inventory
npm install
npm run dev
```

访问：

```text
http://localhost:5173
```

如果两个前端要同时启动，第二个端口要换掉，例如：

```bash
cd frontend_customer_inventory
npm run dev -- --port 5174
```

访问：

```text
http://localhost:5174
```

两个前端的 Vite 代理都指向后端：

```text
/api   -> http://localhost:8080
/admin -> http://localhost:8080
```

## Redis 缓存设计

项目使用 Spring Cache 注解做缓存，不直接在业务代码里手写 Redis 读写逻辑。

核心配置：

```text
store_appointment/src/main/java/com/wkr/store_appointment/config/RedisConfig.java
```

## 常见问题

### 前端页面请求失败

先确认后端是否在 `8080` 端口运行：

```text
http://localhost:8080
```

前端开发环境通过 Vite 代理访问后端，如果后端没启动，页面会报接口错误。

### 两个前端端口冲突

两个前端默认都是 `5173`。同时启动时，第二个用：

```bash
npm run dev -- --port 5174
```

### Redis 连接失败

确认 Redis 已启动，并检查：

本地默认配置是：

```text
localhost:6379
database: 0
```

### MySQL 连接失败

检查：

```text
STORE_DB_HOST
STORE_DB_PORT
STORE_DB_DATABASE
STORE_DB_USERNAME
STORE_DB_PASSWORD
```

如果只是想快速跑项目，可以使用 `portable` 模式走 H2 数据库。



