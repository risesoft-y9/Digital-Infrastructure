/**
 * 浅比较两个object, json的key是否一致
 * @param obj1
 * @param obj2
 * @returns
 */
export function equalObjectKey(obj1: Object, obj2: Object): boolean {
    const obj1Keys: string[] = Object.keys(obj1);
    const obj2Keys: string[] = Object.keys(obj2);
    const obj1KeysLen: number = obj1Keys.length;
    if (obj1KeysLen !== obj2Keys.length) {
        return false;
    }
    let is = true;
    for (let index = 0; index < obj1KeysLen; index++) {
        const element: string = obj1Keys[index];
        if (!Object.prototype.hasOwnProperty.call(obj2, element)) {
            is = false;
            break;
        }
    }
    return is;
}

/**
 * 浅比较两个对象是否相等，这两个对象的值只能是数字或字符串
 * @param obj1
 * @param obj2
 * @returns
 */
export function equalObject(obj1: Object, obj2: Object): boolean {
    const obj1Keys: string[] = Object.keys(obj1);
    const obj2Keys: string[] = Object.keys(obj2);
    const obj1KeysLen: number = obj1Keys.length;
    const obj2KeysLen: number = obj2Keys.length;
    if (obj1KeysLen !== obj2KeysLen) {
        return false;
    }

    if (obj1KeysLen === 0 && obj2KeysLen === 0) {
        return true;
    }

    return !obj1Keys.some((key) => obj1[key] != obj2[key]);
}

/**
 * 精准判断数据类型
 * @param obj
 * @returns
 */
export function $dataType(obj: any): String {
    const toString = Object.prototype.toString;

    const map = {
        '[object Boolean]': 'boolean',
        '[object Number]': 'number',
        '[object String]': 'string',
        '[object Function]': 'function',
        '[object Array]': 'array',
        '[object Date]': 'date',
        '[object RegExp]': 'regExp',
        '[object Undefined]': 'undefined',
        '[object Null]': 'null',
        '[object Object]': 'object',
        '[object Promise]': 'promise',
        '[object AsyncFunction]': 'asyncFunction'
    };

    return map[toString.call(obj)];
}

/**
 * 深度合并对象
 * @param param
 * @returns
 */
export function $deepAssignObject(...param: any): any {
    let result = Object.assign({}, ...param);

    for (let objItem of param) {
        for (let [key, val] of Object.entries(objItem)) {
            if (
                Object.prototype.toString.call(result[key]) == '[object Object]' &&
                Object.prototype.toString.call(val) == '[object Object]'
            ) {
                //对象深度拷贝合并

                result[key] = $deepAssignObject(result[key], val);
            } else if (
                Object.prototype.toString.call(result[key]) == '[object Array]' &&
                Object.prototype.toString.call(val) == '[object Array]'
            ) {
                //数组深度拷贝

                result[key] = $deeploneObject(val);
            }
        }
    }

    return result;
}

/**
 * 深度拷贝数组或对象
 * @param data
 * @returns
 */
export function $deeploneObject<T>(data: T): T {
    let newData = Array.isArray(data) ? [] : {};

    if (data && typeof data == 'object') {
        for (let key in data) {
            if (data.hasOwnProperty(key)) {
                if (data[key] && typeof data[key] == 'object') {
                    newData[key] = $deeploneObject(data[key]);
                } else {
                    newData[key] = data[key];
                }
            }
        }
    }

    return newData;
}

/**
 * 一个对象获取另一个对象键名相同的值
 * @param obj1
 * @param obj2
 * @param {replace:{},filters:{}}
 */
export function $keyNameAssign(obj1: Object, obj2: Object, { replace, filters }: Object = {}): Object {
    /*
     * obj1 被赋值的对象
     * obj2 赋值对象
     * replace 替换赋值时的键名 例如：{aaa: 'ccc'} 结果：obj1['aaa'] = obj2['ccc']
     * filters [Array] 需要过滤的键名
     */

    Object.keys(obj1).forEach((_key) => {
        if (obj2[_key] !== undefined) {
            if (filters && filters.length > 0) {
                filters.forEach((filterKey) => {
                    if (_key !== filterKey) {
                        obj1[_key] = obj2[_key];
                    }
                });
            } else {
                obj1[_key] = obj2[_key];
            }
        }
    });

    if (replace) {
        Object.keys(replace).forEach((replaceKey) => {
            obj1[replaceKey] =
                obj2[replace[replaceKey]] !== undefined ? obj2[replace[replaceKey]] : replace[replaceKey];
        });
    }
}

/**
 * 格式化数字
 * @param param
 * @returns
 */
export function $formatNumber(n) {
    const str = n.toString();

    return str[1] ? str : `0${str}`;
}

/**
 * 格式化日期
 * @param param
 * @returns
 */
export function $formatTime(param) {
    let date_obj = new Date();

    if (param) {
        // 处理传过来的参数

        if (param instanceof Date) {
            // 是日期对象

            date_obj = param;
        } else {
            let timestamp = 0;

            if (isNaN(Number(param))) {
                // 是日期字符

                timestamp = new Date(param).getTime(); // 转时间戳
            } else {
                // 是数字时间戳

                timestamp = Number(param);
            }

            if (
                timestamp.toString().length === 10 ||
                timestamp.toString().length === 9 ||
                timestamp.toString().length === 8
            ) {
                // 精确到'秒'的时间戳

                date_obj = new Date(parseInt(timestamp * 1000));
            } else {
                // 精确到'毫秒'的时间戳

                date_obj = new Date(timestamp);
            }
        }
    }

    const year = date_obj.getFullYear();
    const month = date_obj.getMonth() + 1;
    const day = date_obj.getDate();

    const hour = date_obj.getHours();
    const minute = date_obj.getMinutes();
    const second = date_obj.getSeconds();

    const t1 = [year, month, day].map($formatNumber).join('-');
    const t2 = [hour, minute].map($formatNumber).join(':');
    const t3 = [hour, minute, second].map($formatNumber).join(':');

    let dateText = (year, month, day) => {
        return year + '年 ' + month + '月 ' + day + '日';
    };

    return {
        year, // 年
        shortDate: t1, // 短日期
        longDateTime: `${t1} ${t2}`, // 长日期到分
        longDateTimeSec: `${t1} ${t3}`, // 长日期到秒
        textShortDate: dateText.apply(null, [year, month, day].map($formatNumber)), // 文本短日期
        timestampTens: Date.parse(date_obj) / 1000, // 时间戳精确到秒
        timestampThirteen: Number(date_obj) // 时间戳精确到毫秒
    };
}

/**
 * // 验证两个对象是否相等
 * @param obj1
 * @param obj2
 * @returns
 */

export function $objEqual(obj1, obj2) {
    let isObj = (object) => {
        // 判断是否为对象类型
        return (
            object &&
            typeof object === 'object' &&
            Object.prototype.toString.call(object).toLowerCase() === '[object object]'
        );
    };

    let isArray = (object) => {
        // 判断是否为数组
        return object && typeof object === 'object' && object.constructor === Array;
    };

    let getLength = (object) => {
        // 获取长度
        let count = 0;
        for (let i in object) count++;
        return count;
    };

    let compareObj = (obj1, obj2, flag) => {
        // 比较对象是否相同

        for (let key in obj1) {
            if (!flag) break;

            if (!obj2.hasOwnProperty(key)) {
                //判断obj2是否存在obj1的属性
                flag = false;
                break;
            }

            if (isObj(obj1[key])) {
                // 是对象时

                if (!isObj(obj2[key])) {
                    //A当前value为对象，B如果不是，返回false
                    flag = false;
                    break;
                }

                flag = compareObj(obj1[key], obj2[key], true);
            } else if (isArray(obj1[key])) {
                // 是数组时

                if (!isArray(obj2[key])) {
                    //A当前value为数组，B如果不是，返回false
                    flag = false;
                    break;
                }

                let OA = obj1[key],
                    OB = obj2[key];

                if (OA.length !== OB.length) {
                    flag = false;
                    break;
                }

                for (let key in OA) {
                    if (!flag) {
                        break;
                    }
                    flag = compareObj(OA[key], OB[key], flag);
                }
            } else {
                if (obj2[key] !== obj1[key]) {
                    flag = false;
                    break;
                }
            }
        }

        return flag;
    };

    if (!isObj(obj1) || !isObj(obj2)) return false;

    if (getLength(obj1) !== getLength(obj2)) return false; //判断长度

    return compareObj(obj1, obj2, true); //默认flag为true
}

/**
 * 判断对象所有值都不为空
 * @param _obj
 * @returns
 */

export function $objValueNotEmpty(_obj) {
    let isObj = (object) => {
        // 判断是否为对象类型
        return (
            object &&
            typeof object === 'object' &&
            Object.prototype.toString.call(object).toLowerCase() === '[object object]'
        );
    };

    let getLength = (object) => {
        // 获取长度
        let count = 0;
        for (let i in object) count++;
        return count;
    };

    let isObjValue = (_obj, flag) => {
        for (let _key in _obj) {
            if (!flag) break;

            if (isObj(_obj[_key])) {
                // 是对象时

                flag = isObjValue(_obj[_key], true);
            } else {
                if (!_obj[_key]) {
                    flag = false;
                    break;
                }
            }
        }

        return flag;
    };

    if (!isObj(_obj)) return false;
    if (getLength(_obj) === 0) return false; //判断长度
    return isObjValue(_obj, true); //默认flag为true
}

/**
 * 表格操作渲染
 * @param handle_arr
 */

export function $tableHandleRender(handle_arr) {
    /* handle_arr数组对象中的属性选项：
     * title [String] 操作标题
     * remixicon:[String] 图标
     * disabled [Boolean] 是否禁用
     * is_render [Boolean] 是否渲染
     * onClick [Function] 点击的回调事件
     *
     */

    let new_h = [];

    let new_handle_arr = handle_arr.filter((item) => item.is_render !== false);

    let text = (item) => {
        return h(
            'a',
            {
                style: {
                    color: item.disabled ? '#c5c8ce' : item.color || 'var(--el-color-primary)',
                    cursor: item.disabled ? 'not-allowed' : 'pointer'
                },
                onClick: (e) => {
                    e.stopPropagation(); // 阻止事件冒泡

                    if (item.disabled) return;

                    item.onClick && item.onClick();
                }
            },
            item.title
        );
    };

    for (let i = 0, len = new_handle_arr.length; i < len; i++) {
        let item = new_handle_arr[i];

        new_h.push(
            i > 0
                ? h('div', {
                      style: {
                          width: '1px',
                          height: '14px',
                          margin: '0 5px'
                      }
                  })
                : '',

            item.remixicon
                ? h(
                      'div',
                      {
                          style: {
                              display: 'flex',
                              justifyContent: 'center',
                              alignItems: 'center',
                              color: 'var(--el-color-primary)'
                          }
                      },
                      [
                          h('i', {
                              class: item.remixicon,
                              style: {
                                  marginRight: '2px'
                              }
                          }),
                          text(item)
                      ]
                  )
                : text(item)
        );
    }

    return h(
        'div',
        {
            props: {},
            style: {
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center'
            }
        },
        new_h
    );
}
