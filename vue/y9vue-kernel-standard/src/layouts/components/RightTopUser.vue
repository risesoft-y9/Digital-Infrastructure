<template>
    <el-dropdown @command="onMenuClick" :hide-on-click="true" class="user-el-dropdown">
        <div class="name" @click="(e) => e.preventDefault()">
            <!-- show & if 的vue指令 仅用于适配移动端 -->
            <div v-show="settingStore.getWindowWidth > 425">
                <span>{{ $t(`${userInfo.name}`) }}</span>
                <span>{{ initInfo?.department?.name }}</span>
            </div>
            <el-avatar
                v-if="settingStore.device === 'mobile'"
                :style="{
                    'font-size': fontSizeObj.baseFontSize,
                    'background-color': 'var(--el-color-primary)',
                    'margin-top': '8px',
                }"
                :src="userInfo.avator ? userInfo.avator : ''"
            >
                {{ userInfo.loginName }}
            </el-avatar>
        </div>
        <template #dropdown>
            <el-dropdown-menu>
                <el-dropdown-item command="personalCenter">
                    <div
                        class="el-dropdown-item"
                        :style="{ 'font-size': fontSizeObj.baseFontSize, 'line-height': fontSizeObj.lineHeight }"
                    >
                        <i class="ri-user-line"></i>{{ $t('个人中心') }}
                    </div>
                </el-dropdown-item>
                <!-- <el-dropdown-item command="signIn">
                    <div class="el-dropdown-item" :style="{'font-size': fontSizeObj.baseFontSize, 'line-height': fontSizeObj.lineHeight}">
                        <i class="ri-calendar-check-line"></i>{{ $t("已签到") }}
                    </div>
                </el-dropdown-item>
                <el-dropdown-item command="signOut">
                    <div class="el-dropdown-item" :style="{'font-size': fontSizeObj.baseFontSize, 'line-height': fontSizeObj.lineHeight}">
                        <i class="ri-bookmark-line"></i>{{ $t("已签退") }}
                    </div>
                </el-dropdown-item>
                <el-divider style="padding-bottom: 12px;margin: 0px;margin-top: 6px;"></el-divider>
                <el-dropdown-item command="changeDept">
                    <div class="el-dropdown-item" :style="{'font-size': fontSizeObj.baseFontSize, 'line-height': fontSizeObj.lineHeight}">
                        <i class="ri-route-line"></i>{{ $t("选择切换部门") }}
                    </div>
                </el-dropdown-item>
                <el-dropdown-item>
                    <div
                        class="el-dropdown-item"
                        :style="{'font-size': fontSizeObj.baseFontSize, 'line-height': fontSizeObj.lineHeight}"
                        v-for="item in departmentMapList"
                        :key="item.departmentId"
                        style="text-align: center"
                        @click="changeDept(item.departmentId)"
                    >{{ item.departmentName }}</div>
                </el-dropdown-item>
                <el-divider style="padding-bottom: 5px;margin: 0px;"></el-divider> -->
                <el-dropdown-item command="logout">
                    <div
                        class="el-dropdown-item"
                        :style="{ 'font-size': fontSizeObj.baseFontSize, 'line-height': fontSizeObj.lineHeight }"
                    >
                        <i class="ri-logout-box-r-line"></i>{{ $t('退出') }}
                    </div>
                </el-dropdown-item>
            </el-dropdown-menu>
        </template>
    </el-dropdown>
</template>
<script lang="ts" setup>
    import { ref, watch, inject, defineComponent } from 'vue';
    import { useRouter } from 'vue-router';
    import { useSettingStore } from '@/store/modules/settingStore';
    import y9_storage from '@/utils/storage';
    import { $y9_SSO } from '@/main';
    import { ElMessage } from 'element-plus';
    interface RightTopUserSetupData {
        settingStore?: any;
        userInfo: Object;
        initInfo: Object;
        departmentMapList: Object;
        onMenuClick: (event: any) => Promise<void>;
        fontSizeObj: Object;
    }

    const settingStore = useSettingStore();
    // 注入 字体变量
    const fontSizeObj: any = inject('sizeObjInfo');
    const router = useRouter();
    // const personInfo = ref();
    // 获取当前登录用户信息
    const userInfo = JSON.parse(sessionStorage.getItem('ssoUserInfo'));

    const initInfo = y9_storage.getObjectItem('initInfo');
    const departmentMapList = y9_storage.getObjectItem('departmentMapList');
    // 点击菜单
    const onMenuClick = async (command: string) => {
        switch (command) {
            case 'personalCenter':
                // personInfo.value.show(userInfo.personId);
                router.push({ name: 'personInfo' });
                break;
            case 'signIn':
                break;
            case 'signOut':
                break;
            case 'changeDept':
                break;
            case 'logout':
                try {
                    // const loginOut = await this.$store.dispatch("user/logout");
                    const params = {
                        to: { path: window.location.pathname },
                        logoutUrl: import.meta.env.VUE_APP_SSO_LOGOUT_URL + import.meta.env.VUE_APP_NAME + '/',
                        __y9delete__: () => {
                            // 删除前执行的函数
                            console.log('删除前执行的函数');
                        },
                    };
                    $y9_SSO.ssoLogout(params);
                } catch (error) {
                    ElMessage({
                        message: error.message || 'Has Error',
                        type: 'error',
                        duration: 5 * 1000,
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
        :deep(.el-dropdown--default) {
            display: flex;
            align-items: center;
        }
    }
    .name {
        color: var(--el-text-color-primary);
        font-size: v-bind('fontSizeObj.baseFontSize');
        display: flex;
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
    }
</style>
