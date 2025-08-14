import Request from '@/api/lib/request';

const platformRequest = Request();

/**
 * 分页查询审计日志
 * @param params 条件对象
 * @param page
 * @param size
 * @returns
 */
export const pageAuditLog = async (params, page, size) => {
    const formData = new FormData();
    formData.append('page', page);
    formData.append('size', size);
    // 请求加入新参数
    for (var prop in params) {
        if (Object.prototype.hasOwnProperty.call(params, prop)) {
            formData.append(prop, params[prop]);
        }
    }
    return await platformRequest({
        url: '/api/rest/auditLog/list',
        method: 'POST',
        cType: false,
        data: formData
    });
};