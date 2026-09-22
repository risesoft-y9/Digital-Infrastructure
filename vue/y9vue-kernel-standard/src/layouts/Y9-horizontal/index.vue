<template>
    <div id="indexlayout" :class="{ 'fixed-header': settingStore.getFixedHeader }">
        <div id="indexlayout-right">
            <!-- 顶部栏：补充ID匹配样式选择器 -->
            <RightTop id="right-top" @refresh="refreshFunc" />

            <!-- 导航菜单：补充ID匹配样式选择器 -->
            <Navs
                id="header-menus"
                :belongTopMenu="belongTopMenu"
                :defaultActive="defaultActive"
                :menuCollapsed="menuCollapsed"
                :menuData="menuData"
            />

            <!-- 面包屑：补充class匹配样式选择器 -->
            <BreadCrumbs
                :layoutSubName="layoutSubName"
                :list="breadCrumbs"
                :menuCollapsed="menuCollapsed"
                class="breadcrumbs"
            />

            <!-- 主内容区：唯一允许滚动的区域 -->
            <div :key="refreshContent" class="indexlayout-right-main">
                <router-view />
            </div>
        </div>
    </div>

    <!-- 设置面板：仅在风格匹配时渲染 -->
    <component :is="settingPageStyle === 'Admin-plus' ? Settings : null" />

    <!-- 锁屏组件：使用 v-show 控制显示隐藏，保持 DOM 存在以便快速切换 -->
    <Lock v-show="settingStore.getLockScreen" />

    <!-- 搜索组件 -->
    <!-- <Search /> -->
</template>

<script lang="ts" setup>
    import { computed, ref } from 'vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    import type { BreadcrumbType, RoutesDataItem } from '@/utils/routes';

    import Navs from './Navs.vue';
    import RightTop from './RightTop.vue';
    import Settings from '../components/SettingsMobile.vue';
    import BreadCrumbs from '@/layouts/components/BreadCrumbs/index.vue';
    import Lock from '@/layouts/components/Lock/index.vue';
    import Search from '@/layouts/components/search/index.vue';

    const settingStore = useSettingStore();
    const settingPageStyle = computed(() => settingStore.getSettingPageStyle);

    // 修正Props类型定义，避免Ref包装类型传入
    const props = withDefaults(
        defineProps<{
            layoutName: string;
            layoutSubName: string;
            menuData: RoutesDataItem[];
            menuCollapsed: boolean;
            belongTopMenu: string;
            defaultActive: string;
            breadCrumbs: BreadcrumbType[];
            routeItem: RoutesDataItem;
        }>(),
        { menuCollapsed: false }
    );

    // 刷新主内容区，销毁重建组件重置状态
    const refreshContent = ref(0);
    function refreshFunc() {
        refreshContent.value++;
    }
</script>

<style lang="scss" scoped>
    @import '@/theme/global-vars.scss';

    // 完全复用全局变量定义总高度偏移，消除所有硬编码数值
    $nav-height: 55px;
    $header-total-offset: calc(#{$headerHeight} + #{$nav-height} + #{$headerBreadcrumbHeight});

    #indexlayout {
        display: flex;
        height: 100vh;
        width: 100%;
        overflow: hidden; // 彻底禁止全局页面滚动
        min-width: 1350px;

        #indexlayout-right {
            display: flex;
            flex-direction: column;
            flex: 1;
            height: 100%;
            overflow: hidden; // 外层容器禁止滚动，仅主内容区允许滚动
            background-color: var(--el-color-primary-light-9);

            // 顶部栏：固定高度不参与滚动流
            > #right-top {
                flex-shrink: 0;
                height: $headerHeight;
            }

            // 导航菜单：固定高度不参与滚动流
            > #header-menus {
                flex-shrink: 0;
                height: $nav-height;
            }

            // 面包屑：固定高度不参与滚动流
            > .breadcrumbs {
                flex-shrink: 0;
                display: flex;
                align-items: center;
                justify-content: space-between;
                height: $headerBreadcrumbHeight;
                background-color: #eef0f7;
                padding: 0 35px;
                z-index: 1;
            }

            // 唯一滚动区域：复用全局自定义滚动条mixin
            > .indexlayout-right-main {
                flex: 1; // 自动占满剩余所有可用空间
                overflow-y: auto; // 仅开启垂直方向滚动
                background-color: $background-color;
                padding: $main-padding;
                padding-top: 0;

                // 直接引用全局定义的滚动条样式，无需重复编写代码
                @include scrollbar;
            }
        }

        // 固定头部模式适配
        &.fixed-header {
            #indexlayout-right {
                > #right-top,
                > #header-menus,
                > .breadcrumbs {
                    position: fixed;
                    left: 0;
                    right: 0;
                    z-index: 10;
                }

                > #right-top {
                    top: 0;
                }

                > #header-menus {
                    top: $headerHeight;
                }

                > .breadcrumbs {
                    top: calc(#{$headerHeight} + #{$nav-height});
                }

                // 给固定定位的头部区域留出顶部占位，避免内容被遮挡
                > .indexlayout-right-main {
                    margin-top: $header-total-offset;
                }
            }
        }
    }
</style>
