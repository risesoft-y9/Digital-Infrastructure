<template>
    <!-- 直接通过自定义渲染逻辑挂载根节点 -->
    <component :is="renderLockDiv" v-if="lockStatus" />
</template>
<script lang="ts" setup>
    import { computed, h, onUnmounted, watch } from 'vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { $y9_SSO } from '@/main';

    import Content from './pass.vue';

    const settingStore = useSettingStore();

    // 锁屏背景图响应式获取
    const bgUrl = computed(() => settingStore.getLockScreenImage);
    const lockStatus = computed(() => settingStore.getLockScreen);

    // 定时器统一管理，补充类型标注
    const timerList: number[] = [];

    // 安全获取布局根节点，增加空值保护
    const getLayoutEl = () => document.getElementById('indexlayout');

    // 锁屏状态核心处理逻辑
    function handleLockScreen() {
        if (lockStatus.value) {
            // 启动轮询监测，防止用户手动删除锁屏DOM
            const timerId = window.setInterval(() => {
                // 安全隐藏主布局，避免空元素报错
                const layoutEl = getLayoutEl();
                if (layoutEl) layoutEl.style.display = 'none';

                // 校验锁屏元素是否被恶意删除
                const lockDivEl = document.querySelector('.lock-div');
                if (!lockDivEl) {
                    // 锁屏DOM被篡改，触发SSO登出逻辑
                    const params = {
                        redirect_uri: window.location.origin + import.meta.env.VITE_APP_PUBLIC_PATH
                    };
                    $y9_SSO.ssoLogout(params);
                }
            }, 100);
            timerList.push(timerId);
        } else {
            // 解锁，恢复主布局显示，清空所有定时器
            const layoutEl = getLayoutEl();
            if (layoutEl) layoutEl.style.display = '';
            clearAllTimers();
        }
    }

    // 统一清理所有定时器
    function clearAllTimers() {
        timerList.forEach((timerId) => clearInterval(timerId));
        timerList.length = 0;
    }

    // 组件初始化时执行一次状态处理
    if (lockStatus.value) {
        handleLockScreen();
    }

    // 响应监听锁屏状态变化
    watch(lockStatus, () => {
        handleLockScreen();
    });

    // 组件卸载时强制清理所有副作用，避免内存泄漏
    onUnmounted(() => {
        clearAllTimers();
    });

    // 定义渲染函数，构建全屏锁屏容器
    const renderLockDiv = () => {
        return h(
            'div',
            {
                class: 'lock-div',
                style: {
                    backgroundImage: bgUrl.value ? `url(${bgUrl.value})` : 'none'
                }
            },
            h(Content)
        );
    };

    // 对外暴露渲染方法
    defineExpose({ renderLockDiv });
</script>
<style lang="scss" scoped>
    .lock-div {
        position: fixed;
        z-index: 9000;
        top: 0;
        right: 0;
        bottom: 0;
        left: 0;
        display: flex;
        justify-content: center;
        align-items: center;
        background-position: center;
        background-repeat: no-repeat;
        background-size: cover;
        backdrop-filter: blur(8px);
    }
</style>
