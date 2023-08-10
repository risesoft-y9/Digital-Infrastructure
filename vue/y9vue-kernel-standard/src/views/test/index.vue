<!--
 * @Author: hongzhew
 * @Date: 2022-03-28 09:49:09
 * @LastEditors: hongzhew
 * @LastEditTime: 2022-04-01 20:12:47
 * @Description: 
-->
<script lang="ts" setup>
    import { defineComponent, onMounted, h } from 'vue-demi';
    import { inject, watch, ref } from 'vue';
    // 注入 字体变量
    const fontSizeObj: any = inject('sizeObjInfo');

    const testComp = defineComponent({
        name: 'test-comp',
        props: {
            color: String,
        },
        render() {
            return h(
                'div',
                {
                    class: 'ttt',
                    style: {
                        backgroundColor: this.color,
                    },
                },
                this.$slots
            );
        },
    });

    const buildColor = () => {
        return (
            '#' +
            Math.floor(Math.random() * 0xffffff)
                .toString(16)
                .padStart(6, '0')
        );
    };
</script>
<template>
    <div>测试页面1 —— 首页</div>
    <div id="test">
        <testComp v-for="n in 10" :key="n" :color="buildColor()">{{ n }}</testComp>
    </div>
</template>
<style lang="scss" scoped>
    div:nth-child(1) {
        width: 100%;
        height: 100vh;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        background-color: var(--el-bg-color);
    }

    #test {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        .ttt {
            display: flex;
            justify-content: center;
            align-items: center;
            width: 100%;
            height: 50vh;
            font-size: v-bind('fontSizeObj.baseFontSize');
            font-weight: bold;
        }
    }
</style>
