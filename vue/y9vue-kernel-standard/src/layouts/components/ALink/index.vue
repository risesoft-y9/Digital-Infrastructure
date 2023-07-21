<template>
    <a v-if="isExternal(to)" :href="to" target="_blank" rel="noreferrer">
        <slot></slot>
    </a>
    <router-link v-else :to="to" :replace="replace">
        <slot></slot>
    </router-link>
</template>
<script lang="ts">
import { defineComponent } from "vue";
import { isExternal } from '@/utils/validate';

export default defineComponent({
    name: 'ALink',
    props: {
        to: {
            type: String,
            required: true
        },
        replace: {
            type: Boolean,
            default: false
        }
    },
    setup() {
        return {
            isExternal
        }
    }
})
</script>