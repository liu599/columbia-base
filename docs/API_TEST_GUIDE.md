# API 测试指南

本文档说明如何使用 Postman 测试产品激活系统的所有 API 接口。

## 前置准备

### 1. 导入 Postman Collection

1. 打开 Postman
2. 点击左上角的 "Import" 按钮
3. 选择项目根目录中的 `postman_collection.json` 文件
4. Collection 导入成功后，会在左侧列表看到 "Columbia Base API Collection"

### 2. 配置环境变量

Collection 已经内置了以下变量：

| 变量名 | 说明 | 示例值 |
|--------|------|--------|
| `base_url` | API 基础地址 | `http://localhost:8080` |
| `user_token` | 普通用户 Token | (登录后自动获取) |
| `admin_token` | 管理员 Token | (登录后自动获取) |
| `user_id` | 普通用户 ID | (注册/登录后自动获取) |
| `admin_id` | 管理员 ID | `1` |
| `product_id` | 产品 ID | (创建后自动获取) |
| `activation_code` | 激活码 | (批量创建后自动获取) |

## 测试流程

### 第一步：管理员登录与产品创建

#### 1. Admin Login
- 路径：`Admin (Login & Setup) > Admin Login`
- 修改请求体中的 `account` 和 `password`（使用数据库中已有的管理员账号，userId 在 1-10 范围内）
- 发送请求后，`admin_token` 会自动保存

```json
{
  "account": "admin",
  "password": "Admin@1234"
}
```

#### 2. Create Product
- 路径：`Admin (Login & Setup) > Create Product`
- 已自动使用 `admin_token` 认证
- 发送请求后，`product_id` 会自动保存

```json
{
  "id": null,
  "name": "AI 写作助手",
  "description": "智能文档生成与编辑助手",
  "baseCredits": 1000,
  "status": 1
}
```

#### 3. Batch Create Activation Codes
- 路径：`Admin (Login & Setup) > Batch Create Activation Codes`
- 已自动使用 `admin_token` 和 `product_id`
- 发送请求后，`activation_code` 会自动保存第一个激活码

```json
{
  "productId": {{product_id}},
  "count": 5,
  "codePrefix": "AI-WRITER"
}
```

### 第二步：用户注册与登录

#### 1. User Register
- 路径：`User (Registration & Login) > User Register`
- 发送请求后，`user_id` 会自动保存

```json
{
  "username": "testuser001",
  "phone": "13800138001",
  "password": "Test@1234"
}
```

#### 2. User Login
- 路径：`User (Registration & Login) > User Login`
- 发送请求后，`user_token` 会自动保存

```json
{
  "account": "testuser001",
  "password": "Test@1234"
}
```

### 第三步：用户激活产品

#### 1. Redeem Activation Code
- 路径：`User Activation Operations > Redeem Activation Code`
- 已自动使用 `user_token` 和 `activation_code`

```json
{
  "code": "{{activation_code}}"
}
```

#### 2. Get User's Activated Products
- 路径：`User Activation Operations > Get User's Activated Products`
- 查询当前用户已激活的所有产品

### 第四步：管理员操作

#### 1. Check User Product Activation
- 路径：`Admin User Management > Check User Product Activation`
- 检查指定用户是否激活了指定产品

```json
{
  "targetUserId": {{user_id}},
  "productId": {{product_id}}
}
```

#### 2. Manual Activate Product for User
- 路径：`Admin User Management > Manual Activate Product for User`
- 管理员手动为用户激活产品（无需激活码）

```json
{
  "targetUserId": {{user_id}},
  "productId": {{product_id}},
  "remark": "Admin manual activation"
}
```

#### 3. Deactivate (Reverse) User's Product
- 路径：`Admin User Management > Deactivate (Reverse) User's Product`
- 反激活用户的产品，并扣除相应积分

```json
{
  "targetUserId": {{user_id}},
  "productId": {{product_id}},
  "remark": "User refund requested"
}
```

#### 4. Update User Status
- 路径：`Admin User Management > Update User Status`
- 更新用户状态（0: 锁定，1: 正常）

```json
{
  "targetUserId": {{user_id}},
  "status": 1
}
```

## API 接口列表

### 用户接口（需要 Token）

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 用户登录 | POST | `/api/v1/user/login` | 用户登录获取 Token |
| 用户注册 | POST | `/api/v1/user/register` | 注册新用户 |
| 获取个人资料 | POST | `/api/v1/user/profile` | 获取当前用户信息 |
| 兑换激活码 | POST | `/api/v1/activation/redeem` | 使用激活码激活产品 |
| 我的激活产品 | GET | `/api/v1/activation/my-products` | 查询已激活的产品列表 |

### 管理员接口（需要 admin Token，userId 1-10）

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 创建/更新产品 | POST | `/api/v1/admin/product/save` | 创建新产品或更新现有产品 |
| 批量创建激活码 | POST | `/api/v1/admin/activation/batch-create` | 批量生成激活码 |
| 手动激活产品 | POST | `/api/v1/admin/product/activate` | 为用户手动激活产品 |
| 查询用户激活状态 | POST | `/api/v1/admin/activation/check-user` | 检查用户是否激活指定产品 |
| 反激活产品 | POST | `/api/v1/admin/activation/deactivate` | 反激活用户的产品 |
| 更新用户状态 | POST | `/api/v1/admin/user/status` | 设置用户状态（正常/锁定） |

### 公共接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 产品列表 | POST | `/api/v1/product/list` | 获取产品列表（支持分页和搜索） |

## 响应格式

所有接口统一返回以下格式：

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

错误响应：

```json
{
  "code": 400,
  "message": "错误描述",
  "data": null
}
```

## 常见问题

### 1. 401 Unauthorized
- 原因：Token 无效或已过期
- 解决：重新登录获取新的 Token

### 2. 403 Forbidden
- 原因：无权访问管理接口
- 解决：确保使用的账号 userId 在 1-10 范围内（管理员）

### 3. 激活码无效
- 原因：激活码已被使用或不存在
- 解决：重新批量创建激活码

### 4. 产品不存在
- 原因：product_id 变量未正确设置
- 解决：先执行 "Create Product" 接口

## 数据库初始化

在测试前，请确保已执行数据库脚本：

```bash
mysql -u root -p your_database < schema55.sql
```

然后插入一个测试管理员账号：

```sql
INSERT INTO t_user (id, username, password, phone, status, create_time)
VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOQoY.H6qoPzO.5cZ7Q8YqJZxH7kG.vF2qK9pL3mN4oP', '13800000001', 1, NOW());
```

默认密码：`Admin@1234`（需要使用 PasswordUtils 生成正确的哈希值）
