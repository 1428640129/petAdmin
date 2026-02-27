import { request } from '../request';

/**
 * 上传单个文件
 */
export function fetchUploadFile(file: File) {
    const formData = new FormData();
    formData.append('file', file);
    return request<{
        url: string;
        fileName: string;
        newFileName: string;
        originalFilename: string;
    }>({
        url: '/common/upload',
        method: 'post',
        data: formData
        // 注意：不要手动设置 Content-Type，让浏览器自动设置（包含 boundary）
    });
}

/**
 * 上传多个文件
 */
export function fetchUploadFiles(files: File[]) {
    const formData = new FormData();
    files.forEach(file => {
        formData.append('files', file);
    });
    return request<{
        urls: string;
        fileNames: string;
        newFileNames: string;
        originalFilenames: string;
    }>({
        url: '/common/uploads',
        method: 'post',
        data: formData
        // 注意：不要手动设置 Content-Type，让浏览器自动设置（包含 boundary）
    });
}

