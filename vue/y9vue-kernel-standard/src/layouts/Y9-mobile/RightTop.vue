<script lang="ts" setup>
    import RightTopUser from '../components/RightTopUser.vue';
    import { watch, inject, ref } from 'vue';
    import { Edit, FullScreen } from '@element-plus/icons';
    import { useSettingStore } from '@/store/modules/settingStore';

    const props = defineProps({
        menuCollapsed: {
            type: Boolean,
            default: false,
        },
    });

    // 全屏功能
    const { isFullscreen, toggle } = useFullscreen();
    const toggleFullScreen = toggle;

    // 菜单收缩功能
    const settingStore = useSettingStore();
    // 注入 字体变量
    const fontSizeObj: any = inject('sizeObjInfo');
    const { toggleCollapsed } = settingStore;
    const toggleCollapsedFunc = () => {
        toggleCollapsed();
    };

    // 白天黑夜功能
    const isDark = useDark({
        selector: 'html',
        valueDark: 'theme-dark',
        valueLight: '',
    });
    const toggleDark = useToggle(isDark);

    // 锁屏
    const lockScreenFunc = () => {
        settingStore.$patch({
            lockScreen: true,
        });
    };

    // 搜索
    const searchFunc = () => {};
    // 刷新
    const refreshFunc = () => {
        window.location = window.location.href;
    };
</script>

<template>
    <div id="right-top">
        <div class="left">
            <div class="indexlayout-flexible" @click="toggleCollapsedFunc">
                <i class="ri-menu-fold-line" v-if="menuCollapsed"></i>
                <i class="ri-menu-unfold-line" v-else></i>
            </div>
        </div>
        <div class="right">
            <div class="item" @click="refreshFunc" v-show="settingStore.getRefresh">
                <i class="ri-refresh-line"></i>
                <span></span>
            </div>
            <!-- <div class="item search" @click="searchFunc" v-show="settingStore.getSearch">
                <i class="ri-search-line"></i>
                <span></span>
            </div> -->
            <div class="item" @click="lockScreenFunc" v-show="settingStore.getLock">
                <i class="ri-lock-2-line"></i>
                <span></span>
            </div>
            <div class="item full-screen" @click="toggleFullScreen">
                <i class="ri-fullscreen-line"></i>
                <span v-show="settingStore.getWindowWidth > 425">{{ $t('全屏') }}</span>
            </div>
            <div class="item web-setting">
                <i class="ri-edit-box-line"></i>
                <span v-show="settingStore.getWindowWidth > 425">{{ $t('设置') }}</span>
            </div>
            <!-- <div class="item notify">
                <el-badge :value="3" class="badge"></el-badge>
                <i class="ri-notification-line"></i>
            </div> -->
            <div class="item isDark">
                <i class="ri-moon-line" @click="toggleDark" v-if="!isDark"></i>
                <i class="ri-sun-line" @click="toggleDark" v-else></i>
            </div>
            <div :class="{ item: true, user: true, 'user-mobile': settingStore.getWindowWidth > 425 }">
                <RightTopUser />
            </div>
        </div>
    </div>
</template>

<style lang="scss" scoped>
    @import '@/theme/global-vars.scss';
    #right-top {
        background-color: var(--el-bg-color);
        color: var(--el-text-color-primary);
        display: flex;
        justify-content: space-between;
        width: 100%;
        border-bottom: 1px solid var(--el-color-primary-light-9);
        .left,
        .right {
            display: flex;
            // height: $headerHeight;
            line-height: 60px;
            & > .item {
                overflow: hidden;
                padding: 0 5px;
                min-width: 25px;
                display: flex;
                align-items: center;
                i {
                    position: relative;
                    font-size: v-bind('fontSizeObj.extraLargeFont');
                    // top: 4px;
                }
                span {
                    font-size: v-bind('fontSizeObj.baseFontSize');
                }
            }
        }

        .indexlayout-flexible {
            width: $headerHeight;
            // height: $headerHeight;
            line-height: 60px;
            text-align: center;
            cursor: pointer;
            font-size: v-bind('fontSizeObj.extraLargeFont');
            &:hover {
                background-color: var(--bg-color);
                color: var(--el-text-color-primary);
            }
        }
        .right {
            & > .item {
                &.notify {
                    .badge {
                        position: absolute;
                        z-index: 1;
                        & > .el-badge__content--danger {
                            background-color: var(--el-color-danger);
                        }
                    }
                }
            }
            .user {
                display: flex;
                align-items: center;
                padding-right: 10px;
                & > .name {
                    // font-size: v-bind(fontSize);
                    display: flex;
                    flex-direction: column;
                    justify-content: end;
                    span {
                        line-height: 60px;
                        text-align: end;
                    }
                }
            }
            .user-mobile {
                min-width: 202px;
            }
        }
    }
</style>
