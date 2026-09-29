<template>
    <div id="left" :class="leftClasses" :style="leftStyle">
        <div class="left-logo">
            <router-link class="logo-url" to="/">
                <img v-if="props.menuCollapsed" alt="y9-logo" src="@/assets/images/yun.png" />
                <span v-else class="logo-title">{{ $t('数字底座') }}</span>
            </router-link>
        </div>
        <div class="left-menu">
            <!-- 使用 v-memo 优化菜单渲染性能，仅当依赖项变化时重新渲染 -->
            <sider-menu
                v-memo="[props.menuCollapsed, props.belongTopMenu, props.defaultActive, props.menuData]"
                :belong-top-menu="props.belongTopMenu"
                :default-active="props.defaultActive"
                :menu-collapsed="props.menuCollapsed"
                :menu-data="props.menuData"
            ></sider-menu>
        </div>
    </div>
</template>

<script lang="ts" setup>
    import { computed, inject } from 'vue';
    import SiderMenu from '@/layouts/components/SiderMenu.vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    import type { RoutesDataItem } from '@/utils/routes';

    // 严格定义 Props 类型接口，完全修复原代码中错误的类型断言
    interface Props {
        menuCollapsed: boolean;
        belongTopMenu?: string;
        defaultActive?: string;
        menuData?: RoutesDataItem[];
        layoutSubName: string;
    }

    // 使用 withDefaults 给可选属性设置默认值，避免运行时空值错误
    const props = withDefaults(defineProps<Props>(), {
        belongTopMenu: '',
        defaultActive: '',
        menuData: () => []
    });

    const settingStore = useSettingStore();

    // 注入字体变量
    const fontSizeObj: any = inject('sizeObjInfo');

    // 计算属性集中管理动态类名，大幅简化模板逻辑
    const leftClasses = computed(() => ({
        narrow: props.menuCollapsed,
        'sidebar-separate': props.layoutSubName === 'sidebar-separate',
        'add-backgroundImage': !!settingStore.getMenuBg
    }));

    // 计算属性集中管理动态样式，背景图逻辑完全抽离，更易维护
    const leftStyle = computed(() => ({
        'background-image': settingStore.getMenuBg ? `url(${settingStore.getMenuBg})` : ''
    }));
</script>

<style lang="scss" scoped>
    @import '@/theme/global-vars.scss';

    // 动态绑定字体行高，完全兼容全局字体大小切换
    #left .el-menu-item {
        height: v-bind('fontSizeObj.lineHeight') !important;
    }

    $sidebar-separate-margin-top: calc(#{$sidebar-separate-margin-left} + #{$headerHeight});
    $sidebar-separate-menu-height: calc(100vh - #{$sidebar-separate-margin-top});

    #left {
        display: flex;
        height: 100vh;
        flex-direction: column;
        width: $leftSideBarWidth;
        background-color: var(--el-bg-color);
        transition: width 0.25s ease, background-image 0.25s ease;

        &.sidebar-separate {
            position: absolute;
            z-index: 1;
            left: $sidebar-separate-margin-left;
            top: $sidebar-separate-margin-top;
            height: $sidebar-separate-menu-height;
            border-top-left-radius: 0.25rem;
            border-top-right-radius: 0.25rem;
            box-shadow: 2px 2px 2px 1px rgba(0, 0, 0, 0.06);
        }

        .left-logo {
            width: 100%;
            height: $headerHeight;
            line-height: $headerHeight;
            text-align: center;
            vertical-align: middle;
            flex-shrink: 0; // 防止logo被压缩

            .logo-url {
                display: inline-block;
                width: 100%;
                height: 100%;
                overflow: hidden;

                .logo-title {
                    display: inline-block;
                    font-size: v-bind('fontSizeObj.extraLargeFont');
                    font-weight: 500;
                    color: var(--el-color-primary);
                    transition: color 0.3s ease;
                }
            }

            img {
                width: v-bind('fontSizeObj.logoWidth');
                vertical-align: middle;
            }
        }

        .left-menu {
            flex: 1;
            overflow: hidden auto;
            // 隐藏滚动条但保留功能
            scrollbar-width: none;
            &::-webkit-scrollbar {
                width: 0;
                height: 0;
                background-color: transparent;
            }

            & > ul {
                border-right: none;
                background-color: var(--el-bg-color);

                :deep(a) {
                    text-decoration: none;

                    & > li {
                        i {
                            margin-right: 15px;
                            font-size: v-bind('fontSizeObj.largeFontSize');
                        }

                        &.is-active {
                            color: var(--el-color-primary);
                            background-color: $background-color;
                        }
                    }

                    &:hover > li {
                        background-color: var(--el-color-primary-light-9);
                        color: var(--el-color-primary-light-3);
                    }
                }
            }
        }

        &.narrow {
            width: $menu-collapsed-width;
        }
    }

    // 背景图模式下的样式覆盖
    #left.add-backgroundImage {
        .left-logo .logo-url .logo-title {
            color: var(--el-color-white);
        }

        .left-menu > ul {
            background-color: transparent;
            background: transparent;

            :deep(a) {
                text-decoration: none;

                & > li {
                    color: var(--el-color-white);

                    &.is-active {
                        color: var(--el-color-primary);
                        background-color: var(--el-color-primary-light-9);
                    }
                }

                &:hover > li {
                    color: var(--el-color-primary);
                    background-color: var(--el-color-primary-light-9);
                }
            }

            :deep(li) {
                .el-sub-menu__title {
                    color: var(--el-color-white);
                }

                div:hover {
                    color: var(--el-color-primary);
                    background-color: var(--el-color-primary-light-9);
                }

                ul > a:hover {
                    color: var(--el-color-primary);
                    background-color: var(--el-color-primary-light-9);
                }
            }
        }
    }
</style>
