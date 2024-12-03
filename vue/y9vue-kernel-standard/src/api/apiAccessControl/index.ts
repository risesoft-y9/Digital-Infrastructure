import Request from '@/api/lib/request';
import qs from 'qs';

const platformRequest = Request();

//获取职位数据列表
export const list = async (params) => {
    return await platformRequest({
        url: '/api/rest/apiAccessControl/list',
        method: 'GET',
        cType: false,
        params: params
    });
};

//排序
export const saveOrder = async (params) => {
};

//搜索
export const search = async (key) => {
};

//删除
export const remove = async (id) => {
    const params = {
        id: id
    };
    return await platformRequest({
        url: '/api/rest/apiAccessControl/delete',
        method: 'POST',
        cType: false,
        params: params
    });
};

//切换启用状态
export const changeEnabled = async (id) => {
    const params = {
        id: id
    };
    return await platformRequest({
        url: '/api/rest/apiAccessControl/changeEnabled',
        method: 'POST',
        cType: false,
        params: params
    });
};

//添加/修改数据
export const saveUpdate = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/apiAccessControl/saveOrUpdate',
        method: 'post',
        cType: false,
        data
    });
};

//添加/修改数据
export const saveAppIdSecret = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: '/api/rest/apiAccessControl/saveAppIdSecret',
        method: 'post',
        cType: false,
        data
    });
};

