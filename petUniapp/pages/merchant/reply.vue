<template>
	<view class="container">
		<!-- 评价信息 -->
		<view class="review-card">
			<view class="review-header">
				<text class="review-title">用户评价</text>
				<view class="review-rating">
					<text class="stars">{{ getStars(review.rating) }}</text>
					<text class="rating-num">{{ review.rating }}分</text>
				</view>
			</view>
			<text class="review-content">{{ review.content }}</text>
			<text class="review-time">{{ review.createTime }}</text>
		</view>

		<!-- 回复表单 -->
		<view class="reply-form">
			<text class="form-title">商家回复</text>
			<textarea 
				class="reply-input" 
				v-model="replyContent" 
				placeholder="请输入回复内容..."
				maxlength="500"
				:show-confirm-bar="false"
			></textarea>
			<view class="char-count">{{ replyContent.length }}/500</view>
		</view>

		<!-- 提交按钮 -->
		<view class="submit-section">
			<button class="submit-btn" :disabled="!canSubmit" @click="submitReply">
				提交回复
			</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				appointmentId: '',
				review: {
					id: 1,
					rating: 5,
					content: '服务很好，狗狗洗得很干净，工作人员很专业！',
					createTime: '2026-01-21 16:30'
				},
				replyContent: ''
			}
		},
		computed: {
			canSubmit() {
				return this.replyContent.trim().length > 0;
			}
		},
		onLoad(options) {
			if (options.id) {
				this.appointmentId = options.id;
				this.loadReview();
			}
		},
		methods: {
			async loadReview() {
				// 加载评价信息
				// try {
				//   const res = await uni.request({
				//     url: `http://localhost:8080/bath/review/detail/${this.appointmentId}`,
				//     method: 'GET'
				//   });
				//   this.review = res.data.data || {};
				// } catch (error) {
				//   console.error('加载评价失败', error);
				//   uni.showToast({
				//     title: '加载失败',
				//     icon: 'none'
				//   });
				// }
			},
			async submitReply() {
				if (!this.canSubmit) {
					uni.showToast({
						title: '请输入回复内容',
						icon: 'none'
					});
					return;
				}

				try {
					// 调用回复接口
					// await uni.request({
					//   url: `http://localhost:8080/bath/review/reply/${this.appointmentId}`,
					//   method: 'POST',
					//   data: {
					//     reply: this.replyContent
					//   }
					// });

					uni.showToast({
						title: '回复成功',
						icon: 'success'
					});

					setTimeout(() => {
						uni.navigateBack();
					}, 1500);
				} catch (error) {
					uni.showToast({
						title: '回复失败，请重试',
						icon: 'none'
					});
				}
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
		padding: 30rpx;
		padding-bottom: 200rpx;
	}

	.review-card {
		background-color: #fff;
		border-radius: 24rpx;
		padding: 30rpx;
		margin-bottom: 30rpx;
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
	}

	.review-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
	}

	.review-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.review-rating {
		display: flex;
		align-items: center;
		gap: 12rpx;
	}

	.stars {
		font-size: 32rpx;
	}

	.rating-num {
		font-size: 28rpx;
		color: #F4A460;
		font-weight: bold;
	}

	.review-content {
		font-size: 28rpx;
		color: #333;
		line-height: 1.8;
		margin-bottom: 16rpx;
		display: block;
	}

	.review-time {
		font-size: 22rpx;
		color: #999;
	}

	.reply-form {
		background-color: #fff;
		border-radius: 24rpx;
		padding: 30rpx;
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
	}

	.form-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		display: block;
		margin-bottom: 24rpx;
	}

	.reply-input {
		width: 100%;
		min-height: 300rpx;
		background-color: #f5f5f5;
		border-radius: 16rpx;
		padding: 24rpx;
		font-size: 28rpx;
		color: #333;
		margin-bottom: 16rpx;
	}

	.char-count {
		text-align: right;
		font-size: 24rpx;
		color: #999;
	}

	.submit-section {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		background-color: #fff;
		padding: 20rpx 30rpx;
		padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
		box-shadow: 0 -4rpx 12rpx rgba(0, 0, 0, 0.08);
	}

	.submit-btn {
		width: 100%;
		height: 88rpx;
		background: linear-gradient(135deg, #F4A460, #FFA500);
		border-radius: 44rpx;
		color: #fff;
		font-size: 32rpx;
		font-weight: bold;
		border: none;
	}

	.submit-btn[disabled] {
		background-color: #ccc;
	}
</style>










