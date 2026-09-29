<!--
 * @Author: your name
 * @Date: 2022-01-13 17:31:19
 * @LastEditTime: 2026-09-08 09:39:23
 * @LastEditors: mengjuhua
 * @Description:   
-->
<template>
    <div
        id="breadcrumbs"
        :class="{
            breadcrumbs: true,
            'sidebar-separate-uncollapsed': !menuCollapsed && layoutSubName === 'sidebar-separate',
            'sidebar-separate-menuCollapsed': menuCollapsed && layoutSubName === 'sidebar-separate'
        }"
    >
        <span class="title">{{ $t(`${list[0].meta.title}`) }}</span>
        <div class="breadcrumb-wrapper">
            <i class="ri-map-pin-line ri-lx" style="color: var(--el-color-primary); padding-right: 5px"></i>
            <el-breadcrumb>
                <el-breadcrumb-item v-for="item in list" :key="item.path">
                    <a-link :to="item.path" class="title-link">{{ $t(`${item.meta.title}`) }}</a-link>
                </el-breadcrumb-item>
            </el-breadcrumb>
        </div>
    </div>
</template>
<script lang="ts" setup>
    import { inject, PropType } from 'vue';
    import { BreadcrumbType } from '@/utils/routes';
    import ALink from '../ALink/index.vue';

    // 定义组件 Props
    const props = defineProps({
        layoutSubName: {
            type: String,
            required: true
        },
        list: {
            type: Array as PropType<BreadcrumbType[]>,
            default: () => []
        },
        menuCollapsed: {
            type: Boolean,
            required: true
        }
    });

    // 注入全局字体配置变量
    const fontSizeObj = inject('sizeObjInfo') as Record<string, string>;
</script>

<style lang="scss" scoped>
    .breadcrumb-wrapper {
        display: flex;
        align-items: center;
        cursor: pointer;
    }

    .title {
        font-size: v-bind('fontSizeObj.largerFontSize');
    }

    .title-link {
        font-size: v-bind('fontSizeObj.baseFontSize');
    }
</style>
