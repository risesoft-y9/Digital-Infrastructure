<template>
    <el-dropdown :hide-on-click="true" class="user-el-dropdown" @command="onMenuClick">
        <div class="name">
            <!-- show & if 的vue指令 仅用于适配移动端 -->
            <div v-show="settingStore.getWindowWidth > 425">
                <span>{{ $t(userInfo.name) }}</span>
            </div>
            <el-avatar
                v-if="settingStore.device === 'mobile'"
                :src="userInfo.avator || ''"
                :style="{
                    fontSize: fontSizeObj?.baseFontSize,
                    backgroundColor: 'var(--el-color-primary)',
                    marginTop: '8px'
                }"
            >
                {{ userInfo.loginName }}
            </el-avatar>
        </div>
        <template #dropdown>
            <el-dropdown-menu>
                <el-dropdown-item command="personalCenter">
                    <div
                        class="el-dropdown-item"
                        :style="{ 'font-size': fontSizeObj?.baseFontSize, 'line-height': fontSizeObj?.lineHeight }"
                    >
                        <i class="ri-user-line"></i>
                        <span>{{ $t('个人中心') }}</span>
                    </div>
                </el-dropdown-item>
                <!-- <el-divider style="padding-bottom: 5px;margin: 0px;"></el-divider> -->
                <el-dropdown-item command="logout">
                    <div
                        class="el-dropdown-item"
                        :style="{ 'font-size': fontSizeObj?.baseFontSize, 'line-height': fontSizeObj?.lineHeight }"
                    >
                        <i class="ri-logout-box-r-line"></i>
                        <span>{{ $t('退出') }}</span>
                    </div>
                </el-dropdown-item>
            </el-dropdown-menu>
        </template>
    </el-dropdown>
</template>

<script setup lang="ts">
    import { inject } from 'vue';
    import { useRouter } from 'vue-router';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { $y9_SSO } from '@/main';

    const settingStore = useSettingStore();
    const router = useRouter();

    const fontSizeObj = inject('sizeObjInfo') as Record<string, string> | undefined;

    const getSafeUserInfo = () => {
        try {
            return JSON.parse(sessionStorage.getItem('ssoUserInfo') || '{}');
        } catch (e) {
            console.error('解析 ssoUserInfo 失败:', e);
            return {};
        }
    };
    const userInfo = getSafeUserInfo();

    // 点击菜单
    const onMenuClick = async (command: string) => {
        switch (command) {
            case 'personalCenter':
                router.push({ name: 'personInfo' });
                break;
            case 'logout':
                try {
                    const params = {
                        redirect_uri: window.location.origin + import.meta.env.VUE_APP_PUBLIC_PATH
                    };
                    $y9_SSO.ssoLogout(params);
                } catch (error: any) {
                    ElMessage({
                        message: error.message || 'Has Error',
                        type: 'error',
                        duration: 5000
                    });
                }
                break;
            default:
                break;
        }
    };
</script>

<style lang="scss" scoped>
    @import '@/theme/global-vars.scss';

    .user-el-dropdown {
        z-index: 9999;
        height: $headerHeight;
        outline: none;

        :deep(.el-dropdown--default) {
            display: flex;
            align-items: center;
        }
    }

    .name {
        color: var(--el-text-color-primary);
        font-size: v-bind('fontSizeObj?.baseFontSize');
        display: flex;
        outline: none;

        & > div {
            display: flex;
            flex-direction: column;
            justify-content: center;

            span {
                line-height: 20px;
                text-align: center;
            }
        }

        i {
            color: var(--el-color-primary);
            font-size: 48px;
            margin-left: 8px;
        }
    }

    .el-dropdown-item {
        width: 100%;
        display: flex;
        align-items: center; // 6. 优化：图标和文字垂直居中
    }
</style>
