# 产品课程树形结构API - 请求示例

## 创建/更新API端点
```
POST /api/v1/admin/product/tree
```

## 查询API端点
```
POST /api/v1/admin/product/courses/query
```

---

## 示例 1: 创建全新的产品课程树
**场景**: 创建一个全新的产品（包含课程、章节、课时）

```json
{
  "productName": "Python进阶课程",
  "productDescription": "深入学习Python高级特性",
  "productBaseCredits": 100,
  "productStatus": 1,
  "course": {
    "title": "Python编程进阶",
    "description": "从基础到进阶的完整Python学习路径",
    "status": "DRAFT",
    "chapters": [
      {
        "title": "第一章：高级数据结构",
        "sortOrder": 0,
        "lessons": [
          {
            "title": "1.1 列表推导式",
            "sortOrder": 0,
            "itemType": "VIDEO",
            "isRequired": true
          },
          {
            "title": "1.2 生成器与迭代器",
            "sortOrder": 1,
            "itemType": "VIDEO",
            "isRequired": true
          },
          {
            "title": "1.3 装饰器基础",
            "sortOrder": 2,
            "itemType": "VIDEO",
            "isRequired": true
          }
        ]
      },
      {
        "title": "第二章：面向对象编程",
        "sortOrder": 1,
        "lessons": [
          {
            "title": "2.1 类与对象",
            "sortOrder": 0,
            "itemType": "VIDEO",
            "isRequired": true
          },
          {
            "title": "2.2 继承与多态",
            "sortOrder": 1,
            "itemType": "VIDEO",
            "isRequired": true
          }
        ]
      }
    ]
  }
}
```

---

## 示例 2: 仅创建标题（后续补充详细内容）
**场景**: 快速创建课程大纲，后续再补充课时详细内容

```json
{
  "productName": "React前端开发",
  "course": {
    "title": "React高级特性",
    "status": "DRAFT",
    "chapters": [
      {
        "title": "第一章：React Hooks",
        "lessons": [
          {"title": "1.1 useState详解"},
          {"title": "1.2 useEffect深度解析"},
          {"title": "1.3 useContext与useReducer"}
        ]
      },
      {
        "title": "第二章：性能优化",
        "lessons": [
          {"title": "2.1 React.memo"},
          {"title": "2.2 useCallback"},
          {"title": "2.3 useMemo"}
        ]
      },
      {
        "title": "第三章：状态管理",
        "lessons": [
          {"title": "3.1 Redux Toolkit"},
          {"title": "3.2 Zustand"},
          {"title": "3.3 Jotai"}
        ]
      }
    ]
  }
}
```

---

## 示例 3: 更新现有课程（混合创建和更新）
**场景**: 更新现有课程的章节和课时，同时新增内容

```json
{
  "productId": 123456789012345678,
  "productName": "Java基础课程（升级版）",
  "course": {
    "courseId": 123456789012345679,
    "title": "Java编程基础 v2.0",
    "chapters": [
      {
        "chapterId": 123456789012345680,
        "title": "第一章：Java环境搭建（已更新）",
        "lessons": [
          {
            "lessonId": 123456789012345681,
            "title": "1.1 JDK安装指南（修改标题）"
          },
          {
            "title": "1.3 IDE配置"  // 新增课时，无lessonId
          }
        ]
      },
      {
        "title": "第二章：Java基础语法",  // 新增章节，无chapterId
        "lessons": [
          {"title": "2.1 变量与数据类型"},
          {"title": "2.2 运算符"}
        ]
      }
    ]
  }
}
```

---

## 示例 4: 更新课时类型和内容
**场景**: 为已创建的课时添加详细类型和内容

```json
{
  "productBaseCredits": 50,
  "course": {
    "courseId": 123456789012345679,
    "status": "PUBLISHED",
    "chapters": [
      {
        "chapterId": 123456789012345680,
        "lessons": [
          {
            "lessonId": 123456789012345681,
            "title": "1.1 视频课程讲解",
            "itemType": "VIDEO",
            "isRequired": true,
            "contentPayload": "{\"fileId\": \"video-uuid-123\", \"duration\": 3600, \"format\": \"mp4\"}"
          },
          {
            "lessonId": 123456789012345682,
            "title": "1.2 课程文档",
            "itemType": "DOCUMENT",
            "isRequired": true,
            "contentPayload": "{\"fileId\": \"doc-uuid-456\", \"pages\": 20, \"format\": \"pdf\"}"
          },
          {
            "lessonId": 123456789012345683,
            "title": "1.3 课后作业",
            "itemType": "ASSIGNMENT",
            "isRequired": true,
            "contentPayload": "{\"description\": \"完成第一章练习题\", \"requirement\": \"提交代码到GitHub\", \"deadline\": \"2024-12-31\"}"
          }
        ]
      }
    ]
  }
}
```

---

## 示例 5: 批量删除课时和章节
**场景**: 删除课程中不再需要的章节和课时

```json
{
  "productId": 123456789012345678,
  "course": {
    "courseId": 123456789012345679,
    "title": "Python编程精简版",
    "chapters": [
      {
        "chapterId": 123456789012345680,
        "title": "第一章：Python基础",
        "lessons": [
          {
            "lessonId": 123456789012345681,
            "title": "1.1 Python简介"
          },
          {
            "lessonId": 123456789012345684,
            "title": "1.4 继续学习"
          }
          // 注意：1.2 和 1.3 未包含，将被删除
        ]
      }
      // 注意：第二章、第三章等未包含，将被全部删除
    ]
  }
}
```

---

## 示例 6: 多课时类型混合
**场景**: 创建包含多种课时类型的完整章节

```json
{
  "productName": "Web全栈开发",
  "course": {
    "title": "全栈开发实战",
    "chapters": [
      {
        "title": "第一章：前端基础",
        "lessons": [
          {
            "title": "1.1 HTML/CSS基础",
            "itemType": "VIDEO",
            "isRequired": true
          },
          {
            "title": "1.2 JavaScript入门",
            "itemType": "VIDEO",
            "isRequired": true
          },
          {
            "title": "1.3 前端代码练习",
            "itemType": "INTERACTIVE",
            "isRequired": false,
            "contentPayload": "{\"content\": \"<h2>编写一个计算器</h2>\", \"interactionType\": \"code-editor\"}"
          }
        ]
      },
      {
        "title": "第二章：后端开发",
        "lessons": [
          {
            "title": "2.1 Node.js入门",
            "itemType": "VIDEO",
            "isRequired": true
          },
          {
            "title": "2.2 Express框架",
            "itemType": "VIDEO",
            "isRequired": true
          },
          {
            "title": "2.3 后端API实现",
            "itemType": "ASSIGNMENT",
            "isRequired": true,
            "contentPayload": "{\"description\": \"实现一个用户认证API\", \"requirement\": \"包含注册、登录、权限验证\", \"deadline\": \"2024-12-31\"}"
          }
        ]
      },
      {
        "title": "第三章：数据库",
        "lessons": [
          {
            "title": "3.1 MySQL基础",
            "itemType": "VIDEO",
            "isRequired": true
          },
          {
            "title": "3.2 数据库设计文档",
            "itemType": "DOCUMENT",
            "isRequired": true
          }
        ]
      }
    ]
  }
}
```

---

## 课时类型 contentPayload 格式参考

### VIDEO
```json
{
  "fileId": "video-uuid-123",
  "duration": 3600,
  "format": "mp4",
  "resolution": "1080p"
}
```

### DOCUMENT
```json
{
  "fileId": "doc-uuid-456",
  "pages": 20,
  "format": "pdf",
  "size": 5242880
}
```

### PODCAST
```json
{
  "fileId": "audio-uuid-789",
  "duration": 1800,
  "format": "mp3",
  "bitrate": "128kbps"
}
```

### ASSIGNMENT
```json
{
  "description": "完成第一章练习题",
  "requirement": "提交完整代码和运行截图",
  "deadline": "2024-12-31T23:59:59",
  "maxScore": 100
}
```

### INTERACTIVE
```json
{
  "content": "<html><body><h2>交互式练习</h2>...</body></html>",
  "interactionType": "quiz",
  "questions": [
    {
      "id": 1,
      "text": "Python中定义变量的关键字是？",
      "options": ["var", "let", "不需要关键字", "define"],
      "answer": 2
    }
  ]
}
```

---

## 响应示例

```json
{
  "productId": 123456789012345678,
  "product": {
    "id": 123456789012345678,
    "name": "Python进阶课程",
    "description": "深入学习Python高级特性",
    "baseCredits": 100,
    "status": 1
  },
  "course": {
    "id": 123456789012345679,
    "title": "Python编程进阶",
    "description": "从基础到进阶的完整Python学习路径",
    "status": "DRAFT",
    "productId": 123456789012345678,
    "chapters": [
      {
        "id": 123456789012345680,
        "title": "第一章：高级数据结构",
        "sortOrder": 0,
        "courseId": 123456789012345679,
        "lessons": [
          {
            "id": 123456789012345681,
            "title": "1.1 列表推导式",
            "itemType": "VIDEO",
            "isRequired": true,
            "sortOrder": 0,
            "chapterId": 123456789012345680
          }
        ]
      }
    ]
  },
  "operationType": "CREATE",
  "stats": {
    "productCreated": true,
    "productUpdated": false,
    "courseCreated": true,
    "courseUpdated": false,
    "chaptersCreated": 2,
    "chaptersUpdated": 0,
    "chaptersDeleted": 0,
    "lessonsCreated": 5,
    "lessonsUpdated": 0,
    "lessonsDeleted": 0
  }
}
```

---

# 查询API示例

## 示例 Q1: 查询产品下所有课程 - 基础层级 (BASIC)
**场景**: 获取产品下所有课程的完整树形结构，包含基础信息

```json
// 请求
{
  "productId": 123456789012345678,
  "treeLevel": "BASIC"
}

// 响应
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
    }
  ]
}
```

---

## 示例 Q2: 查询单个课程 - 完整层级 (FULL)
**场景**: 获取特定课程的完整内容，包括课时的 contentPayload

```json
// 请求
{
  "productId": 123456789012345678,
  "courseId": 123456789012345679,
  "treeLevel": "FULL"
}

// 响应
{
  "productId": 123456789012345678,
  "productName": "Python进阶课程",
  "treeLevel": "FULL",
  "course": {
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
              "fileId": "video-uuid-123",
              "duration": 3600,
              "format": "mp4",
              "resolution": "1080p"
            }
          },
          {
            "id": 123456789012345682,
            "title": "1.2 安装Python",
            "itemType": "DOCUMENT",
            "isRequired": true,
            "sortOrder": 1,
            "chapterId": 123456789012345680,
            "contentPayload": {
              "fileId": "doc-uuid-456",
              "pages": 20,
              "format": "pdf",
              "size": 5242880
            }
          }
        ]
      }
    ]
  }
}
```

---

## 示例 Q3: 查询产品下所有课程 - 仅标题层级 (TITLES_ONLY)
**场景**: 快速获取课程大纲，适合树形结构展示

```json
// 请求
{
  "productId": 123456789012345678,
  "treeLevel": "TITLES_ONLY"
}

// 响应
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
            },
            {
              "id": 123456789012345682,
              "title": "1.2 安装Python"
            },
            {
              "id": 123456789012345683,
              "title": "1.3 Python环境配置"
            }
          ]
        },
        {
          "id": 123456789012345684,
          "title": "第二章：基础语法",
          "sortOrder": 1,
          "lessons": [
            {
              "id": 123456789012345685,
              "title": "2.1 变量与数据类型"
            }
          ]
        }
      ]
    }
  ]
}
```

---

## 示例 Q4: 默认查询（不指定 treeLevel）
**场景**: 使用默认的 BASIC 层级查询

```json
// 请求
{
  "productId": 123456789012345678
}
// treeLevel 默认为 BASIC

// 响应
{
  "productId": 123456789012345678,
  "productName": "Python进阶课程",
  "treeLevel": "BASIC",
  "courses": [...]
}
```

---

## 不同层级字段对比

| 字段 | TITLES_ONLY | BASIC | FULL |
| ----- |------------- |-------|------|
| 课程 | ✅ | ✅ | ✅ |
| 课程描述 | ❌ | ✅ | ✅ |
| 课程状态 | ❌ | ✅ | ✅ |
| 章节 | ✅ | ✅ | ✅ |
| 章节排序 | ✅ | ✅ | ✅ |
| 课时 | ✅ | ✅ | ✅ |
| 课时类型 | ❌ | ✅ | ✅ |
| 是否必选 | ❌ | ✅ | ✅ |
| 课时排序 | ❌ | ✅ | ✅ |
| 内容负载 | ❌ | ❌ | ✅ |

