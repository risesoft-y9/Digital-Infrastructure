<!--
 * @Descripttion: 字典表类型管理
 * @version: 
 * @Author:  
 * @Date: 2022-06-28 10:03:00
 * @LastEditors: mengjuhua
 * @LastEditTime: 2025-12-24 09:21:07
-->
<template>
    <y9Table :config="tableConfig" :filterConfig="filterConfig">
        <template #addDictionaryType>
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                class="global-btn-third"
                @click="onAddDictionaryType"
            >
                <i class="ri-add-line"></i>
                <span>{{ $t('字典表类型') }}</span>
            </el-button>
        </template>

        <template #name="{ row, column, index }">
            <el-input v-if="editId === index" v-model="formData.name" />
            <template v-else>{{ row.name }}</template>
        </template>

        <template #type="{ row, column, index }">
            <input autocomplete="new-password" hidden type="password" />
            <el-input v-if="editId === index && addType" v-model="formData.type" />
            <template v-else>{{ row.type }}</template>
        </template>
    </y9Table>
    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { $keyNameAssign } from '@/utils/object';
    import { getOptionClassList, removeOptionClass, saveOptionClass } from '@/api/dictionary/index';
    import { computed, h, inject, onMounted, reactive } from 'vue';
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');

    const { t } = useI18n();
    const data = reactive({
        formData: {
            name: '',
            type: ''
        },
        loading: false, // 全局loading
        editId: -1, //编辑id,
        addType: false, // 是否是添加标志
        tableConfig: {
            //表格配置
            loading: false,
            border: false,
            headerBackground: true,
            columns: [
                {
                    title: '#',
                    type: 'index',
                    width: 60
                },
                {
                    title: computed(() => t('中文名称')),
                    key: 'name',
                    slot: 'name',
                    showOverflowTooltip: false
                },
                {
                    title: computed(() => t('类型名称')),
                    key: 'type',
                    slot: 'type',
                    showOverflowTooltip: false
                },
                {
                    title: computed(() => t('操作')),
                    fixed: 'right',
                    with: 50,
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
                                        ElMessageBox.confirm(`${t('是否删除')}【${row.name}】 ?`, t('提示'), {
                                            confirmButtonText: t('确定'),
                                            cancelButtonText: t('取消'),
                                            type: 'info'
                                            // loading: true,
                                        })
                                            .then(async () => {
                                                loading.value = true;
                                                const result = await removeOptionClass([row.type].toString());
                                                loading.value = false;
                                                if (result.success) {
                                                    editId.value = -1;
                                                    getTypeList(); //获取字典数据列表
                                                }

                                                ElNotification({
                                                    title: result.success ? t('删除成功') : t('删除失败'),
                                                    message: result.msg,
                                                    type: result.success ? 'success' : 'error',
                                                    duration: 2000,
                                                    offset: 80
                                                });
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
                                                message: t('请输入中文名称'),
                                                offset: 65
                                            });
                                        } else if (!formData.value.type) {
                                            ElMessage({
                                                type: 'error',
                                                message: t('请输入类型名称'),
                                                offset: 65
                                            });
                                        } else {
                                            tableConfig.value.loading = true;

                                            const result = await saveOptionClass(formData.value);

                                            if (result.success) {
                                                editId.value = -1;
                                                getTypeList(); //获取字典类型列表
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
            //过滤配置
            itemList: [
                {
                    type: 'slot',
                    span: 24,
                    slotName: 'addDictionaryType'
                }
            ]
        }
    });

    let { formData, editId, addType, tableConfig, filterConfig, loading } = toRefs(data);

    onMounted(() => {
        getTypeList(); //获取字典类型列表
    });

    //获取字典类型列表
    async function getTypeList() {
        tableConfig.value.loading = true;

        const result = await getOptionClassList();
        if (result.success) {
            tableConfig.value.tableData = result.data;
        }

        tableConfig.value.loading = false;
    }

    //新增字典表类型
    function onAddDictionaryType() {
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

        //新增标志
        addType.value = true;

        editId.value = 0; //第一行为编辑状态

        //初始化表单
        for (let key in formData.value) {
            formData.value[key] = '';
        }
    }
</script>
<style lang="scss" scoped></style>
