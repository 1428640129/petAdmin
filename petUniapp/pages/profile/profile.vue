<template>
	<view class="container">
		<view class="form-card">
			<view class="form-item">
				<text class="label">头像</text>
				<view class="avatar-wrap" @click="chooseAvatar">
					<image v-if="form.avatar" :src="form.avatar" class="avatar-img" mode="aspectFill"></image>
					<view v-else class="avatar-placeholder">
						<uni-icons type="person" size="60" color="#ccc"></uni-icons>
					</view>
				</view>
			</view>
			<view class="form-item">
				<text class="label">昵称</text>
				<input class="input" v-model="form.nickName" placeholder="请输入昵称" maxlength="20" />
			</view>
			<view class="form-item">
				<text class="label">账号</text>
				<text class="readonly">{{ form.userName || '-' }}</text>
			</view>
			<view class="form-item">
				<text class="label">手机号</text>
				<input class="input" type="number" v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
			</view>
						<button class="save-btn" :disabled="saving" @click="saveProfile">保存</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				form: {
					userId: null,
					userName: '',
					nickName: '',
					phone: '',
					avatar: '',
					userType: '0'
				},
				saving: false
			};
		},
		onLoad() {
			this.loadProfile();
		},
		methods: {
			getBaseUrl() {
				const app = getApp();
				return (app && app.globalData && app.globalData.baseUrl) || 'http://localhost:8080';
			},
			async loadProfile() {
				const userId = uni.getStorageSync('userId');
				if (!userId) {
					uni.showToast({ title: '请先登录', icon: 'none' });
					setTimeout(() => uni.navigateBack(), 1500);
					return;
				}
				try {
					const res = await uni.request({
						url: `${this.getBaseUrl()}/bath/user/profile`,
						method: 'GET',
						data: { userId }
					});
					if (res.statusCode === 200 && res.data && res.data.code === 200 && res.data.data) {
						const d = res.data.data;
						let avatar = d.avatar || '';
						if (avatar && avatar.startsWith('/') && !avatar.startsWith('http')) {
							avatar = this.getBaseUrl() + avatar;
						}
						this.form = {
							userId: d.userId,
							userName: d.userName || '',
							nickName: d.nickName || '',
							phone: d.phone || '',
							avatar: avatar,
							userType: d.userType || '0'
						};
					}
				} catch (e) {
					uni.showToast({ title: '加载失败', icon: 'none' });
				}
			},
			chooseAvatar() {
				uni.chooseImage({
					count: 1,
					sizeType: ['compressed'],
					sourceType: ['album', 'camera'],
					success: (chooseRes) => {
						const tempPath = chooseRes.tempFilePaths[0];
						// 先预览
						this.form.avatar = tempPath;
						// 上传到服务器
						uni.showLoading({ title: '上传中...' });
						uni.uploadFile({
							url: `${this.getBaseUrl()}/bath/user/uploadAvatar`,
							filePath: tempPath,
							name: 'file',
							success: (uploadRes) => {
								uni.hideLoading();
								try {
									const data = JSON.parse(uploadRes.data);
									if (data.code === 200 && data.url) {
										this.form.avatar = data.url;
										uni.showToast({ title: '头像上传成功', icon: 'success' });
									} else {
										throw new Error(data.msg || '上传失败');
									}
								} catch (e) {
									uni.showToast({ title: e.message || '上传失败', icon: 'none' });
								}
							},
							fail: () => {
								uni.hideLoading();
								uni.showToast({ title: '上传失败', icon: 'none' });
							}
						});
					}
				});
			},
			async saveProfile() {
				if (!this.form.userId) {
					uni.showToast({ title: '请先登录', icon: 'none' });
					return;
				}
				if (this.form.phone && !/^1[3-9]\d{9}$/.test(this.form.phone)) {
					uni.showToast({ title: '手机号格式不正确', icon: 'none' });
					return;
				}
				this.saving = true;
				try {
					const res = await uni.request({
						url: `${this.getBaseUrl()}/bath/user/profile`,
						method: 'PUT',
						header: { 'Content-Type': 'application/json' },
						data: {
							userId: this.form.userId,
							nickName: this.form.nickName || '',
							phone: this.form.phone || '',
							avatar: this.form.avatar || ''
						}
					});
					if (res.statusCode === 200 && res.data && res.data.code === 200) {
						const userInfo = uni.getStorageSync('userInfo') || {};
						userInfo.nickName = this.form.nickName;
						userInfo.phone = this.form.phone;
						userInfo.avatar = this.form.avatar;
						uni.setStorageSync('userInfo', userInfo);
						uni.showToast({ title: '保存成功', icon: 'success' });
						setTimeout(() => uni.navigateBack(), 1500);
					} else {
						throw new Error(res.data && res.data.msg ? res.data.msg : '保存失败');
					}
				} catch (e) {
					uni.showToast({ title: e.message || '保存失败', icon: 'none' });
				} finally {
					this.saving = false;
				}
			}
		}
	};
</script>

<style lang="scss" scoped>
.container {
	min-height: 100vh;
	background-color: #fffdf8;
	padding: 30rpx;
}
.form-card {
	background: #fffaf0;
	border-radius: 20rpx;
	padding: 40rpx;
	border: 2rpx solid #d2b48c;
}
.form-item {
	margin-bottom: 40rpx;
}
.label {
	display: block;
	font-size: 28rpx;
	color: #663300;
	margin-bottom: 16rpx;
}
.input {
	width: 100%;
	height: 80rpx;
	background: #fff;
	border-radius: 12rpx;
	padding: 0 24rpx;
	font-size: 28rpx;
	color: #333;
	box-sizing: border-box;
}
.readonly {
	font-size: 28rpx;
	color: #666;
	line-height: 80rpx;
}
.avatar-wrap {
	width: 160rpx;
	height: 160rpx;
	border-radius: 50%;
	overflow: hidden;
	background: #f5f5f5;
}
.avatar-img {
	width: 100%;
	height: 100%;
}
.avatar-placeholder {
	width: 100%;
	height: 100%;
	display: flex;
	align-items: center;
	justify-content: center;
}
.save-btn {
	width: 100%;
	height: 88rpx;
	background: linear-gradient(to right, #ff8c42, #ff6b35);
	color: #fff;
	border: none;
	border-radius: 44rpx;
	font-size: 32rpx;
	font-weight: bold;
	margin-top: 40rpx;
}
.save-btn[disabled] {
	opacity: 0.6;
}
</style>

