<template>
    <div class="password-view" :class="{ 'is-standalone': !embedded }">
        <div class="password-content" :class="{ 'is-embedded': embedded }">
            <header v-if="!embedded" class="password-header" aria-live="polite">
                <div class="password-security-icon" aria-hidden="true">
                    <svg viewBox="0 0 32 32" fill="none">
                        <path d="M16 3 27 7v8c0 7-4.5 11.5-11 14C9.5 26.5 5 22 5 15V7L16 3Z" />
                        <path d="M16 11v7M16 22v.1" />
                    </svg>
                </div>
                <h1>{{ $t('您的密码已过期，请先修改密码') }}</h1>
            </header>
            <el-form
                ref="passwordRef"
                :model="passwordForm"
                :rules="passwordFormRules"
                label-position="top"
                hide-required-asterisk
                class="password-form"
                @submit.prevent="handlerPassword"
            >
                <el-form-item :label="$t('当前密码')" prop="oldPassword">
                    <el-input
                        v-model="passwordForm.oldPassword"
                        type="password"
                        show-password
                        autocomplete="current-password"
                        :placeholder="$t('请输入当前密码')"
                    />
                </el-form-item>

                <el-form-item :label="$t('新密码')">
                    <el-input
                        v-model="passwordForm.newPassword"
                        type="password"
                        show-password
                        autocomplete="new-password"
                        :placeholder="$t('请输入新密码')"
                        aria-describedby="password-policy"
                    />
                </el-form-item>

                <section id="password-policy" class="password-policy" aria-live="polite">
                    <div
                        v-loading="passwordPolicyLoading"
                        class="password-rules-panel"
                        :class="{ 'is-loading': passwordPolicyLoading }"
                    >
                        <ul v-if="passwordPolicy" class="password-rules">
                            <li
                                v-for="rule in passwordPolicyTips"
                                :key="rule.key"
                                :class="{ 'is-satisfied': rule.satisfied }"
                            >
                                <i
                                    :class="
                                        rule.satisfied ? 'ri-checkbox-circle-line' : 'ri-checkbox-blank-circle-line'
                                    "
                                    aria-hidden="true"
                                />
                                <span>{{ rule.text }}</span>
                                <span class="sr-only">{{ $t(rule.satisfied ? '已满足' : '未满足') }}</span>
                            </li>
                        </ul>
                        <p v-else-if="passwordPolicyLoading" class="password-policy-status">
                            {{ $t('正在加载密码要求…') }}
                        </p>
                        <div v-else class="password-policy-status">
                            <p>{{ $t('密码策略加载失败，请重试') }}</p>
                            <el-button link type="primary" @click="loadPasswordPolicy">
                                {{ $t('重新加载密码策略') }}
                            </el-button>
                        </div>
                    </div>
                </section>

                <el-form-item :label="$t('确认新密码')" prop="confirmPassword">
                    <el-input
                        v-model="passwordForm.confirmPassword"
                        type="password"
                        show-password
                        autocomplete="new-password"
                        :placeholder="$t('请再次输入新密码')"
                    />
                </el-form-item>

                <el-button
                    class="password-submit"
                    type="primary"
                    native-type="submit"
                    :disabled="!canSubmitPassword"
                    :loading="loading"
                >
                    {{ $t('修改密码') }}
                </el-button>
            </el-form>

            <p class="password-footer">
                {{ $t('修改成功后请使用新密码重新登录。') }}
            </p>
        </div>
    </div>
</template>
<script lang="ts" setup>
    import { computed, inject, onMounted, reactive, ref, watch } from 'vue';
    import type { FormInstance, FormRules } from 'element-plus';
    import { useI18n } from 'vue-i18n';
    import { getPasswordStrength, modifyPassword } from '@/api/manager';
    import { $y9_SSO } from '@/main';

    const props = withDefaults(defineProps<{ embedded?: boolean }>(), {
        embedded: false
    });
    const { t } = useI18n();
    const fontSizeObj: any = inject('sizeObjInfo', { baseFontSize: '14px' });
    const loading = ref(false);
    const passwordRef = ref<FormInstance>();
    const passwordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' });

    interface PasswordStrength {
        minLength: number;
        requireUppercase: boolean;
        requireLowercase: boolean;
        requireDigit: boolean;
        requireSymbol: boolean;
    }

    const passwordPolicy = ref<PasswordStrength | null>(null);
    const passwordPolicyLoading = ref(false);
    // 依据服务端策略生成规则，输入时即时显示每项要求是否满足。
    const passwordPolicyRules = computed(() => {
        const policy = passwordPolicy.value;
        if (!policy) return [];
        const rules = [
            {
                key: 'minLength',
                text: t('密码长度至少为 {length} 位', { length: policy.minLength }),
                test: (value: string) => value.length >= policy.minLength
            }
        ];
        const characterRules = [
            { key: 'requireUppercase', text: t('必须包含大写字母'), pattern: /[A-Z]/ },
            { key: 'requireLowercase', text: t('必须包含小写字母'), pattern: /[a-z]/ },
            { key: 'requireDigit', text: t('必须包含数字'), pattern: /[0-9]/ },
            { key: 'requireSymbol', text: t('必须包含英文标点符号'), pattern: /[\x21-\x2F\x3A-\x40\x5B-\x60\x7B-\x7E]/ }
        ] as const;
        characterRules.forEach((rule) => {
            if (policy[rule.key]) {
                rules.push({ key: rule.key, text: rule.text, test: (value: string) => rule.pattern.test(value) });
            }
        });
        // 个人中心保留原有的新旧密码不同要求；独立改密页不增加该限制。
        if (props.embedded)
            rules.push({
                key: 'differentPassword',
                text: t('新密码不能与当前密码相同'),
                test: (value: string) => passwordForm.oldPassword.length > 0 && value !== passwordForm.oldPassword
            });
        return rules;
    });
    const passwordPolicyTips = computed(() =>
        passwordPolicyRules.value.map((rule) => ({
            key: rule.key,
            text: rule.text,
            satisfied: passwordForm.newPassword.length > 0 && rule.test(passwordForm.newPassword)
        }))
    );
    const passwordFormRules = computed<FormRules>(() => ({
        oldPassword: [
            {
                required: true,
                message: t('当前密码不能为空'),
                trigger: 'blur'
            }
        ],
        confirmPassword: [
            {
                required: true,
                validator: (rule, value, callback) => {
                    if (!value) return callback(new Error(t('请输入确认的密码!')));
                    callback(value === passwordForm.newPassword ? undefined : new Error(t('两次输入密码不一致!')));
                },
                trigger: 'blur'
            }
        ]
    }));
    const canSubmitPassword = computed(
        () =>
            !!passwordPolicy.value &&
            !passwordPolicyLoading.value &&
            !loading.value &&
            passwordForm.oldPassword.length > 0 &&
            passwordForm.newPassword.trim().length > 0 &&
            passwordPolicyTips.value.every((rule) => rule.satisfied) &&
            passwordForm.confirmPassword === passwordForm.newPassword
    );

    /** 每次打开改密表单时读取最新策略；加载失败时禁用提交并提供重试。 */
    async function loadPasswordPolicy() {
        if (passwordPolicyLoading.value) return;
        passwordPolicyLoading.value = true;
        try {
            const result = await getPasswordStrength();
            const policy = result?.data;
            if (
                !result?.success ||
                !Number.isInteger(policy?.minLength) ||
                policy.minLength < 6 ||
                policy.minLength > 32 ||
                !['requireUppercase', 'requireLowercase', 'requireDigit', 'requireSymbol'].every(
                    (key) => typeof policy[key] === 'boolean'
                )
            )
                throw new Error('Invalid password policy response');
            passwordPolicy.value = result.data;
            passwordRef.value?.clearValidate('confirmPassword');
        } catch (error) {
            passwordPolicy.value = null;
            ElNotification({
                title: t('失败'),
                message: t('密码策略加载失败，请重试'),
                type: 'error',
                duration: 4000,
                position: 'top-right',
                offset: 80
            });
        } finally {
            passwordPolicyLoading.value = false;
        }
    }

    watch(
        () => passwordForm.newPassword,
        () => {
            if (passwordForm.confirmPassword) passwordRef.value?.validateField('confirmPassword').catch(() => {});
        }
    );
    onMounted(loadPasswordPolicy);

    /** 两个入口均只在前端校验必填项和密码规则，提交时由后端核验原密码，成功后清空表单并退出登录。 */
    async function handlerPassword() {
        if (!canSubmitPassword.value || !passwordRef.value) return;
        loading.value = true;
        try {
            const valid = await passwordRef.value.validate().catch(() => false);
            if (!valid) return;
            const result = await modifyPassword(passwordForm.oldPassword, passwordForm.newPassword);
            if (result.success) {
                Object.assign(passwordForm, { oldPassword: '', newPassword: '', confirmPassword: '' });
                passwordRef.value.clearValidate();
                await ElMessageBox.alert(t('密码修改成功，请使用新密码重新登录。'), t('成功'), {
                    type: 'success',
                    confirmButtonText: t('确定'),
                    showClose: false,
                    closeOnClickModal: false,
                    closeOnPressEscape: false
                });
                await $y9_SSO.ssoLogout({ redirect_uri: window.location.origin + import.meta.env.VUE_APP_PUBLIC_PATH });
            } else {
                ElNotification({
                    title: t('失败'),
                    message: result.msg,
                    type: 'error',
                    duration: 4000,
                    position: 'top-right',
                    offset: 80
                });
            }
        } catch (error) {
            ElNotification({
                title: t('失败'),
                message: error.msg || t('密码修改失败，请重试'),
                type: 'error',
                duration: 4000,
                position: 'top-right',
                offset: 80
            });
        } finally {
            loading.value = false;
        }
    }
</script>
<style lang="scss" scoped>
    .password-view {
        width: 100%;
        min-width: 0;
        box-sizing: border-box;
        padding: 32px 24px;
        background: var(--el-bg-color, #fff);
        color: var(--el-text-color-primary, #1d2536);
        font-size: v-bind('fontSizeObj.baseFontSize');
        container-type: inline-size;

        &.is-standalone {
            display: flex;
            flex-direction: column;
            min-height: 100vh;
            min-height: 100dvh;

            .password-content {
                flex-shrink: 0;
                margin-block: auto;
            }
        }
    }

    .password-content {
        width: min(100%, 480px);
        margin: 0 auto;

        &.is-embedded {
            width: 100%;
            max-width: 480px;
        }
    }

    .password-header {
        margin-bottom: 28px;
        text-align: center;

        h1 {
            margin: 0 0 14px;
            font-size: 1.6em;
            font-weight: 700;
            line-height: 1.5;
        }

        p {
            margin: 0;
            color: var(--el-text-color-secondary, #6d7890);
            font-size: 1.05em;
            line-height: 1.8;
        }
    }

    .password-security-icon {
        display: grid;
        place-items: center;
        width: 64px;
        height: 64px;
        margin: 0 auto 22px;
        border: 1px solid var(--el-color-warning-light-5, #ffda94);
        border-radius: 18px;
        background: var(--el-color-warning-light-9, #fff8e8);
        color: var(--el-color-warning, #f59e0b);

        svg {
            width: 32px;
            height: 32px;
            stroke: currentColor;
            stroke-width: 2.5;
            stroke-linecap: round;
            stroke-linejoin: round;
        }
    }

    .password-form {
        :deep(.el-form-item) {
            display: block;
            margin-bottom: 24px;
            font-size: inherit;
        }

        :deep(.el-form-item__label) {
            height: auto;
            margin-bottom: 10px;
            padding: 0;
            color: var(--el-text-color-primary, #1d2536);
            font-size: 1.05em;
            font-weight: 600;
            line-height: 1.6;
        }

        :deep(.el-form-item__content) {
            display: block;
            font-size: inherit;
            line-height: normal;
        }

        :deep(.el-input) {
            font-size: inherit;
        }

        :deep(.el-input__wrapper) {
            min-height: 48px;
            box-sizing: border-box;
            padding: 1px 16px;
            border-radius: 12px;
            box-shadow: 0 0 0 1px var(--el-border-color, #d8dfed) inset;

            &:hover {
                box-shadow: 0 0 0 1px var(--el-color-primary-light-5) inset;
            }

            &.is-focus {
                box-shadow: 0 0 0 1px var(--el-color-primary) inset;
            }
        }

        :deep(.el-input__inner) {
            font-size: 1.05em;
        }

        :deep(.el-form-item.is-error .el-input__wrapper) {
            box-shadow: 0 0 0 1px var(--el-color-danger) inset;
        }

        :deep(.el-form-item__error) {
            padding-top: 5px;
            font-size: 0.85em;
        }
    }

    .password-policy {
        margin: -12px 0 24px;
    }

    // 仅加载时为动画预留空间，加载完成后按规则内容自然收缩。
    .password-rules-panel.is-loading {
        min-height: 64px;
    }

    .password-rules {
        display: flex;
        flex-direction: column;
        gap: 6px;
        margin: 0;
        padding: 0;
        list-style: none;
        color: var(--el-text-color-secondary, #6d7890);

        li {
            display: flex;
            align-items: baseline;
            gap: 8px;
            line-height: 1.5;
            overflow-wrap: anywhere;

            i {
                flex: 0 0 auto;
                align-self: center;
            }

            &.is-satisfied {
                color: var(--el-color-success);
            }
        }
    }

    .password-policy-status {
        margin: 0;
        color: var(--el-text-color-secondary);
        line-height: 1.6;
        text-align: center;

        p {
            margin: 0 0 4px;
        }
    }

    .password-submit {
        width: 100%;
        height: 50px;
        margin-top: 16px;
        border-radius: 12px;
        font-size: 1.1em;
        font-weight: 600;

        &.is-disabled,
        &.is-disabled:hover {
            border-color: var(--el-color-primary-light-5);
            background: var(--el-color-primary-light-5);
            color: #fff;
        }
    }

    .password-footer {
        margin: 20px 0 0;
        color: var(--el-text-color-secondary, #6d7890);
        font-size: 0.9em;
        line-height: 1.8;
        text-align: center;
    }

    .sr-only {
        position: absolute;
        width: 1px;
        height: 1px;
        overflow: hidden;
        clip-path: inset(50%);
        white-space: nowrap;
    }
</style>
