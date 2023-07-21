import request from "../request.js";

export const getUserInfo = async () => {
    return await request({
        url: '/userInfo',
        method: 'GET',
        cType: false,
    });
};