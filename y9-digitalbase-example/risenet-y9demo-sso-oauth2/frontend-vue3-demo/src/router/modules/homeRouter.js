/*
 * @Author: mengjuhua
 * @Date: 2023-02-23 16:21:02
 * @LastEditors: mengjuhua
 * @Description:
 */
const homeRouter = [
    {
        path: '/demo',
        component: () => import('@/components/demo.vue'),
        name: 'demo',
        meta: {
            title: '登录信息',
            icon: 'ri-contacts-line'
        }
    }
];

export default homeRouter;
