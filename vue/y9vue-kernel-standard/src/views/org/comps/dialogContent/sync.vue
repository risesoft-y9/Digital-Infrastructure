<!--
 * @Descripttion: 数据同步
 * @version:
 * @Author: zhangchongjie
 * @Date: 2022-06-20 09:35:38
 * @LastEditors: mengjuhua
 * @LastEditTime: 2023-12-26 11:24:15
-->
<template>
    <!-- 数据同步 -->
    <y9Form ref="y9FormRef" :config="y9FormConfig"></y9Form>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { computed, inject, onMounted, ref } from 'vue';
    import { $dictionary, $dictionaryFunc } from '@/utils/data';
    import { systemList } from '@/api/system';

    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');

    //表单配置
    const y9FormConfig = ref({
        descriptionsFormConfig: {
            //描述表单配置
            column: 1 //1列模式
        },
        model: {
            //表单属性
            needRecursion: false,
            targetSystemName: 'ALL'
        },
        rules: {
            //表单验证规则
            targetSystemName: { required: true, message: computed(() => t('请选择目标系统标识')), trigger: 'change' }
        },
        itemList: [
            //表单显示列表
            {
                type: 'select',
                prop: 'targetSystemName',
                label: computed(() => t('目标系统标识')),
                props: {
                    filterable: 'true',
                    clearable: false,
                    options: [
                        {
                            label: computed(() => t('ALL')),
                            value: 'ALL'
                        }
                    ]
                }
            },
            {
                type: 'radio',
                prop: 'needRecursion',
                label: computed(() => t('是否递归')),
                props: {
                    options: [
                        {
                            label: computed(() => t('是')),
                            value: true
                        },
                        {
                            label: computed(() => t('否')),
                            value: false
                        }
                    ]
                }
            }
        ]
    });

    //表单实例
    const y9FormRef = ref();

    defineExpose({
        y9FormRef
    });

    onMounted(async () => {
        await $dictionaryFunc('systemList', systemList); //获取系统列表

        for (let i = 0; i < y9FormConfig.value.itemList.length; i++) {
            const item = y9FormConfig.value.itemList[i];
            if (item.prop === 'targetSystemName') {
                item.props.options = $dictionary().systemList?.map((item) => {
                    return {
                        label: computed(() => t(item.cnName)),
                        value: item.name
                    };
                });

                item.props.options.push({
                    label: '所有系统',
                    value: 'ALL'
                });
            }
        }
    });
</script>

<style></style>
