<template>
    <!-- 扩展属性 -->
    <div v-if="loading" class="loading">
        <svg class="circular" viewBox="25 25 50 50">
            <circle class="path" cx="50" cy="50" r="20" fill="none"></circle>
        </svg>
    </div>
    <div class="margin-bottom-20">
        <el-button type="primary" class="global-btn-main" :style="{ fontSize: fontSizeObj.baseFontSize }"
        :size="fontSizeObj.buttonSize"  @click="onAddExtendAttr">
            <i class="ri-external-link-line"></i>
            <span>{{ $t('扩展属性') }}</span>
        </el-button>
    </div>
    <y9Table :config="tableConfig">
        <template #name="{ row, column, index }">
            <el-input v-if="editId === index" v-model="formData.name" />
            <template v-else>{{ row.name }} </template>
        </template>

        <template #attrValue="{ row, column, index }">
            <el-input v-if="editId === index" v-model="formData.attrValue" />
            <template v-else>{{ row.attrValue }}</template>
        </template>
    </y9Table>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { $keyNameAssign, $tableHandleRender } from '@/utils/object';
    import { saveDeptExtendProperties } from '@/api/dept/index';
    import { savePersonExtendProperties } from '@/api/person/index';
    import { saveOrgExtendProperties } from '@/api/org/index';
	import { saveGroupExtendProperties } from '@/api/group/index';
	import { savePositionExtendProperties } from '@/api/position/index';
    import { useSettingStore } from "@/store/modules/settingStore";
    import { inject, ref, watch } from 'vue';
	const settingStore = useSettingStore();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const { t } = useI18n();
    const props = defineProps({
        currInfo: {
            //当前信息
            type: Object,
            default: () => {
                return {};
            },
        },
		
		handAssginNode: {
            //手动合并节点信息
            type: Function,
        },
    });
    const data = reactive({
        loading: false,
        formData: {
            name: '',
            attrValue: '',
        },
        editId: -1, //编辑id
        tableConfig: {
            //表格配置
            columns: [
                {
                    title: computed(() => t("标识")),
                    key: 'name',
                    slot: 'name',
                },
                {
                    title: computed(() => t("属性值")),
                    key: 'attrValue',
                    slot: 'attrValue',
                },
                {
                    title: computed(() => t("操作")),
                    width: settingStore.getTwoBtnWidth,
                    render: (row, params) => {
                        let editActions = $tableHandleRender([
                            {
                                title: t('编辑'),
                                // remixicon:'ri-edit-line',
                                onClick: () => {
                                    if (editId.value == -1) {
                                        editId.value = params.$index;
                                        $keyNameAssign(formData.value, row);
                                    } else {
                                        ElMessage({
                                            type: 'error',
                                            message: t('请保存编辑数据后再操作'),
                                            offset: 65,
                                        });
                                    }
                                },
                            },
                            {
                                title: t('删除'),
                                // remixicon:'ri-delete-bin-line',
                                onClick: () => {
                                    ElMessageBox.confirm(`${t('是否删除')}【${row.name}】 ?`, t('提示'), {
                                        confirmButtonText: t('确定'),
                                        cancelButtonText: t('取消'),
                                        type: 'info',
                                    })
                                        .then(async () => {
                                            if (editId.value == -1) {
                                                for (let i = 0; i < tableConfig.value.tableData.length; i++) {
                                                    const item = tableConfig.value.tableData[i];
                                                    if (item.id === row.id) {
                                                        tableConfig.value.tableData.splice(i, 1);
														let propertiesObj = {};
														tableConfig.value.tableData.forEach((item) => {
														    propertiesObj[item.name] = item.attrValue;
														});
														let propertiesString = JSON.stringify(propertiesObj);
														props.handAssginNode && props.handAssginNode({properties:propertiesString},props.currInfo.id)
                                                        break;
                                                    }
                                                }
                                            } else {
                                                ElMessage({
                                                    type: 'error',
                                                    message: t('请保存编辑数据后再操作'),
                                                    offset: 65,
                                                });
                                            }
                                        })
                                        .catch(() => {
                                            ElMessage({
                                                type: 'info',
                                                message: t('已取消删除'),
                                                offset: 65,
                                            });
                                        });
                                },
                            },
                        ]);

                        let saveActions = $tableHandleRender([
                            {
                                title: t('保存'),
                                onClick: async () => {
                                    if (!formData.value.name) {
                                        ElMessage({
                                            type: 'error',
                                            message: t('请输入标识'),
                                            offset: 65,
                                        });
                                    } else if (!formData.value.attrValue) {
                                        ElMessage({
                                            type: 'error',
                                            message: t('请输入属性值'),
                                            offset: 65,
                                        });
                                    } else {
                                        if (row.id) {
                                            for (let item of tableConfig.value.tableData) {
                                                if (item.id === row.id) {
                                                    Object.assign(item, formData.value, {
                                                        id: formData.value['name'] + '_' + formData.value['attrValue'],
                                                    });
                                                    break;
                                                }
                                            }
                                        } else {
                                            Object.assign(tableConfig.value.tableData[0], {
                                                id: formData.value['name'] + '_' + formData.value['attrValue'],
                                                ...formData.value,
                                            });
                                        }

                                        let result;
                                        let propertiesObj = {};
                                        tableConfig.value.tableData.forEach((item) => {
                                            propertiesObj[item.name] = item.attrValue;
                                        });
                                        let propertiesString = JSON.stringify(propertiesObj);

                                        if (props.currInfo.orgType == 'Department') {
                                            loading.value = true;
                                            result = await saveDeptExtendProperties(
                                                props.currInfo.id,
                                                propertiesString
                                            );
                                        } else if (props.currInfo.orgType == 'Person') {
                                            loading.value = true;
                                            result = await savePersonExtendProperties(
                                                props.currInfo.id,
                                                propertiesString
                                            );
                                        } else if (props.currInfo.orgType == 'Organization') {
                                            loading.value = true;
                                            result = await saveOrgExtendProperties(props.currInfo.id, propertiesString);
                                        } else if (props.currInfo.orgType == 'Group') {
                                            loading.value = true;
                                            result = await saveGroupExtendProperties(props.currInfo.id, propertiesString);
                                        } else if (props.currInfo.orgType == 'Position') {
                                            loading.value = true;
                                            result = await savePositionExtendProperties(props.currInfo.id, propertiesString);
                                        }

                                        loading.value = false;
                                        if (result.success) {
											props.handAssginNode && props.handAssginNode({properties:propertiesString},props.currInfo.id)
                                        }

                                        //初始化表单
                                        for (let key in formData.value) {
                                            formData.value[key] = '';
                                        }
                                        //取消编辑状态
                                        editId.value = -1;
                                    }
                                },
                            },
                            {
                                title: t('取消'),
                                onClick: () => {
                                    if (editId.value === 0 && !row.id) {
                                        //删除第一条数据
                                        tableConfig.value.tableData.shift();
                                    }
                                    //初始化表单
                                    for (let key in formData.value) {
                                        formData.value[key] = '';
                                    }
                                    //取消编辑状态
                                    editId.value = -1;
                                },
                            },
                        ]);

                        return h('span', editId.value === params.$index ? saveActions : editActions);
                    },
                },
            ],
            tableData: [],
            pageConfig: false, //取消分页
        },
    });

    let { loading, formData, editId, tableConfig } = toRefs(data);

    defineExpose({
        editId,
        tableConfig,
    });

    onMounted(() => {
        if (props.currInfo.properties) {
            const properties = JSON.parse(props.currInfo.properties);

            let data = [];

            let count = 0;
            for (let key in properties) {
                count++;
                data.push({
                    id: key + '_' + properties[key],
                    name: key,
                    attrValue: properties[key],
                });
            }
            tableConfig.value.tableData = data;
        }
    });

    //新增扩展属性按钮点击事件
    function onAddExtendAttr() {
        if (editId.value !== -1) {
            ElMessage({
                type: 'error',
                message: t('请保存编辑数据后再操作'),
                offset: 65,
            });

            return;
        }

        //表格上方插入空行
        tableConfig.value.tableData.unshift({
            name: '',
            attrValue: '',
        });

        editId.value = 0; //第一行为编辑状态

        //初始化表单
        for (let key in formData.value) {
            formData.value[key] = '';
        }
    }
</script>

<style lang="scss" scoped>
    .loading {
        position: absolute;
        top: 0;
        right: 0;
        bottom: 0;
        left: 0;
        background-color: rgba(255, 255, 255, 0.9);
        transition: opacity 0.3s;
        z-index: 999;
        display: flex;
        justify-content: center;
        align-items: center;
        .circular {
            display: inline;
            height: 42px;
            width: 42px;
            animation: loading-rotate 2s linear infinite;

            .path {
                animation: loading-dash 1.5s ease-in-out infinite;
                stroke-dasharray: 90, 150;
                stroke-dashoffset: 0;
                stroke-width: 2;
                stroke: var(--el-color-primary);
                stroke-linecap: round;
            }
        }
    }
</style>
