# 人生清单（LifeList）后端

一个用 **Spring Boot 3 + MyBatis-Plus + MySQL + JWT** 写的「人生清单」多人协作后端，作为学习 Spring 后端的练手项目。

核心能力：**朋友各自登录记账** —— 每个人有账号，登录后只能看到/改自己的清单，朋友之间可以加好友、分组。

## 技术栈

| 层 | 选型 |
|---|---|
| 框架 | Spring Boot 3.2.5 |
| 语言 | Java 17 |
| 持久层 | MyBatis-Plus 3.5.7 |
| 数据库 | MySQL 8 |
| 登录鉴权 | 手写 JWT + BCrypt |

## 项目结构

```
src/main/java/com/lifelist/
├── LifeListApplication.java   启动类
├── common/                    统一返回 R、异常、JWT 工具、UserContext
├── config/                    JWT 拦截器、Web 配置、MyBatis-Plus 配置
├── controller/                接口层（auth/items/friends/groups）
├── service/                   业务层
├── entity/                    实体类（对应 5 张表）
├── mapper/                    MyBatis-Plus Mapper
└── dto/                       请求/响应对象
```

## 快速开始

1. **建库建表**：执行 `sql/schema.sql`（或手动导入到 MySQL）
2. **改配置**：编辑 `src/main/resources/application.yml`，把 `datasource` 的 `url/username/password` 改成你自己的
3. **启动**：在 IDEA 里直接运行 `LifeListApplication`，或命令行 `mvn spring-boot:run`
4. 服务跑在 `http://localhost:8080`

## 接口文档

统一返回结构：`{ "code": 0, "message": "ok", "data": ... }`，`code=0` 成功。

### 认证

| 方法 | 路径 | 说明 | 需登录 |
|---|---|---|---|
| POST | `/api/auth/register` | 注册，body: `{username, password, nickname}` | 否 |
| POST | `/api/auth/login` | 登录，body: `{username, password}`，返回 token | 否 |

### 小事清单（需登录，Header 带 `Authorization: Bearer <token>`）

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/items?stage=xx` | 列表，可选按阶段过滤 |
| POST | `/api/items` | 新增，body: `{stage, title, status, doneDate, mood, note, photo}` |
| PUT | `/api/items/{id}` | 更新 |
| DELETE | `/api/items/{id}` | 删除 |
| GET | `/api/items/stats` | 统计（已珍藏数/总数） |

### 朋友（需登录）

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/friends` | 加好友，body: `{friendUsername}` |
| GET | `/api/friends` | 我的好友列表 |
| GET | `/api/friends/pending` | 待我审核的申请 |
| POST | `/api/friends/{id}/accept` | 通过申请 |
| POST | `/api/friends/{id}/reject` | 拒绝申请 |

### 分组（需登录）

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/groups` | 建组，body: `{name}` |
| GET | `/api/groups` | 我的分组 |
| POST | `/api/groups/{groupId}/members` | 加成员，body: `{friendId}` |
| GET | `/api/groups/{groupId}/members` | 组内成员 |

## 数据隔离是怎么实现的（重点）

这是本项目最核心、也最值得学的一点：

1. **登录后**，后端把 `userId` 存进 `ThreadLocal`（`UserContext`）。
2. **每条业务数据**（小事、朋友关系、分组）都带 `user_id` 字段。
3. **所有查询/更新/删除**，都在业务层强制用 `UserContext.get()` 作为 `user_id` 条件过滤。

这样用户 A 永远操作不到用户 B 的数据 —— 前端请求里根本没有 `userId` 字段，后端从 token 里拿，前端无法伪造。

## 后续可以学/做的

- [ ] 分页查询（分页插件已配置好，`selectPage` 直接用）
- [ ] 照片上传（存本地或对象存储，替换现在的 `photo` 字符串 URL）
- [ ] 数据导出 Excel（把 `list()` 结果导出 CSV/XLSX）
- [ ] 前端对接：把之前的「人生清单」HTML 改成调这套 API
- [ ] 部署：打 jar 包部署到自己的服务器（配合之前讨论的自建服务器路线）
