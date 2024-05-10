<!--
 * @Author: hongzhew
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2023-12-26 11:25:49
 * @Description: 应用资源详情
-->
<template>
    <y9Form ref="y9FormRef" :config="y9FormConfig" v-loading="loading"></y9Form>
</template>

<script lang="ts" setup>
    import { computed, h, onMounted, ref, watch } from 'vue';
    import { useI18n } from 'vue-i18n';
    import { getMenuInfo, getOperationInfo, resourceInfo } from '@/api/resource/index';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { $validCheck } from '@/utils/validate';

    const settingStore = useSettingStore();
    const { t } = useI18n();
    const props = defineProps({
        id: String, // id
        type: Number, // 应用0 菜单1 按钮2的区分
        editFlag: Boolean, // 编辑 与 查看 的对应显示变量
        saveClickFlag: Boolean // 是否点击保存 的变量
    });
    const emits = defineEmits(['getInfoData']);

    let loading = ref(false);

    // 基本信息
    let basicInfo: any = ref({});
    let y9FormRef = ref();

    const validateUrl = (rule: any, value: any, callback: any) => {
        let result = $validCheck('url', value, true);
        if (!result.valid) {
            callback(new Error(result.msg));
        } else {
            callback();
        }
    };

    const formList = [
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
            type1: 'input', //自定义字段-编辑时显示的类型
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
            type1: 'input', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            prop: 'aliasName',
            label: computed(() => t('应用别名')),
            props: {
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.aliasName);
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
            type1: 'radio', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            prop: 'hidden',
            label: computed(() => t('是否隐藏')),
            props: {
                options: [
                    { label: computed(() => t('是')), value: true },
                    { label: computed(() => t('否')), value: false }
                ],
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.hidden ? t('是') : t('否'));
                }
            }
        },
        {
            type: 'text',
            type1: 'radio', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            prop: 'inherit',
            label: computed(() => t('是否继承')),
            props: {
                options: [
                    { label: computed(() => t('是')), value: true },
                    { label: computed(() => t('否')), value: false }
                ],
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.inherit ? t('是') : t('否'));
                }
            }
        },
        {
            type: 'text',
            type1: 'radio', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            prop: 'displayType',
            label: computed(() => t('展示方式')),
            props: {
                options: [
                    { label: computed(() => t('图标文本')), value: 0 },
                    { label: computed(() => t('图标')), value: 1 },
                    { label: computed(() => t('文本')), value: 2 }
                ],
                render: () => {
                    //text类型渲染的内容
                    return h(
                        'span',
                        basicInfo.value?.displayType === 0
                            ? t('图标文本')
                            : basicInfo.value?.displayType === 1
                            ? t('图标')
                            : t('文本')
                    );
                }
            }
        },
        {
            type: 'text',
            type1: 'input', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            label: computed(() => t('排列序号')),
            prop: 'tabIndex',
            props: {
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.tabIndex);
                }
            }
        },
        {
            type: 'text',
            type1: 'radio', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            prop: 'showNumber',
            label: computed(() => t('是否显示数字')),
            props: {
                options: [
                    { label: computed(() => t('是')), value: true },
                    { label: computed(() => t('否')), value: false }
                ],
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.showNumber ? t('是') : t('否'));
                }
            }
        },
        {
            type: 'text',
            type1: 'input', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            label: computed(() => t('获取数字的URL')),
            span: 2,
            prop: 'numberUrl',
            props: {
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.numberUrl);
                }
            }
        },
        {
            type: 'text',
            type1: 'input', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            label: computed(() => t('打开位置')),
            prop: 'target',
            props: {
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.target);
                }
            }
        },
        {
            type: 'text',
            type1: 'input', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            label: computed(() => t('菜单部件')),
            prop: 'component',
            props: {
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.component);
                }
            }
        },
        {
            type: 'text',
            type1: 'input', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            label: computed(() => t('链接地址')),
            prop: 'url',
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
            type1: 'input', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            label: computed(() => t('副链接地址')),
            span: 2,
            prop: 'url2',
            props: {
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.url2);
                }
            }
        },
        {
            type: 'text',
            type1: 'input', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            label: computed(() => t('事件')),
            span: 2,
            prop: 'eventName',
            props: {
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.eventName);
                }
            }
        },
        {
            type: 'text',
            type1: 'input', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            label: computed(() => t('图标地址')),
            span: 2,
            prop: 'iconUrl',
            props: {
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.iconUrl);
                }
            }
        },
        {
            type: 'text',
            type1: 'textarea', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            label: computed(() => t('元信息')),
            span: 2,
            prop: 'meta',
            props: {
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.meta);
                }
            }
        },
        {
            type: 'text',
            type1: 'input', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            label: computed(() => t('角色管理URL')),
            span: 2,
            prop: 'roleAdminUrl',
            props: {
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.roleAdminUrl);
                }
            }
        },
        {
            type: 'text',
            type1: 'textarea', //自定义字段-编辑时显示的类型
            type2: 'text', //自定义字段-非编辑状态显示文本类型
            label: computed(() => t('资源管理URL')),
            span: 2,
            prop: 'resourceAdminUrl',
            props: {
                render: () => {
                    //text类型渲染的内容
                    return h('span', basicInfo.value?.resourceAdminUrl);
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
    ];

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
        itemList: formList
    });

    // 请求详情 函数
    async function getInfo() {
        loading.value = true;
        let responseInfo;
        if (props.type === 'APP') {
            // 应用
            y9FormConfig.value.itemList = formList.filter(
                (item) =>
                    item.prop !== 'meta' &&
                    item.prop !== 'component' &&
                    item.prop !== 'target' &&
                    item.prop !== 'inherit' &&
                    item.prop !== 'displayType' &&
                    item.prop !== 'eventName'
            );
            y9FormConfig.value.itemList.forEach((item) => {
                if (item.prop === 'name') {
                    item.label = computed(() => t('应用名称'));
                }
            });
            responseInfo = await resourceInfo(props.id);
        } else if (props.type === 'MENU') {
            // 菜单
            y9FormConfig.value.itemList = formList.filter(
                (item) =>
                    item.prop !== 'displayType' &&
                    item.prop !== 'eventName' &&
                    item.prop !== 'aliasName' &&
                    item.prop !== 'hidden' &&
                    item.prop !== 'showNumber' &&
                    item.prop !== 'numberUrl' &&
                    item.prop !== 'url2' &&
                    item.prop !== 'roleAdminUrl' &&
                    item.prop !== 'resourceAdminUrl'
            );
            y9FormConfig.value.itemList.forEach((item) => {
                if (item.prop === 'name') {
                    item.label = computed(() => t('菜单名称'));
                }
            });
            responseInfo = await getMenuInfo(props.id);
        } else {
            // 按钮
            y9FormConfig.value.itemList = formList.filter(
                (item) =>
                    item.prop !== 'meta' &&
                    item.prop !== 'aliasName' &&
                    item.prop !== 'hidden' &&
                    item.prop !== 'showNumber' &&
                    item.prop !== 'numberUrl' &&
                    item.prop !== 'url2' &&
                    item.prop !== 'roleAdminUrl' &&
                    item.prop !== 'resourceAdminUrl' &&
                    item.prop !== 'component' &&
                    item.prop !== 'target'
            );
            y9FormConfig.value.itemList.forEach((item) => {
                if (item.prop === 'name') {
                    item.label = computed(() => t('按钮名称'));
                }
            });
            responseInfo = await getOperationInfo(props.id);
        }
        basicInfo.value = responseInfo.data;
        loading.value = false;
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
            if (props.type === 'APP') {
                y9FormConfig.value.rules = {
                    name: [{ required: true, message: computed(() => t('请输入名称')), trigger: 'blur' }],
                    url: [
                        { required: true, message: computed(() => t('请输入链接地址')), trigger: 'blur' },
                        { validator: validateUrl, trigger: 'blur' }
                    ]
                };
            } else {
                y9FormConfig.value.rules = {
                    name: [{ required: true, message: computed(() => t('请输入名称')), trigger: 'blur' }]
                };
            }
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
