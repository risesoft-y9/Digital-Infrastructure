/*
 * @Author: your name
 * @Date: 2021-04-09 18:53:30
 * @LastEditTime: 2022-10-18 14:47:23
 * @LastEditors: mengjuhua
 * @Description: 导入
 */
import Request from '@/api/lib/request';
import qs from 'qs';

const platformRequest = Request();

// 导入系统JSON
export const importSystemJSON = async (file) => {
    var data = new FormData();
    data.append('file', file);
    return await platformRequest({
        url: '/api/rest/impExp/importSystemJSON',
        method: 'POST',
        cType: false,
        data: data
    });
};

// 导入应用JSON
export const importAppJSON = async (file, systemId) => {
    var data = new FormData();
    data.append('file', file);
    data.append('systemId', systemId);
    return await platformRequest({
        url: '/api/rest/impExp/importAppJSON',
        method: 'POST',
        cType: false,
        data: data
    });
};

//导入组织机构信息XML
export const impOrg4xml = async (file) => {
    var data = new FormData();
    data.append('upload', file);
    return await platformRequest({
        url: '/api/rest/impExp/importOrgXml',
        method: 'POST',
        cType: false,
        data: data
    });
};

//上传组织机构XLS
export const impOrgTreeExcel = async (file, orgId) => {
    var data = new FormData();
    data.append('file', file);
    data.append('orgId', orgId);
    return await platformRequest({
        url: '/api/rest/impExp/importOrgTreeXls',
        method: 'POST',
        cType: false,
        data: data
    });
};
