<template>
    <div id="indexlayout">
        <!-- 
          注意：如果这是移动端专用布局，建议不要直接复用 PC 端的 menuCollapsed 状态，
          或者确保 Store 中有独立的 mobileDrawerVisible 状态。
          此处假设必须复用 store 状态，改用 :model-value 和 @update:model-value 模式更清晰
        -->
        <el-drawer
            v-model="drawerVisible"
            :direction="direction"
            :size="size"
            z-index="2000"
            @close="handleDrawerClose"
        >
            <template #default>
                <div class="drawer-header-user">
                    <RightTopUser />
                </div>

                <Left
                    :belong-top-menu="belongTopMenu"
                    :default-active="defaultActive"
                    :layout-sub-name="layoutSubName"
                    :menu-collapsed="isMenuCollapsedForLeft"
                    :menu-data="menuData"
                />
            </template>
        </el-drawer>

        <div id="indexlayout-right" class="fiexd-header">
            <RightTop :menu-collapsed="menuCollapsed" style="z-index: 1999" />
            <div class="indexlayout-right-main">
                <BreadCrumbs :list="breadCrumbs" />
                <router-view />
            </div>
        </div>

        <SettingsMobile />
        <Lock v-show="settingStore.getLockScreen" />
        <Search />
    </div>
</template>

<script lang="ts" setup>
    import { computed } from 'vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    import type { BreadcrumbType, RoutesDataItem } from '@/utils/routes';
    import Left from './Left.vue';
    import RightTop from './RightTop.vue';
    import SettingsMobile from '@/layouts/components/SettingsMobile.vue';
    import BreadCrumbs from '@/layouts/components/BreadCrumbs/index.vue';
    import Lock from '@/layouts/components/Lock/index.vue';
    import Search from '@/layouts/components/search/index.vue';
    import RightTopUser from '../components/RightTopUser.vue';

    // 1. 正确定义 Props 类型
    interface LayoutProps {
        layoutName: string;
        layoutSubName: string;
        menuData: RoutesDataItem[];
        menuCollapsed?: boolean;
        belongTopMenu: string;
        defaultActive: string;
        breadCrumbs: BreadcrumbType[];
        routeItem: RoutesDataItem;
    }

    const props = defineProps<LayoutProps>();

    const settingStore = useSettingStore();

    // 2. 优化 Drawer 状态管理
    // 如果 menuCollapsed 在移动端代表“抽屉是否打开”，则直接映射
    // 注意：Element Plus Drawer 的 v-model 期望一个 boolean 的 ref 或者可写 computed
    const drawerVisible = computed({
        get: () => settingStore.getMenuCollapsed,
        set: (val) => {
            // 当 Drawer 关闭时(val=false)，更新 Store
            // 当 Drawer 打开时(val=true)，更新 Store
            settingStore.menuCollapsed = val;
        }
    });

    const handleDrawerClose = () => {
        // 如果需要在关闭时执行额外逻辑，可以在这里添加
        // 由于使用了 computed setter，这里其实可以省略，除非有副作用
        console.log('Drawer closed');
    };

    // 3. 处理 Left 组件的 menuCollapsed 逻辑
    // 原代码是 !menuCollapsed，这里保留你的业务逻辑，但建议重命名变量以清晰表达意图
    // 假设 Left 组件的 menuCollapsed 意为 "isCollapsed" (是否折叠/隐藏)
    // 如果 Drawer 打开了(可见)，通常意味着菜单是“展开”的，所以对于 Left 内部来说可能是 "false" (未折叠)
    // 请根据 Left.vue 的实际实现确认这里是否需要取反
    const isMenuCollapsedForLeft = computed(() => !props.menuCollapsed);

    // 4. 其他计算属性
    const direction = computed(() => settingStore.getMenuAnimation);
    const size = computed(() => settingStore.getMenuWidth);
</script>

<style lang="scss" scoped>
    @import '@/theme/global-vars.scss';

    #indexlayout {
        display: flex;
        height: 100vh;
        overflow: hidden;
    }

    .drawer-header-user {
        position: fixed;
        left: var(--el-dialog-padding-primary, 20px); /* 提供 fallback */
        top: 4px;
        z-index: 10; /* 确保在 Drawer 内容之上 */
    }

    #indexlayout-right {
        position: relative;
        flex: 1;
        overflow: auto;
        scrollbar-width: none;
        background-color: var(--bg-color, #fff);

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
</style>
