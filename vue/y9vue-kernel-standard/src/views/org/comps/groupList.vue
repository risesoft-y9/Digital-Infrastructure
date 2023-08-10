<!--
 * @Author: fuyu
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2023-08-03 15:26:51
 * @Description: 组织架构-用户组列表
-->
<template>
    <y9Card :title="`${$t('用户组列表')}${currInfo.name ? ' - ' + currInfo.name : ''}`">
        <div class="margin-bottom-20" style="display: flex; justify-content: space-between">
            <div>
                <el-button
                    type="primary"
                    class="global-btn-main"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    @click="addGroup"
                >
                    <i class="ri-add-line"></i>
                    <span>{{ $t('用户组') }}</span>
                </el-button>
                <el-button
                    type="primary"
                    class="global-btn-main"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    @click="saveGroupOrder"
                >
                    <i class="ri-save-line"></i>
                    <span>{{ $t('保存') }}</span>
                </el-button>
            </div>
            <div>
                <el-button
                    class="global-btn-second"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    @click="upGroup"
                >
                    <i class="ri-arrow-up-line"></i>
                    <span>{{ $t('上移') }}</span>
                </el-button>
                <el-button
                    class="global-btn-second"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    @click="downGroup"
                >
                    <i class="ri-arrow-down-line"></i>
                    <span>{{ $t('下移') }}</span>
                </el-button>
            </div>
        </div>
        <y9Table
            ref="groupTableRef"
            v-model:selectedVal="groupSelectedData"
            :config="groupListTableConfig"
            @on-current-change="onCurrentChange"
        ></y9Table>
    </y9Card>
    <y9Dialog v-model:config="dialogConfig">
        <selectTree
            ref="selectTreeRef"
            :treeApiObj="treeApiObj"
            :selectField="[
                { fieldName: 'orgType', value: ['Group'] },
                { fieldName: 'disabled', value: false },
            ]"
        ></selectTree>
    </y9Dialog>
    <el-button style="display: none" v-loading.fullscreen.lock="loading"></el-button>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { inject, watch, reactive, computed, h, onMounted, ref, toRefs } from 'vue';
    import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
    import { $deepAssignObject } from '@/utils/object';
    import { getGroupsByPersonId, orderGroups } from '@/api/group/index';
    import { treeInterface, getTreeItemById, searchByName } from '@/api/org/index';
    import { removeGroups, addGroups } from '@/api/person/index';
    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const props = defineProps({
        currTreeNodeInfo: {
            //当前tree节点信息
            type: Object,
            default: () => {
                return {};
            },
        },
    });

    //选择tree实例
    const selectTreeRef = ref();
    const data = reactive({
        treeApiObj: {
            //tree接口对象
            topLevel: treeInterface,
            childLevel: {
                api: getTreeItemById,
                params: { treeType: 'tree_type_group', disabled: false },
            },
            search: {
                api: searchByName,
                params: {
                    treeType: 'ree_type_org',
                },
            },
        },
        loading: false, // 全局loading
        //当前节点信息
        currInfo: props.currTreeNodeInfo,
        currentRow: '', //当前选中行
        groupSelectedData: '', //选中的组数据
        groupListTableConfig: {
            //用户组列表表格配置
            columns: [
                {
                    type: 'radio',
                    width: 80,
                },
                {
                    title: computed(() => t('名称')),
                    key: 'name',
                },
                {
                    title: computed(() => t('全路径')),
                    key: 'dn',
                },
                {
                    title: computed(() => t('操作')),
                    render: (row) => {
                        return h('i', {
                            class: 'ri-delete-bin-line',
                            onClick: () => {
                                ElMessageBox.confirm(`${t('是否删除')}【${row.name}】?`, t('提示'), {
                                    confirmButtonText: t('确定'),
                                    cancelButtonText: t('取消'),
                                    type: 'info',
                                })
                                    .then(async () => {
                                        loading.value = true;
                                        let result = { success: false, msg: '' };
                                        result = await removeGroups(currInfo.value.id, [row.id].toString());
                                        if (result.success) {
                                            groupListTableConfig.value.tableData.forEach((item, index) => {
                                                if (item.id == row.id) {
                                                    groupListTableConfig.value.tableData.splice(index, 1);
                                                }
                                            });
                                        }
                                        loading.value = false;
                                        ElNotification({
                                            title: result.success ? t('成功') : t('失败'),
                                            message: result.msg,
                                            type: result.success ? 'success' : 'error',
                                            duration: 2000,
                                            offset: 80,
                                        });
                                    })
                                    .catch(() => {
                                        ElMessage({
                                            type: 'info',
                                            message: t('已取消删除'),
                                            offset: 65,
                                        });
                                    });
                                title;
                            },
                        });
                    },
                },
            ],
            tableData: [],
            pageConfig: false, //取消分页
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
                            message: t('请选择人员'),
                            type: 'error',
                            duration: 2000,
                            offset: 80,
                        });
                        reject();
                        return;
                    }

                    await addGroups(currInfo.value.id, orgBaseIds.toString())
                        .then((result) => {
                            ElNotification({
                                title: result.success ? t('成功') : t('失败'),
                                message: result.msg,
                                type: result.success ? 'success' : 'error',
                                duration: 2000,
                                offset: 80,
                            });
                            if (result.success) {
                                getGroupList();
                                resolve();
                            }
                        })
                        .catch(() => {
                            reject();
                        });
                });
            },
        },
        groupTableRef: '',
    });

    let {
        treeApiObj,
        currInfo,
        currentRow,
        groupSelectedData,
        groupListTableConfig,
        loading,
        dialogConfig,
        groupTableRef,
    } = toRefs(data);

    watch(
        () => props.currTreeNodeInfo,
        (newVal, oldVal) => {
            currInfo.value = $deepAssignObject(currInfo.value, newVal); //深度合并
            groupTableRef.value?.elTableRef?.setCurrentRow(); //取消选中状态。

            if (newVal.id !== oldVal.id) {
                getGroupList();
            }
        },
        {
            deep: true,
        }
    );

    onMounted(() => {
        getGroupList();
    });

    defineExpose({
        getGroupList,
    });

    async function getGroupList() {
        let result = await getGroupsByPersonId(currInfo.value.id);
        if (result.success) {
            groupListTableConfig.value.tableData = result.data;
        }
    }

    function onCurrentChange(data) {
        currentRow.value = data;

        if (data) {
            groupSelectedData.value = data.id;
        } else {
            groupSelectedData.value = '';
        }
    }

    async function upGroup() {
        if (currentRow.value == '' || currentRow.value == null) {
            ElNotification({ title: t('失败'), message: t('请选择人员'), type: 'error', duration: 2000, offset: 80 });
            return;
        }
        let tableData = groupListTableConfig.value.tableData;
        tableData.forEach(function (element, index) {
            if (index == 0 && element.id == currentRow.value.id) {
                ElNotification({
                    title: t('失败'),
                    message: t('处于顶端，不能继续上移'),
                    type: 'error',
                    duration: 2000,
                    offset: 80,
                });
                return;
            }
            if (element.id == currentRow.value.id) {
                let obj = tableData[index - 1];
                tableData[index - 1] = currentRow.value;
                tableData[index] = obj;
                groupListTableConfig.value.tableData = tableData;
                return;
            }
        });
    }

    async function downGroup() {
        if (currentRow.value == '' || currentRow.value == null) {
            ElNotification({ title: t('失败'), message: t('请选择人员'), type: 'error', duration: 2000, offset: 80 });
            return;
        }
        let tableData = groupListTableConfig.value.tableData;
        for (let i = 0; i < tableData.length; i++) {
            if (tableData.length - 1 == i && tableData[i].id == currentRow.value.id) {
                ElNotification({
                    title: t('失败'),
                    message: t('处于末端，不能继续下移'),
                    type: 'error',
                    duration: 2000,
                    offset: 80,
                });
                return;
            }
            if (tableData[i].id == currentRow.value.id) {
                let obj = tableData[i + 1];
                tableData[i] = obj;
                tableData[i + 1] = currentRow.value;
                groupListTableConfig.value.tableData = tableData;
                break;
            }
        }
    }
    //保存组排序
    async function saveGroupOrder() {
        const ids = [];
        let tableData = groupListTableConfig.value.tableData;
        tableData.forEach((element) => {
            ids.push(element.id);
        });
        loading.value = true;

        let result = { success: false, msg: '' };
        result = await orderGroups(currInfo.value.id, ids.toString());
        loading.value = false;

        ElNotification({
            title: result.success ? t('成功') : t('失败'),
            message: result.msg,
            type: result.success ? 'success' : 'error',
            duration: 2000,
            offset: 80,
        });
    }

    //添加用户组
    function addGroup() {
        Object.assign(dialogConfig.value, {
            show: true,
            title: computed(() => t('添加用户组')),
            width: '30%',
        });
    }
</script>

<style lang="scss" scoped></style>
