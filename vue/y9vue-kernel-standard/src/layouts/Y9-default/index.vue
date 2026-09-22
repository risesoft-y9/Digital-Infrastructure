<template>
    <div
        id="indexlayout"
        :class="{
            fixedHeader: !settingStore.getFixedHeader,
            'fixedHeader-menuCollapsed': settingStore.getMenuCollapsed && !settingStore.getFixedHeader,
            [layout]: true
        }"
    >
        <div id="indexlayout-left" ref="layoutLeftRef">
            <Left
                :belongTopMenu="belongTopMenu"
                :defaultActive="defaultActive"
                :layoutSubName="layoutSubName"
                :menuCollapsed="menuCollapsed"
                :menuData="menuData"
            />
        </div>
        <div id="indexlayout-right" ref="layoutRightRef" class="right">
            <RightTop :menuCollapsed="menuCollapsed" @refresh="refreshFunc" />
            <!-- <component :is="showTab ? Tabs : ''"></component> -->
            <BreadCrumbs :layoutSubName="layoutSubName" :list="breadCrumbs" :menuCollapsed="menuCollapsed" />

            <div
                :key="refreshContent"
                :class="{
                    'indexlayout-right-main': true,
                    'sidebar-separate': layoutSubName === 'sidebar-separate',
                    'sidebar-separate-menuCollapsed': menuCollapsed && layoutSubName === 'sidebar-separate',
                    'tabs-position-left': routerStore.getTabs.length && settingStore.getLabelStyle === 'left',
                    'tabs-position-right': routerStore.getTabs.length && settingStore.getLabelStyle === 'right'
                }"
            >
                <router-view></router-view>
            </div>
        </div>
    </div>
    <component :is="settingPageStyle === 'Admin-plus' ? Settings : ''"></component>
    <Lock v-show="settingStore.getLockScreen" />
    <!-- <Search /> -->
</template>

<script lang="ts" setup>
    import { computed, onBeforeUnmount, onMounted, ref, watchEffect } from 'vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useRouterStore } from '@/store/modules/routerStore';
    import type { BreadcrumbType, RoutesDataItem } from '@/utils/routes';
    import { debounce } from 'lodash-es';
    import Lock from '@/layouts/components/Lock/index.vue';
    import Left from './Left.vue';
    import RightTop from './RightTop.vue';
    import Settings from '@/layouts/components/SettingsMobile.vue';
    import BreadCrumbs from '@/layouts/components/BreadCrumbs/index.vue';
    // import Tabs from "@/layouts/components/Tabs/index.vue"

    const settingStore = useSettingStore();
    const routerStore = useRouterStore();

    const layoutLeftRef = ref<HTMLElement | null>(null);
    const layoutRightRef = ref<HTMLElement | null>(null);
    const scrollListenerActive = ref(false);

    const settingPageStyle = computed(() => settingStore.getSettingPageStyle);
    const showTab = computed(() => settingStore.getShowLabel);

    const props = defineProps<{
        layoutName: string;
        layoutSubName: string;
        menuData: RoutesDataItem[];
        menuCollapsed: boolean;
        belongTopMenu: string;
        defaultActive: string;
        breadCrumbs: BreadcrumbType[];
        routeItem: RoutesDataItem;
    }>();

    const layout = computed(() => settingStore.getLayout);

    // 封装侧边栏宽度更新逻辑，避免重复DOM操作
    function updateSidebarWidth(width: string) {
        const sidebar = layoutLeftRef.value?.firstElementChild as HTMLElement | null;
        if (sidebar) sidebar.style.width = width;
    }

    // 滚动监听逻辑完全重构，基于ref操作DOM，避免全局window滚动冲突
    function listener() {
        const classList = layoutLeftRef.value?.classList;
        if (!classList) return;

        const scrollY = layoutRightRef.value?.scrollTop ?? 0;
        const hasFixedClass = classList.contains('fixed-header-after-scroll');

        if (scrollY > 50 && !hasFixedClass) {
            classList.add('fixed-header-after-scroll');
            if (settingStore.getMenuCollapsed) updateSidebarWidth('68px');
        }
        if (scrollY < 50 && hasFixedClass) {
            classList.remove('fixed-header-after-scroll');
            if (settingStore.getMenuCollapsed) updateSidebarWidth('');
        }
    }

    // 60fps 防抖，避免高频滚动触发大量重绘
    const debouncedScrollListener = debounce(listener, 16);

    function addScrollListener() {
        if (scrollListenerActive.value || !layoutRightRef.value) return;
        layoutRightRef.value.addEventListener('scroll', debouncedScrollListener, false);
        scrollListenerActive.value = true;
    }

    function removeScrollListener() {
        if (!scrollListenerActive.value) return;
        layoutRightRef.value?.removeEventListener('scroll', debouncedScrollListener, false);
        debouncedScrollListener.cancel();
        scrollListenerActive.value = false;
        layoutLeftRef.value?.classList.remove('fixed-header-after-scroll');
    }

    // 自动监听布局变化，自动注册/移除滚动监听
    watchEffect((onInvalidate) => {
        if (layout.value.includes('sidebar-separate') && layoutRightRef.value) {
            addScrollListener();
        }
        onInvalidate(() => removeScrollListener());
    });

    onMounted(() => {
        if (layout.value.includes('sidebar-separate')) addScrollListener();
    });

    onBeforeUnmount(removeScrollListener);

    const refreshContent = ref(0);
    function refreshFunc() {
        refreshContent.value++;
    }
</script>

<style lang="scss" scoped>
    @import '@/theme/global-vars.scss';

    #indexlayout {
        display: flex;
        height: 100vh;
        overflow: hidden;
        min-width: 1350px;
    }

    #indexlayout-left {
        z-index: 1;
        box-shadow: 2px 2px 2px 1px rgb(0 0 0 / 6%);
    }

    #indexlayout-right {
        position: relative;
        flex: 1;
        overflow: auto;
        scrollbar-width: none;
        background-color: var(--bg-color);
        &.right {
            display: flex;
            flex-direction: column;
            background-color: var(--el-color-primary-light-9);
            min-height: 100vh;

            .indexlayout-right-main {
                flex: 1;
                background-color: #eef0f7;
                padding: $main-padding;
                padding-top: 0;
                overflow: auto;
                scrollbar-width: none;
                box-shadow: 3px 3px 3px var(--el-color-info-light);

                &.sidebar-separate {
                    padding-left: calc(#{$leftSideBarWidth} + #{$sidebar-separate-margin-left} + #{$main-padding});
                }

                &.sidebar-separate-menuCollapsed {
                    padding-left: calc(54px + #{$sidebar-separate-margin-left} + #{$main-padding});
                    transition-duration: 0.2s;
                }
            }
        }

        // 补全深度选择器，让面包屑子组件样式正常生效
        & > :deep(.breadcrumbs) {
            display: flex;
            align-items: center;
            justify-content: space-between;
            height: $headerBreadcrumbHeight;
            background-color: #eef0f7;
            padding: 0 35px;
            color: var(--el-text-color-primary) !important;

            a {
                color: var(--el-text-color-primary) !important;
            }

            &.sidebar-separate-uncollapsed {
                padding-left: calc(#{$sidebar-separate-margin-left} + #{$leftSideBarWidth} + #{$main-padding});
                transition-duration: 0.25s;
            }

            &.sidebar-separate-menuCollapsed {
                padding-left: calc(54px + #{$sidebar-separate-margin-left} + #{$main-padding});
                transition-duration: 0.25s;
            }
        }
    }

    .indexlayout-main-conent {
        margin: 24px;
        position: relative;
    }

    // -----------fixed-header功能 css局部修改---------------------------------
    #indexlayout.fixedHeader {
        height: auto;

        & > #indexlayout-left {
            position: fixed;
            z-index: 3;
        }

        & > #indexlayout-right {
            padding-left: $leftSideBarWidth;
            transition-duration: 0.2s;
        }
    }

    #indexlayout.fixedHeader-menuCollapsed > #indexlayout-right {
        padding-left: $menu-collapsed-width;
        transition-duration: 0.2s;
    }

    // -----------sidebar-separate布局 css局部修改----------------------
    #indexlayout.sidebar-separate > #indexlayout-right {
        padding-left: 0;
        transition-duration: 0.2s;
    }

    // 原有深度选择器保留，适配Left子组件内部样式
    #indexlayout-left.fixed-header-after-scroll > :deep(div) {
        top: 0;
        left: 0;
        width: calc(#{$leftSideBarWidth} + 20px);
        height: 100vh;
        transition-duration: 0.2s;
    }
</style>
