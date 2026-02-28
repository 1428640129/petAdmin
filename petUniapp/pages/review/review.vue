<template>
	<view class="container">
		<!-- 顶部导航栏 -->
		<view class="header">
			<text class="header-title">服务评价</text>
		</view>
		
		<!-- 表单内容 -->
		<scroll-view class="form-content" scroll-y="true">
			<!-- 服务信息 -->
			<view class="form-section">
				<view class="section-title">
					<text>服务信息</text>
				</view>
				<view class="service-info">
					<text class="service-name">{{ serviceName || '宠物服务' }}</text>
				</view>
			</view>
			
			<!-- 评分 -->
			<view class="form-section">
				<view class="section-title">
					<text>服务评分</text>
					<text class="required">*</text>
				</view>
				<view class="rating-container">
					<view 
						v-for="(star, index) in 5" 
						:key="index"
						class="star-item"
						@click="setRating(index + 1)"
					>
						<text class="star" :class="{ active: index < formData.rating }">
							{{ index < formData.rating ? '★' : '☆' }}
						</text>
					</view>
					<text class="rating-text">{{ formData.rating > 0 ? formData.rating + '分' : '请选择评分' }}</text>
				</view>
			</view>
			
			<!-- 评价内容 -->
			<view class="form-section">
				<view class="section-title">
					<text>评价内容</text>
					<text class="required">*</text>
				</view>
				<view class="form-item">
					<textarea 
						class="textarea" 
						v-model="formData.content" 
						placeholder="请输入您的评价内容..."
						maxlength="500"
						:show-confirm-bar="false"
					></textarea>
					<view class="char-count">{{ formData.content.length }}/500</view>
				</view>
			</view>
			
			<!-- 上传图片 -->
			<view class="form-section">
				<view class="section-title">
					<text>上传图片</text>
					<text class="optional">（选填，最多3张）</text>
				</view>
				<view class="image-upload-container">
					<view 
						v-for="(image, index) in imageList" 
						:key="index"
						class="image-item"
					>
						<image :src="image" mode="aspectFill" class="uploaded-image"></image>
						<view class="delete-btn" @click="deleteImage(index)">
							<text>×</text>
						</view>
					</view>
					<view 
						v-if="imageList.length < 3" 
						class="upload-btn"
						@click="chooseImage"
					>
						<text class="upload-icon">+</text>
						<text class="upload-text">上传图片</text>
					</view>
				</view>
			</view>
		</scroll-view>
		
		<!-- 底部提交按钮 -->
		<view class="bottom-action">
			<button class="submit-btn" :disabled="!canSubmit" @click="submitReview">
				{{ canSubmit ? '提交评价' : '请完善必填信息' }}
			</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				appointmentId: null,
				serviceId: null,
				serviceName: '',
				formData: {
					rating: 0,
					content: '',
					images: ''
				},
				imageList: []
			}
		},
		computed: {
			canSubmit() {
				return this.formData.rating > 0 && this.formData.content.trim().length > 0;
			}
		},
		onLoad(options) {
			if (options.appointmentId) {
				this.appointmentId = parseInt(options.appointmentId);
			}
			if (options.serviceId) {
				this.serviceId = parseInt(options.serviceId);
			}
			if (options.serviceName) {
				this.serviceName = decodeURIComponent(options.serviceName);
			}
		},
		methods: {
			setRating(rating) {
				this.formData.rating = rating;
			},
			chooseImage() {
				const maxCount = 3 - this.imageList.length;
				uni.chooseImage({
					count: maxCount,
					sizeType: ['compressed'],
					sourceType: ['album', 'camera'],
					success: (res) => {
						const tempFilePaths = res.tempFilePaths;
						// 上传图片到服务器
						this.uploadImages(tempFilePaths);
					},
					fail: (err) => {
						console.error('选择图片失败:', err);
						uni.showToast({
							title: '选择图片失败',
							icon: 'none'
						});
					}
				});
			},
			async uploadImages(filePaths) {
				uni.showLoading({
					title: '上传中...'
				});
				
				try {
					const app = getApp();
					const baseUrl = (app && app.globalData && app.globalData.baseUrl) || 'http://localhost:8080';
					const token = uni.getStorageSync('token');
					
					const uploadPromises = filePaths.map(filePath => {
						return new Promise((resolve, reject) => {
							uni.uploadFile({
								url: `${baseUrl}/common/upload`,
								filePath: filePath,
								name: 'file',
								header: {
									'Authorization': token ? `Bearer ${token}` : ''
								},
								success: (uploadRes) => {
									try {
										const data = JSON.parse(uploadRes.data);
										if (data.code === 200) {
											resolve(data.url || data.data);
										} else {
											reject(new Error(data.msg || '上传失败'));
										}
									} catch (e) {
										reject(new Error('解析上传结果失败'));
									}
								},
								fail: (err) => {
									reject(err);
								}
							});
						});
					});
					
					const uploadedUrls = await Promise.all(uploadPromises);
					this.imageList = this.imageList.concat(uploadedUrls);
					
					uni.hideLoading();
					uni.showToast({
						title: '上传成功',
						icon: 'success'
					});
				} catch (error) {
					uni.hideLoading();
					console.error('上传图片失败:', error);
					uni.showToast({
						title: error.message || '上传图片失败',
						icon: 'none'
					});
				}
			},
			deleteImage(index) {
				this.imageList.splice(index, 1);
			},
			async submitReview() {
				if (!this.canSubmit) {
					uni.showToast({
						title: '请完善必填信息',
						icon: 'none'
					});
					return;
				}
				
				if (!this.appointmentId) {
					uni.showToast({
						title: '预约信息错误',
						icon: 'none'
					});
					return;
				}
				
				// 构建提交数据
				const submitData = {
					appointmentId: this.appointmentId,
					serviceId: this.serviceId,
					rating: this.formData.rating,
					content: this.formData.content.trim(),
					images: this.imageList.length > 0 ? this.imageList.join(',') : ''
				};
				
				uni.showLoading({
					title: '提交中...'
				});
				
				try {
					const app = getApp();
					const baseUrl = (app && app.globalData && app.globalData.baseUrl) || 'http://localhost:8080';
					const token = uni.getStorageSync('token');
					
					const res = await uni.request({
						url: `${baseUrl}/bath/review/miniprogram`,
						method: 'POST',
						data: submitData,
						header: {
							'Content-Type': 'application/json',
							'Authorization': token ? `Bearer ${token}` : ''
						}
					});
					
					uni.hideLoading();
					
					if (res.statusCode === 200 && res.data && res.data.code === 200) {
						uni.showToast({
							title: '评价成功',
							icon: 'success'
						});
						
						// 延迟返回，让用户看到成功提示
						setTimeout(() => {
							uni.navigateBack();
						}, 1500);
					} else {
						throw new Error(res.data?.msg || '评价失败');
					}
				} catch (error) {
					uni.hideLoading();
					console.error('提交评价失败:', error);
					uni.showToast({
						title: error.message || '评价失败',
						icon: 'none'
					});
				}
			}
		}
	}
</script>

<style lang="scss" scoped>
	page {
		height: 100%;
		background-color: #fffdf8;
	}
	
	.container {
		height: 100%;
		display: flex;
		flex-direction: column;
		background-color: #fffdf8;
	}
	
	.header {
		width: 100%;
		min-height: 88rpx;
		padding-top: constant(safe-area-inset-top, 0px);
		padding-top: env(safe-area-inset-top, 0px);
		background: linear-gradient(to right, #ff8c42, #ff6b35);
		display: flex;
		align-items: center;
		justify-content: center;
		position: fixed;
		top: 0;
		left: 0;
		z-index: 1000;
		box-sizing: border-box;
	}
	
	.header-title {
		font-size: 36rpx;
		color: #ffffff;
		font-weight: bold;
	}
	
	.form-content {
		flex: 1;
		padding: 20rpx;
		padding-top: calc(88rpx + env(safe-area-inset-top, 0px) + 20rpx);
		padding-bottom: 120rpx;
		box-sizing: border-box;
	}
	
	.form-section {
		background-color: #ffffff;
		border-radius: 20rpx;
		padding: 30rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}
	
	.section-title {
		font-size: 32rpx;
		color: #333;
		font-weight: bold;
		margin-bottom: 30rpx;
		display: flex;
		align-items: center;
	}
	
	.required {
		color: #ff6b35;
		margin-left: 8rpx;
	}
	
	.optional {
		color: #999;
		font-size: 24rpx;
		font-weight: normal;
		margin-left: 8rpx;
	}
	
	.service-info {
		padding: 20rpx;
		background-color: #fafafa;
		border-radius: 12rpx;
	}
	
	.service-name {
		font-size: 30rpx;
		color: #333;
		font-weight: bold;
	}
	
	.rating-container {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}
	
	.star-item {
		cursor: pointer;
	}
	
	.star {
		font-size: 60rpx;
		color: #ddd;
		transition: all 0.3s;
		
		&.active {
			color: #ffd700;
		}
	}
	
	.rating-text {
		font-size: 28rpx;
		color: #666;
		margin-left: 20rpx;
	}
	
	.form-item {
		margin-bottom: 30rpx;
		
		&:last-child {
			margin-bottom: 0;
		}
	}
	
	.textarea {
		width: 100%;
		min-height: 200rpx;
		padding: 20rpx;
		background-color: #fafafa;
		border: 2rpx solid #f5f5f5;
		border-radius: 12rpx;
		font-size: 28rpx;
		color: #333;
		box-sizing: border-box;
		
		&:focus {
			border-color: #ff6b35;
			background-color: #ffffff;
		}
	}
	
	.char-count {
		text-align: right;
		font-size: 24rpx;
		color: #999;
		margin-top: 10rpx;
	}
	
	.image-upload-container {
		display: flex;
		flex-wrap: wrap;
		gap: 20rpx;
	}
	
	.image-item {
		position: relative;
		width: 200rpx;
		height: 200rpx;
		border-radius: 12rpx;
		overflow: hidden;
	}
	
	.uploaded-image {
		width: 100%;
		height: 100%;
	}
	
	.delete-btn {
		position: absolute;
		top: -10rpx;
		right: -10rpx;
		width: 40rpx;
		height: 40rpx;
		background-color: #ff6b35;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		color: #ffffff;
		font-size: 32rpx;
		font-weight: bold;
	}
	
	.upload-btn {
		width: 200rpx;
		height: 200rpx;
		border: 2rpx dashed #ddd;
		border-radius: 12rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		background-color: #fafafa;
	}
	
	.upload-icon {
		font-size: 60rpx;
		color: #999;
		margin-bottom: 10rpx;
	}
	
	.upload-text {
		font-size: 24rpx;
		color: #999;
	}
	
	.bottom-action {
		width: 100%;
		padding: 20rpx;
		box-sizing: border-box;
		background-color: #fffdf8;
		border-top: 2rpx solid #f5f5f5;
		position: fixed;
		bottom: 0;
		left: 0;
		z-index: 999;
		box-shadow: 0 -4rpx 12rpx rgba(0, 0, 0, 0.1);
	}
	
	.submit-btn {
		width: 100%;
		height: 88rpx;
		background: linear-gradient(to right, #ff8c42, #ff6b35);
		border: none;
		border-radius: 44rpx;
		color: #ffffff;
		font-size: 32rpx;
		font-weight: bold;
		display: flex;
		align-items: center;
		justify-content: center;
		box-shadow: 0 4rpx 12rpx rgba(255, 107, 53, 0.3);
		
		&:disabled {
			background: #cccccc;
			box-shadow: none;
		}
	}
</style>

