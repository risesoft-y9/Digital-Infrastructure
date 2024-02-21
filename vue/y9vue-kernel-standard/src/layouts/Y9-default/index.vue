<template>
    <div
        id="indexlayout"
        :class="{
            fixedHeader: !settingStore.getFixedHeader,
            'fixedHeader-menuCollapsed': settingStore.getMenuCollapsed && !settingStore.getFixedHeader,
            [layout]: true
        }"
    >
        <div id="indexlayout-left">
            <Left
                :belongTopMenu="belongTopMenu"
                :defaultActive="defaultActive"
                :layoutSubName="layoutSubName"
                :menuCollapsed="menuCollapsed"
                :menuData="menuData"
            />
        </div>
        <div id="indexlayout-right" class="right">
            <RightTop :menuCollapsed="menuCollapsed" @refresh="refreshFunc" />
            <!-- <component :is="showTab ? Tabs : ''"></component> -->
            <component
                :is="BreadCrumbs"
                :layoutSubName="layoutSubName"
                :list="breadCrumbs"
                :menuCollapsed="menuCollapsed"
            ></component>

            <div
                :key="refreshContent"
                :class="{
                    'indexlayout-right-main': true,
                    'sidebar-separate': layoutSubName === 'sidebar-separate' ? true : false,
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
    <Search />
</template>

<script lang="ts" setup>
    import { computed, watch } from 'vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useRouterStore } from '@/store/modules/routerStore';
    import Lock from '@/layouts/components/Lock/index.vue';
    import Left from './Left.vue';
    import RightTop from './RightTop.vue';
    // import Settings from "@/layouts/components/Settings.vue"
    import Settings from '@/layouts/components/SettingsMobile.vue';
    import BreadCrumbs from '@/layouts/components/BreadCrumbs/index.vue';
    // import Tabs from "@/layouts/components/Tabs/index.vue"
    import Search from '@/layouts/components/search/index.vue';

    const settingStore = useSettingStore();
    const routerStore = useRouterStore();
    const settingPageStyle = computed(() => settingStore.getSettingPageStyle);
    const showTab = computed(() => settingStore.getShowLabel);
    const props = defineProps({
        layoutName: {
            type: String as Ref<string>,
            required: true
        },
        layoutSubName: {
            type: String as Ref<string>,
            required: true
        },
        menuData: {
            type: Object as RoutesDataItem[],
            required: true
        },
        menuCollapsed: {
            type: Boolean as computed<Boolean>,
            required: true
        },
        belongTopMenu: {
            type: String as ComputedRef<string>,
            required: true
        },
        defaultActive: {
            type: String as Ref<string>,
            required: true
        },
        breadCrumbs: {
            type: Array as ComputedRef<BreadcrumbType[]>,
            required: true
        },
        routeItem: {
            type: Object as ComputedRef<RoutesDataItem>,
            required: true
        }
    });

    // 在 sidebar-separate 布局时 监听滚动事件，indexlayout-left 增加一个class改变样式
    const layout = computed(() => settingStore.getLayout);

    function listener() {
        const classList = document.getElementById('indexlayout-left').classList;
        const scroll_Y = window.scrollY;
        const listToArray = Array.from(classList).includes('fixed-header-after-scroll');

        if (scroll_Y > 50 && !listToArray) {
            document.getElementById('indexlayout-left').className += ' fixed-header-after-scroll';
            // 修复 浮动布局时，收缩菜单后，滚动出现的bug
            if (settingStore.menuCollapsed) {
                document.getElementById('indexlayout-left').children[0].style.width = '68px';
            }
            // listToArray.length
            //   ? document.getElementById('indexlayout-left').className += ' fixed-header-after-scroll'
            //   : document.getElementById('indexlayout-left').className += 'fixed-header-after-scroll'
        }
        if (scroll_Y < 50 && listToArray) {
            let array = document.getElementById('indexlayout-left').className.split('fixed-header-after-scroll');
            let classStr = '';
            array.forEach((item) => {
                classStr += item;
            });
            document.getElementById('indexlayout-left').className = classStr;
            // 修复 浮动布局时，收缩菜单后，滚动出现的bug
            if (settingStore.menuCollapsed) {
                document.getElementById('indexlayout-left').children[0].style.width = '';
            }
        }
    }

    if (layout.value.indexOf('sidebar-separate') > 0) {
        // fixed-header-after-scroll
        window.addEventListener('scroll', listener, false);
    }
    watch(layout, (newV, oldV) => {
        if (newV.indexOf('sidebar-separate') > 0) {
            // fixed-header-after-scroll
            window.addEventListener('scroll', listener, false);
        } else {
            // 移除监听
            window.removeEventListener('scroll', listener, false);
        }
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
        // background-color: #ffffff;
        &.right {
            display: flex;
            flex-direction: column;
            background-color: var(--el-color-primary-light-9);
            min-height: 100vh;

            .indexlayout-right-main {
                flex: 1;
                //暂时不变，等dark版本追加
                background-color: #eef0f7;
                padding: $main-padding;
                padding-top: 0;
                overflow: auto;
                scrollbar-width: none;
                box-shadow: 3px 3px 3px var(--el-color-info-light);
                // > div {
                //     height: calc(100vh - 80px - 60px - 35px);
                // }

                &.sidebar-separate {
                    padding-left: calc(#{$leftSideBarWidth} + #{$sidebar-separate-margin-left} + #{$main-padding});
                }
            }
        }

        & > .breadcrumbs {
            display: flex;
            align-items: center;
            justify-content: space-between;
            // width: $headerBreadcrumbWidth;
            // margin: 0 auto;
            height: $headerBreadcrumbHeight;
            //暂时不变，等dark版本追加
            background-color: #eef0f7;
            padding: 0 35px;
            color: var(--el-text-color-primary) !important;

            :deep(a) {
                color: var(--el-text-color-primary) !important;
            }
        }

        // & > .breadcrumbs span{

        //     color: var(--el-menu-text-color);

        // }
    }

    .indexlayout-main-conent {
        margin: 24px;
        position: relative;
    }

    // -----------fixed-header功能 css局部修改---------------------------------
    #indexlayout {
        &.fixedHeader {
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

        &.fixedHeader-menuCollapsed {
            & > #indexlayout-right {
                padding-left: $menu-collapsed-width;
                transition-duration: 0.2s;
            }
        }
    }

    // -----------sidebar-separate布局 css局部修改----------------------
    #indexlayout {
        &.sidebar-separate {
            & > #indexlayout-right {
                padding-left: 0;
                transition-duration: 0.2s;
            }
        }

        & > #indexlayout-left.fixed-header-after-scroll {
            & > :deep(div) {
                top: 0;
                left: 0;
                width: calc(#{$leftSideBarWidth} + 20px);
                height: 100vh;
                transition-duration: 0.2s;
            }
        }

        & > #indexlayout-right {
            &.right {
                .indexlayout-right-main {
                    // 1_right-main的调整
                    &.sidebar-separate-menuCollapsed {
                        padding-left: calc(54px + #{$sidebar-separate-margin-left} + #{$main-padding});
                        transition-duration: 0.2s;
                    }

                    // > div {
                    //     height: calc(100vh - 80px - 60px - 35px);
                    // }
                }
            }

            & > .breadcrumbs {
                // 2_breadcrumbs的调整
                &.sidebar-separate-uncollapsed {
                    padding-left: calc(#{$sidebar-separate-margin-left} + #{$leftSideBarWidth} + #{$main-padding});
                    transition-duration: 0.25s;
                }

                &.sidebar-separate-menuCollapsed {
                    padding-left: calc(54px + #{$sidebar-separate-margin-left} + #{$main-padding});
                    transition-duration: 0.25s;
                }
            }

            // tabs 相关的css
            // & > #kernel-tabs{
            //   padding-left: calc(
            //       #{$sidebar-separate-margin-left} + #{$leftSideBarWidth} + #{$main-padding}
            //     );
            //   padding-right: $main-padding;
            //   background-color: var(--el-color-primary-light-9);
            //   & > :deep(div){
            //     background-color: var(--el-bg-color);
            //     width: 100%;
            //     margin-top: $sidebar-separate-margin-left;
            //   }
            //   & > :deep(i){
            //     display: none;
            //   }
            // }
        }
    }

    // tabs 相关的css
    // #indexlayout {
    //   & > #indexlayout-right {
    //     & > .tabs-position-left {
    //       padding-left: 18%;
    //     }
    //     & > .tabs-position-right {
    //       padding-right: 18%;
    //     }
    //   }
    // }
</style>
