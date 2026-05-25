<template>
    <el-alert class="authority-alert" type="warning">
        <template #title>
            <div class="authority-alert__title">{{ $t('关于[隐藏]操作权限的特别说明') }}</div>
        </template>
        <div class="authority-alert__content">
            <div class="authority-alert__section">[隐藏]操作权限用于排除授权主体对资源的权限，且优先级最高。</div>
            <div class="authority-alert__section"
                >例如[用户1]同时拥有[资源A]的[隐藏]、[浏览]、[管理]权限时，最终[用户1]对[资源A]无任何权限。</div
            >
        </div>
    </el-alert>
</template>

<script setup lang="ts">

</script>

<style scoped lang="scss">
    .authority-alert {
        margin-bottom: 16px;
        line-height: 1.6;

        .authority-alert__title {
            font-weight: 600;
        }

        .authority-alert__content {
            display: flex;
            flex-direction: column;
            gap: 6px;
            margin-top: 6px;
            color: var(--el-text-color-regular);
            font-size: 13px;
        }

        .authority-alert__section {
            display: grid;
            grid-template-columns: auto 1fr;
            column-gap: 8px;
            align-items: start;
            word-break: break-word;
        }

        :deep(.el-alert__content) {
            min-width: 0;
        }

        :deep(.el-alert__title) {
            font-size: 14px;
            line-height: 1.5;
        }
    }
</style>
