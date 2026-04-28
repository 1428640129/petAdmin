<template>
	<view class="container">
		<!-- 顶部统计卡片 -->
		<view class="stats-section">
			<view class="stats-row">
				<view class="stat-card">
					<text class="stat-value">{{ stats.todayCount }}</text>
					<text class="stat-label">今日预约</text>
				</view>
				<view class="stat-card">
					<text class="stat-value">{{ stats.pendingCount }}</text>
					<text class="stat-label">待处理</text>
				</view>
				<view class="stat-card">
					<text class="stat-value">{{ stats.completedCount }}</text>
					<text class="stat-label">已完成</text>
				</view>
			</view>
		</view>

		<!-- 筛选标签 -->
		<view class="filter-section">
			<scroll-view class="filter-tabs" scroll-x="true" show-scrollbar="false">
				<view 
					class="filter-tab" 
					:class="{ active: activeFilter === filter.value }"
					v-for="filter in filters" 
					:key="filter.value"
					@click="switchFilter(filter.value)"
				>
					<text>{{ filter.label }}</text>
					<text class="filter-badge" v-if="filter.count > 0">{{ filter.count }}</text>
				</view>
			</scroll-view>
		</view>

		<!-- 预约列表 -->
		<view class="appointment-list">
			<view 
				class="appointment-card" 
				v-for="(item, index) in appointments" 
				:key="index"
				@click="viewDetail(item)"
			>
				<view class="card-header">
					<view class="header-left">
						<text class="appointment-time">{{ item.appointmentDate }} {{ item.appointmentTime }}</text>
						<text class="service-name">{{ item.serviceName }}</text>
					</view>
					<text class="status-badge" :class="'status-' + item.status">{{ getStatusText(item.status) }}</text>
				</view>

				<view class="card-content">
					<view class="info-row">
						<text class="info-label">宠物信息：</text>
						<text class="info-value">{{ item.petName }} · {{ item.petType }} · {{ item.petWeight }}kg</text>
					</view>
					<view class="info-row">
						<text class="info-label">联系人：</text>
						<text class="info-value">{{ item.contactName }} {{ item.contactPhone }}</text>
					</view>
					<view class="info-row" v-if="item.remark">
						<text class="info-label">备注：</text>
						<text class="info-value">{{ item.remark }}</text>
					</view>
				</view>

				<view class="card-footer">
					<text class="price">¥{{ item.price }}</text>
					<view class="actions">
						<button 
							class="action-btn cancel" 
							v-if="item.status === APPOINTMENT_STATUS.PENDING"
							@click.stop="handleAction(item, 'cancel')"
						>
							拒绝
						</button>
						<button 
							class="action-btn confirm" 
							v-if="item.status === APPOINTMENT_STATUS.PENDING"
							@click.stop="handleAction(item, 'confirm')"
						>
							确认
						</button>
						<button 
							class="action-btn complete" 
							v-if="item.status === APPOINTMENT_STATUS.CONFIRMED"
							@click.stop="handleAction(item, 'complete')"
						>
							完成
						</button>
						<button 
							class="action-btn reply" 
							v-if="item.status === APPOINTMENT_STATUS.COMPLETED && item.hasReview"
							@click.stop="replyReview(item)"
						>
							回复评价
						</button>
					</view>
				</view>
			</view>

			<!-- 空状态 -->
			<view class="empty-state" v-if="appointments.length === 0">
				<text class="empty-icon">📋</text>
				<text class="empty-text">暂无预约记录</text>
			</view>
		</view>
	</view>
</template>

<script>
	import { APPOINTMENT_STATUS, APPOINTMENT_STATUS_TEXT } from '@/utils/constants.js'
	
	export default {
		data() {
			return {
				stats: {
					todayCount: 0,
					pendingCount: 0,
					completedCount: 0
				},
				activeFilter: 'all',
				filters: [
					{ label: '全部', value: 'all', count: 0 },
					{ label: '待处理', value: APPOINTMENT_STATUS.PENDING, count: 0 },
					{ label: '已确认', value: APPOINTMENT_STATUS.CONFIRMED, count: 0 },
					{ label: '已完成', value: APPOINTMENT_STATUS.COMPLETED, count: 0 },
					{ label: '已取消', value: APPOINTMENT_STATUS.CANCELLED, count: 0 }
				],
				appointments: [
					// 示例数据
					// {
					//   id: 1,
					//   appointmentDate: '2026-01-21',
					//   appointmentTime: '14:00',
					//   serviceName: '基础洗浴',
					//   petName: '旺财',
					//   petType: '金毛',
					//   petWeight: '25',
					//   contactName: '张先生',
					//   contactPhone: '138****8888',
					//   remark: '狗狗比较怕水，请温柔一点',
					//   price: 88,
					//   status: 'pending', // pending, confirmed, completed, cancelled
					//   hasReview: false
					// }
				]
			}
		},
		onLoad() {
			this.loadData();
		},
		onShow() {
			this.loadData();
		},
		onPullDownRefresh() {
			this.loadData();
			setTimeout(() => {
				uni.stopPullDownRefresh();
			}, 1000);
		},
		methods: {
			async loadData() {
				// 加载统计数据
				// const statsRes = await uni.request({
				//   url: 'http://localhost:8080/bath/appointment/merchant/stats',
				//   method: 'GET'
				// });
				// this.stats = statsRes.data.data || {};

				// 加载预约列表
				await this.loadAppointments();
			},
			async loadAppointments() {
				// 调用接口获取预约列表
				// try {
				//   const res = await uni.request({
				//     url: 'http://localhost:8080/bath/appointment/merchant/list',
				//     method: 'GET',
				//     data: {
				//       status: this.activeFilter === 'all' ? '' : this.activeFilter
				//     }
				//   });
				//   this.appointments = res.data.data || [];
				//   
				//   // 更新筛选标签数量
				//   this.updateFilterCounts();
				// } catch (error) {
				//   console.error('加载预约列表失败', error);
				//   uni.showToast({
				//     title: '加载失败',
				//     icon: 'none'
				//   });
				// }
			},
			updateFilterCounts() {
				// 根据实际数据更新筛选标签数量
				// this.filters.forEach(filter => {
				//   if (filter.value === 'all') {
				//     filter.count = this.appointments.length;
				//   } else {
				//     filter.count = this.appointments.filter(item => item.status === filter.value).length;
				//   }
				// });
			},
			switchFilter(value) {
				this.activeFilter = value;
				this.loadAppointments();
			},
			viewDetail(item) {
				const aid = item.appointmentId != null ? item.appointmentId : item.id;
				uni.navigateTo({
					url: `/pages/merchant/detail?id=${aid}`
				});
			},
			async handleAction(item, action) {
				let title = '';
				let content = '';
				let apiAction = '';

				switch(action) {
					case 'confirm':
						title = '确认预约';
						content = '确认接受这个预约吗？';
						apiAction = 'confirm';
						break;
					case 'cancel':
						title = '拒绝预约';
						content = '确定要拒绝这个预约吗？';
						apiAction = 'reject';
						break;
					case 'complete':
						title = '完成服务';
						content = '确认已完成服务吗？';
						apiAction = 'complete';
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
								//   url: `http://localhost:8080/bath/appointment/merchant/${apiAction}/${item.id}`,
								//   method: 'POST'
								// });

								uni.showToast({
									title: '操作成功',
									icon: 'success'
								});

								this.loadData();
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
			replyReview(item) {
				uni.navigateTo({
					url: `/pages/merchant/reply?id=${item.id}`
				});
			},
			getStatusText(status) {
				return APPOINTMENT_STATUS_TEXT[status] || '未知';
			}
		}
	}
</script>

<style lang="scss" scoped>
	.container {
		min-height: 100vh;
		background-color: #f7fafc;
		padding-bottom: 100rpx;
	}

	.stats-section {
		background-color: #fff;
		padding: 30rpx;
		margin-bottom: 20rpx;
	}

	.stats-row {
		display: flex;
		justify-content: space-around;
	}

	.stat-card {
		display: flex;
		flex-direction: column;
		align-items: center;
		flex: 1;
	}

	.stat-value {
		font-size: 48rpx;
		font-weight: bold;
		color: #F4A460;
		margin-bottom: 12rpx;
	}

	.stat-label {
		font-size: 24rpx;
		color: #666;
	}

	.filter-section {
		background-color: #fff;
		padding: 20rpx 0;
		margin-bottom: 20rpx;
	}

	.filter-tabs {
		white-space: nowrap;
		padding: 0 30rpx;
	}

	.filter-tab {
		display: inline-flex;
		align-items: center;
		padding: 12rpx 24rpx;
		margin-right: 20rpx;
		border-radius: 40rpx;
		background-color: #f5f5f5;
		font-size: 26rpx;
		color: #666;
	}

	.filter-tab.active {
		background-color: #FFF8F0;
		color: #F4A460;
		font-weight: bold;
	}

	.filter-badge {
		background-color: #ff4444;
		color: #fff;
		border-radius: 20rpx;
		padding: 2rpx 8rpx;
		font-size: 20rpx;
		margin-left: 8rpx;
	}

	.appointment-list {
		padding: 0 30rpx;
	}

	.appointment-card {
		background-color: #fff;
		border-radius: 24rpx;
		padding: 30rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
	}

	.card-header {
		display: flex;
		justify-content: space-between;
		align-items: flex-start;
		margin-bottom: 24rpx;
		padding-bottom: 24rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.header-left {
		flex: 1;
	}

	.appointment-time {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		display: block;
		margin-bottom: 8rpx;
	}

	.service-name {
		font-size: 24rpx;
		color: #999;
	}

	.status-badge {
		font-size: 24rpx;
		padding: 8rpx 16rpx;
		border-radius: 20rpx;
	}

	.status-badge.status-0 {
		background-color: #FFF8F0;
		color: #F4A460;
	}

	.status-badge.status-1 {
		background-color: #E6F7FF;
		color: #1890ff;
	}

	.status-badge.status-2 {
		background-color: #E6F7FF;
		color: #1890ff;
	}

	.status-badge.status-3 {
		background-color: #F6FFED;
		color: #52c41a;
	}

	.status-badge.status-4 {
		background-color: #F5F5F5;
		color: #999;
	}

	.card-content {
		margin-bottom: 24rpx;
	}

	.info-row {
		display: flex;
		margin-bottom: 12rpx;
		font-size: 26rpx;
	}

	.info-label {
		color: #666;
		width: 140rpx;
		flex-shrink: 0;
	}

	.info-value {
		color: #333;
		flex: 1;
	}

	.card-footer {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding-top: 24rpx;
		border-top: 1rpx solid #f0f0f0;
	}

	.price {
		font-size: 36rpx;
		font-weight: bold;
		color: #F4A460;
	}

	.actions {
		display: flex;
		gap: 16rpx;
	}

	.action-btn {
		border-radius: 20rpx;
		padding: 12rpx 24rpx;
		font-size: 24rpx;
		border: none;
	}

	.action-btn.confirm {
		background-color: #F4A460;
		color: #fff;
	}

	.action-btn.cancel {
		background-color: #fff;
		color: #999;
		border: 1rpx solid #e0e0e0;
	}

	.action-btn.complete {
		background-color: #52c41a;
		color: #fff;
	}

	.action-btn.reply {
		background-color: #1890ff;
		color: #fff;
	}

	.empty-state {
		text-align: center;
		padding: 120rpx 30rpx;
		background-color: #fff;
		border-radius: 24rpx;
	}

	.empty-icon {
		font-size: 120rpx;
		display: block;
		margin-bottom: 30rpx;
	}

	.empty-text {
		font-size: 28rpx;
		color: #999;
	}
</style>

