<template>
    <y9Form ref="y9FormRef" :config="y9FormConfig"></y9Form>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { computed, h, inject, onMounted, ref, watch } from 'vue';
    import { $keyNameAssign } from '@/utils/object';
    import moment from 'moment';
    import { useSettingStore } from '@/store/modules/settingStore';

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
            labelWidth: '150px',
            contentWidth: '200px'
        },
        model: {
            //表单属性
            id: '',
            parentId: props.currInfo.id,
            aliasName: '',
            bureau: false,
            deptAddress: '',
            deptFax: '',
            deptGivenName: '',
            deptOffice: '',
            deptPhone: '',
            description: '',
            divisionCode: '',
            enName: '',
            establishDate: '',
            name: '',
            zipCode: '',
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
                label: computed(() => t('部门名称')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.name);
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
                        return h('span', props.currInfo?.customId);
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
                type1: 'text', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'tenantId',
                label: computed(() => t('租户唯一标识')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.tenantId);
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'aliasName',
                label: computed(() => t('部门简称')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.aliasName);
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'deptGivenName',
                label: computed(() => t('特定名称')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.deptGivenName);
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'enName',
                label: computed(() => t('英文名称')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.enName);
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'divisionCode',
                label: computed(() => t('区域代码')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.divisionCode);
                    }
                }
            },
            {
                type: 'text',
                type1: 'date', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'establishDate',
                label: computed(() => t('成立时间')),
                props: {
                    dateType: 'date',
                    valueFormat: 'YYYY-MM-DD',
                    format: 'YYYY-MM-DD',
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.establishDate);
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'deptOffice',
                label: computed(() => t('办公室')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.deptOffice);
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'deptFax',
                label: computed(() => t('传真号码')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.deptFax);
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'deptPhone',
                label: computed(() => t('电话号码')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.deptPhone);
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'zipCode',
                label: computed(() => t('邮政编码')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.zipCode);
                    }
                }
            },
            {
                type: 'text',
                type1: 'select', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'bureau',
                label: computed(() => t('是否委办局')),
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
                    ],
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.bureau ? t('是') : t('否'));
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'deptAddress',
                label: computed(() => t('部门地址')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.deptAddress);
                    }
                }
            },

            {
                type: 'text',
                type1: 'textarea', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'description',
                label: computed(() => t('部门描述')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.description);
                    }
                }
            }
        ]
    });

    //如果是添加模式
    if (props.isAdd) {
        //过滤掉某些字段不显示
        y9FormConfig.value.itemList = y9FormConfig.value.itemList.filter(
            (item) => item.prop !== 'id' && item.prop !== 'customId' && item.prop !== 'tenantId'
        );
        changeY9FormType(true); //显示编辑表单
    }

    //改变y9Form显示类型
    function changeY9FormType(isEdit) {
        if (isEdit) {
            //编辑状态设置表单校验规则
            y9FormConfig.value.rules = {
                name: [{ required: true, message: computed(() => t('请输入部门名称')), trigger: 'blur' }]
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
                if (props.currInfo.establishDate != null) {
                    props.currInfo.establishDate = moment(props.currInfo.establishDate).format('YYYY-MM-DD');
                }

                $keyNameAssign(y9FormConfig.value.model, props.currInfo); //编辑状态给表单赋值
            }
            changeY9FormType(isEdit);
        }
    );

    onMounted(() => {
        if (props.currInfo.establishDate != null) {
            props.currInfo.establishDate = moment(props.currInfo.establishDate).format('YYYY-MM-DD');
        }
    });

    //表单实例
    const y9FormRef = ref();

    defineExpose({
        y9FormRef
    });
</script>

<style lang="scss" scoped>
    :deep(.el-date-editor) {
        .el-input__wrapper {
            width: calc(100% - 24px);
        }
    }
</style>
