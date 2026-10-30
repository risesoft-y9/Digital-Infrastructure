<template>
    <y9Card :showCloseButton="false" :title="$t('个人中心')">
        <el-tabs v-model="activeTab" class="personal-tabs">
            <el-tab-pane name="profile" :label="$t('个人信息')">
                <y9Form v-if="config_personal.itemList.length" ref="personalY9FormRef" :config="config_personal">
                    <template #slotPersonalActions>
                        <el-button
                            :loading="isLoading"
                            :size="fontSizeObj.buttonSize"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            :type="editBtnText === '保存' ? 'danger' : undefined"
                            @click="onEdit"
                        >
                            {{ $t(editBtnText) }}
                        </el-button>
                        <el-button
                            v-show="editBtnText === '保存'"
                            :size="fontSizeObj.buttonSize"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            @click="onClose"
                        >
                            {{ $t('关闭') }}
                        </el-button>
                    </template>
                </y9Form>
            </el-tab-pane>
            <el-tab-pane name="password" :label="$t('修改密码')">
                <PasswordView v-if="activeTab === 'password'" embedded />
            </el-tab-pane>
        </el-tabs>
    </y9Card>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { computed, inject, reactive, ref } from 'vue';
    import y9_storage from '@/utils/storage';
    import { getCurrentManager, updateManager } from '@/api/manager/index';
    import { $validCheck } from '@/utils/validate';
    import { useSettingStore } from '@/store/modules/settingStore';
    import PasswordView from '@/views/password/index.vue';

    const settingStore = useSettingStore();
    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');

    // 默认显示个人信息，改密表单仅在对应页签激活时挂载，再次进入时重新加载密码要求。
    const activeTab = ref('profile');

    // 重新获取个人信息（登陆返回的个人信息有缺失的属性）
    const ssoUserInfo = y9_storage.getObjectItem('ssoUserInfo');

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
        const data = await getCurrentManager().catch((e) => e);

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
                    slotName: 'slotPersonalActions' //插槽名称
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
                type: 'text',
                label: computed(() => t('姓名')),
                prop: 'name',
                props: {
                    content: latestPersonalInfo.value.manager.name
                }
            },
            {
                type: 'text',
                label: computed(() => t('登录名称')),
                prop: 'loginName',
                props: {
                    content: latestPersonalInfo.value.manager.loginName
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
                    slotName: 'slotPersonalActions' //插槽名称
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
</script>

<style lang="scss" scoped>
    .personal-tabs {
        :deep(.el-tabs__header) {
            margin-bottom: 24px;
        }

        :deep(.el-tabs__item) {
            font-size: v-bind('fontSizeObj.baseFontSize');
        }
    }
</style>
