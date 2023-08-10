/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-05-05 14:56:33
 * @LastEditors: qinman
 * @LastEditTime: 2022-06-09 18:10:44
 * @FilePath: \vue-frontend-9.6.x\y9vue-kernel\src\api\lib\logRequest.js
 */
// import { useI18n } from "vue-i18n"
// const { t } = useI18n();
import settings from '@/settings';
import y9_storage from '@/utils/storage';
import axios from 'axios'; // 考虑CDN
import { ElMessage } from 'element-plus';
import i18n from "@/language/index"
const { t } = i18n.global;

// 创建一个axios实例
const logRequest = axios.create({
    baseURL: import.meta.env.VUE_APP_LOG_URL,
    withCredentials: true,
    timeout: 0
});
// 全局设置 - post请求头
// service.defaults.headers.post['content-type'] = 'application/x-www-form-urlencoded;charset=UTF-8';

// 请求拦截器
logRequest.interceptors.request.use(
    config => {
        config.headers['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8';
        // 自定义
        if (config.cType) {
            config.headers['userLoginName'] = config.data.userLoginName;
        }
        const access_token = y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
        if (access_token) {
            config.headers['Authorization'] = 'Bearer ' + access_token;
        }
        return config;
    },
    error => {
        // 处理请求错误
        return Promise.reject(error);
    }
);

// 响应拦截器
logRequest.interceptors.response.use(
    response => {
        let res;
        if (response.data) {
            res = response.data;
            return res;
        } else {
            res = response;
            const { status, config } = res;
            if (status == 200) {
                return res;
            }
            switch (status) {
                case 400:
                    console.log('API返回-400错误');
                    ElMessageBox({
                        title: t('提示'),
                        showClose: false,
                        closeOnClickModal: false,
                        closeOnPressEscape: false,
                        message: t('当前用户登入信息已失效，请重新登入再操作'),
                        beforeClose: (action, instance, done) => {
                            if (isExternal(settings.serverLoginUrl)) {
                                window.location.href = settings.serverLoginUrl;
                            } else {
                                window.location.reload();
                            }
                        }
                    });
                    break;
                case 401:
                    console.log('API返回-401错误');
                    break;

                default:
                    console.log('API返回-未定义错误');
                    ElMessage({
                        message: res.msg || 'Error',
                        type: 'error',
                        duration: 3 * 1000
                    });
                    break;
            }
            // 返回错误 走 catch 
            return Promise.reject(res);
        }


    },
    error => {
        ElMessage({
            message: error.message,
            type: 'error',
            duration: 3 * 1000
        });
        return Promise.reject(error);
        // return error;
    }
);

export default logRequest;