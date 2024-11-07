import { defineStore } from 'pinia';
import i18n from '@/language/index';

const { t } = i18n.global;

export const useDictionaryStore = defineStore('dictionaryStore', {
    state: () => {
        return {
            dictionary: {
                //字典表
                booleanNum: [
                    {
                        id: 1,
                        name: t('是')
                    },
                    {
                        id: 0,
                        name: t('否')
                    }
                ],
                boolean: [
                    {
                        id: true,
                        name: t('是')
                    },
                    {
                        id: false,
                        name: t('否')
                    }
                ],
                sex: [
                    //性别
                    {
                        id: 0,
                        name: t('女')
                    },
                    {
                        id: 1,
                        name: t('男')
                    }
                ],
                userType: [
                    //人员类型
                    {
                        id: '1',
                        name: t('单位用户')
                    },
                    {
                        id: '2',
                        name: t('管理员用户')
                    },
                    {
                        id: '3',
                        name: t('个人用户')
                    }
                ],
                politicalStatus: [
                    //政治面貌
                    {
                        id: '党员',
                        name: t('党员')
                    },
                    {
                        id: '团员',
                        name: t('团员')
                    },
                    {
                        id: '群众',
                        name: t('群众')
                    }
                ],
                education: [
                    {
                        id: 'EMBA',
                        name: 'EMBA'
                    },
                    {
                        id: 'MBA',
                        name: 'MBA'
                    },
                    {
                        id: '博士',
                        name: t('博士')
                    },
                    {
                        id: '硕士',
                        name: t('硕士')
                    },
                    {
                        id: '本科',
                        name: t('本科')
                    },
                    {
                        id: '大专',
                        name: t('大专')
                    },
                    {
                        id: '高中',
                        name: t('高中')
                    },
                    {
                        id: '高职',
                        name: t('高职')
                    },
                    {
                        id: '中专',
                        name: t('中专')
                    },
                    {
                        id: '中技',
                        name: t('中技')
                    },
                    {
                        id: '初中',
                        name: t('初中')
                    },
                    {
                        id: '其他',
                        name: t('其他')
                    }
                ],
                maritalStatus: [
                    //婚姻状况
                    {
                        id: '0',
                        name: t('保密')
                    },
                    {
                        id: '1',
                        name: t('已婚')
                    },
                    {
                        id: '2',
                        name: t('未婚')
                    }
                ]
            }
        };
    },

    actions: {
        //根据类型获取字典表
        /**
         * dictionaryName 存入字典仓库的名称
         * interfaceName 接口名称
         * interfaceParams 接口参数
         */
        async getDictionaryList(dictionaryName, interfaceName, interfaceParams) {
            let result = await interfaceName(interfaceParams);

            const data = result.data;

            this.dictionary[dictionaryName] = data.filter((item) => {
                item.originalId = item.id;
                item.id = item.name;
                return item;
            });

            return data;

            // if(isEveryRequest || (!this.dictionary[dictionaryName] && $dataType(interfaceName) === 'asyncFunction')){//不存在就请求接口

            // 	let result = await interfaceName(interfaceParams);

            // 	const data = result.data;

            // 	this.dictionary[dictionaryName] = data.filter(item =>{
            // 		item.originalId = item.id;
            // 		item.id = item.name
            // 		return item
            // 	});

            // 	return data

            // }

            // return this.dictionary[dictionaryName];//存在就使用之前的数据
        }
    }
});
