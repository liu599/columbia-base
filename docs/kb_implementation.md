# 知识库(KB)功能实现文档

## 概述
本次实现添加了知识库(KB)功能,包括知识库表和知识库-文件关联表,以及完整的CRUD操作。

## 数据库表结构

### 1. t_kb (知识库表)
- `id`: bigint(20) - 主键 (雪花ID)
- `user_id`: bigint(20) - 用户ID
- `name`: varchar(255) - 知识库名称
- `description`: text - 知识库描述
- `create_time`: datetime - 创建时间
- `update_time`: datetime - 更新时间

**索引:**
- PRIMARY KEY (`id`)
- INDEX `idx_user_id` (`user_id`)

### 2. t_kb_file (知识库-文件关联表)
- `id`: bigint(20) - 主键 (雪花ID)
- `kb_id`: bigint(20) - 知识库ID
- `file_id`: bigint(20) - 文件ID (关联t_file.id)
- `status`: varchar(20) - 状态 (parsing, success, error, uploaded)
- `parser_config`: text - 解析器配置 (JSON格式)
- `create_time`: datetime - 创建时间
- `update_time`: datetime - 更新时间

**索引:**
- PRIMARY KEY (`id`)
- UNIQUE INDEX `uk_kb_file` (`kb_id`, `file_id`)
- INDEX `idx_kb_id` (`kb_id`)
- INDEX `idx_file_id` (`file_id`)
- INDEX `idx_status` (`status`)

## API接口

### 知识库管理接口 (KB CRUD)

#### 1. 创建知识库
- **URL**: `POST /api/v1/kb/create`
- **Request Body**:
  ```json
  {
    "userId": 1234567890,
    "name": "我的知识库",
    "description": "这是一个示例知识库"
  }
  ```
- **Response**: KbVO

#### 2. 更新知识库
- **URL**: `POST /api/v1/kb/update`
- **Request Body**:
  ```json
  {
    "kbId": 1234567890,
    "userId": 1234567890,
    "name": "更新后的名称",
    "description": "更新后的描述"
  }
  ```
- **Response**: KbVO

#### 3. 删除知识库
- **URL**: `POST /api/v1/kb/delete`
- **Request Body**:
  ```json
  {
    "kbId": 1234567890,
    "userId": 1234567890
  }
  ```
- **Response**: void

#### 4. 获取知识库详情
- **URL**: `POST /api/v1/kb/get`
- **Request Body**:
  ```json
  {
    "kbId": 1234567890,
    "userId": 1234567890
  }
  ```
- **Response**: KbVO

#### 5. 获取用户的知识库列表
- **URL**: `POST /api/v1/kb/list`
- **Request Body**:
  ```json
  {
    "userId": 1234567890
  }
  ```
- **Response**: List<KbVO>

### 知识库文件关联接口 (KB File Association)

#### 6. 添加文件到知识库
- **URL**: `POST /api/v1/kb/file/add`
- **Request Body**:
  ```json
  {
    "kbId": 1234567890,
    "userId": 1234567890,
    "fileId": 9876543210,
    "parserConfig": "{\"parser\": \"pdf\", \"options\": {}}"
  }
  ```
- **Response**: KbFileVO

#### 7. 从知识库移除文件
- **URL**: `POST /api/v1/kb/file/remove`
- **Request Body**:
  ```json
  {
    "kbId": 1234567890,
    "userId": 1234567890,
    "fileId": 9876543210
  }
  ```
- **Response**: void

#### 8. 更新知识库文件状态
- **URL**: `POST /api/v1/kb/file/updateStatus`
- **Request Body**:
  ```json
  {
    "kbId": 1234567890,
    "userId": 1234567890,
    "fileId": 9876543210,
    "status": "parsing",
    "parserConfig": "{\"parser\": \"updated\"}"
  }
  ```
- **Response**: KbFileVO

#### 9. 获取知识库文件列表
- **URL**: `POST /api/v1/kb/file/list`
- **Request Body**:
  ```json
  {
    "kbId": 1234567890,
    "userId": 1234567890
  }
  ```
- **Response**: List<KbFileVO>

## 文件状态说明

文件状态 (`status`) 有以下四种:
- `uploaded`: 已上传 - 文件刚添加到知识库的初始状态
- `parsing`: 解析中 - 文件正在被解析处理
- `success`: 成功 - 文件解析成功
- `error`: 失败 - 文件解析失败

## 错误码

新增知识库相关错误码 (47000-47999):
- `47001`: KB_NOT_FOUND - 知识库不存在
- `47002`: KB_NO_PERMISSION - 无权限访问此知识库
- `47003`: KB_FILE_NOT_FOUND - 知识库文件关联不存在
- `47004`: KB_FILE_ALREADY_EXISTS - 文件已存在于知识库中
- `47005`: KB_FILE_STATUS_INVALID - 无效的文件状态

## 技术实现要点

1. **权限控制**: 所有操作都验证userId所有权,防止越权访问
2. **事务管理**: 删除知识库时使用事务确保关联数据同步删除
3. **唯一约束**: kb_id + file_id 组合唯一,防止重复添加
4. **状态验证**: 更新状态时验证status值的有效性
5. **关联查询**: 查询文件列表时join t_file表获取文件详情

## 数据库迁移

执行SQL脚本:
```bash
mysql -u username -p database_name < sql/add_kb_tables.sql
```

## 文件清单

### Entity层
- `core/src/main/java/base/ecs32/top/entity/Kb.java`
- `core/src/main/java/base/ecs32/top/entity/KbFile.java`

### DAO层
- `core/src/main/java/base/ecs32/top/dao/KbMapper.java`
- `core/src/main/java/base/ecs32/top/dao/KbFileMapper.java`

### Service层
- `api/src/main/java/base/ecs32/top/api/service/KbService.java`
- `api/src/main/java/base/ecs32/top/api/service/impl/KbServiceImpl.java`

### Controller层
- `api/src/main/java/base/ecs32/top/api/controller/KbController.java`

### DTO层
- `api/src/main/java/base/ecs32/top/api/dto/KbCreateRequest.java`
- `api/src/main/java/base/ecs32/top/api/dto/KbUpdateRequest.java`
- `api/src/main/java/base/ecs32/top/api/dto/KbDeleteRequest.java`
- `api/src/main/java/base/ecs32/top/api/dto/KbGetRequest.java`
- `api/src/main/java/base/ecs32/top/api/dto/KbListRequest.java`
- `api/src/main/java/base/ecs32/top/api/dto/KbFileAddRequest.java`
- `api/src/main/java/base/ecs32/top/api/dto/KbFileRemoveRequest.java`
- `api/src/main/java/base/ecs32/top/api/dto/KbFileUpdateStatusRequest.java`
- `api/src/main/java/base/ecs32/top/api/dto/KbFileListRequest.java`

### VO层
- `api/src/main/java/base/ecs32/top/api/vo/KbVO.java`
- `api/src/main/java/base/ecs32/top/api/vo/KbFileVO.java`

### SQL
- `sql/add_kb_tables.sql`
