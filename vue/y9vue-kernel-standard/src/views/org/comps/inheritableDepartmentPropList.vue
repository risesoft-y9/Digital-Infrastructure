<template>
    <y9Card :title="`${$t('可继承的部门属性')}${currInfo.name ? ' - ' + currInfo.name : ''}`">
        <y9Table :config="setDepartmentPropTableConfig" :filterConfig="filterConfig"> </y9Table>
    </y9Card>
    <el-button v-loading.fullscreen.lock="loading" style="display: none"></el-button>
</template>

<script lang="ts" setup>
    import { useI18n } from 'vue-i18n';
    import { computed, h, inject, onMounted, reactive, ref, toRefs, watch } from 'vue';
    import { $deepAssignObject } from '@/utils/object';
    import { getTreeItemById, searchByName, treeInterface } from '@/api/org/index';
    import { getInheritableDepartmentPropOrgUnits } from '@/api/dept/index';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { $dictionary, $dictionaryFunc } from '@/utils/data';
    import { listByType } from '@/api/dictionary';

    const settingStore = useSettingStore();
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
        departmentPropFormLine: {
            departmentPropCategory: null
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
                    treeType: 'tree_type_org_person'
                }
            }
        },
        //当前节点信息
        currInfo: props.currTreeNodeInfo,
        setDepartmentPropTableConfig: {
            //设置部门领导表格配置
            columns: [
                {
                    title: computed(() => t('名称')),
                    key: 'name'
                },
                {
                    title: computed(() => t('类别')),
                    width: 120,
                    render: (row) => {
                        if (row.orgType == 'Position') {
                            return '岗位';
                        } else if (row.orgType == 'Person') {
                            return '人员';
                        }
                    }
                },
                {
                    title: computed(() => t('全路径')),
                    key: 'dn'
                },
                {
                    title: computed(() => t('是否禁用')),
                    key: 'disabled',
                    width: 100,
                    render: (row) => {
                        return h('div', row.disabled ? '是' : '否');
                    }
                }
            ],
            tableData: [],
            pageConfig: false //取消分页
        },
        // 表格过滤
        filterConfig: {
            filtersValueCallBack: (filters) => {
                departmentPropFormLine.value = filters;
                getDepartmentPropList();
            },
            showBorder: true,
            itemList: [
                {
                    type: 'select',
                    value: null,
                    key: 'departmentPropCategory',
                    label: computed(() => t('部门属性类型')),
                    labelWidth: '100px',
                    span: settingStore.device === 'mobile' ? 24 : 8,
                    props: {
                        options: [],
                        clearable: false
                    }
                },
                {
                    type: 'slot',
                    slotName: 'filterBtnSlot',
                    span: 6
                }
            ]
        }
    });

    let { treeApiObj, currInfo, setDepartmentPropTableConfig, filterConfig, loading, departmentPropFormLine } =
        toRefs(data);

    watch(
        () => props.currTreeNodeInfo,
        (newVal) => {
            currInfo.value = $deepAssignObject(currInfo.value, newVal);

            getDepartmentPropList();
        },
        {
            deep: true
        }
    );
    onMounted(async () => {
        await $dictionaryFunc('departmentPropCategory', listByType, 'departmentPropCategory'); //请求编制类型
        for (let i = 0; i < filterConfig.value.itemList.length; i++) {
            const item = filterConfig.value.itemList[i];
            if (item.key === 'departmentPropCategory') {
                item.props.options = $dictionary().departmentPropCategory?.map((item) => {
                    return {
                        label: computed(() => t(item.name)),
                        value: item.code
                    };
                });
                if (item.props.options.length > 0) {
                    item.value = item.props.options[0].value;
                }
            }
        }
        getDepartmentPropList();
    });

    async function getDepartmentPropList() {
        if (departmentPropFormLine.value.departmentPropCategory) {
            let res = await getInheritableDepartmentPropOrgUnits(
                props.currTreeNodeInfo.id,
                departmentPropFormLine.value.departmentPropCategory
            );
            if (res.success) {
                setDepartmentPropTableConfig.value.tableData = res.data;
            }
        }
    }
</script>

<style lang="scss" scoped></style>
