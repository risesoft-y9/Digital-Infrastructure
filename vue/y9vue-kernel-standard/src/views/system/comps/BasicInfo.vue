<!--
 * @Author: hongzhew
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2023-12-26 11:26:44
 * @Description: 应用系统详情
-->
<template>
    <y9Card :title="`${$t('基本信息')} - ${systemInfo.cnName ? systemInfo.cnName : ''}`">
        <template v-slot>
            <div v-show="currTreeNodeInfo.manageable" class="basic-btns">
                <span class="btn-top">
                    <el-button
                        v-if="editBtnFlag"
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        class="global-btn-main"
                        type="primary"
                        @click="changeY9FormType(true)"
                    >
                        <i class="ri-edit-line"></i>
                        {{ $t('编辑') }}
                    </el-button>
                    <span v-else>
                        <el-button
                            :loading="saveBtnLoading"
                            :size="fontSizeObj.buttonSize"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            class="global-btn-main"
                            type="primary"
                            @click="handlerEditSave()"
                        >
                            <i class="ri-save-line"></i>
                            {{ $t('保存') }}
                        </el-button>
                        <el-button
                            :size="fontSizeObj.buttonSize"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            class="global-btn-second"
                            @click="changeY9FormType(false)"
                        >
                            <i class="ri-close-line"></i>
                            {{ $t('取消') }}
                        </el-button>
                    </span>
                </span>
                <span>
                    <el-button
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        class="global-btn-second"
                        @click="handlerExport"
                    >
                        <i class="ri-file-upload-line" />
                        {{ $t('导出') }}
                    </el-button>
                    <el-button
                        v-loading.fullscreen.lock="loading"
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        class="global-btn-second"
                        @click="handlerDisable"
                    >
                        <i class="ri-user-unfollow-line"></i>
                        {{ systemInfo.enabled ? $t('禁用') : $t('启用') }}
                    </el-button>
                </span>
            </div>
            <y9Form ref="y9FormRef" :config="y9FormConfig"></y9Form>
        </template>
    </y9Card>
</template>

<script lang="ts" setup>
    import { computed, h, inject, onMounted, ref, watch } from 'vue';
    import { useI18n } from 'vue-i18n';
    import { systemAdd, systemDisabled, systemEnabled, systemInfoGet } from '@/api/system/index';
    import { useSettingStore } from '@/store/modules/settingStore';
    import settings from '@/settings';
    import y9_storage from '@/utils/storage';

    const settingStore = useSettingStore();
    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    let loading = ref(false);

    // 传过来的 系统 id
    const props = defineProps({
        currTreeNodeInfo: {
            //当前tree节点信息
            type: Object,
            default: () => {
                return {};
            }
        }
    });

    // 基本信息
    let systemInfo = ref({} as any);

    // 请求详情 函数
    async function getInfo() {
        const responseInfo = await systemInfoGet(props.currTreeNodeInfo.id);
        systemInfo.value = responseInfo.data;
    }

    onMounted(() => {
        getInfo();
    });
    // 监听系统id 当发生改变时重新请求数据 并赋值
    watch(
        () => props.currTreeNodeInfo.id,
        (new_, old_) => {
            if (new_ && new_ !== old_) {
                console.log('当前系统节点:', props.currTreeNodeInfo);
                getInfo();
            }
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
                        return h('span', systemInfo.value?.name);
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
                        return h('span', systemInfo.value?.id);
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
                        return h('span', systemInfo.value?.cnName);
                    }
                }
            },
            {
                type: 'text',
                type1: 'input', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                prop: 'contextPath',
                label: computed(() => t('系统上下文')),
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', systemInfo.value?.contextPath);
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
                        return h('span', systemInfo.value?.enabled ? t('是') : t('否'));
                    }
                }
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
                    }
                }
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
                        return h('span', systemInfo.value?.description);
                    }
                }
            }
        ]
    });

    // 点击保存按钮 的 flag
    let saveBtnClick = ref(false);
    // 控制 基本信息 编辑按钮 与 保存，取消按钮的显示与隐藏
    let editBtnFlag = ref(true);
    // 保存 按钮 loading
    let saveBtnLoading = ref(false);

    // 基本信息 点击保存 后 进行 接口操作
    async function handlerEditSave() {
        saveBtnLoading.value = true;

        let valid = await y9FormRef?.value.elFormRef?.validate((valid) => valid); //获取表单验证结果;
        if (valid) {
            let res = { success: false, msg: '' } as any;
            res = await systemAdd(y9FormRef.value?.model);
            if (res.success) {
                /**
                 * 对树进行操作：手动更新节点信息
                 */
                systemInfo.value = res.data;
            }
            ElNotification({
                title: res.success ? t('成功') : t('失败'),
                message: res.success ? t('更新成功') : res.msg,
                type: res.success ? 'success' : 'error',
                duration: 2000,
                offset: 80
            });
            // loading为false 编辑 按钮出现 保存按钮未点击状态
            saveBtnLoading.value = false;
            changeY9FormType(false);
        }
    }

    //改变y9Form显示类型
    function changeY9FormType(isEdit) {
        if (isEdit) {
            editBtnFlag.value = false;
            saveBtnClick.value = true;
            y9FormConfig.value.model = systemInfo.value;
            //编辑状态设置表单校验规则
            y9FormConfig.value.rules = {
                name: [{ required: true, message: computed(() => t('请输入系统名称')), trigger: 'blur' }],
                cnName: [{ required: true, message: computed(() => t('请输系统中文名称')), trigger: 'blur' }]
            };
        } else {
            editBtnFlag.value = true;
            saveBtnClick.value = false;
            y9FormConfig.value.rules = {};
        }

        //编辑模式显示type1类型 非编辑模式显示type2类型
        y9FormConfig.value.itemList.forEach((item) => {
            item.type = isEdit ? item.type1 : item.type2;
        });
    }

    // 导出
    function handlerExport() {
        const url =
            import.meta.env.VUE_APP_CONTEXT +
            'api/rest/impExp/exportSystemJSON?systemId=' +
            props.currTreeNodeInfo.id +
            '&access_token=' +
            y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
        window.open(url);
    }

    // 启用  禁用系统
    function handlerDisable() {
        const text = systemInfo.value.enabled ? '禁用' : '启用';
        ElMessageBox.confirm(t(`是否${text}该系统?`), t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info'
        })
            .then(async () => {
                loading.value = true;
                let result;
                if (text === '禁用') {
                    // 禁用系统 接口操作
                    result = await systemDisabled(props.currTreeNodeInfo.id);
                } else {
                    // 启用 系统 接口操作
                    result = await systemEnabled(props.currTreeNodeInfo.id);
                }
                loading.value = false;
                if (result.success) {
                    ElNotification({
                        title: t('成功'),
                        message: t(`${text}成功`),
                        type: 'success',
                        duration: 2000,
                        offset: 80
                    });
                    systemInfo.value = result.data;
                }
            })
            .catch(() => {
                loading.value = false;
                ElMessage({
                    type: 'info',
                    message: t(`已取消${text}`),
                    offset: 65
                });
            });
    }
</script>
<style lang="scss" scoped>
    :deep(.custom-right) {
        display: flex;
        align-items: center;
    }

    .basic-btns {
        display: flex;
        flex-wrap: wrap;
        justify-content: space-between;
        margin-bottom: 20px;
    }
</style>
