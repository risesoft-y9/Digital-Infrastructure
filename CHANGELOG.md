## v9.6.9 2026-02-03

### Added

- 引入risenet-y9boot-starter-repeatsubmit
- 添加日志的控制台输出
- 增加审计日志，自己实现的审计日志替换 javers
- 添加 DateFormatter 处理 Date 类型的时间为 yyyy-MM-dd HH:mm:ss
- 新增risenet-y9boot-common-sso，提取sso公共代码到该工程。
- 新增角色关联数据目录授权
- 添加 y9-digitalbase-bom
- 添加 y9-digitalbase-parent
- 数据目录支持导入导出 excel
- 日志注解 @RiseLog 的操作名称 operationName 支持 SpEL 实现可读性更高的描述
- SpringBoot Actuactor 配置调整，结合 docker 的healthcheck 实现不健康应用的重启

### Changed

- 修改应用的上下文
  对于未前后端分离的工程，应用上下文统一小写，举例：risenet-y9boot-webapp-filemanager 对应上下文 /filemanager
  对于已经前后端分离的后端工程名做了修改，应用上下文统一小写，举例：risenet-y9boot-webapp-addressbook ->
  risenet-y9boot-server-addressbook，对应的上下文 /server-addressbook
- 移除risenet-y9boot-common-util的webService接口调用
- 修改默认的岗位名模板："#jobName.equals('无') ? #personNames : #personNames+ '（' + #jobName + '）'"
- 移除 ftpStoreService 的 @Primary 注解，所有文件存储服务同级，通过开关控制只有一个存储服务注入
- 应用的 docker 基础镜像 tomcat 更新至 9.0.107-jdk11-temurin
- 将登录表单的提交的代码挪回页面中，清晰整个登录提交流程，与点选验证码的代码解耦，以及调整点选验证码样式，移除未使用的页面
- jodd.util.Base64转java.util.Base64
- 调整获取系统下的角色信息：先获取系统角色，再获取应用
- platform 相关类的包名统一
- 实体修改时获取原始对象不使用缓存
- 重构日志管理的 repository 层，降低 service 层对它的依赖，不同的存储实现使用不同的 SpringBoot App
- 调整事务注解仅在必要的方法上加
- 调整Specification多条件查询的实现
- 多条件查询使用 Query 对象封装
- 调整代码，Controller 层不直接使用 Entity
- AES 工具类使用随机 IV，如果有旧的加密数据需考虑迁移
- RSA 使用 OAEP/SHA-256，替代AES加密
- 移除risenet-y9boot-starter-kafka，调整配置文件
- 移除增强实现类，通过不同的方法名区分
- 移除非必要 maven 属性和插件，调整flatten-maven-plugin 配置
- 完善 starter-listener-kafka、starter-publish-kafka 使用的示例工程
- 移除 risenet-y9boot-starter-multi-tenant 对 risenet-y9boot-starter-jpa-tenant 的依赖，同时不强依赖
  risenet-y9boot-starter-liquibase，项目可按需引入
- 日志相关接口调整，移除一些冗余、无用接口
- starter-jpa 公用的一些代码提取到 3rd-jpa 模块中
- 工具类重命名
- version 无需手动设置以及实体的 tenantId 无需手动设置
- 操作日志展示字段调整，优化日志记录功能，方法和类级别的注解 RiseLog 属性 moduleName 的“合并”
- ID 生成器工具类不依赖 Spring 容器
- 移除 Y9FileStoreService 的 Y9FileStore uploadFile(MultipartFile multipartFile, String customPath, String fileName) 方法

### Fixed

- 升级echarts版本，处理窗口拖动报错的问题
- 租户修改触发人员更新
- 处理组织删除关联的授权数据没有删除的问题
- 新增或修改角色时对 appId 和 systemId 的处理
- 处理Y9PersonExt 传入 null 的问题
- 角色移动的父节点为应用时报错
- 授权树获取用于回显授权数据不成功时导致的转圈问题
- 组织节点排列序号更新时同时更新其后代叶子节点的 orderPath
- 移除示例中无用的测试
- 三员禁止修改登录名
- addHeader--》setHeader，处理请求头重复添加y9aoplog，导致请求过大的问题
- 静态代码扫描问题修复
- 选择树的 checked 节点回显
- 使用 hibernate 管理多租户表结构没有正常更新的问题
- 恢复 oauth2 ，处理退出后导致 oidc 退出重定向 URL 非预期的问题
- 处理参数大小写的问题：oSName -> osName，以及修改entityClass路径
- 删除人员的同时删除人员扩展信息
- 前台提交的时间参数不能正确转换成 Date 的问题
- 文件上传文件流关闭
- 处理使用 builder 构建对象某些字段没有默认值的问题
- Y9Tenant 实体跟 platform 中的字段保持一致，避免 sso 先初始化导致 platform 使用该表时字段“缺失”
- 修改前端加密库 jsencrypt -> forge ，解决中文在 RSA-OAEP 中不能正常加密的问题
- 添加 Y9Properties bean 的注册
- 处理数据目录不能正确反序列化的问题
- 处理disabled值为空的异常
- 新租户无法创建、初始化新租户发生异常时人员也同步进账号表的问题
- 租用数字底座事务没提交就进行初始化发生的异常
- 前端没有提交 tabIndex 时该值没有正确设置的问题

### Documentation

- 添加代码注释
- 调整smart-doc.json
- 更新 README.md
- smart-doc 不设置 responseBodyAdvice，否则没有使用统一返回类 net.risesoft.pojo.Y9Result 的接口生成的文档不正确

### Dependency Upgrades

- Upgrade to commons-io 2.19.0
- Upgrade to commons-beanutils 1.11.0
- Upgrade to jaxws-rt 2.3.7
- Upgrade to alibaba-nacos 2.2.4
- Upgrade to spring-cloud-starter-alibaba-nacos-config 2021.0.6.2
- Upgrade to y9plugin-components-auto 3.0.51
- Upgrade to poi-ooxml 5.4.1
- Upgrade to poi-scratchpad 5.4.1

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
