/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2022-07-01 10:10:01
 * @LastEditors: mengjuhua
 * @LastEditTime: 2024-11-19 13:58:05
 * @FilePath: \y9-vue\y9vue-kernel-standard\src\api\appIcon\index.ts
 */
import Request from '@/api/lib/request';
import qs from 'qs';

const platformRequest = Request();
/**
 * 获取应用图标
 * @returns
 */
export const getAppIconById = async (id) => {
    return await platformRequest({
        url: '/api/rest/appIcon/getAppIconById',
        method: 'GET',
        cType: false,
        params: { id: id }
    });
};

/**
 * 查询所有父菜单分页列表
 * @param {*} page
 * @param {*} rows
 * @returns
 */
export const getAppIconPageList = async ({ page, rows }) => {
    return await platformRequest({
        url: '/api/rest/appIcon/pageAppIcons',
        method: 'GET',
        cType: false,
        params: { page: page, size: rows }
    });
};

/**
 * 按名字模糊图标
 * @param {*} name
 * @param {*} page
 * @param {*} rows
 * @returns
 */
export const searchIconPageByName = async ({ name, page, rows }) => {
    return await platformRequest({
        url: '/api/rest/appIcon/searchIconPageByName',
        method: 'GET',
        cType: false,
        params: { name: name, page: page, size: rows }
    });
};

/**
 * 上传图标
 * @param {*} data
 * @returns
 */
export const uploadIcon = async (data) => {
    // var data = new FormData();
    // for (var prop in iconFile) {
    //     if (iconFile[prop] instanceof File) {
    //         console.log(iconFile[prop]);
    //         data.append('colors', prop);
    //         data.append('iconFiles', iconFile[prop]);
    //     }
    // }
    return await platformRequest({
        url: '/api/rest/appIcon/uploadIcon4Colors',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 * 上传图标
 * @param {*} iconFile
 * @param {*} remark
 * @returns
 */
export const uploadIcon4Colors = async (params) => {
    var formData = new FormData();

    for (var prop in params) {
        if (Object.prototype.hasOwnProperty.call(params, prop)) {
            if (params[prop] instanceof File) {
                formData.append('colors', prop);
                formData.append('iconFiles', params[prop]);
            } else {
                formData.append(prop, params[prop]);
            }
        }
    }
    return await platformRequest({
        url: '/api/rest/appIcon/uploadIcon4Colors',
        method: 'POST',
        cType: false,
        data: formData
    });
};

/**
 * 保存图标修改信息
 * @param {*} name
 * @param {*} remark
 * @param {*} id
 * @returns
 */
export const saveIcon = async ({ name, remark, id }) => {
    const params = {
        name: name,
        remark: remark,
        id: id
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/appIcon/saveIcon',
        method: 'POST',
        cType: false,
        data: data
    });
};

/**
 *  删除图标
 * @param {*} id
 * @returns
 */
export const deleteIcon = async (id) => {
    const params = {
        id: id
    };
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/appIcon/deleteIcon',
        method: 'POST',
        cType: false,
        data: data
    });
};
