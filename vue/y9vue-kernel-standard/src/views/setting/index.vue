<template>
    <y9Card v-if="config_setting.itemList.length" key="setting" :showCloseButton="false" :title="$t('系统信息')">
        <y9Form ref="settingY9FormRef" :config="config_setting">
            <template #slotAlterPwdButton>
                <el-button
                    :loading="isLoading"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="alter-personInfo"
                    type="danger"
                    @click="onEdit"
                >
                    {{ $t(`${editBtnText}`) }}
                </el-button>
            </template>
        </y9Form>
    </y9Card>
    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { computed, inject, reactive, ref } from 'vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { getTenantSetting, saveTenantSetting } from '@/api/setting';

    const settingStore = useSettingStore();
    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');

    // 全局loading
    let loading = ref(false);

    // 初始化
    initEdit();

    // 编辑个人信息的button文本
    const editBtnText = ref('保存');
    const settingY9FormRef = ref('');
    const isLoading = ref(false);

    // 个人信息配置 - 传入封装的y9Form组件
    const config_setting = reactive({
        descriptionsFormConfig: {
            //描述表单配置
            column: settingStore.device === 1,
            labelAlign: 'center',
            labelWidth: '150px',
            contentWidth: '200px'
        },
        // labelWidth: settingStore.device === 'mobile' ? '130px' : '236px',
        rules: {},
        itemList: [],
        model: {}
    });

    // 个人信息表单验证规则
    const personFormRules = {};

    // 可编辑状态下
    async function initEdit() {
        const result = await getTenantSetting().catch((e) => e);

        // 设置默认值
        const model = {
            userDefaultPassword: result.data.userDefaultPassword,
            positionNameTemplate: result.data.positionNameTemplate
        };

        const edit_itemList = [
            {
                type: 'input',
                label: computed(() => t('默认用户密码')),
                prop: 'userDefaultPassword'
            },
            {
                type: 'input',
                label: computed(() => t('岗位名称格式')),
                prop: 'positionNameTemplate',
                props: {
                    placeholder:
                        "默认格式为：#jobName + '（' + #personNames + '）'，#jobName 会替换为职位名，#personNames 会替换为人员名称"
                }
            },
            {
                type: 'slot', //插槽类型,
                labelWidth: settingStore.device === 'mobile' ? '50px' : '150px',
                props: {
                    slotName: 'slotAlterPwdButton' //插槽名称
                }
            }
        ];
        config_setting.model = model;
        config_setting.itemList = edit_itemList;
        config_setting.rules = personFormRules;
    }

    // 提交表单
    function onSubmitSettingForm() {
        isLoading.value = true;
        // 接口可提交的所有字段
        const settingForm = reactive({
            userDefaultPassword: ''
        });

        // 合并已填写的字段
        let formData = { ...settingForm, ...settingY9FormRef.value.model };

        console.log(formData);

        // 删除不知怎么来的 undefined 属性
        for (let key in formData) {
            if (key === 'undefined') {
                delete formData.undefined;
            }
        }
        return new Promise((resolve, reject) => {
            const y9FormInstance = settingY9FormRef.value.elFormRef;
            y9FormInstance.validate(async (valid) => {
                if (valid) {
                    let res = await saveTenantSetting(formData);
                    // 无论接口成功或失败都给出提示信息
                    ElNotification({
                        title: res.success ? t('成功') : t('失败'),
                        message: res.msg,
                        type: res.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    if (res.code != 200) {
                        // 接口状态错误，三秒后关闭loading动画
                        setTimeout(() => {
                            isLoading.value = false;
                        }, 3000);
                    } else {
                        isLoading.value = false;
                    }
                    resolve();
                } else {
                    ElMessage({
                        type: 'error',
                        message: t('验证不通过，请检查'),
                        offset: 65
                    });
                    reject();
                }
            });
        });
    }

    function onEdit() {
        onSubmitSettingForm();
    }
</script>

<style lang="scss" scoped>
    .y9-card {
        & .y9-form {
            :deep(.alter-pwd) {
                cursor: pointer;

                &:hover {
                    color: var(--el-color-primary);
                }
            }
        }
    }
</style>
