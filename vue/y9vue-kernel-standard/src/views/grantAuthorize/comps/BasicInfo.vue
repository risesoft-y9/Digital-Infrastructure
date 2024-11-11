<!--
 * @Author: hongzhew
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2024-11-08 14:40:13
 * @Description: 应用资源授权 -基本信息
-->
<template>
    <y9Form ref="y9SystemFormRef" :config="y9SystemFormConfig" v-if="type == 'SYSTEM'"></y9Form>
    <y9Form ref="y9FormRef" :config="y9FormConfig" v-else></y9Form>
</template>

<script lang="ts" setup>
    import { computed, h, onMounted, ref, watch } from 'vue';
    import { useI18n } from 'vue-i18n';
    import { getMenuInfo, getOperationInfo } from '@/api/resource/index';
    import { applicationInfoGet, systemInfoGet } from '@/api/system/index';
    import { useSettingStore } from '@/store/modules/settingStore';

    const settingStore = useSettingStore();
    const { t } = useI18n();
    const props = defineProps({
        id: String, // id
        type: String // 应用0 菜单1 按钮2的区分
    });

    // 基本信息
    let basicInfo = ref({} as any);

    let y9FormRef = ref();

    //表单配置
    let y9FormConfig = ref({
        descriptionsFormConfig: {
            //描述表单配置
            column: settingStore.device === 'mobile' ? 1 : 2,
            labelAlign: 'center',
            labelWidth: '150px',
            contentWidth: '200px'
        },
        model: {},
        rules: {}, //表单验证规则
        itemList: [
            //表单显示列表
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'name',
                label: computed(() => t('名称')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', basicInfo.value?.name);
                    }
                }
            },
            {
                type: 'text',
                type1: 'text', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'id',
                label: computed(() => t('唯一标识')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', basicInfo.value?.id);
                    }
                }
            },
            {
                type: 'text',
                type1: 'text', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'tenantId',
                label: computed(() => t('租户唯一标识')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', basicInfo.value?.tenantId);
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'customId',
                label: computed(() => t('自定义ID')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', basicInfo.value?.customId);
                    }
                }
            },
            {
                type: 'text',
                type1: 'text', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                label: computed(() => t('系统标识')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', basicInfo.value?.systemId);
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                label: computed(() => t('类型')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h(
                            'span',
                            basicInfo.value.resourceType === 0
                                ? t('应用')
                                : basicInfo.value.resourceType === 1
                                ? t('菜单')
                                : t('按钮')
                        );
                    }
                }
            },
            {
                type: 'text',
                type1: 'textarea', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'url',
                label: computed(() => t('链接地址')),
                span: 2,
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', basicInfo.value?.url);
                    }
                }
            },
            {
                type: 'text',
                type1: 'textarea', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'description',
                label: computed(() => t('描述')),
                span: 2,
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', basicInfo.value?.description);
                    }
                }
            }
        ]
    });

    let y9SystemFormRef = ref();

    //表单配置
    let y9SystemFormConfig = ref({
        descriptionsFormConfig: {
            //描述表单配置
            column: settingStore.device === 'mobile' ? 1 : 2,
            labelAlign: 'center',
            labelWidth: '150px',
            contentWidth: '200px'
        },
        model: {},
        rules: {}, //表单验证规则
        itemList: [
            //表单显示列表
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'name',
                label: computed(() => t('系统名称')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', basicInfo.value?.name);
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'cnName',
                label: computed(() => t('系统中文名称')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', basicInfo.value?.cnName);
                    }
                }
            },
            {
                type: 'text',
                type1: 'text', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'id',
                label: computed(() => t('唯一标识')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', basicInfo.value?.id);
                    }
                }
            },
            {
                type: 'text',
                type1: 'radio', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'enabled',
                label: computed(() => t('是否启用')),
                props: {
                    options: [
                        { label: computed(() => t('是')), value: true },
                        { label: computed(() => t('否')), value: false }
                    ],
                    render: () => {
                        //text类型渲染的内容
                        return h('span', basicInfo.value?.enabled ? t('是') : t('否'));
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'contextPath',
                label: computed(() => t('应用上下文')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', basicInfo.value?.contextPath);
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'tabIndex',
                label: computed(() => t('类型')),
                props: {
                    options: [],
                    render: () => {
                        //text类型渲染的内容
                        return h('span', t('系统'));
                    }
                }
            },
            {
                type: 'text',
                type1: 'textarea', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'description',
                label: computed(() => t('系统概述')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', basicInfo.value?.description);
                    }
                }
            }
        ]
    });

    // 请求详情 函数
    async function getInfo() {
        let responseInfo;
        if (props.type === 'SYSTEM') {
            // 应用
            responseInfo = await systemInfoGet(props.id);
        } else if (props.type === 'APP') {
            // 应用
            responseInfo = await applicationInfoGet(props.id);
        } else if (props.type === 'MENU') {
            // 菜单
            responseInfo = await getMenuInfo(props.id);
        } else {
            // 按钮
            responseInfo = await getOperationInfo(props.id);
        }
        basicInfo.value = responseInfo.data;
    }

    onMounted(() => {
        getInfo();
    });
    // 监听系统id 当发生改变时重新请求数据
    watch(
        () => props.id,
        async (new_, old_) => {
            if (new_ && new_ !== old_) {
                getInfo();
            }
        }
    );
</script>
<style lang="scss" scoped></style>
