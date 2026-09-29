<template>
    <y9Card :title="`${$t('基本信息')} - ${vendorInfo.name || ''}`">
        <div class="basic-btns">
            <el-button
                v-if="!editing"
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                class="global-btn-main"
                type="primary"
                @click="changeFormType(true)"
            >
                <i class="ri-edit-line"></i>
                {{ $t('编辑') }}
            </el-button>
            <template v-else>
                <el-button
                    :loading="saving"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-main"
                    type="primary"
                    @click="save"
                >
                    <i class="ri-save-line"></i>
                    {{ $t('保存') }}
                </el-button>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-second"
                    @click="changeFormType(false)"
                >
                    <i class="ri-close-line"></i>
                    {{ $t('取消') }}
                </el-button>
            </template>
        </div>

        <y9Form ref="y9FormRef" :config="formConfig"></y9Form>
    </y9Card>
</template>

<script lang="ts" setup>
    import { computed, h, inject, onMounted, ref, watch } from 'vue';
    import { useI18n } from 'vue-i18n';

    import { checkSystemVendorLoginName, getSystemVendorById, saveSystemVendor } from '@/api/systemVendor/index';

    const props = defineProps({
        currTreeNodeInfo: {
            type: Object,
            default: () => ({})
        }
    });
    const emit = defineEmits(['saved']);

    const { t } = useI18n();
    const fontSizeObj: any = inject('sizeObjInfo');
    const y9FormRef = ref();
    const vendorInfo = ref({} as any);
    const editing = ref(false);
    const saving = ref(false);

    const checkLoginName = (rule, value, callback) => {
        if (!value) {
            callback(new Error(t('请输入登录名称')));
            return;
        }
        checkSystemVendorLoginName(formConfig.value.model.id, value).then((result) => {
            if (result.data) {
                callback();
            } else {
                callback(new Error(t('该登录名称已存在，请重新输入登录名称')));
            }
        });
    };

    const formConfig = ref({
        descriptionsFormConfig: {
            column: 2,
            labelAlign: 'center',
            labelWidth: '150px',
            contentWidth: '200px'
        },
        model: {},
        rules: {},
        itemList: [
            createFormItem('name', '人员名称', 'input'),
            createFormItem('loginName', '登录名称', 'input'),
            createFormItem('email', '电子邮件', 'input'),
            createFormItem('mobile', '移动电话', 'input'),
            createFormItem('description', '人员描述', 'textarea')
        ]
    });

    function createFormItem(prop, label, editType) {
        return {
            type: 'text',
            type1: editType,
            type2: 'text',
            prop,
            label: computed(() => t(label)),
            props: {
                render: () => h('span', vendorInfo.value?.[prop] || '')
            }
        };
    }

    async function getInfo() {
        const result = await getSystemVendorById(props.currTreeNodeInfo.id);
        if (result.success) {
            vendorInfo.value = result.data;
            formConfig.value.model = { ...result.data };
        }
    }

    function changeFormType(isEdit) {
        editing.value = isEdit;
        formConfig.value.model = { ...vendorInfo.value };
        formConfig.value.rules = isEdit
            ? {
                  name: [{ required: true, message: computed(() => t('请输入姓名')), trigger: 'blur' }],
                  loginName: [{ required: true, validator: checkLoginName, trigger: 'blur' }]
              }
            : {};
        formConfig.value.itemList.forEach((item) => {
            item.type = isEdit ? item.type1 : item.type2;
        });
    }

    async function save() {
        const valid = await y9FormRef.value.elFormRef.validate().catch(() => false);
        if (!valid) {
            return;
        }
        saving.value = true;
        const result = await saveSystemVendor(y9FormRef.value.model);
        saving.value = false;
        ElNotification({
            title: result.success ? t('成功') : t('失败'),
            message: result.success ? t('更新成功') : result.msg,
            type: result.success ? 'success' : 'error',
            duration: 2000,
            offset: 80
        });
        if (result.success) {
            vendorInfo.value = result.data;
            changeFormType(false);
            emit('saved', result.data);
        }
    }

    onMounted(getInfo);
    watch(
        () => props.currTreeNodeInfo.id,
        (newId, oldId) => {
            if (newId && newId !== oldId) {
                editing.value = false;
                getInfo();
            }
        }
    );
</script>

<style lang="scss" scoped>
    .basic-btns {
        margin-bottom: 10px;
        text-align: left;
    }
</style>
