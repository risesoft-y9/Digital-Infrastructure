import Request from '@/api/lib/request';

const appCategoryRequest = Request();

/**
 * 分页获取应用分类配置
 */
export const pageAppCategory = async (params) => {
    return await appCategoryRequest({
        url: '/api/rest/appCategory/pageByCategoryId',
        method: 'get',
        cType: false,
        params
    });
};

/**获取分类列表 */
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

/**根据分类id，获取应用分类配置数据 */
export const listByCategoryId = async (params) => {
    return await appCategoryRequest({
        url: '/api/rest/appCategory/listByCategoryId',
        method: 'get',
        cType: false,
        params
    });
};

/**保存应用分类信息 */
export const saveAppCategory = async (appIDs, categoryId) => {
    return await appCategoryRequest({
        url: '/api/rest/appCategory/save',
        method: 'POST',
        cType: false,
        params: { appIds: appIDs, categoryId: categoryId }
    });
};

/**批量移除应用分类配置*/
export const deleteAppCategory = async (ids) => {
    return await appCategoryRequest({
        url: '/api/rest/appCategory/delete',
        method: 'POST',
        cType: false,
        params: { ids: ids }
    });
};

/**更新应用分类的排序信息 */
export const updateAppCategoryOrder = async (ids) => {
    return await appCategoryRequest({
        url: '/api/rest/appCategory/updateOrder',
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

export const pageByApp = async (params) => {
    return await appCategoryRequest({
        url: '/api/rest/appCategory/pageByApp',
        method: 'get',
        cType: false,
        params
    });
};

export const sync4Dept = async (deptId) => {
    return await appCategoryRequest({
        url: '/api/rest/personalApp/sync4Dept',
        method: 'POST',
        cType: false,
        params: { deptId: deptId }
    });
};

export const sync4Position = async (positionId) => {
    return await appCategoryRequest({
        url: '/api/rest/personalApp/sync4Position',
        method: 'POST',
        cType: false,
        params: { positionId: positionId }
    });
};
