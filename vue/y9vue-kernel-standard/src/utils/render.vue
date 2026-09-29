<script lang="ts">
    import { defineComponent, h } from 'vue';

    export default defineComponent({
        name: 'renderComponent',
        props: {
            renderFn: Function,
            data: Object
        },
        setup(props) {
            return () => {
                const fn = props.renderFn as Function | undefined;
                if (typeof fn !== 'function') return null;
                const data = props.data;
                if (data !== undefined && data !== null && typeof data === 'object' && 'row' in data) {
                    return fn(data.row, data);
                }
                if (data !== undefined) return fn(data);
                return fn();
            };
        }
    });
</script>

<template>
    <!-- 留空，用 setup 返回的 render 函数 -->
</template>
