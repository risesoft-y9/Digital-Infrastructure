/*
 * @Author: your name
 * @Date: 2021-04-20 16:03:23
 * @LastEditTime: 2021-12-30 18:45:23
 * @LastEditors: Please set LastEditors
 * @Description: In User Settings Edit
 * @FilePath: /y9vue-todo/src/utils/storage.js
 */
const storageType = sessionStorage;
// const storageType = localStorage;

export default {
    type: function (str) {
        // 为空 使用默认设定类型
        if (!str) {
            return storageType;
        }
        // 有值 判断值使用指定类型
        if (str && str === 'st') {
            return sessionStorage;
        }
        if (str && str === 'lt') {
            return localStorage;
        }
    },
    isAvailable: function storageAvailable(type) {
        var storage;
        try {
            storage = window[type];
            var x = '__storage_test__';
            storage.setItem(x, x);
            storage.removeItem(x);
            return true;
        } catch (e) {
            return (
                e instanceof DOMException &&
                // everything except Firefox
                (e.code === 22 ||
                    // Firefox
                    e.code === 1014 ||
                    // test name field too, because code might not be present
                    // everything except Firefox
                    e.name === 'QuotaExceededError' ||
                    // Firefox
                    e.name === 'NS_ERROR_DOM_QUOTA_REACHED') &&
                // acknowledge QuotaExceededError only if there's something already stored
                storage &&
                storage.length !== 0
            );
        }
    },
    setStringItem: function (key, string) {
        if (typeof string !== 'string' && typeof string !== 'number') {
            return false;
        }
        storageType.setItem(key, string);
    },
    getStringItem: function (key) {
        const str = storageType.getItem(key);
        if (!str || str == 'undefined') {
            return false;
        }
        return str;
    },
    setObjectItem: function (key, obj) {
        if (typeof obj !== 'object') {
            return false;
        }
        storageType.setItem(key, JSON.stringify(obj));
        return true;
    },
    getObjectItem: function (key, item = '') {
        const object = storageType.getItem(key);
        if (!object) {
            return false;
        }
        if (object == 'undefined') {
            return false;
        }
        const obj = JSON.parse(object);
        if (!item) {
            return obj; // 直接返回整个对象
        }
        if (Object.keys(obj).indexOf(item) > -1) {
            return obj[item]; // 返回对象中对应的item属性值
        } else {
            return false;
        }
    },
};
