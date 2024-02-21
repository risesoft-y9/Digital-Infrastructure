<!--
 * @Author: fuyu
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2024-01-12 09:32:46
 * @Description: 组织架构-设置部门领导
-->
<template>
    <y9Card :title="`${$t('设置部门领导')}${currInfo.name ? ' - ' + currInfo.name : ''}`">
        <div v-show="currInfo.haveEditAuth" class="margin-bottom-20">
            <el-button
                :size="fontSizeObj.buttonSize"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                class="global-btn-main"
                type="primary"
                @click="setLeader"
            >
                <i class="ri-add-line"></i>
                <span>{{ $t('部门领导') }}</span>
            </el-button>
        </div>

        <y9Table :config="setLeaderTableConfig"></y9Table>
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
    import { getDeptLeaders, removeLeader, setDeptLeaders } from '@/api/dept/index';
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
        setLeaderTableConfig: {
            //设置部门领导表格配置
            columns: [
                {
                    title: computed(() => t('姓名')),
                    key: 'name'
                },
                {
                    title: computed(() => t('类别')),
                    render: (row) => {
                        if (row.orgType == 'Position') {
                            return '岗位';
                        } else if (row.orgType == 'Person') {
                            return '人员';
                        }
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
                                            let result = await removeLeader(currInfo.value.id, row.id);
                                            loading.value = false;
                                            if (result.success) {
                                                setLeaderTableConfig.value.tableData.forEach((item, index) => {
                                                    if (item.id == row.id) {
                                                        setLeaderTableConfig.value.tableData.splice(index, 1);
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
                            message: t('请选择人员'),
                            type: 'error',
                            duration: 2000,
                            offset: 80
                        });
                        reject();
                        return;
                    }

                    let result = await setDeptLeaders(currInfo.value.id, orgBaseIds.toString());
                    ElNotification({
                        title: result.success ? t('成功') : t('失败'),
                        message: result.msg,
                        type: result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    if (result.success) {
                        getDeptLeaderList();
                    }
                    resolve();
                });
            },
            visibleChange: (visible) => {}
        }
    });

    let { treeApiObj, positionTreeApiObj, currInfo, setLeaderTableConfig, dialogConfig, loading } = toRefs(data);

    watch(
        () => props.currTreeNodeInfo,
        (newVal) => {
            currInfo.value = $deepAssignObject(currInfo.value, newVal);

            getDeptLeaderList();
        },
        {
            deep: true
        }
    );
    onMounted(() => {
        getDeptLeaderList();
    });

    async function getDeptLeaderList() {
        let res = await getDeptLeaders(props.currTreeNodeInfo.id);
        if (res.success) {
            setLeaderTableConfig.value.tableData = res.data;
        }
    }

    async function setLeader() {
        Object.assign(dialogConfig.value, {
            show: true,
            title: computed(() => t('设置部门领导')),
            resetText: false,
            cancelText: computed(() => t('取消'))
        });
    }
</script>

<style lang="scss" scoped></style>
