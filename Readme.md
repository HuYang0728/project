# MyShop - 现代电商平台后端项目

## 项目简介

本项目是一个基于 Spring Boot 和 Spring Security 打造的现代化电商平台后端系统。项目实现了完整且安全的用户认证流程、精细化的RBAC权限管理，并提供了标准的 RESTful API 用于商品的后台管理以及完整的前台购物流程（购物车与订单）。整个项目采用前后端分离的设计思想，以 JWT (JSON Web Tokens) 作为无状态认证的核心技术，并利用事务和数据库锁确保了在高并发场景下的数据一致性与系统稳定性。

## 项目核心功能

### ✅ 用户认证模块 (User Authentication Module)

- **用户注册**: 提供 `/api/users/register` 接口，对用户密码进行 BCrypt 加密存储，保证密码安全。
- **用户登录**: 提供 `/api/users/login` 接口，验证用户凭证，成功后颁发 JWT。
- **JWT 认证**: 通过自定义的 `JwtAuthenticationFilter` 拦截并验证后续请求中的 Token，实现无状态认证。

### ✅ 权限与用户管理模块 (RBAC & User Management Module)

- **三层 RBAC 模型**: 基于“用户-角色-权限”三层模型进行设计，实现了高度灵活的权限管理。
- **角色分配与移除**: 提供 `POST /api/admin/users/{userId}/roles` 等接口供管理员分配用户角色。
- **权限验证**: 后台管理接口均受 Spring Security 的URL级别和方法级安全注解 `@PreAuthorize` 保护，实现了精细化权限控制。

### ✅ 商品管理模块 (Product Management Module)

- **后台管理 (Admin API)**: 提供 `POST /api/admin/products`、`PUT /api/admin/products/{id}`、`DELETE /api/admin/products/{id}` 等接口，实现商品（包括软删除）的增删改查。
- **前台展示 (Public API)**: 提供公开的 `GET /api/products` 及 `GET /api/products/{id}` 接口，供所有用户浏览商品。

### ✅ 购物车模块 (Shopping Cart Module)

- **添加商品**: 提供 `POST /api/cart/items` 接口，智能处理新增商品或增加已存在商品数量的逻辑。
- **查看购物车**: 提供 `GET /api/cart` 接口，展示当前用户购物车内所有商品、数量、价格及总价。
- **修改数量**: 提供 `PUT /api/cart/items/{productId}` 接口，更新购物车中指定商品的数量。
- **移除商品**: 提供 `DELETE /api/cart/items/{productId}` 接口，将整个商品项从购物车中移除。

### ✅ 订单模块 (Order Module)

- **创建订单**: 提供 `POST /api/orders` 接口，在一个**原子性事务**中完成获取购物车、检查库存、创建订单、扣减库存、清空购物车等一系列核心操作。
- **查看订单历史**: 提供 `GET /api/orders` 接口，获取当前用户的所有历史订单列表。
- **查询订单详情**: 提供 `GET /api/orders/{orderNo}` 接口，根据业务订单号查询特定订单的完整信息（包括商品快照）。

## 技术栈 (Technology Stack)

| 技术 | 用途与说明 |
|---|---|
| **核心框架** | |
| Spring Boot | 项目主体框架，用于快速构建、配置和运行独立的 Java 应用。 |
| Spring Security | 业界领先的安全框架，用于处理认证 (Authentication) 和授权 (Authorization)。 |
| Spring Web (MVC) | 用于构建 RESTful API 接口。 |
| **数据持久层** | |
| MySQL | 主流的关系型数据库，用于存储所有业务数据。 |
| MyBatis | 优秀的持久层框架，通过 XML 灵活控制 SQL，并利用 `<foreach>` 实现高效的批量插入。 |
| **认证技术** | |
| JWT | 实现无状态认证，替代传统的 Session 机制，更适合分布式系统和移动应用。 |
| **开发工具** | |
| Maven | 项目构建与依赖管理工具。 |
| Docker | 用于容器化数据库环境，实现开发、测试、生产环境的一致性。 |
| Lombok | 通过注解简化 Java 代码，自动生成 Getter/Setter、构造函数等。 |
| Postman | 强大的 API 测试工具，用于验证所有后端接口的正确性。 |

## 项目亮点与特点

- **RESTful API 设计与最佳实践**
  遵循 RESTful 设计原则，使用标准的 HTTP 方法 (`POST`, `GET`, `PUT`, `DELETE`) 与 HTTP 状态码 (`201`, `400`, `403`, `409`) 清晰地表达操作意图与结果。

- **完整的 RBAC 权限模型**
  通过多对多关联表在数据库层面构建了完整的 RBAC 关系，并通过 `UserDetails` 无缝集成到 Spring Security 的认证体系中。

- **原子性的订单创建流程 (Atomic Order Creation)**
  利用 Spring 的 `@Transactional` 注解，将创建订单涉及的多个数据库写操作（创建订单、批量插入订单项、扣减商品库存、清空购物车）封装在单个事务中，确保了“要么全部成功，要么全部失败”，强力保证了数据的一致性。

- **悲观锁与并发控制 (Pessimistic Locking & Concurrency Control)**
  在创建订单的核心流程中，通过 `SELECT ... FOR UPDATE` SQL语句对商品库存记录施加**排他锁**。这有效解决了高并发场景下（如“秒杀”活动）可能出现的“超卖”问题，是项目具备高可靠性的关键体现。

- **清晰的分层架构与 DTO 模式**
  项目遵循经典的 `Controller` -> `Service` -> `Mapper` 分层设计，并广泛应用 DTO (Data Transfer Object) 模式，有效隔离了内部数据模型与外部 API 接口，增强了系统的健壮性与可维护性。

- **统一异常处理 (Centralized Exception Handling)**
  通过 `@RestControllerAdvice` 实现全局异常处理，能捕获自定义的业务异常（如 `InsufficientStockException`），并向前端返回规范、统一的错误信息，提升了 API 的专业性。

### 用户与权限管理指南

本项目的权限系统基于 RBAC 模型，并将用户角色分为三类：普通用户 (`ROLE_USER`)、商品管理员 (`ROLE_PRODUCT_MANAGER`) 和超级管理员 (`ROLE_ADMIN`)。

#### 1. 用户注册 (默认成为普通用户)

任何访客都可以通过此接口注册成为系统的普通用户 (`ROLE_USER`)。

- **接口**: `POST /api/users/register`
- **请求体 (Body)**:

  ```json
  {
      "username": "newuser",
      "email": "user@example.com",
      "password": "password123"
  }
  ```

- **说明**: 注册成功后，系统会自动为该用户分配 `ROLE_USER` 角色。

#### 2. 用户登录

所有角色的用户都通过此接口登录，获取用于后续操作的 JWT。

- **接口**: `POST /api/users/login`
- **请求体 (Body)**:

  ```json
  {
      "username": "newuser",
      "password": "password123"
  }
  ```

- **响应**:

  ```json
  {
      "token": "eyJhbGciOiJIU..."
  }
  ```

#### 3. 管理员操作：分配与提升角色 (后台数据库操作)

在当前项目设计中，为了最大限度地保证安全，**角色的提升（如普通用户升级为管理员）不能通过 API 完成**，必须由已授权的人员（如数据库管理员）直接在后台数据库中操作。

**场景一：将已存在的普通用户 `newuser` 提升为商品管理员 (`ROLE_PRODUCT_MANAGER`)**

```sql
-- 假设 newuser 的用户ID是 5, ROLE_PRODUCT_MANAGER 的角色ID是 2
-- 你需要先通过查询获取准确的 ID
-- SELECT id FROM users WHERE username = 'newuser';
-- SELECT id FROM roles WHERE name = 'ROLE_PRODUCT_MANAGER';

-- 在 user_roles 关联表中插入一条记录
INSERT INTO user_roles (user_id, role_id) VALUES (5, 2);
```

**场景二：直接在数据库创建一个超级管理员账户**

```sql
-- 1. 在 users 表中创建一个新用户
-- 假设密码是 'superadmin_password'，你需要先在程序中生成它的 BCrypt 哈希值
-- 然后将哈希值填入下方
INSERT INTO users (username, email, password_hash) 
VALUES ('superadmin', 'admin@myshop.com', '$2a$10$your_bcrypt_hashed_password_here');

-- 2. 假设新创建的 superadmin 用户ID是 6, ROLE_ADMIN 的角色ID是 1
-- 为其分配超级管理员角色
INSERT INTO user_roles (user_id, role_id) VALUES (6, 1);
```

## API 使用指南 (购物功能)

所有需要认证的接口，都需要在请求的 Header 中添加 `Authorization: Bearer <Your-JWT-Token>`。

#### 1. 登录获取 Token

- **请求**: `POST /api/users/login`
- **Body**: `{ "username": "your_username", "password": "your_password" }`
- **操作**: 复制返回的 `token`，用于后续所有需要认证的请求。

#### 2. 添加商品到购物车

- **请求**: `POST /api/cart/items`
- **Body**: `{ "productId": 1, "quantity": 2 }`
- **说明**: 将 ID 为 1 的商品添加 2 件到购物车。如果购物车中已有该商品，则数量会累加。

#### 3. 查看购物车

- **请求**: `GET /api/cart`
- **说明**: 返回当前用户购物车的所有商品详情及总价。

#### 4. 修改购物车商品数量

- **请求**: `PUT /api/cart/items/1`
- **Body**: `{ "quantity": 5 }`
- **说明**: 将购物车中 ID 为 1 的商品数量直接修改为 5。如果数量改为 0，商品将从购物车中移除。

#### 5. 从购物车移除商品

- **请求**: `DELETE /api/cart/items/1`
- **说明**: 将 ID 为 1 的商品（无论多少件）从购物车中彻底移除。

#### 6. 创建订单

- **前提**: 购物车中必须有商品。
- **请求**: `POST /api/orders`
- **Body**: `{ "shippingAddress": "中国上海市浦东新区世纪大道88号" }`
- **说明**: 系统会自动从当前用户的购物车生成订单，成功后会清空购物车。

#### 7. 查看历史订单

- **请求**: `GET /api/orders`
- **说明**: 返回当前用户的所有历史订单列表。
- **请求**: `GET /api/orders/20250908153000abcdefgh`
- **说明**: 根据订单号查询单个订单的详细信息。

## 未来规划与改进方向

- **商品高级查询**: 在前台商品列表接口中增加搜索、分类筛选、价格排序等功能。
- **输入验证 (Validation)**: 在 DTO 中使用 `jakarta.validation` 注解（如 `@NotBlank`, `@Email`），对用户输入进行校验，提升系统健壮性。
- **单元与集成测试**: 引入 JUnit 和 Mockito 等框架，编写单元测试和集成测试，确保代码质量和功能稳定性。
- **API 文档**: 集成 Swagger 或 OpenAPI，自动生成交互式的 API 文档，方便前后端协作。
- **持续集成/持续部署 (CI/CD)**: 搭建 CI/CD 流水线（如使用 GitHub Actions），实现代码提交后自动构建、测试和部署。
