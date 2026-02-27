<template>
  <view class="container">
    <!-- 顶部导航栏 -->
    <view class="header">
      <view class="header-left" @click="goBack">
        <uni-icons type="left" size="24" color="#ffffff"></uni-icons>
      </view>
      <text class="header-title">{{ otherUserName }}</text>
    </view>
    
    <!-- 聊天消息列表 -->
    <scroll-view 
      class="chat-list" 
      scroll-y="true"
      :scroll-top="scrollTop"
      scroll-with-animation
      @scrolltolower="loadMore"
    >
      <view v-for="msg in messages" :key="msg.messageId" class="message-wrapper">
        <view 
          class="message-bubble" 
          :class="msg.senderId === currentUserId && msg.senderType === currentUserType ? 'message-right' : 'message-left'"
        >
          <!-- 文字消息 -->
          <view v-if="msg.messageType === '0'" class="text-message">
            {{ msg.content }}
          </view>
          
          <!-- 图片消息 -->
          <view v-else-if="msg.messageType === '1'" class="image-message">
            <image 
              :src="msg.fileUrl" 
              mode="aspectFill"
              @click="previewImage(msg.fileUrl)"
            ></image>
          </view>
          
          <!-- 视频消息 -->
          <view v-else-if="msg.messageType === '2'" class="video-message">
            <image 
              :src="msg.thumbnailUrl || msg.fileUrl" 
              mode="aspectFill"
              class="video-thumbnail"
              @click="playVideo(msg.fileUrl)"
            ></image>
            <view class="video-play-icon">
              <uni-icons type="play-filled" size="40" color="#ffffff"></uni-icons>
            </view>
            <view v-if="msg.fileDuration" class="video-duration">
              {{ formatDuration(msg.fileDuration) }}
            </view>
          </view>
          
          <view class="message-time">{{ formatTime(msg.createTime) }}</view>
        </view>
      </view>
    </scroll-view>
    
    <!-- 底部输入栏 -->
    <view class="input-bar">
      <view class="input-left">
        <view class="icon-btn" @click="chooseImage">
          <uni-icons type="image" size="24" color="#ff6b35"></uni-icons>
        </view>
        <view class="icon-btn" @click="chooseVideo">
          <uni-icons type="videocam" size="24" color="#ff6b35"></uni-icons>
        </view>
      </view>
      <input 
        class="input-field" 
        v-model="inputText" 
        placeholder="输入消息..."
        @confirm="sendTextMessage"
      />
      <view class="send-btn" @click="sendTextMessage">
        <text>发送</text>
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
import { ref, onMounted, nextTick } from 'vue';

// 页面参数
const pages = getCurrentPages();
const currentPage = pages[pages.length - 1];
const options = currentPage.options || {};
const senderId = ref(parseInt(options.senderId) || 1);
const senderType = ref(options.senderType || '1');
const receiverId = ref(parseInt(options.receiverId) || 1);
const receiverType = ref(options.receiverType || '0');

// 当前用户信息
const currentUserId = ref(receiverId.value);
const currentUserType = ref(receiverType.value);
const otherUserName = ref(senderType.value === '1' ? '商家' : '用户');

// 消息列表
const messages = ref<any[]>([]);
const inputText = ref('');
const scrollTop = ref(0);
const loading = ref(false);
const hasMore = ref(true);

// 格式化时间
const formatTime = (timeStr: string) => {
  if (!timeStr) return '';
  const date = new Date(timeStr);
  const hours = date.getHours();
  const minutes = date.getMinutes();
  return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}`;
};

// 格式化视频时长
const formatDuration = (seconds: number) => {
  const mins = Math.floor(seconds / 60);
  const secs = seconds % 60;
  return `${mins}:${secs.toString().padStart(2, '0')}`;
};

// 加载聊天记录
const loadChatHistory = async () => {
  try {
    loading.value = true;
    const res = await uni.request({
      url: `${getAppInstance().globalData.baseUrl}/bath/message/chatHistory`,
      method: 'GET',
      data: {
        senderId: senderId.value,
        senderType: senderType.value,
        receiverId: receiverId.value,
        receiverType: receiverType.value
      },
      header: {
        'Authorization': `Bearer ${uni.getStorageSync('token')}`
      }
    });
    
    if (res.statusCode === 200 && res.data.code === 200) {
      messages.value = res.data.data || [];
      // 滚动到底部
      nextTick(() => {
        scrollToBottom();
      });
      // 标记为已读
      markChatAsRead();
    }
  } catch (error) {
    console.error('加载聊天记录失败:', error);
  } finally {
    loading.value = false;
  }
};

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    const query = uni.createSelectorQuery();
    query.select('.chat-list').boundingClientRect((rect: any) => {
      scrollTop.value = rect.scrollHeight;
    }).exec();
  });
};

// 标记聊天为已读
const markChatAsRead = async () => {
  try {
    await uni.request({
      url: `${getAppInstance().globalData.baseUrl}/bath/message/markChatRead`,
      method: 'POST',
      data: {
        senderId: senderId.value,
        senderType: senderType.value,
        receiverId: receiverId.value,
        receiverType: receiverType.value
      },
      header: {
        'Authorization': `Bearer ${uni.getStorageSync('token')}`
      }
    });
  } catch (error) {
    console.error('标记已读失败:', error);
  }
};

// 发送文字消息
const sendTextMessage = async () => {
  if (!inputText.value.trim()) return;
  
  const content = inputText.value.trim();
  inputText.value = '';
  
  try {
    const res = await uni.request({
      url: `${getAppInstance().globalData.baseUrl}/bath/message`,
      method: 'POST',
      data: {
        senderId: receiverId.value,
        senderType: receiverType.value,
        receiverId: senderId.value,
        receiverType: senderType.value,
        messageType: '0',
        content: content
      },
      header: {
        'Authorization': `Bearer ${uni.getStorageSync('token')}`,
        'Content-Type': 'application/json'
      }
    });
    
    if (res.statusCode === 200 && res.data.code === 200) {
      // 重新加载聊天记录
      loadChatHistory();
    }
  } catch (error) {
    console.error('发送消息失败:', error);
    uni.showToast({
      title: '发送失败',
      icon: 'none'
    });
  }
};

// 选择图片
const chooseImage = () => {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async (res) => {
      const tempFilePath = res.tempFilePaths[0];
      await uploadAndSendImage(tempFilePath);
    }
  });
};

// 上传并发送图片
const uploadAndSendImage = async (filePath: string) => {
  try {
    uni.showLoading({ title: '上传中...' });
    
    // 上传文件
    const uploadRes = await uni.uploadFile({
      url: `${getAppInstance().globalData.baseUrl}/common/upload`,
      filePath: filePath,
      name: 'file',
      header: {
        'Authorization': `Bearer ${uni.getStorageSync('token')}`
      }
    });
    
    uni.hideLoading();
    
    if (uploadRes.statusCode === 200) {
      const result = JSON.parse(uploadRes.data);
      if (result.code === 200) {
        const fileUrl = result.data.url;
        
        // 发送消息
        const res = await uni.request({
          url: `${getAppInstance().globalData.baseUrl}/bath/message`,
          method: 'POST',
          data: {
            senderId: receiverId.value,
            senderType: receiverType.value,
            receiverId: senderId.value,
            receiverType: senderType.value,
            messageType: '1',
            content: '[图片]',
            fileUrl: fileUrl
          },
          header: {
            'Authorization': `Bearer ${uni.getStorageSync('token')}`,
            'Content-Type': 'application/json'
          }
        });
        
        if (res.statusCode === 200 && res.data.code === 200) {
          loadChatHistory();
        }
      }
    }
  } catch (error) {
    uni.hideLoading();
    console.error('上传图片失败:', error);
    uni.showToast({
      title: '上传失败',
      icon: 'none'
    });
  }
};

// 选择视频
const chooseVideo = () => {
  uni.chooseVideo({
    sourceType: ['album', 'camera'],
    maxDuration: 60,
    camera: 'back',
    success: async (res) => {
      await uploadAndSendVideo(res.tempFilePath, res.duration);
    }
  });
};

// 上传并发送视频
const uploadAndSendVideo = async (filePath: string, duration: number) => {
  try {
    uni.showLoading({ title: '上传中...' });
    
    // 上传文件
    const uploadRes = await uni.uploadFile({
      url: `${getAppInstance().globalData.baseUrl}/common/upload`,
      filePath: filePath,
      name: 'file',
      header: {
        'Authorization': `Bearer ${uni.getStorageSync('token')}`
      }
    });
    
    uni.hideLoading();
    
    if (uploadRes.statusCode === 200) {
      const result = JSON.parse(uploadRes.data);
      if (result.code === 200) {
        const fileUrl = result.data.url;
        
        // 发送消息
        const res = await uni.request({
          url: `${getAppInstance().globalData.baseUrl}/bath/message`,
          method: 'POST',
          data: {
            senderId: receiverId.value,
            senderType: receiverType.value,
            receiverId: senderId.value,
            receiverType: senderType.value,
            messageType: '2',
            content: '[视频]',
            fileUrl: fileUrl,
            fileDuration: duration
          },
          header: {
            'Authorization': `Bearer ${uni.getStorageSync('token')}`,
            'Content-Type': 'application/json'
          }
        });
        
        if (res.statusCode === 200 && res.data.code === 200) {
          loadChatHistory();
        }
      }
    }
  } catch (error) {
    uni.hideLoading();
    console.error('上传视频失败:', error);
    uni.showToast({
      title: '上传失败',
      icon: 'none'
    });
  }
};

// 预览图片
const previewImage = (url: string) => {
  uni.previewImage({
    urls: [url],
    current: url
  });
};

// 播放视频
const playVideo = (url: string) => {
  // 使用video组件播放视频
  // 这里可以打开一个全屏视频播放器
  uni.showToast({
    title: '播放视频',
    icon: 'none'
  });
};

// 加载更多
const loadMore = () => {
  // 可以实现分页加载
};

// 返回
const goBack = () => {
  uni.navigateBack();
};

// 获取应用实例
const getAppInstance = () => {
  return getApp();
};

// 页面加载时获取聊天记录
onMounted(() => {
  loadChatHistory();
});
</script>

<style>
page {
  height: 100%;
  background-color: #f5f5f5;
}

.container {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
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
  position: relative;
  box-sizing: border-box;
}

.header-left {
  position: absolute;
  left: 20rpx;
  padding: 10rpx;
}

.header-title {
  font-size: 36rpx;
  color: #ffffff;
  font-weight: bold;
}

.chat-list {
  flex: 1;
  padding: 20rpx;
  box-sizing: border-box;
}

.message-wrapper {
  margin-bottom: 20rpx;
}

.message-bubble {
  max-width: 60%;
  padding: 20rpx;
  border-radius: 20rpx;
  position: relative;
}

.message-left {
  background-color: #ffffff;
  align-self: flex-start;
}

.message-right {
  background-color: #95ec69;
  align-self: flex-end;
  margin-left: auto;
}

.text-message {
  font-size: 28rpx;
  color: #333;
  line-height: 40rpx;
  word-break: break-word;
}

.image-message image {
  width: 400rpx;
  height: 400rpx;
  border-radius: 10rpx;
}

.video-message {
  position: relative;
  width: 400rpx;
  height: 300rpx;
}

.video-thumbnail {
  width: 100%;
  height: 100%;
  border-radius: 10rpx;
}

.video-play-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80rpx;
  height: 80rpx;
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.video-duration {
  position: absolute;
  bottom: 10rpx;
  right: 10rpx;
  background-color: rgba(0, 0, 0, 0.6);
  color: #ffffff;
  font-size: 24rpx;
  padding: 4rpx 8rpx;
  border-radius: 4rpx;
}

.message-time {
  font-size: 20rpx;
  color: #999;
  margin-top: 10rpx;
  text-align: right;
}

.input-bar {
  width: 100%;
  padding: 20rpx;
  background-color: #ffffff;
  border-top: 2rpx solid #f0f0f0;
  display: flex;
  align-items: center;
  box-sizing: border-box;
}

.input-left {
  display: flex;
  gap: 20rpx;
  margin-right: 20rpx;
}

.icon-btn {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.input-field {
  flex: 1;
  height: 60rpx;
  padding: 0 20rpx;
  background-color: #f5f5f5;
  border-radius: 30rpx;
  font-size: 28rpx;
}

.send-btn {
  margin-left: 20rpx;
  padding: 0 30rpx;
  height: 60rpx;
  background: linear-gradient(to right, #ff8c42, #ff6b35);
  border-radius: 30rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  font-size: 28rpx;
}
</style>

