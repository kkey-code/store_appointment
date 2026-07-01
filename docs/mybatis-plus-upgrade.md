# MyBatis-Plus 技术升级记录

## 怎么改

- 后端持久层从 MyBatis + PageHelper 升级为 MyBatis-Plus。
- 单表流程改为 `BaseMapper` / `ServiceImpl` / `LambdaQueryWrapper`：员工、客户、服务项目、库存、用户登录、统计计数。
- 预约和订单列表需要联表返回 VO，保留 XML 查询，但分页参数改为 MyBatis-Plus `Page` / `IPage`。
- 客户、预约、订单启用 `@TableLogic` 逻辑删除；自定义联表 SQL 手动补 `deleted = 0`。
- 业务状态、订单状态、支付状态、欠款状态、支付方式、卡类型、客户等级、性别等常量集中到 `enum`。
- 主后台前端补库存页面、库存 API、路由和侧边栏菜单。

## 改了哪里

- `store_appointment/pom.xml`：移除 PageHelper 和显式 MyBatis 依赖，保留 MyBatis-Plus 依赖。
- `store_appointment/src/main/java/com/wkr/store_appointment/config/MybatisPlusConfig.java`：MyBatis-Plus 分页拦截器。
- `store_appointment/src/main/java/com/wkr/store_appointment/pojo/entity/`：补 `@TableName`、`@TableId`、逻辑删除字段注解。
- `store_appointment/src/main/java/com/wkr/store_appointment/mapper/`：单表 mapper 精简为 `BaseMapper`；新增 `UserMapper`。
- `store_appointment/src/main/resources/mapper/`：删除单表 CRUD XML，仅保留预约、订单联表查询 XML。
- `store_appointment/src/main/java/com/wkr/store_appointment/service/impl/`：服务层改用 MyBatis-Plus 分页、查询、插入、更新、删除。
- `store_appointment/src/main/java/com/wkr/store_appointment/enums/`：新增业务枚举，替换散落的状态数字和默认值字符串。
- `frontend/src/api/modules.ts`：新增库存类型和 `inventoryApi`。
- `frontend/src/views/InventoryView.vue`：新增主后台库存管理页面。
- `frontend/src/router.ts`、`frontend/src/layout/AdminLayout.vue`：新增库存路由和侧边栏入口。
- `README.md`：同步技术栈描述。
