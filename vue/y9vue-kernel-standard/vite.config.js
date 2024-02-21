import vue from '@vitejs/plugin-vue';
// 利用插件动态主题模式和预设主题模式
import { themePreprocessorHmrPlugin, themePreprocessorPlugin } from '@zougt/vite-plugin-theme-preprocessor';
import path, { resolve } from 'path';
import AutoImport from 'unplugin-auto-import/vite';
import Icons from 'unplugin-icons/vite';
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers';
import Components from 'unplugin-vue-components/vite';
import { defineConfig, loadEnv } from 'vite';
import { vitePluginIconSvg } from './src/layouts/components/IconSvg/plugin';

const prefix = 'VUE_APP';

export default (serve) => {
    const ENV = loadEnv(serve.mode, process.cwd(), prefix);
    return defineConfig({
        base: ENV.VUE_APP_PUBLIC_PATH,
        build: {
            outDir: ENV.VUE_APP_NAME,
            chunkSizeWarningLimit: 660,
            rollupOptions: {
                output: {
                    manualChunks(id) {
                        if (id.includes('node_modules')) {
                            return id.toString().split('node_modules/')[1].split('/')[0].toString();
                        }
                    }
                }
            }
        },
        envPrefix: prefix,
        resolve: {
            alias: {
                '@': resolve(__dirname, 'src')
            }
        },
        server: {
            port: 7070,
            // host: '192.168.3.222',   // 局域网内测试【填写你的当前IP】
            proxy: {
                '/.*/sso': {
                    target: process.env.VUE_APP_SSO,
                    changeOrigin: true,
                    secure: true,
                    pathRewrite: { '^/.*/sso': '/sso' }
                }
            },
            cors: true
        },
        plugins: [
            vue(),
            AutoImport({
                imports: ['vue', 'pinia', 'vue-i18n', 'vue-router', '@vueuse/core'],
                resolvers: [ElementPlusResolver()]
            }),
            Components({
                resolvers: [
                    ElementPlusResolver({
                        importStyle: 'sass' // 实现element自定义主题 & 切换，这里选择 sass
                    })
                ]
            }),
            Icons({
                compiler: 'vue3',
                autoInstall: true
            }),
            vitePluginIconSvg(),
            // 创建预设主题切换
            themePreprocessorPlugin({
                scss: {
                    // 是否启用任意主题色模式，这里不启用
                    arbitraryMode: false,
                    // 提供多组变量文件
                    multipleScopeVars: [
                        {
                            scopeName: 'theme-default',
                            // 变量文件内容不应该夹带样式代码，设定上只需存在变量
                            path: path.resolve('src/theme/default/default.scss')
                        },
                        {
                            scopeName: 'theme-green',
                            path: path.resolve('src/theme/green/green.scss')
                        },
                        {
                            scopeName: 'theme-blue',
                            path: path.resolve('src/theme/blue/blue.scss')
                        },
                        {
                            scopeName: 'theme-dark',
                            path: path.resolve('src/theme/dark/dark.scss')
                        }
                    ],
                    // css中不是由主题色变量生成的颜色，也让它抽取到主题css内，可以提高权重
                    includeStyleWithColors: [
                        {
                            color: '#ffffff',
                            // 此类颜色的是否跟随主题色梯度变化，默认false
                            inGradient: true
                        }
                    ],
                    // 默认取 multipleScopeVars[0].scopeName
                    defaultScopeName: '',
                    // 在生产模式是否抽取独立的主题css文件，extract为true以下属性有效
                    extract: true,
                    // 独立主题css文件的输出路径，默认取 viteConfig.build.assetsDir 相对于 (viteConfig.build.outDir)
                    outputDir: '',
                    // 会选取defaultScopeName对应的主题css文件在html添加link
                    themeLinkTagId: 'head',
                    // "head"||"head-prepend" || "body" ||"body-prepend"
                    themeLinkTagInjectTo: 'head',
                    // 是否对抽取的css文件内对应scopeName的权重类名移除
                    removeCssScopeName: false,
                    // 可以自定义css文件名称的函数
                    customThemeCssFileName: (scopeName) => scopeName
                }
            }),
            // 主题热更新，不得已分开插件，因为需要vite插件顺序enforce
            themePreprocessorHmrPlugin()
        ]
    });
};
