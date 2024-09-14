<!--
 * @Author: shidaobang
 * @Date: 2022-04-07 17:43:02
 * @LastEditors: mengjuhua
 * @LastEditTime: 2024-04-07 11:30:16
 * @Description: 角色权限树
-->
<template>
    <fixedTreeModule ref="fixedTreeRef" :showNodeDelete="false" :treeApiObj="treeApiObj" @onTreeClick="onTreeClick">
        <template #treeHeaderRight></template>

        <template #rightContainer>
            <template v-if="Object.keys(currTreeNodeInfo).length > 0">
                <y9Card :title="`${currTreeNodeInfo.name ? currTreeNodeInfo.name : ''}`">
                    <template v-if="currTreeNodeInfo.nodeType === 'Position' || currTreeNodeInfo.nodeType === 'Person'">
                        <y9Table
                            :config="y9TableConfig"
                            :filterConfig="filterConfig"
                            @window-height-change="windowHeightChange"
                        >
                            <template v-slot:slotsync>
                                <el-button class="global-btn-main" type="primary" @click="syncPosition()">
                                    <i class="ri-refresh-line"></i>
                                    {{ $t('权限') }}
                                </el-button>
                            </template>
                        </y9Table>
                    </template>
                    <template v-else>
                        <div style="height: 213px">
                            <el-button
                                class="global-btn-main"
                                style="margin-bottom: 10px"
                                type="primary"
                                @click="syncPosition()"
                            >
                                <i class="ri-refresh-line"></i>
                                {{ $t('权限') }}
                            </el-button>
                            <el-alert title="请点击左侧树，选择人员/岗位。" type="warning" />
                        </div>
                    </template>
                </y9Card>
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
    import { getPersonRolePermissionList, getPositionRolePermissionList, identityRoles } from '@/api/permission';
    import y9_storage from '@/utils/storage';

    const settingStore = useSettingStore();
    const { t } = useI18n();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');

    // 重新获取个人信息（登陆返回的个人信息有缺失的属性）
    const ssoUserInfo = y9_storage.getObjectItem('ssoUserInfo');

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
        currTreeNodeInfo: {} as any, //当前tree节点的信息
        y9TableConfig: {
            headerBackground: false,
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
                        if (row.appName) {
                            let str = '<i class="ri-apps-line"></i><span>' + row.appName + '</span>';
                            return h('div', { innerHTML: str });
                        } else {
                            return '';
                        }
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

                if (column.property === 'appName') {
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
        },
        filterConfig: {
            filtersValueCallBack: (filter) => {},
            itemList: [
                {
                    type: 'slot',
                    slotName: 'slotsync',
                    span: settingStore.device === 'mobile' ? 24 : 12
                }
            ],
            showBorder: true
        }
    });

    const { treeApiObj, currTreeNodeInfo, loading, y9TableConfig, filterConfig } = toRefs(data);

    //窗口变动时触发，获取表格的高度
    function windowHeightChange(tableHeight) {
        y9TableConfig.value.height = tableHeight - 35 - 35 - 28;
        y9TableConfig.value.maxHeight = tableHeight - 35 - 35 - 28; //35 35 是y9-card-content样式中上padding、下padding的值
    }

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
        currTreeNodeInfo.value = currTreeNode;
        getData();
    }

    async function getData() {
        let perData = [];
        y9TableConfig.value.loading = true;
        if (currTreeNodeInfo.value.nodeType === 'Person') {
            let result = await getPersonRolePermissionList(currTreeNodeInfo.value.id);
            if (result.success) {
                perData = result.data;
            }
        } else if (currTreeNodeInfo.value.nodeType === 'Position') {
            let result = await getPositionRolePermissionList(currTreeNodeInfo.value.id);
            if (result.success) {
                perData = result.data;
            }
        } else {
            //ElMessage({ message: '请选择人员或岗位查看！', offset: 65 });
        }
        y9TableConfig.value.tableData = buildRowSpanData(perData);
        y9TableConfig.value.loading = false;
    }

    async function syncPosition() {
        loading.value = true;
        let result = await identityRoles(ssoUserInfo.tenantId, currTreeNodeInfo.value.id);
        loading.value = false;
        if (result.success) {
            if (currTreeNodeInfo.value.nodeType === 'Person' || currTreeNodeInfo.value.nodeType === 'Position') {
                getData();
            }
        }
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
