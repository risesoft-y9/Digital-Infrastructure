<!--
 * @Author: shidaobang
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2023-12-26 11:25:08
 * @Description: 资源权限树
-->
<template>
    <fixedTreeModule ref="fixedTreeRef" :showNodeDelete="false" :treeApiObj="treeApiObj" @onTreeClick="onTreeClick">
        <template #treeHeaderRight></template>

        <template #rightContainer>
            <template v-if="Object.keys(currTreeNodeInfo).length > 0">
                <y9Table :config="y9TableConfig"></y9Table>
            </template>
        </template>
    </fixedTreeModule>

    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { h, inject, reactive, ref, toRefs } from 'vue';
    import { getTreeItemById, searchByName, treeInterface } from '@/api/org/index';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { getPersonResourcePermissionList, getPositionResourcePermissionList } from '@/api/permission';
    import { ElMessage } from 'element-plus';

    const settingStore = useSettingStore();
    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    let fixedTreeRef = ref(); //tree实例
    //数据
    const data = reactive({
        loading: false,
        treeApiObj: {
            //tree接口对象
            topLevel: treeInterface,
            childLevel: {
                api: getTreeItemById,
                params: { treeType: 'tree_type_org', disabled: true }
            },
            search: {
                api: searchByName,
                params: {
                    treeType: 'tree_type_org'
                }
            }
        },
        currTreeNodeInfo: {}, //当前tree节点的信息
        y9TableConfig: {
            headerBackground: true,
            loading: false,
            border: true,
            columns: [
                {
                    title: '系统',
                    key: 'systemCnName',
                    width: 100
                },
                {
                    title: '资源',
                    key: 'resourceName',
                    align: 'left',
                    render: (row) => {
                        let str = '<span>' + row.resourceName + '</span>';
                        if (row.resourceType === 0) {
                            str = '<i class="ri-apps-line"></i> ' + str;
                        } else if (row.resourceType === 1) {
                            str = '<i class="ri-menu-4-line"></i> ' + str;
                        } else if (row.resourceType === 2) {
                            str = '<i class="ri-checkbox-multiple-blank-line"></i> ' + str;
                        }
                        for (let i = 0; i < row.level; i++) {
                            str = `&nbsp;&nbsp;&nbsp;&nbsp;${str}`;
                        }
                        return h('div', { innerHTML: str });
                    }
                },
                {
                    title: '权限类型',
                    key: 'authority',
                    width: 100,
                    render: (row) => {
                        let text = row.authority;
                        if (row.authority === 1) {
                            text = '浏览';
                        } else if (row.authority === 2) {
                            text = '维护';
                        } else if (row.authority === 3) {
                            text = '管理';
                        } else {
                            text = '';
                        }

                        return h('div', t(text));
                    }
                },
                {
                    title: '权限继承',
                    key: 'inherit',
                    width: 100,
                    render: (row) => {
                        let text = row.inherit;
                        if (row.inherit) {
                            text = '是';
                        } else if (row.inherit === false) {
                            text = '否';
                        } else {
                            text = '';
                        }

                        return h('div', t(text));
                    }
                },
                {
                    title: '授权主体（权限来源）',
                    key: 'principalName'
                },
                {
                    title: '主体类型',
                    key: 'principalType',
                    width: 100,
                    render: (row) => {
                        let text = row.principalType;
                        if (row.principalType === 0) {
                            text = '角色';
                        } else if (row.principalType === 1) {
                            text = '岗位';
                        } else if (row.principalType === 2) {
                            text = '人员';
                        } else if (row.principalType === 3) {
                            text = '用户组';
                        } else if (row.principalType === 4) {
                            text = '部门';
                        } else if (row.principalType === 5) {
                            text = '组织机构';
                        } else {
                            text = '';
                        }

                        return h('div', t(text));
                    }
                }
            ],
            tableData: [],
            pageConfig: false,
            spanMethod: ({ row, column, rowIndex, columnIndex }) => {
                if (column.property === 'systemCnName') {
                    if (row.systemCnNameRowspan) {
                        return {
                            rowspan: row.systemCnNameRowspan,
                            colspan: 1
                        };
                    } else {
                        return {
                            rowspan: 0,
                            colspan: 0
                        };
                    }
                }

                if (column.property === 'resourceName') {
                    if (row.detailRowspan) {
                        return {
                            rowspan: row.detailRowspan,
                            colspan: 1
                        };
                    } else {
                        return {
                            rowspan: 0,
                            colspan: 0
                        };
                    }
                }
            }
        }
    });

    const { treeApiObj, currTreeNodeInfo, loading, y9TableConfig } = toRefs(data);

    function countSystemCnNameRowspan(permission) {
        let counter = 0;
        permission.resourceList.map((resource, resourceIndex) => {
            if (resource.permissionDetailList.length > 0) {
                resource.permissionDetailList.map((detail, detailIndex) => {
                    counter++;
                });
            } else {
                counter++;
            }
        });
        return counter;
    }

    function buildRowSpanData(permissionList) {
        // 根据后台返回的数据，构造出
        let perData = [] as any;
        permissionList.map((permission, permissionIndex) => {
            let systemCnNameRowspan = countSystemCnNameRowspan(permission);

            permission.resourceList.map((resource, resourceIndex) => {
                if (resource.permissionDetailList.length > 0) {
                    resource.permissionDetailList.map((detail, detailIndex) => {
                        let content = {} as any;
                        content = detail;
                        content.resourceName = resource.resourceName;
                        content.resourceType = resource.resourceType;
                        content.level = resource.level;
                        if (detailIndex === 0) {
                            content.detailRowspan = resource.permissionDetailList.length;
                            if (resourceIndex === 0) {
                                content.systemCnName = permission.systemCnName;
                                content.systemCnNameRowspan = systemCnNameRowspan;
                            }
                        }
                        perData.push(content);
                    });
                } else {
                    let content = {} as any;
                    content.authority = undefined;
                    content.principalType = undefined;
                    content.inherit = undefined;
                    content.principalName = undefined;

                    content.resourceName = resource.resourceName;
                    content.resourceType = resource.resourceType;
                    content.level = resource.level;
                    content.detailRowspan = 1;
                    if (resourceIndex === 0) {
                        content.systemCnName = permission.systemCnName;
                        content.systemCnNameRowspan = systemCnNameRowspan;
                    }
                    perData.push(content);
                }
            });
        });
        // console.log(data);
        return perData;
    }

    //点击tree的回调
    async function onTreeClick(currTreeNode) {
        let perData = [];
        y9TableConfig.value.loading = true;
        if (currTreeNode.nodeType === 'Person') {
            let result = await getPersonResourcePermissionList(currTreeNode.id);
            if (result.success) {
                perData = result.data;
            }
        } else if (currTreeNode.nodeType === 'Position') {
            let result = await getPositionResourcePermissionList(currTreeNode.id);
            if (result.success) {
                perData = result.data;
            }
        } else {
            ElMessage({ message: '请选择人员或岗位查看！', offset: 65 });
        }
        y9TableConfig.value.tableData = buildRowSpanData(perData);
        y9TableConfig.value.loading = false;
        currTreeNodeInfo.value = currTreeNode;
    }
</script>

<style lang="scss" scoped></style>
