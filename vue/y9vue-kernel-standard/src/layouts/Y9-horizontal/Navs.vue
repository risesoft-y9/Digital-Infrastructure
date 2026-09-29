<script lang="ts" setup>
    import { inject, onMounted, ref } from 'vue';
    import SiderMenu from '@/layouts/components/SiderMenu.vue';

    // 注入字体变量
    const fontSizeObj: any = inject('sizeObjInfo');

    interface Props {
        menuCollapsed?: boolean;
        belongTopMenu?: string;
        defaultActive?: string;
        menuData?: any[];
    }

    const props = withDefaults(defineProps<Props>(), {
        menuCollapsed: false,
        belongTopMenu: '',
        defaultActive: '',
        menuData: () => []
    });

    // --- 滚动逻辑 ---
    const menuContainerRef = ref<HTMLElement | null>(null);

    onMounted(() => {
        const container = menuContainerRef.value;
        if (container) {
            // 监听滚轮事件，将垂直滚动转换为水平滚动
            const handleWheel = (evt: WheelEvent) => {
                // 仅当内容宽度超过容器宽度时生效
                if (container.scrollWidth > container.clientWidth) {
                    // 阻止默认的页面垂直滚动
                    evt.preventDefault();
                    // 将滚轮的 deltaY 转换为 scrollLeft 的变化
                    container.scrollLeft += evt.deltaY;
                }
            };

            // passive: false 允许调用 preventDefault
            container.addEventListener('wheel', handleWheel, { passive: false });
        }
    });
</script>
<template>
    <div ref="menuContainerRef" class="header-menu-container">
        <sider-menu
            :belong-top-menu="belongTopMenu"
            :default-active="defaultActive"
            :menu-collapsed="menuCollapsed"
            :menu-data="menuData"
            class="custom-horizontal-menu"
            menu-mode="horizontal"
            popper-class="my-custom-popup"
        />
    </div>
</template>

<style lang="scss" scoped>
    @import '@/theme/global-vars.scss';

    .header-menu-container {
        width: 100%;
        background-color: var(--el-bg-color);
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
        z-index: 100;
        position: relative;

        // --- 核心滚动样式 ---
        overflow-x: auto; // 允许横向滚动
        overflow-y: hidden; // 隐藏纵向滚动

        // 隐藏滚动条 (Chrome, Safari, Edge)
        &::-webkit-scrollbar {
            display: none;
        }
        // 隐藏滚动条 (Firefox)
        scrollbar-width: none;

        // 确保菜单项单行排列，不换行
        :deep(.custom-horizontal-menu) {
            // 去除 el-link 或 router-link 的下划线
            .el-link,
            a {
                text-decoration: none !important;
            }
            .el-menu {
                display: flex;
                flex-wrap: nowrap; // 强制不换行
                min-width: max-content; // 宽度由内容决定，不被容器压缩
                border-right: none;
                border-bottom: none;
            }

            .el-menu-item,
            .el-sub-menu__title {
                color: var(--el-text-color-primary);
                background-color: transparent;
                transition: all 0.3s ease;

                // 防止文字换行导致高度不一致
                white-space: nowrap;

                i {
                    font-size: v-bind('fontSizeObj?.largeFontSize || "16px"');
                }

                &:hover {
                    background-color: var(--el-color-primary-light-9);
                    color: var(--el-color-primary);
                }

                &.is-active {
                    color: var(--el-color-primary);
                    background-color: var(--el-color-primary-light-9);
                    border-bottom: 2px solid var(--el-color-primary);
                }
            }

            // 选中二级菜单后，一级菜单标题也显示选中样式
            .el-sub-menu.is-active > .el-sub-menu__title {
                color: var(--el-color-primary);
                border-bottom: 2px solid var(--el-color-primary);
                height: 95%;
            }
        }
    }
</style>

<style lang="scss">
    // 全局样式：专门用于 popper 弹出层，因为 popper 默认挂载在 body 下，scoped 无法穿透
    .my-custom-popup.el-menu--popup {
        text-decoration: none;

        .el-menu-item {
            text-align: center;
            color: var(--el-text-color-primary);
            background-color: var(--el-bg-color);

            i {
                margin-right: 10px;
            }

            &:hover {
                background-color: var(--el-color-primary-light-9);
            }
        }
    }
</style>
