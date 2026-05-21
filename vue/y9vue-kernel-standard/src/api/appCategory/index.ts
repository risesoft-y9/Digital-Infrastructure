import Request from '@/api/lib/request';

const appCategoryRequest = Request();

/**
 * 获取应用排序分页列表
 */
export const pageOrderLists = async (params) => {
    return await appCategoryRequest({
        url: '/api/rest/appCategory/pageOrderListByResourceId',
        method: 'get',
        cType: false,
        params
    });
};

/**获取全部租用的应用列表 */
export const getCategoryList = async () => {
    return await appCategoryRequest({
        url: '/api/rest/appCategory/categoryList',
        method: 'get',
        cType: false,
        params: {}
    });
};

/**获取全部租用的应用列表 */
export const listAllAppByTenantId = async () => {
    return await appCategoryRequest({
        url: '/api/rest/appCategory/listAllAppsByTenantId',
        method: 'get',
        cType: false,
        params: {}
    });
};

/**根据系统名称或应用名称，获取应用列表 */
export const searchAppList = async (appName) => {
    return await appCategoryRequest({
        url: '/api/rest/appCategory/searchAppList',
        method: 'get',
        cType: false,
        params: { appName: appName }
    });
};

/**根据资源id，获取应用排序数据 */
export const listOrderDataByResourceId = async (params) => {
    return await appCategoryRequest({
        url: '/api/rest/appCategory/listByCategoryId',
        method: 'get',
        cType: false,
        params
    });
};

/**保存应用分类信息 */
export const saveAppCategoryOrder = async (appIDs, categoryId) => {
    return await appCategoryRequest({
        url: '/api/rest/appCategory/saveIconItemOrder',
        method: 'POST',
        cType: false,
        params: { appIds: appIDs, categoryId: categoryId }
    });
};

export const deleteIcon = async (ids) => {
    return await appCategoryRequest({
        url: '/api/rest/appCategory/delete',
        method: 'POST',
        cType: false,
        params: { ids: ids }
    });
};

/**更新应用的排序信息 */
export const updateAppCategoryOrder = async (ids) => {
    return await appCategoryRequest({
        url: '/api/rest/appCategory/updateIconItemOrder',
        method: 'POST',
        cType: false,
        params: { ids: ids }
    });
};

export const listAppByPersonId2 = async (personId) => {
    return await appCategoryRequest({
        url: '/api/rest/appCategory/listAppByPersonId2',
        method: 'get',
        cType: false,
        params: { personId: personId }
    });
};

export const pageByAppIcon = async (params) => {
    return await appCategoryRequest({
        url: '/api/rest/appCategory/pageByAppIcon',
        method: 'get',
        cType: false,
        params
    });
};

export const syncDeptIcons = async (deptId) => {
    return await appCategoryRequest({
        url: '/api/tenantmain/tenant/syncDeptIcons',
        method: 'POST',
        cType: false,
        params: { deptId: deptId }
    });
};

export const syncPositionIcons = async (positionId) => {
    return await appCategoryRequest({
        url: '/api/tenantmain/tenant/syncPositionIcons',
        method: 'POST',
        cType: false,
        params: { positionId: positionId }
    });
};
