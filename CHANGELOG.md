

## v9.6.4
- 更新了 README
- 移除了一些不再使用的属性文件配置
- 每晚定时重新计算角色/权限，以补偿之前可能的异常导致的不完整
- 添加 git-commit-id-maven-plugin 插件生成当前打包对应的 git 仓库代码版本信息
- 调整目录结构，拆分大 common-model 到各个子模块中
- feign client 调整以兼容注册中心和直连方式，本地默认使用IP端口直连

## v9.6.3
- 添加 nacos 服务注册与发现的支持
- 添加 liquibase 的支持，做数据结构版本的控制
- 添加 risenet-y9boot-starter-multi-tenant 模块，支持租户租用系统后做租户表结构和数据的初始化
- 添加 jib-maven-plugin，用于打包 docker 镜像
- 三员密码过期后强制修改密码后才能登录管理后台
- 修复大量的问题
