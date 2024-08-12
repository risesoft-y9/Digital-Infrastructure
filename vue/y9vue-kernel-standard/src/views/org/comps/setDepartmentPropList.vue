<!--
 * @Author: fuyu
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2024-01-12 09:32:46
 * @Description: 组织架构-设置部门领导
-->
<template>
    <y9Card :title="`${$t('设置部门属性')}${currInfo.name ? ' - ' + currInfo.name : ''}`">
        <y9Table :config="setDepartmentPropTableConfig" :filterConfig="filterConfig">
            <template v-slot:filterBtnSlot>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-main"
                    type="primary"
                    @click="setDepartmentProp"
                >
                    <i class="ri-add-line"></i>
                    <span>{{ $t('添加') }}</span>
                </el-button>
            </template>
        </y9Table>
        <y9Dialog v-model:config="dialogConfig">
            <selectTree
                ref="selectTreeRef"
                :selectField="
                    typeName == 'org'
                        ? [
                              { fieldName: 'nodeType', value: ['Person'] },
                              { fieldName: 'disabled', value: false }
                          ]
                        : [
                              { fieldName: 'nodeType', value: ['Position'] },
                              { fieldName: 'disabled', value: false }
                          ]
                "
                :treeApiObj="typeName == 'org' ? treeApiObj : positionTreeApiObj"
            ></selectTree>
        </y9Dialog>
    </y9Card>
    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { computed, h, inject, onMounted, reactive, ref, toRefs, watch } from 'vue';
    import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
    import { $deepAssignObject } from '@/utils/object';
    import { getTreeItemById, searchByName, treeInterface } from '@/api/org/index';
    import { getDepartmentPropOrgUnits, removeDepartmentProp, setDepartmentPropOrgUnits } from '@/api/dept/index';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { $dictionary, $dictionaryFunc } from '@/utils/data';
    import { listByType } from '@/api/dictionary';

    const settingStore = useSettingStore();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const { t } = useI18n();
    const props = defineProps({
        currTreeNodeInfo: {
            //当前tree节点信息
            type: Object,
            default: () => {
                return {};
            }
        },

        typeName: {
            type: String,
            default: ''
        }
    });

    //选择tree实例
    const selectTreeRef = ref();

    const data = reactive({
        loading: false, // 全局loading
        departmentPropFormLine: {
            departmentPropCategory: null
        },
        treeApiObj: {
            //tree接口对象
            topLevel: treeInterface,
            childLevel: {
                api: getTreeItemById,
                params: { treeType: 'tree_type_person', disabled: false }
            },
            search: {
                api: searchByName,
                params: {
                    treeType: 'tree_type_org_person'
                }
            }
        },
        positionTreeApiObj: {
            //岗位tree接口对象
            topLevel: treeInterface,
            childLevel: {
                api: getTreeItemById,
                params: { treeType: 'tree_type_position', disabled: false }
            },
            search: {
                api: searchByName,
                params: {
                    treeType: 'tree_type_org_position'
                }
            }
        },
        //当前节点信息
        currInfo: props.currTreeNodeInfo,
        setDepartmentPropTableConfig: {
            //设置部门领导表格配置
            columns: [
                {
                    title: computed(() => t('名称')),
                    key: 'name'
                },
                {
                    title: computed(() => t('类别')),
                    width: 120,
                    render: (row) => {
                        if (row.orgType == 'Position') {
                            return '岗位';
                        } else if (row.orgType == 'Person') {
                            return '人员';
                        }
                    }
                },
                {
                    title: computed(() => t('全路径')),
                    key: 'dn'
                },
                {
                    title: computed(() => t('是否禁用')),
                    key: 'disabled',
                    width: 100,
                    render: (row) => {
                        return h('span', row.disabled ? '是' : '否');
                    }
                },
                {
                    title: computed(() => t('操作')),
                    render: (row) => {
                        return h(
                            'span',
                            {
                                onClick: () => {
                                    ElMessageBox.confirm(`${t('是否移除')}【${row.name}】?`, t('提示'), {
                                        confirmButtonText: t('确定'),
                                        cancelButtonText: t('取消'),
                                        type: 'info'
                                    })
                                        .then(async () => {
                                            loading.value = true;
                                            let result = await removeDepartmentProp(
                                                currInfo.value.id,
                                                row.id,
                                                departmentPropFormLine.value.departmentPropCategory
                                            );
                                            loading.value = false;
                                            if (result.success) {
                                                setDepartmentPropTableConfig.value.tableData.forEach((item, index) => {
                                                    if (item.id == row.id) {
                                                        setDepartmentPropTableConfig.value.tableData.splice(index, 1);
                                                    }
                                                });
                                            }

                                            ElNotification({
                                                title: result.success ? t('成功') : t('失败'),
                                                message: result.msg,
                                                type: result.success ? 'success' : 'error',
                                                duration: 2000,
                                                offset: 80
                                            });
                                        })
                                        .catch(() => {
                                            ElMessage({
                                                type: 'info',
                                                message: t('已取消移除'),
                                                offset: 65
                                            });
                                        });
                                }
                            },
                            t('移除')
                        );
                    }
                }
            ],
            tableData: [],
            pageConfig: false //取消分页
        },
        //弹窗配置
        dialogConfig: {
            show: false,
            title: '',
            onOkLoading: true,
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {
                    let orgBaseIds = selectTreeRef.value?.y9TreeRef?.getCheckedKeys(true);

                    if (orgBaseIds.length == 0) {
                        ElNotification({
                            title: t('失败'),
                            message: t('请选择'),
                            type: 'error',
                            duration: 2000,
                            offset: 80
                        });
                        reject();
                        return;
                    }

                    let result = await setDepartmentPropOrgUnits(
                        currInfo.value.id,
                        departmentPropFormLine.value.departmentPropCategory,
                        orgBaseIds.toString()
                    );
                    ElNotification({
                        title: result.success ? t('成功') : t('失败'),
                        message: result.msg,
                        type: result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    if (result.success) {
                        getDepartmentPropList();
                    }
                    resolve();
                });
            },
            visibleChange: (visible) => {}
        },
        // 表格过滤
        filterConfig: {
            filtersValueCallBack: (filters) => {
                departmentPropFormLine.value = filters;
                getDepartmentPropList();
            },
            showBorder: true,
            itemList: [
                {
                    type: 'select',
                    value: null,
                    key: 'departmentPropCategory',
                    label: computed(() => t('部门属性类型')),
                    labelWidth: '100px',
                    span: settingStore.device === 'mobile' ? 24 : 8,
                    props: {
                        options: [],
                        clearable: false
                    }
                },
                {
                    type: 'slot',
                    slotName: 'filterBtnSlot',
                    span: 6
                }
            ]
        }
    });

    let {
        treeApiObj,
        positionTreeApiObj,
        currInfo,
        setDepartmentPropTableConfig,
        dialogConfig,
        filterConfig,
        loading,
        departmentPropFormLine
    } = toRefs(data);

    watch(
        () => props.currTreeNodeInfo,
        (newVal) => {
            currInfo.value = $deepAssignObject(currInfo.value, newVal);

            getDepartmentPropList();
        },
        {
            deep: true
        }
    );
    onMounted(async () => {
        await $dictionaryFunc('departmentPropCategory', listByType, 'departmentPropCategory'); //请求编制类型
        for (let i = 0; i < filterConfig.value.itemList.length; i++) {
            const item = filterConfig.value.itemList[i];
            if (item.key === 'departmentPropCategory') {
                item.props.options = $dictionary().departmentPropCategory?.map((item) => {
                    return {
                        label: computed(() => t(item.name)),
                        value: item.code
                    };
                });
                if (item.props.options.length > 0) {
                    item.value = item.props.options[0].value;
                }
            }
        }
        getDepartmentPropList();
    });

    async function getDepartmentPropList() {
        if (departmentPropFormLine.value.departmentPropCategory) {
            let res = await getDepartmentPropOrgUnits(
                props.currTreeNodeInfo.id,
                departmentPropFormLine.value.departmentPropCategory
            );
            if (res.success) {
                setDepartmentPropTableConfig.value.tableData = res.data;
            }
        }
    }

    async function setDepartmentProp() {
        if (!departmentPropFormLine.value.departmentPropCategory) {
            ElNotification({
                title: t('失败'),
                message: t('请先选择部门属性类型'),
                type: 'error',
                duration: 2000,
                offset: 80
            });
            return;
        }

        Object.assign(dialogConfig.value, {
            show: true,
            title: computed(() => t('设置部门属性')),
            resetText: false,
            cancelText: computed(() => t('取消'))
        });
    }
</script>

<style lang="scss" scoped></style>
