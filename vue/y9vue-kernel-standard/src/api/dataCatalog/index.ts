import Request from '@/api/lib/request';
import qs from 'qs';

const dataCatalogRequest = Request();

// 数据目录树
export const dataCatalogTree = async (params) => {
    return await dataCatalogRequest({
        url: '/api/rest/dataCatalog/tree',
        method: 'get',
        cType: false,
        params
    });
};

// 数据目录树搜索
export const dataCatalogTreeSearch = async (value) => {
    let params;
    if (value?.appId) {
        params = { name: value.key, appId: value.appId };
    } else {
        params = { name: value.key };
    }
    return await dataCatalogRequest({
        url: '/api/rest/dataCatalog/treeSearch',
        method: 'get',
        cType: false,
        params
    });
};

// 获取数据目录详情
export const getDataCatalog = async (id) => {
    return await dataCatalogRequest({
        url: `/api/rest/dataCatalog/${id}`,
        method: 'get',
        cType: false
    });
};

// 获取数据目录树类型
export const getTreeTypeList = async (id) => {
    return await dataCatalogRequest({
        url: `/api/rest/dataCatalog/treeTypeList`,
        method: 'get',
        cType: false
    });
};

// 保存数据目录
export const saveDataCatalog = async (params) => {
    const data = qs.stringify(params);
    return await dataCatalogRequest({
        url: '/api/rest/dataCatalog/save',
        method: 'post',
        cType: false,
        data
    });
};

// 保存数据目录
export const saveDataCatalogByYears = async (params) => {
    const data = qs.stringify(params);
    return await dataCatalogRequest({
        url: '/api/rest/dataCatalog/saveByYears',
        method: 'post',
        cType: false,
        data
    });
};

// 保存数据目录
export const saveDataCatalogByType = async (params) => {
    const data = qs.stringify(params);
    return await dataCatalogRequest({
        url: '/api/rest/dataCatalog/saveByType',
        method: 'post',
        cType: false,
        data
    });
};

// 删除数据目录
export const deleteDataCatalog = async (id) => {
    return await dataCatalogRequest({
        url: '/api/rest/dataCatalog/delete',
        method: 'POST',
        cType: false,
        params: { id: id }
    });
};
