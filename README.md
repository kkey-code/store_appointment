# 门店预约管理系统

门店预约管理系统是一个面向门店日常运营的管理项目，包含 Spring Boot 后端、管理端前端、客户/库存前端，以及便携运行配置。

系统主要覆盖门店的员工管理、客户管理、服务项目、预约管理、订单管理、库存管理和数据统计等业务场景，适合作为 Java 后端项目、毕业设计项目或门店管理系统原型。

## 项目组成

项目分为四个部分：

* **后端服务**：提供登录、员工、客户、服务项目、预约、订单、库存、统计等接口。
* **管理端前端**：面向门店管理员，完成日常业务管理。
* **客户/库存前端**：用于客户档案维护和库存管理。
* **便携运行配置**：支持使用 H2 文件数据库快速运行项目，方便演示和交付。

## 技术栈

### 后端

`Java 17`、`Spring Boot 3.5`、`Spring MVC`、`MyBatis`、`PageHelper`、`JWT`

### 数据库与缓存

`MySQL 8`、`H2 文件数据库`、`Redis`、`Spring Cache`

### 前端

`Vue 3`、`TypeScript`、`Vite`、`Element Plus`、`Axios`、`ECharts`

### 运行环境

`JDK 17`、`Maven`、`Node.js`

## 功能模块

### 管理端 frontend

* 登录
* 工作台
* 员工管理
* 客户管理
* 服务项目
* 预约管理
* 订单管理
* 数据统计

### 客户/库存端 frontend_customer_inventory

* 登录
* 客户档案
* 库存管理

### 后端接口模块

* `/api/login`：登录
* `/api/employees`：员工管理
* `/admin/customer`：客户管理
* `/admin/serviceItem`：服务项目管理
* `/admin/appointment`：预约管理
* `/admin/order`：订单管理
* `/admin/inventory`：库存管理
* `/admin/statistics`：数据统计

## 项目结构

```text
store_appointment/
├─ store_appointment/                 # 后端 Spring Boot 项目
├─ frontend/                          # 管理端前端
├─ frontend_customer_inventory/       # 客户档案 + 库存管理前端
├─ delivery/                          # 便携交付相关文件
├─ README.md                          # 项目说明文档
└─ start-admin.bat                    # Windows 管理端一键启动脚本
```

## 环境要求

本地开发建议准备：

* JDK 17
* Maven
* Node.js
* MySQL 8
* Redis

如果使用便携模式，可以不安装 MySQL，后端会使用 H2 文件数据库。

Redis 仍建议启动，因为项目启用了 Spring Cache。如果 Redis 没有启动，缓存相关配置可能会连接失败。

## 配置文件

后端配置文件位于：

```text
store_appointment/src/main/resources/application.yml
store_appointment/src/main/resources/application-portable.yml
```

便携模式配置文件，主要包含：

* H2 文件数据库配置
* 初始化 SQL 配置
* 便携模式下的数据库连接差异

## 快速启动

### Windows 一键启动管理端

在项目根目录下双击运行：

```text
start-admin.bat
```

脚本会自动完成以下操作：

1. 检查 Java、Maven、Node.js 是否已安装；
2. 检查 `8080` 和 `5173` 端口是否被占用；
3. 以便携 H2 模式启动 Spring Boot 后端；
4. 自动安装管理端前端依赖；
5. 启动管理端前端；
6. 自动打开浏览器访问管理端页面。

默认访问地址：

```text
http://localhost:5173
```

便携模式默认账号：

```text
admin / 123456
readonly / 123456
```

账号说明：

* `admin`：管理员账号，可正常使用系统功能。
* `readonly`：只读演示账号，适合演示查看。

## 后端启动

后端支持 MySQL 模式和便携 H2 模式。

### 方式一：MySQL 模式

先确认 MySQL 和 Redis 已启动。

创建数据库：

```sql
CREATE DATABASE IF NOT EXISTS store_appointment
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

启动后端：

```bash
cd store_appointment
mvn spring-boot:run
```

默认后端地址：

```text
http://localhost:8080
```

### 方式二：便携 H2 模式

便携模式使用 H2 文件数据库，适合快速演示、交付和本地运行。

启动命令：

```bash
cd store_appointment
mvn spring-boot:run -Dspring-boot.run.profiles=portable
```

默认账号：

```text
admin / 123456
readonly / 123456
```

## 前端启动

项目包含两个前端：

* `frontend`：管理端前端
* `frontend_customer_inventory`：客户档案 + 库存管理前端

### 管理端 frontend

```bash
cd frontend
npm install
npm run dev
```

访问地址：

```text
http://localhost:5173
```

### 客户/库存端 frontend_customer_inventory

```bash
cd frontend_customer_inventory
npm install
npm run dev
```

访问地址：

```text
http://localhost:5173
```

如果两个前端需要同时启动，第二个前端需要换端口：

```bash
cd frontend_customer_inventory
npm run dev -- --port 5174
```

访问地址：

```text
http://localhost:5174
```

## 前端代理配置

两个前端的 Vite 代理都指向后端服务：

```text
/api   -> http://localhost:8080
/admin -> http://localhost:8080
```

因此前端页面请求失败时，应优先检查后端是否已经启动。

## Redis 缓存设计

项目使用 Spring Cache 注解完成缓存管理，不在业务代码中直接手写 Redis 读写逻辑。

核心配置文件：

```text
store_appointment/src/main/java/com/wkr/store_appointment/config/RedisConfig.java
```

缓存设计思路：

* 在查询频率较高的数据接口上使用缓存；
* 通过 Spring Cache 统一管理缓存读写；
* 业务代码不直接依赖 Redis API；
* 后续更换缓存实现时，对业务层影响较小。

## 常见问题

### 1. 前端页面请求失败

先确认后端是否已经运行：

```text
http://localhost:8080
```

如果后端未启动，前端页面会出现接口请求失败。

### 2. 两个前端端口冲突

管理端和客户/库存端默认都使用 `5173` 端口。

如果两个前端同时启动，第二个前端请使用其他端口：

```bash
npm run dev -- --port 5174
```

### 3. Redis 连接失败

确认 Redis 已启动，并检查本地配置：

```text
host: localhost
port: 6379
database: 0
```

如果只是演示项目，可以先启动 Redis，再启动后端。

### 4. MySQL 连接失败

检查数据库连接配置或环境变量：

```text
STORE_DB_HOST
STORE_DB_PORT
STORE_DB_DATABASE
STORE_DB_USERNAME
STORE_DB_PASSWORD
```

同时确认数据库已经创建：

```sql
CREATE DATABASE IF NOT EXISTS store_appointment
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

如果只是想快速运行项目，建议使用便携 H2 模式。



## 项目特点

* 前后端分离，后端接口模块清晰；
* 支持管理端和客户/库存端两个前端；
* 支持 MySQL 开发模式和 H2 便携模式；
* 使用 Redis + Spring Cache 进行缓存管理；
* 提供 Windows 一键启动脚本，方便演示和交付；
* 业务模块覆盖门店管理中的核心流程。

