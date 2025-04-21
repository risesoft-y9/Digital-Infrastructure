<template>
    <div>
        <!-- 表格 -->
        <y9Table :config="tableOrgConfig">
            <template #expandRowSlot="props">
                <div class="expand-rows">
                    <p>组织节点名称: {{ props.row.orgUnitName }}</p>
                    <p>组织节点类型: {{ props.row.orgType }}</p>
                    <p>权限来自的资源名称: {{ props.row.resourceName }}</p>
                    <p>权限类型: {{ props.row.authorityStr }}</p>
                    <p>授权者: {{ props.row.authorizer }}</p>
                    <p>授权时间: {{ props.row.authorizeTime }}</p>
                </div>
            </template>
            <template #authoritySlot="props">
                <boolWarningCell
                    :is-true="props.row.authorityStr === '隐藏'"
                    :true-text="props.row.authorityStr"
                    :false-text="props.row.authorityStr"
                ></boolWarningCell>
            </template>
        </y9Table>
    </div>
</template>

<script lang="ts" setup>
    import { computed, onMounted, reactive, toRefs, watch } from 'vue';
    import { getInheritOrgList } from '@/api/grantAuthorize/index';
    import { useI18n } from 'vue-i18n';
    import { useSettingStore } from '@/store/modules/settingStore';

    const settingStore = useSettingStore();
    // 注入 字体对象
    const { t } = useI18n();
    const props = defineProps({
        id: {
            type: String,
            default: ''
        }
    });

    // 变量 对象
    const state = reactive({
        // 角色关联 表格的 配置信息
        tableOrgConfig: {
            columns: [
                // { title: '', type: 'selection', fixed: 'left' },
                { title: computed(() => t('序号')), type: 'index', width: 60, fixed: 'left' },
                { type: 'expand', width: 40, slot: 'expandRowSlot' },
                { title: computed(() => t('组织节点名称')), align: 'left', key: 'orgUnitName' },
                { title: computed(() => t('组织节点类型')), key: 'orgType', width: 150 },
                { title: computed(() => t('权限来自的资源名称')), key: 'resourceName' },
                { title: computed(() => t('权限类型')), key: 'authorityStr', width: 100, slot: 'authoritySlot' },
            ],
            tableData: [],
            pageConfig: false
        }
    });

    let { tableOrgConfig } = toRefs(state);

    onMounted(() => {
        initList();
    });

    watch(
        () => props.id,
        (new_, old_) => {
            if (new_ && new_ !== old_) {
                initList();
            }
        }
    );

    // 请求 列表接口
    async function initList() {
        let result = await getInheritOrgList(props.id);
        tableOrgConfig.value.tableData = result.data;
    }
</script>
<style lang="scss" scoped>
    .expand-rows {
        padding-left: 20px;
    }
</style>
