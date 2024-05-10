<!--
 * @Author: fuyu
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2024-01-11 17:32:40
 * @Description: 组织架构-岗位列表
-->
<template>
    <y9Card :title="`${$t('岗位列表')}${currInfo.name ? ' - ' + currInfo.name : ''}`">
        <div class="margin-bottom-20" style="display: flex; justify-content: space-between">
            <div>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-main"
                    type="primary"
                    @click="onAddPosition"
                >
                    <i class="ri-add-line"></i>
                    <span>{{ $t('岗位') }}</span>
                </el-button>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-main"
                    type="primary"
                    @click="saveOrder"
                >
                    <i class="ri-save-line"></i>
                    <span>{{ $t('保存') }}</span>
                </el-button>
            </div>
            <div>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-second"
                    @click="upPosition"
                >
                    <i class="ri-arrow-up-line"></i>
                    <span>{{ $t('上移') }}</span>
                </el-button>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-second"
                    @click="downPosition"
                >
                    <i class="ri-arrow-down-line"></i>
                    <span>{{ $t('下移') }}</span>
                </el-button>
            </div>
        </div>
        <y9Table
            ref="positionTableRef"
            v-model:selectedVal="positionSelectedData"
            :config="positionListTableConfig"
            @on-current-change="onCurrentChange"
        ></y9Table>
    </y9Card>
    <y9Dialog v-model:config="dialogConfig">
        <selectTree
            ref="selectTreeRef"
            :selectField="[
                { fieldName: 'nodeType', value: ['Position'] },
                { fieldName: 'disabled', value: false }
            ]"
            :treeApiObj="treeApiObj"
        ></selectTree>
    </y9Dialog>
    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import { computed, h, inject, onMounted, reactive, ref, toRefs, watch } from 'vue';
    import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
    import { getJobList } from '@/api/dictionary/index';
    import { $dictionary, $dictionaryFunc } from '@/utils/data';
    import { useI18n } from 'vue-i18n';
    import { $deepAssignObject } from '@/utils/object';
    import { getPositionsByPersonId, orderPositions } from '@/api/position/index';
    import { getTreeItemById, searchByName, treeInterface } from '@/api/org/index';
    import { addPositions, removePositions } from '@/api/person/index';

    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const props = defineProps({
        currTreeNodeInfo: {
            //当前tree节点信息
            type: Object,
            default: () => {
                return {};
            }
        }
    });
    //选择tree实例
    const selectTreeRef = ref();
    const positionTableRef = ref();
    const data = reactive({
        treeApiObj: {
            //tree接口对象
            topLevel: treeInterface,
            childLevel: {
                api: getTreeItemById,
                params: { treeType: 'tree_type_position', disabled: false }
            },
            search: {
                api: searchByName,
                params: {
                    treeType: 'tree_type_org'
                }
            }
        },
        loading: false, // 全局loading
        //当前节点信息
        currInfo: props.currTreeNodeInfo,
        positionSelectedData: '', //选中的岗位数据
        positionListTableConfig: {
            //岗位列表表格配置
            columns: [
                {
                    type: 'radio',
                    width: 80
                },
                {
                    title: computed(() => t('名称')),
                    key: 'name',
                    width: 300,
                    render: (row) => {
                        return h('span', row.name);
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
                    width: 100,
                    render: (row) => {
                        return h('i', {
                            class: 'ri-delete-bin-line',
                            onClick: () => {
                                ElMessageBox.confirm(`${t('是否删除')}【${row.name}】?`, t('提示'), {
                                    confirmButtonText: t('确定'),
                                    cancelButtonText: t('取消'),
                                    type: 'info'
                                })
                                    .then(async () => {
                                        loading.value = true;
                                        let result = await removePositions(currInfo.value.id, row.id);
                                        loading.value = false;
                                        if (result.success) {
                                            positionListTableConfig.value.tableData.forEach((item, index) => {
                                                if (item.id == row.id) {
                                                    positionListTableConfig.value.tableData.splice(index, 1);
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
                                            message: t('已取消删除'),
                                            offset: 65
                                        });
                                    });
                            }
                        });
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
                    let result = { success: false, msg: '' };

                    let orgBaseIds = selectTreeRef.value?.y9TreeRef?.getCheckedKeys(true);
                    if (orgBaseIds.length == 0) {
                        ElNotification({
                            title: t('失败'),
                            message: t('请选择岗位'),
                            type: 'error',
                            duration: 2000,
                            offset: 80
                        });
                        reject();
                        return;
                    }
                    result = await addPositions(currInfo.value.id, orgBaseIds.toString());
                    ElNotification({
                        title: result.success ? t('成功') : t('失败'),
                        message: result.msg,
                        type: result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    if (result.success) {
                        getPositionsList();
                    }
                    resolve();
                });
            },
            visibleChange: (visible) => {}
        },
        currentRow: ''
    });

    let { treeApiObj, currInfo, positionListTableConfig, dialogConfig, currentRow, loading, positionSelectedData } =
        toRefs(data);

    watch(
        () => props.currTreeNodeInfo,
        (newVal, oldVal) => {
            currInfo.value = $deepAssignObject(currInfo.value, newVal); //深度合并

            positionTableRef.value?.elTableRef?.setCurrentRow(); //取消选中状态。

            if (newVal.id !== oldVal.id) {
                getPositionsList();
            }
        },
        {
            deep: true
        }
    );

    onMounted(async () => {
        await $dictionaryFunc('jobList', getJobList); //获取职位列表

        getPositionsList();
    });

    //根据职位id获取职位名称
    function getJobName(jobId) {
        let name = '';
        const jobList = $dictionary().jobList;
        if (jobList && jobList.length > 0) {
            jobList.forEach((item) => {
                if (item.originalId === jobId) {
                    name = item.name;
                }
            });
        }
        return name;
    }

    async function getPositionsList() {
        let result = await getPositionsByPersonId(currInfo.value.id);

        const data = result.data;

        // data.forEach((item) => {
        //     item.jobName = getJobName(item.jobId);
        //     item.name = `${item.jobName}（${currInfo.value.name}）`;
        // });
        if (result.success) {
            positionListTableConfig.value.tableData = data;
        }
    }

    //增加岗位
    function onAddPosition() {
        Object.assign(dialogConfig.value, {
            show: true,
            title: computed(() => t('添加岗位'))
        });
    }

    function onCurrentChange(data) {
        currentRow.value = data;
        if (data) {
            positionSelectedData.value = data.id;
        } else {
            positionSelectedData.value = '';
        }
    }

    async function upPosition() {
        if (currentRow.value == '' || currentRow.value == null) {
            ElNotification({ title: t('失败'), message: t('请选择岗位'), type: 'error', duration: 2000, offset: 80 });
            return;
        }
        let tableData = positionListTableConfig.value.tableData;
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
                positionListTableConfig.value.tableData = tableData;
                return;
            }
        });
    }

    async function downPosition() {
        if (currentRow.value == '' || currentRow.value == null) {
            ElNotification({ title: t('失败'), message: t('请选择岗位'), type: 'error', duration: 2000, offset: 80 });
            return;
        }
        let tableData = positionListTableConfig.value.tableData;
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
                positionListTableConfig.value.tableData = tableData;
                break;
            }
        }
    }

    async function saveOrder() {
        const ids = [];
        let tableData = positionListTableConfig.value.tableData;
        tableData.forEach((element) => {
            ids.push(element.id);
        });
        loading.value = true;
        let result = await orderPositions(props.currTreeNodeInfo.id, ids.toString());
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

<style lang="scss" scoped></style>
