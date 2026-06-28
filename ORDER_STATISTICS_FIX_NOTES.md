# 订单、统计模块修复记录

## 1. 订单管理模块

### 问题位置

- `src/main/java/com/wkr/store_appointment/controller/OrderController.java`
- `src/main/java/com/wkr/store_appointment/service/OrderService.java`
- `src/main/java/com/wkr/store_appointment/service/impl/OrderServiceImpl.java`
- `src/main/java/com/wkr/store_appointment/mapper/OrderMapper.java`
- `src/main/resources/mapper/OrderMapper.xml`
- `src/main/java/com/wkr/store_appointment/pojo/entity/OrderInfo.java`

### 原来的问题

- `OrderController` 是空壳，且路径写成了 `/admin/appointment`，和预约模块冲突。
- `OrderService`、`OrderServiceImpl`、`OrderMapper`、`OrderMapper.xml` 基本没有业务实现。
- 缺少订单实体类。接口文档建议实体为 `OrderInfo`，不能直接用 `order`，因为 `order` 是 SQL 关键字。
- `OrderDTO`、`OrderPageQueryDTO`、`OrderVO` 没有 `@Data`，会导致请求参数绑定、BeanUtils 拷贝、JSON 返回不完整。

### 修改方式

- 新增 `OrderInfo` 实体，默认数据库表名按 `order_info` 处理。
- 补齐订单接口：
  - `GET /admin/order/page`
  - `POST /admin/order`
  - `GET /admin/order/{id}`
  - `PUT /admin/order/{id}/pay`
  - `PUT /admin/order/{id}/complete`
  - `PUT /admin/order/{id}/cancel`
- 新增订单分页 SQL，关联 `customer` 和 `service_item` 返回客户名、服务项目名。
- 新增订单状态流转：
  - 新增订单默认 `payStatus = 0`，`orderStatus = 0`
  - 支付订单：只有未支付订单能支付，支付后 `payStatus = 1` 并记录 `payTime`
  - 完成订单：只有已支付订单能完成，完成后 `orderStatus = 1`
  - 取消订单：已完成订单不能取消，取消后 `orderStatus = 2`
- 后端自动生成订单号，格式为 `ORD + yyyyMMddHHmmssSSS + 3位随机数`。

## 2. 数据统计模块

### 问题位置

- `src/main/java/com/wkr/store_appointment/controller/StatisticsController.java`
- `src/main/java/com/wkr/store_appointment/service/StatisticsService.java`
- `src/main/java/com/wkr/store_appointment/service/impl/StatisticsServiceImpl.java`
- `src/main/java/com/wkr/store_appointment/mapper/StatisticsMapper.java`
- `src/main/resources/mapper/StatisticsMapper.xml`
- `src/main/java/com/wkr/store_appointment/pojo/vo/StatisticsOverviewVO.java`
- `src/main/java/com/wkr/store_appointment/pojo/vo/OrderAmountStatisticsVO.java`

### 原来的问题

- 数据统计模块文件不存在。

### 修改方式

- 新增首页统计接口：
  - `GET /admin/statistics/overview`
  - 返回客户总数、今日预约数、今日订单数、今日已支付订单金额。
- 新增最近 7 天订单金额统计接口：
  - `GET /admin/statistics/orderAmount`
  - 返回 `dateList` 和 `amountList`。
- 金额统计只统计 `pay_status = 1` 的已支付订单。

## 3. 前面模块发现并修正的问题

### DTO / VO 缺少 getter、setter

问题位置：

- `AppointmentDTO`
- `AppointmentVO`
- `CustomerDTO`
- `CustomerVO`
- `EmployeeDTO`
- `EmployeeVO`
- `ServiceItemDTO`
- `ServiceItemPageQueryDTO`
- `OrderDTO`
- `OrderPageQueryDTO`
- `OrderVO`

问题原因：

- 这些类只有字段，没有 `@Data`，Spring 绑定参数、BeanUtils 拷贝、接口 JSON 返回都会受影响。

修改方式：

- 给这些 DTO / VO 补了 Lombok `@Data`。

### 分页返回结构不符合接口文档

问题位置：

- `AppointmentController`
- `AppointmentService`
- `AppointmentServiceImpl`
- `AppointmentMapper`
- `CustomerController`
- `CustomerService`
- `CustomerServiceImpl`
- `CustomerMapper`

问题原因：

- 接口文档要求分页返回 `PageResult<T>`，原来部分模块返回的是单个 VO。

修改方式：

- 调整为 `Result<PageResult<...>>`。
- Mapper 返回 `Page<...>`，Service 中封装为 `PageResult`。

### 预约状态逻辑错误

问题位置：

- `AppointmentMapper.java`
- `AppointmentServiceImpl.java`

问题原因：

- 文档约定预约取消状态为 `3`，原来取消预约把状态改成了 `0`。
- 原来没有限制“已完成预约不能取消”和“只有已确认预约可以完成”。

修改方式：

- 取消预约改为 `status = 3`。
- 完成预约改为只有 `status = 1` 时允许完成，完成后 `status = 2`。
- 已完成预约不能取消。

### 服务项目模块 SQL / Mapper 问题

问题位置：

- `ServiceItemMapper.java`
- `ServiceItemServiceImpl.java`
- `ServiceItemController.java`

问题原因：

- `ServiceItemMapper` 原来缺少 `@Mapper`。
- 根据 ID 查询服务项目用了 `@Insert("select ...")`，注解错误。
- 新增 SQL 中使用了 `#{create_time}`、`#{update_time}`，和 Java 属性 `createTime`、`updateTime` 不匹配。
- 分页调用用了不稳定的 `PageHelper.startPage(serviceItemPageQueryDTO)`。
- 文档有修改服务项目接口，原来没有实现。

修改方式：

- 加 `@Mapper`。
- 查询改成 `@Select`。
- 时间字段参数改成 `#{createTime}`、`#{updateTime}`。
- 分页改成 `PageHelper.startPage(page, pageSize)`。
- 新增 `PUT /admin/serviceItem` 修改服务项目接口。

### 员工模块分页和状态判断问题

问题位置：

- `EmployeePageQueryDTO.java`
- `EmpServiceImpl.java`
- `EmployeeMapper.java`
- `EmployeeMapper.xml`

问题原因：

- 文档要求员工分页支持 `name`、`phone`、`status`，原来 DTO 只有 `name`。
- 分页调用用了不稳定的 `PageHelper.startPage(employeePageQuery)`。
- 登录判断账号禁用时，原来用 `user.getStatus().equals("0")`，`status` 是 Integer，和字符串比较永远不成立。

修改方式：

- 给 `EmployeePageQueryDTO` 增加 `phone`、`status`。
- 分页改为显式 `PageHelper.startPage(page, pageSize)`。
- 登录禁用判断改为 `user.getStatus() != null && user.getStatus() == 0`。

### MyBatis 和 Spring Boot 版本兼容问题

问题位置：

- `pom.xml`
- `StoreAppointmentApplication.java`

问题原因：

- 项目使用 Spring Boot 3.5.16，但 MyBatis starter 原来是 `2.2.2`，属于 Boot 2.x 时代的版本。
- 测试启动时 Mapper Bean 识别异常。

修改方式：

- `mybatis-spring-boot-starter` 升级到 `3.0.5`。
- `pagehelper-spring-boot-starter` 升级到 `2.1.1`。
- 启动类加了 `@MapperScan("com.wkr.store_appointment.mapper")`。

### 驼峰映射问题

问题位置：

- `src/main/resources/application.properties`

问题原因：

- 数据库字段多为 `create_time`、`order_no`，Java 字段是 `createTime`、`orderNo`。
- 不开启下划线转驼峰时，部分查询结果可能映射不上。

修改方式：

- 增加：

```properties
mybatis.configuration.map-underscore-to-camel-case=true
```

### 统一异常处理

问题位置：

- `src/main/java/com/wkr/store_appointment/handler/GlobalExceptionHandler.java`

问题原因：

- 业务异常如果不统一处理，接口可能直接返回 500，不符合统一响应格式。

修改方式：

- 新增全局异常处理器。
- `BaseException` 返回 `Result.error(ex.getMessage())`。
- 其他异常返回 `Result.error("系统异常")`。

## 4. 测试相关修改

### 问题位置

- `pom.xml`
- `src/test/resources/application.properties`

### 原来的问题

- `mvn test` 启动 Spring 上下文时需要 DataSource，但主配置没有数据库连接信息。

### 修改方式

- 增加 H2 测试依赖。
- 新增测试环境内存数据库配置：

```properties
spring.datasource.url=jdbc:h2:mem:store_appointment_test;MODE=MySQL;DB_CLOSE_DELAY=-1
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
```

## 5. 已验证

已执行并通过：

```bash
mvn -q -DskipTests compile
mvn -q test
```

## 6. 还需要继续看的设计问题

这些点这次没有完全迁移，因为会影响已有前端或数据库设计，需要你确认后再统一改：

- 登录接口当前还是 `GET /api/login`，接口文档要求 `POST /admin/user/login`。
- 代码中仍有少量早期文件的旧注释风格，后续可以继续统一成更完整的中文注释。

## 7. 第二轮修复记录

### ServiceItemService 命名规范

问题位置：

- `src/main/java/com/wkr/store_appointment/service/serviceItemService.java`
- `src/main/java/com/wkr/store_appointment/service/impl/serviceItemServiceImpl.java`
- `src/main/java/com/wkr/store_appointment/controller/ServiceItemController.java`

修改方式：

- `serviceItemService` 重命名为 `ServiceItemService`。
- `serviceItemServiceImpl` 重命名为 `ServiceItemServiceImpl`。
- 同步修改 Controller 中的 import 和注入类型。

### 员工删除改为禁用

问题位置：

- `src/main/java/com/wkr/store_appointment/mapper/EmployeeMapper.java`
- `src/main/java/com/wkr/store_appointment/service/impl/EmpServiceImpl.java`

修改方式：

- 原来的 `delete from employee where id = #{id}` 改为 `update employee set status = 0`。
- Controller 的 `DELETE /api/employees/{id}` 接口不变，但业务含义改为禁用员工，避免物理删除影响历史预约。

### 客户删除增加关联校验

问题位置：

- `src/main/java/com/wkr/store_appointment/mapper/CustomerMapper.java`
- `src/main/java/com/wkr/store_appointment/service/impl/CustomerServiceImpl.java`

修改方式：

- 删除客户前查询：
  - `appointment.customer_id`
  - `order_info.customer_id`
- 如果有关联预约或订单，抛出业务异常：`客户存在关联预约或订单，不能删除`。
- 没有关联时才执行物理删除。

### 员工、客户手机号重复校验

问题位置：

- `EmployeeMapper.countByPhone`
- `CustomerMapper.countByPhone`
- `EmpServiceImpl.save/update`
- `CustomerServiceImpl.save/update`

修改方式：

- 新增时检查手机号是否已存在。
- 修改时排除当前记录 ID 后再检查手机号是否重复。
- 员工重复返回：`员工手机号已存在`。
- 客户重复返回：`客户手机号已存在`。

### 主环境 MySQL 配置

问题位置：

- `src/main/resources/application.properties`

修改方式：

新增主库连接配置，默认连接本机 MySQL：

```properties
spring.datasource.url=${STORE_DB_URL:jdbc:mysql://localhost:3306/store_appointment?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=${STORE_DB_USERNAME:root}
spring.datasource.password=${STORE_DB_PASSWORD:}
```

如果你的 MySQL 账号密码不同，建议配置环境变量：

```bash
STORE_DB_URL=jdbc:mysql://localhost:3306/store_appointment?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
STORE_DB_USERNAME=root
STORE_DB_PASSWORD=你的密码
```

### order_info 建表脚本

问题位置：

- `src/main/resources/sql/order_info.sql`

修改方式：

- 新增 `order_info` 建表脚本。
- 字段和当前订单 SQL 对齐，包括：
  - `order_no`
  - `appointment_id`
  - `customer_id`
  - `service_item_id`
  - `amount`
  - `pay_status`
  - `order_status`
  - `pay_time`
  - `remark`
  - `create_time`
  - `update_time`

### 乱码清理

修改方式：

- 本轮重写了服务项目、员工、客户相关实现文件中的乱码注释和乱码业务提示。
- 业务错误提示改为正常中文。
