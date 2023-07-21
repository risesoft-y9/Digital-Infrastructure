/*
 * @Author: haifengy
 * @Date: 2022-03-28 09:49:09
 * @LastEditors: hongzhew
 * @LastEditTime: 2022-04-07 18:07:15
 * @Description:
 */

const dictionaryRouter = {
    path: '/dictionary',
    component: () => import("@/layouts/index.vue"),
    redirect: '/dictionary',
    name: 'dictionary',
    meta: {
        title: '字典表管理',
        roles: ['systemAdmin'],
        icon: 'ri-book-2-line'
    },
    children: [
        {
            path: '/dictionary/typeManage',
            component: () => import("@/views/dictionary/typeManage.vue"),
            name: 'dictionaryTypeManage',
            meta: {
                title: '字典表类型管理',
                icon: 'ri-book-2-line',
                roles: ['systemAdmin']
            }
        },
		{
		    path: '/dictionary/dataManage',
		    component: () => import("@/views/dictionary/dataManage.vue"),
		    name: 'dictionaryDataManage',
		    meta: {
		        title: '字典表数据管理',
		        icon: 'ri-book-open-line',
		        roles: ['systemAdmin']
		    }
		}
    ]
};

export default dictionaryRouter;
