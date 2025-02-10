/**
 * 全局数据
 */
import { useDictionaryStore } from '@/store/modules/dictionaryStore';

export const $dictionaryFunc = async (dictionaryName, interfaceName, interfaceParams) => {
    //请求字典表的方法
    return await useDictionaryStore().getDictionaryList(dictionaryName, interfaceName, interfaceParams);
};

export const $dictionary = () => {
    //获取字典表
    return useDictionaryStore().$state.dictionary;
};

export const $dictionaryNameFunc = (dictionaryName, code) => {
    //获取code的name值
    return useDictionaryStore().getCodeName(dictionaryName, code);
};
