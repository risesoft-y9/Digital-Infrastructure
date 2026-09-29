<template>
    <div id="indexlayout">
        <!-- 移动端/折叠状态下的抽屉菜单 -->
        <el-drawer
            v-model="menuDrawer"
            :direction="direction"
            :z-index="2000"
            @close="toggleCollapsedFunc"
            class="custom-drawer-width"
        >
            <template #default>
                <div style="position: fixed; left: var(--el-dialog-padding-primary); top: 4px">
                    <RightTopUser />
                </div>

                <Left
                    :belong-top-menu="belongTopMenu"
                    :default-active="defaultActive"
                    :layout-sub-name="layoutSubName"
                    :menu-collapsed="!menuCollapsed"
                    :menu-data="menuData"
                />
            </template>
        </el-drawer>

        <!-- 右侧主内容区域 -->
        <div id="indexlayout-right" class="fiexd-header">
            <RightTop :menu-collapsed="menuCollapsed" style="z-index: 1999" />
            <div class="indexlayout-right-main">
                <BreadCrumbs :layoutSubName="layoutSubName" :list="breadCrumbs" :menuCollapsed="menuCollapsed" />
                <router-view />
            </div>
        </div>

        <!-- 移动端设置按钮 -->
        <SettingsMobile />

        <!-- 锁屏组件 -->
        <Lock v-show="settingStore.getLockScreen" />
    </div>
</template>

<script lang="ts" setup>
    import { computed } from 'vue';
    import { useRoute } from 'vue-router';
    import { useSettingStore } from '@/store/modules/settingStore';
    import type { BreadcrumbType, RoutesDataItem } from '@/utils/routes';
    import Left from './Left.vue';
    import RightTop from './RightTop.vue';
    import SettingsMobile from '@/layouts/components/SettingsMobile.vue';
    import BreadCrumbs from '@/layouts/components/BreadCrumbs/index.vue';
    import Lock from '@/layouts/components/Lock/index.vue';
    import RightTopUser from '../components/RightTopUser.vue';

    // 定义 Props 接口
    interface Props {
        layoutName: string;
        layoutSubName: string;
        menuData: RoutesDataItem[];
        menuCollapsed: boolean;
        belongTopMenu: string;
        defaultActive: string;
        breadCrumbs: BreadcrumbType[];
        routeItem: RoutesDataItem;
    }

    // 使用 defineProps 接收参数
    const props = defineProps<Props>();

    const route = useRoute();
    const settingStore = useSettingStore();

    // 计算属性：控制抽屉方向
    const direction = computed(() => settingStore.getMenuAnimation);

    // 计算属性：控制抽屉宽度
    const size = computed(() => settingStore.getMenuWidth);

    // 计算属性：控制抽屉显示状态 (双向绑定 v-model)
    // 注意：el-drawer 的 v-model 需要可写的 ref 或 computed getter/setter
    // 如果 settingStore.getMenuCollapsed 只是 getter，则需要通过 emit 或 store action 修改
    // 这里假设 settingStore 支持直接 patch 或通过 action 修改
    const menuDrawer = computed({
        get: () => settingStore.getMenuCollapsed,
        set: (val) => {
            // 当抽屉关闭时（val 为 false），触发关闭逻辑
            if (!val) {
                toggleCollapsedFunc();
            }
        }
    });

    // 关闭抽屉时的回调
    const toggleCollapsedFunc = () => {
        settingStore.$patch({
            menuCollapsed: false
        });
    };
</script>

<style lang="scss" scoped>
    @import '@/theme/global-vars.scss';

    #indexlayout {
        display: flex;
        height: 100vh;
        overflow: hidden;
    }

    #indexlayout-right {
        position: relative;
        flex: 1;
        overflow: auto;
        scrollbar-width: none; // Firefox
        -ms-overflow-style: none; // IE/Edge
        background-color: var(--bg-color, #f5f7fa); // 提供默认背景色

        &::-webkit-scrollbar {
            display: none; // Chrome/Safari
        }

        &.fiexd-header {
            display: flex;
            flex-direction: column;

            .indexlayout-right-main {
                flex: 1;
                overflow: auto;
                scrollbar-width: none;
                background-color: #eef0f7;
                padding: $mobile-main-padding;
                padding-top: 0;

                & > .breadcrumbs {
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                    height: $headerBreadcrumbHeight;
                }
            }
        }
    }

    .indexlayout-main-conent {
        margin: 24px;
        position: relative;
    }

    :deep(.custom-drawer-width) {
        width: calc($leftSideBarWidth + 10px) !important;
    }
</style>
