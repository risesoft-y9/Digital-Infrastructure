import axios from 'axios';

// 创建一个axios实例
const service = axios.create({
    baseURL: import.meta.env.VUE_APP_HOST,
    withCredentials: true,
    timeout: 0,
});

//添加请求拦截器
service.interceptors.request.use(
    (config) => {
        // 在发送请求之前做些什么
        config.headers['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8';
        // 自定义
        if (config.cType) {
            config.headers['userLoginName'] = config.data.userLoginName;
        }
        let sessionObj = JSON.parse(sessionStorage.getItem(import.meta.env.VUE_APP_SITETOKEN));
        if (sessionObj.access_token) {
            // console.log("access_token = ",access_token);
            config.headers['Authorization'] = 'Bearer ' + sessionObj.access_token;
        }

        console.log("config", config)

        return config;
    },
    (error) => {
        // 对请求错误做些什么
        console.log(error);
        return Promise.reject(error);
    }
);

//添加响应拦截器
service.interceptors.response.use(
    (response) => {
        let res;
        if (response.data) {
            res = response.data;
        } else {
            res = response;
        }
        const {code} = res;
        if (code !== 0) {
            console.log("code", code)
            // 获取替换后的字符串
            const reqUrl = response.config.url.split('?')[0].replace(response.config.baseURL, '');
            switch (code) {
                case 40101:
                case 40101:
                case 40102:
                case 40102:
                case 401: // 未登陆
                    alert('当前用户登入信息已失效，请重新登入再操作')

                    break;
                case 40300:
                    window.location.href = import.meta.env.VUE_APP_PUBLIC_PATH + '/401';
                    break;
                case 40400:
                    window.location.href = import.meta.env.VUE_APP_PUBLIC_PATH + '/404';
                    break;
                case 50000:
                    return res;
                default:
                    console.error(res.msg);
                    // ElMessage({
                    //     message: res.msg || 'Errors',
                    //     type: 'error',
                    //     duration: 1500,
                    // });
                    break;
            }

            // 返回错误 走 catch
            return Promise.reject(res);
        } else {
            return res;
        }
    },
    (error) => {
        // 对响应错误做些什么
        console.log(error);
        return Promise.reject(error);
    }
);

export default service;
