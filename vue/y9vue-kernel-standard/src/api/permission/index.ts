import Request from "@/api/lib/request";
const request = Request();

export const getPersonResourcePermissionList = async (personId) => {
    return await request({
        url: '/api/rest/personResources',
        method: 'get',
        cType: false,
        params: { 'personId': personId}
    });
};

export const getPositionResourcePermissionList = async (positionId) => {
    return await request({
        url: '/api/rest/positionResources',
        method: 'get',
        cType: false,
        params: { 'positionId': positionId}
    });
};

export const getPersonRolePermissionList = async (personId) => {
    return await request({
        url: '/api/rest/personRoles',
        method: 'get',
        cType: false,
        params: { 'personId': personId}
    });
};

export const getPositionRolePermissionList = async (positionId) => {
    return await request({
        url: '/api/rest/positionRoles',
        method: 'get',
        cType: false,
        params: { 'positionId': positionId}
    });
};