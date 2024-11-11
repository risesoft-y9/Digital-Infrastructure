<!--
 * @Author: hongzhew
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2024-11-08 11:22:01
 * @Description: 应用角色详情
-->
<!--  -->
<template>
    <y9Form ref="y9FormRef" :config="y9FormConfig"></y9Form>
</template>

<script lang="ts" setup>
    import { computed, h, onMounted, ref, watch } from 'vue';
    import { useI18n } from 'vue-i18n';
    import { getRoleInfo } from '@/api/role/index';
    import { applicationInfoGet } from '@/api/system/index';
    import { useSettingStore } from '@/store/modules/settingStore';

    const settingStore = useSettingStore();
    const { t } = useI18n();

    const props = defineProps({
        id: String, // 系统id
        type: String, // 资源类型
        editFlag: Boolean, // 编辑 与 查看 的对应显示变量
        saveClickFlag: Boolean // 是否点击保存 的变量
    });
    const emits = defineEmits(['getInfoData']);

    // 基本信息
    let basicInfo = ref({});
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
                label: computed(() => t('所属租户ID')),
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
                type1: 'textarea', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'properties',
                label: computed(() => t('扩展属性')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', basicInfo.value?.properties);
                    }
                }
            },
            {
                type: 'text',
                type1: 'text', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                label: computed(() => t('类型')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return basicInfo.value.resourceType != 0
                            ? basicInfo.value.type == 'role'
                                ? h('span', t('角色'))
                                : h('span', t('节点'))
                            : h('span', t('应用'));
                    }
                }
            },
            {
                type: 'text',
                type1: 'textarea', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'description',
                label: computed(() => t('描述')),
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
        // 请求接口 并赋予值
        let result;
        if (props.type === 'APP') {
            result = await applicationInfoGet(props.id);
        } else {
            result = await getRoleInfo(props.id);
        }
        basicInfo.value = result.data;
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

    // 监听 saveClicFlag 当为true 时 将对象传给 index
    watch(
        () => props.saveClickFlag,
        async (new_, old_) => {
            if (new_) {
                let valid = await y9FormRef?.value.elFormRef?.validate((valid) => valid); //获取表单验证结果;
                if (valid) {
                    basicInfo.value = y9FormRef.value?.model;
                    emits('getInfoData', basicInfo.value);
                    return;
                }
            }
        }
    );

    watch(
        () => props.editFlag,
        (new_, old_) => {
            y9FormConfig.value.model = basicInfo.value;
            changeY9FormType(!new_);
        }
    );

    //改变y9Form显示类型
    function changeY9FormType(isEdit) {
        if (isEdit) {
            //编辑状态设置表单校验规则
            y9FormConfig.value.rules = {
                name: [{ required: true, message: computed(() => t('请输入名称')), trigger: 'blur' }]
            };
        } else {
            y9FormConfig.value.rules = {};
        }

        //编辑模式显示type1类型 非编辑模式显示type2类型
        y9FormConfig.value.itemList.forEach((item) => {
            item.type = isEdit ? item.type1 : item.type2;
        });
    }
</script>
<style lang="scss" scoped></style>
