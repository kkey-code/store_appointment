# 本地只读交付说明

## 结论

如果程序和数据库都放在对方本地电脑上，不能做到绝对防篡改。对方拥有本机管理员权限时，可以绕过应用直接改 MySQL 数据文件或用 root 登录数据库。

本项目提供的是“交付演示只读模式”：正常打开系统、查询数据、看统计，但系统接口会拦截新增、修改、删除、收款等写操作；数据库账号也只授予 `select` 权限，防止通过应用账号改库。

## 已处理

- 已删除数据库里的物理外键，只保留业务字段和索引。
- 已新增 `demo` 只读配置。
- 已新增只读拦截器，开启后会拦截 `POST / PUT / PATCH / DELETE`。
- 已提供只读数据库账号脚本。

## 交付给对方前

1. 导出数据库：

```powershell
& "D:\_Dev\Database\MySQL\mysql-8.0.25-winx64\bin\mysqldump.exe" -uroot -p12345678 --databases store_appointment > store_appointment_demo.sql
```

2. 对方导入数据库后，执行只读账号脚本：

```powershell
& "mysql.exe" -uroot -p < create_demo_readonly_user.sql
```

3. 后端用 demo 模式启动：

```powershell
java -jar store_appointment.jar --spring.profiles.active=demo
```

默认 demo 数据库账号：

- 用户名：`store_demo`
- 密码：`store_demo_123456`

如需改账号密码，用环境变量覆盖：

```powershell
$env:STORE_DB_USERNAME="store_demo"
$env:STORE_DB_PASSWORD="store_demo_123456"
java -jar store_appointment.jar --spring.profiles.active=demo
```

## 不能篡改数据的真实方案

如果数据不能被对方改，最稳的是不要把真实数据库交出去：

- 数据库和后端放在你自己的服务器。
- 对方本地只运行前端，访问你的 API。
- 或者给对方一份脱敏 demo 数据，允许本地随便改，真实数据不交付。

本地只读模式适合演示和验收，不适合作为绝对数据安全方案。
