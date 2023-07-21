/*
 * @Author: your name
 * @Date: 2021-07-07 18:01:58
 * @LastEditTime: 2023-02-27 15:51:09
 * @LastEditors: mengjuhua
 * @Description: In User Settings Edit
 * @FilePath: \vue-frontend-9.6.x\y9vue-kernel\src\api\userLoginInfo\index.ts
 */
import logRequest from '@/api/lib/logRequest';

/**
 * 获取一般用户登录日志
 * @param {*} page 
 * @param {*} size 
 * @returns 
 */
export const getLoginInfoList4users = async (userId, page, size) => {
    return await logRequest({
        url: "/admin/userLoginInfo/pageByUsers",
        method: 'GET',
        cType: false,
        params: { userId: userId, 'page': page, 'size': size },
    });
};

/**
 * 查询用户登录日志
 * @param params 条件对象
 * @param page 
 * @param size 
 * @returns 
 */
export const searchLoginInfoList4Users = async (params, page, size) => {
    const formData = new FormData();
    formData.append("page", page);
    formData.append("size", size);

    // 请求加入新参数
    for (var prop in params) {
        if (Object.prototype.hasOwnProperty.call(params, prop)) {
            formData.append(prop, params[prop]);
        }
    }
    return await logRequest({
        url: "/admin/userLoginInfo/searchUsers",
        method: 'POST',
        cType: false,
        data: formData,
    });
};

/**
 * 获取系统管理员登录日志
 * @param {*} page 
 * @param {*} size 
 * @returns 
 */
export const getLoginInfoList4SystemManagers = async (userId, page, size) => {
    return await logRequest({
        url: "/admin/userLoginInfo/pageBySystemManagers",
        method: 'GET',
        cType: false,
        params: { userId: userId, 'page': page, 'size': size },
    });
};

/**
 * 查询系统管理员登录日志
 * @param params 条件对象
 * @param page 
 * @param size 
 * @returns 
 */
export const searchLoginInfoList4SystemManagers = async (params, page, size) => {
    const formData = new FormData();
    formData.append("page", page);
    formData.append("size", size);
    // 请求加入新参数
    for (var prop in params) {
        if (Object.prototype.hasOwnProperty.call(params, prop)) {
            formData.append(prop, params[prop]);
        }
    }
    return await logRequest({
        url: "/admin/userLoginInfo/searchSystemManagers",
        method: 'POST',
        cType: false,
        data: formData,
    });
};

/**
 * 获取安全保密员登录日志
 * @param {*} page 
 * @param {*} size 
 * @returns 
 */
export const getLoginInfoList4SecurityManagers = async (userId, page, size) => {
    return await logRequest({
        url: "/admin/userLoginInfo/pageBySecurityManagers",
        method: 'GET',
        cType: false,
        params: { userId: userId, 'page': page, 'size': size },
    });
};

/**
 * 查询安全保密员登录日志
 * @param params 条件对象
 * @param page 
 * @param size 
 * @returns 
 */
export const searchLoginInfoList4SecurityManagers = async (params, page, size) => {
    const formData = new FormData();
    formData.append("page", page);
    formData.append("size", size);
    // 请求加入新参数
    for (var prop in params) {
        if (Object.prototype.hasOwnProperty.call(params, prop)) {
            formData.append(prop, params[prop]);
        }
    }
    return await logRequest({
        url: "/admin/userLoginInfo/searchSecurityManagers",
        method: 'POST',
        cType: false,
        data: formData,
    });
};

/**
 * 获取安全审计员登录日志
 * @param {*} page 
 * @param {*} size 
 * @returns 
 */
export const getLoginInfoList4AuditManagers = async (userId, page, size) => {
    return await logRequest({
        url: "/admin/userLoginInfo/pageByAuditManagers",
        method: 'GET',
        cType: false,
        params: { userId: userId, 'page': page, 'size': size },
    });
};

/**
 * 查询安全审计员登录日志
 * @param params 条件对象
 * @param page 
 * @param size 
 * @returns 
 */
export const searchLoginInfoList4AuditManagers = async (params, page, size) => {
    const formData = new FormData();
    formData.append("page", page);
    formData.append("size", size);
    // 请求加入新参数
    for (var prop in params) {
        if (Object.prototype.hasOwnProperty.call(params, prop)) {
            formData.append(prop, params[prop]);
        }
    }
    return await logRequest({
        url: "/admin/userLoginInfo/searchAuditManagers",
        method: 'POST',
        cType: false,
        data: formData,
    });
};
