# 产品课程树形结构API设计文档

## 概述

本文档描述了产品课程树形结构API的设计和实现，该API支持一次性创建或更新产品-课程-章节-课时的完整层级结构，一个产品可包含多个课程。同时支持查询接口返回不同层级的树形结构，统一使用数组格式。

## API端点

### 1. 创建/更新产品课程树

```
POST /api/v1/admin/product/tree
```

### 2. 查询产品课程树

```
POST /api/v1/admin/product/courses/query
```

## 请求结构 (ProductCourseTreeRequest)

### 完整请求示例

```json
{
  "productId": null,              // 可选：产品ID（更新时传入）
  "productName": "Python进阶课程", // 可选：产品名称
  "productDescription": "深入学习Python", // 可选：产品描述
  "productBaseCredits": 100,      // 可选：产品基础学分
  "productStatus": 1,             // 可选：产品状态
  "courses": [                    // 必填：课程数组
    {
      "courseId": null,           // 可选：课程ID（更新时传入）
      "title": "Python编程基础",   // 必填：课程标题
      "description": "从零开始学习Python", // 可选：课程描述
      "status": "DRAFT",          // 可选：课程状态
      "chapters": [               // 必填：章节列表
        {
          "chapterId": null,      // 可选：章节ID（更新时传入）
          "title": "第一章：Python简介", // 必填：章节标题
          "sortOrder": 0,         // 可选：排序序号
          "lessons": [            // 必填：课时列表
            {
              "lessonId": null,   // 可选：课时ID（更新时传入）
              "title": "1.1 Python是什么", // 必填：课时标题
              "sortOrder": 0,     // 可选：排序序号
              "itemType": "VIDEO",// 可选：课时类型
              "isRequired": true, // 可选：是否必选
              "contentPayload": "{\"fileId\": \"uuid-123\", \"duration\": 3600}" // 可选：内容JSON
            },
            {
              "lessonId": null,
              "title": "1.2 安装Python",
              "sortOrder": 1
            }
          ]
        },
        {
          "chapterId": null,
          "title": "第二章：基础语法",
          "sortOrder": 1,
          "lessons": [
            {
              "lessonId": null,
              "title": "2.1 变量与数据类型",
              "sortOrder": 0
            }
          ]
        }
      ]
    },
    {
      "courseId": null,
      "title": "Python进阶",
      "description": "Python高级特性",
      "status": "DRAFT",
      "chapters": [
        {
          "chapterId": null,
          "title": "第一章：装饰器",
          "sortOrder": 0,
          "lessons": [
            {
              "lessonId": null,
              "title": "1.1 装饰器基础",
              "sortOrder": 0
            }
          ]
        }
      ]
    }
  ]
}
```

### 字段说明

#### 产品层
| 字段 | 类型 | 必填 | 说明 |
|-----|------|-----|------|
| productId | Long | 否 | 更新模式下传入，不传则创建新产品 |
| productName | String | 创建时必填 | 产品名称 |
| productDescription | String | 否 | 产品描述 |
| productBaseCredits | Integer | 否 | 产品基础学分 |
| productStatus | Integer | 否 | 产品状态 |

#### 课程层
| 字段 | 类型 | 必填 | 说明 |
| ----- | ----- | ----- | ----- |
| courseId | Long | 否 | 更新模式下传入，不传则创建新课程 |
| title | String | 是 | 课程标题 |
| description | String | 否 | 课程描述 |
| status | String | 否 | 课程状态（DRAFT/PUBLISHED/OFFLINE） |
| chapters | List | 是 | 章节列表 |

#### 章节层
| 字段 | 类型 | 必填 | 说明 |
| ----- | ----- | ----- | ----- |
| chapterId | Long | 否 | 更新模式下传入，不传则创建新章节 |
| title | String | 是 | 章节标题 |
| sortOrder | Integer | 否 | 排序序号，默认按索引 |
| lessons | List | 是 | 课时列表 |

#### 课时层
| 字段 | 类型 | 必填 | 说明 |
| ----- | ----- | ----- | ----- |
| lessonId | Long | 否 | 更新模式下传入，不传则创建新课时 |
| title | String | 是 | 课时标题 |
| sortOrder | Integer | 否 | 排序序号，默认按索引 |
| itemType | String | 否 | 课时类型（VIDEO/DOCUMENT/PODCAST/ASSIGNMENT/INTERACTIVE），默认VIDEO |
| isRequired | Boolean | 否 | 是否必选，默认true |
| contentPayload | String | 否 | 内容JSON，根据itemType包含不同字段 |

## 查询API

### 端点
```
POST /api/v1/admin/product/courses/query
```

### 请求结构 (ProductCourseTreeQueryRequest)

```json
{
  "productId": 123456789012345678,  // 必填：产品ID
  "courseId": null,                  // 可选：课程ID，指定则返回单课程的完整树
  "treeLevel": "BASIC"               // 可选：返回层级，默认BASIC
}
```

### 字段说明

| 字段 | 类型 | 必填 | 说明 |
| ----- | ----- | ----- | ----- |
| productId | Long | 是 | 产品ID |
| courseId | Long | 否 | 课程ID，不传则返回产品下所有课程列表，传入则返回单课程完整树 |
| treeLevel | String | 否 | 返回层级：TITLES_ONLY/BASIC/FULL，默认BASIC |

### treeLevel 说明

| 层级 | 说明 | 包含字段 |
| ----- | ----- | ----- |
| TITLES_ONLY | 仅标题树 | id, title, sortOrder (课程仅id, title) |
| BASIC | 基础信息树 | id, title, description, status, sortOrder, itemType, isRequired |
| FULL | 完整内容树 | BASIC所有字段 + contentPayload (JSON对象) |

### 响应结构 (ProductCourseTreeQueryResponse)

#### 查询产品下所有课程（指定treeLevel）

```json
{
  "productId": 123456789012345678,
  "productName": "Python进阶课程",
  "treeLevel": "BASIC",
  "courses": [
    {
      "id": 123456789012345679,
      "title": "Python编程基础",
      "description": "从零开始学习Python",
      "status": "DRAFT",
      "productId": 123456789012345678,
      "chapters": [
        {
          "id": 123456789012345680,
          "title": "第一章：Python简介",
          "sortOrder": 0,
          "courseId": 123456789012345679,
          "lessons": [
            {
              "id": 123456789012345681,
              "title": "1.1 Python是什么",
              "itemType": "VIDEO",
              "isRequired": true,
              "sortOrder": 0,
              "chapterId": 123456789012345680
            }
          ]
        }
      ]
    }
  ]
}
```

#### 查询单个课程完整树（指定courseId + FULL）

```json
{
  "productId": 123456789012345678,
  "productName": "Python进阶课程",
  "treeLevel": "FULL",
  "courses": [
    {
      "id": 123456789012345679,
      "title": "Python编程基础",
      "description": "从零开始学习Python",
      "status": "DRAFT",
      "productId": 123456789012345678,
      "chapters": [
        {
          "id": 123456789012345680,
          "title": "第一章：Python简介",
          "sortOrder": 0,
          "courseId": 123456789012345679,
          "lessons": [
            {
              "id": 123456789012345681,
              "title": "1.1 Python是什么",
              "itemType": "VIDEO",
              "isRequired": true,
              "sortOrder": 0,
              "chapterId": 123456789012345680,
              "contentPayload": {
                "fileId": "uuid-123",
                "duration": 3600,
                "format": "mp4"
              }
            }
          ]
        }
      ]
    }
  ]
}
```

#### 仅标题树（treeLevel: TITLES_ONLY）

```json
{
  "productId": 123456789012345678,
  "productName": "Python进阶课程",
  "treeLevel": "TITLES_ONLY",
  "courses": [
    {
      "id": 123456789012345679,
      "title": "Python编程基础",
      "chapters": [
        {
          "id": 123456789012345680,
          "title": "第一章：Python简介",
          "sortOrder": 0,
          "lessons": [
            {
              "id": 123456789012345681,
              "title": "1.1 Python是什么"
            }
          ]
        }
      ]
    }
  ]
}
```

## 响应结构 (ProductCourseTreeResponse)

```json
{
  "productId": 123456789012345678,
  "product": {
    "id": 123456789012345678,
    "name": "Python进阶课程",
    "description": "深入学习Python",
    "baseCredits": 100,
    "status": 1
  },
  "courses": [
    {
      "id": 123456789012345679,
      "title": "Python编程基础",
      "description": "从零开始学习Python",
      "status": "DRAFT",
      "productId": 123456789012345678,
      "chapters": [
        {
          "id": 123456789012345680,
          "title": "第一章：Python简介",
          "sortOrder": 0,
          "courseId": 123456789012345679,
          "lessons": [
            {
              "id": 123456789012345681,
              "title": "1.1 Python是什么",
              "itemType": "VIDEO",
              "isRequired": true,
              "sortOrder": 0,
              "chapterId": 123456789012345680
            },
            {
              "id": 123456789012345682,
              "title": "1.2 安装Python",
              "itemType": "VIDEO",
              "isRequired": true,
              "sortOrder": 1,
              "chapterId": 123456789012345680
            }
          ]
        }
      ]
    },
    {
      "id": 123456789012345683,
      "title": "Python进阶",
      "description": "Python高级特性",
      "status": "DRAFT",
      "productId": 123456789012345678,
      "chapters": []
    }
  ],
  "operationType": "CREATE",
  "stats": {
    "productCreated": true,
    "productUpdated": false,
    "coursesCreated": 2,
    "coursesUpdated": 0,
    "chaptersCreated": 2,
    "chaptersUpdated": 0,
    "chaptersDeleted": 0,
    "lessonsCreated": 3,
    "lessonsUpdated": 0,
    "lessonsDeleted": 0
  }
}
```

### 响应字段说明
| 字段 | 类型 | 说明 |
| ----- | ----- | ----- |
| operationType | String | 操作类型：CREATE 或 UPDATE |
| stats | Object | 操作统计信息 |

## 使用场景

### 场景1：创建全新的产品课程树（单个课程）
```json
{
  "productName": "Java基础课程",
  "productBaseCredits": 150,
  "courses": [
    {
      "title": "Java入门",
      "status": "DRAFT",
      "chapters": [
        {
          "title": "第一章：Java环境搭建",
          "lessons": [
            {"title": "1.1 JDK安装"}
          ]
        }
      ]
    }
  ]
}
```

### 场景2：创建包含多个课程的产品
```json
{
  "productName": "Java全栈套餐",
  "productBaseCredits": 300,
  "courses": [
    {
      "title": "Java入门",
      "description": "Java基础语法",
      "status": "PUBLISHED",
      "chapters": [
        {
          "title": "第一章：Java环境",
          "lessons": [{"title": "1.1 JDK安装"}]
        }
      ]
    },
    {
      "title": "Spring Boot实战",
      "description": "企业级开发实战",
      "status": "PUBLISHED",
      "chapters": [
        {
          "title": "第一章：快速入门",
          "lessons": [{"title": "1.1 创建项目"}]
        }
      ]
    }
  ]
}
```

### 场景3：更新现有产品的课程结构
```json
{
  "productId": 123456789012345678,
  "courses": [
    {
      "courseId": 123456789012345679,
      "title": "Java入门（更新版）",
      "chapters": [
        {
          "chapterId": 123456789012345680,
          "title": "第一章：Java环境搭建（更新）",
          "lessons": [
            {"lessonId": 123456789012345681, "title": "1.1 JDK安装（修改）"},
            {"title": "1.2 新增课时"}  // 新增课时，没有lessonId
          ]
        },
        {
          "title": "第二章：Java语法",  // 新增章节，没有chapterId
          "lessons": [
            {"title": "2.1 变量与类型"}
          ]
        }
      ]
    }
  ]
}
```

### 场景4：仅创建标题（后续补充详细内容）
```json
{
  "productName": "React进阶",
  "courses": [
    {
      "title": "React高级特性",
      "chapters": [
        {
          "title": "第一章：Hooks",
          "lessons": [
            {"title": "1.1 useState"},
            {"title": "1.2 useEffect"},
            {"title": "1.3 useContext"}
          ]
        }
      ]
    }
  ]
}
```

### 场景5：为现有产品新增课程
```json
{
  "productId": 123456789012345678,
  "courses": [
    {
      "courseId": 123456789012345679,  // 已有课程，更新
      "title": "Java入门",
      "chapters": [...]
    },
    {
      // 无courseId，创建新课程
      "title": "Java并发编程",
      "status": "DRAFT",
      "chapters": [
        {
          "title": "第一章：线程基础",
          "lessons": [{"title": "1.1 线程创建"}]
        }
      ]
    }
  ]
}
```

## 核心特性

### 1. 混合模式支持（Create/Update）
- 传入ID的实体执行UPDATE操作
- 不传ID的实体执行CREATE操作
- 同一次请求中可以混合创建和更新操作

### 2. 级联删除
- 更新模式下，未在请求中引用的旧数据会被自动删除
- 删除顺序：课时 → 章节（级联删除）

### 3. 操作统计
- 响应中包含详细的操作统计信息
- 可用于数据变更审计

### 4. 事务保证
- 整个操作在单一事务中执行
- 任何失败都会回滚所有更改

### 5. 验证机制
- 课时类型验证（VIDEO/DOCUMENT/PODCAST/ASSIGNMENT/INTERACTIVE）
- 必填字段验证（使用@Valid和@NotBlank等注解）
- 级联关系验证

## 课时类型说明

| 类型 | contentPayload示例 |
| ----- | ----- |
| VIDEO | `{"fileId": "uuid-123", "duration": 3600, "format": "mp4"}` |
| DOCUMENT | `{"fileId": "uuid-456", "pages": 20, "format": "pdf"}` |
| PODCAST | `{"fileId": "uuid-789", "duration": 1800, "format": "mp3"}` |
| ASSIGNMENT | `{"description": "完成练习题", "requirement": "提交代码", "deadline": "2024-12-31"}` |
| INTERACTIVE | `{"content": "<html>...</html>", "interactionType": "quiz"}` |

## 错误码

| 错误码 | 错误信息 | 说明 |
| ----- | ----- | ----- |
| 44004 | 产品不存在 | 更新模式下产品ID不存在 |
| 43001 | 课程不存在 | 更新模式下课程ID不存在 |
| 43011 | 章节不存在 | 更新模式下章节ID不存在（注意：代码中使用CHAPTER_NOT_FOUND）
| 43006 | 课时不存在 | 更新模式下课时ID不存在 |
| 43008 | 无效的课时类型 | itemType不在枚举值中 |
| 49006 | 数据验证失败 | 必填字段为空或格式错误 |

## 注意事项

1. **ID一致性**：更新时传入的ID必须与现有数据库中的ID一致
2. **级联关系**：章节和课时必须包含在正确的父级下
3. **排序**：sortOrder会自动分配，可以不传
4. **内容**：contentPayload是JSON字符串，需要正确转义
5. **事务**：操作是原子的，任何错误都会导致整体回滚

## 文件位置

### 创建/更新API
- **请求DTO**: `api/src/main/java/base/ecs32/top/api/dto/ProductCourseTreeRequest.java`
- **响应DTO**: `api/src/main/java/base/ecs32/top/api/dto/ProductCourseTreeResponse.java`
- **服务**: `api/src/main/java/base/ecs32/top/api/service/impl/ProductServiceImpl.java:51`
- **控制器**: `api/src/main/java/base/ecs32/top/api/controller/AdminProductController.java:90`

### 查询API
- **请求DTO**: `api/src/main/java/base/ecs32/top/api/dto/ProductCourseTreeQueryRequest.java`
- **响应DTO**: `api/src/main/java/base/ecs32/top/api/dto/ProductCourseTreeQueryResponse.java`
- **服务**: `api/src/main/java/base/ecs32/top/api/service/impl/ProductServiceImpl.java:393`
- **控制器**: `api/src/main/java/base/ecs32/top/api/controller/AdminProductController.java:105`
