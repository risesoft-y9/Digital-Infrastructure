<!--
 * @Author: hongzhew
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2023-08-03 15:23:24
 * @Description: 岗位列表
-->
<template>
    <y9Card :title="`${$t('岗位列表')}${currInfo.name ? ' - ' + currInfo.name : ''}`">
        <div
            class="margin-bottom-20"
            v-show="currInfo.haveEditAuth"
            style="display: flex; justify-content: space-between"
        >
            <!-- <el-button
			type="primary"
			class="global-btn-main"
			@click="onAddRelationPosition">
			  	<i class="ri-add-line"></i>
			  	<span>岗位</span>
			</el-button> -->
            <div>
                <el-button
                    type="primary"
                    @click="savePositionOrder"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-main"
                >
                    <i class="ri-save-line"></i>
                    <span>{{ $t('保存') }}</span>
                </el-button>
            </div>
            <div>
                <el-button
                    @click="upPosition"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-second"
                >
                    <i class="ri-arrow-up-line"></i>
                    <span>{{ $t('上移') }}</span>
                </el-button>
                <el-button
                    @click="downPosition"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-second"
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
        >
        </y9Table>
    </y9Card>
    <el-button style="display: none" v-loading.fullscreen.lock="loading"></el-button>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { inject, watch, reactive, computed, h, onMounted, ref, toRefs } from 'vue';
    import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
    import { $deepAssignObject, $dataType } from '@/utils/object';
    import { getPositionsByParentId, removePosition, saveOrder } from '@/api/position/index';
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

        handAssginNode: Function, //手动更新节点信息
    });

    const data = reactive({
        //当前节点信息
        currInfo: props.currTreeNodeInfo,
        loading: false, // 全局loading
        //表格配置
        positionListTableConfig: {
            columns: [
                {
                    type: 'radio',
                    width: 80,
                },
                {
                    title: computed(() => t('名称')),
                    key: 'name',
                    width: 200,
                },
                {
                    title: computed(() => t('全路径')),
                    key: 'dn',
                },
                {
                    title: computed(() => t('操作')),
                    fixed: 'right',
                    width: 120,
                    showOverflowTooltip: false,
                    render: (row) => {
                        return h(
                            'span',
                            {
                                class: 'flex-center',
                                onClick: () => {
                                    ElMessageBox.confirm(`${t('是否删除岗位')}【${row.name}】?`, t('提示'), {
                                        confirmButtonText: t('确定'),
                                        cancelButtonText: t('取消'),
                                        type: 'info',
                                    })
                                        .then(async () => {
                                            loading.value = true;
                                            let result = await removePosition([row.id].toString());
                                            loading.value = false;
                                            if (result.success) {
                                                positionListTableConfig.value.tableData.forEach((item, index) => {
                                                    if (item.id == row.id) {
                                                        positionListTableConfig.value.tableData.splice(index, 1);
                                                    }
                                                });

                                                props.handAssginNode({}, currInfo.value.id, currInfo.value.id); //手动更新节点到tree
                                            }
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
                                                message: t('已取消删除岗位'),
                                                offset: 65,
                                            });
                                        });
                                },
                            },
                            [
                                h('i', {
                                    class: 'ri-delete-bin-line',
                                    style: {
                                        marginRight: '4px',
                                    },
                                }),
                            ]
                        );
                    },
                },
            ],
            tableData: [],
            pageConfig: false, //取消分页
        },
        currentRow: '',
        positionSelectedData: '',
        positionTableRef: '',
        tabIndexs: [],
    });

    let { currInfo, positionListTableConfig, currentRow, tabIndexs, loading, positionSelectedData, positionTableRef } =
        toRefs(data);

    watch(
        () => props.currTreeNodeInfo,
        (newVal) => {
            currInfo.value = $deepAssignObject(currInfo.value, newVal);
            positionTableRef.value?.elTableRef?.setCurrentRow(); //取消选中状态。
            getPositionsList();
        },
        {
            deep: true,
        }
    );

    onMounted(() => {
        getPositionsList();
    });

    async function getPositionsList() {
        let result = await getPositionsByParentId(currInfo.value.id);
        if (result.success) {
            tabIndexs.value = [];
            result.data.forEach((element) => {
                tabIndexs.value.push(element.tabIndex);
            });
            positionListTableConfig.value.tableData = result.data;
        }
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
                    offset: 80,
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
                    offset: 80,
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

    async function savePositionOrder() {
        const ids = [];
        let tableData = positionListTableConfig.value.tableData;
        tableData.forEach((element) => {
            ids.push(element.id);
        });
        loading.value = true;
        let result = await saveOrder(ids.toString(), tabIndexs.value.toString());
        loading.value = false;
        if (result.success) {
            props.handAssginNode({}, currInfo.value.id, currInfo.value.id); //手动更新节点到tree
        }
        ElNotification({
            title: result.success ? t('成功') : t('失败'),
            message: result.msg,
            type: result.success ? 'success' : 'error',
            duration: 2000,
            offset: 80,
        });
    }
</script>

<style lang="scss" scoped></style>
