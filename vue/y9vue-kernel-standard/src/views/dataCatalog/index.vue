<!--
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-06-16 10:16:08
 * @LastEditors: mengjuhua
 * @LastEditTime: 2025-12-24 09:28:09
 * @Descripttion: 数据目录管理/数据目录授权
-->
<template>
    <div>
        <fixedTreeModule
            ref="fixedTreeRef"
            :treeApiObj="treeApiObj"
            hiddenSearch="true"
            nodeLabel="newName"
            @onDeleteTree="dataCatalogRemove"
            @onTreeClick="handlerTreeClick"
        >
            <template v-slot:treeHeaderRight>
                <el-select v-model="currentTreeType" class="expand-select" @change="treeTypeChange">
                    <el-option v-for="item in treeTypeList" :key="item.code" :label="item.name" :value="item.code" />
                </el-select>

                <el-popover v-if="managerLevel === 1" placement="bottom" trigger="hover" @hide="onHidePopover">
                    <template #reference>
                        <el-button
                            :size="fontSizeObj.buttonSize"
                            :style="{ fontSize: fontSizeObj.baseFontSize }"
                            class="global-btn-main"
                            type="primary"
                        >
                            <i class="ri-menu-line"></i>
                            <span>{{ $t('数据目录') }}</span>
                        </el-button>
                    </template>

                    <y9List ref="y9ListRef" :config="actionListConfig" @on-click-row="onActionPopListClick"></y9List>
                </el-popover>
            </template>
            <template v-slot:rightContainer>
                <!-- 右边卡片 -->
                <div v-if="currData.id">
                    <BasicInfo
                        :currTreeNodeInfo="currData"
                        :findNode="findNode"
                        :getTreeData="getTreeData"
                        :getTreeInstance="getTreeInstance"
                        :handClickNode="handClickNode"
                        :postNode="postNode"
                        :treeType="currentTreeType"
                    />

                    <!-- 角色关联 -->
                    <y9Card
                        v-if="managerLevel === 2 && currData.nodeType === 'DATA_CATALOG'"
                        :title="`${$t('角色关联')} - ${currData.name ? currData.name : ''}`"
                    >
                        <template v-slot>
                            <RelationRole :id="currData.id" :appId="currData.appId" />
                        </template>
                    </y9Card>

                    <!-- 角色关联授权继承 -->
                    <y9Card
                        v-if="managerLevel === 2 && currData.nodeType === 'DATA_CATALOG' && currData.parentId"
                        :title="`${$t('角色关联授权继承')} - ${currData.name ? currData.name : ''}`"
                    >
                        <template v-slot>
                            <InheritRole :id="currData.id" />
                        </template>
                    </y9Card>

                    <!-- 组织关联 -->
                    <y9Card
                        v-if="managerLevel === 2 && currData.nodeType === 'DATA_CATALOG'"
                        :title="`${$t('组织关联')} - ${currData.name ? currData.name : ''}`"
                    >
                        <RelationOrg :id="currData.id" />
                    </y9Card>

                    <!-- 组织关联授权继承 -->
                    <y9Card
                        v-if="managerLevel === 2 && currData.nodeType === 'DATA_CATALOG' && currData.parentId"
                        :title="`${$t('组织关联授权继承')} - ${currData.name ? currData.name : ''}`"
                    >
                        <InheritOrg :id="currData.id" />
                    </y9Card>
                </div>
            </template>
        </fixedTreeModule>

        <y9Dialog v-model:config="dialogConfig">
            <y9Form v-if="dialogConfig.type == 'addDataCatalog'" ref="ruleRef" :config="formSystem"></y9Form>
            <ImportDataCatalog v-if="dialogConfig.type == 'importXls'" :tree-type="currentTreeType"></ImportDataCatalog>
        </y9Dialog>

        <!-- 制造loading效果 -->
        <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
    </div>
</template>

<script lang="ts" setup>
    import y9_storage from '@/utils/storage';
    import { computed, inject, onMounted, ref } from 'vue';
    import {
        dataCatalogTree,
        dataCatalogTreeSearch,
        deleteDataCatalog,
        getTreeTypeList,
        saveDataCatalog
    } from '@/api/dataCatalog';
    // 基本信息
    import BasicInfo from './comps/BasicInfo.vue';
    import { useI18n } from 'vue-i18n';
    import RelationRole from '@/views/grantAuthorize/comps/RelationRole.vue';
    import RelationOrg from '@/views/grantAuthorize/comps/RelationOrg.vue';
    import InheritRole from '@/views/grantAuthorize/comps/InheritRole.vue';
    import InheritOrg from '@/views/grantAuthorize/comps/InheritOrg.vue';
    import ImportDataCatalog from '@/views/dataCatalog/dialogContent/importDataCatalog.vue';
    import settings from '@/settings';
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const managerLevel = y9_storage.getObjectItem('ssoUserInfo', 'managerLevel');

    const { t } = useI18n();

    // loading
    let loading = ref(false);

    // 点击树节点 对应数据的载体
    let currData: any = ref({ id: null });

    let currentTreeType = ref(null);

    let treeTypeList = ref([] as any);

    // 节点的 基本信息 获取
    function handlerTreeClick(data) {
        // 将拿到的节点信息 储存起来
        currData.value = data;
    }

    // 树 ref
    const fixedTreeRef = ref();
    // 树的一级 子级的请求接口函数
    const treeApiObj = ref({
        topLevel: async () => {
            await initTreeTypeList();
            return dataCatalogTree({ treeType: currentTreeType.value });
        }, //顶级（一级）tree接口,
        childLevel: {
            //子级（二级及二级以上）tree接口
            api: dataCatalogTree,
            params: {
                treeType: currentTreeType.value
            }
        },
        search: {
            //搜索接口及参数
            api: dataCatalogTreeSearch,
            params: {}
        }
    });

    //气泡操作列表配置
    const actionListConfig = ref({
        padding: '0px',
        listData: [
            {
                id: 'addDataCatalog',
                name: computed(() => t('新增根分类'))
            },
            {
                id: 'importXls',
                name: computed(() => t('导入 XLS'))
            },
            {
                id: 'exportXls',
                name: computed(() => t('导出 XLS'))
            }
        ]
    });

    async function initTreeTypeList() {
        if (!currentTreeType.value) {
            let result = await getTreeTypeList();
            treeTypeList.value = result.data;
            currentTreeType.value = result.data[0].code;
        }
    }

    onMounted(() => {
        // initTreeTypeList();
    });

    let y9ListRef = ref(); //气泡框的列表实例
    const ruleRef = ref();
    let dataCatalogForm = ref({});
    const formSystem = ref({
        model: dataCatalogForm.value,
        rules: {
            name: [{ required: true, message: computed(() => t('请输入目录名称')), trigger: 'blur' }]
        },
        labelWidth: '120px',
        itemList: [
            {
                type: 'input',
                props: {
                    type: 'text'
                },
                label: t('目录名称'),
                prop: 'name',
                required: true
            },
            {
                type: 'input',
                props: {
                    type: 'text'
                },
                label: t('自定义ID'),
                prop: 'customId'
            },
            {
                type: 'input',
                props: {
                    type: 'text'
                },
                label: t('排列序号'),
                prop: 'tabIndex'
            },
            {
                type: 'textarea',
                props: {
                    type: 'textarea',
                    row: 3
                },
                label: t('描述'),
                prop: 'description'
            }
        ],
        descriptionsFormConfig: {
            labelWidth: '200px',
            labelAlign: 'center'
        }
    });

    let dialogConfig = ref({
        show: false,
        title: '',
        width: '40%',
        onOkLoading: true,
        type: '',
        onOk: (newConfig) => {
            return new Promise(async (resolve, reject) => {
                if (newConfig.value.type == 'addDataCatalog') {
                    const ruleFormRef = ruleRef.value.elFormRef;
                    if (!ruleFormRef) return;
                    await ruleFormRef.validate(async (valid, fields) => {
                        if (valid) {
                            // 通过验证
                            // 请求 新增系统 接口
                            let res = { success: false, msg: '' } as any;

                            let dataCatalog = {
                                ...ruleRef.value.model,
                                treeType: currentTreeType.value
                            };
                            res = await saveDataCatalog(dataCatalog);
                            if (res.success) {
                                fixedTreeRef.value.onRefreshTree();
                            }
                            // 清空表单 数据
                            dataCatalogForm.value = { enabled: true };
                            ElNotification({
                                title: res.success ? t('成功') : t('失败'),
                                message: res.success ? t('保存成功') : res.msg,
                                type: res.success ? 'success' : 'error',
                                duration: 2000,
                                offset: 80
                            });
                            resolve();
                        } else {
                            reject();
                        }
                    });
                }
            });
        }
    });

    function showAddDialog() {
        dialogConfig.value.show = true;
    }

    function treeTypeChange(value) {
        currentTreeType.value = value;
        currData.value = { id: null };
        fixedTreeRef.value.onRefreshTree();
    }

    //隐藏气泡框时触发
    function onHidePopover() {
        y9ListRef.value && y9ListRef.value.removeHighlight(); //清空高亮
    }

    // 删除资源
    function dataCatalogRemove(data) {
        ElMessageBox.confirm(`${t('是否删除')}【${data.name}】?`, t('提示'), {
            confirmButtonText: t('确定'),
            cancelButtonText: t('取消'),
            type: 'info'
        })
            .then(async () => {
                loading.value = true;
                // 进行 删除 操作 --
                let result = await deleteDataCatalog(data.id);

                if (result.success) {
                    /**
                     * 对树进行操作
                     */
                    //1.删除前，需要手动点击的节点信息，如果有父节点则默认点击父节点，没有则点击tree数据的第一个节点
                    const treeData = getTreeData(); //获取tree数据
                    let clickNode = null;
                    if (data.parentId) {
                        clickNode = findNode(treeData, data.parentId); //找到父节点的信息
                        fixedTreeRef.value?.y9TreeRef?.remove(data); //删除此节点
                    } else if (treeData.length > 0) {
                        fixedTreeRef.value?.y9TreeRef?.remove(data); //删除此节点
                        clickNode = treeData[0];
                    }
                    if (clickNode) {
                        handClickNode(clickNode); //手动设置点击当前节点
                    } else {
                        currData.value = { id: null };
                    }
                }

                loading.value = false;
                ElNotification({
                    message: result.success ? t('删除成功') : t('删除失败'),
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

    //操作列表点击事件
    function onActionPopListClick(currRow) {
        if (currRow.id === 'exportXls') {
            const aDom = document.createElement('a');
            aDom.href =
                import.meta.env.VUE_APP_CONTEXT +
                'api/rest/impExp/exportDataCatalogXls?treeType=' +
                currentTreeType.value +
                '&access_token=' +
                y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
            aDom.target = '_blank';
            aDom.click();
        } else {
            Object.assign(dialogConfig.value, {
                show: true,
                title: t(`${currRow.name}`),
                type: currRow.id,
                showFooter: currRow.id === 'addDataCatalog',
                width: '40%',
                columns: []
            });
        }
    }

    //获取tree数据
    function getTreeData() {
        return fixedTreeRef.value.getTreeData();
    }

    //获取树的实例
    function getTreeInstance() {
        return fixedTreeRef.value.y9TreeRef;
    }

    //请求某个节点，返回格式化好的数据
    function postNode(node) {
        return new Promise((resolve, reject) => {
            fixedTreeRef.value.onTreeLazyLoad(node, (data) => {
                resolve(data);
            });
        });
    }

    //在树数据中根据id找到对应的节点并返回
    function findNode(treeData, targetId) {
        return fixedTreeRef.value.findNode(treeData, targetId);
    }

    /**手动点击树节点
     * @param {Boolean} isExpand 是否展开节点
     */
    function handClickNode(node, isExpand?) {
        fixedTreeRef.value?.handClickNode(node, isExpand);
    }
</script>
<style lang="scss" scoped>
    :deep(.custom-right) {
        display: flex;
        flex-wrap: wrap;
        max-width: 70%;
        min-width: 40%;

        .expand-select {
            width: 150px;
            margin-right: 20px;

            .el-select__wrapper {
                font-size: v-bind('fontSizeObj.baseFontSize');
            }
        }
    }
</style>
