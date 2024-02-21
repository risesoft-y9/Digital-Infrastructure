<!--
 * @Author: shidaobang
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2024-01-30 18:03:15
 * @Description: 角色权限树
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
    import { getPersonRolePermissionList, getPositionRolePermissionList } from '@/api/permission';
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
            border: true,
            loading: false,
            columns: [
                {
                    title: '系统',
                    key: 'systemCnName',
                    width: 150
                },
                {
                    title: '应用',
                    key: 'appName',
                    width: 150,
                    render: (row) => {
                        let str = '<i class="ri-apps-line"></i><span>' + row.appName + '</span>';
                        return h('div', { innerHTML: str });
                    }
                },
                {
                    title: '角色',
                    key: 'roleName',
                    width: 150
                },
                {
                    title: '角色描述',
                    key: 'roleDescription'
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
        permission.appList.map((app, resourceIndex) => {
            if (app.permissionDetailList.length > 0) {
                app.permissionDetailList.map((detail, detailIndex) => {
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

            permission.appList.map((app, resourceIndex) => {
                if (app.permissionDetailList.length > 0) {
                    app.permissionDetailList.map((detail, detailIndex) => {
                        let content = {} as any;
                        content = detail;
                        content.appName = app.appName;
                        if (detailIndex === 0) {
                            content.detailRowspan = app.permissionDetailList.length;
                            if (resourceIndex === 0) {
                                content.systemCnName = permission.systemCnName;
                                content.systemCnNameRowspan = systemCnNameRowspan;
                            }
                        }
                        perData.push(content);
                    });
                }
            });
        });
        // console.log(perData);
        return perData;
    }

    //点击tree的回调
    async function onTreeClick(currTreeNode) {
        let perData = [];
        y9TableConfig.value.loading = true;
        if (currTreeNode.nodeType === 'Person') {
            let result = await getPersonRolePermissionList(currTreeNode.id);
            if (result.success) {
                perData = result.data;
            }
        } else if (currTreeNode.nodeType === 'Position') {
            let result = await getPositionRolePermissionList(currTreeNode.id);
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
