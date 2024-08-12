/*
 * @Author: hongzhew
 * @Date: 2022-03-31 18:01:58
 * @LastEditors: hongzhew
 * @LastEditTime: 2022-04-07 18:04:42
 * @Description: 首页路由
 */

const homeRouter = {
    path: '/home',
    component: () => import('@/layouts/index.vue'),
    redirect: '/home',
    name: 'home',
    meta: {
        title: '控制台',
        roles: ['systemAdmin', 'subSystemAdmin']
    },
    children: [
        {
            path: '/home',
            component: () => import('@/views/home/index3.vue'),
            name: 'homeIndex',
            meta: {
                title: '控制台',
                icon: 'ri-equalizer-line',
                roles: ['systemAdmin', 'subSystemAdmin']
            }
        }
    ]
};

export default homeRouter;
