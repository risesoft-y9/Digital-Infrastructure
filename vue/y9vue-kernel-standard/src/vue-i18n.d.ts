// src/shims-vue-i18n.d.ts
import 'vue-i18n'

declare module 'vue' {
    interface ComponentCustomProperties {
        $t: (key: string, ...args: any[]) => string
    }
}