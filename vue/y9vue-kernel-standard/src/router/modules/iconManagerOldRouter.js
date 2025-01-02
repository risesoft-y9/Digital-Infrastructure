/*
 * @Author: haifengy
 * @Date: 2022-03-28 09:49:09
 * @LastEditors: hongzhew
 * @LastEditTime: 2022-04-07 18:07:15
 * @Description:
 */

const iconManagerOldRouter = {
    path: '/iconManager-old',
    component: () => import('@/layouts/index.vue'),
    redirect: '/iconManager-old',
    name: 'iconManager-old',
    meta: {
        title: '图标管理',
        roles: ['systemAdmin']
    },
    children: [
        {
            path: '/iconManager-old',
            component: () => import('@/views/iconManager/index-old.vue'),
            name: 'iconManagerIndex-old',
            meta: {
                title: '图标管理(原)',
                icon: 'ri-remixicon-line',
                roles: ['systemAdmin']
            }
        }
    ]
};

export default iconManagerOldRouter;
