declare namespace Api {
    /**
     * namespace Profile
     *
     * backend api module: "profile"
     */
    namespace Profile {
        /** 个人信息 */
        interface ProfileInfo {
            userId: number;
            userName: string;
            nickName: string;
            email: string;
            phonenumber: string;
            sex: string;
            avatar: string;
            deptId: number;
            dept: {
                deptName: string;
            };
            roleGroup: string;
            postGroup: string;
            loginDate: string;
            loginIp: string;
        }

        /** 更新个人信息参数 */
        interface UpdateProfileParams {
            nickName?: string;
            email?: string;
            phonenumber?: string;
            sex?: string;
        }

        /** 修改密码参数 */
        interface UpdatePasswordParams {
            oldPassword: string;
            newPassword: string;
        }
    }
}





