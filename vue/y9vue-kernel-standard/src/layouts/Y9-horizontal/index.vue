<template>
    <div id="indexlayout" :class="{ 'fixed-header': settingStore.getFixedHeader }">
        <div id="indexlayout-right">
            <RightTop @refresh="refreshFunc" />
            <Navs
                :menuCollapsed="menuCollapsed"
                :belongTopMenu="belongTopMenu"
                :defaultActive="defaultActive"
                :menuData="menuData"
            />
            <BreadCrumbs :list="breadCrumbs" :menuCollapsed="menuCollapsed"></BreadCrumbs>
            <div class="indexlayout-right-main" :key="refreshContent">
                <router-view></router-view>
            </div>
        </div>
    </div>
    <!-- <component :is="settingPageStyle === 'Dcat' ? Settings : ''"></component> -->
    <component :is="settingPageStyle === 'Admin-plus' ? Settings : ''"></component>
    <!-- <component :is="settingStore.getLockScreen ? Lock : ''"></component> -->
    <Lock v-show="settingStore.getLockScreen" />
    <Search />
</template>

<script lang="ts" setup>
    import { defineComponent, onMounted } from 'vue';
    import { useSettingStore } from '@/store/modules/settingStore';

    import Navs from './Navs.vue';
    import RightTop from './RightTop.vue';
    import Settings from '../components/SettingsMobile.vue';
    import BreadCrumbs from '@/layouts/components/BreadCrumbs/index.vue';
    import Lock from '@/layouts/components/Lock/index.vue';
    import Search from '@/layouts/components/search/index.vue';

    const settingStore = useSettingStore();
    const settingPageStyle = computed(() => settingStore.getSettingPageStyle);
    const props = defineProps({
        menuData: {
            type: Object as RoutesDataItem[],
            required: true,
        },
        menuCollapsed: Boolean,
        belongTopMenu: {
            type: String as ComputedRef<string>,
            required: true,
        },
        defaultActive: {
            type: String as Ref<string>,
            required: true,
        },
        breadCrumbs: {
            type: Array as ComputedRef<BreadcrumbType[]>,
            required: true,
        },
        routeItem: {
            type: Object as ComputedRef<RoutesDataItem>,
            required: true,
        },
    });

    // 刷新组件
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
    #indexlayout-right {
        position: relative;
        flex: 1;
        overflow: auto;
        scrollbar-width: none;
        // background-color: var(--bg-color);
        background-color: var(--el-color-primary-light-9);
        & > .indexlayout-right-main {
            flex: 1;
            overflow: auto;
            scrollbar-width: none;
            background-color: #eff1f7;
            padding: $main-padding;
            padding-top: 0;
            // box-shadow: 2px 3px 4px var(--el-color-primary-light-8);
            > div {
                height: calc(100vh - 80px - 60px - 35px);
            }
        }
        & > .breadcrumbs {
            display: flex;
            align-items: center;
            justify-content: space-between;
            // width: $headerBreadcrumbWidth;  // beta-0.1(因最初的原型稿而增加的代码)
            margin: 0 auto;
            height: $headerBreadcrumbHeight;
            background-color: #eef0f7;
            padding: 0 35px;
            z-index: 1;
        }
    }

    // -----------fixed-header功能 css局部修改---------------------------------
    #indexlayout {
        &.fixed-header {
            & > #indexlayout-right {
                & > #right-top,
                #header-menus,
                .breadcrumbs {
                    position: fixed;
                    // z-index: 1;
                }

                & > #header-menus {
                    top: calc(#{$headerHeight} + 1px);
                }
                & > .breadcrumbs {
                    top: calc(#{$headerHeight} + 55px);
                    left: 0;
                    right: 0;
                }
                & > .indexlayout-right-main {
                    margin-top: calc(#{$headerHeight} + 55px + #{$headerBreadcrumbHeight});
                    height: calc(100vh - 235px);
                    > div {
                        height: calc(100vh - 80px - 60px - 35px);
                    }
                }
            }
        }
    }
</style>
