<template>
	<view class="container">
		<view class="login-card">
			<text class="title">登录/注册</text>
			
			<!-- 登录方式切换Tab -->
			<view class="login-tabs">
				<view 
					class="tab-item" 
					:class="{ active: loginType === 'phone' }"
					@click="switchLoginType('phone')"
				>
					<text>手机号登录</text>
				</view>
				<view 
					class="tab-item" 
					:class="{ active: loginType === 'account' }"
					@click="switchLoginType('account')"
				>
					<text>账号密码登录</text>
				</view>
			</view>

			<!-- 手机号登录表单 -->
			<view v-if="loginType === 'phone'" class="login-form">
				<!-- 手机号输入 -->
				<view class="input-group">
					<view class="phone-prefix">+86</view>
					<input 
						class="phone-input" 
						type="number" 
						v-model="phone" 
						placeholder="请输入您的手机号"
						maxlength="11"
					/>
				</view>

				<!-- 验证码输入 -->
				<view class="input-group">
					<input 
						class="code-input" 
						type="number" 
						v-model="code" 
						placeholder="请输入验证码"
						maxlength="6"
					/>
					<button 
						class="code-btn" 
						:disabled="isCounting || !isPhoneValid"
						@click="getCode"
					>
						{{ codeBtnText }}
					</button>
				</view>

				<!-- 短信验证码登录按钮 -->
				<button class="login-btn" :disabled="!canLogin" @click="handleLogin">
					验证码登录
				</button>
			</view>

			<!-- 账号密码登录表单 -->
			<view v-if="loginType === 'account'" class="login-form">
				<input
					class="account-input"
					v-model="username"
					placeholder="请输入账号"
					maxlength="30"
				/>
				<input
					class="account-input"
					v-model="password"
					placeholder="请输入密码"
					password="true"
					maxlength="32"
				/>
				<button class="login-btn" :disabled="!canAccountLogin" @click="handleAccountLogin">
					账号密码登录
				</button>
			</view>

			<!-- 其他登录方式 -->
			<!-- <view class="other-login">
				<text class="other-title">其他登录方式</text>
				<view class="other-icons">
					<view class="other-icon wechat" @click="wechatLogin">
						<text class="icon-text">💬</text>
					</view>
					<view class="other-icon qq" @click="qqLogin">
						<text class="icon-text">🐧</text>
					</view>
				</view>
			</view> -->

			<!-- 宠物图片 -->
			<image class="pet-image" src="/static/pet-login.png" mode="aspectFit"></image>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				loginType: 'phone', // 'phone' 或 'account'
				phone: '',
				code: '',
				countdown: 0,
				isCounting: false,
				username: '',
				password: ''
			}
		},
		computed: {
			isPhoneValid() {
				return /^1[3-9]\d{9}$/.test(this.phone);
			},
			canLogin() {
				return this.isPhoneValid && this.code.length === 6;
			},
			canAccountLogin() {
				return this.username.trim().length > 0 && this.password.length >= 6;
			},
			codeBtnText() {
				if (this.isCounting) {
					return `${this.countdown}秒后重新获取`;
				}
				return '获取验证码';
			}
		},
		methods: {
			switchLoginType(type) {
				this.loginType = type;
				// 切换时清空表单
				if (type === 'phone') {
					this.username = '';
					this.password = '';
				} else {
					this.phone = '';
					this.code = '';
					this.isCounting = false;
					this.countdown = 0;
				}
			},
			async getCode() {
				if (!this.isPhoneValid) {
					uni.showToast({
						title: '请输入正确的手机号',
						icon: 'none'
					});
					return;
				}

				// 调用发送验证码接口
				try {
					// 这里调用后端接口
					// const res = await uni.request({
					//   url: 'http://localhost:8080/auth/sendSmsCode',
					//   method: 'POST',
					//   data: { phone: this.phone }
					// });
					
					uni.showToast({
						title: '验证码已发送',
						icon: 'success'
					});

					// 开始倒计时
					this.isCounting = true;
					this.countdown = 60;
					const timer = setInterval(() => {
						this.countdown--;
						if (this.countdown <= 0) {
							clearInterval(timer);
							this.isCounting = false;
						}
					}, 1000);
				} catch (error) {
					uni.showToast({
						title: '发送失败，请重试',
						icon: 'none'
					});
				}
			},
			async handleLogin() {
				if (!this.canLogin) {
					return;
				}
				try {
					uni.showToast({
						title: '登录成功',
						icon: 'success'
					});

					// 跳转到首页
					setTimeout(() => {
						uni.switchTab({
							url: '/pages/index/index'
						});
					}, 1500);
				} catch (error) {
					uni.showToast({
						title: '登录失败，请重试',
						icon: 'none'
					});
				}
			},
			// 账号密码登录（使用新的前台用户表）
			async handleAccountLogin() {
				if (!this.canAccountLogin) {
					return;
				}

				try {
					const app = getApp();
					const baseUrl = (app && app.globalData && app.globalData.baseUrl) || 'http://localhost:8080';

					const res = await uni.request({
						url: `${baseUrl}/bath/user/login`,
						method: 'POST',
						header: {
							'Content-Type': 'application/json'
						},
						data: {
							userName: this.username.trim(),
							password: this.password
						}
					});

					if (res.statusCode === 200 && res.data && res.data.code === 200) {
						// 保存用户信息
						const userInfo = res.data.data;
						if (userInfo) {
							uni.setStorageSync('userInfo', userInfo);
							uni.setStorageSync('userId', userInfo.userId);
							uni.setStorageSync('userType', userInfo.userType); // 0=顾客,1=商家
						}

						uni.showToast({
							title: '登录成功',
							icon: 'success'
						});

						setTimeout(() => {
							uni.switchTab({
								url: '/pages/index/index'
							});
						}, 1500);
					} else {
						throw new Error(res.data?.msg || '登录失败');
					}
				} catch (error) {
					uni.showToast({
						title: error.message || '账号或密码错误',
						icon: 'none'
					});
				}
			},
			wechatLogin() {
				uni.showToast({
					title: '微信登录功能开发中',
					icon: 'none'
				});
			},
			qqLogin() {
				uni.showToast({
					title: 'QQ登录功能开发中',
					icon: 'none'
				});
			}
		}
	}
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

	.login-card {
		width: 100%;
		background-color: #fff;
		border-radius: 32rpx;
		padding: 60rpx 40rpx;
		box-shadow: 0 16rpx 48rpx rgba(0, 0, 0, 0.1);
	}

	.title {
		font-size: 48rpx;
		font-weight: bold;
		color: #333;
		text-align: center;
		display: block;
		margin-bottom: 60rpx;
	}

	.input-group {
		display: flex;
		align-items: center;
		background-color: #f5f5f5;
		border-radius: 16rpx;
		padding: 0 20rpx;
		margin-bottom: 30rpx;
		height: 88rpx;
	}

	.phone-prefix {
		font-size: 28rpx;
		color: #666;
		margin-right: 20rpx;
	}

	.phone-input {
		flex: 1;
		font-size: 28rpx;
		color: #333;
	}

	.code-input {
		flex: 1;
		font-size: 28rpx;
		color: #333;
	}

	.code-btn {
		background-color: #F4A460;
		color: #fff;
		border: none;
		border-radius: 12rpx;
		padding: 12rpx 24rpx;
		font-size: 24rpx;
		margin-left: 20rpx;
	}

	.code-btn[disabled] {
		background-color: #ccc;
	}

	.login-btn {
		width: 100%;
		height: 88rpx;
		background: linear-gradient(135deg, #F4A460, #FFA500);
		color: #fff;
		border: none;
		border-radius: 16rpx;
		font-size: 32rpx;
		font-weight: bold;
		margin-top: 40rpx;
		margin-bottom: 40rpx;
	}

	.login-btn[disabled] {
		background-color: #ccc;
	}

	.other-login {
		text-align: center;
		margin-top: 60rpx;
	}

	.other-title {
		font-size: 24rpx;
		color: #999;
		display: block;
		margin-bottom: 30rpx;
	}

	.other-icons {
		display: flex;
		justify-content: center;
		gap: 40rpx;
	}

	.other-icon {
		width: 80rpx;
		height: 80rpx;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.other-icon.wechat {
		background-color: #07c160;
	}

	.other-icon.qq {
		background-color: #12b7f5;
	}

	.icon-text {
		font-size: 40rpx;
	}

	.pet-image {
		width: 200rpx;
		height: 200rpx;
		margin: 40rpx auto 0;
		display: block;
	}

	.login-tabs {
		display: flex;
		background-color: #f5f5f5;
		border-radius: 16rpx;
		padding: 8rpx;
		margin-bottom: 40rpx;
	}

	.tab-item {
		flex: 1;
		text-align: center;
		padding: 20rpx 0;
		border-radius: 12rpx;
		font-size: 28rpx;
		color: #666;
		transition: all 0.3s;
	}

	.tab-item.active {
		background-color: #fff;
		color: #F4A460;
		font-weight: bold;
	}

	.login-form {
		margin-top: 20rpx;
	}

	.account-input {
		width: 100%;
		height: 88rpx;
		background-color: #f5f5f5;
		border-radius: 16rpx;
		padding: 0 24rpx;
		font-size: 28rpx;
		color: #333;
		margin-bottom: 30rpx;
	}
</style>



