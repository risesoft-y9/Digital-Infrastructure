# demo说明

## 主要流程说明

```
1、main.js传入单点登录必须的参数到插件
2、插件先在本地缓存一份
3、在插件的任意函数中从缓存中直接获取参数
```

## 主要的api

```
1、登录的API：ssoLoginApi 
2、检查登录的API：checkSsoLoginInfoApi
3、登录成功后获取access_token的API：ssoGetAccessTokenApi
4、拿到access_token后获取用户profile信息的API：ssoGetUserInfoApi

以下几个和redis有关api主要是用来解决多标签下的访问问题
5、ssoRedisSaveApi
6、ssoRedisRefreshApi
7、ssoRedisGetApi
8、ssoRedisDeleteApi
```

## 重要函数说明

```
1、checkToken
是vue框架中作为检查token的全局入口，缓存中有sso的token信息且没有过期返回true，无或者过期返回false

2、checkLogin
这个函数里包含项目中多个重定向场景，且有顺序排列，调试过程中，诸多问题根源都出自这里
```

## helpers.js里的函数都是一些助理函数，专门处理对应api的一些数据逻辑，比如登录账号密码加密，是index.js和ssoApi.js之间的助手

