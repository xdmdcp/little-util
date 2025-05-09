# little

## 项目说明

little 是一个基于 Java 17 与 Spring Boot 的轻量级多模块工具框架，旨在为 Web 层开发提供统一的技术规范和通用功能支持，提升开发效率与代码质量。

## 模块划分

> ✅ 表示该模块已开发完成并可用</br>
> 🟡 表示正在开发中</br>
> ❌ 表示尚未开始开发</br>

### 核心模块

-  🟡 little-common: 公共能力模块，定义通用接口、注解、结果封装类等，与具体技术栈无关。
-  🟡 little-core: 基础核心模块，保持技术纯净性，不引入外部依赖，仅提供基础配置和上下文工具类。

### 扩展模块

-  ✅ [little-starter-log](./little-starter-log/README.md)：日志处理模块，集成日志记录、异常处理、事件监听等功能，支持日志发布机制。
-  ❌ little-starter-security：安全模块，计划集成 JWT 认证、权限校验、接口访问控制等安全相关能力。
-  ❌ little-starter-swagger：API 文档模块，用于集成 Swagger 或 Knife4j，自动生成和展示 RESTful 接口文档。
-  ❌ little-starter-validation：参数校验模块，提供统一的参数校验能力，支持自定义注解及异常处理。
-  ❌ little-starter-mybatis：MyBatis Plus 集成模块，提供分页拦截器、SQL 日志打印、通用 CRUD 封装等功能。
-  ❌ little-starter-jpa：JPA 集成模块，提供类似 MyBatis Plus 的便捷操作与扩展功能。
-  ❌ little-starter-social：第三方登录模块，基于 JustAuth 实现多平台授权登录。
-  ❌ little-starter-cache：缓存抽象模块，支持本地缓存、Redis、Memcached 等多种缓存实现方式。
-  ❌ little-starter-oss：对象存储模块，集成阿里云 OSS、MinIO、七牛云、腾讯云 COS 等主流对象存储服务。
-  ❌ little-starter-mq：消息队列模块，计划支持 Kafka、RabbitMQ、RocketMQ 等消息中间件的统一接入。

### 应用模块

-  🟡 little-starter-web：Web 层通用支持模块，整合日志、异常处理、响应封装等能力，简化 Spring Boot Web 开发流程。