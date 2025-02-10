<!--
 * @Descripttion: 字典表数据管理
 * @version: 
 * @Author:  
 * @Date: 2022-06-28 10:03:00
 * @LastEditors: chensiwen cikl777@163.com
 * @LastEditTime: 2025-01-11 17:12:57
-->
<template>
    <y9Table v-model:selectedVal="tableCurrSelectedVal" :config="tableConfig" :filterConfig="filterConfig">
        <template #addDictionaryData>
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                class="global-btn-third"
                @click="onAddDictionaryData"
            >
                <i class="ri-add-line"></i>
                <span>{{ $t('字典表数据') }}</span>
            </el-button>
            <!-- <el-button 
			class="global-btn-third"
			@click="onDeleteDictionaryData()">
			  	<i class="ri-delete-bin-line"></i>
				<span>{{ $t('删除') }}</span>
			</el-button> -->
        </template>
        <template #name="{ row, column, index }">
            <el-input v-if="editId === index" v-model="formData.name" />
            <template v-else>{{ row.name }}</template>
        </template>
        <template #code="{ row, column, index }">
            <input autocomplete="new-password" hidden type="password" />
            <el-input v-if="editId === index && addType" v-model="formData.code" />
            <template v-else>{{ row.code }}</template>
        </template>
    </y9Table>
    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { $keyNameAssign } from '@/utils/object';
    import { getOptionClassList, listByType, removeByIds, saveOptionValue } from '@/api/dictionary/index';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { computed, h, inject, onMounted, reactive, watch } from 'vue';
    import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';

    const settingStore = useSettingStore();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const { t } = useI18n();
    const data = reactive({
        tableCurrSelectedVal: [], //表格选择的数据
        formData: {
            // id:"",
            name: '',
            code: '',
            // tabIndex:"",
            type: ''
        },
        loading: false, // 全局loading
        editId: -1, //编辑id
        addType: false, // 是否是添加标志
        currFilters: {}, //当前选择的过滤数据
        tableConfig: {
            //表格配置
            border: false,
            headerBackground: true,
            columns: [
                // {
                //     type: 'selection',
                //     width: 60
                // },
                {
                    type: 'index',
                    width: 60,
                    title: '#'
                },
                {
                    title: computed(() => t('数据名称')),
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
                    title: computed(() => t('字典类型')),
                    key: 'type',
                    showOverflowTooltip: false
                },
                {
                    title: computed(() => t('操作')),
                    with: 200,
                    fixed: 'right',

                    render: (row, params) => {
                        let editActions = [
                            h(
                                'span',
                                {
                                    style: {
                                        display: 'inline-flex',
                                        alignItems: 'center'
                                    },
                                    onClick: () => {
                                        editId.value = params.$index;
                                        addType.value = false;
                                        $keyNameAssign(formData.value, row);
                                    }
                                },
                                [
                                    h('i', {
                                        class: 'ri-edit-line',
                                        style: {
                                            marginRight: '2px'
                                        }
                                    }),
                                    h('span', t('编辑'))
                                ]
                            ),
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
                                                message: t('请输入数据名称'),
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

                                            const result = await saveOptionValue(formData.value);

                                            if (result.success) {
                                                editId.value = -1;
                                                getDictionaryData(); //获取字典数据列表
                                            }

                                            ElNotification({
                                                title: result.success ? t('保存成功') : t('保存失败'),
                                                message:
                                                    !addType.value && result.success ? '编辑字典类型成功' : result.msg,
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
                                        if (addType.value) {
                                            //删除第一条数据
                                            tableConfig.value.tableData.shift();

                                            //初始化表单
                                            for (let key in formData.value) {
                                                formData.value[key] = '';
                                            }
                                        }
                                        //取消编辑状态
                                        editId.value = -1;
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
        filterConfig: {
            itemList: [
                {
                    type: 'select',
                    key: 'type',
                    props: {
                        options: []
                    },
                    span: settingStore.device === 'mobile' ? 12 : 4
                },
                {
                    type: 'slot',
                    span: settingStore.device === 'mobile' ? 12 : 20,
                    slotName: 'addDictionaryData'
                }
            ],
            filtersValueCallBack: (filters) => {
                //过滤值回调

                currFilters.value = filters;
            }
        }
    });

    let { loading, tableCurrSelectedVal, formData, editId, addType, currFilters, tableConfig, filterConfig } =
        toRefs(data);

    onMounted(() => {
        getTypeList(); //获取字典类型列表
    });

    //获取字典表类型列表
    async function getTypeList() {
        const result = await getOptionClassList();

        if (result.success) {
            filterConfig.value.itemList.forEach((item) => {
                if (item.type == 'select' && item.key == 'type') {
                    item.props.options = result.data.map((item) => {
                        return {
                            label: item.name,
                            value: item.type
                        };
                    });

                    item.value = item.props.options[0].value; //默认选择第一项
                }
            });
        }
    }

    //监听过滤条件改变时，获取字典表数据
    watch(
        () => currFilters.value,
        (newVal) => {
            if (newVal.type) {
                getDictionaryData(); //获取字典数据列表
            }
        },
        {
            deep: true,
            immediate: true
        }
    );

    //获取字典数据列表
    async function getDictionaryData() {
        tableConfig.value.loading = true;

        const result = await listByType(currFilters.value.type);

        if (result.success) {
            tableConfig.value.tableData = result.data;
        }

        tableConfig.value.loading = false;
    }

    //新增字典表数据
    function onAddDictionaryData() {
        if (editId.value !== -1) {
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

        //保存标志
        addType.value = true;

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

    //删除选中的字典数据
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
                    // let loading = ElLoading.service({
                    // 					lock: true,
                    // 					background: 'rgba(0, 0, 0, 0.5)',
                    // 				});
                    loading.value = true;

                    // if (addType.value) {
                    //     //删除第一条数据
                    //     tableConfig.value.tableData.shift();

                    //     //初始化表单
                    //     for (let key in formData.value) {
                    //         formData.value[key] = '';
                    //     }

                    //     //取消编辑状态
                    //     editId.value = -1;

                    //     ids.shift();
                    // }

                    if (ids.length > 0) {
                        const result = await removeByIds(ids.toString());

                        if (result.success) {
                            editId.value = -1;
                            getDictionaryData(); //获取字典数据列表
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
</script>
<style lang="scss" scoped></style>
