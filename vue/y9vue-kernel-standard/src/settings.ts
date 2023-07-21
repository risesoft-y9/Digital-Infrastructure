/*
 * @Author: your name
 * @Date: 2022-01-13 17:37:14
 * @LastEditTime: 2022-01-17 18:35:30
 * @LastEditors: your name
 * @Description: 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 * @FilePath: /sz- team-frontend-9.6.x/y9vue-home/src/settings.ts
 */
import { RoutesDataItem } from "@/utils/routes";
/**
 * 站点配置
 * @author LiQingSong
 */
export interface SettingsType {
    /**
     * 站点名称
     */
    siteTitle: string;
  
    /**
     * 顶部菜单开启
     */
    topNavEnable: boolean;
  
    /**
     * 头部固定开启
     */
    headFixed: boolean;

    /**
     * tab菜单开启
     */
     tabNavEnable: boolean;

     /**
     * 站点首页路由
     */
    homeRouteItem: RoutesDataItem;
  
    /**
     * 站点本地存储Token 的 Key值
     */
    siteTokenKey: string;
  
    /**
     * Ajax请求头发送Token 的 Key值
     */
    ajaxHeadersTokenKey: string;
  
    /**
     * Ajax返回值不参加统一验证的api地址
     */
    ajaxResponseNoVerifyUrl: string[];

    /**
     * iconfont.cn 项目在线生成的 js 地址
     */
    iconfontUrl: string[];
}
  
const settings: SettingsType = {
    siteTitle: 'Y9-ADMIN-VUE',
    topNavEnable: true,
    headFixed: true,
    tabNavEnable: true,
    homeRouteItem: {
        icon: 'control',
        title: 'index-layout.menu.home.workplace',
        path: '/home/workplace',
        component: ()=> import('@/App.vue')
    },
    siteTokenKey: 'y9AT',
    ajaxHeadersTokenKey: 'x-token',
    ajaxResponseNoVerifyUrl: [
        '/user/login', // 用户登录
        '/user/info', // 获取用户信息
    ],
    iconfontUrl: [],
};

export default settings;
  