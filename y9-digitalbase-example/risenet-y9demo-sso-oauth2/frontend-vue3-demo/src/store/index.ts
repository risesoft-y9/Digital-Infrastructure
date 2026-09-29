import {createPinia} from 'pinia';
// init pinia
const pinia = createPinia();

export function setupStore(app) {
    app.use(pinia);

}

export default pinia;
