<template>
    <y9Form ref="y9FormRef" :config="y9FormConfig"></y9Form>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { computed, h, inject, onMounted, ref, watch } from 'vue';
    import { $keyNameAssign } from '@/utils/object';
    import { $dictionary, $dictionaryFunc, $dictionaryNameFunc } from '@/utils/data';
    import { getOrgTypeList } from '@/api/dictionary/index';
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

    onMounted(async () => {
        await $dictionaryFunc('organizationType', getOrgTypeList); //请求机构类型
        for (let i = 0; i < y9FormConfig.value.itemList.length; i++) {
            const item = y9FormConfig.value.itemList[i];
            if (item.prop === 'organizationType') {
                item.props.options = $dictionary().organizationType?.map((item) => {
                    return {
                        label: computed(() => t(item.name)),
                        value: item.id
                    };
                });
                break;
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
            description: '',
            enName: '',
            name: '',
            organizationCode: '',
            organizationType: '',
            tabIndex: ''
        },
        rules: {}, //表单验证规则
        itemList: [
            //表单显示列表
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'name',
                label: computed(() => t('机构名称')),
                props: {
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
                type1: 'text', //自定义字段-编辑时显示的类型
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
                type1: 'select', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'organizationType',
                label: computed(() => t('机构类型')),
                props: {
                    options: [],
                    render: () => {
                        //text类型渲染的内容
                        let name = $dictionaryNameFunc('organizationType', props.currInfo?.organizationType);
                        return h('span', name);
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'organizationCode',
                label: computed(() => t('机构代码')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', props.currInfo?.organizationCode);
                    }
                }
            },
            {
                type: 'text',
                type1: 'textarea', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'description',
                label: computed(() => t('机构描述')),
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
    }

    //改变y9Form显示类型
    const changeY9FormType = (isEdit) => {
        if (isEdit) {
            //编辑状态设置表单校验规则
            y9FormConfig.value.rules = {
                name: [{ required: true, message: computed(() => t('请输入机构名称')), trigger: 'blur' }]
            };
        } else {
            y9FormConfig.value.rules = {};
        }
        //编辑模式显示type1类型 非编辑模式显示type2类型
        y9FormConfig.value.itemList.forEach((item) => {
            item.type = isEdit ? item.type1 : item.type2;
        });
    };

    //监听是否为编辑状态
    watch(
        () => props.isEditState,
        (isEdit) => {
            if (isEdit) {
                //编辑状态
                $keyNameAssign(y9FormConfig.value.model, props.currInfo); //编辑状态给表单赋值
            }
            changeY9FormType(isEdit);
        },
        {
            immediate: true
        }
    );

    //表单实例
    const y9FormRef = ref();
    defineExpose({
        y9FormRef
    });
</script>

<style lang="scss" scoped>
    :deep(.el-select) {
        width: 100%;
    }
</style>
