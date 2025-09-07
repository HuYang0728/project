# MyShop - 现代电商平台后端项目

## 项目简介

本项目是一个基于 Spring Boot 和 Spring Security 打造的现代化电商平台后端系统。项目实现了完整且安全的用户认证流程（注册与登录），并提供了标准的 RESTful API 用于商品的后台管理与前台展示。整个项目采用前后端分离的设计思想，以 JWT (JSON Web Tokens) 作为无状态认证的核心技术，确保了系统的安全性、可扩展性和高并发能力。

## 项目核心功能

### ✅ 用户认证模块 (User Authentication Module)

- **用户注册**: 提供 `/api/users/register` 接口，对用户密码进行 BCrypt 加密存储，保证密码安全，并处理用户名已存在的场景。
- **用户登录**: 提供 `/api/users/login` 接口，验证用户凭证，成功后颁发 JWT。
- **JWT 认证**: 通过自定义的 `JwtAuthenticationFilter` 拦截并验证后续请求中的 Token，实现无状态认证。
- **受保护的资源**: 实现 `/api/users/me` 接口，只有携带有效 Token 的用户才能访问，用以验证整个认证闭环。

### ✅ 商品管理模块 (Product Management Module)

#### 后台管理接口 (Admin API)

- **新增商品**: 提供 `POST /api/admin/products` 接口，用于添加新商品，并处理商品重名的情况。
- **修改商品**: 提供 `PUT /api/admin/products/{id}` 接口，用于更新指定商品的信息。
- **删除商品**: 提供 `DELETE /api/admin/products/{id}` 接口，实现商品**软删除**，保证数据一致性。
- **查询商品详情**: 提供 `GET /api/admin/products/{id}` 接口，供管理员查看商品详情。

#### 前台展示接口 (Public API)

- **获取所有商品**: 提供公开的 `GET /api/products` 接口，无需登录即可浏览所有状态为有效的商品列表。
- **获取单个商品详情**: 提供公开的 `GET /api/products/{id}` 接口，用于查看特定商品的详细信息。

## 技术栈 (Technology Stack)

| 技术 | 用途与说明 |
|---|---|
| **核心框架** | |
| Spring Boot | 项目主体框架，用于快速构建、配置和运行独立的 Java 应用。 |
| Spring Security | 业界领先的安全框架，用于处理认证 (Authentication) 和授权 (Authorization)。 |
| Spring Web (MVC) | 用于构建 RESTful API 接口。 |
| **数据持久层** | |
| MySQL | 主流的关系型数据库，用于存储用户信息和商品数据。 |
| MyBatis | 优秀的持久层框架，通过 XML 或注解将接口与 SQL 语句绑定，灵活可控。 |
| **认证技术** | |
| JWT | 实现无状态认证，替代传统的 Session 机制，更适合分布式系统和移动应用。 |
| **开发工具** | |
| Maven | 项目构建与依赖管理工具。 |
| Docker | 用于容器化数据库环境，实现开发、测试、生产环境的一致性。 |
| Lombok | 通过注解简化 Java 代码，自动生成 Getter/Setter、构造函数等。 |
| Postman | 强大的 API 测试工具，用于验证所有后端接口的正确性。 |

## 项目亮点与特点

- **RESTful API 设计与最佳实践**
  遵循 RESTful 设计原则，使用标准的 HTTP 方法 (`POST`, `GET`, `PUT`, `DELETE`) 表达操作意图，使用 HTTP 状态码 (`201`, `204`, `404`, `409`) 表示操作结果。实现了**软删除**等业界最佳实践，并清晰地分离了后台管理 (`/api/admin`) 与前台 (`/api`) 接口。

- **无状态认证 (Stateless Authentication)**
  采用 JWT 进行认证，服务器端无需存储用户 Session，大大提高了系统的可伸缩性 (Scalability) 和性能。这与传统的 Session 认证机制形成了鲜明对比，是现代 Web 应用的主流选择。

- **清晰的分层架构 (Layered Architecture)**
  项目遵循经典的 `Controller` -> `Service` -> `Mapper` 分层设计，职责分明，代码结构清晰，易于维护和扩展。

- **统一异常处理 (Centralized Exception Handling)**
  通过 `@RestControllerAdvice` 实现全局异常处理。能够捕获自定义的业务异常（如 `UsernameAlreadyExistsException`, `ProductAlreadyExistsException`），并向前端返回规范、统一的 `409 Conflict` 错误信息，提升了 API 的健壮性和开发调试效率。

- **DTO 模式的应用 (Data Transfer Object)**
  在 Controller 层使用 `LoginRequest`, `UserDto` 等 DTO 对象，有效隔离了内部数据模型 (Entity) 和外部 API 接口的数据结构。这不仅增强了 API 的稳定性和安全性（如隐藏密码哈希），也避免了不必要的数据泄露。

- **开发环境容器化 (Containerized Environment)**
  使用 Docker 部署 MySQL 数据库，避免了在本地直接安装数据库带来的版本冲突和环境配置问题，实现了“一次构建，到处运行”的现代化开发流程。

## 未来规划与改进方向

- **完善权限系统 (RBAC)**:
  使用 Spring Security 的方法级安全注解 (`@PreAuthorize`) 来保护所有 `/api/admin/**` 接口，确保只有具备 `ADMIN` 角色的用户才能进行商品的增、删、改操作。

- **功能模块扩展**:
  - **购物车**: 实现添加、删除、查看购物车商品的功能。
  - **订单系统**: 实现创建订单、支付（模拟）、查看订单历史等核心电商功能。
  - **商品高级查询**: 在前台商品列表接口中增加搜索、分类筛选、价格排序等功能。

- **输入验证 (Validation)**:
  在 DTO 中使用 `jakarta.validation` 注解（如 `@NotBlank`, `@Email`），对用户输入进行校验，提升系统健اط- **单元与集成测试**:
  引入 JUnit 和 Mockito 等框架，编写单元测试和集成测试，确保代码质量和功能稳定性。

- **API 文档**:
  集成 Swagger 或 OpenAPI，自动生成交互式的 API 文档，方便前后端协作。

- **持续集成/持续部署 (CI/CD)**:
  搭建 CI/CD 流水线（如使用 GitHub Actions），实现代码提交后自动构建、测试和部署。
