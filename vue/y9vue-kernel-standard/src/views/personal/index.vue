<template>
    <y9Card
        key="personal"
        :title="$t('个人信息')"
        v-if="config_personal.itemList.length"
        :showCloseButton="false"
    >
        <y9Form ref="personalY9FormRef" :config="config_personal">
            <template #slotAlterPwdButton>
                <el-button
                    v-if="showAlterPwdCompsRef"
                    type="danger"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    @click="showAlterPwdComps"
                    class="alter-pwd"
                >{{ $t('修改密码') }}</el-button>
                <el-button v-else @click="showAlterPwdComps"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                 class="alter-pwd">{{ $t('修改密码') }}</el-button>
                <el-button
                    v-if="editBtnText === '保存'"
                    type="danger"
                    @click="onEdit"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="alter-personInfo"
                    :loading="isLoading"
                >{{ $t(`${editBtnText}`) }}</el-button>
                <el-button
                    v-else
                    @click="onEdit"
                    class="alter-personInfo"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    :loading="isLoading"
                >{{ $t(`${editBtnText}`) }}</el-button>
                <el-button
                    v-show="editBtnText === '保存'"
                    @click="onClose"
                     :size="fontSizeObj.buttonSize"
                     :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="alter-personInfo"
                >{{ $t('关闭') }}</el-button>
            </template>
        </y9Form>
    </y9Card>

    <br />

    <y9Card
        v-model:show="showAlterPwdCompsRef"
        key="pwd"
        :title="$t('修改密码')"
        :showFooter="true"
        :footerBtnConfig="pwd_footerBtnConfig"
    >
        <y9Form ref="passwordY9FormRef" :config="config_pwd"></y9Form>
    </y9Card>
</template>

<script lang="ts" setup>
import { useI18n } from "vue-i18n"
import y9_storage from '@/utils/storage'
import { getManagerById, updateManager, checkPassword, modifyPassword } from '@/api/manager/index';
import { reactive } from "@vue/reactivity";
import { forIn } from "lodash-es";
import { nextTick, onBeforeMount, onMounted } from "@vue/runtime-core";
import { $validCheck } from "@/utils/validate"
import { useSettingStore } from "@/store/modules/settingStore"
import { inject, ref, watch } from "vue";
const settingStore = useSettingStore();
const { t } = useI18n();
// 注入 字体对象
const fontSizeObj: any = inject('sizeObjInfo');

// 重新获取个人信息（登陆返回的个人信息有缺失的属性）
const ssoUserInfo = y9_storage.getObjectItem("ssoUserInfo")

// 初始化
init()

// 编辑个人信息的button文本
const editBtnText = ref("编辑个人信息")
const personalY9FormRef = ref('')
const isLoading = ref(false)

function onClose() {
    editBtnText.value = "编辑个人信息"
    init()
}

// 个人信息配置 - 传入封装的y9Form组件
const config_personal = reactive({
    labelWidth: settingStore.device === 'mobile'?"130px":"236px",
    rules: {},
    itemList: []
})


const latestPersonalInfo = ref({})
// 不可编辑状态下
async function init() {
    console.log(ssoUserInfo.personId);
    const data = await getManagerById(ssoUserInfo.personId).catch(e => e)
    
    
    const { manager, nextCheckTime, nextModifyPwdTime } = data.data
    latestPersonalInfo.value = { manager, nextCheckTime, nextModifyPwdTime }

    const disabled_itemList = [
        {
            type: "text",
            label: computed(() => t("姓名")),
            prop: "name",
            props: {
                content: manager?.name,
            },
        },
        {
            type: "text",
            label: computed(() => t("登录名称")),
            prop: "loginName",
            props: {
                content: manager?.loginName,
            },
        },
        {
            type: "text",
            label: computed(() => t("性别")),
            prop: "sex",
            props: {
                content: manager?.sex == 1 ? computed(() => t("男")) : computed(() => t("女")),
            },
        },
        {
            type: "text",
            label: computed(() => t("电子邮件")),
            prop: "email",
            props: {
                content: manager?.email,
            },
        },
        {
            type: "text",
            label: computed(() => t("移动电话")),
            prop: "mobile",
            props: {
                content: manager?.mobile,
            },
        },
        {
            type: "text",
            label: computed(() => t("上次密码修改时间")),
            props: {
                content: nextCheckTime,
            },

        },
        {
            type: "text",
            label: computed(() => t("密码修改周期")),
            props: {
                content: computed(() => t("7天"))
            }
        },
        {
            type: "text",
            label: computed(() => t("下次修改密码时间")),
            props: {
                content: nextModifyPwdTime
            }
        },
        {
            type: "slot",//插槽类型
            labelWidth: settingStore.device === 'mobile'?'50px':'150px',
            props: {
                slotName: "slotAlterPwdButton",//插槽名称
            },
        }
    ]

    config_personal.itemList = disabled_itemList
}


// 手机号码格式校验
const mobileRex = (rule, value, callback) => {
    if (value != '' && value != null && !$validCheck("phone", value)) {
        return callback(new Error(computed(() => t("请输入正确电话或手机格式"))));
    }
    callback();
};

// 邮箱格式验证
const emailRex = (rule, value, callback) => {
    if (value != '' && value != null && !$validCheck("email", value)) {
        return callback(new Error(computed(() => t("请输入正确的邮箱格式"))));
    }

    // 如果这是必填字段需要对空值进行验证
    // if (value == '' || value == null) {
    //     return callback(new Error('邮箱数据为空'));
    // }
    callback();
}

// 个人信息表单验证规则
const personFormRules = {
    email: [{ validator: emailRex, trigger: 'blur' }],
    mobile: [{ validator: mobileRex, trigger: 'blur' }]
}

// 可编辑状态下
function initEdit() {
    let loginName_prependText = ''
    switch (ssoUserInfo.loginName) {
        case "systemManager":
            loginName_prependText = "sys_"
            break;
        case "systemManager":
            loginName_prependText = "audit_"
            break;
        case "systemManager":
            loginName_prependText = "security_"
            break;
        default:
            break;
    }

    // 设置默认值
    const model = {
        sex: latestPersonalInfo.value.manager?.sex == 1 ? "1" : "0",
        name: latestPersonalInfo.value.manager?.name,
        loginName: latestPersonalInfo.value.manager?.loginName,
        email: latestPersonalInfo.value.manager?.email,
        mobile: latestPersonalInfo.value.manager?.mobile
    }

    const edit_itemList = [
        {
            type: "input",
            label: computed(() => t("姓名")),
            prop: "name"
        },
        {
            type: "input",
            label: computed(() => t("登录名称")),
            prop: "loginName",
            props: {
                prependText: loginName_prependText,
            }
        },
        {
            type: "radio",
            label: computed(() => t("性别")),
            prop: "sex",
            props: {
                radioType: "radio",
                options: [
                    {
                        label: computed(() => t("男")),
                        value: "1"
                    },
                    {
                        label: computed(() => t("女")),
                        value: "0"
                    },
                ],
            }
        },
        {
            type: "input",
            prop: "email",
            label: computed(() => t("电子邮件"))
        },
        {
            type: "input",
            prop: "mobile",
            label: computed(() => t("移动电话"))
        },
        {
            type: "text",
            label: computed(() => t("上次密码修改时间")),
            props: {
                content: "2022-06-15 11:05:06",
            },

        },
        {
            type: "text",
            label: computed(() => t("密码修改周期")),
            props: {
                content: computed(() => t("7天"))
            }
        },
        {
            type: "text",
            label: computed(() => t("下次修改密码时间")),
            props: {
                content: `2022-06-22 11:05:06 ${computed(() => t("之前"))}`
            }
        },
        {
            type: "slot",//插槽类型,
            labelWidth: settingStore.device === 'mobile'?'50px':'150px',
            props: {
                slotName: "slotAlterPwdButton",//插槽名称
            },
        }
    ]
    config_personal.model = model
    config_personal.itemList = edit_itemList
    config_personal.rules = personFormRules
}


// 提交表单
function onSubmitPersonForm() {
    isLoading.value = true
    // 接口可提交的所有字段
    const personalForm = reactive({
        id: ssoUserInfo.personId,
        parentId: ssoUserInfo.parentId,
        tenantId: ssoUserInfo.tenantId,
        name: "",
        loginName: "",
        sex: "",
        email: "",
        mobile: "",
        pass1: "",
        managerLevel: ssoUserInfo.managerLevel
    });
    // 合并已填写的字段
    let formData = { ...personalForm, ...personalY9FormRef.value.model }
    // 删除不知怎么来的 undefined 属性
    for (let key in formData) {
        if (key === "undefined") {
            delete formData.undefined
        }
    }
    return new Promise((resolve, reject) => {
        const y9FormInstance = personalY9FormRef.value.elFormRef
        y9FormInstance.validate(async valid => {
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
                        isLoading.value = false
                    }, 3000)
                } else {
                    // 接口状态正确，立即关闭loading动画并重新初始化数据
                    init()
                    editBtnText.value = "编辑个人信息"
                    isLoading.value = false
                }
                resolve()
            } else {
                ElMessage({
                    type: 'error',
                    message: t('验证不通过，请检查'),
                    offset: 65
                });
                reject()
            }
        })

    })
}

function onEdit() {
    switch (editBtnText.value) {
        case "编辑个人信息":
            editBtnText.value = "保存"
            initEdit()
            break;
        case "保存":
            // 保存接口
            onSubmitPersonForm()
            break;

        default:
            break;
    }


}

// 获取密码表单实例
const passwordY9FormRef = ref('')
// 密码修改表单的接口字段
const passWordForm = reactive({
    id: ssoUserInfo.personId,
    oldPassword: '',
    newPassword: '',
});

// 当前密码验证
const oldPasswordRex = async (rule, value, callback) => {
    if (!value) {
        return callback(new Error(computed(() => t("当前密码不能为空"))));
    }
    let check = await checkPassword(passWordForm.id, value)
    if (!check.data) {
        return callback(new Error(computed(() => t("当前密码与原密码不一致！"))));
    }
    passWordForm.oldPassword = value
    // 验证通过
    callback()
}

// 新密码验证-1
const newPasswordRex = (rule, value, callback) => {
    if (!value) {
        return callback(new Error(computed(() => t("新密码不能为空"))));
    }
    // 验证与当前密码是否相同
    if (value && value === passWordForm.oldPassword) {
        return callback(new Error(computed(() => t("新密码不能和当前密码相同"))));
    }
    // 验证是否符合密码规则 - 长度
    if (value.length < 10) {
        return callback(new Error(computed(() => t("新密码不符合验证规则，长度必须大于或等于10"))));
    }
    // 验证是否符合密码规则 - 数字
    if (!(/[0-9]+/.test(value))) {
        return callback(new Error(computed(() => t("新密码不符合验证规则，必须包含”数字“"))));
    }
    // 验证是否符合密码规则 - 小写字母
    if (!(/[a-z]+/.test(value))) {
        return callback(new Error(computed(() => t("新密码不符合验证规则，必须包含”小写字母“"))));
    }
    // 验证是否符合密码规则 - 大写字母
    if (!(/[A-Z]+/.test(value))) {
        return callback(new Error(computed(() => t("新密码不符合验证规则，必须包含”大写字母“"))));
    }
    // 验证通过
    passWordForm.newPassword = value
    callback()
}
// 新密码验证-2
const checknewpasswordRex = (rule, value, callback) => {
    if (!value) {
        return callback(new Error(computed(() => t("请再次输入新密码确认"))));
    }
    // 验证是否与输入的新密码一致
    if (value && passWordForm.newPassword !== value) {
        return callback(new Error(computed(() => t("与前一次输入的新密码不一致，请检查或重新输入"))));
    }
    // 验证通过
    callback()
}

// 密码表单验证规则
const passwordFormRules = {
    oldPassword: { required: true, validator: oldPasswordRex, trigger: 'blur' },
    newPassword: { required: true, validator: newPasswordRex, trigger: 'blur' },
    checknewpassword: { required: true, validator: checknewpasswordRex, trigger: 'blur' }
}


// 修改密码
const config_pwd = reactive({
    rules: passwordFormRules,
    labelWidth: settingStore.device === 'mobile'? "100px":"150px",
    itemList: [
        {
            type: "text",
            props: {
                content: computed(() => t("新密码必须包含”数字“、”大写字母“、”小写字母“，长度大于或等于10"))
            }

        },
        {
            type: "input",
            prop: "oldPassword",
            label: computed(() => t("当前密码"))
        },
        {
            type: "input",
            prop: "newPassword",
            label: computed(() => t("新密码"))
        },
        {
            type: "input",
            prop: "checknewpassword",
            label: computed(() => t("确认密码"))
        },
    ]
})


const pwd_footerBtnConfig = reactive({
    okLoading: false,
    okText: computed(() => t("确定")),//确定按钮名称，传空或者false不显示按钮
    okDisabled: false,//确定按钮禁用状态
    closeText: computed(() => t("关闭")),//关闭按钮名称，传空或者false不显示按钮
    closeDisabled: false,//关闭按钮禁用状态
    resetText: "",// 重置按钮名称，传空或者false不显示按钮
    resetDisabled: false,//重置按钮禁用状态
    onOk: onSavePwd, // 确定按钮回调，支持promise，resolve()关闭对话框同时关闭onOkLoading，rejected()只关闭onOkLoading

})


// 显示修改密码组件
const showAlterPwdCompsRef = ref(false)
function showAlterPwdComps() {
    showAlterPwdCompsRef.value = true
}


// 密码修改 - 确定按钮
function onSavePwd() {
    pwd_footerBtnConfig.okLoading = true

    const y9FormPasswordInstance = passwordY9FormRef.value.elFormRef
    return new Promise((resolve, reject) => {
        y9FormPasswordInstance.validate(async valid => {
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
                if (res.code != 200) {
                    // 接口状态错误，三秒后关闭loading动画
                    setTimeout(() => {
                        pwd_footerBtnConfig.okLoading = false
                    }, 3000)
                } else {
                    // 接口状态正确，立即关闭loading动画并重新初始化数据
                    pwd_footerBtnConfig.okLoading = false
                }
                resolve()
            } else {
                pwd_footerBtnConfig.okLoading = false
                ElMessage({
                    type: 'error',
                    message: t('验证不通过，请检查'),
                    offset: 65
                });
                reject()
            }
        })

    })
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