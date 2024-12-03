<template>
    <y9Table
        ref="apiAccessControlTableRef"
        :config="apiAccessControlTableConfig"
        :filterConfig="filterConfig"
        @on-current-change="onCurrentChange"
    >
        <template #addApiAccessControl>
            <el-button
                :disabled="apiAccessControlTableConfig.loading"
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                class="global-btn-main"
                type="primary"
                @click="onAddApiAccessControlData"
            >
                <i class="ri-add-line"></i>
                <span>{{ $t('生成') }}</span>
            </el-button>
        </template>
        <template #appId="{ row, column, index }">
            <div>{{ row.id }}</div>
        </template>
        <template #appSecret="{ row, column, index }">
            <div>{{ row.value }}</div>
        </template>
        <template #remark="{ row, column, index }">
            <el-input v-if="currEditId === index" v-model="editForm.remark" :size="fontSizeObj.buttonSize"></el-input>
            <div v-else>{{ row.remark }}</div>
        </template>
        <template #enabled="{ row, column, index }">
            <div>{{ row.enabled ? '☑️' : '❌' }}</div>
        </template>
    </y9Table>
</template>

<script lang="ts" setup>
    import y9_storage from '@/utils/storage';
    import { changeEnabled, list, remove, saveAppIdSecret, saveOrder, search } from '@/api/apiAccessControl/index';
    import { useI18n } from 'vue-i18n';
    import { inject, ref } from 'vue';

    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const managerLevel = y9_storage.getObjectItem('ssoUserInfo', 'managerLevel');

    //正在编辑的行id
    const currEditId = ref(-1); //-1代表没有正在编辑的行，大于-1代表有正在编辑的行

    const editForm = ref({
        id: '', //id不存在代表是新增的行，存在代表是编辑已经存在的行
        value: '',
        remark: '',
        type: 'APP_ID_SECRET',
        enabled: false
    });

    const apiAccessControlTableRef = ref();

    //新增名单数据
    function onAddApiAccessControlData() {
        if (currEditId.value == 0) {
            ElMessage({
                type: 'error',
                message: t('请保存编辑数据后再操作'),
                offset: 65
            });
            return;
        }

        editForm.value = {
            //初始化表单
            id: '',
            value: '',
            remark: '',
            type: 'APP_ID_SECRET',
            enabled: false
        };
        //表格上方插入空行
        apiAccessControlTableConfig.value.tableData.unshift(editForm.value);

        currEditId.value = 0; //第一行为编辑状态
    }

    //表格列表配置
    const apiAccessControlTableConfig = ref({
        loading: false,
        border: false,
        headerBackground: true,
        columns: [
            /*{
        type: 'radio',
        title: computed(() => t('请选择')),
        width: 200
    },*/
            {
                title: computed(() => t('appId')),
                key: 'appId',
                slot: 'appId',
                showOverflowTooltip: false
            },
            {
                title: computed(() => t('appSecret')),
                key: 'value',
                slot: 'appSecret',
                showOverflowTooltip: false
            },
            {
                title: computed(() => t('备注')),
                key: 'remark',
                slot: 'remark',
                showOverflowTooltip: false
            },
            {
                title: computed(() => t('启用')),
                key: 'enabled',
                slot: 'enabled',
                showOverflowTooltip: false
            },
            {
                title: computed(() => t('操作')),
                width: 200,
                fixed: 'right',
                render: (row, params) => {
                    const btnsGroup1 = [];

                    if (managerLevel === 1 || managerLevel === 4) {
                        btnsGroup1.unshift(
                            h(
                                'div',
                                {
                                    style: {
                                        display: 'flex',
                                        alignItems: 'center'
                                    },
                                    onClick: () => {
                                        //编辑按钮被点击后
                                        if (currEditId.value > -1) {
                                            ElMessage({
                                                type: 'error',
                                                message: t('请保存编辑数据后再操作'),
                                                offset: 65
                                            });
                                            return;
                                        }
                                        currEditId.value = params.$index;
                                        editForm.value.id = row.id;
                                        editForm.value.value = row.value;
                                        editForm.value.remark = row.remark;
                                        editForm.value.type = row.type;
                                    }
                                },
                                [
                                    h('i', {
                                        class: 'ri-pencil-line',
                                        style: {
                                            marginRight: '2px',
                                            fontSize: fontSizeObj.baseFontSize
                                        }
                                    }),
                                    h('span', t('编辑'))
                                ]
                            ),
                            h(
                                'div',
                                {
                                    style: {
                                        display: 'flex',
                                        alignItems: 'center',
                                        marginLeft: '10px'
                                    },
                                    onClick: () => {
                                        //删除按钮被点击后

                                        if (currEditId.value > -1) {
                                            ElMessage({
                                                type: 'error',
                                                message: t('请保存编辑数据后再操作'),
                                                offset: 65
                                            });
                                            return;
                                        }

                                        ElMessageBox.confirm(`${t('是否删除')}【${row.value}】 名单?`, t('提示'), {
                                            confirmButtonText: t('确定'),
                                            cancelButtonText: t('取消'),
                                            type: 'info'
                                        })
                                            .then(async () => {
                                                apiAccessControlTableConfig.value.loading = true;
                                                const res = await remove(row.id);

                                                if (res.code == 0 && res.success) {
                                                    await getAccessControlList();
                                                }

                                                ElNotification({
                                                    title: res.success ? t('删除成功') : t('删除失败'),
                                                    message: res.msg,
                                                    type: res.success ? 'success' : 'error',
                                                    duration: 2000,
                                                    offset: 80
                                                });
                                                apiAccessControlTableConfig.value.loading = false;
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
                                            marginRight: '2px',
                                            fontSize: fontSizeObj.baseFontSize
                                        }
                                    }),
                                    h('span', t('删除'))
                                ]
                            )
                        );
                    }

                    if (managerLevel === 2 || managerLevel === 5) {
                        btnsGroup1.unshift(
                            h(
                                'div',
                                {
                                    style: {
                                        display: 'flex',
                                        alignItems: 'center'
                                    },
                                    onClick: async () => {
                                        apiAccessControlTableConfig.value.loading = true;
                                        const res = await changeEnabled(row.id);

                                        if (res.success) {
                                            await getAccessControlList();
                                        }

                                        ElNotification({
                                            title: res.success ? t('修改成功') : t('修改失败'),
                                            message: res.msg,
                                            type: res.success ? 'success' : 'error',
                                            duration: 2000,
                                            offset: 80
                                        });
                                        apiAccessControlTableConfig.value.loading = false;
                                    }
                                },
                                [
                                    h('i', {
                                        class: 'ri-pencil-line',
                                        style: {
                                            marginRight: '2px',
                                            fontSize: fontSizeObj.baseFontSize
                                        }
                                    }),
                                    h('span', t(row.enabled ? '禁用' : '启用'))
                                ]
                            )
                        );
                    }

                    const btnsGroup2 = [
                        h(
                            'div',
                            {
                                style: {
                                    display: 'flex',
                                    alignItems: 'center'
                                },
                                onClick: async () => {
                                    //保存按钮被点击后

                                    apiAccessControlTableConfig.value.loading = true;
                                    const res = await saveAppIdSecret(editForm.value);
                                    if (res.code == 0 && res.success) {
                                        if (!editForm.value.id) {
                                            //如果是新增就手动删除
                                            apiAccessControlTableConfig.value.tableData.splice(params.$index, 1);
                                        }
                                        currEditId.value = -1;
                                        editForm.value = {};
                                        await getAccessControlList();
                                    }

                                    ElNotification({
                                        title: res.success ? t('保存成功') : t('保存失败'),
                                        message: res.msg,
                                        type: res.success ? 'success' : 'error',
                                        duration: 2000,
                                        offset: 80
                                    });
                                    apiAccessControlTableConfig.value.loading = false;
                                }
                            },
                            [
                                h('i', {
                                    class: 'ri-pencil-line',
                                    style: {
                                        marginRight: '2px',
                                        fontSize: fontSizeObj.baseFontSize
                                    }
                                }),
                                h('span', t('保存'))
                            ]
                        ),
                        h(
                            'div',
                            {
                                style: {
                                    display: 'flex',
                                    alignItems: 'center',
                                    marginLeft: '10px'
                                },
                                onClick: () => {
                                    //删除按钮被点击后

                                    if (!editForm.value.id) {
                                        //id不存在，代表是新增数据，手动删除
                                        apiAccessControlTableConfig.value.tableData.splice(params.$index, 1);
                                    }
                                    currEditId.value = -1;
                                    editForm.value = {};
                                }
                            },
                            [
                                h('i', {
                                    class: 'ri-delete-bin-line',
                                    style: {
                                        marginRight: '2px',
                                        fontSize: fontSizeObj.baseFontSize
                                    }
                                }),
                                h('span', t('取消'))
                            ]
                        )
                    ];
                    return h(
                        'div',
                        {
                            style: {
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'center'
                            }
                        },
                        currEditId.value === params.$index ? btnsGroup2 : btnsGroup1
                    );
                }
            }
        ],
        tableData: [],
        pageConfig: false
    });

    const filterConfig = ref({
        showBorder: true,
        filtersValueCallBack: async (filter) => {
            //过滤回调
            //搜索逻辑处理
            if (filter.name) {
                apiAccessControlTableConfig.value.loading = true;
                const res = await search(filter.name);
                apiAccessControlTableConfig.value.tableData = res.data;
                apiAccessControlTableConfig.value.loading = false;
            } else {
                await getAccessControlList();
            }
        },
        itemList: [
            {
                type: 'slot',
                span: 18,
                slotName: 'addApiAccessControl'
            }
        ]
    });

    onMounted(async () => {
        await getAccessControlList();
    });

    async function getAccessControlList() {
        apiAccessControlTableConfig.value.loading = true;
        const res = await list({ type: 'APP_ID_SECRET' });
        if (res.code == 0 && res.success) {
            apiAccessControlTableConfig.value.tableData = res.data;
        }
        apiAccessControlTableConfig.value.loading = false;
    }

    const currentRow = ref(''); //当前选中行
    function onCurrentChange(data) {
        currentRow.value = data;
    }

    const tabIndexs = ref([]);

    //上移
    async function upJob() {
        if (currentRow.value == '' || currentRow.value == null) {
            ElNotification({ title: t('失败'), message: t('请选择名单'), type: 'error', duration: 2000, offset: 80 });
            return;
        }
        let tableData = apiAccessControlTableConfig.value.tableData;
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
                apiAccessControlTableConfig.value.tableData = tableData;
                return;
            }
        });
    }

    //下移
    async function downJob() {
        if (currentRow.value == '' || currentRow.value == null) {
            ElNotification({ title: t('失败'), message: t('请选择名单'), type: 'error', duration: 2000, offset: 80 });
            return;
        }
        let tableData = apiAccessControlTableConfig.value.tableData;
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
                apiAccessControlTableConfig.value.tableData = tableData;
                break;
            }
        }
    }

    //保存名单排序
    async function saveJobOrder() {
        if (currentRow.value == '' || currentRow.value == null) {
            ElNotification({ title: t('失败'), message: t('请选择排序'), type: 'error', duration: 2000, offset: 80 });
            return;
        }
        const ids = [];
        let tableData = apiAccessControlTableConfig.value.tableData;
        tableData.forEach((element) => {
            ids.push(element.id);
        });
        apiAccessControlTableConfig.value.loading = true;
        let result = await saveOrder(ids.toString(), tabIndexs.value.toString());
        apiAccessControlTableConfig.value.loading = false;
        currentRow.value = '';

        ElNotification({
            title: result.success ? t('成功') : t('失败'),
            message: result.msg,
            type: result.success ? 'success' : 'error',
            duration: 2000,
            offset: 80
        });
    }
</script>

<style lang="scss" scoped></style>
