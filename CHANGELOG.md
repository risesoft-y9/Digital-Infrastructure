## v9.6.8 2025-06-27

### Added

- sso 增加 password 认证方式，资源服务器使用 oidc 协议
- 单点登录的登录日志保存增加对 kafka 的支持
- 整理单点登录页面，登录页面介绍和图标可以动态配置
- 数据目录管理以及添加获取数据目录详情的接口
- 添加岗位角色相关接口
- 添加职位接口
- 使用 SpringEL 以支持更灵活的岗位名称模板
- JaVers 通过导入 sql 和 指定方言支持达梦数据库
- 增加对部门属性可继承的支持
- 图标上传增加颜色类别和所属分类


### Changed

- 登录页手动选择租户，不使用输入用户名弹出候选的方式
- 添加防抖函数
- 处理运维显示应用系统角色的时候未获取系统角色的问题
- ConstraintValidator 的实现移至 risenet-y9boot-common-util 中，方便其他模块调用
- 角色成员列表分页
- 组织节点列表禁用的高亮
- 对称加密 AES 使用无填充模式，避免字节填充导致加密前后数据不一致
- 移除禁用节点在某些组织架构树中的展示
- 移除不需要调用的接口
- 修改以支持有条件开启 JaVers
- JWT Access Token 的解析支持，不再调用 profile 接口获取用户信息
- 使用 fastexcel 替换 jxls 实现 excel 的导入导出
- build-helper-maven 替换 buildnumber-maven
- 继承的权限内容块仅资源继承上级节点的权限时展示
- 新建的租户数据源名规则修改 y9_{tenantShortName}_{randomStr}
- 密码修改周期从后台返回数据中获取
- 登录页输入框移除蜂鸣声
- InetAddressUtil 对 y9.internalIp 配置的依赖通过属性注入，移除配置文件的直接依赖以便支持远程配置
- 添加 xss.ignoreParam
- 移除 Y9User 的 roles 字段
- 移除 Y9Oauth2ResourceFilter 的 saveOnlineMessage 配置
- 移除 risenet-y9boot-starter-useronline


### Fixed

- 处理系统、资源和角色树顶节点获取异常
- 处理应用资源管理里面的应用出现删除和修改的问题
- 新增组织节点时有 tabIndex 则使用，否则计算 tabIndex （针对导入的情况）
- 登录日志用户 IP 没有记录的问题
- 授权树中对已添加的授权回显
- 邮箱不能修改的问题
- 后台查询不到禁用的组织节点的问题
- 优化授权相关记录的展示
- 登陆岗位默认返回排序靠前的岗位id
- 多个租户切换的位置增加打印日志输出租户 id，方便定位错误发生的租户
- 资源负权限继承的完善
- 升级前端 y9plugin-components 的版本，解决表格分页样式异常的问题

### Documentation

- 修改 README.md
- 添加接口安全控制使用文档

### Dependency Upgrades

- 升级了一些依赖包的版本
- 升级 vue 和 vite 的版本


## v9.6.7 2024-10-12

### Added

- 9LoginUserHolder添加OrgUnit类型，OrgUnitApi添加根据orgUnitId获取岗位或人员接口
- 开源工程添加打包至maven中央仓库的项目信息，同时增加y9-digitalbase-parent统一代码版本（发布中央库的时候先把web工程注释，目前最后一个工程skipPublishing=true，会导致所以发布操作停止）
- 引入 flatten-maven-plugin 展平 pom.xml（占位符的替换、简化pom、计算依赖的版本等）
- 增加缓存示例工程
- 增加data-jpa示例工程
- 增加elasticsearch示例工程
- 增加publish-kafka示例
- 添加日志上报的 demo 工程

### Changed

- 规范maven profile id，打包release的时候添加excludeArtifacts属性，排除webapp工程
- 统一 Java 版本；移除重复引入的配置
- 数字底座前端加入统一码对接，同时插件版本升级
- 对于没有静态资源的工程，移除注册 DefaultServlet
- 移除重复定义的 RequestContextFilter
- 使用 spring profile 代替 maven profile 多环境配置
- 属性文件的属性调整：server.intranet.ip -> y9.internalIp
- 支持传统的外部 servlet 容器部署 && 内嵌 tomcat 的可执行 war
- 完善租户角色，修改应用角色及应用资源的展示层级
- 角色表字段调整
- Elasticsearch统一使用ElasticsearchOperations查询
- nacos 密码可从系统环境变量里获取
- 移除 fileManager 中文件浏览前端组件 RichFilemanager
- 日志上报方式使用枚举
- 移除非必要文件、打包方式及类命名
- 将模块相关的配置属性类和sdk相关的配置属性类移至对应模块中
- lombok 依赖的 scope 修改为 provided

### Fixed

- 处理线程变量，当orgUnit的租户id为空的时不设置TENANT_ID_HOLDER的值
- 角色权限树不能正确展示的问题
- 处理多租户下租用系统初始化错误

### Documentation

- 修改README.md
- 增加缓存组件的说明

### Dependency Upgrades

- smart-doc-maven-plugin version to 3.0.0

## v9.6.6 2024-07-25

### Added

- 添加对 mariadb 数据库的支持
- 完善 liquibase 对人大金仓数据库的支持
- 完善资源的禁用，查询有权限的资源时，禁用的资源不返回
- 登录候选人排序
- 初始化的默认租户名和租户库名修改为可配置
- 定时扫表实现多租户的事件通知

### Changed

- 雪花算法加入默认构造方法，使用 hutool 工具类生成数据中心及机器号
- 日志代码重构，RestHighLevelClient 换成 ElasticsearchClient

### Fixed

- 租户租用系统后未初始化，重新执行初始化

## v9.6.5 2024-05-10

- 优化数字底座接口，完善组织节点的禁用
- 添加租户应用租用事件
- 添加系统配置表，实现多租户系统的个性设置
- 修复组织机构节点排序保存异常的问题，同时调整更新组织机构节点属性的保存代码
- fix: 删除公共角色时关联的租户数据没同步删除的问题
- 添加系统配置表，实现多租户系统的个性设置
- @RefreshScope 结合 nacos 以支持应用配置的在线动态修改
- 树查询时用 HashSet 替代 ArrayList，这对于在数据量较大时判断对象是否存在集合中有显著提升
- 整理文件存储服务的配置类型，新增文件上传至本地文件目录，默认使用本地文件存储而非 ftp 存储，此外文件存储目录按日期分割
- 日志管理拆分成elasticsearch和关系型数据库两种支持方式，默认为关系型数据库。
- 部门属性类型使用字典表代替枚举以实现动态的增删
- 单点登录（sso）拆分两个版本，引入纯净版仅使用关系型数据库。
- 数字底座分布式锁（ShedLock provider） 由 redis 切换到 JdbcTemplate
- 日志保存插件更新，新增属性y9.feature.log.logSaveTarget(用来切换日志保存方式)：kafka或者api，默认为kafka。
- 单点登录插件保存用户在线和访问日志日志的方法增加切换方式)：kafka或者api，默认为kafka。

## v9.6.4 2024-03-13

- 更新了 README
- 移除了一些不再使用的属性文件配置
- 每晚定时重新计算角色/权限，以补偿之前可能的异常导致的不完整
- 添加 git-commit-id-maven-plugin 插件生成当前打包对应的 git 仓库代码版本信息
- 调整目录结构，拆分大 common-model 到各个子模块中
- feign client 调整以兼容注册中心和直连方式，本地默认使用IP端口直连

## v9.6.3 2024-02-19

- 添加 nacos 服务注册与发现的支持
- 添加 liquibase 的支持，做数据结构版本的控制
- 添加 risenet-y9boot-starter-multi-tenant 模块，支持租户租用系统后做租户表结构和数据的初始化
- 添加 jib-maven-plugin，用于打包 docker 镜像
- 三员密码过期后强制修改密码后才能登录管理后台
- 修复大量的问题
