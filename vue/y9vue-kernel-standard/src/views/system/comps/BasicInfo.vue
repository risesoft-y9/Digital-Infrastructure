<!--
 * @Author: hongzhew
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2023-08-03 15:19:43
 * @Description: 应用系统详情
-->
<template>
    <y9Form :config="y9FormConfig" ref="y9FormRef"></y9Form>
</template>

<script lang="ts" setup>
    import { watch, computed, h, onMounted, ref } from 'vue';
    import { useI18n } from 'vue-i18n';
    import { systemInfoGet } from '@/api/system/index';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { $dataType, $tableHandleRender } from '@/utils/object';
    const settingStore = useSettingStore();
    const { t } = useI18n();

    // 传过来的 系统 id
    const props = defineProps({
        id: String, // 系统id
        editFlag: Boolean, // 编辑 与 查看 的对应显示变量
        saveClickFlag: Boolean, // 是否点击保存 的变量
    });
    const emits = defineEmits(['getInfoData']);

    // 基本信息
    let systemInfo = ref({} as any);

    // 请求详情 函数
    async function getInfo() {
        const responseInfo = await systemInfoGet(props.id);
        systemInfo.value = responseInfo.data;
    }

    onMounted(() => {
        getInfo();
    });
    // 监听系统id 当发生改变时重新请求数据 并赋值
    watch(
        () => props.id,
        (new_, old_) => {
            if (new_ && new_ !== old_) {
                getInfo();
            }
        }
    );

    watch(
        () => props.editFlag,
        (new_, old_) => {
            y9FormConfig.value.model = systemInfo.value;
            changeY9FormType(!new_);
        }
    );

    let y9FormRef = ref();

    //表单配置
    let y9FormConfig = ref({
        descriptionsFormConfig: {
            //描述表单配置
            column: settingStore.device === 'mobile' ? 1 : 2,
            labelAlign: 'center',
            labelWidth: '150px',
            contentWidth: '200px',
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
                        return h('span', systemInfo.value?.name);
                    },
                },
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
                        return h('span', systemInfo.value?.cnName);
                    },
                },
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
                        { label: computed(() => t('否')), value: false },
                    ],
                    render: () => {
                        //text类型渲染的内容
                        return h('span', systemInfo.value?.enabled ? t('是') : t('否'));
                    },
                },
            },
            {
                type: 'text',
                type1: 'text', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'singleDatasource',
                label: computed(() => t('独立数据源')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', systemInfo.value?.singleDatasource ? t('是') : t('否'));
                    },
                },
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
                        return h('span', systemInfo.value?.contextPath);
                    },
                },
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'tabIndex',
                label: computed(() => t('排列序号')),
                props: {
                    options: [],
                    render: () => {
                        //text类型渲染的内容
                        return h('span', systemInfo.value?.tabIndex);
                    },
                },
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
                        return h('span', systemInfo.value?.description);
                    },
                },
            },
        ],
    });

    //改变y9Form显示类型
    function changeY9FormType(isEdit) {
        if (isEdit) {
            //编辑状态设置表单校验规则
            y9FormConfig.value.rules = {
                name: [{ required: true, message: computed(() => t('请输入系统名称')), trigger: 'blur' }],
                cnName: [{ required: true, message: computed(() => t('请输系统中文名称')), trigger: 'blur' }],
            };
        } else {
            y9FormConfig.value.rules = {};
        }

        //编辑模式显示type1类型 非编辑模式显示type2类型
        y9FormConfig.value.itemList.forEach((item) => {
            item.type = isEdit ? item.type1 : item.type2;
        });
    }

    // 监听 saveClicFlag 当为true 时 将对象传给 index
    watch(
        async () => props.saveClickFlag,
        async (new_, old_) => {
            if (new_) {
                let valid = await y9FormRef?.value.elFormRef?.validate((valid) => valid); //获取表单验证结果;
                if (valid) {
                    systemInfo.value = y9FormRef.value?.model;
                    emits('getInfoData', systemInfo.value);
                    return;
                }
            }
        }
    );
</script>
<style scoped lang="scss"></style>
