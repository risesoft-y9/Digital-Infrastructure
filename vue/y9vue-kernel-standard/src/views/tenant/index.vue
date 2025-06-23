<template>
    <y9Card key="y9Tenant" :showCloseButton="false" :title="$t('个性化设置')">
        <y9Form ref="y9FormRef" :config="y9FormConfig">
            <template #slotEditButton>
                <el-button
                    v-if="editBtnText === '保存' && y9UserInfo.managerLevel < 4"
                    :loading="isLoading"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="alter-personInfo"
                    type="danger"
                    @click="onEdit"
                >
                    {{ $t(`${editBtnText}`) }}
                </el-button>
                <el-button
                    v-else-if="y9UserInfo.managerLevel < 4"
                    :loading="isLoading"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="alter-personInfo"
                    @click="onEdit"
                >
                    {{ $t(`${editBtnText}`) }}
                </el-button>
                <el-button
                    v-show="editBtnText === '保存'"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="alter-personInfo"
                    @click="onClose"
                >
                    {{ $t('关闭') }}
                </el-button>
            </template>
        </y9Form>
    </y9Card>
</template>

<script lang="ts" setup>
    import { getTenantById, saveY9Tenant, uploadTenantLogoIcon } from '@/api/tenant/index';
    import { h, inject, onMounted, ref } from 'vue';
    import { ElMessage, ElNotification, type UploadInstance, type UploadProps, type UploadRawFile } from 'element-plus';
    import { useI18n } from 'vue-i18n';
    import { useSettingStore } from '@/store/modules/settingStore';
    import y9_storage from '@/utils/storage';

    const { t } = useI18n();
    const settingStore = useSettingStore();

    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');

    const upload = ref<UploadInstance>();
    const uploadfile = ref({});

    const y9UserInfo = y9_storage.getObjectItem('ssoUserInfo');
    const editBtnText = ref('编辑信息');
    // 全局loading
    let isLoading = ref(false);

    async function getData() {
        const responseInfo = await getTenantById(y9UserInfo.tenantId);
        y9FormConfig.value.model = responseInfo.data;
        if (!y9FormConfig.value.model.parentId) {
            y9FormConfig.value.model.parentId = '';
        }
        if (!y9FormConfig.value.model.logoIcon || y9FormConfig.value.model.logoIcon === 'null') {
            y9FormConfig.value.model.logoIcon = '';
        } else {
            y9FormConfig.value.itemList.forEach((item) => {
                if (item.prop == 'logoIcon') {
                    item.props.fileList = [
                        {
                            name: '',
                            url: y9FormConfig.value.model.logoIcon
                        }
                    ];
                }
            });
        }
    }

    onMounted(() => {
        getData();
    });

    function handlerUpload(params) {
        y9FormConfig.value.model = y9FormRef.value?.model;
        uploadTenantLogoIcon('', params.file).then((res) => {
            ElNotification({
                title: '成功',
                message: res.msg,
                type: res.success ? 'success' : 'error',
                duration: 2000,
                offset: 80
            });
            if (res.success) {
                // 重新刷新树 数据
                y9FormConfig.value.model.logoIcon = res.data;
                y9FormConfig.value.itemList.forEach((item) => {
                    if (item.prop == 'logoIcon') {
                        item.props.fileList = [
                            {
                                name: params.file.name,
                                url: res.data
                            }
                        ];
                    }
                });
            }
        });
    }

    const handleExceed: UploadProps['onExceed'] = (files) => {
        upload.value!.clearFiles();
        const file = files[0] as UploadRawFile;
        upload.value!.handleStart(file);
    };

    const handleChange: UploadProps['onChange'] = (uploadFile, uploadFiles) => {
        if (uploadFile.raw?.type !== 'image/png') {
            ElNotification({
                title: '失败',
                message: '请选择png格式的图片!',
                type: 'error',
                duration: 2000,
                offset: 80
            });
            upload.value!.clearFiles();
            return false;
        } else if (uploadFile.raw.size / 1024 / 1024 > 2) {
            ElNotification({
                title: '失败',
                message: '上传图片大小不能超过 2MB!',
                type: 'error',
                duration: 2000,
                offset: 80
            });
            upload.value!.clearFiles();
            return false;
        }
        uploadfile.value = uploadFile.raw;
    };

    const y9FormRef = ref();

    const y9FormConfig = ref({
        descriptionsFormConfig: {
            //描述表单配置
            column: settingStore.device === 'mobile' ? 1 : 1,
            labelAlign: 'center',
            labelWidth: '150px',
            contentWidth: '200px'
        },
        model: {
            name: '',
            id: '',
            shortName: '',
            enabled: '',
            description: '',
            logoIcon: '',
            footer: ' ',
            parentId: ''
        },
        labelWidth: '120px',
        itemList: [
            {
                type: 'text',
                prop: 'logoIcon',
                label: t('logo'), //标签文本。类型：string
                type1: 'upload', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                props: {
                    defaultCustomClass: 'custom-picture',
                    listType: 'picture-card', //"text" | "picture" | "picture-card"
                    limit: 1,
                    drag: true,
                    accept: 'image/png',
                    multiple: false,
                    fileList: [] as any,
                    removeIcon: true,
                    replaceIcon: true,
                    httpRequest: handlerUpload,
                    onExceed: handleExceed,
                    onChange: handleChange,
                    render: () => {
                        //text类型渲染的内容
                        return h('img', { src: y9FormConfig.value.model?.logoIcon });
                    }
                }
            },
            {
                type: 'text',
                label: t('描述'),
                prop: 'description',
                type1: 'textarea', //自定义字段-编辑时显示的类型
                type2: 'text', //自定义字段-非编辑状态显示文本类型
                props: {
                    render: () => {
                        //text类型渲染的内容
                        return h('span', y9FormConfig.value.model?.description);
                    }
                }
            },
            {
                type: 'slot', //插槽类型,
                labelWidth: settingStore.device === 'mobile' ? '50px' : '150px',
                type1: 'slot', //自定义字段-编辑时显示的类型
                type2: 'slot', //自定义字段-非编辑状态显示文本类型
                props: {
                    slotName: 'slotEditButton' //插槽名称
                }
            }
        ]
    });

    function onEdit() {
        switch (editBtnText.value) {
            case '编辑信息':
                editBtnText.value = '保存';
                initEdit(true);
                break;
            case '保存':
                // 保存接口
                saveTenantInfo();
                break;

            default:
                break;
        }
    }

    function initEdit(isEdit) {
        //编辑模式显示type1类型 非编辑模式显示type2类型
        y9FormConfig.value.itemList.forEach((item) => {
            item.type = isEdit ? item.type1 : item.type2;
        });
    }

    async function saveTenantInfo() {
        const ruleFormRef = y9FormRef.value.elFormRef;
        if (!ruleFormRef) return;
        isLoading.value = true;
        await ruleFormRef.validate(async (valid, fields) => {
            if (valid) {
                let res = {} as any;
                res = await saveY9Tenant(y9FormRef.value.model);
                isLoading.value = false;
                if (res.success) {
                    ElNotification({
                        title: res.success ? t('成功') : t('失败'),
                        message: res.msg,
                        type: res.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    getData();
                    onClose();
                } else {
                    ElNotification({
                        title: res.success ? t('成功') : t('失败'),
                        message: res.msg,
                        type: res.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                }
            } else {
                ElMessage({
                    message: '验证失败，请重新确认表单信息!!',
                    type: 'warning'
                });
                isLoading.value = false;
                return false;
            }
        });
    }

    function onClose() {
        editBtnText.value = '编辑信息';
        initEdit(false);
    }
</script>
<style lang="scss" scoped>
    .avatar {
        max-width: 180px;
    }

    .avatar-uploader-icon {
        --el-upload-picture-card-size: 148px;
        background-color: var(--el-fill-color-lighter);
        border: 1px dashed #cdd0d6;
        border-radius: 6px;
        box-sizing: border-box;
        width: 45px;
        height: 45px;
        cursor: pointer;
        vertical-align: top;
        display: inline-flex;
        justify-content: center;
        align-items: center;
    }

    :deep(.el-form-item__content img) {
        max-height: 150px;
    }
</style>
