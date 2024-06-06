<p align="center">
 <img alt="logo" src="https://vue.youshengyun.com/files/img/qrCodeLogo.png">
</p>
<p align="center">基于SpringBoot+Vue前后端分离的Java快速开发框架</p>
<p align="center">
 <a href='https://gitee.com/risesoft-y9/y9-core/stargazers'><img src='https://gitee.com/risesoft-y9/y9-core/badge/star.svg?theme=dark' alt='star'></img></a>
    <img src="https://img.shields.io/badge/version-v9.6.3-yellow.svg">
    <img src="https://img.shields.io/badge/Spring%20Boot-2.7-blue.svg">
    <img alt="logo" src="https://img.shields.io/badge/Vue-3.3-red.svg">
    <img alt="" src="https://img.shields.io/badge/JDK-11-green.svg">
    <a href="https://gitee.com/risesoft-y9/y9-core/blob/master/LICENSE"><img src="https://img.shields.io/badge/license-GPL3-blue.svg"></a>
</p>

## 简介

数字底座是一款面向大型政府、企业数字化转型，基于身份认证、组织架构、岗位职务、应用系统、资源角色等功能构建的统一且安全的管理支撑平台。数字底座基于三员管理模式，具备微服务、多租户、容器化和国产化，支持用户利用代码生成器快速构建自己的业务应用，同时可关联诸多成熟且好用的内部生态应用。

## 源码目录

```
common -- 系统公共模块
 ├── risenet-y9boot-3rd-jpa -- SpringDataJPA相关配置
 ├── risenet-y9boot-common-model -- 公共对象模型
 ├── risenet-y9boot-common-nacos -- nacos加解密包
 ├── risenet-y9boot-common-tenant-datasource -- 租户动态数据源包
 ├── risenet-y9boot-common-util -- 公共工具包
 ├── risenet-y9boot-properties -- 公共配置文件包
example -- 基于数字底座的示例工程
 ├── risenet-y9demo-file -- 文件服务使用示例
 ├── risenet-y9demo-kernel-api -- 数字底座接口调用示例
 ├── risenet-y9demo-sso-oauth2 -- 数字底座OAuth2认证示例
 ├── risenet-y9demo-sync-kafka -- 数字底座组织信息同步(kafka消息机制)示例
starter -- 初始化模块
 ├── risenet-y9boot-starter-apisix -- 微服务api网关组件
 ├── risenet-y9boot-starter-cache-redis -- 缓存组件
 ├── risenet-y9boot-starter-elasticsearch -- 全文检索基本依赖封装
 ├── risenet-y9boot-starter-idgenerator -- 唯一标示生成组件
 ├── risenet-y9boot-starter-jpa-public -- 公共库组件
 ├── risenet-y9boot-starter-jpa-tenant -- 多租户相关组件
 ├── risenet-y9boot-starter-kafka -- kafka通用消息监听组件(y9_common_event队列)
 ├── risenet-y9boot-starter-liquibase -- 监听数据库结构变化组件
 ├── risenet-y9boot-starter-listener-kafka -- kafka组织信息消息监听组件(y9_org_event队列)
 ├── risenet-y9boot-starter-log -- 日志组件
 ├── risenet-y9boot-starter-multi-tenant -- 多租户相关组件
 ├── risenet-y9boot-starter-openfeign -- 远程调用组件
 ├── risenet-y9boot-starter-permission -- 权限组件
 ├── risenet-y9boot-starter-publish-kafka -- kafka发布消息组件
 ├── risenet-y9boot-starter-security -- 安全模块组件
 ├── risenet-y9boot-starter-sso-oauth2-resource -- OAuth2.0认证组件
 ├── risenet-y9boot-starter-web -- 全局的异常处理器
support -- 业务支撑模块
 ├── risenet-y9boot-support-file-jpa-repository -- 文件信息存储模块
 ├── risenet-y9boot-support-file-service-ftp -- 文件服务器支持
 ├── risenet-y9boot-support-history -- 实体审计日志组件
vue -- 前端工程
 ├── y9vue-kernel-standard -- 数字底座前端工程
webapp -- 系统公共模块
 ├── y9-module-log -- 日志后端工程
 ├── y9-module-platform -- 数字底座后端工程
 ├── y9-module-sso -- OAuth2.0认证服务端
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

- 图标管理

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

## 逻辑架构图

<div><img src="https://vue.youshengyun.com/files/img/ljjgt.png"><div/>

## 后端技术选型

| 序号 | 依赖            | 版本    | 官网                                                         |
| ---- | --------------- | ------- | ------------------------------------------------------------ |
| 1    | Spring Boot     | 2.7.10  | <a href="https://spring.io/projects/spring-boot" target="_blank">官网</a> |
| 2    | SpringDataJPA   | 2.7.10  | <a href="https://spring.io/projects/spring-data-jpa" target="_blank">官网</a> |
| 3    | SpringDataRedis | 2.7.10  | <a href="https://spring.io/projects/spring-data-redis" target="_blank">官网</a> |
| 4    | SpringKafka     | 2.8.11  | <a href="https://spring.io/projects/spring-kafka" target="_blank">官网</a> |
| 5    | nacos           | 2.2.1   | <a href="https://nacos.io/zh-cn/docs/v2/quickstart/quick-start.html" target="_blank">官网</a> |
| 6    | druid           | 1.2.16  | <a href="https://github.com/alibaba/druid/wiki/%E9%A6%96%E9%A1%B5" target="_blank">官网</a> |
| 7    | Jackson         | 2.13.5  | <a href="https://github.com/FasterXML/jackson-core" target="_blank">官网</a> |
| 8    | javers          | 6.13.0  | <a href="https://github.com/javers/javers" target="_blank">官网</a> |
| 9    | lombok          | 1.18.26 | <a href="https://projectlombok.org/" target="_blank">官网</a> |
| 10   | logback         | 1.2.11  | <a href="https://www.docs4dev.com/docs/zh/logback/1.3.0-alpha4/reference/introduction.html" target="_blank">官网</a> |

## 前端技术选型

| 序号 | 依赖         | 版本    | 官网                                                         |
| ---- | ------------ | ------- | ------------------------------------------------------------ |
| 1    | vue          | 3.3.2   | <a href="https://cn.vuejs.org/" target="_blank">官网</a>     |
| 2    | vite2        | 2.9.13  | <a href="https://vitejs.cn/" target="_blank">官网</a>        |
| 3    | vue-router   | 4.0.13  | <a href="https://router.vuejs.org/zh/" target="_blank">官网</a> |
| 4    | pinia        | 2.0.11  | <a href="https://pinia.vuejs.org/zh/" target="_blank">官网</a> |
| 5    | axios        | 0.24.0  | <a href="https://www.axios-http.cn/" target="_blank">官网</a> |
| 6    | typescript   | 4.5.4   | <a href="https://www.typescriptlang.org/" target="_blank">官网</a> |
| 7    | core-js      | 3.20.1  | <a href="https://www.npmjs.com/package/core-js" target="_blank">官网</a> |
| 8    | element-plus | 2.2.29  | <a href="https://element-plus.org/zh-CN/" target="_blank">官网</a> |
| 9    | sass         | 1.58.0  | <a href="https://www.sass.hk/" target="_blank">官网</a>      |
| 10   | animate.css  | 4.1.1   | <a href="https://animate.style/" target="_blank">官网</a>    |
| 11   | vxe-table    | 4.3.5   | <a href="https://vxetable.cn" target="_blank">官网</a>       |
| 12   | echarts      | 5.3.2   | <a href="https://echarts.apache.org/zh/" target="_blank">官网</a> |
| 13   | svgo         | 1.3.2   | <a href="https://github.com/svg/svgo" target="_blank">官网</a> |
| 14   | lodash       | 4.17.21 | <a href="https://lodash.com/" target="_blank">官网</a>       |

## 中间件选型

| 序号 | 工具             | 版本 | 官网                                                         |
| ---- | ---------------- | ---- | ------------------------------------------------------------ |
| 1    | JDK              | 11   | <a href="https://openjdk.org/" target="_blank">官网</a>      |
| 2    | Tomcat           | 9.0+ | <a href="https://tomcat.apache.org/" target="_blank">官网</a> |
| 3    | Kafka            | 2.6+ | <a href="https://kafka.apache.org/" target="_blank">官网</a> |
| 4    | filezilla server | 1.7+ | <a href="https://www.filezilla.cn/download/server" target="_blank">官网</a> |

## 数据库选型

| 序号 | 工具          | 版本       | 官网                                                         |
| ---- | ------------- | ---------- | ------------------------------------------------------------ |
| 1    | Mysql         | 5.7 / 8.0+ | <a href="https://www.mysql.com/cn/" target="_blank">官网</a> |
| 2    | Redis         | 6.2+       | <a href="https://redis.io/" target="_blank">官网</a>         |
| 3    | elasticsearch | 7.9+       | <a href="https://www.elastic.co/cn/elasticsearch/" target="_blank">官网</a> |

## 数字底座专利

| 序号 | 专利号           | 专利名称 |
| ----- | ---------------- | ----------------------- |
| 1    | ZL202111207338.0 | 《基于集合运算的资源授权方法及资源授权系统》       |
| 2    | ZL202210702228.X | 《一种静默化数据处理方法及处理系统》             |
| 3    | ZL202310030893.3 | 《基于多租户模式下的权限调度方法及调度系统》       |
| 4    | ZL202310238451.8 | 《一种基于前后端分离架构的前端双随机多态混淆方法》 |
| 5    | ZL202310238534.7 | 《多租户模式下数字底座子域三员架构模型的实现方法》 |

## 数字底座信创

| **序号** | 类型     | 对象                       |
| :------- | -------- | -------------------------- |
| 1        | 浏览器   | 奇安信、火狐、谷歌、360等  |
| 2        | 插件     | 金山、永中、数科、福昕等   |
| 3        | 中间件   | 东方通、金蝶、宝兰德等     |
| 4        | 数据库   | 人大金仓、达梦、高斯等     |
| 5        | 操作系统 | 统信、麒麟、中科方德等     |
| 6        | 芯片     | ARM体系、MIPS体系、X86体系 |

## 在线体验

演示地址：<a href="https://test.youshengyun.com/kernel-standard/" target="_blank">https://test.youshengyun.com/kernel-standard/</a>

> 演示账号：
>
> 系统管理员：systemManager  密码：Risesoft@2022
>
> 安全保密员：securityManager  密码：Risesoft@2022
>
> 安全审计员：auditManager   密码：Risesoft@2022
>
> 说明：输入登录名后，请选择"北京有生博大软件股份有限公司"租户，再输入密码进行登录

## 私有化部署

地址：<a href="https://test.youshengyun.com/y9vue-code/" target="_blank">https://test.youshengyun.com/y9vue-code/</a>

> 包含数字底座使用中间件的安装教程、数字底座重要配置讲解，进行本地化部署务必要浏览该网站，会少走弯路。

## 文档专区

| 序号 | 名称                                                                                              |
|:---|-------------------------------------------------------------------------------------------------|
| 1  | <a href="https://vue.youshengyun.com/files/单点登录对接文档.pdf" target="_blank">单点登录对接文档</a>           |
| 2  | <a href="https://vue.youshengyun.com/files/数字底座接口文档.pdf" target="_blank">数字底座接口文档</a>           |
| 3  | <a href="https://test.youshengyun.com/y9vue-code/digitalBase" target="_blank">安装部署文档</a>        |
| 4  | <a href="https://vue.youshengyun.com/files/操作使用文档（技术白皮书）.pdf" target="_blank">操作使用文档（技术白皮书）</a> |
| 5  | <a href="https://vue.youshengyun.com/files/数字底座数据库设计文档.pdf" target="_blank">数字底座数据库设计文档</a>     |
| 6  | <a href="https://vue.youshengyun.com/files/内部Java开发规范手册.pdf" target="_blank">内部Java开发规范手册</a>   |
| 7  | <a href="https://vue.youshengyun.com/files/日志组件使用文档.pdf" target="_blank">日志组件使用文档</a>           |
| 8  | <a href="https://vue.youshengyun.com/files/文件组件使用文档.pdf" target="_blank">文件组件使用文档</a>           |
| 9  | <a href="https://vue.youshengyun.com/files/代码生成器使用文档.pdf" target="_blank">代码生成器使用文档</a>         |
| 10 | <a href="https://vue.youshengyun.com/files/配置文件说明文档.pdf" target="_blank">配置文件说明文档</a>           |
| 11 | <a href="https://vue.youshengyun.com/files/常用工具类使用示例文档.pdf" target="_blank">常用工具类使用示例文档</a>     |
| 12 | <a href="https://vue.youshengyun.com/files/有生博大Vue开发手册v1.0.pdf" target="_blank">前端开发手册</a>      |
| 13 | <a href="https://vue.youshengyun.com/files/开发规范.pdf" target="_blank">前端开发规范</a>                 |
| 14 | <a href="https://vue.youshengyun.com/files/代码格式化.pdf" target="_blank">前端代码格式化</a>               |
| 15 | <a href="https://vue.youshengyun.com/files/系统组件.pdf" target="_blank">前端系统组件</a>                 |
| 16 | <a href="https://vue.youshengyun.com/files/通用方法.pdf" target="_blank">前端通用方法</a>                 |
| 17 | <a href="https://vue.youshengyun.com/files/国际化.pdf" target="_blank">前端国际化</a>                   |
| 18 | <a href="https://vue.youshengyun.com/files/Icon图标.pdf" target="_blank">前端Icon图标</a>             |
| 19 | <a href="https://vue.youshengyun.com/files/Oracle数据库适配文档.pdf" target="_blank">Oracle数据库适配文档</a>          |

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

<div><img src="https://vue.youshengyun.com/files/img/gongzuozhuomian.png"><div/>
<div><img src="https://vue.youshengyun.com/files/img/neirongguanli.png"><div/>
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

即时通讯仿照微信的体验和界面，支持私有化部署，支持多端同步，支持组织架构，易于接入或者关联各类应用消息。

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

## 助力政企数字化转型

#### 三座大山

<div><img src="https://vue.youshengyun.com/files/img/sanzuodashan.png"><div/>

#### 七步走

<div><img src="https://vue.youshengyun.com/files/img/qibuzou.png"><div/>

## 赞助与支持

### 中关村软件和信息服务产业创新联盟

官网：<a href="https://www.zgcsa.net" target="_blank">https://www.zgcsa.net</a>

> 介绍：中关村软件和信息服务产业创新联盟（简称中关村软联）是2013年7月在北京市民政局登记注册成立的全国性社团法人，主管单位为中关村管委会，在2023年被评为中国社会组织评估等级4A。中关村软联以自主原创型企业为龙头，设立了协同创新专业委员会，包括全过程咨询、数据治理、开源生态、金融、数字经济、信创、安全、人力资源、教育培训、法治合规、智能算法共十一大专委会。数字底座项目隶属中关村软联开源专委会孵化的开源项目，致力于服务国家政企持续的、全面的信创工作。
>

### 北京有生博大软件股份有限公司

官网：<a href="https://www.risesoft.net/" target="_blank">https://www.risesoft.net/</a>

> 介绍：北京有生博大软件股份有限公司（简称有生软件，英文缩写RiseSoft），成立于 1999 年，是一家专业的政务领域软件开发和数据服务供应商。有生软件以北、上、深的政府机构和政府行业管理部门为主要的服务对象，通过深圳、北京、山东三地研发中心持续研发信创“1+5”产品和数据产品，向全国 1500 多家党政机关和企事业单位提供相关的服务。数字底座项目的核心内容由有生软件发起、组织、开源并持续技术维护。
>

### 统一标识代码注册管理中心

官网：<a href="https://www.idcode.org.cn/" target="_blank">https://www.idcode.org.cn/</a>

> 介绍：标识代码是数字世界的身份证，可以为现实世界和虚拟世界有形或无形的对象分配全球唯一数字身份，是人、事、物、数据等对象的全球唯一数字身份证，可控安全、兼容开放、国际通用。MA码是中国首个获得ISO、CEN、AIM三大国际组织认可的全球顶级节点代码，是ISO/IEC 15459国际标准的组成部分。数字底座已经全面接入统一标识码（MA码），具体使用说明请查看：<a href="https://gitee.com/risesoft-y9/y9-core/tree/main/y9-digitalbase-idcode" target="_blank">https://gitee.com/risesoft-y9/y9-core/tree/main/y9-digitalbase-idcode</a>
>

## 咨询与合作

联系人：曲经理

微信号：qq349416828

备注：开源数字底座咨询-姓名
<div><img style="width: 40%" src="https://vue.youshengyun.com/files/img/曲经理-二维码.png"><div/>
联系人：有生博大-咨询热线

座机号：010-86393151
<div><img style="width: 45%" src="https://vue.youshengyun.com/files/img/有生博大-咨询热线.png"><div/>

