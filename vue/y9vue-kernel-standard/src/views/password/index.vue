<template>
    <y9Card
        key="pwd"
        :footerBtnConfig="pwd_footerBtnConfig"
        :showFooter="true"
        :title="$t('更新密码')"
        class="password-card"
    >
        <y9Form ref="passwordY9FormRef" :config="config_pwd"></y9Form>
    </y9Card>
    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { computed, inject, reactive, ref } from 'vue';
    import y9_storage from '@/utils/storage';
    import { modifyPassword } from '@/api/manager/index';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { $y9_SSO } from '@/main';

    const settingStore = useSettingStore();
    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');

    // 重新获取个人信息（登陆返回的个人信息有缺失的属性）
    const ssoUserInfo = y9_storage.getObjectItem('ssoUserInfo');

    // 全局loading
    let loading = ref(false);

    // 获取密码表单实例
    const passwordY9FormRef = ref('');
    // 密码修改表单的接口字段
    const passWordForm = reactive({
        id: ssoUserInfo.personId,
        newPassword: ''
    });

    // 新密码验证-1
    const newPasswordRex = (rule, value, callback) => {
        if (!value) {
            return callback(new Error(t('新密码不能为空')));
        }
        // 验证与当前密码是否相同
        // if (value && value === passWordForm.oldPassword) {
        //     return callback(new Error(t('新密码不能和当前密码相同')));
        // }
        // 验证是否符合密码规则 - 长度
        if (value.length < 8) {
            return callback(new Error(t('新密码不符合验证规则，长度必须大于或等于8')));
        }
        // 验证是否符合密码规则 - 数字
        if (!/[0-9]+/.test(value)) {
            return callback(new Error(t('新密码不符合验证规则，必须包含”数字“')));
        }
        // 验证是否符合密码规则 - 小写字母
        if (!/[a-z]+/.test(value)) {
            return callback(new Error(t('新密码不符合验证规则，必须包含”小写字母“')));
        }
        // 验证是否符合密码规则 - 大写字母
        if (!/[A-Z]+/.test(value)) {
            return callback(new Error(t('新密码不符合验证规则，必须包含”大写字母“')));
        }
        // 验证通过
        passWordForm.newPassword = value;
        callback();
    };
    // 新密码验证-2
    const checknewpasswordRex = (rule, value, callback) => {
        if (!value) {
            return callback(new Error(t('请再次输入新密码确认')));
        }
        // 验证是否与输入的新密码一致
        if (value && passWordForm.newPassword !== value) {
            return callback(new Error(t('与前一次输入的新密码不一致，请检查或重新输入')));
        }
        // 验证通过
        callback();
    };

    // 密码表单验证规则
    const passwordFormRules = {
        newPassword: { required: true, validator: newPasswordRex, trigger: 'blur' },
        checknewpassword: { required: true, validator: checknewpasswordRex, trigger: 'blur' }
    };

    // 修改密码
    const config_pwd = reactive({
        rules: passwordFormRules,
        labelWidth: settingStore.device === 'mobile' ? '100px' : '150px',
        itemList: [
            {
                type: 'text',
                props: {
                    content: computed(() => t('新密码必须包含”数字“、”大写字母“、”小写字母“，长度大于或等于8'))
                }
            },
            {
                type: 'input',
                props: {
                    type: 'password'
                },
                prop: 'newPassword',
                label: computed(() => t('新密码'))
            },
            {
                type: 'input',
                props: {
                    type: 'password'
                },
                prop: 'checknewpassword',
                label: computed(() => t('确认密码'))
            }
        ]
    });

    const pwd_footerBtnConfig = reactive({
        okLoading: false,
        okText: computed(() => t('确定')), //确定按钮名称，传空或者false不显示按钮
        okDisabled: false, //确定按钮禁用状态
        closeText: false, //关闭按钮名称，传空或者false不显示按钮
        closeDisabled: false, //关闭按钮禁用状态
        resetText: computed(() => t('重置')), // 重置按钮名称，传空或者false不显示按钮
        resetDisabled: false, //重置按钮禁用状态
        onOk: onSavePwd, // 确定按钮回调，支持promise，resolve()关闭对话框同时关闭onOkLoading，rejected()只关闭onOkLoading
        onReset: (newConfig) => {
            passwordY9FormRef.value.elFormRef.resetFields();
        }
    });

    // 密码修改 - 确定按钮
    function onSavePwd() {
        pwd_footerBtnConfig.okLoading = true;
        loading.value = true;
        const y9FormPasswordInstance = passwordY9FormRef.value.elFormRef;
        return new Promise((resolve, reject) => {
            y9FormPasswordInstance.validate(async (valid) => {
                if (valid) {
                    let res = await modifyPassword(passWordForm.id, passWordForm.newPassword);
                    // 无论接口成功或失败都给出提示信息
                    // ElMessage({
                    //     message: res.msg,
                    //     type: res.success ? 'success' : 'error',
                    //     offset: 60
                    // });
                    ElNotification({
                        title: res.success ? t('成功') : t('失败'),
                        message: '更新成功',
                        type: res.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    if (res.code != 0) {
                        // 接口状态错误，三秒后关闭loading动画
                        setTimeout(() => {
                            pwd_footerBtnConfig.okLoading = false;
                            loading.value = false;
                        }, 3000);
                    } else {
                        // 接口状态正确，立即关闭loading动画并重新初始化数据
                        pwd_footerBtnConfig.okLoading = false;
                        loading.value = false;
                        ElMessageBox.confirm('密码修改成功，即将跳转登录页面重新登录', '提示', {
                            confirmButtonText: 'OK',
                            type: 'warning',
                            showClose: false,
                            showCancelButton: false,
                            closeOnClickModal: false,
                            closeOnPressEscape: false
                        }).then(() => {
                            try {
                                const params = {
                                    redirect_uri: window.location.origin + import.meta.env.VUE_APP_PUBLIC_PATH
                                };
                                $y9_SSO.ssoLogout(params);
                            } catch (error) {
                                ElMessage.error(error.msg || 'Has Error');
                            }
                        });
                    }
                    resolve();
                } else {
                    ElMessage({
                        type: 'error',
                        message: t('验证不通过，请检查'),
                        offset: 65
                    });
                    pwd_footerBtnConfig.okLoading = false;
                    loading.value = false;
                    reject();
                }
            });
        });
    }
</script>

<style lang="scss" scoped>
    .password-card {
        width: 60%;
        margin: 10% auto;
    }
</style>
