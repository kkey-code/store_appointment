# 门店预约管理系统

这是一个门店预约管理项目，包含 Spring Boot 后端、管理端前端、客户/库存前端，以及便携运行配置。

## 项目结构

```text
store_appointment/
├─ store_appointment/                 # 后端 Spring Boot 项目
├─ frontend/                          # 管理端前端
├─ frontend_customer_inventory/       # 客户档案 + 库存管理前端
├─ delivery/                          # 便携交付相关文件
├─ README.md                          # 整个项目说明
└─ 简历项目描述.md                    # 简历项目描述
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

已经删除的旧配置：

```text
application-dev.yml
application-demo.yml
application-dev.yml.example
```

常用环境变量：

```text
STORE_PORT=8080
STORE_DB_HOST=localhost
STORE_DB_PORT=3306
STORE_DB_DATABASE=store_appointment
STORE_DB_USERNAME=root
STORE_DB_PASSWORD=12345678
STORE_REDIS_HOST=localhost
STORE_REDIS_PORT=6379
STORE_REDIS_PASSWORD=
STORE_REDIS_DATABASE=0
STORE_JWT_ADMIN_SECRET_KEY=itcast
STORE_JWT_ADMIN_TTL=7200000
STORE_JWT_ADMIN_TOKEN_NAME=token
```

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

便携模式不依赖 MySQL，适合演示和联调：

```bash
cd store_appointment
mvn spring-boot:run -Dspring-boot.run.profiles=portable
```

也可以打包后运行：

```bash
cd store_appointment
mvn -DskipTests package
java -jar target/store_appointment-0.0.1-SNAPSHOT.jar --spring.profiles.active=portable
```

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

所以联调顺序建议是：

```text
先启动 Redis
再启动后端
最后启动前端
```

## Redis 缓存设计

项目使用 Spring Cache 注解做缓存，不直接在业务代码里手写 Redis 读写逻辑。

核心配置：

```text
store_appointment/src/main/java/com/wkr/store_appointment/config/RedisConfig.java
```

已经缓存的内容：

```text
statistics:overview       数据统计总览
statistics:orderAmount    订单金额统计
customer:page             客户分页
customer:detail           客户详情
employee:list             员工分页
employee:detail           员工详情
serviceItem:page          服务项目分页
serviceItem:detail        服务项目详情
inventoryItem:page        库存分页
inventoryItem:detail      库存详情
appointment:page          预约分页
appointment:getById       预约详情
order:page                订单分页
order:getById             订单详情
```

缓存原则：

- 统计类数据适合优先缓存，例如 `overview`、`orderAmount`。
- 服务项目、员工、客户、库存列表这类低频变动数据可以缓存。
- 新增、修改、删除、状态变更后要清理相关缓存。
- 库存、订单、预约这类强一致业务不要把 Redis 当主数据源，数据库仍然是主数据源。
- Redis 在这里主要做查询加速，不负责保存最终业务事实。

分页缓存注意点：

- PageHelper 的 `Page` 对象不要直接放进 Redis。
- 业务返回前要转成普通 `ArrayList`，否则反序列化容易出问题。

## 测试和构建

后端测试：

```bash
cd store_appointment
mvn test
```

后端打包：

```bash
cd store_appointment
mvn -DskipTests package
```

管理端构建：

```bash
cd frontend
npm run build
```

客户/库存端构建：

```bash
cd frontend_customer_inventory
npm run build
```

最近一次联调验证结果：

- 后端测试通过。
- 管理端前端构建通过。
- 客户/库存端前端构建通过。
- 登录、统计、客户、员工、服务项目、库存、预约、订单接口流程通过。
- 管理端页面联调正常：工作台、员工、客户、服务项目、预约、订单、统计。
- 客户/库存端页面联调正常：客户档案、库存管理。

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

```text
STORE_REDIS_HOST
STORE_REDIS_PORT
STORE_REDIS_PASSWORD
STORE_REDIS_DATABASE
```

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

### 构建提示 chunk 过大

前端构建时 Vite 可能提示部分 chunk 超过 `500 kB`。这是构建体积提醒，不是构建失败。

后续可以用路由懒加载、手动分包等方式优化。

## 开发建议

新增缓存时按这个顺序做：

1. 先判断数据是否适合缓存。
2. 查询方法加 `@Cacheable`。
3. 新增、修改、删除方法加 `@CacheEvict` 或 `@Caching`。
4. 分页结果不要缓存框架内部对象，返回普通集合。
5. 启动后端和前端做一次真实联调。

适合优先缓存：

- 统计总览
- 金额趋势
- 服务项目列表
- 员工列表
- 客户分页
- 库存分页

不建议一开始用 Redis 做主数据源：

- 库存扣减
- 订单状态
- 预约状态
- 支付状态

这些数据要以数据库为准，Redis 只做查询缓存。
