<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { fetchGetProfile, fetchUpdatePassword, fetchUpdateProfile, fetchUploadAvatar } from '@/service/api/profile';
import { useAuthStore } from '@/store/modules/auth';

defineOptions({ name: 'UserCenter' });

const authStore = useAuthStore();

// 个人信息
const profileInfo = ref<Api.Profile.ProfileInfo>({
  userId: 0,
  userName: '',
  nickName: '',
  email: '',
  phonenumber: '',
  sex: '',
  avatar: '',
  deptId: 0,
  dept: { deptName: '' },
  roleGroup: '',
  postGroup: '',
  loginDate: '',
  loginIp: ''
});

// 编辑表单
const editForm = ref({
  nickName: '',
  email: '',
  phonenumber: '',
  sex: ''
});

// 密码表单
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 表单验证规则
const editFormRules = {
  nickName: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  email: [{ type: 'email' as const, message: '请输入正确的邮箱地址', trigger: 'blur' }],
  phonenumber: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }]
};

const passwordFormRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (_rule: any, value: string, callback: any) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
};

const editFormRef = ref();
const passwordFormRef = ref();

// 加载状态
const loading = ref(false);
const passwordDialogVisible = ref(false);
const avatarUploading = ref(false);

// 加载个人信息
async function loadProfile() {
  loading.value = true;
  try {
    const { data, error } = await fetchGetProfile();
    if (!error && data) {
      profileInfo.value = data;
      editForm.value = {
        nickName: data.nickName || '',
        email: data.email || '',
        phonenumber: data.phonenumber || '',
        sex: data.sex || ''
      };
    }
  } catch {
    // 加载失败，静默处理
  } finally {
    loading.value = false;
  }
}

// 保存个人信息
async function handleSaveProfile() {
  if (!editFormRef.value) return;

  await editFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true;
      try {
        const { error } = await fetchUpdateProfile(editForm.value);
        if (!error) {
          ElMessage.success('保存成功');
          await loadProfile();
          // 更新store中的用户信息
          await authStore.initUserInfo();
        }
      } catch {
        // 保存失败，静默处理
      } finally {
        loading.value = false;
      }
    }
  });
}

// 打开修改密码对话框
function openPasswordDialog() {
  passwordDialogVisible.value = true;
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  };
  if (passwordFormRef.value) {
    passwordFormRef.value.clearValidate();
  }
}

// 修改密码
async function handleUpdatePassword() {
  if (!passwordFormRef.value) return;

  await passwordFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true;
      try {
        const { error } = await fetchUpdatePassword({
          oldPassword: passwordForm.value.oldPassword,
          newPassword: passwordForm.value.newPassword
        });
        if (!error) {
          ElMessage.success('密码修改成功，请重新登录');
          passwordDialogVisible.value = false;
          // 延迟退出登录，让用户看到成功提示
          setTimeout(() => {
            authStore.resetStore();
          }, 1500);
        }
      } catch {
        // 修改密码失败，静默处理
      } finally {
        loading.value = false;
      }
    }
  });
}

// 上传头像
async function handleAvatarUpload(event: Event) {
  const target = event.target as HTMLInputElement;
  const file = target.files?.[0];
  if (!file) return;

  // 验证文件类型
  const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
  if (!validTypes.includes(file.type)) {
    ElMessage.error('请上传图片文件（JPG、PNG、GIF、WEBP）');
    return;
  }

  // 验证文件大小（5MB）
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 5MB');
    return;
  }

  avatarUploading.value = true;
  try {
    const { data, error } = await fetchUploadAvatar(file);
    if (!error && data) {
      ElMessage.success('头像上传成功');
      profileInfo.value.avatar = data.imgUrl;
      // 更新store中的用户信息
      await authStore.initUserInfo();
    }
  } catch {
    // 上传头像失败，静默处理
  } finally {
    avatarUploading.value = false;
    // 清空input，以便可以重复选择同一文件
    target.value = '';
  }
}

// 获取头像URL
function getAvatarUrl() {
  if (!profileInfo.value.avatar) {
    return '';
  }
  // 如果是相对路径，需要拼接基础URL
  if (profileInfo.value.avatar.startsWith('/')) {
    const baseURL = import.meta.env.VITE_SERVICE_BASE_URL || '';
    return baseURL + profileInfo.value.avatar;
  }
  return profileInfo.value.avatar;
}

// 格式化日期（账户信息隐藏后暂时未使用）
// function formatDate(dateStr: string) {
//   if (!dateStr) return '-';
//   return new Date(dateStr).toLocaleString('zh-CN');
// }

onMounted(() => {
  loadProfile();
});
</script>

<template>
  <div class="user-center-container">
    <ElCard class="profile-card">
      <template #header>
        <div class="card-header">
          <h3 class="card-title">个人信息</h3>
        </div>
      </template>

      <div class="profile-content">
        <!-- 头像区域 -->
        <div class="avatar-section">
          <div class="avatar-wrapper">
            <img
              v-if="getAvatarUrl()"
              :src="getAvatarUrl()"
              alt="头像"
              class="avatar-img"
            />
            <div v-else class="avatar-placeholder">
              <SvgIcon icon="ph:user-circle" class="text-80px" />
            </div>
            <div class="avatar-overlay">
              <label class="avatar-upload-btn">
                <SvgIcon icon="ph:camera" class="text-20px" />
                <input
                  type="file"
                  accept="image/*"
                  style="display: none"
                  @change="handleAvatarUpload"
                />
              </label>
            </div>
            <div v-if="avatarUploading" class="avatar-loading">
              <SvgIcon icon="svg-spinners:ring-resize" class="text-24px" />
            </div>
          </div>
          <p class="avatar-tip">点击头像上传新头像</p>
        </div>

        <!-- 基本信息 -->
        <div class="info-section">
          <h4 class="section-title">基本信息</h4>
          <ElForm
            ref="editFormRef"
            :model="editForm"
            :rules="editFormRules"
            label-width="100px"
            class="profile-form"
          >
            <ElFormItem label="用户名">
              <span class="readonly-text">{{ profileInfo.userName }}</span>
            </ElFormItem>

            <ElFormItem label="昵称" prop="nickName">
              <ElInput v-model="editForm.nickName" placeholder="请输入昵称" />
            </ElFormItem>

            <ElFormItem label="邮箱" prop="email">
              <ElInput v-model="editForm.email" placeholder="请输入邮箱" />
            </ElFormItem>

            <ElFormItem label="手机号" prop="phonenumber">
              <ElInput v-model="editForm.phonenumber" placeholder="请输入手机号" />
            </ElFormItem>

            <ElFormItem label="性别" prop="sex">
              <ElRadioGroup v-model="editForm.sex">
                <ElRadio label="0">男</ElRadio>
                <ElRadio label="1">女</ElRadio>
                <ElRadio label="2">未知</ElRadio>
              </ElRadioGroup>
            </ElFormItem>

            <ElFormItem>
              <ElButton type="primary" :loading="loading" @click="handleSaveProfile">
                保存修改
              </ElButton>
            </ElFormItem>
          </ElForm>
        </div>

        <!-- 账户信息 - 已隐藏 -->
        <!-- <div class="info-section">
          <h4 class="section-title">账户信息</h4>
          <div class="info-list">
            <div class="info-item">
              <span class="info-label">角色：</span>
              <span class="info-value">{{ profileInfo.roleGroup || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">岗位：</span>
              <span class="info-value">{{ profileInfo.postGroup || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">部门：</span>
              <span class="info-value">{{ profileInfo.dept?.deptName || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">最后登录：</span>
              <span class="info-value">{{ formatDate(profileInfo.loginDate) }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">登录IP：</span>
              <span class="info-value">{{ profileInfo.loginIp || '-' }}</span>
            </div>
          </div>
        </div> -->

        <!-- 安全设置 -->
        <div class="info-section">
          <h4 class="section-title">安全设置</h4>
          <div class="security-actions">
            <ElButton type="warning" @click="openPasswordDialog">
              <SvgIcon icon="ph:lock" class="mr-8px" />
              修改密码
            </ElButton>
          </div>
        </div>
      </div>
    </ElCard>

    <!-- 修改密码对话框 -->
    <ElDialog
      v-model="passwordDialogVisible"
      title="修改密码"
      width="500px"
      :close-on-click-modal="false"
    >
      <ElForm
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordFormRules"
        label-width="100px"
      >
        <ElFormItem label="旧密码" prop="oldPassword">
          <ElInput
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入旧密码"
            show-password
          />
        </ElFormItem>

        <ElFormItem label="新密码" prop="newPassword">
          <ElInput
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码（6-20个字符）"
            show-password
          />
        </ElFormItem>

        <ElFormItem label="确认密码" prop="confirmPassword">
          <ElInput
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </ElFormItem>
      </ElForm>

      <template #footer>
        <ElButton @click="passwordDialogVisible = false">取消</ElButton>
        <ElButton type="primary" :loading="loading" @click="handleUpdatePassword">
          确定
        </ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<style scoped lang="scss">
.user-center-container {
  padding: 20px;
  min-height: 100%;
  background-color: var(--el-bg-color-page);
}

.profile-card {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.profile-content {
  padding: 20px 0;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 40px;
  padding-bottom: 40px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.avatar-wrapper {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    transform: scale(1.05);
  }

  &:hover .avatar-overlay {
    opacity: 1;
  }
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--el-fill-color-light);
  color: var(--el-text-color-placeholder);
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-upload-btn {
  cursor: pointer;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.avatar-loading {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: var(--el-color-primary);
}

.avatar-tip {
  margin-top: 12px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.info-section {
  margin-bottom: 40px;

  &:last-child {
    margin-bottom: 0;
  }
}

.section-title {
  margin: 0 0 20px 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  padding-bottom: 12px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.profile-form {
  max-width: 600px;
}

.readonly-text {
  color: var(--el-text-color-regular);
  font-size: 14px;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  font-size: 14px;
}

.info-label {
  min-width: 100px;
  color: var(--el-text-color-secondary);
}

.info-value {
  color: var(--el-text-color-primary);
  flex: 1;
}

.security-actions {
  display: flex;
  gap: 12px;
}

.mr-8px {
  margin-right: 8px;
}
</style>
