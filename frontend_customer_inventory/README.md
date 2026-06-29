# 门店预约管理系统前端

## 目录位置

前端代码放在：

```text
D:\_study\java\store_appointment\frontend
```

后端代码放在：

```text
D:\_study\java\store_appointment\store_appointment
```

这样前后端是同级目录，互不混入构建产物。

## 技术栈

- Vue 3
- TypeScript
- Vite
- Element Plus
- Axios
- ECharts

## 已实现页面

- 登录页
- 工作台
- 员工管理
- 客户管理
- 服务项目
- 预约管理
- 订单管理
- 数据统计

## 启动命令

```bash
npm install
npm run dev
```

默认访问：

```text
http://localhost:5173
```

## 后端代理

开发环境已在 `vite.config.ts` 中配置代理：

```ts
/api -> http://localhost:8080
/admin -> http://localhost:8080
```

所以启动前端前，需要先启动 Spring Boot 后端。

## 构建命令

```bash
npm run build
```

当前已验证构建通过。
