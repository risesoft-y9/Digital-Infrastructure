/*
 * @Author: qinman
 * @Date: 2022-09-27 14:28:34
 * @LastEditors: qinman
 * @LastEditTime: 2022-09-27 17:45:22
 * @Description: 
 * @FilePath: \frontend-vue3-demo\vite.config.js
 */
import {defineConfig, loadEnv} from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite';
import {resolve} from 'path';
// https://vitejs.dev/config/
const prefix = 'VUE_APP';
export default (serve) => {
	const ENV = loadEnv(serve.mode, process.cwd(), prefix);
	return defineConfig({
			base: ENV.VUE_APP_PUBLIC_PATH,
			resolve: {
				alias: {
					'@': resolve(__dirname, 'src')
				},
			},
			envPrefix: prefix,
			
			server: {
				port: 8081,
				// host: '192.168.3.15',   // 局域网内测试【填写你的当前IP】
				proxy: {
					'/.*/sso': {
						target: process.env.VUE_APP_SSO,
						changeOrigin: true,
						secure: true,
						pathRewrite: { '^/.*/sso': '/sso' },
					},
				},
				cors: true,
			},
		  plugins: [
			  vue(),
			  AutoImport({
				  imports: ['vue', 'pinia', 'vue-router'],
			  }),
			]
		})
} 
