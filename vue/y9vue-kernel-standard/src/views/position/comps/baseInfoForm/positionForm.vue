<!--
 * @Descripttion: 岗位详情
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-06-16 10:09:20
 * @LastEditors: mengjuhua
 * @LastEditTime: 2024-01-12 10:11:33
-->
<template>
    <y9Form ref="y9FormRef" :config="y9FormConfig"></y9Form>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { computed, h, inject, onMounted, ref, watch } from 'vue';

    import { $keyNameAssign } from '@/utils/object';
    import { $dictionary, $dictionaryFunc } from '@/utils/data';
    import { getJobList } from '@/api/dictionary/index';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { $validCheck } from '@/utils/validate';

    const { t } = useI18n();
    const settingStore = useSettingStore();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const props = defineProps({
        isAdd: {
            //是否为添加模式，添加模式有些字段不需要显示
            type: Boolean,
            default: false
        },
        isEditState: {
            //是否为编辑状态
            type: Boolean
        },
        currInfo: {
            //当前信息
            type: Object,
            default: () => {
                return {};
            }
        }
    });

    //表单配置
    const y9FormConfig = ref({
        descriptionsFormConfig: {
            //描述表单配置
            column: settingStore.device === 'mobile' ? 1 : 2,
            labelAlign: 'center',
            labelWidth: '200px'
        },
        model: {
            //表单属性
            id: '',
            parentId: props.currInfo.id,
            capacity: 1, //岗位容量
            name: '自动生成格式：职位名（人名）',
            jobId: '',
            description: '',
            tabIndex: null // 排序
        },
        rules: {}, //表单验证规则
        itemList: [
            //表单显示列表
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'name',
                label: computed(() => t('岗位名称')),
                props: {
                    disabled: true,
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.name);
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
                        return h('span', props.currInfo?.id);
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'capacity',
                label: computed(() => t('岗位容量')),
                props: {
                    type: 'number',
                    min: 1,
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.capacity);
                    }
                }
            },
            {
                type: 'text',
                type1: 'select', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'jobId',
                label: computed(() => t('职位')),
                props: {
                    options: [],
                    render: () => {
                        //text类型渲染的内容
                        return h('span', getZhiWeiName());
                    }
                }
            },
            {
                type: 'text',
                type1: 'textarea', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'description',
                label: computed(() => t('岗位描述')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.description);
                    }
                }
            }
        ]
    });

    //岗位容量校验规则
    const capacityValidator = (rule, value, callback) => {
        let result = $validCheck('moreThan0', value, true);
        if (!result.valid) {
            callback(new Error(result.msg));
        } else {
            callback();
        }
    };

    //改变y9Form显示类型
    function changeY9FormType(isEdit) {
        if (isEdit) {
            //编辑状态设置表单校验规则
            y9FormConfig.value.rules = {
                capacity: [
                    { required: true, message: computed(() => t('请输入岗位容量')), trigger: 'blur' },
                    { validator: capacityValidator, trigger: 'blur' }
                ],
                jobId: [{ required: true, message: computed(() => t('请选择职位')), trigger: 'change' }]
            };
        } else {
            y9FormConfig.value.rules = {};
        }

        //编辑模式显示type1类型 非编辑模式显示type2类型
        y9FormConfig.value.itemList.forEach((item) => {
            item.type = isEdit ? item.type1 : item.type2;
        });
    }

    //如果是添加模式
    if (props.isAdd) {
        //过滤掉某些字段不显示
        y9FormConfig.value.itemList = y9FormConfig.value.itemList.filter((item) => item.prop !== 'id');
        changeY9FormType(true); //显示编辑表单
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

    onMounted(async () => {
        await $dictionaryFunc('jobList', getJobList); //获取职位列表

        for (let i = 0; i < y9FormConfig.value.itemList.length; i++) {
            const item = y9FormConfig.value.itemList[i];
            if (item.prop === 'jobId') {
                item.props.options = $dictionary().jobList?.map((item) => {
                    return {
                        label: item.name,
                        value: item.originalId
                    };
                });
            }
        }
    });

    function getZhiWeiName() {
        let name = '';
        const jobList = $dictionary().jobList;
        if (jobList && jobList.length > 0) {
            jobList.forEach((item) => {
                if (item.originalId === props.currInfo.jobId) {
                    name = item.name;
                }
            });
        }
        return name;
    }

    //表单实例
    const y9FormRef = ref();
    defineExpose({
        y9FormRef
    });
</script>

<style></style>
