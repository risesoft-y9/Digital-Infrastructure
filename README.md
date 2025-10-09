<p align="center">
 <img alt="logo" src="https://vue.youshengyun.com/files/img/qrCodeLogo.png">
</p>
<p align="center">基于SpringBoot+Vue前后端分离的Java快速开发框架</p>
<p align="center">
 <a href='https://gitee.com/risesoft-y9/y9-core/stargazers'><img src='https://gitee.com/risesoft-y9/y9-core/badge/star.svg?theme=dark' alt='star'></img></a>
    <img src="https://img.shields.io/badge/version-v9.6.9-yellow.svg">
    <img src="https://img.shields.io/badge/Spring%20Boot-2.7-blue.svg">
    <img alt="logo" src="https://img.shields.io/badge/Vue-3.3-red.svg">
    <img alt="" src="https://img.shields.io/badge/JDK-11-green.svg">
    <a href="https://gitee.com/risesoft-y9/y9-core/blob/master/LICENSE">
    <img src="https://img.shields.io/badge/license-GPL3-blue.svg"></a>
    <img src="https://img.shields.io/badge/total%20lines-236.5k-blue.svg">
</p>

## 简介

数字底座是一款面向大型政府、企业数字化转型，基于身份认证、组织架构、岗位职务、应用系统、资源角色、数据目录、安全控制等功能构建的统一且安全的管理支撑平台。数字底座基于三员管理模式，具备微服务、多租户、容器化和国产化，支持用户利用代码生成器快速构建自己的业务应用，同时可关联诸多成熟且好用的内部生态应用。
[系统在线体验----->>>>>](#在线体验)

### 三员管理

（一）系统管理员

系统管理员承担系统基础架构的搭建与维护职责，核心功能涵盖控制台监控、组织架构管理、应用资源配置等。控制台实时展示用户、应用及日志的统计信息，支持按需增改监控维度；通过树结构可视化展现组织机构、部门、人员及岗位关系，实现层级化管理；可创建多级应用系统目录，定义菜单、按钮等应用资源，构建公共角色与应用角色体系。同时负责字典表维护、图标库管理、数据目录编目，以及子组织三员体系的搭建，通过资源权限树与角色权限树实现全局权限可视化查看，为权限管控奠定基础。

（二）安全保密员

安全保密员聚焦权限分配与安全管控，核心功能包括角色与资源的绑定授权、日志审查及子域权限管理。通过应用角色关联、公共角色关联功能，将角色与菜单、按钮等资源精准绑定；基于组织、岗位、角色维度实施应用资源授权与数据目录授权，确保数据访问合规。同时负责审查普通用户及安全审计员的操作日志，对下级组织三员进行授权管理与权限配置，通过建立“角色-资源-岗位”的授权矩阵，实现权限的精细化分配与动态调整，筑牢系统安全防线。

（三）安全审计员

安全审计员承担全系统操作的监督职责，核心功能涵盖操作日志审查与工作流监控。可全面审查系统管理员、安全保密员的登录及操作日志，追溯权限配置与资源变更轨迹。

## 开源地址

源码地址：<https://github.com/risesoft-y9/Digital-Infrastructure>

## 源码目录

```
y9-digitalbase-common -- 系统公共模块
 ├── risenet-y9boot-3rd-jpa -- SpringDataJPA相关配置
 ├── risenet-y9boot-common-model -- 公共对象模型
 ├── risenet-y9boot-common-nacos -- nacos加解密包
 ├── risenet-y9boot-common-sqlddl -- 数据库操作工具类
 ├── risenet-y9boot-common-tenant-datasource -- 租户动态数据源包
 ├── risenet-y9boot-common-util -- 公共工具包
 ├── risenet-y9boot-properties -- 公共配置文件包
y9-digitalbase-example -- 基于数字底座的示例工程
 ├── risenet-y9demo-cache -- Redis 缓存使用示例
 ├── risenet-y9demo-data-jpa -- SpringDataJPA 使用示例
 ├── risenet-y9demo-elasticsearch -- SpringDataElasticsearch 使用示例
 ├── risenet-y9demo-file -- 文件服务使用示例
 ├── risenet-y9demo-idcode -- 统一码使用示例
 ├── risenet-y9demo-kernel-api -- 数字底座接口调用示例
 ├── risenet-y9demo-log -- 日志上报示例
 ├── risenet-y9demo-sso-oauth2 -- 数字底座 OAuth2 认证示例
 ├── risenet-y9demo-sync-kafka -- 数字底座组织信息同步( kafka 消息机制)示例
y9-digitalbase-starter -- 快速配置模块
 ├── risenet-y9boot-starter-apisix -- apisix 网关接口注册组件
 ├── risenet-y9boot-starter-cache-redis -- 数据缓存组件
 ├── risenet-y9boot-starter-elasticsearch -- 全文检索基本依赖封装
 ├── risenet-y9boot-starter-idgenerator -- 唯一标示生成组件
 ├── risenet-y9boot-starter-jpa-dedicated -- 指定库 jpa 组件
 ├── risenet-y9boot-starter-jpa-public -- 公共库 jpa 组件
 ├── risenet-y9boot-starter-jpa-tenant -- 多租户库 jpa 组件
 ├── risenet-y9boot-starter-kafka -- kafka通用消息监听组件(y9_common_event队列)
 ├── risenet-y9boot-starter-liquibase -- 数据表结构版本化控制组件
 ├── risenet-y9boot-starter-listener-kafka -- kafka组织信息消息监听组件(y9_org_event队列)
 ├── risenet-y9boot-starter-log -- 日志组件
 ├── risenet-y9boot-starter-multi-tenant -- 多租户相关组件
 ├── risenet-y9boot-starter-openfeign -- 远程调用组件
 ├── risenet-y9boot-starter-permission -- 权限组件
 ├── risenet-y9boot-starter-publish-kafka -- kafka发布消息组件
 ├── risenet-y9boot-starter-repeatsubmit -- 防重复请求组件
 ├── risenet-y9boot-starter-security -- 安全模块组件
 ├── risenet-y9boot-starter-sso-oauth2-resource -- OAuth2.0认证组件
 ├── risenet-y9boot-starter-web -- SpringMVC 相关配置组件
y9-digitalbase-support -- 业务支撑模块
 ├── risenet-y9boot-support-file-jpa-repository -- 文件信息存储模块
 ├── risenet-y9boot-support-file-service-ftp -- ftp文件服务器支持
 ├── risenet-y9boot-support-file-service-local -- 本地文件存储支持
 ├── risenet-y9boot-support-file-service-rest -- 远程文件存储支持，需结合 filemanager 使用
 ├── risenet-y9boot-support-history -- 实体审计日志组件
vue -- 前端工程
 ├── y9vue-kernel-standard -- 数字底座前端工程
y9-digitalbase-module -- 系统公共模块
 ├── y9-module-log -- 日志后端工程
 ├── y9-module-platform -- 数字底座后端工程
 ├── y9-module-sso -- 单点认证服务端，支持 OAuth2.0、CAS 等协议
```

## 内置功能

系统三员是系统默认生成的三个账号，包含系统管理员、安全保密员、安全审计员。

系统管理员：主要负责系统的配置和组织人员的管理

安全保密员：主要负责权限管理和子域三员管理（部门三员管理）以及查看安全审计员的日志和普通用户的日志

安全审计员：主要负责审查系统管理员的日志和安全保密员的日志

<table>
<tr>
  <td colspan="2" align="center">系统管理员</td>
</tr>
<tr>
  <td>控制台</td>
  <td>显示系统的监控统计信息，主要面向用户、应用和日志信息，可根据业务的实际需要进行增改。</td>
</tr>
<tr>
  <td>组织架构</td>
  <td>包括组织机构、部门、人员、部门领导的构建和管理，其中对组织、部门、人员进行树结构展现。</td>
</tr>
<tr>
  <td>组织岗位</td>
  <td>包括组织机构、部门、岗位、部门领导的构建和管理，其中对组织、部门、岗位进行树结构展现。</td>
</tr>
<tr>
  <td>职位管理</td>
  <td>职位构建和管理，是后续创建岗位的基础。</td>
</tr>
<tr>
  <td>应用系统管理</td>
  <td>对需授权的系统和应用进行构建和管理，支持在系统目录下面添加多级应用。</td>
</tr>
<tr>
  <td>应用角色管理</td>
  <td>对系统下属的应用角色进行构建和管理。</td>
</tr>
<tr>
  <td>公共角色管理</td>
  <td>对独立于所有系统之外的公共角色进行构建和管理。</td>
</tr>
<tr>
  <td>应用资源管理</td>
  <td>对系统下属的应用资源进行构建和管理。应用资源主要包含菜单、按钮等，支持对于内部资源的自由定义。</td>
</tr>
<tr>
  <td>数据目录管理</td>
  <td>采用组织部门、数据分类、时间周期模板的树形结构方式对数据进行编目设定，从而满足后续数据授权的要求。</td>
</tr>
<tr>
  <td>子域三员管理</td>
  <td>支持对于子组织、下属组织、二级组织的三员的构建与管理。</td>
</tr>
<tr>
  <td>权限树</td>
  <td>内含资源权限树和角色权限树，支持用户以列表和树的形式对全局的人员所持有的资源和角色进行详细查看。</td>
</tr>
<tr>
  <td>字典表管理</td>
  <td>对系统中经常使用的较为固定的数据进行维护和管理。</td>
</tr>
<tr>
  <td>图标管理</td>
  <td>对系统中的图标进行构建和管理，系统创建应用的时候可以从该图标库中选择。</td>
</tr>
<tr>
  <td>系统设置</td>
  <td>对系统进行个性化的设置，如用户初始化密码，岗位的格式等。</td>
</tr>
</table>

<table>
<tr>
  <td colspan="2" align="center">安全保密员</td>
</tr>
<tr>
  <td>应用角色关联</td>
  <td>展示已经构建的角色，将角色与资源进行绑定授权。</td>
</tr>
<tr>
  <td>公共角色关联</td>
  <td>展示已经构建的公共角色，将公共角色与资源进行绑定授权。</td>
</tr>
<tr>
  <td>应用资源授权</td>
  <td>展示已经构建的资源，将资源与角色、组织、岗位进行绑定授权。</td>
</tr>
<tr>
  <td>数据目录授权</td>
  <td>展示已经构建的数据目录，将数据目录与组织、岗位、角色进行绑定授权。</td>
</tr>
<tr>
  <td>子域三员管理</td>
  <td>对已经构建的子域三员进行绑定授权。</td>
</tr>
<tr>
  <td>权限树</td>
  <td>内含资源权限树和角色权限树，支持用户以列表和树的形式对全局的人员所持有的资源和角色进行详细查看。</td>
</tr>
<tr>
  <td>用户日志</td>
  <td>支持审查普通用户的登录日志和操作日志。支持增加针对应用相关的日志审查。</td>
</tr>
<tr>
  <td>安全审计员日志</td>
  <td>支持审查安全审计员的登录日志和操作日志。</td>
</tr>
</table>

<table>
<tr>
  <td colspan="2" align="center">安全审计员</td>
</tr>
<tr>
  <td>系统管理员日志</td>
  <td>可以审查系统管理员的登录日志和操作日志。</td>
</tr>
<tr>
  <td>安全保密员日志</td>
  <td>可以审查安全保密员的登录日志和操作日志。</td>
</tr>
<tr>
  <td>审计日志</td>
  <td>可以查看数字底座后台系统的关键操作日志以及操作具体的数据差异。</td>
</tr>
</table>

## 逻辑架构图

<div><img src="https://vue.youshengyun.com/files/img/digitalBase_logic_framework.png"><div/>

1. 数字底座围绕组织身份、应用资源、数据目录、安全防御、工具支撑等方向进行构建和管理

2. 数字底座的操作逻辑需要第三方应用接入单点登录，导入组织架构，按照岗位将人员和应用资源进行绑定

3. 数字底座基于三员管理，支持云租户模式，主要面向大规模分布式的数字化基础建设

## 功能架构图

<div><img src="https://vue.youshengyun.com/files/img/digitalBase_function_frameword.png"><div/>

1. 数字底座的功能分布按照三员管理的逻辑进行划分

2. 数字底座内置单点登录（统一身份认证）模块，实际使用也可以对接其他单点登录系统

## 部署架构图

<div><img src="https://vue.youshengyun.com/files/img/deploy1.png"><div/>

1. 数字底座支持以微服务、分布式的方式进行平行扩展

2. 数字底座支持容器化方式部署

3. 数字底座中需要单独部署单点登录服务器

4. 数字底座单体在信创环境中，4核8GB内存的虚拟机可以轻松应对2000用户

## 后端技术选型

| 序号 | 依赖              | 版本      | 官网                                                                                                                 |
|----|-----------------|---------|--------------------------------------------------------------------------------------------------------------------|
| 1  | Spring Boot     | 2.7.10  | <a href="https://spring.io/projects/spring-boot" target="_blank">官网</a>                                            |
| 2  | SpringDataJPA   | 2.7.10  | <a href="https://spring.io/projects/spring-data-jpa" target="_blank">官网</a>                                        |
| 3  | SpringDataRedis | 2.7.10  | <a href="https://spring.io/projects/spring-data-redis" target="_blank">官网</a>                                      |
| 4  | SpringKafka     | 2.8.11  | <a href="https://spring.io/projects/spring-kafka" target="_blank">官网</a>                                           |
| 5  | nacos           | 2.2.1   | <a href="https://nacos.io/zh-cn/docs/v2/quickstart/quick-start.html" target="_blank">官网</a>                        |
| 6  | druid           | 1.2.16  | <a href="https://github.com/alibaba/druid/wiki/%E9%A6%96%E9%A1%B5" target="_blank">官网</a>                          |
| 7  | Jackson         | 2.13.5  | <a href="https://github.com/FasterXML/jackson-core" target="_blank">官网</a>                                         |
| 8  | javers          | 6.13.0  | <a href="https://github.com/javers/javers" target="_blank">官网</a>                                                  |
| 9  | lombok          | 1.18.26 | <a href="https://projectlombok.org/" target="_blank">官网</a>                                                        |
| 10 | logback         | 1.2.11  | <a href="https://www.docs4dev.com/docs/zh/logback/1.3.0-alpha4/reference/introduction.html" target="_blank">官网</a> |

## 前端技术选型

| 序号 | 依赖           | 版本      | 官网                                                                     |
|----|--------------|---------|------------------------------------------------------------------------|
| 1  | vue          | 3.3.2   | <a href="https://cn.vuejs.org/" target="_blank">官网</a>                 |
| 2  | vite4        | 4.4.9   | <a href="https://vitejs.cn/" target="_blank">官网</a>                    |
| 3  | vue-router   | 4.0.13  | <a href="https://router.vuejs.org/zh/" target="_blank">官网</a>          |
| 4  | pinia        | 2.0.11  | <a href="https://pinia.vuejs.org/zh/" target="_blank">官网</a>           |
| 5  | axios        | 0.24.0  | <a href="https://www.axios-http.cn/" target="_blank">官网</a>            |
| 6  | typescript   | 4.5.4   | <a href="https://www.typescriptlang.org/" target="_blank">官网</a>       |
| 7  | core-js      | 3.20.1  | <a href="https://www.npmjs.com/package/core-js" target="_blank">官网</a> |
| 8  | element-plus | 2.2.29  | <a href="https://element-plus.org/zh-CN/" target="_blank">官网</a>       |
| 9  | sass         | 1.58.0  | <a href="https://www.sass.hk/" target="_blank">官网</a>                  |
| 10 | animate.css  | 4.1.1   | <a href="https://animate.style/" target="_blank">官网</a>                |
| 11 | vxe-table    | 4.3.5   | <a href="https://vxetable.cn" target="_blank">官网</a>                   |
| 12 | echarts      | 5.3.2   | <a href="https://echarts.apache.org/zh/" target="_blank">官网</a>        |
| 13 | svgo         | 1.3.2   | <a href="https://github.com/svg/svgo" target="_blank">官网</a>           |
| 14 | lodash       | 4.17.21 | <a href="https://lodash.com/" target="_blank">官网</a>                   |

## 中间件选型

| 序号 | 工具               | 版本   | 官网                                                                        |
|----|------------------|------|---------------------------------------------------------------------------|
| 1  | JDK              | 11   | <a href="https://openjdk.org/" target="_blank">官网</a>                     |
| 2  | Tomcat           | 9.0+ | <a href="https://tomcat.apache.org/" target="_blank">官网</a>               |
| 3  | Kafka            | 2.6+ | <a href="https://kafka.apache.org/" target="_blank">官网</a>                |
| 4  | filezilla server | 1.7+ | <a href="https://www.filezilla.cn/download/server" target="_blank">官网</a> |

## 数据库选型

| 序号 | 工具            | 版本         | 官网                                                                        |
|----|---------------|------------|---------------------------------------------------------------------------|
| 1  | Mysql         | 5.7 / 8.0+ | <a href="https://www.mysql.com/cn/" target="_blank">官网</a>                |
| 2  | Redis         | 6.2+       | <a href="https://redis.io/" target="_blank">官网</a>                        |
| 3  | elasticsearch | 7.9+       | <a href="https://www.elastic.co/cn/elasticsearch/" target="_blank">官网</a> |

## 数字底座专利

| 序&nbsp;号 | 专利号              | 专利名称                      |
|----------|------------------|---------------------------|
| 1        | ZL202111207338.0 | 《基于集合运算的资源授权方法及资源授权系统》    |
| 2        | ZL202210702228.X | 《一种静默化数据处理方法及处理系统》        |
| 3        | ZL202310030893.3 | 《基于多租户模式下的权限调度方法及调度系统》    |
| 4        | ZL202310238451.8 | 《一种基于前后端分离架构的前端双随机多态混淆方法》 |
| 5        | ZL202310238534.7 | 《多租户模式下数字底座子域三员架构模型的实现方法》 |

## 信创兼容适配

| **序号** | 类型   | 对象                 |
|:-------|------|--------------------|
| 1      | 浏览器  | 奇安信、火狐、谷歌、360等     |
| 2      | 插件   | 金山、永中、数科、福昕等       |
| 3      | 中间件  | 东方通、金蝶、宝兰德等        |
| 4      | 数据库  | 人大金仓、达梦、高斯等        |
| 5      | 操作系统 | 统信、麒麟、中科方德等        |
| 6      | 芯片   | ARM体系、MIPS体系、X86体系 |

<a name="在线体验"></a>

## 在线体验

演示地址：<https://demo.youshengyun.com/kernel-standard/>

> 演示账号：
>
> 系统管理员：systemManager 密码：Risesoft@2024
>
> 安全保密员：securityManager 密码：Risesoft@2024
>
> 安全审计员：auditManager 密码：Risesoft@2024
>

## 模板代码生成

地址：<https://gitee.com/risesoft-y9/y9-code-generator>
> 快速生成使用了数字底座相关组件的前后端工程

## 文档专区

开发文档：<https://docs.youshengyun.com/>
> 包含数字底座使用中间件的安装教程、数字底座重要配置讲解，进行本地化部署务必要浏览该文档，会少走弯路。

| 序号 | 名称                                                                                                           |
|----|--------------------------------------------------------------------------------------------------------------|
| 1  | <a href="https://vue.youshengyun.com/files/单点登录对接文档.pdf" target="_blank">单点登录对接文档</a>                        |
| 2  | <a href="https://vue.youshengyun.com/files/数字底座接口文档.pdf" target="_blank">数字底座接口文档</a>                        |
| 3  | <a href="https://vue.youshengyun.com/files/数字底座中间件安装文档（Linux）.pdf" target="_blank">数字底座中间件安装文档（Linux）</a>    |
| 4  | <a href="https://vue.youshengyun.com/files/数字底座源码部署文档.pdf" target="_blank">数字底座源码部署文档</a>                    |
| 5  | <a href="https://vue.youshengyun.com/files/操作使用文档（技术白皮书）.pdf" target="_blank">操作使用文档（技术白皮书）</a>              |
| 6  | <a href="https://vue.youshengyun.com/files/数字底座数据库设计文档.pdf" target="_blank">数字底座数据库设计文档</a>                  |
| 7  | <a href="https://vue.youshengyun.com/files/内部Java开发规范手册.pdf" target="_blank">内部Java开发规范手册</a>                |
| 8  | <a href="https://vue.youshengyun.com/files/日志组件使用文档.pdf" target="_blank">日志组件使用文档</a>                        |
| 9  | <a href="https://vue.youshengyun.com/files/文件组件使用文档.pdf" target="_blank">文件组件使用文档</a>                        |
| 10 | <a href="https://vue.youshengyun.com/files/接口安全控制使用文档.pdf" target="_blank">接口安全控制使用文档</a>                    |
| 11 | <a href="https://vue.youshengyun.com/files/代码生成器使用文档.pdf" target="_blank">代码生成器使用文档</a>                      |
| 12 | <a href="https://vue.youshengyun.com/files/配置文件说明文档.pdf" target="_blank">配置文件说明文档</a>                        |
| 13 | <a href="https://vue.youshengyun.com/files/常用工具类使用示例文档.pdf" target="_blank">常用工具类使用示例文档</a>                  |
| 14 | <a href="https://vue.youshengyun.com/files/有生博大Vue开发手册v1.0.pdf" target="_blank">前端开发手册</a>                   |
| 15 | <a href="https://vue.youshengyun.com/files/开发规范.pdf" target="_blank">前端开发规范</a>                              |
| 16 | <a href="https://vue.youshengyun.com/files/代码格式化.pdf" target="_blank">前端代码格式化</a>                            |
| 17 | <a href="https://vue.youshengyun.com/files/系统组件.pdf" target="_blank">前端系统组件</a>                              |
| 18 | <a href="https://vue.youshengyun.com/files/通用方法.pdf" target="_blank">前端通用方法</a>                              |
| 19 | <a href="https://vue.youshengyun.com/files/国际化.pdf" target="_blank">前端国际化</a>                                |
| 20 | <a href="https://vue.youshengyun.com/files/Icon图标.pdf" target="_blank">前端Icon图标</a>                          |
| 21 | <a href="https://vue.youshengyun.com/files/Oracle数据库适配文档.pdf" target="_blank">Oracle数据库适配文档</a>              |
| 22 | <a href="https://vue.youshengyun.com/files/Dameng数据库适配文档.pdf" target="_blank">Dameng数据库适配文档</a>              |
| 23 | <a href="https://vue.youshengyun.com/files/PostgreSQL数据库适配文档.pdf" target="_blank">PostgreSQL数据库适配文档</a>      |
| 24 | <a href="https://vue.youshengyun.com/files/Kingbase数据库适配文档.pdf" target="_blank">Kingbase数据库适配文档</a>          |
| 25 | <a href="https://vue.youshengyun.com/files/Mariadb数据库适配文档.pdf" target="_blank">Mariadb数据库适配文档</a>            |
| 26 | <a href="https://vue.youshengyun.com/files/OceanBase数据库适配文档.pdf" target="_blank">OceanBase数据库适配文档</a>        |
| 27 | <a href="https://docs.youshengyun.com/digitalbase/backend/hole/holeInfo" target="_blank">安全扫描漏洞问题及解决方案文档</a> | 

## 数字底座截图

#### 系统管理员界面截图

<table>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/img/1.png"></td>
        <td><img src="https://vue.youshengyun.com/files/img/2.png"></td>
    </tr>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/img/3.png"></td>
        <td><img src="https://vue.youshengyun.com/files/img/4.png"></td>
    </tr>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/img/5.png"></td>
        <td><img src="https://vue.youshengyun.com/files/img/6.png"></td>
    </tr>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/img/7.png"></td>
        <td><img src="https://vue.youshengyun.com/files/img/8.png"></td>
    </tr>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/img/9.png"></td>
    </tr>
</table>

#### 安全保密员界面截图

<table>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/img/21.png"></td>
        <td><img src="https://vue.youshengyun.com/files/img/22.png"></td>
    </tr>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/img/23.png"></td>
        <td><img src="https://vue.youshengyun.com/files/img/24.png"></td>
    </tr>
</table>

#### 安全审计员界面截图

<table>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/img/31.png"></td>
        <td><img src="https://vue.youshengyun.com/files/img/32.png"></td>
    </tr>
    <tr>
        <td><img src="https://vue.youshengyun.com/files/img/33.png"></td>
        <td><img src="https://vue.youshengyun.com/files/img/34.png"></td>
    </tr>
</table>

## 数字底座生态

<div><img src="https://vue.youshengyun.com/files/img/shengtai.png"><div/>

#### 工作桌面

##### 简介

工作桌面内置首页、内容管理、日程管理、统一待办、通讯录五大模块，支持拖拉拽和点选配置定制个性化的界面，方便集成各个应用、消息。

##### 截图

<div><img src="https://vue.youshengyun.com/files/img/desktop.png"><div/>
<div><img src="https://vue.youshengyun.com/files/img/cmsManage.png"><div/>
<div><img src="https://vue.youshengyun.com/files/img/bianjiwendang.png"><div/>

#### 工作流程

##### 简介

工作流程内置流程设计器、表单设计器、事项管理器三大工具，支持中国式流程的自由定义，具备细颗粒度的事项分类。

##### 截图

<div><img src="https://vue.youshengyun.com/files/img/gongzuoliucheng.png"><div/>
<div><img src="https://vue.youshengyun.com/files/img/liuchengsheji.png"><div/>
<div><img src="https://vue.youshengyun.com/files/img/biaodansheji.png"><div/>

#### 即时通讯

##### 简介

即时通讯仿照微信的设计体验和界面交互，支持私有化部署和多端同步（Web端、PC端、手机端），支持组织架构和应用系统的动态接入，易于集成消息、邮件、待办、日程等各类信息。

##### 截图

<div><img src="https://vue.youshengyun.com/files/img/jishitongxun.png"><div/>
<div><img src="https://vue.youshengyun.com/files/img/qunliao.png"><div/>

#### 网络硬盘

##### 简介

网络硬盘是一个私密安全的机关单位内部的知识共享和任务收发工具，支持分级权限管控和文件加密分享。

##### 截图

<div><img src="https://vue.youshengyun.com/files/img/wangluoyingpan.png"><div/>
<div><img src="https://vue.youshengyun.com/files/img/wenjianyidong.png"><div/>
<div><img src="https://vue.youshengyun.com/files/img/wenjiangongxiang.png"><div/>

#### 电子邮件

##### 简介

电子邮件支持内网高效模式和互联网协议模式，具备精简、安全和高效的功能模块。

##### 截图

<div><img src="https://vue.youshengyun.com/files/img/dianziyoujian.png"><div/>
<div><img src="https://vue.youshengyun.com/files/img/youjianxiangqing.png"><div/>
<div><img src="https://vue.youshengyun.com/files/img/tongxunlu.png"><div/>

## 数字化解决方案

#### 三座大山

<div><img src="https://vue.youshengyun.com/files/img/sanzuodashan.png"><div/>

#### 七步走

<div><img src="https://vue.youshengyun.com/files/img/qibuzou.png"><div/>

#### 三模态

<div><img src="https://vue.youshengyun.com/files/img/smt.png"><div/>

#### 信创安全

<div><img src="https://vue.youshengyun.com/files/img/xcyaq.png"><div/>

## 生态开源项目

| 序&nbsp;号 | 项&nbsp;目&nbsp;名&nbsp;称&nbsp; | 项目介绍                                                                                                                                                                                                         | 地&nbsp;址                                                                                                              |
|:---------|------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------|
| 1        | 工作流引擎                        | 工作流引擎对内提供单位/机关流程管理规则和内部业务流程的数字化落地实践；对外提供自动化地第三方业务驱动、接口接入和算法单元驱动能力；工作流引擎在提供底层驱动引擎的同时对全局透明监控、安全防御和国产化特色功能进行充分考虑，是内部流程管理和业务算法驱动的不二之选。                                                                           | [码云](https://gitee.com/risesoft-y9/y9-flowable) [GitHub](https://github.com/risesoft-y9/WorkFlow-Engine)              |
| 2        | 数据流引擎                        | 数据流引擎是一款面向数据集成、数据同步、数据交换、数据共享、任务配置、任务调度的底层数据驱动引擎。数据流引擎采用管执分离、多流层、插件库等体系应对大规模数据任务、数据高频上报、数据高频采集、异构数据兼容的实际数据问题。                                                                                                | [码云](https://gitee.com/risesoft-y9/y9-dataflow) [GitHub](https://github.com/risesoft-y9/DataFlow-Engine)              |
| 3        | 网络硬盘                         | 网络硬盘是通过存储、分类、检索、分享、协作、下发、回收等方式管理文档、文件、图片、音频、视频等资料的工具。网络硬盘擅长在国产的私有化环境中，管控文档权限、分配存储空间、安全加密、分享共享，同时可以完成一定轻量级的文件任务收发。网络硬盘是一个完全开源的项目，无商业版，但是需要依赖开源的数字底座进行人员岗位管控。                                                  | [码云](https://gitee.com/risesoft-y9/y9-storage) [GitHub](https://github.com/risesoft-y9/Network-Drive)                 |
| 4        | 电子邮件                         | 电子邮件是一款简化的具备邮件服务器的企业邮箱，支持在将其他主流邮箱的邮件进行导入后自主控制邮件数据安全。电子邮件以其简洁精确的功能和小巧安全的架构方便根据企业和政府机构的业务要求进行二次开发。电子邮件是一个完全开源的项目，无商业版，但是需要依赖开源的数字底座进行人员岗位管控。                                                                   | [码云](https://gitee.com/risesoft-y9/y9-email) [GitHub](https://github.com/risesoft-y9/Email)                           |
| 5        | 数据标注                         | 数据标注是一款专门对文本数据进行处理和标注的工具，通过简化快捷的文本标注流程和动态的算法反馈，支持用户快速标注关键词并能通过算法持续减少人工标注的成本和时间。数据标注的过程先由人工标注构筑基础，再由自动标注反哺人工标注，最后由人工标注进行纠偏，从而大幅度提高标注的精准度和高效性。数据标注是一个完全开源的项目，无商业版，但是需要依赖开源的数字底座进行人员岗位管控。数据标注的各类词库结果会定期在本平台中公开。 | [码云](https://gitee.com/risesoft-y9/y9-label) [GitHub](https://github.com/risesoft-y9/Data-Labeling)                   |
| 6        | 接口管理                         | 接口管理通过接口的注册、审批、集市和日志等方式对接口共享进行全生命周期的管理。接口管理注重流程化的接口共享模式和支持大规模接口转发的部署模式，从而面向多接口来源、高流量交互和复杂组织结构的场景。接口管理同时是一个轻量化的内核，支持各种面向特定场景的定制化业务改造。接口管理是一个完全开源的项目，无商业版，但是需要依赖开源的数字底座进行相关目录权限的管控。                            | [码云](https://gitee.com/risesoft-y9/y9-interface-platform) [GitHub](https://github.com/risesoft-y9/Interface-Platform) |

## 赞助与支持

### 中关村软件和信息服务产业创新联盟

官网：<a href="https://www.zgcsa.net" target="_blank">https://www.zgcsa.net</a>

### 北京有生博大软件股份有限公司

官网：<a href="https://www.risesoft.net/" target="_blank">https://www.risesoft.net/</a>

### 统一标识代码注册管理中心

官网：<a href="https://www.idcode.org.cn/" target="_blank">https://www.idcode.org.cn/</a>

>
数字底座已经全面接入统一标识码（MA码），具体使用说明请查看：<a href="https://gitee.com/risesoft-y9/y9-core/tree/main/y9-digitalbase-idcode" target="_blank">https://gitee.com/risesoft-y9/y9-core/tree/main/y9-digitalbase-idcode</a>
>

### 中国城市发展研究会

官网：<a href="https://www.china-cfh.com/" target="_blank">https://www.china-cfh.com/</a>

### 北京超维创想信息技术有限公司

官网：<a href="http://www.creatar.com/" target="_blank">http://www.creatar.com/</a>

### 深圳市北斗云信息技术有限公司

官网：<a href="https://www.northdoo.com/" target="_blank">https://www.northdoo.com/</a>

## 咨询与合作

联系人：曲经理

微信号：qq349416828

备注：开源咨询-姓名
<div><img style="width: 40%" src="https://vue.youshengyun.com/files/img/曲经理统一二维码咨询.png"><div/>
联系人：有生博大-咨询热线

座机号：010-86393151
<div><img style="width: 45%" src="https://vue.youshengyun.com/files/img/有生博大-咨询热线.png"><div/>
