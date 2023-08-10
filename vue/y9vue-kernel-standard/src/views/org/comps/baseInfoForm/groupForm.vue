<!--
 * @Descripttion: 用户组
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-06-16 10:16:08
 * @LastEditors: mengjuhua
 * @LastEditTime: 2023-08-03 10:44:53 
-->
<template>
    <y9Form :config="y9FormConfig" ref="y9FormRef"></y9Form>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { watch, computed, h, ref } from 'vue';
    import { $keyNameAssign } from '@/utils/object';
    const { t } = useI18n();
    const props = defineProps({
        isAdd: {
            //是否为添加模式，添加模式有些字段不需要显示
            type: Boolean,
            default: false,
        },
        isEditState: {
            //是否为编辑状态
            type: Boolean,
        },
        currInfo: {
            //当前信息
            type: Object,
            default: () => {
                return {};
            },
        },
    });

    //表单配置
    const y9FormConfig = ref({
        descriptionsFormConfig: {
            //描述表单配置
            column: 1,
            labelAlign: 'center',
            labelWidth: '150px',
            contentWidth: '200px',
        },
        model: {
            //表单属性
            id: '',
            name: '',
            description: '',
            parentId: props.currInfo.id,
        },
        rules: {}, //表单验证规则
        itemList: [
            //表单显示列表
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'name',
                label: computed(() => t('用户组名称')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.name);
                    },
                },
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
                        return h('span', props.currInfo?.id);
                    },
                },
            },
            {
                type: 'text',
                type1: 'textarea', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'description',
                label: computed(() => t('用户组描述')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.description);
                    },
                },
            },
        ],
    });

    //如果是添加模式
    if (props.isAdd) {
        //过滤掉某些字段不显示
        y9FormConfig.value.itemList = y9FormConfig.value.itemList.filter((item) => item.prop !== 'id');
        changeY9FormType(true); //显示编辑表单
    }

    //改变y9Form显示类型
    function changeY9FormType(isEdit) {
        if (isEdit) {
            //编辑状态设置表单校验规则
            y9FormConfig.value.rules = {
                name: [{ required: true, message: computed(() => t('请输入用户组名称')), trigger: 'blur' }],
            };
        } else {
            y9FormConfig.value.rules = {};
        }

        //编辑模式显示type1类型 非编辑模式显示type2类型
        y9FormConfig.value.itemList.forEach((item) => {
            item.type = isEdit ? item.type1 : item.type2;
        });
    }

    //监听是否为编辑状态
    watch(
        () => props.isEditState,
        (isEdit) => {
            if (isEdit) {
                //编辑状态
                $keyNameAssign(y9FormConfig.value.model, props.currInfo); //编辑状态给表单赋值
            }
            changeY9FormType(isEdit);
        }
    );

    //表单实例
    const y9FormRef = ref();

    defineExpose({
        y9FormRef,
    });
</script>

<style></style>
