<script lang="ts">
    import { computed, defineComponent, h, watch } from 'vue-demi';
    import Content from './pass.vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { $y9_SSO } from '@/main';
    // const bgUrl = 'https://v3.cn.vuejs.org/logo.png'
    const settingStore = useSettingStore();
    const bgUrl = computed(() => settingStore.getLockScreenImage);
    // console.log(bgUrl.value);

    // 监听F12手动移除锁屏元素
    const lockStatus = computed(() => settingStore.getLockScreen);
    let timerList = [];

    // onUnmounted(() => {
    //     console.log("onUnmounted");

    // })
    // onBeforeMount(() => {
    //     console.log('onBeforeMount');

    // })

    // 当锁屏为true的时候 保证骨架视觉上不可见
    if (lockStatus.value) {
        getLockScreenState();
    }

    // 监听
    watch(lockStatus, () => {
        getLockScreenState();
    });

    function getLockScreenState() {
        timerList.push(
            setInterval(() => {
                // 保证骨架视觉上不可见 防止修改锁屏的css
                document.getElementById('indexlayout').style.display = 'none';
                // 删除标签 & 隐藏标签
                const lockDivClassName = document.getElementsByClassName('lock-div')[0]?.className;
                if (lockStatus.value && lockDivClassName !== 'lock-div') {
                    const params = {
                        redirect_uri: window.location.origin + import.meta.env.VUE_APP_PUBLIC_PATH
                    };
                    $y9_SSO.ssoLogout(params);
                }
            }, 100)
        );
        if (!lockStatus.value) {
            console.log(1);
            document.getElementById('indexlayout').style.display = '';
            timerList.forEach((item) => {
                clearInterval(item);
            });
        }
    }

    export default defineComponent({
        name: 'lock-comp',
        render() {
            return h(
                'div',
                {
                    class: 'lock-div'
                    // style: {
                    // backgroundImage: 'url(' + bgUrl.value + ')'
                    // }
                },
                h(Content)
            );
        }
    });
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
        background-repeat: repeat;
        background-size: 100% 100%;
        // background-color: rgba($color: #000000, $alpha: 0.3);
        // filter: blur(10px);
    }
</style>
