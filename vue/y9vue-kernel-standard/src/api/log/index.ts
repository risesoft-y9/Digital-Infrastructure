/*
 * @Author: your name
 * @Date: 2021-07-07 18:01:58
 * @LastEditTime: 2023-02-27 15:49:44
 * @LastEditors: mengjuhua
 * @Description: In User Settings Edit
 * @FilePath: \vue-frontend-9.6.x\y9vue-kernel\src\api\log\index.ts
 */
import logRequest from '@/api/lib/logRequest';

/**
 * 查询用户日志
 * @param params
 * @param page
 * @param size
 * @returns
 */
export const searchLogInfoList4Users = async (params, page, size) => {
    const formData = new FormData();
    formData.append('page', page);
    formData.append('size', size);
    // 请求加入新参数
    for (var prop in params) {
        if (Object.prototype.hasOwnProperty.call(params, prop)) {
            formData.append(prop, params[prop]);
        }
    }
    return await logRequest({
        url: '/admin/accessLog/searchUsers',
        method: 'POST',
        cType: false,
        data: formData
    });
};

/**
 * 查询系统管理员日志
 * @param params
 * @param page
 * @param size
 * @returns
 */
export const searchLogInfoList4SystemManagers = async (params, page, size) => {
    const formData = new FormData();
    formData.append('page', page);
    formData.append('size', size);
    // 请求加入新参数
    for (var prop in params) {
        if (Object.prototype.hasOwnProperty.call(params, prop)) {
            formData.append(prop, params[prop]);
        }
    }
    return await logRequest({
        url: '/admin/accessLog/searchSystemManagers',
        method: 'POST',
        cType: false,
        data: formData
    });
};

/**
 * 查询安全保密员日志
 * @param params
 * @param page
 * @param size
 * @returns
 */
export const searchLogInfoList4SecurityManagers = async (params, page, size) => {
    const formData = new FormData();
    formData.append('page', page);
    formData.append('size', size);
    // 请求加入新参数
    for (var prop in params) {
        if (Object.prototype.hasOwnProperty.call(params, prop)) {
            formData.append(prop, params[prop]);
        }
    }
    return await logRequest({
        url: '/admin/accessLog/searchSecurityManagers',
        method: 'POST',
        cType: false,
        data: formData
    });
};

/**
 * 查询安全审计员日志
 * @param params
 * @param page
 * @param size
 * @returns
 */
export const searchLogInfoList4AuditManagers = async (params, page, size) => {
    const formData = new FormData();
    formData.append('page', page);
    formData.append('size', size);
    // 请求加入新参数
    for (var prop in params) {
        if (Object.prototype.hasOwnProperty.call(params, prop)) {
            formData.append(prop, params[prop]);
        }
    }
    return await logRequest({
        url: '/admin/accessLog/searchAuditManagers',
        method: 'POST',
        cType: false,
        data: formData
    });
};
