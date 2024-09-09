import Request from '@/api/lib/request';

const settingRequest = Request();

// 租户设置
export const getTenantSetting = async () => {
    return await settingRequest({
        url: '/api/rest/setting/getTenantSetting',
        method: 'get',
        cType: false,
        params: {}
    });
};

// 保存租户设置
export const saveTenantSetting = async (params) => {
    // const data = qs.stringify({ params });
    return await settingRequest({
        url: '/api/rest/setting/saveTenantSetting',
        method: 'post',
        cType: false,
        params
    });
};
