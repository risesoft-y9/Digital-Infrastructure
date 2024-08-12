import { createPinia } from 'pinia';
import { useSettingStore } from './modules/settingStore';
// init pinia
const pinia = createPinia();

export function setupStore(app) {
    app.use(pinia);

    //  示例代码（持久化pinia的settingStore模块）
    const settingStore = useSettingStore();
    // settingStore-persist-key
    const persistKey = 'userSettingData';
    const object = localStorage.getItem(persistKey) && JSON.parse(localStorage.getItem(persistKey));
    if (object) {
        for (const key in object) {
            settingStore.$patch({
                [key]: object[key]
            });
        }
    }
    settingStore.$subscribe((mutation, state) => {
        localStorage.setItem(persistKey, JSON.stringify(state));
    });
}

export default pinia;
