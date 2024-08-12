/**
 * 判断是否是外链
 * @param {string} path
 * @returns {Boolean}
 * @author LiQingSong
 */
export const isExternal = (path: string): boolean => {
    return /^(https?:|mailto:|tel:)/.test(path);
};

/**
 * @description 判断是否为空
 * @param value
 * @returns {boolean}
 */
export function isBlank(value) {
    return (
        value === null || false || value === '' || value.trim() === '' || value.toLocaleLowerCase().trim() === 'null'
    );
}

/**
 * 校验正则
 * @param {string} type
 * @param {string} value
 * @returns {Boolean}
 * @author Fuyu
 */
export function $validCheck(type: string, value: string, isCallbackMsg: boolean): boolean | object {
    let regular = ''; //正则
    let msg = '';
    switch (type) {
        case 'email': //邮箱
            regular = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]w+)*$/;
            msg = '请输入正确的邮箱格式';
            break;
        case 'phone': //11位手机号
            regular = /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/;
            msg = '请输入正确的手机号码格式';
            break;
        case 'idCard':
            regular = /^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/;
            msg = '请输入正确的身份证格式';
            break;
        case 'moreThan0':
            regular = /^[1-9]d*$/;
            msg = '请输入大于0的正整数';
            break;
        case 'url':
            regular = /^(http|ftp|https):\/\/[^\u4e00-\u9fa5]/;
            msg = '必须以http、https、ftp开头,且不能缺少url字段';
            break;
    }

    if (regular) {
        return isCallbackMsg ? { valid: regular.test(value), msg: msg } : value ? regular.test(value) : regular;
    } else {
        return false;
    }
}
