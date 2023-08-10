<!--
 * @Author: hongzhew
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2023-08-03 15:23:01
 * @Description: 岗位基本信息
-->
<template>
    <y9Card :title="`${$t('基本信息')}${currInfo.name ? ' - ' + currInfo.name : ''}`">
        <div class="oth-btns" v-show="currInfo.haveEditAuth">
            <div v-if="isEditState">
                <el-button
                    type="primary"
                    :loading="saveFormBtnLoading"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    :size="fontSizeObj.buttonSize"
                    @click="onActions('save')"
                    class="global-btn-main"
                >
                    <i class="ri-save-line"></i>
                    <span>{{ $t('保存') }}</span>
                </el-button>

                <el-button
                    @click="isEditState = false"
                    class="global-btn-second"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    :size="fontSizeObj.buttonSize"
                >
                    <i class="ri-close-line"></i>
                    <span> {{ $t('取消') }}</span>
                </el-button>
            </div>

            <div v-else style="display: flex; justify-content: space-between; text-align: right">
                <el-button
                    type="primary"
                    @click="onActions('edit')"
                    class="global-btn-main"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    :size="fontSizeObj.buttonSize"
                >
                    <i class="ri-edit-line"></i>
                    <span>{{ $t('编辑') }}</span>
                </el-button>

                <div style="margin-left: 10px">
                    <el-button
                        v-show="showBtn('addDepartment')"
                        type="primary"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        :size="fontSizeObj.buttonSize"
                        class="global-btn-main"
                        @click="
                            onActions(
                                'addDepartment',
                                `${currInfo.orgType == 'Organization' ? '新增部门' : '新增子部门'}`
                            )
                        "
                    >
                        <i class="ri-add-line"></i>
                        <span>{{ currInfo.orgType == 'Organization' ? $t('部门') : $t('子部门') }}</span>
                    </el-button>
                    <el-button
                        v-show="showBtn('addPosition')"
                        type="primary"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        :size="fontSizeObj.buttonSize"
                        class="global-btn-main"
                        @click="onActions('addPosition', '新增岗位')"
                    >
                        <i class="ri-add-line"></i>
                        <span>{{ $t('岗位') }}</span>
                    </el-button>
                    <el-button
                        v-show="showBtn('move')"
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        @click="onActions('move', '移动')"
                        class="global-btn-second"
                    >
                        <i class="ri-route-line"></i>
                        <span>{{ $t('移动') }}</span>
                    </el-button>
                    <el-button
                        v-show="showBtn('sort')"
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        @click="onActions('sort', '综合排序')"
                        class="global-btn-second"
                    >
                        <i class="ri-arrow-up-down-line"></i>
                        <span> {{ $t('排序') }}</span>
                    </el-button>
                    <!-- <el-button v-show="showBtn('positionLeader')" @click="onActions('positionLeader','设置岗位领导')" class="global-btn-second">
						<i class="ri-account-pin-circle-line"></i>
						<span>设置岗位领导</span>
					</el-button> -->
                    <el-button
                        v-show="showBtn('extendAttr')"
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        @click="onActions('extendAttr', '扩展属性')"
                        class="global-btn-second"
                    >
                        <i class="ri-external-link-line"></i>
                        <span>{{ $t('扩展属性') }}</span>
                    </el-button>
                    <el-button
                        v-show="showBtn('sync')"
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        @click="onActions('sync', '数据同步')"
                        class="global-btn-second"
                    >
                        <i class="ri-repeat-line"></i>
                        <span>{{ $t('同步') }}</span>
                    </el-button>
                </div>
            </div>
        </div>

        <div>
            <orgForm
                v-if="currInfo.orgType == 'Organization'"
                ref="orgFormRef"
                :currInfo="currInfo"
                :isEditState="isEditState"
            ></orgForm>
            <departmentForm
                v-if="currInfo.orgType == 'Department'"
                ref="deptFormRef"
                :currInfo="currInfo"
                :isEditState="isEditState"
            ></departmentForm>
            <positionForm
                v-if="currInfo.orgType == 'Position'"
                ref="positionFormRef"
                :currInfo="currInfo"
                :isEditState="isEditState"
            ></positionForm>
        </div>
    </y9Card>

    <!-- 弹窗 -->
    <y9Dialog v-model:config="dialogConfig">
        <departmentForm
            v-if="dialogConfig.type == 'addDepartment'"
            ref="addDeptFormRef"
            :currInfo="currInfo"
            :isEditState="true"
            isAdd
        ></departmentForm>
        <positionForm
            v-if="dialogConfig.type == 'addPosition'"
            ref="addPositionFormRef"
            :currInfo="currInfo"
            :isEditState="true"
            isAdd
        ></positionForm>
        <extendAttr
            v-if="dialogConfig.type == 'extendAttr'"
            :currInfo="currInfo"
            :handAssginNode="handAssginNode"
        ></extendAttr>
        <Sync v-if="dialogConfig.type == 'sync'" ref="syncRef"></Sync>
        <selectTree
            v-if="dialogConfig.type == 'move'"
            :treeApiObj="deptTreeApiObj"
            :selectField="selectField"
            ref="selectTreeRef"
            checkStrictly
            @onCheckChange="onCheckChange"
            @onNodeExpand="onNodeExpand"
        ></selectTree>
        <treeSort
            v-if="dialogConfig.type == 'sort'"
            :currInfo="currInfo"
            :apiRequest="getOrderDepts"
            :apiParams="currInfo.id"
            :columns="dialogConfig.columns"
            type="Position"
            ref="sortRef"
        ></treeSort>
    </y9Dialog>

    <el-button style="display: none" v-loading.fullscreen.lock="loading"></el-button>
</template>
l
<script lang="ts" setup>
    import { inject, watch, reactive, computed, ref, toRefs } from 'vue';
    import { ElNotification } from 'element-plus';
    import { getOrderDepts } from '@/api/dept/index';
    import { $dataType, $keyNameAssign, $deepAssignObject, $deeploneObject } from '@/utils/object';
    import orgForm from '../../org/comps/baseInfoForm/orgForm.vue';
    import departmentForm from '../../org/comps/baseInfoForm/departmentForm.vue';
    import positionForm from '../comps/baseInfoForm/positionForm.vue';
    import extendAttr from '../../org/comps/dialogContent/extendAttr.vue';
    import Sync from '../../org/comps/dialogContent/sync.vue';
    import { orgSaveOrUpdate, treeInterface, getTreeItemById, searchByName, sync } from '@/api/org/index';
    import { deptSaveOrUpdate, moveDept, saveOrder } from '@/api/dept/index';
    import { positionSaveOrUpdate, movePosition } from '@/api/position/index';
    import { getOrgTypeList, listByType } from '@/api/dictionary/index';
    import { useI18n } from 'vue-i18n';
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
        getTreeInstance: Object, //树的实例
    });

    //移动-选择树节点展开时触发
    const selectTreeExpandNode = ref();
    function onNodeExpand(node) {
        selectTreeExpandNode.value = node;
    }

    let selectTreeRef = ref();
    let orgFormRef = ref(); //编辑机构表单实例
    let deptFormRef = ref(); //编辑部门表单实例
    let positionFormRef = ref(); //编辑岗位表单机构
    let addDeptFormRef = ref(); //新增部门表单实例
    let addPositionFormRef = ref(); //新增部门表单实例
    let syncRef = ref(); // 同步表单实例
    let sortRef = ref();

    const data = reactive({
        //当前节点信息
        currInfo: props.currTreeNodeInfo,

        //表单
        isEditState: false, //是否为编辑状态
        saveFormBtnLoading: false, //保存按钮加载状态
        loading: false, // 全局loading
        poForm: {
            name: '',
            description: '',
            duty: '',
            dutyLevel: '',
            dutyType: '',
            dutyLevelName: '',
            jobId: '',
            capacity: 1,
        },
        //弹窗配置
        dialogConfig: {
            show: false,
            title: '',
            type: '',
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {
                    let result = { success: false, msg: '' };
                    if (newConfig.value.type == 'addDepartment') {
                        let formData = addDeptFormRef.value?.y9FormRef?.model;
                        let valid = await addDeptFormRef.value?.y9FormRef?.elFormRef?.validate((valid) => valid); //获取表单验证结果
                        if (!valid) {
                            reject();
                            return;
                        }
                        await deptSaveOrUpdate(formData)
                            .then(async (res) => {
                                result = res;
                                //更新当前节点的children信息到树数据当中
                                await props.handAssginNode({}, currInfo.value.id, currInfo.value.id); //手动更新节点到tree
                            })
                            .catch(() => {});
                    } else if (newConfig.value.type == 'addPosition') {
                        let formData = addPositionFormRef.value?.y9FormRef?.model;
                        let valid = await addPositionFormRef.value?.y9FormRef?.elFormRef?.validate((valid) => valid); //获取表单验证结果
                        if (!valid) {
                            reject();
                            return;
                        }
                        await positionSaveOrUpdate(formData)
                            .then(async (res) => {
                                result = res;
                                //更新当前节点的children信息到树数据当中
                                await props.handAssginNode({}, currInfo.value.id, currInfo.value.id); //手动更新节点到tree
                            })
                            .catch(() => {});
                    } else if (newConfig.value.type == 'move') {
                        const selectIds = selectTreeRef.value?.y9TreeRef?.getCheckedKeys();
                        if (selectIds.length == 0) {
                            ElNotification({
                                title: '失败',
                                message: '请选择部门',
                                type: 'error',
                                duration: 2000,
                                offset: 80,
                            });
                            reject();
                            return;
                        }
                        const targetId = selectIds[0];
                        if (props.currTreeNodeInfo.orgType == 'Department') {
                            await moveDept(props.currTreeNodeInfo.id, targetId)
                                .then((res) => {
                                    result = res;
                                })
                                .catch(() => {});
                        } else if (props.currTreeNodeInfo.orgType == 'Position') {
                            await movePosition(props.currTreeNodeInfo.id, targetId)
                                .then((res) => {
                                    result = res;
                                })
                                .catch(() => {});
                        }

                        //1.删除被移动的节点
                        props.getTreeInstance().remove(currInfo.value);
                        //2.重新请求，移动的目标节点，获得最新的子节点信息，然后手动点击该节点
                        await props.handAssginNode({}, targetId, targetId);
                    } else if (newConfig.value.type == 'sort') {
                        let tableData = sortRef.value.tableConfig.tableData;
                        const ids = [];
                        tableData.forEach((element) => {
                            ids.push(element.id);
                        });
                        await saveOrder(props.currTreeNodeInfo.id, ids.toString())
                            .then(async (res) => {
                                result = res;
                                if (res.success) {
                                    //更新当前节点的children信息到树数据当中
                                    await props.handAssginNode({}, currInfo.value.id, currInfo.value.id); //手动更新节点到tree
                                }
                            })
                            .catch(() => {});
                    } else if (newConfig.value.type == 'sync') {
                        let valid = await syncRef.value?.y9FormRef?.elFormRef?.validate((valid) => valid); //获取表单验证结果

                        if (!valid) {
                            reject();
                            return;
                        }

                        let formData = syncRef.value?.y9FormRef?.model;
                        const params = {
                            syncId: currInfo.value.id,
                            orgType: currInfo.value.orgType,
                            targetSysName: formData.targetSysName,
                            needRecursion: formData.needRecursion,
                        };
                        await sync(params)
                            .then((res) => {
                                result = res;
                            })
                            .catch(() => {});
                    }
                    ElNotification({
                        title: result.success ? '成功' : '失败',
                        message: result.msg,
                        type: result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80,
                    });
                    if (result.success) {
                        resolve();
                    } else {
                        reject();
                    }
                });
            },
            onReset: (newConfig) => {
                if (newConfig.value.type == 'sync') {
                    syncRef.value.y9FormRef.model.targetSysName = '';
                } else if (newConfig.value.type == 'addPosition') {
                    Object.assign(addPositionFormRef.value.y9FormRef.model, poForm.value);
                }
            },
            columns: [] as any,
        },
        deptTreeApiObj: {
            //tree接口对象
            topLevel: treeInterface,
            childLevel: {
                api: async () => {
                    const res = await getTreeItemById({
                        parentId: selectTreeExpandNode.value.id,
                        treeType: 'tree_type_dept',
                        disabled: false,
                    });

                    const data = res.data || [];

                    data.forEach((item) => {
                        //禁止选择移动到自己本身,也禁止选择移动到自身的子节点
                        if (item.id === currInfo.value.id) {
                            item.disabled = true;
                        }
                    });

                    return data;
                },
                params: {},
            },
            search: {
                api: searchByName,
                params: {
                    treeType: 'tree_type_dept',
                },
            },
        },
    });

    let { currInfo, isEditState, saveFormBtnLoading, dialogConfig, deptTreeApiObj, poForm, loading } = toRefs(data);

    const selectField = ref([
        {
            fieldName: 'orgType',
            value: ['Department'],
        },
        {
            fieldName: 'disabled',
            value: false,
        },
    ]);
    watch(
        () => props.currTreeNodeInfo,
        (newVal) => {
            currInfo.value = $deeploneObject(newVal);

            if (currInfo.value.orgType == 'Position') {
                selectField.value[0].value = ['Department'];
            } else {
                selectField.value[0].value = ['Organization', 'Department'];
            }

            isEditState.value = false;
        },
        {
            deep: true,
        }
    );

    //选择树选择框被点击时触发
    const onCheckChange = (node, isChecked) => {
        //已经选择的节点
        const alreadyCheckedNode = selectTreeRef.value?.y9TreeRef?.getCheckedNodes();
        //如果是选中并且存在已经选择的节点超过1个，则取消其他选择，做成单选效果
        if (isChecked && alreadyCheckedNode.length > 1) {
            alreadyCheckedNode.forEach((item) => {
                if (item.id !== node.id) {
                    selectTreeRef.value?.y9TreeRef?.setChecked(item, false, false);
                }
            });
        }
    };

    //显示按钮
    const showBtn = computed(() => {
        return (btnType) => {
            if (btnType == 'addDepartment' || btnType == 'addPosition' || btnType == 'sort') {
                if (currInfo.value.orgType == 'Organization' || currInfo.value.orgType == 'Department') {
                    return true;
                }
            } else if (btnType == 'move') {
                if (currInfo.value.orgType == 'Department' || currInfo.value.orgType == 'Position') {
                    return true;
                }
            } else if (btnType == 'positionLeader') {
                if (currInfo.value.orgType == 'Department') {
                    return true;
                }
            } else if (btnType == 'extendAttr' || btnType == 'sync') {
                if (currInfo.value.orgType == 'Position') {
                    return true;
                }
            }
        };
    });

    //操作按钮
    async function onActions(type, title?) {
        if (type == 'edit') {
            //编辑

            isEditState.value = true;
        } else if (type == 'save') {
            //保存
            loading.value = true;
            saveFormBtnLoading.value = true;

            if (currInfo.value.orgType == 'Organization') {
                let valid = await orgFormRef.value?.y9FormRef?.elFormRef?.validate((valid) => valid); //获取表单验证结果
                if (valid) {
                    const result = await orgSaveOrUpdate(orgFormRef.value?.y9FormRef?.model);
                    ElNotification({
                        title: result.success ? '成功' : '失败',
                        message: result.msg,
                        type: result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80,
                    });
                    if (result.success) {
                        //修改后的数据更新到对应的树数据当中
                        await props.handAssginNode(result.data, currInfo.value.id); //手动更新节点到tree

                        //取消编辑状态
                        isEditState.value = false;
                    }
                }
            } else if (currInfo.value.orgType == 'Department') {
                let valid = await deptFormRef.value?.y9FormRef?.elFormRef?.validate((valid) => valid); //获取表单验证结果
                if (valid) {
                    const result = await deptSaveOrUpdate(deptFormRef.value?.y9FormRef?.model);
                    ElNotification({
                        title: result.success ? '成功' : '失败',
                        message: result.msg,
                        type: result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80,
                    });
                    if (result.success) {
                        //修改后的数据更新到对应的树数据当中
                        await props.handAssginNode(result.data, currInfo.value.id); //手动更新节点信息

                        //取消编辑状态
                        isEditState.value = false;
                    }
                }
            } else if (currInfo.value.orgType == 'Position') {
                let valid = await positionFormRef.value?.y9FormRef?.elFormRef?.validate((valid) => valid); //获取表单验证结果
                if (valid) {
                    const result = await positionSaveOrUpdate(positionFormRef.value?.y9FormRef?.model);
                    ElNotification({
                        title: result.success ? '成功' : '失败',
                        message: result.msg,
                        type: result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80,
                    });
                    if (result.success) {
                        //修改后的数据更新到对应的树数据当中
                        await props.handAssginNode(result.data, currInfo.value.id); //手动更新节点信息

                        //取消编辑状态
                        isEditState.value = false;
                    }
                }
            }

            saveFormBtnLoading.value = false;
            loading.value = false;
        } else if (
            type == 'addDepartment' ||
            type == 'addPosition' ||
            type == 'extendAttr' ||
            type == 'move' ||
            type == 'sync' ||
            type == 'sort'
        ) {
            //点击按钮显示弹窗
            Object.assign(dialogConfig.value, {
                show: true,
                title: computed(() => t(`${title}`)),
                okText: type == 'extendAttr' ? false : computed(() => t('保存')),
                resetText: type == 'addPosition' || type == 'sync' ? computed(() => t('重置')) : false,
                cancelText: type == 'sync' || type == 'extendAttr' ? false : computed(() => t('关闭')),
                width: type == 'move' ? '30%' : '60%',
                type: type,
                columns:
                    type == 'sort'
                        ? [
                              {
                                  type: 'radio',
                                  title: computed(() => t('请选择')),
                                  width: 200,
                              },
                              {
                                  title: computed(() => t('名称')),
                                  key: 'name',
                              },
                              {
                                  title: computed(() => t('类别')),
                                  key: 'orgType',
                              },
                          ]
                        : [],
            });
        }
    }
</script>

<style lang="scss" scoped>
    .oth-btns {
        margin-bottom: 10px;
        :deep(.el-button) {
            margin-bottom: 10px;
        }
    }
</style>
