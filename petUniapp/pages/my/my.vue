<template>
<view class="container">
<!-- 顶部导航栏 -->
<view class="header">
<text class="header-title">我的</text>
</view>
<!-- 用户信息 -->
<view class="my-header">
<view class="avatar-container">
<image v-if="userInfo.avatar" :src="userInfo.avatar" class="avatar" mode="aspectFill"></image>
<image v-else src="https://ai-public.mastergo.com/ai/img_res/9cc5efd4fca929355ba21e769663ed23.jpg" class="avatar"></image>
</view>
<view class="user-info">
<text class="username">{{ userInfo.nickName || userInfo.userName || '宠物主人' }}</text>
<text class="member-level">{{ userInfo.userType === '1' ? '商家' : '顾客' }}</text>
</view>
</view>
<!-- 内容区域 -->
<scroll-view class="my-content" scroll-y="true">
<view class="section">
<view class="section-title-bar">
<text class="section-title">账户管理</text>
</view>
<view class="account-list">
<view class="account-item" @click="goToProfile">
<uni-icons type="personadd" size="20" color="#ff6b35"></uni-icons>
<text class="account-text">个人信息</text>
<uni-icons type="arrowright" size="16" color="#ccc"></uni-icons>
</view>
<view class="account-item" @click="goToSettings">
<uni-icons type="gear" size="20" color="#ff6b35"></uni-icons>
<text class="account-text">设置</text>
<uni-icons type="arrowright" size="16" color="#ccc"></uni-icons>
</view>
</view>
</view>
</scroll-view>
</view>
</template>
<script lang="ts" setup>
import { ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';

const userInfo = ref<Record<string, any>>({});

const loadUserInfo = () => {
	const info = uni.getStorageSync('userInfo');
	userInfo.value = info || {};
};

const goToProfile = () => {
	if (!uni.getStorageSync('userId')) {
		uni.showToast({ title: '请先登录', icon: 'none' });
		setTimeout(() => {
			uni.navigateTo({ url: '/pages/login/login' });
		}, 1500);
		return;
	}
	uni.navigateTo({ url: '/pages/profile/profile' });
};

const goToSettings = () => {
	if (!uni.getStorageSync('userId')) {
		uni.showToast({ title: '请先登录', icon: 'none' });
		setTimeout(() => {
			uni.navigateTo({ url: '/pages/login/login' });
		}, 1500);
		return;
	}
	uni.navigateTo({ url: '/pages/settings/settings' });
};

onShow(() => {
	loadUserInfo();
});
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
.my-header {
background: linear-gradient(to right, #ff8c42, #ff6b35);
border-radius: 20rpx;
padding: 40rpx 30rpx;
margin: calc(108rpx + env(safe-area-inset-top, 0px)) 20rpx 30rpx;
display: flex;
align-items: center;
color: #ffffff;
}
.avatar-container {
width: 120rpx;
height: 120rpx;
border-radius: 50%;
overflow: hidden;
margin-right: 20rpx;
border: 4rpx solid rgba(255, 255, 255, 0.3);
}
.avatar {
width: 100%;
height: 100%;
}
.user-info {
flex: 1;
}
.username {
font-size: 36rpx;
font-weight: bold;
display: block;
margin-bottom: 10rpx;
}
.member-level {
font-size: 28rpx;
opacity: 0.9;
display: block;
}
.my-content {
flex: 1;
overflow: auto;
padding: 0 20rpx 20rpx;
padding-bottom: 120rpx;
box-sizing: border-box;
}
.section {
margin-bottom: 30rpx;
}
.section-title-bar {
padding: 20rpx 0;
border-bottom: 2rpx solid #f0e6d2;
margin-bottom: 20rpx;
}
.section-title {
font-size: 32rpx;
color: #a0522d;
font-weight: bold;
padding-left: 10rpx;
border-left: 4rpx solid #ff6b35;
}
.account-list {
background-color: #fffaf0;
border-radius: 20rpx;
overflow: hidden;
}
.account-item {
display: flex;
align-items: center;
padding: 25rpx;
background-color: #ffffff;
border-bottom: 1rpx solid #f5f5f5;
}
.account-item:last-child {
border-bottom: none;
}
.account-text {
flex: 1;
font-size: 28rpx;
color: #663300;
margin: 0 20rpx;
}
</style>
