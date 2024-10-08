# 一、运行前提

```
这个项目里的配置文件是对应这套开源代码的后端代码，需要先在本地环境中，将后端的代码运行起来，才能运行起来看到页面效果
```

## 为了不依赖后端的代码【即本地不跑后端服务程序，只运行前端项目代码就看到页面和效果】

## npm install 或 pnpm install加载完依赖之后，您可以直接使用“npm run test”命令，来运行前端项目

```
1、用编辑器打开 .env.test 文件

2、对比.env.development文件，相信您会发现如何修改配置文件，适配您公司或客户的开发环境

3、将文件下的所有这个字符串替换为“https://vue.youshengyun.com/”

【因为此时用的是公司的测试环境的接口，可能会有改动，不一定及时更新，建议搭建本地环境，将此项目的后端代码跑起来】

【第一次运行程序会比较久，运行过一次，vite脚手架有过运行缓存后，相关的功能点选操作就会快很多。其次是接口请求时间问题。】
```

# 二、路由即菜单

```
开源内核框架中router与功能的中英文命名对应关系：
权限分配界面：  authRouter（初始化的权限管控，不展示）
首页：         homeRouter
菜单管理：     menuRouter
组织架构：     orgRouter
岗位管理：     positionRouter
角色管理：     roleRouter
三元管理：     threePowersRouter
资源管理：     resourceRouter
字典管理：     dictionaryRouter
权限树：       permissionTreeRouter
安全防护层：   protectionRouter
应用中心管理：  appCenterRouter
审计日志监测：  auditLogRouter
服务器监测：   serverMonitorRouter
接口监测：     interfaceMonitorRouter
用户在线监测：  onlineMonitorRouter
数据源管理：   dataSourceRouter
数据库表管理： dataBaseRouter
实体关系模型： erModelRouter
系统功能模型： systemModelRouter
```

# 三、统一框架说明

理念：基于前后端分离架构：基于微服务管理：基于统一框架开发流程模型开发方法：基于统一过程方法、原型开发方法、螺旋上升开发方法开发语言：基于 JavaScript、基于 vue 开发原则：基于业务、尽可能让代码简单，新手都能看懂和维护

## 目录结构

```
src\api:                接口目录
src\assets:             需打包编译的静态资源
src\components:         页面组件模块
src\language:           语言包
src\layouts:            前端框架多布局的核心代码
src\router:             前端页面组件路由
src\store:              工程的公共仓库
src\theme:              element-plus主题包
src\utils:              辅助函数
src\views:              页面组件及其子组件
App.vue:                根组件
main.ts:                项目程序入口文件
settings:               项目部分设置文件
.env.test:              测试环境配置文件
.env.development:       开发环境配置文件
.env.production:        生产环境配置文件
.eslintignore:          语法规范忽略文件
.eslintrc.js:           语法规范文件
.prettierignore:        代码风格配置忽略文件
prettier.config.js:     代码风格配置文件
README.md:              说明文档
tsconfig.json:          ts配置文件
vite.config.js:         vite脚手架各种环境编译配置文件
```

## 框架渲染过程

 <img src="https://vue.youshengyun.com/files/img/前端/框架渲染过程.png">

# 四、单点登录

## <a href="https://vue.youshengyun.com/files/img/前端/单点登录原理时序图.pdf" target="_blank">单点登录时序图</a>

<img src="https://vue.youshengyun.com/files/img/前端/单点登录原理时序图.png">

# 五、路由

## 路由程序设计流程图

<div align=center>
    <img src="https://vue.youshengyun.com/files/img/前端/路由程序流程图.png">
</div>

# 六、组件

## <a href="vue.youshengyun.com/y9vue-components/" target="_blank">组件说明文档</a>

```
组件插件化下载地址（私有仓库）：npm --registry https://svn.youshengyun.com:9900/nexus/repository/y9npm-hosted install y9plugin-components


<a href="https://vue.youshengyun.com/y9vue-components/used" target="_blank">组件详细安装使用步骤</a>

注意：
在下载的过程中，您可能遇到同单点登录插件类似的问题，参考解决即可，如遇到问题，期待您的主动联系反馈，我们将乐意为您解答！
```

# 七、开发规范

```
必要说明：
    1、eslink\ts\scss
    （<a href="https://vue.youshengyun.com/files/img/前端/字体大小设置的说明文档.pdf" target="_blank">2、全局字体大小统一设置规范，详细说明文档</a>）
    3、组件统一使用自己插件化开发的组件
    4、局部组件的css全部使用 scoped关键字作用域，lang=“scss” 不要求
    5、vite、vue、element-plus统一开发版本，版本问题统一升级维护
    6、页面设计按照公司前端的设计原则
    7、一个页面级菜单对应一个路由模块

最佳实践：
    1、下载干净的框架源码
    2、了解将要开发的项目需求
    3、设计项目菜单，即路由，补充项目路由（兼容静态路由、动态路由、异步路由、以及它们的组合）
        这里涉及的框架源码路由的修改可能比较复杂，视项目需求以及项目架构做适当修改
    4、开发对应路由的页面级组件以及其子组件
    5、项目开发将要完成时，视项目需求，做整个项目字体的适配
```

# 八、更新记录
> 2024-08-12 统一码对接；bug 修复；插件版本升级

> 2024-06-14 更新前端的配置文件和说明文档，以适配最新的开源代码

> 2024-05-20 图标库管理-->图标管理

> 2024-05-10 新增组织禁用，租户设置以及门属性类型使用字典表代替枚举以实现动态的增删

> 2023-12-19 优化个人中心页面，只需替换 src/view/personal 文件夹即可
