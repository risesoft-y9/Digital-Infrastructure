import '@/assets/css/nprogress.css'; // progress bar style
import router from '@/router';
import { checkRole } from '@/router/checkRole';
import { constantRoutes } from '@/router/index';
import NProgress from 'nprogress'; // progress bar
import { $y9_SSO } from '../main';

NProgress.configure({
    showSpinner: false,
    easing: 'ease',
    speed: 1000
});

// 路由白名单过滤
function routerWriteList(array, path) {
    let find = false;
    for (let index = 0; index < array.length; index++) {
        const item = array[index];
        if (item.path === path) {
            return true;
        }
        if (item.children) {
            find = routerWriteList(item.children, path);
        }
    }
    if (find) {
        return true;
    } else {
        return false;
    }
}

// 路由白名单
export async function checkWriteList(to, from) {
    // 白名单
    let isWriteList = routerWriteList(constantRoutes, to.path);
    if (isWriteList) {
        if (to.path === '/' || to.path === '/login') {
            // 登陆过 导航直接跳过login页面进入系统
            const isRight = await $y9_SSO.checkToken();
            if (isRight) {
                window.location = import.meta.env.VUE_APP_HOST_INDEX;
            }
        }
        return true;
    } else {
        return false;
    }
}

let userRole = ['user'];

async function check() {
    let isTokenValid, isRoleValid;

    // access_token 是否过期
    isTokenValid = await $y9_SSO.checkToken();
    // console.log(`isTokenValid=${isTokenValid}`);
    if (!isTokenValid) {
        return false;
    }

    isRoleValid = (await checkRole(userRole)) ? true : false;
    if (!isRoleValid) {
        return false;
    }
    // token在有效期且角色已获取路由
    return true;
}

let flag = 0;
export const routerBeforeEach = async (to, from) => {
    NProgress.start();
    console.log(to.path, from.path);
    // 检查路由白名单
    let isWriteRoute = await checkWriteList(to, from);
    // console.log(`isWriteList = ${isWriteRoute}`);
    if (isWriteRoute) {
        return true;
    }
    let CHECK = await check();
    // console.log(`CHECK = ${CHECK}`);
    if (CHECK) {
        // console.log(await router.getRoutes(),router,from,to, router.hasRoute(to.name));
        if (!to.name) {
            let array = await router.getRoutes();
            // console.log(to, array)
            array.forEach((item) => {
                if (item.path === to.path && item.name) {
                    // console.log("=====", router.hasRoute(item.name));
                    router.push({
                        name: item.name
                    });
                }
            });
        } else {
            return true;
        }
    } else {
        await $y9_SSO.checkLogin();
    }

    // 导航被中止时afterEach不会触发，手动结束进度条避免残留
    NProgress.done();
    return false;
};
