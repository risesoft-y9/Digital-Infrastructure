/*
 * @Author: mengjuhua
 * @Date: 2023-08-02 16:04:34
 * @LastEditors: mengjuhua
 * @Description:
 */
import Request from '@/api/lib/request';

const request = Request();

export const getPersonResourcePermissionList = async (personId) => {
    return await request({
        url: '/api/rest/personResources/getByPersonId',
        method: 'get',
        cType: false,
        params: { personId: personId }
    });
};

export const getPositionResourcePermissionList = async (positionId) => {
    return await request({
        url: '/api/rest/positionResources/getByPositionId',
        method: 'get',
        cType: false,
        params: { positionId: positionId }
    });
};

export const getPersonRolePermissionList = async (personId) => {
    return await request({
        url: '/api/rest/personRoles/getByPersonId',
        method: 'get',
        cType: false,
        params: { personId: personId }
    });
};

export const getPositionRolePermissionList = async (positionId) => {
    return await request({
        url: '/api/rest/positionRoles/getByPositionId',
        method: 'get',
        cType: false,
        params: { positionId: positionId }
    });
};

/**
 * 同步某租户下某组织节点或其下所有人员/岗位角色对应表
 */
export const identityRoles = async (tenantId, orgUnitId) => {
    return await request({
        url: `/sync/identityRoles/${tenantId}/${orgUnitId}`,
        method: 'get',
        cType: false,
        params: {}
    });
};

/**
 * 同步租户人员的权限缓存
 */
export const identityResources = async (tenantId, orgUnitId) => {
    return await request({
        url: `/sync/identityResources/${tenantId}/${orgUnitId}`,
        method: 'get',
        cType: false,
        params: {}
    });
};
