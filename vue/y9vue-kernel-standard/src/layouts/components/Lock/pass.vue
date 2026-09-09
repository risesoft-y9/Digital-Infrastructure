<!--
 * @Author: your name
 * @Date: 2022-01-13 17:31:19
 * @LastEditTime: 2026-09-08 09:44:01
 * @LastEditors: mengjuhua
 * @Description:   屏幕锁定
-->
<template>
    <div class="lock-pane">
        <img :src="avatar" alt="锁屏头像" />
        <span> {{ $t('屏幕已锁定') }}</span>
        <div class="form">
            <span :class="{ showErrorText: showError }">{{ $t('密码错误') }}</span>
            <input autocomplete="new-password" hidden type="password" />
            <el-input
                v-model="inputPwd"
                placeholder="Please input password"
                show-password
                type="password"
                @change="checkPwdFunc"
                @focus="showError = false"
                @keyup.enter="checkPwdFunc"
            />
            <el-button type="primary" @click="checkPwdFunc">
                <i class="ri-lock-unlock-line"></i>{{ $t('解锁') }}
            </el-button>
        </div>
    </div>
</template>
<script lang="ts" setup>
    import { useSettingStore } from '@/store/modules/settingStore';
    import { computed, ComputedRef, ref, watch } from 'vue';
    import y9_storage from '@/utils/storage';
    import avatarDefault from '@/assets/images/avatar-default.gif';

    const settingStore = useSettingStore();

    // 定义解锁屏幕密码的计算属性，并明确类型为可能为空的字符串
    const unlockScreenPwd: ComputedRef<string | null> = computed(() => settingStore.getUnlockScreenPwd);
    // 定义锁屏状态的计算属性，类型为布尔值
    const lockStatus: ComputedRef<boolean> = computed(() => settingStore.getLockScreen);

    // 绑定输入数据，类型为字符串
    const inputPwd = ref('');
    // 密码错误提示，类型为布尔值
    const showError = ref(false);
    // 头像，类型为字符串
    const avatar = ref('');

    // 密码验证逻辑函数，明确返回类型为void
    const checkPwdFunc = (): void => {
        // 兼容默认无锁屏密码场景，避免密码未初始化时无法解锁
        if (!unlockScreenPwd.value || inputPwd.value === unlockScreenPwd.value) {
            // Pinia原生直接赋值，替代旧版$patch写法
            settingStore.lockScreen = false;
            // 解锁成功后清空输入框和错误状态
            inputPwd.value = '';
            showError.value = false;
        } else {
            showError.value = true;
        }
    };

    // 全局头像加载状态缓存，避免重复请求
    const avatarLoadStatusMap = new Map<string, 'pending' | 'success' | 'fail'>();

    // 获取用户头像信息的函数，明确返回类型为Promise<void>
    const getInfoAvatar = async (): Promise<void> => {
        const userInfo = y9_storage.getObjectItem('ssoUserInfo');
        const userAvatar = userInfo?.avatar || userInfo?.avator;

        if (!userAvatar) {
            avatar.value = avatarDefault;
            return;
        }

        // 已加载成功，直接复用结果
        if (avatarLoadStatusMap.get(userAvatar) === 'success') {
            avatar.value = userAvatar;
            return;
        }

        // 正在加载中，直接返回不重复发起请求
        if (avatarLoadStatusMap.get(userAvatar) === 'pending') {
            return;
        }

        // 标记为加载中，发起首次请求
        avatarLoadStatusMap.set(userAvatar, 'pending');
        try {
            await checkImgExists(userAvatar);
            avatarLoadStatusMap.set(userAvatar, 'success');
            avatar.value = userAvatar;
        } catch {
            avatarLoadStatusMap.set(userAvatar, 'fail');
            avatar.value = avatarDefault;
        }
    };

    // 优化watch监听，仅锁屏激活时触发一次
    watch(lockStatus, (newVal, oldVal) => {
        if (newVal === true && oldVal === false) {
            getInfoAvatar();
        }
    });

    // 检查图片是否存在的函数，明确返回类型为Promise<void>
    const checkImgExists = (imgUrl: string): Promise<void> => {
        return new Promise((resolve, reject) => {
            const imgObj = new Image();
            imgObj.src = imgUrl;
            imgObj.onload = () => resolve();
            imgObj.onerror = () => reject();
        });
    };

    // 在锁屏状态为真时，调用获取头像信息的函数
    if (lockStatus.value) {
        getInfoAvatar();
    }
</script>
<style lang="scss" scoped>
    .lock-pane {
        width: 400px;
        min-height: 480px;
        padding: 50px 30px;
        border-radius: 15px;
        color: var(--el-text-color-primary);
        background-color: var(--el-bg-color);
        opacity: 0.9;
        box-sizing: border-box;
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;

        & > img {
            width: 180px;
            height: 180px;
            border-radius: 50%;
            overflow: hidden;
            margin-bottom: 25px;
        }

        & > span {
            margin: 25px 0;
            font-size: 20px;
            font-weight: 500;
        }

        & > div.form {
            width: 100%;
            display: flex;
            flex-wrap: nowrap;
            position: relative;

            & > span {
                display: none;
                font-size: 14px;
                letter-spacing: 1px;
                color: #f40;
                position: absolute;
                top: -20px;
                z-index: 1;

                &.showErrorText {
                    display: block;
                }
            }

            & > .el-input {
                flex: 1;
            }

            & > button {
                line-height: 40px;
                border: none;
                border-top-left-radius: 0;
                border-bottom-left-radius: 0;

                & > i {
                    position: relative;
                    top: 2px;
                    margin-right: 3px;
                }
            }
        }

        & > a {
            margin-top: 30px;
            line-height: 30px;
            font-size: 12px;
            letter-spacing: 1px;
            color: var(--el-color-primary-light-3);
            text-decoration: underline;
            cursor: pointer;
        }
    }
</style>
