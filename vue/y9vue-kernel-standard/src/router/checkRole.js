/*
 * @Author: your name
 * @Date: 2021-12-22 15:41:55
 * @LastEditTime: 2023-08-03 10:24:58
 * @LastEditors: mengjuhua
 * @Description: 权限判定
 */
import router, { asyncRoutes } from '@/router';
import { useRouterStore } from '@/store/modules/routerStore';

/**
 * 根据 meta.role 判断当前用户是否有权限
 * @param roles 用户的权限
 * @param route 路由
 */
function hasPermission(roles, route) {
    if (route.meta && route.meta.roles) {
        return roles.some((role) => route.meta.roles.includes(role));
    } else {
        return true;
    }
}

/**
 * 根据权限 - 递归过滤异步路由 - 深度优先遍历 - 留下有权限的路由
 * @param routes asyncRoutes
 * @param roles
 */
function filterAsyncRoutes(routes, roles) {
    const res = [];

    routes.forEach((route) => {
        const tmp = { ...route };
        if (hasPermission(roles, tmp)) {
            if (tmp.children) {
                tmp.children = filterAsyncRoutes(tmp.children, roles);
            }
            res.push(tmp);
        }
    });
    return res;
}

/**
 * 获取对应权限路由
 * @param routes asyncRoutes
 * @param roles
 */
export function getPermissionRoutes(rolesArr = ['systemAdmin']) {
    const routerStore = useRouterStore();
    const roles = rolesArr;
    const permissionRoutes = filterAsyncRoutes(asyncRoutes, roles);
    // 项目存储中心 pinia - routerStore模块 存储有权限的所有路由源数据，permissionRoutes即包含项目所有可跳转的路由
    routerStore.$patch({
        PermissionRoutes: permissionRoutes
    });
    return permissionRoutes;
}

// 查询路由权限
export async function checkRole(rolesArr = ['systemAdmin']) {
    // 获取权限路由
    const permissionRoutes = getPermissionRoutes(rolesArr);
    if (permissionRoutes.length !== 0) {
        await permissionRoutes.map((route) => {
            router.addRoute(route);
        });
        return permissionRoutes;
    } else {
        return false;
    }
}
