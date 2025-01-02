/**
 * File对象到Base64编码
 * 使用示例
try {
    const base64String = await fileToBase64(file);
    console.log('Base64 String:', base64String);
} catch (error) {
    console.error('Error reading file:', error);
}
*/
export async function fileToBase64(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = (event) => {
            resolve(event.target.result);
        };
        reader.onerror = reject;
        reader.readAsDataURL(file); // 读取文件为DataURL
    });
}

/**
 * 从Base64编码到File对象
 * 使用示例
const base64String = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=';
const file = base64ToFile(base64String, 'example.png');
console.log('File:', file);
*/
export function base64ToFile(base64String, fileName) {
    const arr = base64String.split(',');
    const mime = arr[0].match(/:(.*?);/)[1];
    const bstr = atob(arr[1]);
    let n = bstr.length;
    const u8arr = new Uint8Array(n);

    while (n--) {
        u8arr[n] = bstr.charCodeAt(n);
    }

    return new File([u8arr], fileName, { type: mime });
}

/**
 * 批量转换File对象为Base64编码
 * 使用示例
try {
    const base64Strings = await filesToBase64(files);
    console.log('Base64 Strings:', base64Strings);
  } catch (error) {
    console.error('Error reading files:', error);
  }
*/
export async function filesToBase64(files) {
    const promises = Array.from(files).map((file) => fileToBase64(file));
    const base64Strings = await Promise.all(promises);
    return base64Strings;
}

/**
 * 批量转换Base64编码为File对象
 * 使用示例
const base64Strings = [
  'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=',
  'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBxMTEhUTExMWFhUXGBgXGBgaGhsfHh0eHyggGBolHRUtHyUvLzYtLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLf/AABEIALwBQAMAwEAAhEDEQA/ALy8A...'
];
const fileNames = ['example1.png', 'example2.jpg'];
const files = base64sToFiles(base64Strings, fileNames);
console.log('Files:', files);
*/
export function base64sToFiles(base64Strings, fileNames) {
    const files = base64Strings.map((base64String, index) => {
        return base64ToFile(base64String, fileNames[index]);
    });
    return files;
}
