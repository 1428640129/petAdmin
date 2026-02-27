<template>
	<view class="container">
		<!-- 预约详情卡片 -->
		<view class="detail-card">
			<view class="detail-section">
				<text class="section-title">预约信息</text>
				<view class="detail-item">
					<text class="item-label">预约时间</text>
					<text class="item-value">{{ appointment.appointmentDate }} {{ appointment.appointmentTime }}</text>
				</view>
				<view class="detail-item">
					<text class="item-label">服务类型</text>
					<text class="item-value">{{ appointment.serviceName }}</text>
				</view>
				<view class="detail-item">
					<text class="item-label">服务价格</text>
					<text class="item-value price">¥{{ appointment.price }}</text>
				</view>
				<view class="detail-item">
					<text class="item-label">预约状态</text>
					<text class="item-value status" :class="'status-' + appointment.status">{{ getStatusText(appointment.status) }}</text>
				</view>
			</view>

			<view class="detail-section">
				<text class="section-title">宠物信息</text>
				<view class="detail-item">
					<text class="item-label">宠物名称</text>
					<text class="item-value">{{ appointment.petName }}</text>
				</view>
				<view class="detail-item">
					<text class="item-label">宠物类型</text>
					<text class="item-value">{{ appointment.petType }}</text>
				</view>
				<view class="detail-item">
					<text class="item-label">宠物体重</text>
					<text class="item-value">{{ appointment.petWeight }}kg</text>
				</view>
			</view>

			<view class="detail-section">
				<text class="section-title">联系人信息</text>
				<view class="detail-item">
					<text class="item-label">联系人</text>
					<text class="item-value">{{ appointment.contactName }}</text>
				</view>
				<view class="detail-item">
					<text class="item-label">联系电话</text>
					<text class="item-value" @click="makeCall">{{ appointment.contactPhone }}</text>
				</view>
			</view>

			<view class="detail-section" v-if="appointment.remark">
				<text class="section-title">备注信息</text>
				<text class="remark-text">{{ appointment.remark }}</text>
			</view>

			<!-- 评价信息 -->
			<view class="detail-section" v-if="appointment.review">
				<text class="section-title">用户评价</text>
				<view class="review-item">
					<view class="review-rating">
						<text class="rating-text">评分：</text>
						<text class="stars">{{ getStars(appointment.review.rating) }}</text>
					</view>
					<text class="review-content">{{ appointment.review.content }}</text>
					<text class="review-time">{{ appointment.review.createTime }}</text>
				</view>
				<view class="review-reply" v-if="appointment.review.reply">
					<text class="reply-label">商家回复：</text>
					<text class="reply-content">{{ appointment.review.reply }}</text>
				</view>
			</view>
		</view>

		<!-- 操作按钮 -->
		<view class="action-buttons">
			<button 
				class="action-btn cancel" 
				v-if="appointment.status === APPOINTMENT_STATUS.PENDING"
				@click="handleAction('reject')"
			>
				拒绝预约
			</button>
			<button 
				class="action-btn confirm" 
				v-if="appointment.status === APPOINTMENT_STATUS.PENDING"
				@click="handleAction('confirm')"
			>
				确认预约
			</button>
			<button 
				class="action-btn complete" 
				v-if="appointment.status === APPOINTMENT_STATUS.CONFIRMED"
				@click="handleAction('complete')"
			>
				完成服务
			</button>
			<button 
				class="action-btn reply" 
				v-if="appointment.status === APPOINTMENT_STATUS.COMPLETED && appointment.review && !appointment.review.reply"
				@click="goToReply"
			>
				回复评价
			</button>
		</view>
	</view>
</template>

<script>
	import { APPOINTMENT_STATUS, APPOINTMENT_STATUS_TEXT } from '@/utils/constants.js'
	
	export default {
		data() {
			return {
				appointmentId: '',
				appointment: {
					id: 1,
					appointmentDate: '2026-01-21',
					appointmentTime: '14:00',
					serviceName: '基础洗浴',
					price: 88,
					status: APPOINTMENT_STATUS.PENDING, // 使用数字状态：'0'
					petName: '旺财',
					petType: '金毛',
					petWeight: '25',
					contactName: '张先生',
					contactPhone: '13800138000',
					remark: '狗狗比较怕水，请温柔一点',
					review: null
				}
			}
		},
		onLoad(options) {
			if (options.id) {
				this.appointmentId = options.id;
				this.loadDetail();
			}
		},
		methods: {
			async loadDetail() {
				// 加载预约详情
				// try {
				//   const res = await uni.request({
				//     url: `http://localhost:8080/bath/appointment/merchant/detail/${this.appointmentId}`,
				//     method: 'GET'
				//   });
				//   this.appointment = res.data.data || {};
				// } catch (error) {
				//   console.error('加载详情失败', error);
				//   uni.showToast({
				//     title: '加载失败',
				//     icon: 'none'
				//   });
				// }
			},
			handleAction(action) {
				let title = '';
				let content = '';

				switch(action) {
					case 'confirm':
						title = '确认预约';
						content = '确认接受这个预约吗？';
						break;
					case 'reject':
						title = '拒绝预约';
						content = '确定要拒绝这个预约吗？';
						break;
					case 'complete':
						title = '完成服务';
						content = '确认已完成服务吗？';
						break;
				}

				uni.showModal({
					title: title,
					content: content,
					success: async (res) => {
						if (res.confirm) {
							try {
								// 调用接口
								// await uni.request({
								//   url: `http://localhost:8080/bath/appointment/merchant/${action}/${this.appointmentId}`,
								//   method: 'POST'
								// });

								uni.showToast({
									title: '操作成功',
									icon: 'success'
								});

								setTimeout(() => {
									uni.navigateBack();
								}, 1500);
							} catch (error) {
								uni.showToast({
									title: '操作失败',
									icon: 'none'
								});
							}
						}
					}
				});
			},
			goToReply() {
				uni.navigateTo({
					url: `/pages/merchant/reply?id=${this.appointmentId}`
				});
			},
			makeCall() {
				uni.makePhoneCall({
					phoneNumber: this.appointment.contactPhone
				});
			},
			getStatusText(status) {
				return APPOINTMENT_STATUS_TEXT[status] || '未知';
			},
			getStars(rating) {
				return '⭐'.repeat(rating || 0);
			}
		}
	}
</script>

<style lang="scss" scoped>
	.container {
		min-height: 100vh;
		background-color: #f7fafc;
		padding-bottom: 200rpx;
	}

	.detail-card {
		background-color: #fff;
		margin: 20rpx 30rpx;
		border-radius: 24rpx;
		padding: 30rpx;
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
	}

	.detail-section {
		margin-bottom: 40rpx;
	}

	.detail-section:last-child {
		margin-bottom: 0;
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		display: block;
		margin-bottom: 24rpx;
		padding-bottom: 16rpx;
		border-bottom: 2rpx solid #f0f0f0;
	}

	.detail-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
	}

	.item-label {
		font-size: 28rpx;
		color: #666;
	}

	.item-value {
		font-size: 28rpx;
		color: #333;
		font-weight: 500;
	}

	.item-value.price {
		color: #F4A460;
		font-size: 32rpx;
		font-weight: bold;
	}

	.item-value.status {
		padding: 8rpx 16rpx;
		border-radius: 20rpx;
		font-size: 24rpx;
	}

	.item-value.status.status-0 {
		background-color: #FFF8F0;
		color: #F4A460;
	}

	.item-value.status.status-1 {
		background-color: #E6F7FF;
		color: #1890ff;
	}

	.item-value.status.status-2 {
		background-color: #E6F7FF;
		color: #1890ff;
	}

	.item-value.status.status-3 {
		background-color: #F6FFED;
		color: #52c41a;
	}

	.item-value.status.status-4 {
		background-color: #F5F5F5;
		color: #999;
	}

	.remark-text {
		font-size: 28rpx;
		color: #666;
		line-height: 1.8;
	}

	.review-item {
		background-color: #f9f9f9;
		border-radius: 16rpx;
		padding: 24rpx;
	}

	.review-rating {
		display: flex;
		align-items: center;
		margin-bottom: 16rpx;
	}

	.rating-text {
		font-size: 26rpx;
		color: #666;
		margin-right: 12rpx;
	}

	.stars {
		font-size: 32rpx;
	}

	.review-content {
		font-size: 28rpx;
		color: #333;
		line-height: 1.8;
		margin-bottom: 12rpx;
		display: block;
	}

	.review-time {
		font-size: 22rpx;
		color: #999;
	}

	.review-reply {
		margin-top: 20rpx;
		padding: 20rpx;
		background-color: #FFF8F0;
		border-radius: 12rpx;
		border-left: 4rpx solid #F4A460;
	}

	.reply-label {
		font-size: 24rpx;
		color: #F4A460;
		font-weight: bold;
		display: block;
		margin-bottom: 8rpx;
	}

	.reply-content {
		font-size: 26rpx;
		color: #666;
		line-height: 1.8;
	}

	.action-buttons {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		background-color: #fff;
		padding: 20rpx 30rpx;
		padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
		box-shadow: 0 -4rpx 12rpx rgba(0, 0, 0, 0.08);
		display: flex;
		gap: 20rpx;
	}

	.action-btn {
		flex: 1;
		height: 88rpx;
		border-radius: 44rpx;
		font-size: 30rpx;
		font-weight: bold;
		border: none;
	}

	.action-btn.confirm {
		background: linear-gradient(135deg, #F4A460, #FFA500);
		color: #fff;
	}

	.action-btn.cancel {
		background-color: #fff;
		color: #999;
		border: 2rpx solid #e0e0e0;
	}

	.action-btn.complete {
		background-color: #52c41a;
		color: #fff;
	}

	.action-btn.reply {
		background-color: #1890ff;
		color: #fff;
	}
</style>

