<!--
 * @Author: fuyu
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2023-12-26 11:23:34
 * @Description: 组织架构-职位管理
-->
<template>
    <div>
        <y9Table
            v-model:selectedVal="tableCurrSelectedVal"
            :config="tableConfig"
            :filterConfig="filterConfig"
            @on-current-change="onCurrentChange"
        >
            <template v-slot:slotSearch>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-third"
                    @click="getJobByName"
                >
                    <i class="ri-search-line"></i>
                    {{ $t('搜索') }}
                </el-button>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-third"
                    type="primary"
                    @click="getDictionaryData"
                >
                    <i class="ri-restart-line"></i>
                    <span>{{ $t('刷新') }}</span>
                </el-button>
            </template>

            <template #addDictionaryData>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-main"
                    type="primary"
                    @click="onAddDictionaryData"
                >
                    <i class="ri-add-line"></i>
                    <span>{{ $t('职位') }}</span>
                </el-button>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-third"
                    type="primary"
                    @click="upJob"
                >
                    <i class="ri-arrow-up-line"></i>
                    <span>{{ $t('上移') }}</span>
                </el-button>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-third"
                    type="primary"
                    @click="downJob"
                >
                    <i class="ri-arrow-down-line"></i>
                    <span>{{ $t('下移') }}</span>
                </el-button>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-third"
                    type="primary"
                    @click="saveJobOrder"
                >
                    <i class="ri-save-line"></i>
                    <span>{{ $t('保存') }}</span>
                </el-button>
            </template>

            <template #name="{ row, column, index }">
                <el-input v-if="editId === index" v-model="formData.name" />
                <template v-else>{{ row.name }}</template>
            </template>
            <template #code="{ row, column, index }">
                <input autocomplete="new-password" hidden type="password" />
                <el-input v-if="editId === index" v-model="formData.code" />
                <template v-else>{{ row.code }}</template>
            </template>
        </y9Table>

        <y9Dialog v-model:config="addDialogConfig">
            <template v-slot>
                <y9Form ref="ruleFormRef" :config="ruleFormConfig"></y9Form>
            </template>
        </y9Dialog>
    </div>
    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { computed, h, inject, onMounted, reactive, ref, toRefs, watch } from 'vue';
    import { ElMessage, ElMessageBox, ElNotification, FormInstance } from 'element-plus';
    import { $keyNameAssign } from '@/utils/object';
    import {
        deleteByIds,
        getJobByMajorName,
        getJobList,
        jobInfoGet,
        saveJobValue,
        saveOrder
    } from '@/api/dictionary/index';
    import { useSettingStore } from '@/store/modules/settingStore';

    const { t } = useI18n();
    const settingStore = useSettingStore();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    // 应用 添加 修改表单ref
    const ruleFormRef = ref<FormInstance>();

    // const ruleForm = ref({
    //     code: '',
    //     name: '',
    // });
    const data = reactive({
        tableCurrSelectedVal: [], //表格选择的数据
        formData: {
            name: '',
            code: ''
            // tabIndex:"",
        },
        loading: false, // 全局loading
        editId: -1, //编辑id
        currFilters: {}, //当前选择的过滤数据
        tableConfig: {
            //表格配置
            loading: false,
            border: false,
            headerBackground: true,
            columns: [
                {
                    type: 'radio',
                    title: computed(() => t('请选择')),
                    width: 200
                },
                {
                    title: computed(() => t('职位名称')),
                    key: 'name',
                    slot: 'name',
                    showOverflowTooltip: false
                },
                {
                    title: computed(() => t('数据代码')),
                    key: 'code',
                    slot: 'code',
                    showOverflowTooltip: false
                },
                {
                    title: computed(() => t('操作')),
                    width: settingStore.getThreeBtnWidth,
                    fixed: 'right',

                    render: (row, params) => {
                        let editActions = [
                            h('span', {
                                style: {
                                    display: 'inline-flex',
                                    alignItems: 'center'
                                },
                                onClick: () => {
                                    editId.value = params.$index;
                                    $keyNameAssign(formData.value, row);
                                }
                            }),
                            h(
                                'span',
                                {
                                    style: {
                                        marginLeft: '10px',
                                        display: 'inline-flex',
                                        alignItems: 'center'
                                    },
                                    onClick: () => {
                                        onDeleteDictionaryData(row);
                                    }
                                },
                                [
                                    h('i', {
                                        class: 'ri-delete-bin-line',
                                        style: {
                                            marginRight: '2px'
                                        }
                                    }),
                                    h('span', t('删除'))
                                ]
                            ),

                            h(
                                'span',
                                {
                                    style: {
                                        marginLeft: '12px',
                                        display: 'inline-flex',
                                        alignItems: 'center'
                                    },
                                    onClick: async () => {
                                        const result = await jobInfoGet(row.id);
                                        ruleFormConfig.value.model = result.data;
                                        addDialogConfig.value.show = true;
                                        addDialogConfig.value.title = computed(() => t('编辑职位'));
                                    }
                                },
                                [
                                    h('i', {
                                        class: 'ri-pencil-line',
                                        style: {
                                            marginRight: '2px'
                                        }
                                    }),
                                    h('span', t('编辑'))
                                ]
                            )
                        ];

                        let saveActions = [
                            h(
                                'span',
                                {
                                    onClick: async () => {
                                        if (!formData.value.name) {
                                            ElMessage({
                                                type: 'error',
                                                message: t('请输入职位名称'),
                                                offset: 65
                                            });
                                        } else if (!formData.value.code) {
                                            ElMessage({
                                                type: 'error',
                                                message: t('请输入数据代码'),
                                                offset: 65
                                            });
                                        } else {
                                            tableConfig.value.loading = true;

                                            const result = await saveJobValue(formData.value);

                                            if (result.success) {
                                                editId.value = -1;
                                                await getDictionaryData(); //获取职位数据列表
                                            }

                                            ElNotification({
                                                title: result.success ? t('保存成功') : t('保存失败'),
                                                message: result.msg,
                                                type: result.success ? 'success' : 'error',
                                                duration: 2000,
                                                offset: 80
                                            });

                                            tableConfig.value.loading = false;
                                        }
                                    }
                                },
                                t('保存')
                            ),
                            h(
                                'span',
                                {
                                    style: {
                                        marginLeft: '10px'
                                    },
                                    onClick: () => {
                                        if (editId.value === 0) {
                                            //删除第一条数据
                                            tableConfig.value.tableData.shift();

                                            //初始化表单
                                            for (let key in formData.value) {
                                                formData.value[key] = '';
                                            }

                                            //取消编辑状态
                                            editId.value = -1;
                                        }
                                    }
                                },
                                t('取消')
                            )
                        ];
                        return h('span', editId.value === params.$index ? saveActions : editActions);
                    }
                }
            ],
            tableData: [],
            pageConfig: false
        },
        // 表单
        ruleFormConfig: {
            //表单配置
            model: {},
            rules: {
                //	表单验证规则。类型：FormRules
                name: [{ required: true, message: computed(() => t('请输入职位名称')), trigger: 'blur' }],
                code: [{ required: true, message: computed(() => t('请输入数据代码')), trigger: 'blur' }]
            },
            itemList: [
                {
                    type: 'input',
                    label: computed(() => t('职位名称')),
                    prop: 'name',
                    required: true
                },

                {
                    type: 'input',
                    label: computed(() => t('数据代码')),
                    prop: 'code',
                    required: true
                }
            ],
            descriptionsFormConfig: {
                labelWidth: '200px',
                labelAlign: 'center'
            }
        },
        currentRow: '',
        tabIndexs: [],
        filterConfig: {
            showBorder: true,
            filtersValueCallBack: (filter) => {
                formInline.value = filter;
            },
            itemList: [
                {
                    type: 'slot',
                    span: settingStore.device === 'mobile' ? 12 : 18,
                    slotName: 'addDictionaryData'
                },
                {
                    type: 'input',
                    key: 'name',
                    span: settingStore.device === 'mobile' ? 12 : 6
                }
            ]
        }
    });

    let {
        loading,
        tableCurrSelectedVal,
        formData,
        editId,
        currFilters,
        tableConfig,
        currentRow,
        tabIndexs,
        ruleFormConfig,
        filterConfig
    } = toRefs(data);

    //监听过滤条件改变时，获取职位数据
    const formInline = ref({
        name: undefined
    });

    watch(
        () => formInline.value,
        (newVal) => {
            if (newVal.name) {
                getJobByName(); //获取icon列表
            } else {
                getDictionaryData(); //获取icon列表
            }
        },
        {
            deep: true,
            immediate: true
        }
    );

    let addDialogConfig = ref({
        show: false,
        title: computed(() => t('编辑职位')),
        width: '40%',
        onOkLoading: true,
        onOk: (newConfig) => {
            return new Promise(async (resolve, reject) => {
                const y9RuleFormInstance = ruleFormRef.value?.elFormRef;
                await y9RuleFormInstance.validate(async (valid) => {
                    if (valid) {
                        const params = {
                            ...ruleFormRef.value?.model
                        };
                        await saveJobValue(params)
                            .then(async (result) => {
                                ElNotification({
                                    title: result.success ? t('成功') : t('失败'),
                                    message: result.success ? t('操作成功') : t('操作失败'),
                                    type: result.success ? 'success' : 'error',
                                    duration: 2000,
                                    offset: 80
                                });
                                if (result.success) {
                                    // 更新成功后 表单的数据 清空
                                    ruleFormConfig.value.model = {
                                        code: '',
                                        name: ''
                                        // tabIndex: '',
                                    };
                                    // 重新获取职位列表数据
                                    await getDictionaryData();
                                }
                                resolve();
                            })
                            .catch(() => {
                                reject();
                            });
                    } else {
                        reject();
                    }
                });
            });
        }
    });
    onMounted(() => {
        getDictionaryData(); //获取职位列表
    });

    //获取职位列表
    async function getDictionaryData() {
        tableConfig.value.loading = true;

        const result = await getJobList();

        if (result.success) {
            tabIndexs.value = [];
            result.data.forEach((element) => {
                // dn 只显示最后一个
                let temp_1 = element.code.split(',');
                let temp_2 = temp_1[temp_1.length - 1].split('=');
                element.code = temp_2[temp_2.length - 1];
                tabIndexs.value.push(element.tabIndex);
            });
            tableConfig.value.tableData = result.data;
        }

        tableConfig.value.loading = false;
    }

    //新增职位数据
    function onAddDictionaryData() {
        if (editId.value === 0) {
            ElMessage({
                type: 'error',
                message: t('请保存编辑数据后再操作'),
                offset: 65
            });

            return;
        }

        //表格上方插入空行
        tableConfig.value.tableData.unshift({
            name: '',
            type: ''
        });

        editId.value = 0; //第一行为编辑状态

        //初始化表单
        for (let key in formData.value) {
            if (key == 'type') {
                formData.value[key] = currFilters.value.type;
            } else {
                formData.value[key] = '';
            }
        }
    }

    //删除选中的职位数据
    function onDeleteDictionaryData(row) {
        const selected = tableCurrSelectedVal.value.map((item) => item.id);

        const ids = row ? [row.id] : selected;

        if (selected.length > 0 || row) {
            ElMessageBox.confirm(`${t('是否删除')}${row ? '【' + row.name + '】' : t('选中的数据')} ?`, t('提示'), {
                confirmButtonText: t('确定'),
                cancelButtonText: t('取消'),
                type: 'info'
                // loading: true,
            })
                .then(async () => {
                    loading.value = true;

                    if (editId.value === 0) {
                        //删除第一条数据
                        tableConfig.value.tableData.shift();

                        //初始化表单
                        for (let key in formData.value) {
                            formData.value[key] = '';
                        }

                        //取消编辑状态
                        editId.value = -1;

                        ids.shift();
                    }

                    if (ids.length > 0) {
                        const result = await deleteByIds(ids.toString());

                        if (result.success) {
                            editId.value = -1;
                            getDictionaryData(); //获取职位数据列表
                        }

                        ElNotification({
                            title: result.success ? t('删除成功') : t('删除失败'),
                            message: result.msg,
                            type: result.success ? 'success' : 'error',
                            duration: 2000,
                            offset: 80
                        });
                        // loading.close()
                        loading.value = false;
                    } else {
                        // loading.close()
                        loading.value = false;
                    }
                })
                .catch(() => {
                    ElMessage({
                        type: 'info',
                        message: t('已取消删除'),
                        offset: 65
                    });
                });
        } else {
            ElMessage({
                type: 'error',
                message: t('请选择数据'),
                offset: 65
            });
        }
    }

    //按职位名称搜索
    async function getJobByName() {
        let sendData = {
            name: formInline.value.name
        };
        if (sendData.name === undefined) {
            getDictionaryData();
            return;
        }
        const result = await getJobByMajorName(sendData);

        tableConfig.value.tableData = result.data;
    }

    function onCurrentChange(data) {
        currentRow.value = data;
    }

    //上移
    async function upJob() {
        if (currentRow.value == '' || currentRow.value == null) {
            ElNotification({ title: t('失败'), message: t('请选择职位'), type: 'error', duration: 2000, offset: 80 });
            return;
        }
        let tableData = tableConfig.value.tableData;
        tableData.forEach(function (element, index) {
            if (index == 0 && element.id == currentRow.value.id) {
                ElNotification({
                    title: t('失败'),
                    message: t('处于顶端，不能继续上移'),
                    type: 'error',
                    duration: 2000,
                    offset: 80
                });
                return;
            }
            if (element.id == currentRow.value.id) {
                let obj = tableData[index - 1];
                tableData[index - 1] = currentRow.value;
                tableData[index] = obj;
                tableConfig.value.tableData = tableData;
                return;
            }
        });
    }

    //下移
    async function downJob() {
        if (currentRow.value == '' || currentRow.value == null) {
            ElNotification({ title: t('失败'), message: t('请选择职位'), type: 'error', duration: 2000, offset: 80 });
            return;
        }
        let tableData = tableConfig.value.tableData;
        for (let i = 0; i < tableData.length; i++) {
            if (tableData.length - 1 == i && tableData[i].id == currentRow.value.id) {
                ElNotification({
                    title: t('失败'),
                    message: t('处于末端，不能继续下移'),
                    type: 'error',
                    duration: 2000,
                    offset: 80
                });
                return;
            }
            if (tableData[i].id == currentRow.value.id) {
                let obj = tableData[i + 1];
                tableData[i] = obj;
                tableData[i + 1] = currentRow.value;
                tableConfig.value.tableData = tableData;
                break;
            }
        }
    }

    //保存职位排序
    async function saveJobOrder() {
        const ids = [];
        let tableData = tableConfig.value.tableData;
        tableData.forEach((element) => {
            ids.push(element.id);
        });
        loading.value = true;
        let result = await saveOrder(ids.toString(), tabIndexs.value.toString());
        loading.value = false;

        ElNotification({
            title: result.success ? t('成功') : t('失败'),
            message: result.msg,
            type: result.success ? 'success' : 'error',
            duration: 2000,
            offset: 80
        });
    }
</script>
<style lang="scss" scoped>
    // :deep(.y9-filter-item) {
    //     .el-input__wrapper {
    //         padding: 0;
    //         border-radius: 30px;
    //         .el-input__prefix {
    //             margin-left: 20px;
    //         }
    //         .el-input__inner{
    //             border: none !important;
    //             padding-left: 10px;
    //         }
    //     }
    // }

    :deep(.el-col):nth-child(4) {
        padding-right: 0px !important;
    }
</style>
