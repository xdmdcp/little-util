# little-util

## 项目结构

### 核心模块

- little-core-bom: 依赖配置
- little-core-boot: Spring Boot 启动相关配置，包含自动装配、文件处理等功能。
- little-core-common: 工具类模块，提供通用工具方法（如加密解密、日期处理、文件操作等）。
- little-core-launch: 应用启动相关的工具类和配置。
- little-core-secure: 安全模块，包含 JWT 认证、权限校验等功能。

### 功能模块

- little-starter-cache: 缓存模块
- little-starter-echo: 数据回显模块，提供回显自动配置和属性配置等
- little-starter-mybatis: MyBatis Plus 集成模块，提供分页拦截器、SQL 日志等功能。
- little-starter-log: 日志模块，包含日志记录、异常处理、事件监听等功能。
- little-starter-social:  第三方登录模块，基于 JustAuth 实现。
- little-starter-swagger: swagger文档模块，用于API文档生成。
- little-starter-validator: 参数校验模块，如验证注解和验证控制器等
- little-starter-uid: 唯一ID生成相关的代码
- little-starter-oss: 对象存储模块，支持阿里云 OSS、MinIO、七牛云、腾讯云 COS 等。
- little-starter-monitor: 监控模块，用于监控应用的运行状态和性能。