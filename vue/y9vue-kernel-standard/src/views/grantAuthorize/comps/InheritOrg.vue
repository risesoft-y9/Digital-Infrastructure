<template>
    <div>
        <!-- 表格 -->
        <y9Table :config="tableOrgConfig"></y9Table>
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
                { title: computed(() => t('序号')), type: 'index', width: '100px', fixed: 'left' },
                { title: computed(() => t('组织名称')), key: 'orgName' },
                { title: computed(() => t('组织类型')), key: 'orgType', width: 150 },
                { title: computed(() => t('资源名称')), key: 'resourceName' },
                { title: computed(() => t('权限类型')), key: 'authorityStr', width: 100 },
                { title: computed(() => t('授权者')), key: 'authorizer' },
                { title: computed(() => t('授权时间')), key: 'authorizeTime', width: settingStore.getDatetimeSpan }
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
<style lang="scss" scoped></style>
