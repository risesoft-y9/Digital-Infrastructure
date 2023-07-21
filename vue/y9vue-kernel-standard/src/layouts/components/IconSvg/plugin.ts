/*
 * @Author: your name
 * @Date: 2022-01-12 17:06:57
 * @LastEditTime: 2022-01-20 15:52:31
 * @LastEditors: your name
 * @Description: 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 * @FilePath: /sz-team-frontend-9.6.x/y9vue-home/src/layouts/components/IconSvg/plugin.ts
 */
import { Plugin } from 'vite';
import * as path from "path";
import { readFileSync, readdirSync } from 'fs';

const svgTitle = /<svg([^>+].*?)>/;
const clearHeightWidth = /(width|height)="([^>+].*?)"/g;
const hasViewBox = /(viewBox="[^>+].*?")/g;
const clearReturn = /(\r)|(\n)/g;
// const svgDir = path.resolve(__dirname, './src/assets/iconsvg');
const svgDir = path.resolve(__dirname, '../../../assets/iconsvg');

export function findSvgFile(dir: string, idPerfix: string = 'icon-'): string[] {
    const svgRes = []
    const dirents = readdirSync(dir, {
      withFileTypes: true
    })
    for (const dirent of dirents) {
      if (dirent.isDirectory()) {
        
        svgRes.push(...findSvgFile(path.join(dir,dirent.name, '/'), idPerfix))
      } else {
        const svg = readFileSync(path.join(dir,dirent.name))
          .toString()
          .replace(clearReturn, '')
          .replace(svgTitle, ($1, $2) => {
            let width = 0
            let height = 0
            let content = $2.replace(
              clearHeightWidth,
              (s1: any, s2: string, s3: number) => {
                if (s2 === 'width') {
                  width = s3
                } else if (s2 === 'height') {
                  height = s3
                }
                return ''
              }
            )
            if (!hasViewBox.test($2)) {
              content += `viewBox="0 0 ${width} ${height}"`
            }
            return `<symbol id="${idPerfix}${dirent.name.replace(
              '.svg',
              ''
            )}" ${content}>`
          })
          .replace('</svg>', '</symbol>')
        svgRes.push(svg)
      }
    }
    return svgRes
}


/**
 * 自动导入 @/assets/iconsvg 下svg文件 vite Plugin
 * @author LiQingSong
 */
export function vitePluginIconSvg(): Plugin {

    const res: string[] = findSvgFile(svgDir, '');

    return {
        name: 'vite-plugin-icon-svg',
        transformIndexHtml(html): string {
          return html.replace(
            '<body>',
            `
              <body>
                <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" style="position: absolute; width: 0; height: 0">
                  ${res.join('')}
                </svg>
            `
          )
        }
      }
    
}