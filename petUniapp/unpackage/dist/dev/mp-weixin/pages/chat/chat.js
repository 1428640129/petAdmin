"use strict";
const common_vendor = require("../../common/vendor.js");
if (!Array) {
  const _component_uni_icons = common_vendor.resolveComponent("uni-icons");
  _component_uni_icons();
}
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "chat",
  setup(__props) {
    const pages = getCurrentPages();
    const currentPage = pages[pages.length - 1];
    const options = currentPage.options || {};
    const senderId = common_vendor.ref(parseInt(options.senderId) || 1);
    const senderType = common_vendor.ref(options.senderType || "1");
    const receiverId = common_vendor.ref(parseInt(options.receiverId) || 1);
    const receiverType = common_vendor.ref(options.receiverType || "0");
    const currentUserId = common_vendor.ref(receiverId.value);
    const currentUserType = common_vendor.ref(receiverType.value);
    const otherUserName = common_vendor.ref(senderType.value === "1" ? "商家" : "用户");
    const messages = common_vendor.ref([]);
    const inputText = common_vendor.ref("");
    const scrollTop = common_vendor.ref(0);
    const loading = common_vendor.ref(false);
    common_vendor.ref(true);
    const formatTime = (timeStr) => {
      if (!timeStr)
        return "";
      const date = new Date(timeStr);
      const hours = date.getHours();
      const minutes = date.getMinutes();
      return `${hours.toString().padStart(2, "0")}:${minutes.toString().padStart(2, "0")}`;
    };
    const formatDuration = (seconds) => {
      const mins = Math.floor(seconds / 60);
      const secs = seconds % 60;
      return `${mins}:${secs.toString().padStart(2, "0")}`;
    };
    const loadChatHistory = async () => {
      try {
        loading.value = true;
        const res = await common_vendor.index.request({
          url: `${getAppInstance().globalData.baseUrl}/bath/message/chatHistory`,
          method: "GET",
          data: {
            senderId: senderId.value,
            senderType: senderType.value,
            receiverId: receiverId.value,
            receiverType: receiverType.value
          },
          header: {
            "Authorization": `Bearer ${common_vendor.index.getStorageSync("token")}`
          }
        });
        if (res.statusCode === 200 && res.data.code === 200) {
          messages.value = res.data.data || [];
          common_vendor.nextTick$1(() => {
            scrollToBottom();
          });
          markChatAsRead();
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/chat/chat.vue:150", "加载聊天记录失败:", error);
      } finally {
        loading.value = false;
      }
    };
    const scrollToBottom = () => {
      common_vendor.nextTick$1(() => {
        const query = common_vendor.index.createSelectorQuery();
        query.select(".chat-list").boundingClientRect((rect) => {
          scrollTop.value = rect.scrollHeight;
        }).exec();
      });
    };
    const markChatAsRead = async () => {
      try {
        await common_vendor.index.request({
          url: `${getAppInstance().globalData.baseUrl}/bath/message/markChatRead`,
          method: "POST",
          data: {
            senderId: senderId.value,
            senderType: senderType.value,
            receiverId: receiverId.value,
            receiverType: receiverType.value
          },
          header: {
            "Authorization": `Bearer ${common_vendor.index.getStorageSync("token")}`
          }
        });
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/chat/chat.vue:183", "标记已读失败:", error);
      }
    };
    const sendTextMessage = async () => {
      if (!inputText.value.trim())
        return;
      const content = inputText.value.trim();
      inputText.value = "";
      try {
        const res = await common_vendor.index.request({
          url: `${getAppInstance().globalData.baseUrl}/bath/message`,
          method: "POST",
          data: {
            senderId: receiverId.value,
            senderType: receiverType.value,
            receiverId: senderId.value,
            receiverType: senderType.value,
            messageType: "0",
            content
          },
          header: {
            "Authorization": `Bearer ${common_vendor.index.getStorageSync("token")}`,
            "Content-Type": "application/json"
          }
        });
        if (res.statusCode === 200 && res.data.code === 200) {
          loadChatHistory();
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/chat/chat.vue:217", "发送消息失败:", error);
        common_vendor.index.showToast({
          title: "发送失败",
          icon: "none"
        });
      }
    };
    const chooseImage = () => {
      common_vendor.index.chooseImage({
        count: 1,
        sizeType: ["compressed"],
        sourceType: ["album", "camera"],
        success: async (res) => {
          const tempFilePath = res.tempFilePaths[0];
          await uploadAndSendImage(tempFilePath);
        }
      });
    };
    const uploadAndSendImage = async (filePath) => {
      try {
        common_vendor.index.showLoading({ title: "上传中..." });
        const uploadRes = await common_vendor.index.uploadFile({
          url: `${getAppInstance().globalData.baseUrl}/common/upload`,
          filePath,
          name: "file",
          header: {
            "Authorization": `Bearer ${common_vendor.index.getStorageSync("token")}`
          }
        });
        common_vendor.index.hideLoading();
        if (uploadRes.statusCode === 200) {
          const result = JSON.parse(uploadRes.data);
          if (result.code === 200) {
            const fileUrl = result.data.url;
            const res = await common_vendor.index.request({
              url: `${getAppInstance().globalData.baseUrl}/bath/message`,
              method: "POST",
              data: {
                senderId: receiverId.value,
                senderType: receiverType.value,
                receiverId: senderId.value,
                receiverType: senderType.value,
                messageType: "1",
                content: "[图片]",
                fileUrl
              },
              header: {
                "Authorization": `Bearer ${common_vendor.index.getStorageSync("token")}`,
                "Content-Type": "application/json"
              }
            });
            if (res.statusCode === 200 && res.data.code === 200) {
              loadChatHistory();
            }
          }
        }
      } catch (error) {
        common_vendor.index.hideLoading();
        common_vendor.index.__f__("error", "at pages/chat/chat.vue:286", "上传图片失败:", error);
        common_vendor.index.showToast({
          title: "上传失败",
          icon: "none"
        });
      }
    };
    const chooseVideo = () => {
      common_vendor.index.chooseVideo({
        sourceType: ["album", "camera"],
        maxDuration: 60,
        camera: "back",
        success: async (res) => {
          await uploadAndSendVideo(res.tempFilePath, res.duration);
        }
      });
    };
    const uploadAndSendVideo = async (filePath, duration) => {
      try {
        common_vendor.index.showLoading({ title: "上传中..." });
        const uploadRes = await common_vendor.index.uploadFile({
          url: `${getAppInstance().globalData.baseUrl}/common/upload`,
          filePath,
          name: "file",
          header: {
            "Authorization": `Bearer ${common_vendor.index.getStorageSync("token")}`
          }
        });
        common_vendor.index.hideLoading();
        if (uploadRes.statusCode === 200) {
          const result = JSON.parse(uploadRes.data);
          if (result.code === 200) {
            const fileUrl = result.data.url;
            const res = await common_vendor.index.request({
              url: `${getAppInstance().globalData.baseUrl}/bath/message`,
              method: "POST",
              data: {
                senderId: receiverId.value,
                senderType: receiverType.value,
                receiverId: senderId.value,
                receiverType: senderType.value,
                messageType: "2",
                content: "[视频]",
                fileUrl,
                fileDuration: duration
              },
              header: {
                "Authorization": `Bearer ${common_vendor.index.getStorageSync("token")}`,
                "Content-Type": "application/json"
              }
            });
            if (res.statusCode === 200 && res.data.code === 200) {
              loadChatHistory();
            }
          }
        }
      } catch (error) {
        common_vendor.index.hideLoading();
        common_vendor.index.__f__("error", "at pages/chat/chat.vue:355", "上传视频失败:", error);
        common_vendor.index.showToast({
          title: "上传失败",
          icon: "none"
        });
      }
    };
    const previewImage = (url) => {
      common_vendor.index.previewImage({
        urls: [url],
        current: url
      });
    };
    const playVideo = (url) => {
      common_vendor.index.showToast({
        title: "播放视频",
        icon: "none"
      });
    };
    const loadMore = () => {
    };
    const goBack = () => {
      common_vendor.index.navigateBack();
    };
    const getAppInstance = () => {
      return getApp();
    };
    common_vendor.onMounted(() => {
      loadChatHistory();
    });
    return (_ctx, _cache) => {
      return {
        a: common_vendor.p({
          type: "left",
          size: "24",
          color: "#ffffff"
        }),
        b: common_vendor.o(goBack, "94"),
        c: common_vendor.t(otherUserName.value),
        d: common_vendor.f(messages.value, (msg, k0, i0) => {
          return common_vendor.e({
            a: msg.messageType === "0"
          }, msg.messageType === "0" ? {
            b: common_vendor.t(msg.content)
          } : msg.messageType === "1" ? {
            d: msg.fileUrl,
            e: common_vendor.o(($event) => previewImage(msg.fileUrl), msg.messageId)
          } : msg.messageType === "2" ? common_vendor.e({
            g: msg.thumbnailUrl || msg.fileUrl,
            h: common_vendor.o(($event) => playVideo(msg.fileUrl), msg.messageId),
            i: "67400a6e-1-" + i0,
            j: common_vendor.p({
              type: "play-filled",
              size: "40",
              color: "#ffffff"
            }),
            k: msg.fileDuration
          }, msg.fileDuration ? {
            l: common_vendor.t(formatDuration(msg.fileDuration))
          } : {}) : {}, {
            c: msg.messageType === "1",
            f: msg.messageType === "2",
            m: common_vendor.t(formatTime(msg.createTime)),
            n: common_vendor.n(msg.senderId === currentUserId.value && msg.senderType === currentUserType.value ? "message-right" : "message-left"),
            o: msg.messageId
          });
        }),
        e: scrollTop.value,
        f: common_vendor.o(loadMore, "db"),
        g: common_vendor.p({
          type: "image",
          size: "24",
          color: "#ff6b35"
        }),
        h: common_vendor.o(chooseImage, "0b"),
        i: common_vendor.p({
          type: "videocam",
          size: "24",
          color: "#ff6b35"
        }),
        j: common_vendor.o(chooseVideo, "99"),
        k: common_vendor.o(sendTextMessage, "7e"),
        l: inputText.value,
        m: common_vendor.o(($event) => inputText.value = $event.detail.value, "6b"),
        n: common_vendor.o(sendTextMessage, "22")
      };
    };
  }
});
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/chat/chat.js.map
