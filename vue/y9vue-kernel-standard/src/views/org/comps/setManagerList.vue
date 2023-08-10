<!--
 * @Author: hongzhew
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2023-08-03 15:25:40
 * @Description: 设置主管领导
-->
<template>
    <y9Card :title="`${$t('设置主管领导')}${currInfo.name ? ' - ' + currInfo.name : ''}`">
        <div class="margin-bottom-20" v-show="currInfo.haveEditAuth">
            <el-button
                @click="setManager"
                class="global-btn-main"
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                type="primary"
            >
                <i class="ri-add-line"></i>
                <span>{{ $t('主管领导') }}</span>
            </el-button>
        </div>
        <y9Table :config="setManagerTableConfig"></y9Table>
        <y9Dialog v-model:config="dialogConfig">
            <selectTree
                ref="selectTreeRef"
                :treeApiObj="typeName == 'org' ? treeApiObj : positionTreeApiObj"
                :selectField="
                    typeName == 'org'
                        ? [
                              { fieldName: 'orgType', value: ['Person'] },
                              { fieldName: 'disabled', value: false },
                          ]
                        : [
                              { fieldName: 'orgType', value: ['Position'] },
                              { fieldName: 'disabled', value: false },
                          ]
                "
            ></selectTree>
        </y9Dialog>
    </y9Card>
    <el-button style="display: none" v-loading.fullscreen.lock="loading"></el-button>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { inject, watch, reactive, computed, h, onMounted, ref, toRefs } from 'vue';
    import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
    import { $deepAssignObject } from '@/utils/object';
    import { treeInterface, getTreeItemById, searchByName } from '@/api/org/index';
    import { setDeptManagers, getManagers, removeManager } from '@/api/dept/index';
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const { t } = useI18n();
    const props = defineProps({
        currTreeNodeInfo: {
            //当前tree节点信息
            type: Object,
            default: () => {
                return {};
            },
        },

        typeName: {
            type: String,
            default: '',
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
                params: { treeType: 'tree_type_person', disabled: false },
            },
            search: {
                api: searchByName,
                params: {
                    treeType: 'tree_type_org_person',
                },
            },
        },
        positionTreeApiObj: {
            //岗位tree接口对象
            topLevel: treeInterface,
            childLevel: {
                api: getTreeItemById,
                params: { treeType: 'tree_type_position', disabled: false },
            },
            search: {
                api: searchByName,
                params: {
                    treeType: 'tree_type_org_position',
                },
            },
        },
        loading: false, // 全局loading
        //当前节点信息
        currInfo: props.currTreeNodeInfo,
        setManagerTableConfig: {
            columns: [
                {
                    title: computed(() => t('姓名')),
                    key: 'name',
                },
                {
                    title: computed(() => t('类别')),
                    render: (row) => {
                        if (row.orgType == 'Position') {
                            return '岗位';
                        } else if (row.orgType == 'Person') {
                            return '人员';
                        }
                    },
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
                                        type: 'info',
                                    })
                                        .then(async () => {
                                            loading.value = true;
                                            let result = await removeManager(currInfo.value.id, row.id);
                                            loading.value = false;
                                            if (result.success) {
                                                setManagerTableConfig.value.tableData.forEach((item, index) => {
                                                    if (item.id == row.id) {
                                                        setManagerTableConfig.value.tableData.splice(index, 1);
                                                    }
                                                });
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
                                                message: t('已取消移除'),
                                                offset: 65,
                                            });
                                        });
                                },
                            },
                            t('移除')
                        );
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
                    await setDeptManagers(currInfo.value.id, orgBaseIds.toString())
                        .then((result) => {
                            ElNotification({
                                title: result.success ? t('成功') : t('失败'),
                                message: result.msg,
                                type: result.success ? 'success' : 'error',
                                duration: 2000,
                                offset: 80,
                            });
                            if (result.success) {
                                getManagersList();
                            }
                            resolve();
                        })
                        .catch(() => {
                            reject();
                        });
                });
            },
            visibleChange: (visible) => {},
        },
    });

    let { treeApiObj, positionTreeApiObj, currInfo, setManagerTableConfig, dialogConfig, loading } = toRefs(data);

    watch(
        () => props.currTreeNodeInfo,
        (newVal) => {
            currInfo.value = $deepAssignObject(currInfo.value, newVal);
            getManagersList();
        },
        {
            deep: true,
        }
    );

    onMounted(() => {
        getManagersList();
    });

    async function getManagersList() {
        let res = await getManagers(props.currTreeNodeInfo.id);
        if (res.success) {
            setManagerTableConfig.value.tableData = res.data;
        }
    }

    async function setManager() {
        Object.assign(dialogConfig.value, {
            show: true,
            title: computed(() => t('设置主管领导')),
            resetText: false,
            cancelText: computed(() => t('取消')),
        });
    }
</script>

<style lang="scss" scoped></style>
