import {createRouter, createWebHashHistory} from 'vue-router';
import {$y9_SSO} from '@/main';

const routes = [

    {
        path: '/',
        name: 'index',
        redirect: '/login' //重定向
    },
    {
        path: '/login', //登录页
        name: 'login',
        component: () => import('@/components/demo.vue')

    },

];

const router = createRouter({
    history: createWebHashHistory(),
    scrollBehavior: () => ({y: 0}),
    routes
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
function checkWriteList(to, from, next) {
    let whiteList = [];
    // 白名单
    let isWriteList = routerWriteList(whiteList, to.path);
    // if (isWriteList) {
    //     if (to.path === '/' || to.path === '/login') {
    //         console.log('登陆过 跳过login页面逻辑');
    //     } else {
    //         //
    //         next();
    //     }
    // }
}

let flag = 0;
router.beforeEach(async (to, from, next) => {
    // 检查路由白名单
    checkWriteList(to, from, next);
    let result = sessionStorage.getItem(import.meta.env.VUE_APP_SESSIONSTORAGE_GUID);
    // 没有缓存
    if (!result) {
        await $y9_SSO.checkLogin();
        return false;
    }
    // 有缓存
    if (result) {
        let isCasheRedis = await $y9_SSO.ssoRedisGetApi({
            key: sessionStorage.getItem(import.meta.env.VUE_APP_REDISKEY)
        }).then((res) => {
            return res;
        });
        if (isCasheRedis.success) {
            await $y9_SSO.checkToken();
            // await getLoginInfoApi();
            next();

        } else {
            sessionStorage.clear();
            await $y9_SSO.checkLogin();
            return false;
        }
    }
})


export default router;
