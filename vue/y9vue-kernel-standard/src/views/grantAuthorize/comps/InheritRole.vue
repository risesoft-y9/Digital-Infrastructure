<template>
    <div>
        <!-- 表格 -->
        <y9Table :config="tableRoleConfig">
            <template #expandRowSlot="props">
                <div class="expand-rows">
                    <p>角色名称: {{ props.row.roleNamePath }}</p>
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
    import { getInheritRoleList } from '@/api/grantAuthorize/index';
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
        tableRoleConfig: {
            columns: [
                // { title: '', type: 'selection', fixed: 'left' },
                { title: computed(() => t('序号')), type: 'index', width: 60, fixed: 'left' },
                { type: 'expand', width: 40, slot: 'expandRowSlot' },
                { title: computed(() => t('角色名称')), align: 'left', key: 'roleNamePath' },
                { title: computed(() => t('权限来自的资源名称')), key: 'resourceName' },
                { title: computed(() => t('权限类型')), key: 'authorityStr', width: 100, slot: 'authoritySlot' }
            ],
            tableData: [],
            pageConfig: false
        }
    });

    let { tableRoleConfig } = toRefs(state);

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
        let result = await getInheritRoleList(props.id);
        tableRoleConfig.value.tableData = result.data;
    }
</script>
<style lang="scss" scoped>
    .expand-rows {
        padding-left: 20px;
    }
</style>
