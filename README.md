# 门店预约与库存管理系统

这是一个面向线下门店经营场景的后台管理系统，采用 Spring Boot + Vue 前后端分离架构。项目覆盖员工、客户、服务项目、预约、订单收款、库存、经营统计、登录认证、只读演示账号、Redis 缓存和 Excel 导出等功能，适合作为 Java 后端练习项目、管理系统原型或本地演示项目。

当前后端持久层已升级为 MyBatis-Plus，单表流程使用 `BaseMapper` / `ServiceImpl` / `LambdaQueryWrapper`，预约和订单保留必要的联表 XML 查询，分页统一使用 MyBatis-Plus 分页插件。

## 技术栈

### 后端

- Java 17
- Spring Boot 3.5.16
- Spring MVC
- MyBatis-Plus 3.5.16
- Spring Cache
- Redis
- JWT
- EasyExcel
- Maven
- MySQL 8 / H2

### 前端

- Vue 3
- TypeScript
- Vite
- Element Plus
- Axios
- ECharts

## 功能模块

### 管理端 `frontend`

- 登录认证
- 工作台概览
- 员工管理
- 客户管理
- 服务项目管理
- 预约管理
- 订单管理与收款
- 库存管理
- 数据统计
- 客户 Excel 导出
- 订单 Excel 导出

### 后端接口

| 模块 | 接口前缀 | 说明 |
| --- | --- | --- |
| 登录 | `/api/login` | 登录并返回 JWT Token |
| 员工管理 | `/api/employees` | 员工分页、新增、修改、禁用、查询 |
| 客户管理 | `/admin/customer` | 客户档案维护 |
| 服务项目 | `/admin/serviceItem` | 门店服务项目维护 |
| 预约管理 | `/admin/appointment` | 预约记录维护、取消、完成 |
| 订单管理 | `/admin/order` | 订单创建、收款、完成、取消 |
| 库存管理 | `/admin/inventory` | 商品或耗材库存维护 |
| 数据统计 | `/admin/statistics` | 首页概览和近 7 日订单金额 |
| 导出 | `/admin/export` | 客户、订单 Excel 导出 |

## 项目结构

```text
store_appointment/
├─ store_appointment/                 # Spring Boot 后端
│  ├─ src/main/java/com/wkr/store_appointment/
│  │  ├─ config/                      # Web、Redis、MyBatis-Plus、数据库兼容迁移配置
│  │  ├─ controller/                  # REST 接口
│  │  ├─ service/                     # 业务接口
│  │  ├─ service/impl/                # 业务实现，使用 MyBatis-Plus 和 Spring Cache
│  │  ├─ mapper/                      # MyBatis-Plus Mapper
│  │  ├─ enums/                       # 业务枚举
│  │  ├─ pojo/                        # DTO、entity、VO
│  │  ├─ interceptor/                 # 登录和只读模式拦截器
│  │  ├─ handler/                     # 全局异常处理
│  │  └─ utils/                       # JWT、分页工具
│  └─ src/main/resources/
│     ├─ mapper/                      # 预约、订单联表查询 XML
│     └─ sql/                         # H2 便携初始化和 MySQL 升级 SQL
├─ frontend/                          # Vue 管理端
├─ docs/                              # 截图和技术升级说明
├─ start-admin.bat                    # Windows 一键启动脚本
└─ README.md
```

## 关键设计

### MyBatis-Plus 

- `customer`、`appointment`、`order_info` 使用 `@TableLogic` 支持逻辑删除。
- 单表 CRUD 和分页尽量使用 MyBatis-Plus 自动 SQL。
- 预约、订单需要返回客户名、员工名、服务项目名等联表字段，因此保留 XML 查询。
- `SchemaMigrationRunner` 会在启动时检查旧 MySQL 表结构，自动补 `deleted` 字段和索引，避免旧库升级后出现 `Unknown column 'deleted'`。

### 枚举化

项目已将常用业务状态集中到 `store_appointment/src/main/java/com/wkr/store_appointment/enums/`：

- `CommonStatusEnum`：启用 / 禁用
- `AppointmentStatusEnum`：待确认、已确认、已完成、已取消
- `PayStatusEnum`：未支付、已支付、已退款
- `DebtStatusEnum`：无欠款、分期中、已结清
- `OrderStatusEnum`：待服务、已完成、已取消
- `PaymentMethodEnum`：微信、支付宝、现金、次卡
- `CardTypeEnum`：次卡、护理卡、会员卡、疗程卡
- `CustomerLevelEnum`：普通、银卡、金卡、VIP
- `GenderEnum`：男、女

### Redis 缓存

项目使用 Spring Cache 注解统一管理缓存，核心配置在：

```text
store_appointment/src/main/java/com/wkr/store_appointment/config/RedisConfig.java
```

已缓存的主要数据：

- 员工列表与详情
- 客户分页与详情
- 服务项目分页与详情
- 库存分页与详情
- 预约分页与详情
- 订单分页与详情
- 首页统计与近 7 日订单金额

写操作会清理对应缓存，并处理客户、员工、服务项目变更对预约和订单联表展示数据的影响。登录接口和 Excel 导出不做缓存。

### 只读演示模式

系统内置两个账号：

| 账号 | 密码 | 权限 |
| --- | --- | --- |
| `admin` | `123456` | 管理员账号，可新增、修改、删除和查询 |
| `readonly` | `123456` | 只读演示账号，仅允许查询，限制写操作 |

## 数据库表

| 表名 | 说明 |
| --- | --- |
| `user` | 登录账号、角色、状态 |
| `employee` | 员工信息 |
| `customer` | 客户档案，支持逻辑删除 |
| `service_item` | 服务项目 |
| `appointment` | 预约记录，支持逻辑删除 |
| `order_info` | 订单、收款、欠款信息，支持逻辑删除 |
| `inventory_item` | 库存物品 |

便携模式建表脚本：

```text
store_appointment/src/main/resources/sql/portable_schema.sql
store_appointment/src/main/resources/sql/portable_data.sql
```

MySQL 业务升级脚本：

```text
store_appointment/src/main/resources/sql/business_upgrade.sql
store_appointment/src/main/resources/sql/order_info.sql
store_appointment/src/main/resources/sql/inventory_item.sql
```

## 启动方式

### Windows 一键启动

在项目根目录运行：

```text
start-admin.bat
```

脚本会使用 `portable` 配置启动后端，并启动 `frontend` 管理端。默认访问：

```text
http://localhost:5173
```

### 后端 MySQL 模式

先启动 MySQL 和 Redis，并创建数据库：

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

常用环境变量：

```text
STORE_PORT
STORE_DB_HOST
STORE_DB_PORT
STORE_DB_DATABASE
STORE_DB_USERNAME
STORE_DB_PASSWORD
STORE_REDIS_HOST
STORE_REDIS_PORT
STORE_REDIS_PASSWORD
STORE_REDIS_DATABASE
```

### 后端 H2 便携模式

便携模式适合本地演示，不依赖 MySQL：

```bash
cd store_appointment
mvn spring-boot:run -Dspring-boot.run.profiles=portable
```

### 前端管理端

```bash
cd frontend
npm install
npm run dev
```

默认访问：

```text
http://localhost:5173
```

前端代理已配置：

```text
/api   -> http://localhost:8080
/admin -> http://localhost:8080
```

执行 `npm run build` 会把前端产物输出到后端 `store_appointment/src/main/resources/static`，用于后端 jar 直接托管最新管理端页面。

## 接口示例

### 登录

```http
GET /api/login?username=admin&password=123456
```

返回：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "userName": "admin",
    "token": "JWT_TOKEN",
    "role": "admin"
  }
}
```

### 客户分页

```http
GET /admin/customer/page?page=1&pageSize=10&name=王
```

### 新增预约

```http
POST /admin/appointment
```

```json
{
  "customerId": 1,
  "employeeId": 2,
  "serviceItemId": 1,
  "appointmentTime": "2026-07-01T14:00:00",
  "status": 0,
  "remark": "客户预约下午护理"
}
```

### 新增订单

```http
POST /admin/order
```

```json
{
  "appointmentId": 1,
  "customerId": 1,
  "serviceItemId": 2,
  "cardType": "护理卡",
  "originalAmount": 300.00,
  "discountAmount": 20.00,
  "amount": 280.00,
  "paidAmount": 200.00,
  "monthlyPayment": 50.00,
  "paymentMethod": "微信",
  "remark": "客户分期支付"
}
```

### 更新订单收款

```http
PUT /admin/order/1/payment
```

```json
{
  "paidAmount": 280.00,
  "monthlyPayment": 0.00,
  "paymentMethod": "微信"
}
```

### 新增库存

```http
POST /admin/inventory
```

```json
{
  "name": "补水面膜",
  "category": "护理耗材",
  "unit": "盒",
  "quantity": 36,
  "safetyStock": 10,
  "costPrice": 28,
  "supplier": "美业供应商A",
  "status": 1,
  "remark": "常用补水项目耗材"
}
```

## 截图

截图位于 `docs/images/`，当前包含：

- `dashboard.png`：工作台
- `customer.png`：客户管理
- `appointment.png`：预约管理
- `order.png`：订单管理
- `inventory.png`：库存管理
- `statistics.png`：数据统计

## 常见问题

### 1. 前端接口请求失败

先确认后端已启动：

```text
http://localhost:8080
```

前端通过 Vite 代理访问后端，后端未启动时页面会出现接口请求失败。

### 2. `Unknown column 'deleted'`

这是旧 MySQL 表结构缺少逻辑删除字段导致的。当前项目已加入 `SchemaMigrationRunner`，重启后端后会自动补：

```text
customer.deleted
appointment.deleted
order_info.deleted
```

如果自动迁移未执行，手动运行 `business_upgrade.sql` 也可以。

### 3. Redis 连接失败

确认 Redis 已启动，并检查：

```text
STORE_REDIS_HOST
STORE_REDIS_PORT
STORE_REDIS_PASSWORD
STORE_REDIS_DATABASE
```

项目配置了缓存错误处理，Redis 短暂不可用不会直接中断业务，但会影响缓存效果并打印告警日志。

### 4. 端口占用

默认端口：

```text
后端: 8080
前端: 5173
```

后端端口可通过 `STORE_PORT` 调整；前端可使用：

```bash
npm run dev -- --port 5174
```

## 验证命令

后端测试：

```bash
cd store_appointment
mvn test
```

前端构建：

```bash
cd frontend
npm run build
```

构建产物会同步到后端静态资源目录：

```text
store_appointment/src/main/resources/static
```

## 项目亮点

- 后端基于 Spring Boot + MyBatis-Plus 实现完整门店管理业务。
- 使用 Spring Cache + Redis 缓存高频查询，并在写操作后清理相关缓存。
- 使用 enum 管理业务状态，减少散落的魔法数字和字符串。
- 支持 MySQL 开发模式和 H2 便携演示模式。
- 订单支持原价、优惠、应收、已收、欠款、月付计划和支付状态维护。
- 预约、订单支持联表 VO 查询，满足前端直接展示客户、员工、项目名称。
- 通过 JWT 和拦截器实现登录认证，通过只读账号支持演示环境数据保护。
- 使用 EasyExcel 导出客户和订单数据。
