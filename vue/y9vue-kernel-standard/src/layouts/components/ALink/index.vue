<!--
 * @Author: mengjuhua
 * @Date: 2026-08-31 15:49:35
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-09-08 09:39:07
 * @Description: 链接
-->
<template>
    <!-- 外部链接 -->
    <a v-if="isExternalLink" :href="to" class="app-link-external" rel="noopener noreferrer" target="_blank">
        <slot></slot>
    </a>

    <!-- 内部路由链接 -->
    <router-link v-else :replace="replace" :to="to" class="app-link-internal">
        <slot></slot>
    </router-link>
</template>
<script lang="ts" setup>
    import { computed } from 'vue';
    import { isExternal } from '@/utils/validate';

    interface Props {
        to: string;
        replace?: boolean;
    }

    const props = withDefaults(defineProps<Props>(), {
        replace: false
    });

    // 计算是否为外部链接，提高可读性
    const isExternalLink = computed(() => isExternal(props.to));
</script>

<style scoped></style>
