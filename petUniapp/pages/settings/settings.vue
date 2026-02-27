<template>
	<view class="container">
		<view class="menu-card">
			<view class="menu-item" @click="goChangePassword">
				<uni-icons type="locked" size="20" color="#ff6b35"></uni-icons>
				<text class="menu-text">修改密码</text>
				<uni-icons type="arrowright" size="16" color="#ccc"></uni-icons>
			</view>
			<view class="menu-item" @click="logout">
				<uni-icons type="redo" size="20" color="#ff6b35"></uni-icons>
				<text class="menu-text logout-text">退出登录</text>
				<uni-icons type="arrowright" size="16" color="#ccc"></uni-icons>
			</view>
		</view>
		<view v-if="showPasswordModal" class="modal-mask" @click="showPasswordModal = false">
			<view class="modal-content" @click.stop>
				<text class="modal-title">修改密码</text>
				<input class="modal-input" type="password" v-model="passwordForm.oldPassword" placeholder="请输入原密码" />
				<input class="modal-input" type="password" v-model="passwordForm.newPassword" placeholder="请输入新密码（至少6位）" />
				<input class="modal-input" type="password" v-model="passwordForm.confirmPassword" placeholder="请再次输入新密码" />
				<view class="modal-btns">
					<button class="modal-btn cancel" @click="showPasswordModal = false">取消</button>
					<button class="modal-btn confirm" :disabled="changing" @click="submitPassword">确定</button>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				showPasswordModal: false,
				changing: false,
				passwordForm: {
					oldPassword: '',
					newPassword: '',
					confirmPassword: ''
				}
			};
		},
		methods: {
			getBaseUrl() {
				const app = getApp();
				return (app && app.globalData && app.globalData.baseUrl) || 'http://localhost:8080';
			},
			goChangePassword() {
				this.passwordForm = { oldPassword: '', newPassword: '', confirmPassword: '' };
				this.showPasswordModal = true;
			},
			async submitPassword() {
				const { oldPassword, newPassword, confirmPassword } = this.passwordForm;
				if (!oldPassword) {
					uni.showToast({ title: '请输入原密码', icon: 'none' });
					return;
				}
				if (!newPassword || newPassword.length < 6) {
					uni.showToast({ title: '新密码至少6位', icon: 'none' });
					return;
				}
				if (newPassword !== confirmPassword) {
					uni.showToast({ title: '两次输入密码不一致', icon: 'none' });
					return;
				}
				const userId = uni.getStorageSync('userId');
				if (!userId) {
					uni.showToast({ title: '请先登录', icon: 'none' });
					return;
				}
				this.changing = true;
				try {
					const res = await uni.request({
						url: `${this.getBaseUrl()}/bath/user/password`,
						method: 'PUT',
						header: { 'Content-Type': 'application/json' },
						data: {
							userId,
							oldPassword,
							newPassword
						}
					});
					if (res.statusCode === 200 && res.data && res.data.code === 200) {
						uni.showToast({ title: '修改成功', icon: 'success' });
						this.showPasswordModal = false;
					} else {
						throw new Error(res.data && res.data.msg ? res.data.msg : '修改失败');
					}
				} catch (e) {
					uni.showToast({ title: e.message || '修改失败', icon: 'none' });
				} finally {
					this.changing = false;
				}
			},
			logout() {
				uni.showModal({
					title: '提示',
					content: '确定要退出登录吗？',
					success: (res) => {
						if (res.confirm) {
							uni.removeStorageSync('userInfo');
							uni.removeStorageSync('userId');
							uni.removeStorageSync('userType');
							uni.removeStorageSync('token');
							uni.showToast({ title: '已退出', icon: 'success' });
							setTimeout(() => {
								uni.switchTab({ url: '/pages/index/index' });
							}, 1000);
						}
					}
				});
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
.menu-card {
	background: #fffaf0;
	border-radius: 20rpx;
	overflow: hidden;
	border: 2rpx solid #d2b48c;
}
.menu-item {
	display: flex;
	align-items: center;
	padding: 30rpx;
	background: #fff;
	border-bottom: 2rpx solid #f5f5f5;
}
.menu-item:last-child {
	border-bottom: none;
}
.menu-text {
	flex: 1;
	font-size: 28rpx;
	color: #663300;
	margin-left: 20rpx;
}
.logout-text {
	color: #e74c3c;
}
.modal-mask {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.5);
	display: flex;
	align-items: center;
	justify-content: center;
	z-index: 9999;
}
.modal-content {
	width: 600rpx;
	background: #fff;
	border-radius: 20rpx;
	padding: 40rpx;
}
.modal-title {
	font-size: 36rpx;
	font-weight: bold;
	color: #333;
	display: block;
	margin-bottom: 30rpx;
}
.modal-input {
	width: 100%;
	height: 80rpx;
	background: #f5f5f5;
	border-radius: 12rpx;
	padding: 0 24rpx;
	font-size: 28rpx;
	margin-bottom: 24rpx;
	box-sizing: border-box;
}
.modal-btns {
	display: flex;
	margin-top: 40rpx;
}
.modal-btn:first-child {
	margin-right: 20rpx;
}
.modal-btn {
	flex: 1;
	height: 80rpx;
	border-radius: 40rpx;
	font-size: 28rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}
.modal-btn.cancel {
	background: #f5f5f5;
	color: #666;
}
.modal-btn.confirm {
	background: linear-gradient(to right, #ff8c42, #ff6b35);
	color: #fff;
}
.modal-btn[disabled] {
	opacity: 0.6;
}
</style>

