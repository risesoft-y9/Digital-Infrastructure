import Request from '@/api/lib/request';
import qs from 'qs';

const systemRequest = Request();

// 系统列表
export const systemList = async () => {
    return await systemRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/system/list',
        url: '/api/rest/system/list2',
        method: 'get',
        cType: false,
        params: {}
    });
};

// 系统 树 二级
export const appSourceList = async (systemId) => {
    return await systemRequest({
        // url: `http://127.0.0.1:4523/mock/891645/platform/api/rest/resource/treeRoot/${systemId}`,
        url: `/api/rest/resource/treeRoot/${systemId}`,
        method: 'get',
        cType: false
    });
};

// 新增系统
export const systemAdd = async (params) => {
    const data = qs.stringify(params);
    return await systemRequest({
        url: '/api/rest/system/save',
        method: 'post',
        cType: false,
        data
    });
};

// 系统详情
export const systemInfoGet = async (systemId) => {
    return await systemRequest({
        // url: `http://127.0.0.1:4523/mock/891645/platform/api/rest/system/${systemId}`,
        url: `/api/rest/system/${systemId}`,
        method: 'get',
        cType: false
    });
};

// 禁用系统
export const systemDisabled = async (id) => {
    const data = qs.stringify({ id });
    return await systemRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/system/disable',      // apifox - mock
        url: '/api/rest/system/disable',
        method: 'post',
        cType: false,
        data
    });
};

// 启用 系统
export const systemEnabled = async (id) => {
    const data = qs.stringify({ id });
    return await systemRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/system/enable',      // apifox - mock
        url: '/api/rest/system/enable',
        method: 'post',
        cType: false,
        data
    });
};

// 系统排序
export const systemSaveOrder = async (systemIds) => {
    return await systemRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/system/enable',      // apifox - mock
        url: '/api/rest/system/saveOrder',
        method: 'post',
        cType: false,
        params: { systemIds: systemIds }
    });
};

// 导入 系统
export const importSystemJSON = async (file) => {
    var formData = new FormData();
    formData.append('file', file);
    return await systemRequest({
        url: '/api/rest/impExp/importSystemJSON',
        method: 'POST',
        cType: false,
        data: formData
    });
};

/**
 * 删除系统
 * @param {*} params
 * @returns
 */
export const removeSystem = async (id) => {
    const data = qs.stringify({ id });
    return await systemRequest({
        // url: "http://127.0.0.1:4523/mock/891645/platform/api/rest/system/delete",
        url: '/api/rest/system/delete',
        method: 'POST',
        cType: false,
        data
    });
};

// 应用列表
export const applicationList = async (params) => {
    return await systemRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/resource/app/page',
        url: '/api/rest/resource/app/page',
        method: 'GET',
        cType: false,
        params
    });
};

// 新增应用
export const applicationAdd = async (params) => {
    const data = qs.stringify(params);
    return await systemRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/resource/app/save',
        url: '/api/rest/resource/app/save',
        method: 'post',
        cType: false,
        data
    });
};

// 删除应用
export const applicationDel = async (idArr) => {
    const params = {
        ids: idArr.join()
    };
    const data = qs.stringify(params);
    return await systemRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/resource/app/delete',
        url: '/api/rest/resource/app/delete',
        method: 'post',
        cType: false,
        data
    });
};

// 禁用 应用
export const applicationDisable = async (idArr) => {
    const params = {
        ids: idArr.join()
    };
    const data = qs.stringify(params);
    return await systemRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/resource/app/disable',
        url: '/api/rest/resource/app/disable',
        method: 'post',
        cType: false,
        data
    });
};

// 启用 应用
export const applicationEnable = async (idArr) => {
    const params = {
        ids: idArr.join()
    };
    const data = qs.stringify(params);
    return await systemRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/resource/app/enable',
        url: '/api/rest/resource/app/enable',
        method: 'post',
        cType: false,
        data
    });
};

// 应用详情
export const applicationInfoGet = async (appId) => {
    return await systemRequest({
        // url: `http://127.0.0.1:4523/mock/891645/platform/api/rest/resource/app/${appId}`,
        url: `/api/rest/resource/app/${appId}`,
        method: 'get',
        cType: false
    });
};

// 导入 应用
export const importAppJSON = async (file) => {
    var formData = new FormData();
    formData.append('file', file);
    return await systemRequest({
        url: '/api/rest/impExp/importAppJSON',
        method: 'POST',
        cType: false,
        data: formData
    });
};

// 应用中的图标 选择 列表
export const iconSelectList = async () => {
    return await systemRequest({
        url: '/api/rest/app/getAppIconFileList',
        method: 'POST',
        cType: false
    });
};

// 应用图标 列表搜索
export const searchIcon = async (value) => {
    return await systemRequest({
        url: `/api/isvmain/app/searchAppIcon?name=${value}`,
        method: 'POST',
        cType: false
    });
};

// 根据系统id获取应用列表
export const getApplicationList = async (systemId) => {
    return await systemRequest({
        url: `/api/rest/resource/app/listBySystemId?systemId=${systemId}`,
        method: 'get',
        cType: false
    });
};

// 应用排序
export const appSaveOrder = async (appIds) => {
    return await systemRequest({
        url: `/api/rest/resource/app/saveOrder?appIds=${appIds}`,
        method: 'post',
        cType: false
    });
};
