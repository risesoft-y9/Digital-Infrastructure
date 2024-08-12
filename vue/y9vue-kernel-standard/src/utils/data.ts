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
