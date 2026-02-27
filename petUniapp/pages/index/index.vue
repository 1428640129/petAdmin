<template>
<view class="container">
<!-- 顶部导航栏 -->
<view class="header">
<text class="header-title">宠物洗澡预约</text>
</view>
<!-- 主内容区域 -->
<scroll-view class="main-content" scroll-y="true">
<!-- 轮播图区域 -->
<view class="carousel-container" v-if="carouselList.length > 0">
<swiper class="carousel-swiper" :indicator-dots="true" indicator-color="#f5f5f5" indicator-active-color="#ff6b35" :autoplay="true" :interval="3000" :duration="500">
<swiper-item v-for="(item, index) in carouselList" :key="index">
<view class="carousel-item">
<image :src="item.image" mode="aspectFill" class="carousel-image"></image>
<view class="carousel-text">{{ item.title }}</view>
</view>
</swiper-item>
</swiper>
</view>
<view class="carousel-container" v-else>
<view class="loading-text">加载中...</view>
</view>
<!-- 服务介绍卡片 -->
<view class="service-intro-card">
<view class="intro-header">
<text class="intro-title">专业宠物洗澡服务</text>
<text class="intro-subtitle">让您的爱宠享受五星级护理</text>
</view>
<view class="intro-content">
<text class="intro-desc">我们拥有专业的宠物美容师团队，使用天然温和的洗护用品，为您的爱宠提供安全、舒适的洗澡体验。所有工具均经过严格消毒，确保宠物健康。</text>
</view>
</view>
<!-- 服务项目区域 -->
<view class="services-section">
<text class="section-title">精选服务</text>
			<view class="services-grid" v-if="services.length > 0">
			<view 
				v-for="(service, index) in services" 
				:key="index" 
				class="service-item"
				@click="goToServicePage(service)"
			>
			<image :src="service.image" mode="aspectFill" class="service-image"></image>
			<text class="service-name">{{ service.name }}</text>
			<text class="service-price">¥{{ service.price }}</text>
			</view>
			</view>
<view v-else class="loading-text">加载中...</view>
</view>
<!-- 店铺信息 -->
<view class="shop-info">
<view class="info-row">
<uni-icons type="location" size="16" color="#ff6b35"></uni-icons>
<text class="info-text">北京市朝阳区幸福大街123号</text>
</view>
<view class="info-row">
<uni-icons type="phone" size="16" color="#ff6b35"></uni-icons>
<text class="info-text">400-123-4567</text>
</view>
<view class="info-row">
<uni-icons type="clock" size="16" color="#ff6b35"></uni-icons>
<text class="info-text">营业时间：9:00-18:00</text>
</view>
</view>
</scroll-view>
<!-- 底部预约按钮 -->
<view class="bottom-action">
<button class="appointment-btn" @click="makeAppointment">立即预约</button>
</view>
</view>
</template>
<script lang="ts" setup>
import { ref, onMounted } from 'vue';

// 轮播图数据
const carouselList = ref<any[]>([]);
// 服务项目数据
const services = ref<any[]>([]);
// 加载状态
const loading = ref(false);

// 获取App实例
const getAppInstance = () => {
	return getApp();
};

// 获取轮播图数据
const loadCarouselList = async () => {
	try {
		const app = getAppInstance();
		const baseUrl = app?.globalData?.baseUrl || 'http://localhost:8080';
		const res = await uni.request({
			url: `${baseUrl}/bath/carousel/enabled`,
			method: 'GET',
			header: {
				'Content-Type': 'application/json'
			}
		});
		
		if (res.statusCode === 200 && res.data.code === 200) {
			carouselList.value = (res.data.data || []).map((item: any) => ({
				title: item.title || '',
				image: item.imageUrl || ''
			}));
		} else {
			console.error('获取轮播图失败:', res.data.msg);
			// 如果接口失败，使用默认数据
			carouselList.value = [
				{
					title: '专业宠物洗澡',
					image: 'https://ai-public.mastergo.com/ai/img_res/13b5469fd6d2406f55b473faf54a6268.jpg'
				}
			];
		}
	} catch (error) {
		console.error('获取轮播图异常:', error);
		// 如果接口失败，使用默认数据
		carouselList.value = [
			{
				title: '专业宠物洗澡',
				image: 'https://ai-public.mastergo.com/ai/img_res/13b5469fd6d2406f55b473faf54a6268.jpg'
			}
		];
	}
};

// 获取服务列表数据
const loadServiceList = async () => {
	try {
		const app = getAppInstance();
		const baseUrl = app?.globalData?.baseUrl || 'http://localhost:8080';
		const res = await uni.request({
			url: `${baseUrl}/bath/service/enabled`,
			method: 'GET',
			header: {
				'Content-Type': 'application/json'
			}
		});
		
		if (res.statusCode === 200 && res.data.code === 200) {
			const serviceList = res.data.data || [];
			services.value = serviceList.map((item: any) => {
				// 解析服务图片（可能是JSON字符串）
				let imageUrl = '';
				if (item.serviceImages) {
					try {
						const images = JSON.parse(item.serviceImages);
						imageUrl = Array.isArray(images) && images.length > 0 ? images[0] : '';
					} catch (e) {
						imageUrl = item.serviceImages;
					}
				}
				
				// 获取最低价格（如果有价格梯度）
				let price = '0';
				if (item.prices && item.prices.length > 0) {
					const prices = item.prices.map((p: any) => parseFloat(p.price || 0));
					price = Math.min(...prices).toString();
				}
				
				return {
					id: item.serviceId,
					name: item.serviceName || '',
					price: price,
					image: imageUrl || 'https://ai-public.mastergo.com/ai/img_res/3a21b5f82e4f86baf34988ced88a8d66.jpg'
				};
			});
		} else {
			console.error('获取服务列表失败:', res.data.msg);
			// 如果接口失败，使用默认数据
			services.value = [
				{
					name: '基础洗澡',
					price: '68',
					image: 'https://ai-public.mastergo.com/ai/img_res/3a21b5f82e4f86baf34988ced88a8d66.jpg'
				}
			];
		}
	} catch (error) {
		console.error('获取服务列表异常:', error);
		// 如果接口失败，使用默认数据
		services.value = [
			{
				name: '基础洗澡',
				price: '68',
				image: 'https://ai-public.mastergo.com/ai/img_res/3a21b5f82e4f86baf34988ced88a8d66.jpg'
			}
		];
	}
};

// 页面加载时获取数据
onMounted(() => {
	loading.value = true;
	Promise.all([loadCarouselList(), loadServiceList()]).finally(() => {
		loading.value = false;
	});
});

// 立即预约事件
const makeAppointment = () => {
	uni.navigateTo({
		url: '/pages/service/service'
	});
};

// 跳转到服务预约页面
const goToServicePage = (service: any) => {
	uni.navigateTo({
		url: `/pages/service/service?serviceId=${service.id}`
	});
};

</script>
<style>
page {
height: 100%;
background-color: #fffdf8;
}
.container {
height: 100%;
background-color: #fffdf8;
display: flex;
flex-direction: column;
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
.main-content {
flex: 1;
overflow: auto;
padding: 20rpx;
padding-top: calc(108rpx + env(safe-area-inset-top, 0px));
padding-bottom: 240rpx;
box-sizing: border-box;
}
.carousel-container {
width: 100%;
height: 300rpx;
border-radius: 20rpx;
overflow: hidden;
margin-bottom: 30rpx;
box-shadow: 0 8rpx 20rpx rgba(255, 107, 53, 0.2);
}
.carousel-swiper {
width: 100%;
height: 100%;
}
.carousel-item {
width: 100%;
height: 100%;
position: relative;
}
.carousel-image {
width: 100%;
height: 100%;
}
.carousel-text {
position: absolute;
bottom: 20rpx;
left: 20rpx;
color: #ffffff;
font-size: 32rpx;
font-weight: bold;
text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.5);
}
.service-intro-card {
background-color: #fffaf0;
border-radius: 20rpx;
padding: 30rpx;
margin-bottom: 30rpx;
border: 2rpx solid #d2b48c;
box-shadow: 0 4rpx 12rpx rgba(210, 180, 140, 0.2);
}
.intro-header {
text-align: center;
margin-bottom: 20rpx;
}
.intro-title {
font-size: 36rpx;
color: #ff6b35;
font-weight: bold;
display: block;
}
.intro-subtitle {
font-size: 28rpx;
color: #a0522d;
display: block;
margin-top: 10rpx;
}
.intro-content {
text-align: center;
}
.intro-desc {
font-size: 28rpx;
color: #663300;
line-height: 40rpx;
}
.services-section {
margin-bottom: 30rpx;
}
.section-title {
font-size: 36rpx;
color: #ff6b35;
font-weight: bold;
display: block;
margin-bottom: 20rpx;
}
.services-grid {
display: grid;
grid-template-columns: repeat(2, 1fr);
gap: 20rpx;
}
.service-item {
background-color: #fffaf0;
border-radius: 20rpx;
padding: 20rpx;
text-align: center;
box-shadow: 0 4rpx 12rpx rgba(210, 180, 140, 0.2);
}
.service-image {
width: 160rpx;
height: 160rpx;
border-radius: 10rpx;
margin: 0 auto 15rpx;
}
.service-name {
font-size: 28rpx;
color: #663300;
display: block;
margin-bottom: 10rpx;
}
.service-price {
font-size: 32rpx;
color: #ff6b35;
font-weight: bold;
display: block;
}
.shop-info {
background-color: #fffaf0;
border-radius: 20rpx;
padding: 30rpx;
border: 2rpx solid #d2b48c;
box-shadow: 0 4rpx 12rpx rgba(210, 180, 140, 0.2);
}
.info-row {
display: flex;
align-items: center;
margin-bottom: 20rpx;
}
.info-row:last-child {
margin-bottom: 0;
}
.info-text {
font-size: 28rpx;
color: #663300;
margin-left: 15rpx;
}
.bottom-action {
width: 100%;
padding: 20rpx;
box-sizing: border-box;
background-color: #fffdf8;
border-top: 2rpx solid #f5f5f5;
position: fixed;
bottom: 100rpx;
left: 0;
z-index: 999;
box-shadow: 0 -4rpx 12rpx rgba(0, 0, 0, 0.1);
}
.appointment-btn {
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
}
.loading-text {
text-align: center;
padding: 40rpx;
color: #999;
font-size: 28rpx;
}
</style>
