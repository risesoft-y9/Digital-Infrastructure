import request from "../request.js";

const baseRequest = request();
export const getUserInfo = async () => {
    return await baseRequest({
        url: '/userInfo',
        method: 'GET',
        cType: false,
    });
};
