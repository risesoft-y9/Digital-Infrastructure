<!--
 * @Author: fuyu
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2025-12-24 09:41:25
 * @Description: 组织架构-基本信息
-->
<template>
    <y9Card :headerPadding="false">
        <template #header>
            <div :style="{ 'padding-bottom': currInfo.haveEditAuth ? '0px' : '16px' }" class="slot-header">
                <span>{{ $t('基本信息') }}{{ currInfo.name ? ' - ' + currInfo.name : '' }}</span>

                <div v-show="currInfo.haveEditAuth" class="expand-btns-div">
                    <div v-show="isExpandBtns" class="expand-btns">
                        <el-button
                            :size="fontSizeObj.buttonSize"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            class="global-btn-second"
                            @click="onActions('extendAttr', '扩展属性')"
                        >
                            <i class="ri-external-link-line"></i>
                            <span>{{ $t('扩展') }}</span>
                        </el-button>

                        <el-button
                            :size="fontSizeObj.buttonSize"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            class="global-btn-second"
                            @click="onActions('sync', '数据同步')"
                        >
                            <i class="ri-repeat-line"></i>
                            <span>{{ $t('同步') }}</span>
                        </el-button>

                        <el-button
                            v-show="showBtn('move')"
                            :size="fontSizeObj.buttonSize"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            class="global-btn-second"
                            @click="onActions('move', '移动')"
                        >
                            <i class="ri-route-line"></i>
                            <span>{{ $t('移动') }}</span>
                        </el-button>

                        <el-button
                            v-show="showBtn('sort')"
                            :size="fontSizeObj.buttonSize"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            class="global-btn-second"
                            @click="onActions('sort', '综合排序')"
                        >
                            <i class="ri-arrow-up-down-line"></i>
                            <span> {{ $t('排序') }}</span>
                        </el-button>

                        <el-button
                            v-show="showBtn('disabled')"
                            :size="fontSizeObj.buttonSize"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            class="global-btn-second"
                            @click="onActions('disabled', currInfo.disabled ? '取消禁用' : '禁用')"
                        >
                            <i class="ri-user-unfollow-line"></i>
                            <span>{{ currInfo.disabled ? $t('取消禁用') : $t('禁用') }}</span>
                        </el-button>

                        <!-- 	<el-button class="global-btn-second" v-show="showBtn('unlock')" @click="onActions('unlock','账号解锁')">
							<i class="ri-lock-unlock-line"></i>
							<span>账号解锁</span>
						</el-button> -->

                        <el-button
                            v-show="showBtn('resetPassword')"
                            :size="fontSizeObj.buttonSize"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            class="global-btn-second"
                            @click="onActions('resetPassword', '重置当前人员密码为默认密码')"
                        >
                            <i class="ri-refresh-line"></i>
                            <span>{{ $t('重置密码') }}</span>
                        </el-button>

                        <!-- <el-button class="global-btn-second" v-show="showBtn('updateIcon')" @click="onActions('updateIcon','更新图标')">
							<i class="ri-refresh-line"></i>
							<span>更新图标</span>
						</el-button> -->

                        <el-button
                            v-show="showBtn('exportJSON')"
                            :size="fontSizeObj.buttonSize"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            class="global-btn-second"
                            @click="onActions('exportJSON')"
                        >
                            <i class="ri-file-upload-line"></i>
                            <span>{{ $t('导出JSON') }}</span>
                        </el-button>

                        <el-button
                            v-show="showBtn('importPersonXLS')"
                            :size="fontSizeObj.buttonSize"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            class="global-btn-second"
                            @click="onActions('importPersonXLS', '导入组织架构XLS')"
                        >
                            <i class="ri-file-upload-line"></i>
                            <span>{{ $t('导入XLS') }}</span>
                        </el-button>

                        <el-button
                            v-show="showBtn('exportXls')"
                            :size="fontSizeObj.buttonSize"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            class="global-btn-second"
                            @click="onActions('exportXls')"
                        >
                            <i class="ri-file-upload-line"></i>
                            <span v-if="currInfo.orgType == 'Organization'">{{ $t('导出XLS') }}</span>
                            <span v-if="currInfo.orgType == 'Department'">{{ $t('导出XLS') }}</span>
                            <span v-if="currInfo.orgType == 'Person'">{{ $t('导出XLS') }}</span>
                        </el-button>
                    </div>

                    <i
                        :class="isExpandBtns ? 'ri-checkbox-indeterminate-line' : 'ri-add-box-line'"
                        @click="isExpandBtnsFunc('click')"
                    ></i>
                </div>
            </div>
        </template>
        <div v-show="currInfo.haveEditAuth" class="oth-btns">
            <div v-if="isEditState">
                <el-button
                    :loading="saveFormBtnLoading"
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-main"
                    type="primary"
                    @click="onActions('save')"
                >
                    <i class="ri-save-line"></i>
                    <span>{{ $t('保存') }}</span>
                </el-button>

                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-second"
                    @click="isEditState = false"
                >
                    <i class="ri-close-line"></i>
                    <span> {{ $t('取消') }}</span>
                </el-button>
            </div>

            <div v-else style="display: flex; justify-content: space-between; text-align: right">
                <el-button
                    :size="fontSizeObj.buttonSize"
                    :style="{ fontSize: fontSizeObj.baseFontSize }"
                    class="global-btn-main"
                    type="primary"
                    @click="onActions('edit')"
                >
                    <i class="ri-edit-line"></i>
                    <span>{{ $t('编辑') }}</span>
                </el-button>

                <div
                    v-show="currInfo.orgType == 'Organization' || currInfo.orgType == 'Department'"
                    style="margin-left: 10px"
                >
                    <el-button
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        class="global-btn-main"
                        type="primary"
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
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        class="global-btn-main"
                        type="primary"
                        @click="onActions('addPerson', '新增人员')"
                    >
                        <i class="ri-add-line"></i>
                        <span>{{ $t('人员') }}</span>
                    </el-button>
                    <!-- <el-button
                        type="primary"
                        class="global-btn-main"
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        @click="onActions('selectPerson', '选择已有人员')"
                    >
                        <i class="ri-add-line"></i>
                        <span>{{ $t('选择已有人员') }}</span>
                    </el-button> -->
                    <el-button
                        :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize }"
                        class="global-btn-main"
                        type="primary"
                        @click="onActions('addGroup', '新增用户组')"
                    >
                        <i class="ri-add-line"></i>
                        <span>{{ $t('用户组') }}</span>
                    </el-button>
                    <!-- <el-button type="primary" class="global-btn-main" @click="onActions('addPosition','新增岗位')">
						<i class="ri-add-line"></i>
						<span>新增岗位</span>
					</el-button> -->
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
            <personForm
                v-if="currInfo.orgType == 'Person'"
                ref="personFormRef"
                :currInfo="currInfo"
                :isEditState="isEditState"
            ></personForm>
            <groupForm
                v-if="currInfo.orgType == 'Group'"
                ref="groupFormRef"
                :currInfo="currInfo"
                :isEditState="isEditState"
            ></groupForm>
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
        <addPersonForm
            v-if="dialogConfig.type == 'addPerson'"
            ref="addPersonFormRef"
            :currInfo="currInfo"
            :step="addPersonStep"
        ></addPersonForm>
        <groupForm
            v-if="dialogConfig.type == 'addGroup'"
            ref="addGroupFormRef"
            :currInfo="currInfo"
            :isEditState="true"
            isAdd
        ></groupForm>
        <extendAttr
            v-if="dialogConfig.type == 'extendAttr'"
            :currInfo="currInfo"
            :handAssginNode="handAssginNode"
        ></extendAttr>
        <Sync v-if="dialogConfig.type == 'sync'" ref="syncRef"></Sync>
        <uploadOrgInfo
            v-if="dialogConfig.type == 'importPersonXLS'"
            :id="currInfo.id"
            :refresh="refreshTree"
            type="xls"
            @update="dialogConfig.show = false"
        ></uploadOrgInfo>
        <selectTree
            v-if="dialogConfig.type == 'selectPerson'"
            ref="selectPersonTreeRef"
            :selectField="[
                { fieldName: 'orgType', value: ['Person'] },
                { fieldName: 'disabled', value: false }
            ]"
            :treeApiObj="treeApiObj"
        ></selectTree>
        <selectTree
            v-if="dialogConfig.type == 'move'"
            ref="moveSelectTreeRef"
            :treeApiObj="deptTreeApiObj"
            checkStrictly
            @onCheckChange="onCheckChange"
            @onNodeExpand="onNodeExpand"
        ></selectTree>

        <treeSort
            v-if="dialogConfig.type == 'sort'"
            ref="sortRef"
            :apiParams="currInfo.id"
            :apiRequest="getOrderDepts"
            :columns="dialogConfig.columns"
            :currInfo="currInfo"
            type="Person"
        ></treeSort>
    </y9Dialog>

    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import { computed, inject, reactive, toRefs, watch, ref } from 'vue';
    import { $dictionaryFunc } from '@/utils/data';
    import { $deeploneObject } from '@/utils/object';
    import y9_storage from '@/utils/storage';
    import settings from '@/settings';
    import orgForm from '../comps/baseInfoForm/orgForm.vue';
    import departmentForm from '../comps/baseInfoForm/departmentForm.vue';
    import personForm from '../comps/baseInfoForm/personForm.vue';
    import addPersonForm from '../comps/baseInfoForm/addPersonForm.vue';
    import groupForm from '../comps/baseInfoForm/groupForm.vue';
    import extendAttr from '../comps/dialogContent/extendAttr.vue';
    import Sync from '../comps/dialogContent/sync.vue';
    import uploadOrgInfo from '../comps/dialogContent/uploadOrgInfo.vue';
    import {
        changeDisabledOrganization,
        getAllPersonsCount,
        getDepartmentById,
        getGroupById,
        getOrganizationById,
        getPersonById,
        getTreeItemById,
        orgSaveOrUpdate,
        searchByName,
        sync,
        treeInterface
    } from '@/api/org/index';
    import { changeDisabledDept, deptSaveOrUpdate, getOrderDepts, moveDept, saveOrder } from '@/api/dept/index';
    import {
        changeDisabledPerson,
        movePerson,
        personSaveOrUpdate,
        resetPassword,
        savePersons
    } from '@/api/person/index';
    import { changeDisabledGroup, groupSaveOrUpdate, moveGroup } from '@/api/group/index';
    import { listByType } from '@/api/dictionary/index';
    import { useI18n } from 'vue-i18n';
    // 注入 字体变量
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

        getTreeData: Function, //获取树数据

        postNode: Function, //请求某个节点，返回格式化好的数据

        findNode: Function, //在树数据中根据id找到对应的节点并返回

        getTreeInstance: Object, //树的实例

        refreshTree: {
            //更新tree
            type: Function
        },

        handAssginNode: Function, //手动更新节点信息

        updateTreePersonCount: Function //手动更新tree的人员计数
    });

    //移动-选择树节点展开时触发
    const selectTreeExpandNode = ref();

    function onNodeExpand(node) {
        selectTreeExpandNode.value = node;
    }

    let orgFormRef = ref(); //编辑机构表单实例
    let deptFormRef = ref(); //编辑部门表单实例
    let personFormRef = ref(); //编辑人员表单实例
    let groupFormRef = ref(); //编辑用户组表单实例
    let addDeptFormRef = ref(); //新增部门表单实例
    let addPersonFormRef = ref(); //新增人员表单实例
    let addGroupFormRef = ref(); //新增用户组表单实例
    let syncRef = ref();
    let sortRef = ref();
    let moveSelectTreeRef = ref(); //移动选择tree实例
    let selectPersonTreeRef = ref(); //人员选择tree实例

    const data = reactive({
        //当前节点信息
        currInfo: props.currTreeNodeInfo,
        //展开类按钮
        isExpandBtns: true, //是否展开所有按钮
        expandAddBtnsAnimation: '', //展开类的按钮动画名称
        isClickAction: false, //展开类的按钮，是否为点击操作

        //表单
        isEditState: false, //是否为编辑状态
        saveFormBtnLoading: false, //保存按钮加载状态
        loading: false, // 全局loading

        addPersonStep: 1, //添加人员的步骤页面flag,

        //弹窗配置
        dialogConfig: {
            show: false,
            title: '',
            onOkLoading: true,
            type: '',
            columns: [],
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {
                    let result = { success: false, msg: '' };
                    if (newConfig.value.type == 'addDepartment') {
                        let formData = addDeptFormRef.value?.y9FormRef?.model;
                        let valid = await addDeptFormRef.value?.y9FormRef?.elFormRef?.validate((valid) => valid); //获取表单验证结果;
                        if (!valid) {
                            reject();
                            return;
                        }
                        result = await deptSaveOrUpdate(formData);
                        if (result.success) {
                            //更新当前节点的children信息到树数据当中
                            await props.handAssginNode({}, currInfo.value.id, currInfo.value.id); //手动更新节点到tree
                        }
                    } else if (newConfig.value.type == 'addPerson') {
                        let formData = addPersonFormRef.value.personForm;
                        if (formData.jobIds.length === 0 && formData.positionIds.length === 0) {
                            ElNotification({
                                title: t('失败'),
                                message: t('岗位和职位请必选一个'),
                                type: 'error',
                                duration: 2000,
                                offset: 80
                            });
                            reject();
                            return;
                        } else {
                            formData = personFormData(formData);

                            result = await personSaveOrUpdate(formData);
                            if (result.success) {
                                //手动更新tree的人员计数并更新子节点
                                props.updateTreePersonCount(currInfo.value, 1, currInfo.value.id);
                            }
                        }
                    } else if (newConfig.value.type == 'addGroup') {
                        let formData = addGroupFormRef.value?.y9FormRef?.model;
                        let valid = await addGroupFormRef.value?.y9FormRef?.elFormRef?.validate((valid) => valid); //获取表单验证结果
                        if (!valid) {
                            reject();
                            return;
                        }

                        result = await groupSaveOrUpdate(formData);
                        if (result.success) {
                            //更新当前节点的children信息到树数据当中
                            await props.handAssginNode({}, currInfo.value.id, currInfo.value.id); //手动更新节点到tree
                        }
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
                            targetSystemName: formData.targetSystemName,
                            needRecursion: formData.needRecursion
                        };
                        result = await sync(params);
                    } else if (newConfig.value.type == 'selectPerson') {
                        let orgBaseIds = selectPersonTreeRef.value?.y9TreeRef?.getCheckedKeys(true);

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
                        result = await savePersons(currInfo.value.id, orgBaseIds.toString());
                        if (result.success) {
                            //更新当前节点的人员计数信息
                            if (props.getTreeData) {
                                //原本的人数
                                const originCount = currInfo.value.personCount;

                                //获取最新的人数
                                let res = await getAllPersonsCount(currInfo.value.id, currInfo.value.orgType); //获取人员数量
                                const newCount = res.data; //最新的人数

                                //手动更新tree的人员计数并更新子节点
                                props.updateTreePersonCount(currInfo.value, newCount - originCount, currInfo.value.id); //手动更新tree的人员计数
                            }
                        }
                    } else if (newConfig.value.type == 'move') {
                        const selectNodes = moveSelectTreeRef.value?.y9TreeRef?.getCheckedNodes();
                        if (selectNodes.length == 0) {
                            ElNotification({
                                title: t('失败'),
                                message: t('请选择部门'),
                                type: 'error',
                                duration: 2000,
                                offset: 80
                            });
                            reject();
                            return;
                        }
                        const targetNode = selectNodes[0];

                        if (currInfo.value.orgType == 'Department') {
                            result = await moveDept(currInfo.value.id, targetNode.id);
                        } else if (currInfo.value.orgType == 'Person') {
                            result = await movePerson(currInfo.value.id, targetNode.id);
                        } else if (currInfo.value.orgType == 'Group') {
                            result = await moveGroup(currInfo.value.id, targetNode.id);
                        }
                        if (result.success) {
                            //1.删除被移动的节点,
                            props.getTreeInstance().remove(currInfo.value);

                            // 2.获取被移动节点的父节点信息
                            const currParentNode = props.findNode(props.getTreeData(), currInfo.value.parentId);

                            // 3.更新所有父节点的数值信息
                            await props.updateTreePersonCount(currParentNode, -1);

                            //4.重新请求，移动的目标节点，获得最新的子节点信息，然后手动点击该节点
                            await props.handAssginNode({}, targetNode.id, targetNode.id);

                            // 5. 更新目标节点的所有父节点的数值
                            await props.updateTreePersonCount(targetNode, 1);
                        }
                    } else if (newConfig.value.type == 'sort') {
                        let tableData = sortRef.value.tableConfig.tableData;
                        const ids = [];
                        tableData.forEach((element) => {
                            ids.push(element.id);
                        });
                        result = await saveOrder(currInfo.value.id, ids.toString());
                        if (result.success) {
                            //更新当前节点的children信息到树数据当中
                            await props.handAssginNode({}, currInfo.value.id, currInfo.value.id); //手动更新节点到tree
                        }
                    }
                    ElNotification({
                        title: result.success ? t('成功') : t('失败'),
                        message: result.msg,
                        type: result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    if (result.success) {
                        resolve();
                    } else {
                        reject();
                    }
                });
            },
            onReset: async (newConfig) => {
                if (newConfig.value.type == 'sync') {
                    syncRef.value.y9FormRef.model.targetSysName = undefined;
                } else if (newConfig.value.type == 'addGroup') {
                    addGroupFormRef.value.y9FormRef.model.name = '';
                    addGroupFormRef.value.y9FormRef.model.description = '';
                } else if (newConfig.value.type == 'addPerson') {
                    if (addPersonStep.value === 1) {
                        let formData = addPersonFormRef.value.personForm;
                        let valid = await addPersonFormRef.value.validForm();
                        if (valid) {
                            addPersonStep.value = 2;
                            newConfig.value.resetText = computed(() => t('上一步'));
                            newConfig.value.okText = computed(() => t('保存'));
                        }
                    } else if (addPersonStep.value === 2) {
                        addPersonStep.value = 1;
                        newConfig.value.resetText = computed(() => t('下一步'));
                        newConfig.value.okText = '';
                    }
                }
            }
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
                    treeType: 'tree_type_org_person',
                    disabled: false
                }
            }
        },
        deptTreeApiObj: {
            //tree接口对象
            topLevel: treeInterface,
            childLevel: {
                api: async () => {
                    const res = await getTreeItemById({
                        parentId: selectTreeExpandNode.value.id,
                        treeType: 'tree_type_dept',
                        disabled: false
                    });

                    const data = res.data || [];

                    data.forEach((item) => {
                        //禁止选择移动到自己本身,也禁止选择移动到自身的子节点
                        if (item.id === currInfo.value.id || selectTreeExpandNode.value.disabled) {
                            item.disabled = true;
                        }
                    });
                    return data;
                },
                params: {}
            },
            search: {
                api: searchByName,
                params: {
                    treeType: 'tree_type_dept',
                    disabled: false
                }
            }
        }
    });

    let {
        treeApiObj,
        deptTreeApiObj,
        addPersonStep,
        currInfo,
        isExpandBtns,
        expandAddBtnsAnimation,
        isClickAction,
        isEditState,
        saveFormBtnLoading,
        dialogConfig,
        loading
    } = toRefs(data);

    watch(
        () => props.currTreeNodeInfo,
        async (newVal) => {
            // 请求详情接口
            let currNodeInfo = await getNodeTypeInfo(newVal);
            // 赋值
            currInfo.value = $deeploneObject(currNodeInfo);
            isEditState.value = false;
            isExpandBtns.value = true;
        },
        {
            deep: true
        }
    );

    // 请求组织架构树的各类信息
    async function getNodeTypeInfo(item: any) {
        let result = { data: {} };
        switch (item.nodeType) {
            case 'Organization':
                result = await getOrganizationById(item.id);
                break;
            case 'Department':
                result = await getDepartmentById(item.id);
                break;
            case 'Group':
                result = await getGroupById(item.id);
                break;
            case 'Person':
                result = await getPersonById(item.id);
                break;
            default:
                break;
        }
        let totalObj = Object.assign({}, item, result.data);
        return totalObj;
    }

    //移动tree点击选择框时触发
    const onCheckChange = (node, isChecked) => {
        //已经选择的节点
        const alreadyCheckedNode = moveSelectTreeRef.value?.y9TreeRef?.getCheckedNodes();
        //如果是选中并且存在已经选择的节点超过1个，则取消其他选择，做成单选效果
        if (isChecked && alreadyCheckedNode.length > 1) {
            alreadyCheckedNode.forEach((item) => {
                if (item.id !== node.id) {
                    moveSelectTreeRef.value?.y9TreeRef?.setChecked(item, false, false);
                }
            });
        }
    };

    function personFormData(formData) {
        if (formData.sex == '男') {
            formData.sex = 1;
        } else if (formData.sex == '女') {
            formData.sex = 0;
        }
        if (formData.official == '是') {
            formData.official = 1;
        } else if (formData.official == '否') {
            formData.official = 0;
        }
        if (formData.maritalStatus == '保密') {
            formData.maritalStatus = 0;
        } else if (formData.maritalStatus == '已婚') {
            formData.maritalStatus = 1;
        } else if (formData.maritalStatus == '未婚') {
            formData.maritalStatus = 2;
        }

        if (formData.jobIds || formData.positionIds) {
            //格式化职位数据
            let jobIds = [] as any;
            formData.jobIds.forEach((item) => {
                jobIds.push(item.originalId);
            });
            formData.jobIds = jobIds;

            //格式化岗位数据
            let positionIds = [] as any;
            formData.positionIds.forEach((item) => {
                positionIds.push(item.originalId);
            });
            formData.positionIds = positionIds;
        }

        return formData;
    }

    const showBtn = computed(() => {
        return (btnType) => {
            if (btnType == 'exportJSON') {
                //导出JSON

                if (currInfo.value.orgType == 'Organization') {
                    return true;
                }
            } else if (btnType == 'importPersonXLS') {
                //导入组织机构XLS
                if (currInfo.value.orgType == 'Organization') {
                    return true;
                }
            } else if (btnType == 'move') {
                if (
                    currInfo.value.orgType == 'Department' ||
                    currInfo.value.orgType == 'Person' ||
                    currInfo.value.orgType == 'Group'
                ) {
                    return true;
                }
            } else if (btnType == 'exportXls') {
                //导出XLS

                if (
                    currInfo.value.orgType == 'Organization' ||
                    currInfo.value.orgType == 'Department' ||
                    currInfo.value.orgType == 'Person'
                ) {
                    return true;
                }
            } else if (btnType == 'unlock' || btnType == 'resetPassword') {
                //设置禁用、账号解锁、重置密码

                if (currInfo.value.orgType == 'Person') {
                    return true;
                }
            } else if (btnType == 'disabled') {
                return true;
            } else if (btnType == 'updateIcon') {
                //更新图标

                if (currInfo.value.orgType == 'Department' || currInfo.value.orgType == 'Person') {
                    return true;
                }
            } else if (btnType == 'sort') {
                if (currInfo.value.orgType == 'Organization' || currInfo.value.orgType == 'Department') {
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

            if (currInfo.value.orgType == 'Person') {
                await $dictionaryFunc('dutyLevel', listByType, 'dutyLevel'); //请求职级
                await $dictionaryFunc('duty', listByType, 'duty'); //请求职务
                await $dictionaryFunc('officialType', listByType, 'officialType'); //请求编制类型
                await $dictionaryFunc('principalIDType', listByType, 'principalIDType'); //请求证件类型
            }
        } else if (type == 'save') {
            //保存
            loading.value = true;
            saveFormBtnLoading.value = true;
            if (currInfo.value.orgType == 'Organization') {
                let valid = await orgFormRef.value?.y9FormRef?.elFormRef?.validate((valid) => valid); //获取表单验证结果
                if (valid) {
                    const result = await orgSaveOrUpdate(orgFormRef.value?.y9FormRef?.model);
                    ElNotification({
                        title: result.success ? t('成功') : t('失败'),
                        message: result.msg,
                        type: result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    if (result.success) {
                        //修改后的数据更新到对应的树数据当中
                        await props.handAssginNode(result.data, currInfo.value.id); //手动更新节点到tree

                        //取消编辑状态
                        isEditState.value = false;
                    }
                }
            } else if (currInfo.value.orgType == 'Group') {
                let valid = await groupFormRef.value?.y9FormRef?.elFormRef?.validate((valid) => valid); //获取表单验证结果
                if (valid) {
                    const result = await groupSaveOrUpdate(groupFormRef.value?.y9FormRef?.model);
                    ElNotification({
                        title: result.success ? t('成功') : t('失败'),
                        message: result.msg,
                        type: result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    if (result.success) {
                        //修改后的数据更新到对应的树数据当中
                        await props.handAssginNode(result.data, currInfo.value.id); //手动更新节点到tree
                        //取消编辑状态
                        isEditState.value = false;
                    }
                }
            } else if (currInfo.value.orgType == 'Person') {
                let valid = await personFormRef.value.validForm();

                if (valid) {
                    let formData = personFormData(personFormRef.value.personForm);
                    const result = await personSaveOrUpdate(formData);
                    ElNotification({
                        title: result.success ? t('成功') : t('失败'),
                        message: result.msg,
                        type: result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    if (result.success) {
                        personFormRef.value.getPersonExt();

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
                        title: result.success ? t('成功') : t('失败'),
                        message: result.msg,
                        type: result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    if (result.success) {
                        //修改后的数据更新到对应的树数据当中
                        await props.handAssginNode(result.data, currInfo.value.id); //手动更新节点到tree

                        //取消编辑状态
                        isEditState.value = false;
                    }
                }
            }
            saveFormBtnLoading.value = false;
            loading.value = false;
        } else if (
            type == 'addDepartment' ||
            type == 'addPerson' ||
            type == 'selectPerson' ||
            type == 'move' ||
            type == 'addGroup' ||
            type == 'sync' ||
            type == 'extendAttr' ||
            type == 'sort' ||
            type == 'importPersonXLS'
        ) {
            //点击按钮显示弹窗
            addPersonStep.value = 1;
            // 深克隆对象
            let configInfo = $deeploneObject(dialogConfig.value);
            Object.assign(configInfo, {
                show: true,
                title: computed(() => t(`${title}`)),
                okText:
                    type == 'extendAttr' || (type == 'addPerson' && addPersonStep.value === 1)
                        ? false
                        : computed(() => t('保存')),
                resetText:
                    type == 'addPosition' || type == 'sync'
                        ? computed(() => t('重置'))
                        : type == 'addPerson' && addPersonStep.value === 1
                        ? computed(() => t('下一步'))
                        : false,
                cancelText: type == 'sync' || type == 'extendAttr' ? false : computed(() => t('关闭')),
                width:
                    type == 'selectPerson' || type == 'move' || type == 'sync' || type == 'importPersonXLS'
                        ? '30%'
                        : '60%',
                type: type,
                showFooter: type == 'importPersonXLS' ? false : true,
                columns:
                    type == 'sort'
                        ? [
                              {
                                  type: 'radio',
                                  title: computed(() => t('请选择')),
                                  width: 200
                              },
                              {
                                  title: computed(() => t('名称')),
                                  key: 'name'
                              },
                              {
                                  title: computed(() => t('类别')),
                                  key: 'nodeType'
                              }
                          ]
                        : []
            });

            // 赋值
            dialogConfig.value = configInfo;
        } else if (type == 'disabled' || type == 'unlock' || type == 'resetPassword' || type == 'updateIcon') {
            ElMessageBox.confirm(`${t('确定要')}${title}`, t('提示'), {
                confirmButtonText: t('确定'),
                cancelButtonText: t('取消'),
                type: 'info'
            })
                .then(async () => {
                    loading.value = true;
                    let res = { success: false, msg: '', data: {} as any };
                    if (type == 'resetPassword') {
                        res = await resetPassword(currInfo.value.id);
                    } else if (type == 'disabled') {
                        if (currInfo.value.orgType == 'Organization') {
                            res = await changeDisabledOrganization(currInfo.value.id);
                        } else if (currInfo.value.orgType == 'Department') {
                            res = await changeDisabledDept(currInfo.value.id);
                        } else if (currInfo.value.orgType == 'Group') {
                            res = await changeDisabledGroup(currInfo.value.id);
                        } else if (currInfo.value.orgType == 'Person') {
                            res = await changeDisabledPerson(currInfo.value.id);
                        }

                        if (res.success) {
                            //更新当前节点的人员计数信息
                            if (props.getTreeData) {
                                //1.更新当前节点显示的名称
                                const currNode = props.findNode(props.getTreeData(), currInfo.value.id);
                                currNode.disabled = res.data.disabled;
                                currInfo.value.disabled = res.data.disabled;
                                if (res.data.disabled) {
                                    currNode.newName = currNode.name + '[禁用]'; //显示名称
                                } else {
                                    currNode.newName = currNode.name; //显示名称
                                }

                                if (currInfo.value.orgType == 'Person') {
                                    //2.更新其父节点的人员数量
                                    props.updateTreePersonCount(currNode, res.data.disabled ? -1 : 1); //手动更新tree的人员计数
                                }
                            }
                        }
                    } else if (type == 'updateIcon') {
                        //更新图标
                    } else if (type == 'unlock') {
                        //解锁
                    }
                    loading.value = false;
                    ElNotification({
                        title: res.success ? t('成功') : t('失败'),
                        message: res.msg,
                        type: res.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                })
                .catch(() => {
                    ElMessage({
                        type: 'info',
                        message: t('已取消') + title,
                        offset: 65
                    });
                });
        } else if (type == 'exportJSON') {
            const aDom = document.createElement('a');
            aDom.href =
                import.meta.env.VUE_APP_CONTEXT +
                'api/rest/impExp/exportOrgTreeJson?orgId=' +
                currInfo.value.id +
                '&access_token=' +
                y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
            aDom.target = 'blank';
            aDom.click();
        } else if (type == 'exportXls') {
            const url = ref('');

            url.value =
                import.meta.env.VUE_APP_CONTEXT +
                'api/rest/impExp/exportPersonXls?orgBaseId=' +
                currInfo.value.id +
                '&access_token=' +
                y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');

            window.open(url.value);
        }
    }

    //展开类的按钮
    function isExpandBtnsFunc(type) {
        switch (type) {
            case 'click':
                isClickAction.value = true;
                if (data.isExpandBtns) {
                    expandAddBtnsAnimation.value = 'fadeOutRightBig';
                    setTimeout(() => {
                        data.isExpandBtns = false;
                    }, 1000);
                }
                if (!data.isExpandBtns) {
                    data.isExpandBtns = true;
                    expandAddBtnsAnimation.value = 'fadeInRightBig';
                }
                break;
            case 'mouseleave':
                if (data.isExpandBtns || isClickAction.value) {
                    expandAddBtnsAnimation.value = 'fadeOutRightBig';
                    setTimeout(() => {
                        data.isExpandBtns = false;
                        isClickAction.value = false;
                    }, 1000);
                }
                break;
            default:
                break;
        }
    }
</script>

<style lang="scss" scoped>
    .slot-header {
        width: 100%;
        display: flex;
        justify-content: space-between;
        padding: 16px 16px 0;

        .expand-btns-div {
            display: flex;
            flex-wrap: wrap;
            max-width: 70%;

            .ri-add-box-line,
            .ri-checkbox-indeterminate-line {
                margin-left: 10px;
                color: var(--el-color-primary);
                font-size: v-bind('fontSizeObj.largeFontSize');
                cursor: pointer;
                line-height: v-bind('fontSizeObj.lineHeight');
            }

            & > .expand-btns {
                animation: v-bind(expandAddBtnsAnimation) 1.5s;
                animation-fill-mode: forwards;
                flex: 1;
                text-align: right;

                :deep(.el-button) {
                    margin-bottom: 10px;
                }
            }

            .ri-checkbox-indeterminate-line,
            .ri-add-box-line {
                margin-bottom: 16px;
            }
        }
    }

    .oth-btns {
        margin-bottom: 10px;

        :deep(.el-button) {
            margin-bottom: 10px;
        }
    }
</style>
