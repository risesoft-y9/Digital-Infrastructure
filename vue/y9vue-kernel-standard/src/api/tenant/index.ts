/*
 * @Author: your name
 * @Date: 2021-08-05 16:46:18
 * @LastEditTime: 2025-06-13 11:41:43
 * @LastEditors: mengjuhua
 * @Description: 租户管理
 */
import request from '@/api/lib/request';

const tenantRequest = request();

/**
 *  根据id，获取租户信息
 * @returns
 */
export const getTenantById = async (id) => {
    return await tenantRequest({
        url: '/api/rest/y9Tenant/getTenantById',
        method: 'GET',
        cType: false,
        params: { id: id }
    });
};

/**
 * 上传租户logo
 * @param {*} file
 * @returns
 */
export const uploadTenantLogoIcon = async (tenantId, file) => {
    var formData = new FormData();
    formData.append('tenantId', tenantId);
    formData.append('file', file);
    return await tenantRequest({
        url: '/api/rest/y9Tenant/uploadTenantLogoIcon',
        method: 'POST',
        cType: false,
        data: formData
    });
};

/**
 * 保存编辑过的租户信息
 * @param {*} params
 * @returns
 */
export const saveY9Tenant = async (params) => {
    const formData = new FormData();
    // 请求加入新参数
    for (var prop in params) {
        if (params.hasOwnProperty(prop)) {
            formData.append(prop, params[prop]);
        }
    }
    return await tenantRequest({
        url: '/api/rest/y9Tenant/saveTenant',
        method: 'POST',
        cType: false,
        data: formData
    });
};