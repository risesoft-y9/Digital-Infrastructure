import Request from '@/api/lib/request';
import qs from 'qs';

const platformRequest = Request();

export const checkSystemVendorLoginName = async (managerId, loginName) => {
    return await platformRequest({
        url: '/api/rest/systemVendor/checkLoginName',
        method: 'GET',
        cType: false,
        params: { managerId, loginName }
    });
};

export const getSystemVendorById = async (managerId) => {
    return await platformRequest({
        url: '/api/rest/systemVendor/getById',
        method: 'GET',
        cType: false,
        params: { managerId }
    });
};

export const getSystemVendors = async (name = '') => {
    return await platformRequest({
        url: '/api/rest/systemVendor/list',
        method: 'GET',
        cType: false,
        params: { name }
    });
};

export const removeSystemVendor = async (ids) => {
    const data = qs.stringify({ ids });
    return await platformRequest({
        url: '/api/rest/systemVendor/remove',
        method: 'POST',
        cType: false,
        data
    });
};

export const getSystemsByManagerId = async (managerId) => {
    return await platformRequest({
        url: '/api/rest/systemVendor/listSystems',
        method: 'GET',
        cType: false,
        params: { managerId }
    });
};

export const getUnrelatedSystemsByManagerId = async (managerId) => {
    return await platformRequest({
        url: '/api/rest/systemVendor/listUnrelatedSystems',
        method: 'GET',
        cType: false,
        params: { managerId }
    });
};

export const saveVendorSystems = async (managerId, systemIds) => {
    const data = qs.stringify({ managerId, systemIds }, { arrayFormat: 'repeat' });
    return await platformRequest({
        url: '/api/rest/systemVendor/saveSystems',
        method: 'POST',
        cType: false,
        data
    });
};

export const removeVendorSystem = async (managerId, systemId) => {
    const data = qs.stringify({ managerId, systemId });
    return await platformRequest({
        url: '/api/rest/systemVendor/removeSystem',
        method: 'POST',
        cType: false,
        data
    });
};

export const saveSystemVendor = async (vendor) => {
    const data = qs.stringify(vendor);
    return await platformRequest({
        url: '/api/rest/systemVendor/saveOrUpdate',
        method: 'POST',
        cType: false,
        data
    });
};
