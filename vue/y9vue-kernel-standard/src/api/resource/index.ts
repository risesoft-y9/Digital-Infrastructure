import Request from '@/api/lib/request';
import qs from 'qs';

const resourceRequest = Request();

//资源树
export const resourceTree = async (params) => {
    return await resourceRequest({
        url: '/api/rest/resource/tree',
        method: 'get',
        cType: false,
        params
    });
};

// 应用树
export const resourceTreeList = async () => {
    return await resourceRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/resource/treeRoot',
        url: '/api/rest/resource/treeRoot2',
        method: 'get',
        cType: false,
        params: {}
    });
};

// 搜索资源树
export const treeSearch = async (value) => {
    let params;
    if (value?.appId) {
        params = { name: value.key, appId: value.appId };
    } else {
        params = { name: value.key };
    }
    return await resourceRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/resource/treeSearch',
        url: '/api/rest/resource/treeSearch2',
        method: 'get',
        cType: false,
        params
    });
};

// 资源 二级
export const resourceTreeRoot = async (params) => {
    return await resourceRequest({
        url: `/api/rest/resource/listByParentId2`,
        method: 'get',
        cType: false,
        params
    });
};

//根据应用id查询资源（App资源树）
export const appTreeRoot = async (id) => {
    return await resourceRequest({
        // url: `http://127.0.0.1:4523/mock/891645/platform/api/rest/resource/operation/${id}`,
        url: `/api/rest/resource/appTreeRoot/${id}`,
        method: 'get',
        cType: false
    });
};

// 菜单 详情
export const getMenuInfo = async (id) => {
    return await resourceRequest({
        // url: `http://127.0.0.1:4523/mock/891645/platform/api/rest/resource/menu/${id}`,
        url: `/api/rest/resource/menu/${id}`,
        method: 'get',
        cType: false
    });
};

// 菜单新增
export const menuAdd = async (params) => {
    const data = qs.stringify(params);
    return await resourceRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/resource/menu/save',
        url: '/api/rest/resource/menu/save',
        method: 'post',
        cType: false,
        data
    });
};

// 菜单 删除
export const menuDelete = async (id) => {
    const data = qs.stringify({ id });
    return await resourceRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/resource/menu/delete',
        url: '/api/rest/resource/menu/delete',
        method: 'post',
        cType: false,
        data
    });
};

// 操作按钮 详情
export const getOperationInfo = async (id) => {
    return await resourceRequest({
        // url: `http://127.0.0.1:4523/mock/891645/platform/api/rest/resource/operation/${id}`,
        url: `/api/rest/resource/operation/${id}`,
        method: 'get',
        cType: false
    });
};

// 操作  新增
export const operationAdd = async (params) => {
    const data = qs.stringify(params);
    return await resourceRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/resource/operation/save',
        url: '/api/rest/resource/operation/save',
        method: 'post',
        cType: false,
        data
    });
};

// 操作  删除
export const operationDel = async (id) => {
    const data = qs.stringify({ id });
    return await resourceRequest({
        // url: 'http://127.0.0.1:4523/mock/891645/platform/api/rest/resource/operation/delete',
        url: '/api/rest/resource/operation/delete',
        method: 'post',
        cType: false,
        data
    });
};

//资源排序
export const sort = async (ids) => {
    return await resourceRequest({
        url: '/api/rest/resource/sort',
        method: 'get',
        cType: false,
        params: { ids: ids }
    });
};
