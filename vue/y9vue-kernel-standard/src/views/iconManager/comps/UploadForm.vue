<script setup lang="ts">
    import { reactive, watch } from 'vue';
    import { useI18n } from 'vue-i18n';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useIconStore } from '@/store/modules/iconStore';
    import { Plus } from '@element-plus/icons-vue';
    import type { UploadProps, UploadUserFile, UploadFile } from 'element-plus';
    import { base64ToFile } from '@/utils/file';
    // import TestForm from './y9Form/index.vue';

    interface UploadFile {
        name: string;
        percentage?: number;
        status: UploadStatus;
        size?: number;
        response?: unknown;
        uid: number;
        url?: string;
        raw?: UploadRawFile;
    }
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const { t } = useI18n();
    const settingStore = useSettingStore();
    const iconStore = useIconStore();
    const disabled = ref(false);
    const emits = defineEmits(['update:fileList', 'update:edit']);
    const props = defineProps({
        iconData: {
            type: String,
            default: ''
        },
        name: {
            type: String,
            default: ''
        },
        remark: {
            type: String,
            default: ''
        },
        satus: {
            type: String,
            default: 'upload'
        }
    });
    let y9FormRef = ref();
    const formConfig = ref({
        //表单配置
        model: {
            id: '',
            name: '',
            remark: ''
        },
        rules: {
            //	表单验证规则。类型：FormRules
            iconFile: [{ required: true, message: computed(() => t('请选择图标')), trigger: 'change' }],
            name: [{ required: true, message: computed(() => t('请输入图标名称')), trigger: 'blur' }]
        },
        itemList: [
            {
                type: 'slot',
                label: computed(() => t('上传图标')),
                prop: 'iconFile',
                props: {
                    slotName: 'iconFiles'
                }
            },
            {
                type: 'input',
                label: computed(() => t('名称')),
                prop: 'name',
                props: {
                    events: {
                        input: (value) => {
                            emits('update:edit', { name: value, remark: formConfig.value.model.remark });
                        }
                    }
                }
            },

            {
                type: 'textarea',
                label: computed(() => t('备注')),
                prop: 'remark',
                props: {
                    events: {
                        input: (value) => {
                            emits('update:edit', { name: formConfig.value.model.name, remark: value });
                        }
                    }
                }
            }
        ],
        descriptionsFormConfig: {
            labelWidth: '200px',
            labelAlign: 'center'
        }
    });
    const fileList = ref<UploadUserFile[]>([]);
    const dialogImageUrl = ref('');
    const dialogVisible = ref(false);
    const AddIcon = ref('inline-flex');
    let tip_1 = '上传前，请确认图标的命名格式：xxx-red.png，让程序从名字中识别名字和颜色';
    let tip_2 = '如需更改图标，请删除图标后重新上传，编辑状态不支持更换图标';
    const Tips = ref(tip_1);
    watch(
        () => props.satus,
        (val) => {
            console.log(val);
            switch (val) {
                case 'upload':
                    // Tips.value = tip_1;
                    printEffect(tip_1);
                    disabled.value = false;
                    break;
                case 'edit':
                    // Tips.value = tip_2;
                    printEffect(tip_2);
                    disabled.value = true;
                    formConfig.value.model.name = props.name;
                    formConfig.value.model.remark = props.remark;
                    fileList.value = [];
                    let file = base64ToFile(props.iconData, props.name);
                    fileList.value = [
                        {
                            raw: file,
                            name: props.name,
                            uid: Date.now(),
                            status: 'null',
                            url: props.iconData
                        }
                    ];
                    break;
                default:
                    break;
            }
        },
        {
            immediate: true,
            deep: true
        }
    );
    watch(fileList, (fs) => {
        console.log('fileList: ', fs);
        showAddIcon(fs);
    });

    function printEffect(tips) {
        let count = tips.length;
        let index = 0;
        let timer = null;
        timer = setInterval(() => {
            index++;
            if (index >= count) {
                clearInterval(timer);
            }
            Tips.value = tips.substr(0, index);
        }, 200);
    }
    // 上传图标数量限，达到上限隐藏upload按钮
    let maxUploadCount = 6;
    function showAddIcon(fs) {
        if (fs.length >= maxUploadCount) {
            AddIcon.value = 'none';
        } else {
            AddIcon.value = 'inline-flex';
        }
        let fileName = fs[fs.length - 1].name.split('.')[0].split('-')[0];
        formConfig.value.model.name = fileName;
        emits('update:fileList', { fs, name: formConfig.value.model.name, remark: formConfig.value.model.remark });
    }

    const handleRemove: UploadProps['onRemove'] = (uploadFile, uploadFiles) => {
        // console.log(uploadFile, uploadFiles);
        showAddIcon(fileList.value);
    };

    // const handlePictureCardPreview: UploadProps['onPreview'] = (uploadFile) => {
    //     dialogImageUrl.value = uploadFile.url!;
    //     dialogVisible.value = true;
    // };
</script>
<template>
    <div class="IconManagerForm">
        <y9Form ref="y9FormRef" :config="formConfig">
            <template #iconFiles>
                <el-text class="mx-1" type="danger">{{ Tips }}</el-text>
                <el-upload
                    v-model:file-list="fileList"
                    action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15"
                    list-type="picture-card"
                    :on-remove="handleRemove"
                    :auto-upload="false"
                    :disabled="disabled"
                    multiple
                    class="upload-wrapper"
                >
                    <el-icon><Plus /></el-icon>
                </el-upload>

                <!-- <el-dialog v-model="dialogVisible">
                    <img w-full :src="dialogImageUrl" alt="Preview Image" />
                </el-dialog> -->
            </template>
        </y9Form>
    </div>
</template>
<style lang="scss" scoped>
    .IconManagerForm {
        :deep(.el-form .el-form-item__content .upload-wrapper) {
            .el-upload-list--picture-card .el-upload-list__item {
                width: 82px;
                height: 82px;
                .el-upload-list__item-actions:hover {
                    span.el-upload-list__item-preview {
                        display: none;
                    }
                    span.el-upload-list__item-delete {
                        margin-left: 0;
                    }
                }
            }
            .el-upload--picture-card {
                width: 82px;
                height: 82px;
                display: v-bind(AddIcon);
            }
        }
        :deep(.el-form .el-form-item__content .el-text) {
            position: relative;
            top: -5px;
        }
    }
</style>
