/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2022-04-28 15:09:08
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2022-05-17 10:00:22
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-kernel\src\api\manager\index.ts
 */
import Request from '@/api/lib/request';
import qs from 'qs';

const platformRequest = Request();
/**
 * 获取当前登录管理员的个人资料，用户 ID 由后端获取。
 * @returns
 */
export const getCurrentManager = async () => {
    return await platformRequest({
        url: '/api/rest/personalCenter/getCurrentManager',
        method: 'GET',
        cType: false
    });
};

/**
 * 按原有字段保存当前管理员资料，用户 ID 由后端获取。
 * @param {*} params 管理员资料
 * @returns
 */
export const updateManager = async (params) => {
    const profile = { ...params };
    delete profile.id;
    const data = qs.stringify(profile);
    return await platformRequest({
        url: '/api/rest/personalCenter/updateManager',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 修改当前登录管理员的密码，用户 ID 由后端从登录上下文获取。
 * @param {*} oldPassword 原密码（明文）
 * @param {*} newPassword
 * @returns
 */
export const modifyPassword = async (oldPassword, newPassword) => {
    const params = {
        oldPassword: oldPassword,
        newPassword: newPassword
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/personalCenter/modifyPassword',
        method: 'POST',
        cType: false,
        data: data
    });
};

// 检验三员密码是否过期
export const verifyPasswordExpiration = async () => {
    return await platformRequest({
        url: '/api/rest/personalCenter/isPasswordExpired',
        method: 'GET',
        cType: false
    });
};

/** 获取当前租户的密码强度要求。 */
export const getPasswordStrength = async () => {
    return await platformRequest({
        url: '/api/rest/personalCenter/getPasswordStrength',
        method: 'GET',
        cType: false
    });
};
