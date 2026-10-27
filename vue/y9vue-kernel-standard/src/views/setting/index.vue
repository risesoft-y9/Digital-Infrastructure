<template>
    <y9Card :showCloseButton="false" :title="$t('系统设置')">
        <el-form
            ref="settingFormRef"
            v-loading="loading"
            :model="model"
            :rules="rules"
            :label-width="settingStore.device === 'mobile' ? '140px' : '180px'"
            class="setting-form"
        >
            <el-form-item :label="$t('默认密码')" prop="userDefaultPassword">
                <el-input v-model="model.userDefaultPassword" type="password" show-password />
            </el-form-item>
            <el-form-item :label="$t('岗位名称格式')" prop="positionNameTemplate">
                <el-input v-model="model.positionNameTemplate" :placeholder="positionNamePlaceholder" />
            </el-form-item>
            <el-divider content-position="left">{{ $t('普通用户密码策略') }}</el-divider>
            <el-form-item :label="$t('默认密码强制修改')" prop="userDefaultPasswordChangeRequired">
                <el-switch v-model="model.userDefaultPasswordChangeRequired" />
            </el-form-item>
            <el-form-item :label="$t('密码有效天数')" prop="userPasswordMaxAge">
                <el-input-number v-model="model.userPasswordMaxAge" :min="0" :max="365" :step="1" step-strictly />
                <span class="setting-tip">{{ $t('0 为永久，设置范围 0～365') }}</span>
            </el-form-item>
            <el-form-item :label="$t('密码最小长度')" prop="userPasswordMinLength">
                <el-input-number v-model="model.userPasswordMinLength" :min="6" :max="32" :step="1" step-strictly />
                <span class="setting-tip">{{ $t('设置范围 6～32 位') }}</span>
            </el-form-item>
            <el-form-item :label="$t('密码必须包含')">
                <el-checkbox v-model="model.userPasswordRequireUppercase">{{ $t('大写字母') }}</el-checkbox>
                <el-checkbox v-model="model.userPasswordRequireLowercase">{{ $t('小写字母') }}</el-checkbox>
                <el-checkbox v-model="model.userPasswordRequireDigit">{{ $t('数字') }}</el-checkbox>
                <el-checkbox v-model="model.userPasswordRequireSymbol">{{ $t('英文标点符号') }}</el-checkbox>
            </el-form-item>
            <el-form-item>
                <el-button :loading="isLoading" :disabled="loading || !loaded" type="primary" @click="onSave">
                    {{ $t('保存') }}
                </el-button>
            </el-form-item>
        </el-form>
    </y9Card>
</template>

<script lang="ts" setup>
    import { computed, onMounted, reactive, ref } from 'vue';
    import { useI18n } from 'vue-i18n';
    import type { FormInstance, FormRules } from 'element-plus';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { getTenantSetting, saveTenantSetting } from '@/api/setting';

    const { t } = useI18n();
    const settingStore = useSettingStore();
    const positionNamePlaceholder = computed(() =>
        t("默认格式为：#jobName + '（' + #personNames + '）'，#jobName 会替换为职位名，#personNames 会替换为人员名称")
    );
    const settingFormRef = ref<FormInstance>();
    const loading = ref(false);
    const loaded = ref(false);
    const isLoading = ref(false);
    const model = reactive({
        userDefaultPassword: '',
        positionNameTemplate: '',
        userDefaultPasswordChangeRequired: false,
        userPasswordMaxAge: 0,
        userPasswordMinLength: 6,
        userPasswordRequireUppercase: false,
        userPasswordRequireLowercase: false,
        userPasswordRequireDigit: false,
        userPasswordRequireSymbol: false
    });
    const rules = computed<FormRules>(() => ({
        userPasswordMaxAge: [
            { type: 'integer', min: 0, max: 365, message: t('请输入 0～365 的整数'), trigger: 'change' }
        ],
        userPasswordMinLength: [
            { type: 'integer', min: 6, max: 32, message: t('最小长度须设置为 6～32 的整数'), trigger: 'change' }
        ]
    }));

    onMounted(async () => {
        loading.value = true;
        try {
            const result = await getTenantSetting();
            if (!result.success) {
                ElMessage.error(result.msg || t('读取系统设置失败'));
                return;
            }
            for (const key of Object.keys(model)) {
                if (result.data[key] != null) model[key] = result.data[key];
            }
            loaded.value = true;
        } catch (error) {
            ElMessage.error(t('读取系统设置失败'));
        } finally {
            loading.value = false;
        }
    });

    async function onSave() {
        if (!(await settingFormRef.value?.validate().catch(() => false))) {
            ElMessage.warning(t('验证不通过，请检查'));
            return;
        }
        isLoading.value = true;
        try {
            const result = await saveTenantSetting({ ...model });
            ElNotification({
                title: result.success ? t('成功') : t('失败'),
                message: result.success ? t('保存成功') : result.msg || t('保存失败'),
                type: result.success ? 'success' : 'error',
                duration: 2000,
                offset: 80
            });
        } catch (error) {
            ElMessage.error(t('保存系统设置失败'));
        } finally {
            isLoading.value = false;
        }
    }
</script>

<style lang="scss" scoped>
    .setting-form {
        max-width: 1000px;

        :deep(.el-form-item__label) {
            height: auto;
            min-height: 32px;
            line-height: 1.5;
            align-items: center;
        }
    }
    .setting-tip {
        margin-left: 12px;
        color: var(--el-text-color-secondary);
        font-size: 13px;
    }
</style>
