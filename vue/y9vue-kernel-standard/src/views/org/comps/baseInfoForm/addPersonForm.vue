<template>
    <div v-show="step === 1" v-loading="loading" class="person-form-div">
        <div class="person-form-title-base">{{ $t('基本信息') }}</div>
        <y9Form ref="y9FormBaseRef" :config="y9FormBaseConfig"></y9Form>
        <div class="person-form-title-person">{{ $t('个人信息') }}</div>
        <y9Form ref="y9FormPersonRef" :config="y9FormPersonConfig"></y9Form>
    </div>
    <div v-show="step === 2" class="step2-div">
        <div class="table-item">
            <div class="text">{{ $t('选择已有岗位') }}</div>
            <y9Table v-model:selectedVal="tableForm.positionIds" :config="positionListTableConfig"></y9Table>
        </div>
        <div class="table-item">
            <div class="text">{{ $t('通过选择职位新增岗位') }}</div>
            <y9Table v-model:selectedVal="tableForm.jobIds" :config="jobListTableConfig"></y9Table>
        </div>
    </div>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { computed, inject, onMounted, ref } from 'vue';
    import { $validCheck } from '@/utils/validate';
    import { $dictionary, $dictionaryFunc } from '@/utils/data';
    import { loginNameCheck } from '@/api/person/index';
    import { getJobList, listByType } from '@/api/dictionary/index';
    import { getPositionsByParentId } from '@/api/position/index';
    import { useSettingStore } from '@/store/modules/settingStore';

    const { t } = useI18n();
    const settingStore = useSettingStore();
    // 注入 字体变量
    const fontSizeObj: any = inject('sizeObjInfo');
    const props = defineProps({
        currInfo: {
            //当前信息
            type: Object,
            default: () => {
                return {};
            }
        },

        step: {
            //步骤
            type: Number
        }
    });

    const loading = ref(false);

    //移动电话号码格式校验规则
    const mobileValidator = (rule, value, callback) => {
        let result = $validCheck('phone', value, true);
        if (!result.valid) {
            callback(new Error(t(result.msg)));
        } else {
            callback();
        }
    };
    //是否存在登录名的校验规则
    const hasLoginNameValidator = async (rule, value, callback) => {
        let result = await loginNameCheck('', value);
        if (!result.data) {
            callback(new Error(t('该登录名称已存在，请重新输入登录名称')));
        } else {
            callback();
        }
    };

    //基本信息表单配置
    const y9FormBaseConfig = ref({
        descriptionsFormConfig: {
            //描述表单配置
            column: settingStore.device === 'mobile' ? 1 : 3,
            labelAlign: 'center',
            labelWidth: '150px',
            contentWidth: '200px'
        },
        model: {}, //表单属性
        rules: {
            //表单校验规则
            name: [{ required: true, message: computed(() => t('请输入人员名称')), trigger: 'blur' }],
            loginName: [
                { required: true, message: computed(() => t('请输入登录名称')), trigger: 'blur' },
                { validator: hasLoginNameValidator, trigger: 'blur' }
            ],
            mobile: [
                { required: true, message: computed(() => t('请输入移动电话')), trigger: 'blur' },
                { validator: mobileValidator, trigger: 'blur' }
            ]
        },
        itemList: [
            //表单显示列表
            {
                type: 'input',
                prop: 'name',
                label: computed(() => t('人员名称'))
            },
            {
                type: 'input',
                prop: 'loginName',
                label: computed(() => t('登录名称'))
            },
            {
                type: 'input',
                prop: 'customId',
                label: computed(() => t('自定义ID'))
            },
            {
                type: 'input',
                prop: 'caid',
                label: computed(() => t('CA认证码'))
            },
            {
                type: 'select',
                prop: 'sex',
                label: computed(() => t('性别')),
                props: {
                    options: []
                }
            },
            {
                type: 'select',
                prop: 'official',
                label: computed(() => t('是否在编')),
                props: {
                    options: []
                }
            },
            {
                type: 'input',
                prop: 'officeAddress',
                label: computed(() => t('办公室'))
            },
            {
                type: 'input',
                prop: 'officePhone',
                label: computed(() => t('办公电话'))
            },
            {
                type: 'input',
                prop: 'officeFax',
                label: computed(() => t('办公传真'))
            },
            {
                type: 'select',
                prop: 'disabled',
                label: computed(() => t('是否禁用')),
                props: {
                    options: []
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
            }
        ]
    });
    //个人信息表单配置
    const y9FormPersonConfig = ref({
        descriptionsFormConfig: {
            //描述表单配置
            column: settingStore.device === 'mobile' ? 1 : 3,
            labelAlign: 'center',
            labelWidth: '150px',
            contentWidth: '200px'
        },
        model: {} as any, //表单属性
        rules: {}, //表单验证规则
        itemList: [
            //表单显示列表
            {
                type: 'select',
                prop: 'maritalStatus',
                label: computed(() => t('婚姻状况')),
                props: {
                    options: []
                }
            },
            {
                type: 'input',
                prop: 'professional',
                label: computed(() => t('专业'))
            },
            {
                type: 'input',
                prop: 'city',
                label: computed(() => t('居住城市'))
            },
            {
                type: 'input',
                prop: 'country',
                label: computed(() => t('居住国家'))
            },
            {
                type: 'input',
                prop: 'homePhone',
                label: computed(() => t('家庭电话'))
            },
            {
                type: 'select',
                prop: 'idType',
                label: computed(() => t('证件类型')),
                props: {
                    options: []
                }
            },
            {
                type: 'input',
                prop: 'idNum',
                label: computed(() => t('证件号码'))
            },
            {
                type: 'select',
                prop: 'politicalStatus',
                label: computed(() => t('政治面貌')),
                props: {
                    options: []
                }
            },
            {
                type: 'date',
                prop: 'workTime',
                label: computed(() => t('入职日期')),
                props: {
                    dataType: 'date',
                    format: 'YYYY-MM-DD',
                    valueFormat: 'YYYY-MM-DD',
                    options: []
                }
            },
            {
                type: 'select',
                prop: 'education',
                label: computed(() => t('学历')),
                props: {
                    options: []
                }
            },
            {
                type: 'input',
                prop: 'province',
                label: computed(() => t('人员籍贯'))
            },
            {
                type: 'date',
                prop: 'birthday',
                label: computed(() => t('出生日期')),
                props: {
                    dataType: 'date',
                    format: 'YYYY-MM-DD',
                    valueFormat: 'YYYY-MM-DD',
                    options: []
                }
            },
            {
                type: 'input',
                prop: 'homeAddress',
                label: computed(() => t('家庭住址')),
                span: 3
            },
            {
                type: 'textarea',
                prop: 'description',
                label: computed(() => t('人员描述')),
                span: 3
            }
        ]
    });

    let jobListTableConfig = ref({
        //职位列表的表格配置
        columns: [
            {
                type: 'selection',
                width: 100,
                selectable: () => {
                    return true;
                }
            },
            {
                title: computed(() => t('职位')),
                key: 'name',
                width: 200
            }
        ],
        tableData: [],
        pageConfig: false //取消分页
    });

    let positionListTableConfig = ref({
        //岗位列表的表格配置
        columns: [
            {
                type: 'selection',
                width: 100,
                selectable: (row, index) => {
                    if (row.headCount < row.capacity) {
                        return true;
                    } else {
                        return false;
                    }
                }
            },
            {
                title: computed(() => t('岗位')),
                key: 'name',
                width: 200
            },
            {
                title: computed(() => t('占用岗位')),
                key: 'headCount',
                width: 100
            },
            {
                title: computed(() => t('岗位容量')),
                key: 'capacity',
                width: 100
            }
        ],
        tableData: [],
        pageConfig: false //取消分页
    });

    const tableForm = ref({
        positionIds: [],
        jobIds: []
    });

    onMounted(async () => {
        loading.value = true;
        await $dictionaryFunc('dutyLevel', listByType, 'dutyLevel'); //请求职级
        await $dictionaryFunc('duty', listByType, 'duty'); //请求职务
        await $dictionaryFunc('officialType', listByType, 'officialType'); //请求编制类型
        await $dictionaryFunc('principalIDType', listByType, 'principalIDType'); //请求证件类型
        await $dictionaryFunc('positionList', getPositionsByParentId, props.currInfo.id); //获取岗位列表
        await $dictionaryFunc('jobList', getJobList); //获取职位列表

        for (let i = 0; i < y9FormBaseConfig.value.itemList.length; i++) {
            const item = y9FormBaseConfig.value.itemList[i];

            if (item.prop === 'sex') {
                item.props.options = $dictionary().sex?.map((item) => {
                    return {
                        label: computed(() => t(item.name)),
                        value: item.id
                    };
                });
            } else if (item.prop === 'official') {
                item.props.options = $dictionary().booleanNum?.map((item) => {
                    return {
                        label: computed(() => t(item.name)),
                        value: item.id
                    };
                });
            } else if (item.prop === 'disabled') {
                item.props.options = $dictionary().boolean?.map((item) => {
                    return {
                        label: computed(() => t(item.name)),
                        value: item.id
                    };
                });
            }
        }

        y9FormBaseConfig.value.model = { sex: 1, official: 1 };
        y9FormPersonConfig.value.model = { maritalStatus: '0' };

        for (let i = 0; i < y9FormPersonConfig.value.itemList.length; i++) {
            const item = y9FormPersonConfig.value.itemList[i];

            if (item.prop === 'maritalStatus') {
                item.props.options = $dictionary().maritalStatus?.map((item) => {
                    return {
                        label: computed(() => t(item.name)),
                        value: item.id
                    };
                });
            } else if (item.prop === 'idType') {
                item.props.options = $dictionary().principalIDType?.map((item) => {
                    return {
                        label: computed(() => t(item.name)),
                        value: item.id
                    };
                });
            } else if (item.prop === 'politicalStatus') {
                item.props.options = $dictionary().politicalStatus?.map((item) => {
                    return {
                        label: computed(() => t(item.name)),
                        value: item.id
                    };
                });
            } else if (item.prop === 'education') {
                item.props.options = $dictionary().education?.map((item) => {
                    return {
                        label: computed(() => t(item.name)),
                        value: item.id
                    };
                });
            }
        }

        jobListTableConfig.value.tableData = $dictionary().jobList;
        positionListTableConfig.value.tableData = $dictionary().positionList;
        loading.value = false;
    });

    const y9FormBaseRef = ref();

    function validForm() {
        return y9FormBaseRef.value.elFormRef?.validate((valid) => valid);
    }

    const y9FormPersonRef = ref();
    const personForm = computed(() => {
        return Object.assign(
            { parentId: props.currInfo.id },
            y9FormBaseRef.value?.model,
            y9FormPersonRef.value?.model,
            tableForm.value
        );
    });

    defineExpose({
        personForm,
        validForm
    });
</script>

<style lang="scss" scoped>
    .person-form-div {
        display: flex;
        flex-direction: column;

        .person-form-title-base {
            color: var(--el-text-color-primary);
            text-align: center;
            line-height: 40px;
            border-top: 1px solid var(--el-border-color-lighter);
            border-left: 1px solid var(--el-border-color-lighter);
            border-right: 1px solid var(--el-border-color-lighter);
        }

        .person-form-title-person {
            color: var(--el-text-color-primary);
            text-align: center;
            line-height: 40px;
            border-left: 1px solid var(--el-border-color-lighter);
            border-right: 1px solid var(--el-border-color-lighter);
        }
    }

    .step2-div {
        display: flex;
        justify-content: space-around;
        padding: 20px 50px;

        .table-item {
            border: solid 1px #eee;
            border-radius: 4px;
            margin: 0 20px;
            padding: 20px 40px;
            box-shadow: 2px 2px 2px 1px rgba(0, 0, 0, 0.06);

            .text {
                color: #666;
                margin-bottom: 10px;
            }
        }
    }
</style>
