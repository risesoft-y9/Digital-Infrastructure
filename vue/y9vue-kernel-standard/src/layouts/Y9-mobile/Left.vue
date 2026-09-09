<template>
    <div
        id="indexlayout-left"
        :class="{
            narrow: menuCollapsed,
            'sidebar-separate': layoutSubName === 'sidebar-separate'
        }"
    >
        <div class="indexlayout-left-logo">
            <router-link class="logo-url" to="/">
                <img alt="y9-logo" src="@/assets/images/yunLogo.png" />
                <!-- 增加空值保护，防止 fontSizeObj 未注入时报错 -->
                <span v-if="!menuCollapsed" class="logo-title">
                    {{ $t('数字底座') }}
                </span>
            </router-link>
        </div>
        <div class="indexlayout-left-menu">
            <sider-menu
                :belong-top-menu="belongTopMenu"
                :default-active="defaultActive"
                :menu-collapsed="menuCollapsed"
                :menu-data="menuData"
            />
        </div>
    </div>
</template>

<script lang="ts" setup>
    import { computed, inject } from 'vue';
    import SiderMenu from '@/layouts/components/SiderMenu.vue';
    import type { RoutesDataItem } from '@/utils/routes'; // 假设你有这个类型定义

    // 1. 正确定义 Props 接口
    interface LeftLayoutProps {
        menuCollapsed: boolean;
        belongTopMenu: string;
        defaultActive: string;
        menuData: RoutesDataItem[];
        layoutSubName: string;
    }

    // 2. 使用泛型 defineProps
    const props = withDefaults(defineProps<LeftLayoutProps>(), {
        belongTopMenu: '',
        defaultActive: '',
        menuData: () => []
    });

    // 3. 安全地注入字体变量
    // 提供默认值，防止 inject 返回 undefined 导致 CSS v-bind 出错
    const fontSizeObj = inject('sizeObjInfo', { largeFontSize: '14px' });

    // 如果 sizeObjInfo 是响应式对象，v-bind 会自动追踪变化
    // 如果不是响应式，可能需要将其转换为 ref 或 computed
    // 假设 inject 返回的是一个响应式对象或普通对象
    const fontSizeVars = computed(() => {
        return {
            '--font-size-large': fontSizeObj?.largeFontSize || '14px'
        };
    });
</script>

<style lang="scss" scoped>
    @import '@/theme/global-vars.scss';

    $sidebar-separate-margin-top: calc(#{$sidebar-separate-margin-left} + #{$headerHeight});
    $sidebar-separate-menu-height: calc(100vh - #{$sidebar-separate-margin-top});

    #indexlayout-left {
        display: flex;
        height: 100vh;
        flex-direction: column;
        width: $leftSideBarWidth;
        background-color: var(--el-bg-color);
        transition-duration: 0.1s;
        transition-property: width; // 明确过渡属性，性能更好

        &.sidebar-separate {
            position: absolute;
            z-index: 1;
            left: $sidebar-separate-margin-left;
            top: $sidebar-separate-margin-top;
            height: $sidebar-separate-menu-height;
            // 分离模式下可能需要圆角或阴影
            // border-radius: 4px;
            // box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
        }

        .indexlayout-left-logo {
            width: 100%;
            height: $headerHeight;
            line-height: $headerHeight;
            text-align: left;
            vertical-align: middle;
            flex-shrink: 0; // 防止 logo 区域被压缩

            .logo-url {
                display: inline-flex;
                align-items: center;
                width: 100%;
                height: 100%;
                overflow: hidden;
                text-decoration: none; // 移除链接下划线

                .logo-title {
                    display: inline-block;
                    margin-left: 15px;
                    // 使用 CSS 变量或直接绑定，确保安全性
                    font-size: v-bind('fontSizeVars["--font-size-large"]');
                    font-family: Roboto, sans-serif;
                    color: var(--el-text-color-primary);
                    white-space: nowrap; // 防止文字换行
                }
            }

            img {
                width: $logoWidth;
                vertical-align: middle;
            }
        }

        .indexlayout-left-menu {
            flex: 1;
            overflow: hidden auto;
            position: relative;

            // 深度选择器优化
            :deep(.el-menu) {
                border-right: none;
                background-color: transparent; // 通常菜单背景由容器控制

                .el-menu-item,
                .el-sub-menu__title {
                    color: var(--el-text-color-primary);

                    i {
                        margin-right: 10px;
                        font-size: v-bind('fontSizeVars["--font-size-large"]');
                    }

                    &.is-active {
                        color: var(--el-color-primary);
                    }

                    &:hover {
                        background-color: var(--el-menu-hover-bg-color, rgba(0, 0, 0, 0.05));
                    }
                }
            }
        }

        &.narrow {
            width: $menu-collapsed-width;

            // 折叠时隐藏文字，只显示图标
            .logo-title {
                display: none;
            }
        }

        @include scrollbar;
    }
</style>
