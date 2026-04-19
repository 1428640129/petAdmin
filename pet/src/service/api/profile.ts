import { request } from '../request';

/**
 * 获取个人信息
 */
export function fetchGetProfile() {
    return request<Api.Profile.ProfileInfo>({
        url: '/system/user/profile',
        method: 'get'
    });
}

/**
 * 更新个人信息
 */
export function fetchUpdateProfile(data: Api.Profile.UpdateProfileParams) {
    return request<null>({
        url: '/system/user/profile',
        method: 'put',
        data
    });
}

/**
 * 修改密码
 */
export function fetchUpdatePassword(data: Api.Profile.UpdatePasswordParams) {
    return request<null>({
        url: '/system/user/profile/updatePwd',
        method: 'put',
        data
    });
}

/**
 * 上传头像
 */
export function fetchUploadAvatar(file: File) {
    const formData = new FormData();
    formData.append('avatarfile', file);
    return request<{ imgUrl: string }>({
        url: '/system/user/profile/avatar',
        method: 'post',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}





