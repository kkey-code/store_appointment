# 便携交付说明

本目录用于存放便携交付相关说明或后续打包产物。

当前项目已提供根目录脚本 `start-admin.bat`，可在 Windows 环境下一键启动管理端演示：

```text
start-admin.bat
```

该脚本会使用 `portable` 配置启动后端，并启动管理端前端 `frontend`。

客户 / 库存端如需同时演示，可单独启动：

```bash
cd frontend_customer_inventory
npm run dev -- --port 5174
```
