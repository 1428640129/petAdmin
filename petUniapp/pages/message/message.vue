<template>
<view class="container">
<!-- 顶部导航栏 -->
<view class="header">
<text class="header-title">消息</text>
</view>
<!-- 消息列表 -->
<scroll-view 
  class="message-list" 
  scroll-y="true"
  refresher-enabled
  @refresherrefresh="onPullDownRefresh"
>
<view v-if="loading && messages.length === 0" class="loading">
  <text>加载中...</text>
</view>
<view v-else-if="messages.length === 0" class="empty">
  <text>暂无消息</text>
</view>
<view
  v-else
  v-for="msg in messages"
  :key="msg.messageId"
  class="message-item"
  @click="viewMessageDetail(msg)"
>
<view class="message-avatar">
<uni-icons
:type="getOtherUserInfo(msg).type === '1' ? 'shop' : 'staff'"
size="30"
color="#ff6b35"
></uni-icons>
</view>
<view class="message-content">
<view class="message-header-info">
<text class="sender">{{ getOtherUserInfo(msg).name }}</text>
<text class="time">{{ formatTime(msg.createTime) }}</text>
</view>
<text class="content">{{ getMessagePreview(msg) }}</text>
<view v-if="msg.isRead === '0' && msg.receiverId === currentUserId && msg.receiverType === currentUserType" class="unread-dot"></view>
</view>
</view>
</scroll-view>
<!-- 发送消息按钮 -->
<view class="send-message-section">
<button class="send-message-btn" @click="sendMessage">发送消息</button>
</view>
<!-- 底部导航栏 -->
<view class="tab-bar">
<view class="tab-item" @click="switchTab('index')">
<image class="tab-icon" src="/static/tabbar/home.png" mode="aspectFit"></image>
<text class="tab-text">首页</text>
</view>
<view class="tab-item" @click="switchTab('my')">
<image class="tab-icon" src="/static/tabbar/my.png" mode="aspectFit"></image>
<text class="tab-text">我的</text>
</view>
</view>
</view>
</template>
<script lang="ts" setup>
import { ref, onMounted } from 'vue';

// 当前用户信息（从缓存或登录信息获取）
const currentUserId = ref(1); // 示例：实际应从登录信息获取
const currentUserType = ref('0'); // 0=用户, 1=商家

// 消息列表数据
const messages = ref<any[]>([]);
const loading = ref(false);

// 格式化时间
const formatTime = (timeStr: string) => {
  if (!timeStr) return '';
  const date = new Date(timeStr);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  
  if (days === 0) {
    const hours = date.getHours();
    const minutes = date.getMinutes();
    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}`;
  } else if (days === 1) {
    return '昨天';
  } else if (days < 7) {
    return `${days}天前`;
  } else {
    return `${date.getMonth() + 1}-${date.getDate()}`;
  }
};

// 获取消息预览内容
const getMessagePreview = (msg: any) => {
  if (msg.messageType === '0') {
    return msg.content || '';
  } else if (msg.messageType === '1') {
    return '[图片]';
  } else if (msg.messageType === '2') {
    return '[视频]';
  }
  return '';
};

// 获取对方用户信息
const getOtherUserInfo = (msg: any) => {
  if (msg.senderId === currentUserId.value && msg.senderType === currentUserType.value) {
    // 我是发送者，显示接收者信息
    return {
      id: msg.receiverId,
      type: msg.receiverType,
      name: msg.receiverType === '1' ? '商家' : '用户'
    };
  } else {
    // 我是接收者，显示发送者信息
    return {
      id: msg.senderId,
      type: msg.senderType,
      name: msg.senderType === '1' ? '商家' : '用户'
    };
  }
};

// 加载聊天列表
const loadChatList = async () => {
  try {
    loading.value = true;
    // 调用后端API获取聊天列表
    const res = await uni.request({
      url: `${getAppInstance().globalData.baseUrl}/bath/message/chatList`,
      method: 'GET',
      data: {
        userId: currentUserId.value,
        userType: currentUserType.value
      },
      header: {
        'Authorization': `Bearer ${uni.getStorageSync('token')}`
      }
    });
    
    if (res.statusCode === 200 && res.data.code === 200) {
      messages.value = res.data.data || [];
    }
  } catch (error) {
    console.error('加载聊天列表失败:', error);
    uni.showToast({
      title: '加载失败',
      icon: 'none'
    });
  } finally {
    loading.value = false;
  }
};

// 查看消息详情（进入聊天页面）
const viewMessageDetail = (msg: any) => {
  const otherUser = getOtherUserInfo(msg);
  uni.navigateTo({
    url: `/pages/chat/chat?senderId=${otherUser.id}&senderType=${otherUser.type}&receiverId=${currentUserId.value}&receiverType=${currentUserType.value}`
  });
};

// 发送消息（跳转到选择接收者页面）
const sendMessage = () => {
  uni.navigateTo({
    url: '/pages/sendMessage/sendMessage'
  });
};

// 页面加载时获取聊天列表
onMounted(() => {
  loadChatList();
});

// 下拉刷新
const onPullDownRefresh = () => {
  loadChatList().finally(() => {
    uni.stopPullDownRefresh();
  });
};
// 切换标签页
const switchTab = (page: string) => {
if (page === 'index') {
uni.switchTab({
url: '/pages/index/index'
});
} else if (page === 'my') {
uni.switchTab({
url: '/pages/my/my'
});
}
};

// 获取应用实例
const getAppInstance = () => {
  return getApp();
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
.message-list {
flex: 1;
overflow: auto;
padding: 20rpx;
padding-top: calc(108rpx + env(safe-area-inset-top, 0px));
padding-bottom: 220rpx;
box-sizing: border-box;
}
.message-item {
display: flex;
padding: 20rpx;
background-color: #fffaf0;
border-radius: 20rpx;
margin-bottom: 20rpx;
position: relative;
}
.message-avatar {
width: 80rpx;
height: 80rpx;
border-radius: 50%;
background-color: #ffffff;
display: flex;
align-items: center;
justify-content: center;
margin-right: 20rpx;
border: 2rpx solid #ff6b35;
}
.message-content {
flex: 1;
position: relative;
}
.message-header-info {
display: flex;
justify-content: space-between;
align-items: center;
margin-bottom: 10rpx;
}
.sender {
font-size: 32rpx;
color: #ff6b35;
font-weight: bold;
}
.time {
font-size: 24rpx;
color: #999;
}
.content {
font-size: 28rpx;
color: #663300;
line-height: 36rpx;
}
.unread-dot {
position: absolute;
top: 0;
right: 0;
width: 16rpx;
height: 16rpx;
background-color: #ff0000;
border-radius: 50%;
}
.send-message-section {
width: 100%;
padding: 20rpx;
box-sizing: border-box;
background-color: #fffdf8;
border-top: 2rpx solid #f5f5f5;
position: fixed;
bottom: 100rpx;
left: 0;
z-index: 998;
}
.send-message-btn {
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
}
.tab-bar {
width: 100%;
height: 100rpx;
background-color: #fffaf0;
display: flex;
justify-content: space-around;
align-items: center;
border-top: 2rpx solid #f5f5f5;
position: fixed;
bottom: 0;
left: 0;
z-index: 1000;
}
.tab-item {
display: flex;
flex-direction: column;
align-items: center;
justify-content: center;
padding: 10rpx 0;
flex: 1;
}
.tab-item.active .tab-text {
color: #ff6b35;
}
.tab-icon {
width: 48rpx;
height: 48rpx;
margin-bottom: 6rpx;
}
.tab-item.active .tab-icon {
opacity: 1;
}
.tab-item:not(.active) .tab-icon {
opacity: 0.5;
}
.tab-text {
font-size: 24rpx;
color: #999;
}
.loading, .empty {
display: flex;
justify-content: center;
align-items: center;
height: 400rpx;
color: #999;
font-size: 28rpx;
}
</style>

