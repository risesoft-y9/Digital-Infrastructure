<template>
    <div :class="{ 'row-warning': isTrue }">{{ isTrue ? trueText : falseText }}</div>
</template>

<script setup lang="ts">
    const props = defineProps({
        isTrue: {
            type: Boolean,
            default: false
        },
        trueText: {
            type: String,
            default: '是'
        },
        falseText: {
            type: String,
            default: '否'
        }
    });
</script>

<style scoped lang="scss">
    .row-warning {
        color: rgb(230, 162, 60);
    }
</style>
