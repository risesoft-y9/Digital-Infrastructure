<!--
 * @Author: hongzhew
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2023-08-03 15:22:36
 * @Description: 组织岗位-人员列表
-->
<template>
    <y9Card :title="`${$t('人员列表')}${currInfo.name ? ' - ' + currInfo.name : ''}`">
        <div class="margin-bottom-20" style="display: flex; justify-content: space-between">
            <div>
                <el-button
                    @click="addPerson"
                    type="primary"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-main"
                >
                    <i class="ri-add-line"></i>
                    <span>{{ $t('选择已有人员') }}</span>
                </el-button>
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    type="primary"
                    @click="savePersonOrder"
                    class="global-btn-main"
                >
                    <i class="ri-save-line"></i>
                    <span>{{ $t('保存') }}</span>
                </el-button>
            </div>
            <div>
                <el-button
                    @click="upPerson"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-second"
                >
                    <i class="ri-arrow-up-line"></i>
                    <span>{{ $t('上移') }}</span>
                </el-button>
                <el-button
                    @click="downPerson"
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
            ref="personTableRef"
            v-model:selectedVal="userSelectedData"
            :config="personListTableConfig"
            @on-current-change="onCurrentChange"
        ></y9Table>
    </y9Card>
    <y9Dialog v-model:config="dialogConfig">
        <selectTree
            ref="selectTreeRef"
            :treeApiObj="treeApiObj"
            :selectField="[
                { fieldName: 'orgType', value: ['Person'] },
                { fieldName: 'disabled', value: false },
            ]"
            @onNodeExpand="onNodeExpand"
        >
        </selectTree>
    </y9Dialog>
    <el-button style="display: none" v-loading.fullscreen.lock="loading"></el-button>
</template>

<script lang="ts" setup>
    import { inject, watch, reactive, computed, h, onMounted, ref, toRefs } from 'vue';
    import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
    import { getJobList } from '@/api/dictionary/index';
    import { $dictionary, $dictionaryFunc } from '@/utils/data';
    import { useI18n } from 'vue-i18n';
    import { $deepAssignObject } from '@/utils/object';
    import { getPersonsByPositionId } from '@/api/person/index';
    import { removePersons, addPersons, orderPersons } from '@/api/position/index';
    import { treeInterface, getTreeItemById, searchByName } from '@/api/org/index';
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

    const selectTreeRef = ref();

    //移动-选择树节点展开时触发
    const selectTreeExpandNode = ref();
    function onNodeExpand(node) {
        selectTreeExpandNode.value = node;
    }

    const data = reactive({
        loading: false, // 全局loading
        treeApiObj: {
            //tree接口对象
            topLevel: treeInterface,
            childLevel: {
                api: async () => {
                    const res = await getTreeItemById({
                        parentId: selectTreeExpandNode.value.id,
                        treeType: 'tree_type_person',
                        disabled: false,
                    });

                    const data = res.data || [];

                    const ids = personListTableConfig.value.tableData.map((item) => item.id);

                    //禁止选择已经存在的人员
                    data.forEach((item) => {
                        if (ids.includes(item.id)) {
                            item.disabled = true;
                            item.disabledRemark = '[已存在]';
                        }
                    });

                    return data;
                },
                params: { treeType: 'tree_type_person', disabled: false },
            },
            search: {
                api: searchByName,
                params: {
                    treeType: 'tree_type_org',
                },
            },
        },

        //当前节点信息
        currInfo: props.currTreeNodeInfo,
        personListTableConfig: {
            //人员列表表格配置
            columns: [
                {
                    type: 'radio',
                    width: 80,
                },
                {
                    title: computed(() => t('姓名')),
                    key: 'name',
                    width: 200,
                },
                {
                    title: computed(() => t('性别')),
                    key: 'sex',
                    width: 120,
                },
                {
                    title: computed(() => t('职务')),
                    key: 'duty',
                    width: 300,
                },
                {
                    title: computed(() => t('所属部门')),
                    key: 'dn',
                },
                {
                    title: computed(() => t('操作')),
                    width: 120,
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
                                        let result = await removePersons(currInfo.value.id, [row.id].toString());
                                        loading.value = false;
                                        if (result.success) {
                                            personListTableConfig.value.tableData.forEach((item, index) => {
                                                if (item.id == row.id) {
                                                    personListTableConfig.value.tableData.splice(index, 1);
                                                }
                                            });
                                            let name = `${currInfo.value.jobName}  (空缺)`;
                                            if (personListTableConfig.value.tableData.length > 0) {
                                                name = `${
                                                    currInfo.value.jobName
                                                }  (${personListTableConfig.value.tableData
                                                    .map((item) => item.name)
                                                    .join('，')})`;
                                            }
                                            props.handAssginNode({ name: name }, currInfo.value.id); //手动更新节点信息
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
                                            message: t('已取消删除'),
                                            offset: 65,
                                        });
                                    });
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
                    const selectData = selectTreeRef.value?.y9TreeRef?.getCheckedNodes(true);
                    let orgBaseIds = selectData.map((item) => item.id);
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
                    await addPersons(currInfo.value.id, orgBaseIds.toString())
                        .then((result) => {
                            if (result.success) {
                                let data = personListTableConfig.value.tableData;
                                data = data.concat(selectData);

                                const name = `${currInfo.value.jobName}  (${data.map((item) => item.name).join('，')})`;
                                props.handAssginNode({ name: name }, currInfo.value.id); //手动更新节点信息
                            }
                            ElNotification({
                                title: result.success ? t('成功') : t('失败'),
                                message: result.msg,
                                type: result.success ? 'success' : 'error',
                                duration: 2000,
                                offset: 80,
                            });
                            resolve();
                        })
                        .catch(() => {
                            reject();
                        });
                });
            },
        },
        currentRow: '',
        userSelectedData: '',
        personTableRef: '',
    });

    let {
        treeApiObj,
        currInfo,
        personListTableConfig,
        dialogConfig,
        currentRow,
        loading,
        userSelectedData,
        personTableRef,
    } = toRefs(data);

    watch(
        () => props.currTreeNodeInfo,
        (newVal) => {
            currInfo.value = $deepAssignObject(currInfo.value, newVal);
            //加入职位名称字段
            currInfo.value.jobName = getJobName(currInfo.value.jobId);

            //取消选中状态。
            personTableRef.value?.elTableRef?.setCurrentRow();

            //获取人员列表
            getPersonsList();
        },
        {
            deep: true,
        }
    );

    onMounted(async () => {
        await $dictionaryFunc('jobList', getJobList); //获取职位列表

        //加入职位名称字段
        currInfo.value.jobName = getJobName(currInfo.value.jobId);

        //获取人员列表
        getPersonsList();
    });

    async function getPersonsList() {
        let result = await getPersonsByPositionId(currInfo.value.id);
        if (result.success) {
            result.data.forEach((element) => {
                if (element.sex == 1) {
                    element.sex = '男';
                } else if (element.sex == 0) {
                    element.sex = '女';
                }
            });
            personListTableConfig.value.tableData = result.data;
        }
    }

    //根据职位id获取职位名称
    function getJobName() {
        let name = '';
        const jobList = $dictionary().jobList;
        if (jobList && jobList.length > 0) {
            jobList.forEach((item) => {
                if (item.originalId === currInfo.value.jobId) {
                    name = item.name;
                }
            });
        }
        return name;
    }

    //添加人员
    function addPerson() {
        Object.assign(dialogConfig.value, {
            show: true,
            title: computed(() => t('添加人员')),
        });
    }

    function onCurrentChange(data) {
        currentRow.value = data;
        if (data) {
            userSelectedData.value = data.id;
        } else {
            userSelectedData.value = '';
        }
    }

    async function upPerson() {
        if (currentRow.value == '' || currentRow.value == null) {
            ElNotification({ title: t('失败'), message: t('请选择人员'), type: 'error', duration: 2000, offset: 80 });
            return;
        }
        let tableData = personListTableConfig.value.tableData;
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
                personListTableConfig.value.tableData = tableData;
                return;
            }
        });
    }

    async function downPerson() {
        if (currentRow.value == '' || currentRow.value == null) {
            ElNotification({ title: t('失败'), message: t('请选择人员'), type: 'error', duration: 2000, offset: 80 });
            return;
        }
        let tableData = personListTableConfig.value.tableData;
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
                personListTableConfig.value.tableData = tableData;
                break;
            }
        }
    }

    async function savePersonOrder() {
        const ids = [];
        let tableData = personListTableConfig.value.tableData;
        tableData.forEach((element) => {
            ids.push(element.id);
        });
        loading.value = true;
        let result = await orderPersons(props.currTreeNodeInfo.id, ids.toString());
        loading.value = false;
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
