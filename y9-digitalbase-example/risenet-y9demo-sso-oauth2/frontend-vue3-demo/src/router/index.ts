/*
 * @Author: your name
 * @Date: 2021-05-14 09:26:23
 * @LastEditTime: 2026-09-02 16:57:25
 * @LastEditors: mengjuhua
 * @Description: In User Settings Edit
 * @FilePath: \y9-cloud-v9.6.x\y9-digitalbase\y9-digitalbase-example\risenet-y9demo-sso-oauth2\frontend-vue3-demo\src\router\index.ts
 */

import { routerBeforeEach } from '@/router/checkRouter';
import { createRouter, createWebHistory } from 'vue-router';
import homeRouter from './modules/homeRouter';
import NProgress from 'nprogress';
//constantRoutes为不需要动态判断权限的路由，如登录、404、500等
export const constantRoutes: Array<any> = [
    {
        path: '/',
        name: 'index',
        hidden: true,
        redirect: '/demo'
    }
];

//asyncRoutes需求动态判断权限并动态添加的页面  这里的路由模块顺序也是菜单显示的顺序（位置：src->router->modules）
export const asyncRoutes = [...homeRouter];
// 引入其他模块路由

//创建路由模式，采用history模式没有“#”
const router = createRouter({
    history: createWebHistory(import.meta.env.VUE_APP_PUBLIC_PATH),
    routes: constantRoutes
});

//在用户点击前，进入routerBeforeEach去判断用户是否有权限
//全部判断逻辑请查看checkRouter.js
router.beforeEach(routerBeforeEach);
router.afterEach(() => {
    NProgress.done();
});
export default router;
