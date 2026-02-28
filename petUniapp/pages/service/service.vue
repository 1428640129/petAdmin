<template>
	<view class="container">
		<!-- 表单内容 -->
		<scroll-view class="form-content" scroll-y="true">
			<!-- 选择服务 -->
			<view class="form-section">
				<view class="section-title">
					<text>选择服务</text>
					<text class="required">*</text>
				</view>
				<view class="service-list">
					<view 
						v-for="service in services" 
						:key="service.id"
						class="service-item"
						:class="{ active: formData.serviceId === service.id }"
						@click="selectService(service)"
					>
						<image v-if="service.image" :src="service.image" mode="aspectFill" class="service-image"></image>
						<view class="service-info">
							<text class="service-name">{{ service.name }}</text>
							<text class="service-price">¥{{ formatPrice(service.price) }}</text>
						</view>
						<view v-if="formData.serviceId === service.id" class="check-icon">✓</view>
					</view>
				</view>
			</view>
			
			<!-- 服务介绍（选中服务时显示） -->
			<view v-if="selectedService" class="form-section service-intro-section">
				<view class="section-title">
					<text>服务介绍</text>
				</view>
				<view class="service-intro-content">
					<text class="service-intro-text">{{ selectedService.desc || '暂无服务介绍' }}</text>
				</view>
			</view>
			
			<!-- 用户评价（选中服务时显示） -->
			<view v-if="selectedService" class="form-section review-section">
				<view class="section-title">
					<text>用户评价</text>
					<text class="review-count" v-if="reviews.length > 0">({{ reviews.length }})</text>
				</view>
				<view v-if="loadingReviews" class="review-loading">
					<text>加载评价中...</text>
				</view>
				<view v-else-if="reviews.length === 0" class="review-empty">
					<text>暂无评价</text>
				</view>
				<view v-else class="review-list">
					<view v-for="(review, index) in reviews" :key="index" class="review-item">
						<view class="review-header">
							<view class="review-rating">
								<text class="rating-stars">{{ getStars(review.rating) }}</text>
								<text class="rating-value">{{ review.rating }}分</text>
							</view>
							<text class="review-time">{{ formatTime(review.createTime) }}</text>
						</view>
						<text class="review-content">{{ review.content }}</text>
						<view v-if="review.reply" class="review-reply">
							<text class="reply-label">商家回复：</text>
							<text class="reply-content">{{ review.reply }}</text>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 宠物信息 -->
			<view class="form-section">
				<view class="section-title">
					<text>宠物信息</text>
				</view>
				
				<!-- 宠物名称 -->
				<view class="form-item">
					<text class="label">宠物名称</text>
					<input 
						class="input" 
						v-model="formData.petName" 
						placeholder="请输入宠物名称"
						maxlength="20"
					/>
				</view>
				
				<!-- 长毛/短毛 -->
				<view class="form-item">
					<text class="label">毛发类型</text>
					<picker 
						mode="selector" 
						:range="hairTypes" 
						range-key="label"
						:value="hairTypeIndex"
						@change="onHairTypeChange"
					>
						<view class="picker-view">
							<text :class="{ placeholder: !formData.hairType }">
								{{ formData.hairType ? getHairTypeLabel(formData.hairType) : '请选择毛发类型' }}
							</text>
							<text class="picker-arrow">›</text>
						</view>
					</picker>
				</view>
				
				<!-- 宠物品种 -->
				<view class="form-item">
					<text class="label">宠物品种</text>
					<input 
						class="input" 
						v-model="formData.petBreed" 
						placeholder="请输入宠物品种（选填）"
						maxlength="30"
					/>
				</view>
				
				<!-- 宠物体重 -->
				<view class="form-item">
					<text class="label">宠物体重（kg）</text>
					<text class="required">*</text>
					<input 
						class="input" 
						type="digit"
						v-model="formData.petWeight" 
						placeholder="请输入宠物体重"
						@blur="calculatePrice"
					/>
				</view>
			</view>
			
			<!-- 预约时间 -->
			<view class="form-section">
				<view class="section-title">
					<text>预约时间</text>
					<text class="required">*</text>
				</view>
				<view class="form-item">
					<picker 
						mode="date" 
						:value="formData.appointmentDate"
						:start="minDate"
						@change="onDateChange"
					>
						<view class="picker-view">
							<text :class="{ placeholder: !formData.appointmentDate }">
								{{ formData.appointmentDate || '请选择日期' }}
							</text>
							<text class="picker-arrow">›</text>
						</view>
					</picker>
				</view>
				<view class="form-item">
					<picker 
						mode="time" 
						:value="formData.appointmentTime"
						@change="onTimeChange"
					>
						<view class="picker-view">
							<text :class="{ placeholder: !formData.appointmentTime }">
								{{ formData.appointmentTime || '请选择时间' }}
							</text>
							<text class="picker-arrow">›</text>
						</view>
					</picker>
				</view>
			</view>
			
			<!-- 联系方式 -->
			<view class="form-section">
				<view class="section-title">
					<text>联系方式</text>
				</view>
				<view class="form-item">
					<text class="label">联系电话</text>
					<text class="required">*</text>
					<input 
						class="input" 
						type="number"
						v-model="formData.contactPhone" 
						placeholder="请输入联系电话"
						maxlength="11"
					/>
				</view>
			</view>
			
			<!-- 备注信息 -->
			<view class="form-section">
				<view class="section-title">
					<text>备注信息</text>
				</view>
				<view class="form-item">
					<textarea 
						class="textarea" 
						v-model="formData.remark" 
						placeholder="请输入备注信息（选填）"
						maxlength="200"
					></textarea>
				</view>
			</view>
			
			<!-- 价格预览 -->
			<view v-if="formData.expectedPrice" class="price-preview">
				<text class="price-label">预计价格：</text>
				<text class="price-value">¥{{ formatPrice(formData.expectedPrice) }}</text>
			</view>
		</scroll-view>
		
		<!-- 底部提交按钮 -->
		<view class="bottom-action">
			<button class="submit-btn" :disabled="!canSubmit" @click="submitAppointment">
				{{ canSubmit ? '提交预约' : '请完善必填信息' }}
			</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				// 表单数据
				formData: {
					serviceId: null,
					petName: '',
					hairType: '0', // 默认短毛
					petBreed: '',
					petWeight: '',
					appointmentDate: '',
					appointmentTime: '',
					contactPhone: '',
					remark: '',
					expectedPrice: null
				},
				// 服务列表
				services: [],
				loading: false,
				// 评价列表
				reviews: [],
				loadingReviews: false,
				// 毛发类型选项（0=短毛,1=长毛）
				hairTypes: [
					{ value: '0', label: '短毛' },
					{ value: '1', label: '长毛' }
				],
				hairTypeIndex: 0,
				// 预选的服务ID（从首页传递）
				preSelectedServiceId: null
			}
		},
		computed: {
			// 当前选中的服务
			selectedService() {
				if (!this.formData.serviceId) return null;
				return this.services.find(s => s.id === this.formData.serviceId) || null;
			},
			// 最小日期（今天）
			minDate() {
				const date = new Date();
				const year = date.getFullYear();
				const month = String(date.getMonth() + 1).padStart(2, '0');
				const day = String(date.getDate()).padStart(2, '0');
				return `${year}-${month}-${day}`;
			},
			// 是否可以提交
			canSubmit() {
				return this.formData.serviceId && 
				       this.formData.appointmentDate && 
				       this.formData.appointmentTime &&
				       this.formData.petWeight &&
				       this.formData.contactPhone;
			}
		},
		onLoad(options) {
			// 如果从首页传递了服务ID，保存起来
			if (options.serviceId) {
				this.preSelectedServiceId = parseInt(options.serviceId);
			}
			this.loadServices();
		},
		methods: {
			// 加载服务列表
			async loadServices() {
				this.loading = true;
				try {
					const app = getApp();
					const baseUrl = (app && app.globalData && app.globalData.baseUrl) || 'http://localhost:8080';
					
					const res = await uni.request({
						url: `${baseUrl}/bath/service/enabled`,
						method: 'GET',
						header: {
							'Content-Type': 'application/json'
						}
					});
					
					if (res.statusCode === 200 && res.data && res.data.code === 200) {
						const serviceList = res.data.data || [];
						this.services = serviceList.map((item) => {
							// 解析服务图片
							let imageUrl = '';
							if (item.serviceImages) {
								try {
									const images = JSON.parse(item.serviceImages);
									imageUrl = Array.isArray(images) && images.length > 0 ? images[0] : '';
								} catch (e) {
									imageUrl = item.serviceImages;
								}
							}
							
							// 获取最低价格
							let price = '0';
							if (item.prices && item.prices.length > 0) {
								const prices = item.prices.map(p => parseFloat(p.price || 0));
								price = Math.min(...prices).toString();
							}
							
							return {
								id: item.serviceId,
								name: item.serviceName || '',
								price: price,
								image: imageUrl || 'https://ai-public.mastergo.com/ai/img_res/3a21b5f82e4f86baf34988ced88a8d66.jpg',
								desc: item.serviceDesc || ''
							};
						});
						
						// 如果从首页传递了服务ID，自动选中该服务
						if (this.preSelectedServiceId) {
							const selectedService = this.services.find(s => s.id === this.preSelectedServiceId);
							if (selectedService) {
								this.selectService(selectedService);
							}
						}
					} else {
						throw new Error(res.data?.msg || '获取服务列表失败');
					}
				} catch (error) {
					console.error('获取服务列表失败:', error);
					uni.showToast({
						title: error.message || '获取服务列表失败',
						icon: 'none'
					});
				} finally {
					this.loading = false;
				}
			},
			// 选择服务
			selectService(service) {
				this.formData.serviceId = service.id;
				// 重新计算价格
				this.calculatePrice();
				// 加载该服务的评价
				this.loadReviews(service.id);
			},
			// 加载评价列表
			async loadReviews(serviceId) {
				if (!serviceId) {
					this.reviews = [];
					return;
				}
				
				this.loadingReviews = true;
				try {
					const app = getApp();
					const baseUrl = (app && app.globalData && app.globalData.baseUrl) || 'http://localhost:8080';
					
					const res = await uni.request({
						url: `${baseUrl}/bath/review/miniprogram/list`,
						method: 'GET',
						data: {
							serviceId: serviceId,
							pageNum: 1,
							pageSize: 10
						},
						header: {
							'Content-Type': 'application/json'
						}
					});
					
					if (res.statusCode === 200 && res.data && res.data.code === 200) {
						this.reviews = res.data.rows || [];
					} else {
						this.reviews = [];
					}
				} catch (error) {
					console.error('加载评价失败:', error);
					this.reviews = [];
				} finally {
					this.loadingReviews = false;
				}
			},
			// 获取星级显示
			getStars(rating) {
				if (!rating) return '☆☆☆☆☆';
				const fullStars = '★'.repeat(rating);
				const emptyStars = '☆'.repeat(5 - rating);
				return fullStars + emptyStars;
			},
			// 格式化时间
			formatTime(timeStr) {
				if (!timeStr) return '';
				try {
					const date = new Date(timeStr);
					const year = date.getFullYear();
					const month = String(date.getMonth() + 1).padStart(2, '0');
					const day = String(date.getDate()).padStart(2, '0');
					return `${year}-${month}-${day}`;
				} catch (e) {
					return timeStr;
				}
			},
			// 毛发类型改变
			onHairTypeChange(e) {
				this.hairTypeIndex = e.detail.value;
				this.formData.hairType = this.hairTypes[e.detail.value].value;
				// 重新计算价格
				this.calculatePrice();
			},
			// 获取毛发类型标签
			getHairTypeLabel(value) {
				const type = this.hairTypes.find(t => t.value === value);
				return type ? type.label : '';
			},
			// 日期改变
			onDateChange(e) {
				this.formData.appointmentDate = e.detail.value;
			},
			// 时间改变
			onTimeChange(e) {
				this.formData.appointmentTime = e.detail.value;
			},
			// 计算价格
			async calculatePrice() {
				if (!this.formData.serviceId || !this.formData.petWeight) {
					this.formData.expectedPrice = null;
					return;
				}
				
				const weight = parseFloat(this.formData.petWeight);
				if (isNaN(weight) || weight <= 0) {
					this.formData.expectedPrice = null;
					return;
				}
				
				try {
					const app = getApp();
					const baseUrl = (app && app.globalData && app.globalData.baseUrl) || 'http://localhost:8080';
					
					// 使用带宠物类型的价格计算接口
					const petType = this.formData.hairType || '0'; // 默认短毛
					const res = await uni.request({
						url: `${baseUrl}/bath/service/calculatePriceWithType`,
						method: 'GET',
						data: {
							serviceId: this.formData.serviceId,
							petType: petType,
							weight: weight
						},
						header: {
							'Content-Type': 'application/json'
						}
					});
					
					if (res.statusCode === 200 && res.data && res.data.code === 200) {
						this.formData.expectedPrice = res.data.data || null;
					}
				} catch (error) {
					console.error('计算价格失败:', error);
					// 计算失败不影响提交
				}
			},
			// 格式化价格
			formatPrice(price) {
				if (!price) return '0.00';
				return parseFloat(price).toFixed(2);
			},
			// 提交预约
			async submitAppointment() {
				if (!this.canSubmit) {
					uni.showToast({
						title: '请完善必填信息',
						icon: 'none'
					});
					return;
				}
				
				// 验证宠物体重
				if (!this.formData.petWeight) {
					uni.showToast({
						title: '请输入宠物体重',
						icon: 'none'
					});
					return;
				}
				
				const weight = parseFloat(this.formData.petWeight);
				if (isNaN(weight) || weight <= 0) {
					uni.showToast({
						title: '请输入有效的宠物体重',
						icon: 'none'
					});
					return;
				}
				
				// 验证联系电话
				if (!this.formData.contactPhone) {
					uni.showToast({
						title: '请输入联系电话',
						icon: 'none'
					});
					return;
				}
				
				// 验证联系电话格式（11位手机号）
				const phone = this.formData.contactPhone.trim();
				if (phone.length < 11 || !/^1[3-9]\d{9}$/.test(phone)) {
					uni.showToast({
						title: '请输入正确的手机号码',
						icon: 'none'
					});
					return;
				}
				
				// 组合预约时间
				const appointmentDateTime = `${this.formData.appointmentDate} ${this.formData.appointmentTime}:00`;
				
				// 构建提交数据
				const submitData = {
					serviceId: this.formData.serviceId,
					appointmentTime: appointmentDateTime,
					petName: this.formData.petName || '未命名宠物',
					petWeight: weight,
					petType: this.formData.hairType || '0', // 0=短毛,1=长毛
					petBreed: this.formData.petBreed || '',
					contactPhone: this.formData.contactPhone || '',
					remark: this.formData.remark || ''
				};
				
				uni.showLoading({
					title: '提交中...'
				});
				
				try {
					const app = getApp();
					const baseUrl = (app && app.globalData && app.globalData.baseUrl) || 'http://localhost:8080';
					const token = uni.getStorageSync('token');
					
					const res = await uni.request({
						url: `${baseUrl}/bath/appointment/miniprogram`,
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
							title: '预约成功',
							icon: 'success'
						});
						
						// 延迟跳转到我的预约页面，让用户看到成功提示
						setTimeout(() => {
							uni.switchTab({
								url: '/pages/appointment/appointment'
							});
						}, 1500);
					} else {
						throw new Error(res.data?.msg || '预约失败');
					}
				} catch (error) {
					uni.hideLoading();
					console.error('提交预约失败:', error);
					uni.showToast({
						title: error.message || '预约失败',
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
	
	.form-content {
		flex: 1;
		padding: 20rpx;
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
	
	.service-list {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}
	
	.service-item {
		display: flex;
		align-items: center;
		padding: 20rpx;
		border: 2rpx solid #f5f5f5;
		border-radius: 16rpx;
		background-color: #fafafa;
		position: relative;
		transition: all 0.3s;
		
		&.active {
			border-color: #ff6b35;
			background-color: #fff5f0;
		}
	}
	
	.service-image {
		width: 120rpx;
		height: 120rpx;
		border-radius: 12rpx;
		margin-right: 20rpx;
		flex-shrink: 0;
	}
	
	.service-info {
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: 10rpx;
	}
	
	.service-name {
		font-size: 30rpx;
		color: #333;
		font-weight: bold;
	}
	
	.service-price {
		font-size: 28rpx;
		color: #ff6b35;
		font-weight: bold;
	}
	
	.check-icon {
		width: 48rpx;
		height: 48rpx;
		border-radius: 50%;
		background-color: #ff6b35;
		color: #ffffff;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 32rpx;
		font-weight: bold;
		flex-shrink: 0;
	}
	
	.service-intro-section {
		background: linear-gradient(to bottom, #fff9f6, #ffffff);
	}
	
	.service-intro-content {
		padding: 10rpx 0;
	}
	
	.service-intro-text {
		font-size: 28rpx;
		color: #666;
		line-height: 1.6;
		white-space: pre-wrap;
		word-break: break-all;
	}
	
	.review-section {
		background: linear-gradient(to bottom, #fff9f6, #ffffff);
	}
	
	.review-count {
		font-size: 24rpx;
		color: #999;
		font-weight: normal;
		margin-left: 10rpx;
	}
	
	.review-loading,
	.review-empty {
		padding: 40rpx 0;
		text-align: center;
		font-size: 28rpx;
		color: #999;
	}
	
	.review-list {
		display: flex;
		flex-direction: column;
		gap: 30rpx;
	}
	
	.review-item {
		padding: 20rpx 0;
		border-bottom: 1rpx solid #f5f5f5;
		
		&:last-child {
			border-bottom: none;
		}
	}
	
	.review-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-bottom: 15rpx;
	}
	
	.review-rating {
		display: flex;
		align-items: center;
		gap: 10rpx;
	}
	
	.rating-stars {
		font-size: 28rpx;
		color: #ffa500;
		letter-spacing: 2rpx;
	}
	
	.rating-value {
		font-size: 24rpx;
		color: #666;
	}
	
	.review-time {
		font-size: 24rpx;
		color: #999;
	}
	
	.review-content {
		font-size: 28rpx;
		color: #333;
		line-height: 1.6;
		word-break: break-all;
	}
	
	.review-reply {
		margin-top: 15rpx;
		padding: 15rpx;
		background-color: #f9f9f9;
		border-radius: 8rpx;
		border-left: 4rpx solid #ff6b35;
	}
	
	.reply-label {
		font-size: 24rpx;
		color: #ff6b35;
		font-weight: bold;
		margin-right: 10rpx;
	}
	
	.reply-content {
		font-size: 26rpx;
		color: #666;
		line-height: 1.6;
	}
	
	.form-item {
		margin-bottom: 30rpx;
		
		&:last-child {
			margin-bottom: 0;
		}
	}
	
	.label {
		display: inline-block;
		font-size: 28rpx;
		color: #666;
		margin-bottom: 15rpx;
		margin-right: 8rpx;
	}
	
	.form-item .required {
		color: #ff6b35;
		font-size: 28rpx;
		margin-right: 0;
	}
	
	.input {
		width: 100%;
		height: 80rpx;
		padding: 0 20rpx;
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
	
	.picker-view {
		width: 100%;
		height: 80rpx;
		padding: 0 20rpx;
		background-color: #fafafa;
		border: 2rpx solid #f5f5f5;
		border-radius: 12rpx;
		display: flex;
		align-items: center;
		justify-content: space-between;
		box-sizing: border-box;
		
		text {
			flex: 1;
			font-size: 28rpx;
			color: #333;
			overflow: hidden;
			text-overflow: ellipsis;
			white-space: nowrap;
			margin-right: 20rpx;
			
			&.placeholder {
				color: #999;
			}
		}
	}
	
	.picker-arrow {
		color: #999;
		font-size: 32rpx;
		font-weight: bold;
		flex-shrink: 0;
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
	
	.price-preview {
		background: linear-gradient(to right, #fff5f0, #ffe8d6);
		border-radius: 20rpx;
		padding: 30rpx;
		margin-top: 20rpx;
		display: flex;
		align-items: center;
		justify-content: space-between;
		border: 2rpx solid #ff6b35;
	}
	
	.price-label {
		font-size: 30rpx;
		color: #666;
	}
	
	.price-value {
		font-size: 40rpx;
		color: #ff6b35;
		font-weight: bold;
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
	
	.submit-btn::after {
		border: none;
	}
</style>
