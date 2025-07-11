<template>
    <y9Card v-if="config_personal.itemList.length" key="personal" :showCloseButton="false" :title="$t('个人信息')">
        <y9Form ref="personalY9FormRef" :config="config_personal">
            <template #slotAlterPwdButton>
                <el-button
                    v-if="showAlterPwdCompsRef"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="alter-pwd"
                    type="danger"
                    @click="showAlterPwdComps"
                >
                    {{ $t('修改密码') }}
                </el-button>
                <el-button
                    v-else
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="alter-pwd"
                    @click="showAlterPwdComps"
                >
                    {{ $t('修改密码') }}
                </el-button>
                <el-button
                    v-if="editBtnText === '保存'"
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
                    v-else
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

    <br />

    <y9Card
        key="pwd"
        v-model:show="showAlterPwdCompsRef"
        :footerBtnConfig="pwd_footerBtnConfig"
        :showFooter="true"
        :title="$t('修改密码')"
    >
        <y9Form ref="passwordY9FormRef" :config="config_pwd"></y9Form>
    </y9Card>
    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { computed, inject, reactive, ref } from 'vue';
    import { ElMessage, ElNotification } from 'element-plus';
    import y9_storage from '@/utils/storage';
    import { checkPassword, getManagerById, modifyPassword, updateManager } from '@/api/manager/index';
    import { $validCheck } from '@/utils/validate';
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

    // 初始化
    init();

    // 编辑个人信息的button文本
    const editBtnText = ref('编辑个人信息');
    const personalY9FormRef = ref('');
    const isLoading = ref(false);

    function onClose() {
        editBtnText.value = '编辑个人信息';
        init();
    }

    // 个人信息配置 - 传入封装的y9Form组件
    const config_personal = reactive({
        descriptionsFormConfig: {
            //描述表单配置
            column: settingStore.device === 'mobile' ? 1 : 2,
            labelAlign: 'center',
            labelWidth: '150px',
            contentWidth: '200px'
        },
        // labelWidth: settingStore.device === 'mobile' ? '130px' : '236px',
        rules: {},
        itemList: []
    });

    const latestPersonalInfo = ref({} as any);

    // 不可编辑状态下
    async function init() {
        const data = await getManagerById(ssoUserInfo.personId).catch((e) => e);

        const { manager, nextCheckTime, nextModifyPwdTime, passwordModifiedCycle } = data.data;
        latestPersonalInfo.value = { manager, nextCheckTime, nextModifyPwdTime, passwordModifiedCycle };

        const disabled_itemList = [
            {
                type: 'text',
                label: computed(() => t('姓名')),
                prop: 'name',
                props: {
                    content: manager?.name
                }
            },
            {
                type: 'text',
                label: computed(() => t('登录名称')),
                prop: 'loginName',
                props: {
                    content: manager?.loginName
                }
            },
            {
                type: 'text',
                label: computed(() => t('性别')),
                prop: 'sex',
                props: {
                    content: manager?.sex == 1 ? computed(() => t('男')) : computed(() => t('女'))
                }
            },
            {
                type: 'text',
                label: computed(() => t('电子邮件')),
                prop: 'email',
                props: {
                    content: manager?.email
                }
            },
            {
                type: 'text',
                label: computed(() => t('移动电话')),
                prop: 'mobile',
                props: {
                    content: manager?.mobile
                }
            },
            {
                type: 'text',
                label: computed(() => t('下次审查时间')),
                props: {
                    content: nextCheckTime
                }
            },
            {
                type: 'text',
                label: computed(() => t('密码修改周期')),
                props: {
                    content: computed(() => passwordModifiedCycle + t('天'))
                }
            },
            {
                type: 'text',
                label: computed(() => t('下次修改密码时间')),
                props: {
                    content: nextModifyPwdTime
                }
            },
            {
                type: 'slot', //插槽类型
                labelWidth: settingStore.device === 'mobile' ? '50px' : '150px',
                props: {
                    slotName: 'slotAlterPwdButton' //插槽名称
                }
            }
        ];

        config_personal.itemList = disabled_itemList;
    }

    // 手机号码格式校验
    const mobileRex = (rule, value, callback) => {
        if (value != '' && value != null && !$validCheck('phone', value)) {
            return callback(new Error(computed(() => t('请输入正确电话或手机格式'))));
        }
        callback();
    };

    // 邮箱格式验证
    const emailRex = (rule, value, callback) => {
        if (value != '' && value != null && !$validCheck('email', value)) {
            return callback(new Error(computed(() => t('请输入正确的邮箱格式'))));
        }

        // 如果这是必填字段需要对空值进行验证
        // if (value == '' || value == null) {
        //     return callback(new Error('邮箱数据为空'));
        // }
        callback();
    };

    // 个人信息表单验证规则
    const personFormRules = {
        email: [{ validator: emailRex, trigger: 'blur' }],
        mobile: [{ validator: mobileRex, trigger: 'blur' }]
    };

    // 可编辑状态下
    function initEdit() {
        let loginName_prependText = '';

        if (
            ssoUserInfo.loginName != 'systemManager' &&
            ssoUserInfo.loginName != 'securityManager' &&
            ssoUserInfo.loginName != 'auditManager'
        ) {
            switch (ssoUserInfo.managerLevel) {
                case 1:
                    loginName_prependText = 'sys_';
                    latestPersonalInfo.value.manager.loginName = latestPersonalInfo.value.manager.loginName.slice(4);
                    break;

                case 2:
                    loginName_prependText = 'security_';
                    latestPersonalInfo.value.manager.loginName = latestPersonalInfo.value.manager.loginName.slice(9);
                    break;
                case 3:
                    loginName_prependText = 'audit_';
                    latestPersonalInfo.value.manager.loginName = latestPersonalInfo.value.manager.loginName.slice(6);
                    break;
                default:
                    break;
            }
        } else {
            loginName_prependText = '';
        }

        // 设置默认值
        const model = {
            sex: latestPersonalInfo.value.manager?.sex == 1 ? '1' : '0',
            name: latestPersonalInfo.value.manager?.name,
            loginName: latestPersonalInfo.value.manager?.loginName,
            email: latestPersonalInfo.value.manager?.email,
            mobile: latestPersonalInfo.value.manager?.mobile
        };

        const edit_itemList = [
            {
                type: 'input',
                label: computed(() => t('姓名')),
                prop: 'name'
            },
            {
                type: 'input',
                label: computed(() => t('登录名称')),
                prop: 'loginName',
                props: {
                    prependText: loginName_prependText
                }
            },
            {
                type: 'radio',
                label: computed(() => t('性别')),
                prop: 'sex',
                props: {
                    radioType: 'radio',
                    options: [
                        {
                            label: computed(() => t('男')),
                            value: '1'
                        },
                        {
                            label: computed(() => t('女')),
                            value: '0'
                        }
                    ]
                }
            },
            {
                type: 'input',
                prop: 'email',
                label: computed(() => t('电子邮件'))
            },
            {
                type: 'input',
                prop: 'mobile',
                label: computed(() => t('移动电话'))
            },
            {
                type: 'text',
                label: computed(() => t('下次审查时间')),
                props: {
                    content: latestPersonalInfo.value.nextCheckTime
                }
            },
            {
                type: 'text',
                label: computed(() => t('密码修改周期')),
                props: {
                    content: computed(() => latestPersonalInfo.value.passwordModifiedCycle + t('天'))
                }
            },
            {
                type: 'text',
                label: computed(() => t('下次修改密码时间')),
                props: {
                    content: latestPersonalInfo.value.nextModifyPwdTime
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
        config_personal.model = model;
        config_personal.itemList = edit_itemList;
        config_personal.rules = personFormRules;
    }

    // 提交表单
    function onSubmitPersonForm() {
        isLoading.value = true;
        // 接口可提交的所有字段
        const personalForm = reactive({
            id: ssoUserInfo.personId,
            parentId: ssoUserInfo.parentId,
            tenantId: ssoUserInfo.tenantId,
            name: '',
            loginName: '',
            sex: '',
            email: '',
            mobile: '',
            pass1: '',
            managerLevel: ssoUserInfo.managerLevel,
            globalManager: ssoUserInfo.globalManager
        });

        // 合并已填写的字段
        let formData = { ...personalForm, ...personalY9FormRef.value.model };

        if (
            ssoUserInfo.loginName != 'systemManager' &&
            ssoUserInfo.loginName != 'securityManager' &&
            ssoUserInfo.loginName != 'auditManager'
        ) {
            switch (ssoUserInfo.managerLevel) {
                case 1:
                    formData.loginName = 'sys_' + formData.loginName;
                    break;

                case 2:
                    formData.loginName = 'security_' + formData.loginName;
                    break;
                case 3:
                    formData.loginName = 'audit_' + formData.loginName;
                    break;
                default:
                    break;
            }
        }
        // 删除不知怎么来的 undefined 属性
        for (let key in formData) {
            if (key === 'undefined') {
                delete formData.undefined;
            }
        }
        return new Promise((resolve, reject) => {
            const y9FormInstance = personalY9FormRef.value.elFormRef;
            y9FormInstance.validate(async (valid) => {
                if (valid) {
                    let res = await updateManager(formData);
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
                        // 接口状态正确，立即关闭loading动画并重新初始化数据
                        init();
                        editBtnText.value = '编辑个人信息';
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
        switch (editBtnText.value) {
            case '编辑个人信息':
                editBtnText.value = '保存';
                initEdit();
                break;
            case '保存':
                // 保存接口
                onSubmitPersonForm();
                break;

            default:
                break;
        }
    }

    // 获取密码表单实例
    const passwordY9FormRef = ref('');
    // 密码修改表单的接口字段
    const passWordForm = reactive({
        id: ssoUserInfo.personId,
        oldPassword: '',
        newPassword: ''
    });

    // 当前密码验证
    const oldPasswordRex = async (rule, value, callback) => {
        if (!value) {
            return callback(new Error(t('当前密码不能为空')));
        }
        let check = await checkPassword(passWordForm.id, value);
        if (!check.data) {
            return callback(new Error(t('当前密码与原密码不一致！')));
        }
        passWordForm.oldPassword = value;
        // 验证通过
        callback();
    };

    // 新密码验证-1
    const newPasswordRex = (rule, value, callback) => {
        if (!value) {
            return callback(new Error(t('新密码不能为空')));
        }
        // 验证与当前密码是否相同
        if (value && value === passWordForm.oldPassword) {
            return callback(new Error(t('新密码不能和当前密码相同')));
        }
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
        oldPassword: { required: true, validator: oldPasswordRex, trigger: 'blur' },
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
                prop: 'oldPassword',
                label: computed(() => t('当前密码'))
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
        closeText: computed(() => t('关闭')), //关闭按钮名称，传空或者false不显示按钮
        closeDisabled: false, //关闭按钮禁用状态
        resetText: computed(() => t('重置')), // 重置按钮名称，传空或者false不显示按钮
        resetDisabled: false, //重置按钮禁用状态
        onOk: onSavePwd, // 确定按钮回调，支持promise，resolve()关闭对话框同时关闭onOkLoading，rejected()只关闭onOkLoading
        onReset: (newConfig) => {
            passwordY9FormRef.value.elFormRef.resetFields();
        }
    });

    // 显示修改密码组件
    const showAlterPwdCompsRef = ref(false);

    function showAlterPwdComps() {
        showAlterPwdCompsRef.value = true;
    }

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
                    ElNotification({
                        title: res.success ? t('成功') : t('失败'),
                        message: res.msg,
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
