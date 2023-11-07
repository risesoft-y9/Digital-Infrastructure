<p align="center">
	<img alt="logo" src="https://vue.youshengyun.com/files/img/qrCodeLogo.png">
</p>
<h4 align="center">基于SpringBoot+Vue前后端分离的Java快速开发框架</h4>
<p align="center">
	<a href='https://gitee.com/risesoft-y9/y9-core/stargazers'><img src='https://gitee.com/risesoft-y9/y9-core/badge/star.svg?theme=dark' alt='star'></img></a>
    <img src="https://img.shields.io/badge/version-v9.6.3-yellow.svg">
    <img src="https://img.shields.io/badge/Spring%20Boot-2.7-blue.svg">
    <img alt="logo" src="https://img.shields.io/badge/Vue-3.3-red.svg">
    <img alt="" src="https://img.shields.io/badge/JDK-11-green.svg">
    <a href="https://gitee.com/risesoft-y9/y9-core/blob/master/LICENSE"><img src="https://img.shields.io/badge/license-GPL3-blue.svg"></a>
</p>



## 简介

<p style="text-indent:2em">数字底座是一款面向大型政府、企业数字化转型，基于身份认证、组织架构、岗位职务、应用系统、资源角色等功能构建的统一且安全的管理支撑平台。数字底座基于三员管理模式，具备微服务、多租户、容器化和国产化，支持用户利用代码生成器快速构建自己的业务应用，同时可关联诸多成熟且好用的内部生态应用。</p>
## 数字底座专利

| 专利号              | 专利名称                                           |
| ------------------- | -------------------------------------------------- |
| ZL 2021 1 1207338.0 | 《基于集合运算的资源授权方法及资源授权系统》       |
| ZL 2022 1 0702228.X | 《一种静默化数据处理方法及处理系统》               |
| ZL 2023 1 0030893.3 | 《基于多租户模式下的权限调度方法及调度系统》       |
| ZL 2023 1 0238451.8 | 《一种基于前后端分离架构的前端双随机多态混淆方法》 |
| ZL 2023 1 0238534.7 | 《多租户模式下数字底座子域三员架构模型的实现方法》 |



## 数字底座信创适配

| 类型     | 对象                       |
| -------- | -------------------------- |
| 浏览器   | 奇安信、火狐、谷歌、360等  |
| 插件     | 金山、永中、数科、福昕等   |
| 中间件   | 东方通、金蝶、宝兰德等     |
| 数据库   | 人大金仓、达梦、高斯等     |
| 操作系统 | 统信、麒麟、中科方德等     |
| 芯片     | ARM体系、MIPS体系、X86体系 |



## 后端技术选型

| 依赖            | 版本    |
| --------------- | ------- |
| Spring Boot     | 2.7.10  |
| SpringDataJPA   | 2.7.10  |
| SpringDataRedis | 2.7.10  |
| SpringKafka     | 2.8.11  |
| nacos           | 2.2.1   |
| druid           | 1.2.16  |
| Jackson         | 2.13.5  |
| javers          | 6.13.0  |
| lombok          | 1.18.26 |
| logback         | 1.2.11  |

## 前端技术选型

|   依赖        |   版本   |
| -------      | ------   |
|   vue        | 3.3.2    |
|  vite2       | 2.9.13   |
|  vue-router  | 4.0.13   |
|  pinia       | 2.0.11   |
|  axios       | 0.24.0   |
|  typescript  | 4.5.4    |
|  core-js     | 3.20.1   |
|  element-plus| 2.2.29   |
|  sass        | 1.58.0   |
|  animate.css | 4.1.1    |
|  vxe-table   | 4.3.5    |
|  echarts     | 5.3.2    |
|  svgo        | 1.3.2    |
|  lodash      | 4.17.21  |

## 中间件

| 工具              | 版本       |
| ---------------- | ---------- |
| JDK              | 11         |
| Tomcat           | 9.0+       |
| Mysql            | 5.7 / 8.0+ |
| Redis            | 6.2+       |
| Kafka            | 2.6+       |
| elasticsearch    | 7.9+       |
| filezilla server | 1.7+       |

## 源码目录

```
common -- 系统公共模块
	├── risenet-y9boot-3rd-jpa -- SpringDataJPA相关配置
	├── risenet-y9boot-common-model -- 公共对象模型
	├── risenet-y9boot-common-nacos -- nacos加解密包
	├── risenet-y9boot-common-tenantDataSource -- 租户动态数据源包
	├── risenet-y9boot-common-util -- 公共工具包
	├── risenet-y9boot-properties -- 公共配置文件包
example -- 基于数字底座的示例工程
	├── risenet-y9demo-kernel-api -- 数字底座接口调用示例
	├── risenet-y9demo-sso-oauth2 -- 数字底座OAuth2认证示例
	├── risenet-y9demo-sync-kafka -- 数字底座组织信息同步(kafka消息机制)示例
rest -- 对外提供接口模块
	├── risenet-y9boot-rest-feignClient-platform -- 基于feignClient实现
	├── risenet-y9boot-rest-interface-platform -- 对外提供接口包
starter -- 初始化模块
	├── risenet-y9boot-starter-apisix -- 微服务api网关组件
	├── risenet-y9boot-starter-cache-redis -- 缓存组件
	├── risenet-y9boot-starter-elasticsearch -- 全文检索基本依赖封装
	├── risenet-y9boot-starter-idGenerator -- 唯一标示生成组件
	├── risenet-y9boot-starter-jpa-public -- 公共库组件
	├── risenet-y9boot-starter-jpa-tenant -- 多租户相关组件
	├── risenet-y9boot-starter-kafka -- kafka通用消息监听组件(y9_common_event队列)
	├── risenet-y9boot-starter-listener-kafka -- kafka组织信息消息监听组件(y9_org_event队列)
	├── risenet-y9boot-starter-log -- 日志组件
	├── risenet-y9boot-starter-permission -- 权限组件
	├── risenet-y9boot-starter-publish-kafka -- kafka发布消息组件
	├── risenet-y9boot-starter-security -- 安全模块组件
	├── risenet-y9boot-starter-sso-oauth2-resource -- OAuth2.0认证组件
	├── risenet-y9boot-starter-web -- 全局的异常处理器
support -- 业务支撑模块
	├── risenet-y9boot-support-file-jpaRepository -- 文件信息存储模块
	├── risenet-y9boot-support-fileService-FTP -- 文件服务器支持
	├── risenet-y9boot-support-history -- 实体审计日志组件
	├── risenet-y9boot-support-log-elasticSearch -- 日志组件
	├── risenet-y9boot-support-platform-jpaRepository -- 数字底座持久层
	├── risenet-y9boot-support-platform-service -- 数字底座业务层
	├── risenet-y9boot-support-platform-web -- 数字底座控制层
vue -- 前端工程
	├── y9vue-kernel-standard -- 数字底座前端工程
webapp -- 系统公共模块
	├── risenet-y9boot-webapp-log -- 日志后端工程
	├── risenet-y9boot-webapp-platform -- 数字底座后端工程
	├── risenet-y9boot-webapp-sso-server-6.6.x -- OAuth2.0认证服务端
```



## 内置功能

系统三员是系统默认生成的三个账号，包含系统管理员、安全保密员、安全审计员。

系统管理员：主要负责系统的配置和组织人员的管理

安全保密员：主要负责权限管理和子域三员管理（部门三员管理）以及查看安全审计员的日志和普通用户的日志

安全审计员：主要负责审查系统管理员的日志和安全保密员的日志

#### 系统管理员

- 控制台

  显示系统总体信息，目前是一个模板页面，可以根据自己的实际需要进行改造。

- 组织架构

  包括组织机构、部门、人员、部门领导的管理，其中组织、部门、人员树结构展现。

- 组织岗位

  包括组织机构、部门、岗位、部门领导的管理，其中组织、部门、岗位树结构展现。

- 应用系统管理

  需要授权的系统的管理，可以添加系统并在系统下面添加应用。

- 应用角色管理

  对系统下面的应用的角色进行管理。

- 应用资源管理

  对系统下面的应用的资源进行管理。

- 字典表管理

  对系统中经常使用的一些较为固定的数据进行维护。

- 图标库管理

  对系统中的图标进行管理，系统创建应用的时候可以从该图标库中选择。

#### 安全保密员

- 授权管理

  针对应用中的菜单，授权给拥有某些角色的用户。

- 子域三员管理

  对部门的三员进行管理。

- 用户日志

  可以审查普通用户的登录日志以及操作日志。

- 安全审计员日志

  可以审查安全审计员的登录日志以及操作日志。

#### 安全审计员

- 系统管理员日志

  可以审查系统管理员的登录日志以及操作日志。

- 安全保密员日志

  可以审查安全保密员的登录日志以及操作日志。

##  在线体验

官网地址：<a href="https://www.risesoft.net/" target="_blank">https://www.risesoft.net/</a>

白皮书：<a href="https://vue.youshengyun.com/files/Y9-DI-V5.0.pdf" target="_blank">有生云-数字底座软件（标准版）白皮书下载</a>

演示地址：<a href="https://dev.youshengyun.com/kernel-standard/" target="_blank">https://dev.youshengyun.com/kernel-standard/</a>

> 演示账号：
>
> 系统管理员：systemManager		密码：Risesoft@2022
>
> 安全保密员：securityManager		密码：Risesoft@2022
>
> 安全审计员：auditManager			密码：Risesoft@2022
>
> 说明：输入登录名后，请选择"有生数字底座"租户，再输入密码进行登录

## 私有化部署

地址：<a href="https://dev.youshengyun.com/y9vue-code/" target="_blank">https://dev.youshengyun.com/y9vue-code/</a>

> 包含数字底座使用中间件的安装教程、数字底座重要配置讲解，进行本地化部署务必要浏览该网站，会少走弯路。

## 演示图

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



## 数字底座交流群

QQ群

<a href="https://jq.qq.com/?_wv=1027&k=9ozxloLD" target="_blank" title="点我申请入群"><img src="https://vue.youshengyun.com/files/img/qq.png"></a>

QQ群二维码

<div><img src="https://vue.youshengyun.com/files/img/qqQRCode.png" width="200" height="200"><div/>
微信群二维码

<div><img src="https://vue.youshengyun.com/files/img/wxQRCode.png" width="200" height="200"><div/>