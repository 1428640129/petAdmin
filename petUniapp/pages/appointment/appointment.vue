<template>
	<view class="container">
		<!-- 顶部导航栏 -->
		<view class="header">
			<text class="header-title">我的预约</text>
		</view>
		
		<!-- 状态筛选标签 -->
		<view class="status-tabs">
			<view 
				class="tab-item" 
				:class="{ active: activeTab === 'all' }"
				@click="switchStatusTab('all')"
			>
				<text>全部</text>
			</view>
			<view 
				class="tab-item" 
				:class="{ active: activeTab === '0' }"
				@click="switchStatusTab('0')"
			>
				<text>待确认</text>
			</view>
			<view 
				class="tab-item" 
				:class="{ active: activeTab === '1' }"
				@click="switchStatusTab('1')"
			>
				<text>已确认</text>
			</view>
			<view 
				class="tab-item" 
				:class="{ active: activeTab === '2' }"
				@click="switchStatusTab('2')"
			>
				<text>服务中</text>
			</view>
			<view 
				class="tab-item" 
				:class="{ active: activeTab === '3' }"
				@click="switchStatusTab('3')"
			>
				<text>已完成</text>
			</view>
		</view>

		<!-- 预约列表 -->
		<scroll-view 
			class="appointment-list" 
			scroll-y="true"
			@scrolltolower="loadMore"
			refresher-enabled
			@refresherrefresh="onRefresh"
			:refresher-triggered="refreshing"
		>
			<view v-if="loading && appointmentList.length === 0" class="loading-container">
				<text>加载中...</text>
			</view>
			
			<view v-else-if="appointmentList.length === 0" class="empty-container">
				<text>暂无预约记录</text>
			</view>
			
			<view v-else>
				<view 
					class="appointment-item" 
					v-for="item in appointmentList" 
					:key="item.appointmentId"
					@click="viewDetail(item)"
				>
					<view class="item-header">
						<text class="appointment-no">预约单号：{{ item.appointmentNo }}</text>
						<view class="status-badge" :class="getStatusClass(item.status)">
							<text>{{ getStatusText(item.status) }}</text>
						</view>
					</view>
					
					<view class="item-content">
						<view class="info-row">
							<text class="label">服务名称：</text>
							<text class="value">{{ item.serviceName || '未命名服务' }}</text>
						</view>
						<view class="info-row">
							<text class="label">宠物名称：</text>
							<text class="value">{{ item.petName || '未填写' }}</text>
						</view>
						<view class="info-row">
							<text class="label">预约时间：</text>
							<text class="value">{{ formatDateTime(item.appointmentTime) }}</text>
						</view>
						<view class="info-row">
							<text class="label">预计价格：</text>
							<text class="value price">¥{{ formatPrice(item.expectedPrice) }}</text>
						</view>
					</view>
					
					<view class="item-footer">
						<text class="create-time">创建时间：{{ formatDateTime(item.createTime) }}</text>
						<!-- 已确认状态且订单未支付时显示支付按钮 -->
						<view v-if="item.status === '1' && !isOrderPaid(item)" class="action-buttons">
							<button class="pay-btn" @click.stop="handlePay(item)">立即支付</button>
						</view>
						<!-- 已完成状态显示评价按钮 -->
						<view v-if="item.status === '3'" class="action-buttons">
							<button 
								v-if="!item.hasReview" 
								class="review-btn" 
								@click.stop="goToReview(item)"
							>
								去评价
							</button>
							<text v-else class="reviewed-text">已评价</text>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 加载更多提示 -->
			<view v-if="hasMore && !loading" class="load-more">
				<text>上拉加载更多</text>
			</view>
			<view v-if="!hasMore && appointmentList.length > 0" class="load-more">
				<text>没有更多了</text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				activeTab: 'all',
				appointmentList: [],
				loading: false,
				refreshing: false,
				currentPage: 1,
				pageSize: 10,
				hasMore: true,
				orderStatusCache: {} // 缓存订单状态，key为appointmentId，value为订单状态
			}
		},
		onLoad() {
			this.loadAppointmentList();
		},
		onPullDownRefresh() {
			this.onRefresh();
		},
		methods: {
			switchStatusTab(status) {
				this.activeTab = status;
				this.currentPage = 1;
				this.hasMore = true;
				this.appointmentList = [];
				this.loadAppointmentList();
			},
			async loadAppointmentList() {
				if (this.loading) return;
				
				this.loading = true;
				try {
					const app = getApp();
					const baseUrl = (app && app.globalData && app.globalData.baseUrl) || 'http://localhost:8080';
					const token = uni.getStorageSync('token');
					
					// 构建查询参数
					const params = {
						pageNum: this.currentPage,
						pageSize: this.pageSize
					};
					
					// 如果选择了特定状态，添加状态筛选
					if (this.activeTab !== 'all') {
						params.status = this.activeTab;
					}
					
					const res = await uni.request({
						url: `${baseUrl}/bath/appointment/miniprogram/list`,
						method: 'GET',
						data: params,
						header: {
							'Content-Type': 'application/json',
							'Authorization': token ? `Bearer ${token}` : ''
						}
					});
					
					if (res.statusCode === 200 && res.data && res.data.code === 200) {
						const data = res.data.data || res.data;
						const rows = data.rows || data.list || [];
						const total = data.total || 0;
						
						if (this.currentPage === 1) {
							this.appointmentList = rows;
						} else {
							this.appointmentList = this.appointmentList.concat(rows);
						}
						
					// 批量查询订单状态（仅对已确认状态的预约）
					this.batchCheckOrderStatus(rows);
					
					// 注意：评价状态（hasReview）已由后端直接返回，无需前端再次查询
					
					// 判断是否还有更多数据
					this.hasMore = this.appointmentList.length < total;
					} else {
						throw new Error(res.data?.msg || '获取预约列表失败');
					}
				} catch (error) {
					console.error('获取预约列表失败:', error);
					uni.showToast({
						title: error.message || '获取预约列表失败',
						icon: 'none'
					});
				} finally {
					this.loading = false;
					this.refreshing = false;
					uni.stopPullDownRefresh();
				}
			},
			loadMore() {
				if (!this.hasMore || this.loading) return;
				this.currentPage++;
				this.loadAppointmentList();
			},
			onRefresh() {
				this.refreshing = true;
				this.currentPage = 1;
				this.hasMore = true;
				this.appointmentList = [];
				this.loadAppointmentList();
			},
			viewDetail(item) {
				// 跳转到预约详情页
				uni.navigateTo({
					url: `/pages/appointment/detail?appointmentId=${item.appointmentId}`
				});
			},
			getStatusText(status) {
				const statusMap = {
					'0': '待确认',
					'1': '已确认',
					'2': '服务中',
					'3': '已完成',
					'4': '已取消'
				};
				return statusMap[status] || '未知';
			},
			getStatusClass(status) {
				const classMap = {
					'0': 'status-pending',
					'1': 'status-confirmed',
					'2': 'status-in-service',
					'3': 'status-completed',
					'4': 'status-cancelled'
				};
				return classMap[status] || '';
			},
			formatDateTime(dateTime) {
				if (!dateTime) return '';
				
				// iOS 兼容性处理：将 "yyyy-MM-dd HH:mm:ss" 转换为 "yyyy-MM-ddTHH:mm:ss"
				let dateStr = dateTime;
				if (typeof dateTime === 'string') {
					// 如果包含空格，替换为 T（ISO 8601 格式，iOS 支持）
					dateStr = dateTime.replace(' ', 'T');
					// 如果没有秒数，添加 :00
					if (dateStr.split(':').length === 2) {
						dateStr += ':00';
					}
				}
				
				const date = new Date(dateStr);
				
				// 检查日期是否有效
				if (isNaN(date.getTime())) {
					// 如果解析失败，尝试手动解析
					if (typeof dateTime === 'string') {
						const parts = dateTime.match(/(\d{4})-(\d{2})-(\d{2})\s+(\d{2}):(\d{2}):?(\d{2})?/);
						if (parts) {
							const year = parseInt(parts[1]);
							const month = parseInt(parts[2]) - 1; // 月份从0开始
							const day = parseInt(parts[3]);
							const hours = parseInt(parts[4]);
							const minutes = parseInt(parts[5]);
							return `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')} ${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}`;
						}
					}
					return dateTime; // 如果都解析失败，返回原字符串
				}
				
				const year = date.getFullYear();
				const month = String(date.getMonth() + 1).padStart(2, '0');
				const day = String(date.getDate()).padStart(2, '0');
				const hours = String(date.getHours()).padStart(2, '0');
				const minutes = String(date.getMinutes()).padStart(2, '0');
				return `${year}-${month}-${day} ${hours}:${minutes}`;
			},
			formatPrice(price) {
				if (!price) return '0.00';
				return parseFloat(price).toFixed(2);
			},
			// 检查订单是否已支付
			isOrderPaid(item) {
				// 如果缓存中有订单状态，直接返回
				if (this.orderStatusCache[item.appointmentId] !== undefined) {
					const orderStatus = this.orderStatusCache[item.appointmentId];
					// 订单状态 '1' 表示已支付，'0' 表示待支付
					return orderStatus === '1' || orderStatus === 'paid';
				}
				// 如果没有缓存，异步查询订单状态
				this.checkOrderStatus(item);
				return false; // 默认返回false，避免按钮闪烁
			},
			// 检查订单状态并缓存
			async checkOrderStatus(item) {
				try {
					const app = getApp();
					const baseUrl = (app && app.globalData && app.globalData.baseUrl) || 'http://localhost:8080';
					const token = uni.getStorageSync('token');
					
					// 查询订单信息
					const orderRes = await uni.request({
						url: `${baseUrl}/bath/order/miniprogram/byAppointment/${item.appointmentId}`,
						method: 'GET',
						header: {
							'Content-Type': 'application/json',
							'Authorization': token ? `Bearer ${token}` : ''
						}
					});
					
					if (orderRes.statusCode === 200 && orderRes.data && orderRes.data.code === 200) {
						const order = orderRes.data.data;
						if (order && order.status) {
							// 缓存订单状态
							this.$set(this.orderStatusCache, item.appointmentId, order.status);
							// 强制更新视图
							this.$forceUpdate();
						}
					}
				} catch (error) {
					console.error('查询订单状态失败:', error);
					// 查询失败时，不缓存，下次再查询
				}
			},
			// 批量检查订单状态
			async batchCheckOrderStatus(appointments) {
				// 只查询已确认状态的预约
				const confirmedAppointments = appointments.filter(item => item.status === '1');
				if (confirmedAppointments.length === 0) return;
				
				// 并发查询所有订单状态
				const promises = confirmedAppointments.map(item => this.checkOrderStatus(item));
				await Promise.all(promises);
			},
			// 检查是否已评价
			async checkReviewStatus(item) {
				try {
					const app = getApp();
					const baseUrl = (app && app.globalData && app.globalData.baseUrl) || 'http://localhost:8080';
					const token = uni.getStorageSync('token');
					
					// 查询评价信息
					const reviewRes = await uni.request({
						url: `${baseUrl}/bath/review/miniprogram/byAppointment/${item.appointmentId}`,
						method: 'GET',
						header: {
							'Content-Type': 'application/json',
							'Authorization': token ? `Bearer ${token}` : ''
						}
					});
					
					if (reviewRes.statusCode === 200 && reviewRes.data && reviewRes.data.code === 200) {
						const review = reviewRes.data.data;
						// 设置是否已评价标记
						this.$set(item, 'hasReview', review != null);
						// 强制更新视图
						this.$forceUpdate();
					}
				} catch (error) {
					console.error('查询评价状态失败:', error);
					// 查询失败时，默认未评价
					this.$set(item, 'hasReview', false);
				}
			},
			// 批量检查评价状态
			async batchCheckReviewStatus(appointments) {
				// 只查询已完成状态的预约
				const completedAppointments = appointments.filter(item => item.status === '3');
				if (completedAppointments.length === 0) return;
				
				// 并发查询所有评价状态
				const promises = completedAppointments.map(item => this.checkReviewStatus(item));
				await Promise.all(promises);
			},
			// 跳转到评价页面
			goToReview(item) {
				uni.navigateTo({
					url: `/pages/review/review?appointmentId=${item.appointmentId}&serviceId=${item.serviceId || ''}&serviceName=${encodeURIComponent(item.serviceName || '')}`
				});
			},
			// 处理支付成功
			async handlePaymentSuccess(baseUrl, token, orderId, tradeNo, appointmentId) {
				try {
					// 调用支付回调接口更新订单状态
					const callbackRes = await uni.request({
						url: `${baseUrl}/bath/order/miniprogram/alipay/callback`,
						method: 'POST',
						data: {
							orderId: orderId,
							tradeNo: tradeNo,
							tradeStatus: 'TRADE_SUCCESS'
						},
						header: {
							'Content-Type': 'application/json',
							'Authorization': token ? `Bearer ${token}` : ''
						}
					});
					
					if (callbackRes.statusCode === 200 && callbackRes.data && callbackRes.data.code === 200) {
						// 更新订单状态缓存为已支付
						if (appointmentId) {
							this.$set(this.orderStatusCache, appointmentId, '1');
						}
						uni.showToast({
							title: '支付成功',
							icon: 'success'
						});
						// 刷新列表
						this.onRefresh();
					} else {
						uni.showToast({
							title: '支付成功，但更新订单状态失败',
							icon: 'none'
						});
					}
				} catch (error) {
					console.error('更新订单状态失败:', error);
					uni.showToast({
						title: '支付成功，但更新订单状态失败',
						icon: 'none'
					});
				}
			},
			// 处理支付失败
			handlePaymentFail(err) {
				console.error('支付失败:', err);
				
				// 如果是用户取消支付，不显示错误提示
				if (err.errMsg && (err.errMsg.indexOf('cancel') !== -1 || err.errMsg.indexOf('取消') !== -1)) {
					uni.showToast({
						title: '已取消支付',
						icon: 'none'
					});
				} else {
					uni.showToast({
						title: '支付失败：' + (err.errMsg || '未知错误'),
						icon: 'none'
					});
				}
			},
			// 处理支付
			async handlePay(item) {
				try {
					// 先查询订单信息
					const app = getApp();
					const baseUrl = (app && app.globalData && app.globalData.baseUrl) || 'http://localhost:8080';
					const token = uni.getStorageSync('token');
					
					// 根据预约ID查询订单（使用小程序专用接口）
					const orderRes = await uni.request({
						url: `${baseUrl}/bath/order/miniprogram/byAppointment/${item.appointmentId}`,
						method: 'GET',
						header: {
							'Content-Type': 'application/json',
							'Authorization': token ? `Bearer ${token}` : ''
						}
					});
					
					if (orderRes.statusCode === 200 && orderRes.data && orderRes.data.code === 200) {
						const order = orderRes.data.data;
						
						if (!order || !order.orderId) {
							uni.showToast({
								title: '订单不存在',
								icon: 'none'
							});
							return;
						}
						
						// 检查订单状态
						if (order.status !== '0') {
							uni.showToast({
								title: '订单状态不正确，无法支付',
								icon: 'none'
							});
							return;
						}
						
						// 确认支付
						const confirmRes = await uni.showModal({
							title: '确认支付',
							content: `确认支付 ¥${this.formatPrice(order.totalAmount)} 吗？`,
							confirmText: '确认支付',
							cancelText: '取消'
						});
						
						if (!confirmRes.confirm) {
							return;
						}
						
						// 创建支付宝支付订单
						uni.showLoading({
							title: '正在创建支付订单...'
						});
						
						const paymentRes = await uni.request({
							url: `${baseUrl}/bath/order/miniprogram/alipay/create/${order.orderId}`,
							method: 'POST',
							header: {
								'Content-Type': 'application/json',
								'Authorization': token ? `Bearer ${token}` : ''
							}
						});
						
						uni.hideLoading();
						
						if (paymentRes.statusCode === 200 && paymentRes.data && paymentRes.data.code === 200) {
							const paymentData = paymentRes.data.data;
							
							// 调用支付宝支付（沙盒环境）
							uni.showLoading({
								title: '支付中...'
							});
							
							// 调用支付宝支付（沙盒环境）
							// 支付宝小程序使用 my.requestPayment，其他平台使用 uni.requestPayment
							// #ifdef MP-ALIPAY
							// 支付宝小程序
							my.requestPayment({
								tradeNO: paymentData.tradeNo,
								success: async (res) => {
									uni.hideLoading();
									console.log('支付成功:', res);
									await this.handlePaymentSuccess(baseUrl, token, order.orderId, paymentData.tradeNo, item.appointmentId);
								},
								fail: (err) => {
									uni.hideLoading();
									this.handlePaymentFail(err);
								}
							});
							// #endif
							
							// #ifndef MP-ALIPAY
							// 其他平台：检测平台类型
							// #ifdef MP-WEIXIN
							// 微信小程序：当前后端只支持支付宝支付，使用模拟支付
							console.log('微信小程序：使用模拟支付（沙盒环境）');
							uni.hideLoading();
							
							const confirmRes = await uni.showModal({
								title: '模拟支付',
								content: `沙盒环境：确认支付 ¥${this.formatPrice(order.totalAmount)} 吗？`,
								confirmText: '确认支付',
								cancelText: '取消'
							});
							
							if (confirmRes.confirm) {
								// 直接调用支付成功回调
								await this.handlePaymentSuccess(baseUrl, token, order.orderId, paymentData.tradeNo, item.appointmentId);
							} else {
								uni.showToast({
									title: '已取消支付',
									icon: 'none'
								});
							}
							// #endif
							
							// #ifndef MP-WEIXIN
							// 其他平台（H5、App等）：检查是否支持 uni.requestPayment
							if (typeof uni.requestPayment === 'function') {
								// 尝试使用支付API（可能不支持支付宝）
								try {
									uni.requestPayment({
										provider: 'alipay',
										orderInfo: paymentData.paymentString || JSON.stringify({
											tradeNo: paymentData.tradeNo,
											subject: paymentData.subject,
											totalAmount: paymentData.totalAmount.toString(),
											body: paymentData.body
										}),
										success: async (res) => {
											uni.hideLoading();
											console.log('支付成功:', res);
											await this.handlePaymentSuccess(baseUrl, token, order.orderId, paymentData.tradeNo, item.appointmentId);
										},
										fail: (err) => {
											uni.hideLoading();
											this.handlePaymentFail(err);
										}
									});
								} catch (error) {
									// 如果支付API调用失败，使用模拟支付
									console.log('支付API调用失败，使用模拟支付（沙盒环境）');
									uni.hideLoading();
									
									const confirmRes = await uni.showModal({
										title: '模拟支付',
										content: `沙盒环境：确认支付 ¥${this.formatPrice(order.totalAmount)} 吗？`,
										confirmText: '确认支付',
										cancelText: '取消'
									});
									
									if (confirmRes.confirm) {
										await this.handlePaymentSuccess(baseUrl, token, order.orderId, paymentData.tradeNo, item.appointmentId);
									} else {
										uni.showToast({
											title: '已取消支付',
											icon: 'none'
										});
									}
								}
							} else {
								// H5平台或其他不支持支付API的平台，使用模拟支付
								console.log('当前平台不支持支付API，使用模拟支付（沙盒环境）');
								uni.hideLoading();
								
								const confirmRes = await uni.showModal({
									title: '模拟支付',
									content: `沙盒环境：确认支付 ¥${this.formatPrice(order.totalAmount)} 吗？`,
									confirmText: '确认支付',
									cancelText: '取消'
								});
								
								if (confirmRes.confirm) {
									await this.handlePaymentSuccess(baseUrl, token, order.orderId, paymentData.tradeNo, item.appointmentId);
								} else {
									uni.showToast({
										title: '已取消支付',
										icon: 'none'
									});
								}
							}
							// #endif
							// #endif
						} else {
							throw new Error(paymentRes.data?.msg || '创建支付订单失败');
						}
					} else {
						throw new Error(orderRes.data?.msg || '查询订单失败');
					}
				} catch (error) {
					console.error('支付失败:', error);
					uni.hideLoading();
					uni.showToast({
						title: error.message || '支付失败',
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
	
	.status-tabs {
		display: flex;
		background-color: #ffffff;
		padding: 20rpx;
		margin-top: calc(88rpx + env(safe-area-inset-top, 0px));
		border-bottom: 2rpx solid #f5f5f5;
		position: sticky;
		top: calc(88rpx + env(safe-area-inset-top, 0px));
		z-index: 999;
	}
	
	.tab-item {
		flex: 1;
		text-align: center;
		padding: 10rpx 0;
		font-size: 28rpx;
		color: #666;
		position: relative;
		
		&.active {
			color: #ff6b35;
			font-weight: bold;
			
			&::after {
				content: '';
				position: absolute;
				bottom: 0;
				left: 50%;
				transform: translateX(-50%);
				width: 60rpx;
				height: 4rpx;
				background-color: #ff6b35;
				border-radius: 2rpx;
			}
		}
	}
	
	.appointment-list {
		flex: 1;
		padding: 20rpx;
		padding-bottom: 120rpx; /* 为tabBar留出空间 */
	}
	
	.loading-container,
	.empty-container {
		display: flex;
		justify-content: center;
		align-items: center;
		padding: 100rpx 0;
		color: #999;
		font-size: 28rpx;
	}
	
	.appointment-item {
		background-color: #ffffff;
		border-radius: 20rpx;
		padding: 30rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}
	
	.item-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
		padding-bottom: 20rpx;
		border-bottom: 2rpx solid #f5f5f5;
	}
	
	.appointment-no {
		font-size: 26rpx;
		color: #666;
	}
	
	.status-badge {
		padding: 8rpx 20rpx;
		border-radius: 20rpx;
		font-size: 24rpx;
		
		&.status-pending {
			background-color: #fff3cd;
			color: #856404;
		}
		
		&.status-confirmed {
			background-color: #d1ecf1;
			color: #0c5460;
		}
		
		&.status-in-service {
			background-color: #d4edda;
			color: #155724;
		}
		
		&.status-completed {
			background-color: #d1ecf1;
			color: #0c5460;
		}
		
		&.status-cancelled {
			background-color: #f8d7da;
			color: #721c24;
		}
	}
	
	.item-content {
		margin-bottom: 20rpx;
	}
	
	.info-row {
		display: flex;
		margin-bottom: 15rpx;
		font-size: 28rpx;
		
		&:last-child {
			margin-bottom: 0;
		}
	}
	
	.label {
		color: #999;
		width: 160rpx;
		flex-shrink: 0;
	}
	
	.value {
		color: #333;
		flex: 1;
		
		&.price {
			color: #ff6b35;
			font-weight: bold;
			font-size: 32rpx;
		}
	}
	
	.item-footer {
		padding-top: 20rpx;
		border-top: 2rpx solid #f5f5f5;
		display: flex;
		justify-content: space-between;
		align-items: center;
	}
	
	.create-time {
		font-size: 24rpx;
		color: #999;
	}
	
	.action-buttons {
		display: flex;
		gap: 20rpx;
	}
	
	.pay-btn {
		background: linear-gradient(to right, #ff8c42, #ff6b35);
		color: #ffffff;
		border: none;
		border-radius: 40rpx;
		padding: 12rpx 40rpx;
		font-size: 26rpx;
		font-weight: bold;
	}
	
	.pay-btn::after {
		border: none;
	}
	
	.review-btn {
		background: linear-gradient(to right, #4CAF50, #45a049);
		color: #ffffff;
		border: none;
		border-radius: 40rpx;
		padding: 12rpx 40rpx;
		font-size: 26rpx;
		font-weight: bold;
	}
	
	.review-btn::after {
		border: none;
	}
	
	.reviewed-text {
		color: #999;
		font-size: 26rpx;
		padding: 12rpx 40rpx;
	}
	
	.load-more {
		text-align: center;
		padding: 30rpx 0;
		color: #999;
		font-size: 24rpx;
	}
</style>

