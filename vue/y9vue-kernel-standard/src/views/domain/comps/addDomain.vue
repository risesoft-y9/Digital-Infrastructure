<!--
 * @Descripttion: 子域三员列表
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-06-28 10:03:00
 * @LastEditors: mengjuhua
 * @LastEditTime: 2024-01-12 10:51:59
-->
<template>
    <y9Card :title="`${currInfo.name ? currInfo.name : ''}`">
        <div class="add-action">
            <el-button
                v-show="showSysBnt === true"
                :disabled="disabled1"
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                type="primary"
                @click="onAddTreeManage(1, '系统管理员', '')"
            >
                <i class="ri-add-line"></i>
                <span>{{ $t('系统管理员') }}</span>
            </el-button>
            <el-button
                v-show="showSysBnt === true"
                :disabled="disabled2"
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                type="primary"
                @click="onAddTreeManage(2, '安全保密员', '')"
            >
                <i class="ri-add-line"></i>
                <span>{{ $t('安全保密员') }}</span>
            </el-button>
            <el-button
                v-show="showSysBnt === true"
                :disabled="disabled3"
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                type="primary"
                @click="onAddTreeManage(3, '安全审计员', '')"
            >
                <i class="ri-add-line"></i>
                <span>{{ $t('安全审计员') }}</span>
            </el-button>
        </div>
        <y9Table :config="tableConfig"></y9Table>
    </y9Card>

    <y9Dialog v-model:config="dialogConfig">
        <y9Form ref="y9FormRef" :config="formConfig"></y9Form>
    </y9Dialog>
    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import { computed, h, inject, onMounted, reactive, ref, toRefs, watch } from 'vue';
    import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
    import { $deepAssignObject } from '@/utils/object';
    import {
        changeDisabled,
        delManager,
        getManagerById,
        getManagersByParentId,
        loginNameCheck,
        saveOrUpdate
    } from '@/api/deptManager/index';
    import { useI18n } from 'vue-i18n';
    import { useSettingStore } from '@/store/modules/settingStore';

    const settingStore = useSettingStore();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');

    const { t } = useI18n();

    const y9UserInfo = JSON.parse(sessionStorage.getItem('ssoUserInfo'));
    let y9FormRef = ref();
    const props = defineProps({
        currTreeNodeInfo: {
            //当前tree节点信息
            type: Object,
            default: () => {
                return {};
            }
        }
    });

    const checkLoginName = (rule, value, callback) => {
        if (!value) {
            return callback(new Error(t('请输入登录名称')));
        }
        let prefix = formConfig.value.itemList[1].props.prependText;
        setTimeout(() => {
            loginNameCheck(formConfig.value.model.id, prefix + value).then((res) => {
                if (!res.data) {
                    callback(new Error(t('该登录名称已存在，请重新输入登录名称')));
                } else {
                    callback();
                }
            });
        }, 500);
    };

    const data = reactive({
        //当前节点信息
        currInfo: props.currTreeNodeInfo,
        loading: false,
        //表格配置
        tableConfig: {
            columns: [
                {
                    title: computed(() => t('序号')),
                    type: 'index',
                    width: '100px',
                    fixed: 'left'
                },
                {
                    title: computed(() => t('名称')),
                    key: 'name'
                },
                {
                    title: computed(() => t('登录名称')),
                    key: 'loginName'
                },
                {
                    title: computed(() => t('移动电话')),
                    key: 'mobile'
                },
                {
                    title: computed(() => t('人员类型')),
                    key: 'userType',
                    width: 150
                },
                {
                    title: computed(() => t('操作')),
                    width: settingStore.getTwoBtnWidth,
                    fixed: 'right',
                    showOverflowTooltip: false,
                    render: (row) => {
                        let button = [
                            h(
                                'span',
                                {
                                    style: {
                                        marginRight: '10px',
                                        display: 'inline-flex',
                                        alignItems: 'center'
                                    },
                                    onClick: () => {
                                        onAddTreeManage(row.managerLevel, row.userType, row.id);
                                    }
                                },
                                [
                                    h('i', {
                                        class: 'ri-edit-line',
                                        style: {
                                            marginRight: '4px'
                                        }
                                    }),
                                    h('span', t('编辑'))
                                ]
                            ),
                            h(
                                'span',
                                {
                                    style: {
                                        display: 'inline-flex',
                                        alignItems: 'center'
                                    },
                                    onClick: () => {
                                        ElMessageBox.confirm(`${t('是否删除')}【${row.name}】?`, t('提示'), {
                                            confirmButtonText: t('确定'),
                                            cancelButtonText: t('取消'),
                                            type: 'info'
                                        })
                                            .then(async () => {
                                                loading.value = true;
                                                let res = await delManager([row.id].toString());
                                                ElNotification({
                                                    title: res.success ? t('成功') : t('失败'),
                                                    message: res.msg,
                                                    type: res.success ? 'success' : 'error',
                                                    duration: 2000,
                                                    offset: 80
                                                });
                                                if (res.success) {
                                                    getManagersList();
                                                }
                                                loading.value = false;
                                            })
                                            .catch(() => {
                                                ElMessage({
                                                    type: 'info',
                                                    message: t('已取消删除'),
                                                    offset: 65
                                                });
                                            });
                                    }
                                },
                                [
                                    h('i', {
                                        class: 'ri-delete-bin-line',
                                        style: {
                                            marginRight: '4px'
                                        }
                                    }),
                                    h('span', t('删除'))
                                ]
                            )
                        ];
                        if (!showSysBnt.value) {
                            if (row.disabled) {
                                button = [
                                    h(
                                        'span',
                                        {
                                            style: {
                                                marginRight: '10px',
                                                display: 'inline-flex',
                                                alignItems: 'center'
                                            },
                                            onClick: () => {
                                                ElMessageBox.confirm(`${t('是否启用')}【${row.name}】?`, t('提示'), {
                                                    confirmButtonText: t('确定'),
                                                    cancelButtonText: t('取消'),
                                                    type: 'info'
                                                })
                                                    .then(async () => {
                                                        loading.value = true;
                                                        let res = await changeDisabled(row.id);
                                                        ElNotification({
                                                            title: res.success ? t('成功') : t('失败'),
                                                            message: res.success ? t('启用成功') : res.msg,
                                                            type: res.success ? 'success' : 'error',
                                                            duration: 2000,
                                                            offset: 80
                                                        });
                                                        if (res.success) {
                                                            getManagersList();
                                                        }
                                                        loading.value = false;
                                                    })
                                                    .catch(() => {
                                                        ElMessage({
                                                            type: 'info',
                                                            message: t('已取消启用'),
                                                            offset: 65
                                                        });
                                                    });
                                            }
                                        },
                                        [
                                            h('i', {
                                                class: 'ri-user-follow-line',
                                                style: {
                                                    marginRight: '4px'
                                                }
                                            }),
                                            h('span', t('启用'))
                                        ]
                                    )
                                ];
                            } else {
                                button = [
                                    h(
                                        'span',
                                        {
                                            style: {
                                                marginRight: '10px',
                                                display: 'inline-flex',
                                                alignItems: 'center'
                                            },
                                            onClick: () => {
                                                ElMessageBox.confirm(`${t('是否禁用')}【${row.name}】?`, t('提示'), {
                                                    confirmButtonText: t('确定'),
                                                    cancelButtonText: t('取消'),
                                                    type: 'info'
                                                })
                                                    .then(async () => {
                                                        loading.value = true;
                                                        let res = await changeDisabled(row.id);
                                                        ElNotification({
                                                            title: res.success ? t('成功') : t('失败'),
                                                            message: res.success ? t('禁用成功') : res.msg,
                                                            type: res.success ? 'success' : 'error',
                                                            duration: 2000,
                                                            offset: 80
                                                        });
                                                        if (res.success) {
                                                            getManagersList();
                                                        }

                                                        loading.value = false;
                                                    })
                                                    .catch(() => {
                                                        ElMessage({
                                                            type: 'info',
                                                            message: t('已取消禁用'),
                                                            offset: 65
                                                        });
                                                    });
                                            }
                                        },
                                        [
                                            h('i', {
                                                class: 'ri-user-unfollow-line',
                                                style: {
                                                    marginRight: '4px'
                                                }
                                            }),
                                            h('span', t('禁用'))
                                        ]
                                    )
                                ];
                            }
                        } else {
                            if (!row.disabled) {
                                button = [
                                    h(
                                        'span',
                                        {
                                            style: {
                                                marginRight: '10px',
                                                display: 'inline-flex',
                                                alignItems: 'center'
                                            },
                                            onClick: async () => {
                                                let res = await getManagerById(row.id);
                                                formConfig.value.model = res.data;
                                                formConfig.value.model.loginName = res.data.loginName.slice(4);

                                                formConfig.value.itemList.forEach((item) => {
                                                    if (item.prop == 'loginName') {
                                                        switch (row.managerLevel) {
                                                            case 1:
                                                                item.props.prependText = 'sys_';
                                                                break;
                                                            case 2:
                                                                item.props.prependText = 'sec_';
                                                                break;
                                                            case 3:
                                                                item.props.prependText = 'aud_';
                                                                break;
                                                            default:
                                                                item.props.prependText = '';
                                                        }
                                                    }
                                                });

                                                Object.assign(dialogConfig.value, {
                                                    show: true,
                                                    title: computed(() => t(`查看${row.userType}`)),
                                                    type: row.managerLevel,
                                                    showFooter: false
                                                });
                                            }
                                        },
                                        [
                                            h('i', {
                                                class: 'ri-contacts-line',
                                                style: {
                                                    marginRight: '4px'
                                                }
                                            }),
                                            h('span', t('查看'))
                                        ]
                                    )
                                ];
                            }
                        }
                        return button;
                    }
                }
            ],
            tableData: [],
            pageConfig: false, //取消分页
            loading: false
        },
        //弹窗配置
        dialogConfig: {
            width: '32%',
            show: false,
            title: '',
            resetText: t('重置'),
            onOk: (newConfig) => {
                return new Promise((resolve, reject) => {
                    const y9FormInstance = y9FormRef.value.elFormRef;
                    y9FormInstance.validate(async (valid) => {
                        if (valid) {
                            let data = y9FormRef.value.model;
                            let prefix = formConfig.value.itemList[1].props.prependText;
                            data.loginName = prefix + data.loginName;
                            // data
                            let res = await saveOrUpdate(data);
                            ElNotification({
                                title: res.success ? t('成功') : t('失败'),
                                message: res.msg,
                                type: res.success ? 'success' : 'error',
                                duration: 2000,
                                offset: 80
                            });
                            if (res.success) {
                                getManagersList();
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
            },
            onReset: (newConfig) => {
                y9FormRef.value.elFormRef.resetFields();
            }
        },

        formConfig: {
            //表单配置
            model: {
                id: '',
                sex: 1,
                orgType: 'Manager',
                parentId: props.currTreeNodeInfo.id
            },
            rules: {
                //	表单验证规则。类型：FormRules
                name: [{ required: true, message: computed(() => t('请输入姓名')), trigger: 'blur' }],
                loginName: [{ required: true, validator: checkLoginName, trigger: 'blur' }]
            },
            itemList: [
                {
                    type: 'input',
                    label: computed(() => t('人员名称')),
                    prop: 'name'
                },
                {
                    type: 'input',
                    label: computed(() => t('登录名称')),
                    prop: 'loginName',
                    props: {
                        prependText: ''
                    }
                },
                {
                    type: 'input',
                    label: computed(() => t('电子邮件')),
                    prop: 'email'
                },
                {
                    type: 'input',
                    label: computed(() => t('移动电话')),
                    prop: 'mobile'
                },
                {
                    type: 'textarea',
                    label: computed(() => t('人员描述')),
                    prop: 'description'
                }
            ]
        },
        disabled1: false,
        disabled2: false,
        disabled3: false
    });

    let { currInfo, tableConfig, dialogConfig, formConfig, loading, disabled1, disabled2, disabled3 } = toRefs(data);

    let showSysBnt = ref(true);
    watch(
        () => props.currTreeNodeInfo,
        (newVal) => {
            currInfo.value = $deepAssignObject(currInfo.value, newVal);
            getManagersList();
        }
    );

    onMounted(() => {
        getManagersList();

        if (y9UserInfo.managerLevel === 1) {
            showSysBnt.value = true;
        } else {
            showSysBnt.value = false;
        }
    });

    async function getManagersList() {
        tableConfig.value.loading = true;

        let result = await getManagersByParentId(currInfo.value.id);
        if (result.success) {
            disabled1.value = false;
            disabled2.value = false;
            disabled3.value = false;
            result.data.forEach((element) => {
                if (element.managerLevel == 1) {
                    element.userType = '系统管理员';
                    disabled1.value = true;
                } else if (element.managerLevel == 2) {
                    element.userType = '安全保密员';
                    disabled2.value = true;
                } else if (element.managerLevel == 3) {
                    element.userType = '安全审计员';
                    disabled3.value = true;
                }
            });
            tableConfig.value.tableData = result.data;
        }

        tableConfig.value.loading = false;
    }

    async function onAddTreeManage(type, title, id) {
        if (id != '') {
            let res = await getManagerById(id);
            formConfig.value.model = res.data;
            formConfig.value.model.loginName = res.data.loginName.slice(4);
        } else {
            formConfig.value.model = {};
            formConfig.value.model.id = '';
            formConfig.value.model.sex = 1;
            formConfig.value.model.orgType = 'Manager';
            formConfig.value.model.parentId = props.currTreeNodeInfo.id;
            formConfig.value.model.managerLevel = type;
        }
        formConfig.value.itemList.forEach((item) => {
            if (item.prop == 'loginName') {
                switch (type) {
                    case 1:
                        item.props.prependText = 'sys_';
                        break;
                    case 2:
                        item.props.prependText = 'sec_';
                        break;
                    case 3:
                        item.props.prependText = 'aud_';
                        break;
                    default:
                        item.props.prependText = '';
                }
            }
        });

        Object.assign(dialogConfig.value, {
            show: true,
            title: id == '' ? computed(() => t(`新增${title}`)) : computed(() => t(`编辑${title}`)),
            type: type,
            showFooter: true
        });
    }
</script>

<style lang="scss" scoped>
    :deep(.add-action) {
        margin-bottom: 10px;

        button {
            margin-bottom: 10px;
            margin-left: 0;
            margin-right: 10px;
        }
    }
</style>
