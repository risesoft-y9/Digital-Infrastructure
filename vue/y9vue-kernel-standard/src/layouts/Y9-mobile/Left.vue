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
                <span v-if="!menuCollapsed" class="logo-title">{{ $t('数字底座') }}</span>
            </router-link>
        </div>
        <div class="indexlayout-left-menu">
            <sider-menu
                :belong-top-menu="belongTopMenu"
                :default-active="defaultActive"
                :menu-collapsed="menuCollapsed"
                :menu-data="menuData"
            ></sider-menu>
        </div>
    </div>
</template>
<script lang="ts" setup>
    import { inject } from 'vue';
    import SiderMenu from '@/layouts/components/SiderMenu.vue';

    // 注入字体变量
    const fontSizeObj: any = inject('sizeObjInfo');

    // 定义 Props 接口
    interface Props {
        menuCollapsed: boolean;
        belongTopMenu?: string;
        defaultActive?: string;
        menuData?: any[]; // 建议根据实际菜单数据结构定义更详细的类型，如 MenuItem[]
        layoutSubName: string;
    }

    // 使用 withDefaults 设置默认值
    const props = withDefaults(defineProps<Props>(), {
        belongTopMenu: '',
        defaultActive: '',
        menuData: () => []
    });
</script>

<style lang="scss" scoped>
    @import '@/theme/global-vars.scss';

    // 计算分离式侧边栏的 margin 和 height
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

        // 分离式侧边栏样式
        &.sidebar-separate {
            position: absolute;
            z-index: 1;
            left: $sidebar-separate-margin-left;
            top: $sidebar-separate-margin-top;
            height: $sidebar-separate-menu-height;
            // 通常分离式需要添加阴影或边框以区分层级
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
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
                    // 使用 v-bind 将 JS 变量绑定到 CSS
                    font-size: v-bind('fontSizeObj.largeFontSize');
                    font-family: Roboto, sans-serif;
                    color: var(--el-text-color-primary);
                    white-space: nowrap; // 防止文字换行
                }
            }

            img {
                width: $logoWidth;
                vertical-align: middle;
                object-fit: contain; // 保持图片比例
            }
        }

        .indexlayout-left-menu {
            flex: 1;
            overflow: hidden auto; // 允许菜单滚动

            :deep(ul) {
                border-right: none;
                background-color: var(--el-bg-color);
            }

            :deep(a) {
                text-decoration: none;
                display: block;

                .is-active {
                    color: var(--el-color-primary);
                }

                li {
                    color: var(--el-text-color-primary);
                    transition: background-color 0.2s;

                    i {
                        margin-right: 10px;
                        font-size: v-bind('fontSizeObj.largeFontSize');
                    }
                }

                li:hover {
                    background-color: var(--el-fill-color-light); // 使用 Element Plus 变量
                }
            }
        }

        // 折叠状态样式
        &.narrow {
            width: $menu-collapsed-width;

            .logo-title {
                opacity: 0;
                visibility: hidden;
            }
        }

        // 引入全局滚动条样式 mixin
        @include scrollbar;
    }
</style>
