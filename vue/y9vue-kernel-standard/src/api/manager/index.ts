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
 * 根据管理员id，获取管理员信息
 * @param {*} managerId 管理员id
 * @returns
 */
export const getManagerById = async (managerId) => {
    return await platformRequest({
        url: '/api/rest/personalCenter/getManagerById',
        method: 'GET',
        cType: false,
        params: { 'managerId': managerId }
    });
};

/**
 * 验证密码是否正确
 * @param {*} personID
 * @param {*} passWord
 * @returns
 */
export const checkPassword = async (personId, password) => {
    return await platformRequest({
        url: '/api/rest/personalCenter/checkPassword',
        method: 'GET',
        cType: false,
        params: { 'personId': personId, 'password': password }
    });
};


/**
 * 保存人员信息
 * @param {*} params
 * @returns
 */
export const updateManager = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/personalCenter/updateManager',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 修改密码
 * @param {*} personId
 * @param {*} newPassword
 * @returns
 */
export const modifyPassword = async (personId,newPassword) => {
    const params = {
        personId: personId,
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

/**
 * 上传图片
 * @param {*} iconFile
 * @param {*} personID
 * @returns
 */
export const savePersonPhoto = async (iconFile, personId) => {
    const data = new FormData();
    data.append('iconFile', iconFile);
    data.append('personId', personId);
    return await platformRequest({
        url: '/api/rest/personalCenter/savePersonPhoto',
        method: 'POST',
        cType: false,
        data: data
    });
};
