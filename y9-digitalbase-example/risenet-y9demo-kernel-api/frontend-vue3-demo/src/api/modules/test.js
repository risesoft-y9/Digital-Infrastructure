/*
 * @Author: qinman
 * @Date: 2022-09-30 09:03:10
 * @LastEditors: qinman
 * @LastEditTime: 2022-10-19 17:58:46
 * @Description: 
 * @FilePath: \frontend-vue3-demo\src\api\modules\test.js
 */
import request from "../request.js";

export const getUserInfo = async () => {
    return await request({
        url: '/userInfo',
        method: 'GET',
        cType: false,
    });
};

export const getParent = async () => {
    return await request({
        url: '/parent',
        method: 'GET',
        cType: false,
    });
};

export const getMenus = async () => {
    return await request({
        url: '/getMenus',
        method: 'GET',
        cType: false,
    });
};