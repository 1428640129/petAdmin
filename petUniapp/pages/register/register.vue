<template>
	<view class="container">
		<view class="register-card">
			<text class="title">账号注册</text>

			<input
				class="input"
				v-model="form.userName"
				placeholder="请输入账号"
				maxlength="30"
			/>
			<input
				class="input"
				type="number"
				v-model="form.phone"
				placeholder="请输入手机号（可选）"
				maxlength="11"
			/>
			<input
				class="input"
				v-model="form.password"
				placeholder="请输入密码（至少6位）"
				password="true"
				maxlength="32"
			/>
			<input
				class="input"
				v-model="form.confirmPassword"
				placeholder="请再次输入密码"
				password="true"
				maxlength="32"
			/>

			<button class="submit-btn" :disabled="submitting || !canSubmit" @click="handleRegister">
				{{ submitting ? '注册中...' : '注册' }}
			</button>

			<view class="back-login" @click="goBackLogin">已有账号，返回登录</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				submitting: false,
				form: {
					userName: '',
					phone: '',
					password: '',
					confirmPassword: ''
				}
			};
		},
		computed: {
			canSubmit() {
				return (
					this.form.userName.trim().length > 0 &&
					this.form.password.length >= 6 &&
					this.form.confirmPassword.length >= 6
				);
			}
		},
		methods: {
			getBaseUrl() {
				const app = getApp();
				return (app && app.globalData && app.globalData.baseUrl) || 'http://localhost:8080';
			},
			validateForm() {
				const userName = this.form.userName.trim();
				const phone = this.form.phone.trim();
				const password = this.form.password;
				const confirmPassword = this.form.confirmPassword;

				if (!userName) return '账号不能为空';
				if (phone && !/^1[3-9]\d{9}$/.test(phone)) return '手机号格式不正确';
				if (password.length < 6) return '密码至少6位';
				if (password !== confirmPassword) return '两次输入的密码不一致';
				return '';
			},
			async handleRegister() {
				const error = this.validateForm();
				if (error) {
					uni.showToast({ title: error, icon: 'none' });
					return;
				}

				this.submitting = true;
				try {
					const baseUrl = this.getBaseUrl();
					const res = await uni.request({
						url: `${baseUrl}/bath/user/register`,
						method: 'POST',
						header: { 'Content-Type': 'application/json' },
						data: {
							userName: this.form.userName.trim(),
							phone: this.form.phone.trim(),
							password: this.form.password,
							userType: '0'
						}
					});

					if (res.statusCode === 200 && res.data && res.data.code === 200) {
						uni.showToast({ title: '注册成功，请登录', icon: 'success' });
						setTimeout(() => {
							uni.navigateBack();
						}, 1200);
					} else {
						throw new Error((res.data && res.data.msg) || '注册失败');
					}
				} catch (e) {
					uni.showToast({
						title: e.message || '注册失败，请稍后重试',
						icon: 'none'
					});
				} finally {
					this.submitting = false;
				}
			},
			goBackLogin() {
				uni.navigateBack();
			}
		}
	};
</script>

<style lang="scss" scoped>
	.container {
		min-height: 100vh;
		background: linear-gradient(135deg, #FFD4A3, #FFA500);
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 40rpx;
	}

	.register-card {
		width: 100%;
		background-color: #fff;
		border-radius: 32rpx;
		padding: 60rpx 40rpx;
		box-shadow: 0 16rpx 48rpx rgba(0, 0, 0, 0.1);
	}

	.title {
		font-size: 44rpx;
		font-weight: bold;
		color: #333;
		text-align: center;
		display: block;
		margin-bottom: 50rpx;
	}

	.input {
		width: 100%;
		height: 88rpx;
		background-color: #f5f5f5;
		border-radius: 16rpx;
		padding: 0 24rpx;
		font-size: 28rpx;
		color: #333;
		margin-bottom: 24rpx;
	}

	.submit-btn {
		width: 100%;
		height: 88rpx;
		background: linear-gradient(135deg, #F4A460, #FFA500);
		color: #fff;
		border: none;
		border-radius: 16rpx;
		font-size: 32rpx;
		font-weight: bold;
		margin-top: 20rpx;
	}

	.submit-btn[disabled] {
		background-color: #ccc;
	}

	.back-login {
		text-align: center;
		color: #F4A460;
		font-size: 24rpx;
		margin-top: 28rpx;
	}
</style>
