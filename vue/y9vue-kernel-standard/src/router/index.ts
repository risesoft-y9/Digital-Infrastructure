/*
 * @Author: your name
 * @Date: 2021-05-14 09:26:23
 * @LastEditTime: 2022-08-02 11:13:19
 * @LastEditors: error: git config user.name && git config user.email & please set dead value or install git
 * @Description: In User Settings Edit
 * @FilePath: \workspace-y9boot-v9.5.x-vue\y9vue-info\src\router\index.js
 */

import { routerBeforeEach } from '@/router/checkRouter';
import NProgress from 'nprogress';
import { createRouter, createWebHistory } from 'vue-router';
import appResourceRouter from './modules/appResourceRouter';
import appRoleRouter from './modules/appRoleRouter';
import publicRoleRouter from './modules/publicRoleRouter';
import appSystemRouter from './modules/appSystemRouter';
import authRouter from './modules/authRouter';
import dictionaryRouter from './modules/dictionaryRouter';
import grantAuthorizeRouter from './modules/grantAuthorizeRouter';
import homeRouter from './modules/homeRouter';
import iconManagerRouter from './modules/iconManagerRouter';
import orgRouter from './modules/orgRouter';
import personalRouter from './modules/personalRouter';
import positionRouter from './modules/positionRouter';
import securityAuditorLogRouter from './modules/securityAuditorLogRouter';
import securityUserLogRouter from './modules/securityUserLogRouter';
import subDomainRouter from './modules/subDomainRouter';
import sysManagerLogRouter from './modules/sysManagerLogRouter';
import userLogRouter from './modules/userLogRouter';
import jobRouter from './modules/jobRouter';
import permission from './modules/permission';

//constantRoutes为不需要动态判断权限的路由，如登录、404、500等
export const constantRoutes: Array<any> = [
    {
        path: '/',
        name: 'index',
        hidden: true,
        redirect: '/auth'
    },
    {
        path: '/401',
        hidden: true,
        meta: {
            title: 'Not Permission'
        },
        component: () => import('@/views/401/index.vue')
    },
    {
        path: '/404',
        hidden: true,
        meta: {
            title: 'Not Found'
        },
        component: () => import('@/views/404/index.vue')
    },
    {
        path: '/password',
        hidden: true,
        meta: {
            title: 'Change Password'
        },
        component: () => import('@/views/password/index.vue')
    }
];

//asyncRoutes需求动态判断权限并动态添加的页面  这里的路由模块顺序也是菜单显示的顺序（位置：src->router->modules）
export const asyncRoutes = [
    ...authRouter,
    homeRouter,
    orgRouter,
    positionRouter,
    grantAuthorizeRouter,
    subDomainRouter,
    jobRouter,
    appSystemRouter,
    appRoleRouter,
    publicRoleRouter,
    appResourceRouter,
    permission,
    dictionaryRouter,
    iconManagerRouter,
    userLogRouter,
    sysManagerLogRouter,
    securityUserLogRouter,
    securityAuditorLogRouter,
    personalRouter
    // 引入其他模块路由
];

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
