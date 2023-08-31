<!--
 * @Author: your name
 * @Date: 2022-01-13 17:31:19
 * @LastEditTime: 2023-08-03 09:44:01
 * @LastEditors: mengjuhua
 * @Description:   屏幕锁定
-->
<script lang="ts" setup>
    import { useSettingStore } from '@/store/modules/settingStore';
    import { computed, ref } from 'vue-demi';
    const settingStore = useSettingStore();
    const unlockScreenPwd = computed(() => settingStore.getUnlockScreenPwd);

    // 绑定输入数据
    const inputPwd = ref('');
    // 密码错误提示
    const showError = ref(false);
    // 密码验证
    const checkPwdFunc = () => {
        if (inputPwd.value === unlockScreenPwd.value) {
            settingStore.$patch({
                lockScreen: false,
            });
        } else {
            showError.value = true;
        }
    };

    //
    const showInput = ref('hidden');
    // 更换网络背景图片
    const imageUrl = ref('');
    const changeImageUrlFunc = () => {
        settingStore.$patch({
            lockScreenImage: imageUrl.value,
        });
        showInput.value = 'hidden';
    };
</script>

<template>
    <div class="lock-pane"></div>
    <div class="content">
        <img src="https://i.gtimg.cn/club/item/face/img/2/16022_100.gif" alt srcset />
        <!-- <i class="ri-lock-2-fill"></i> -->
        <span> {{ $t('屏幕已锁定') }}</span>
        <div class="form">
            <span :class="{ showErrorText: showError }">{{ $t('密码错误') }}</span>
            <el-input
                v-model="inputPwd"
                type="password"
                placeholder="Please input password"
                show-password
                @change="checkPwdFunc"
                @focus="showError = false"
            />
            <el-button type="primary" @click="checkPwdFunc" @mousemove="toopTipsVisible = false">
                <i class="ri-lock-unlock-line"></i>{{ $t('解锁') }}
            </el-button>
        </div>
        <!-- <a @click="showInput='visible'">{{ $t('更换网络图片作为锁屏背景') }}...</a> -->
        <!-- <el-input 
            :style="{'visibility': showInput}"
            v-model="imageUrl" 
            @change="changeImageUrlFunc"
            :placeholder="`jpg png svg ... Ctrl+C & V ${$t('粘贴')}URL`"
        ></el-input> -->
    </div>
</template>

<style lang="scss" scoped>
    .lock-pane {
        width: 400px;
        height: 400px;
        text-align: center;
        padding: 50px;
        border-radius: 15px;
        color: var(--el-text-color-primary);
        background-color: var(--el-bg-color);
        opacity: 0.6;
        box-sizing: border-box;
    }
    .content {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        position: absolute;

        & > img {
            width: 180px;
            height: 180px;
            border-radius: 50%;
            overflow: hidden;
            // margin-bottom: 25px;
        }
        & > span {
            margin: 25px 0;
        }
        & > div.form {
            display: flex;
            justify-content: center;
            align-items: center;

            & > button {
                line-height: 25px;
                border: none;
                border-top-left-radius: 0;
                border-bottom-left-radius: 0;
                position: relative;
                left: -2px;
                & > i {
                    position: relative;
                    top: 2px;
                    margin-right: 3px;
                }
            }
            & > span {
                display: none;
                font-size: 14px;
                letter-spacing: 1px;
                color: #f40;
                position: absolute;
                z-index: 1;
                &.showErrorText {
                    display: block;
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
