"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
if (!Array) {
  const _component_uni_icons = common_vendor.resolveComponent("uni-icons");
  _component_uni_icons();
}
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "message",
  setup(__props) {
    const currentUserId = common_vendor.ref(1);
    const currentUserType = common_vendor.ref("0");
    const messages = common_vendor.ref([]);
    const loading = common_vendor.ref(false);
    const formatTime = (timeStr) => {
      if (!timeStr)
        return "";
      const date = new Date(timeStr);
      const now = /* @__PURE__ */ new Date();
      const diff = now.getTime() - date.getTime();
      const days = Math.floor(diff / (1e3 * 60 * 60 * 24));
      if (days === 0) {
        const hours = date.getHours();
        const minutes = date.getMinutes();
        return `${hours.toString().padStart(2, "0")}:${minutes.toString().padStart(2, "0")}`;
      } else if (days === 1) {
        return "昨天";
      } else if (days < 7) {
        return `${days}天前`;
      } else {
        return `${date.getMonth() + 1}-${date.getDate()}`;
      }
    };
    const getMessagePreview = (msg) => {
      if (msg.messageType === "0") {
        return msg.content || "";
      } else if (msg.messageType === "1") {
        return "[图片]";
      } else if (msg.messageType === "2") {
        return "[视频]";
      }
      return "";
    };
    const getOtherUserInfo = (msg) => {
      if (msg.senderId === currentUserId.value && msg.senderType === currentUserType.value) {
        return {
          id: msg.receiverId,
          type: msg.receiverType,
          name: msg.receiverType === "1" ? "商家" : "用户"
        };
      } else {
        return {
          id: msg.senderId,
          type: msg.senderType,
          name: msg.senderType === "1" ? "商家" : "用户"
        };
      }
    };
    const loadChatList = async () => {
      try {
        loading.value = true;
        const res = await common_vendor.index.request({
          url: `${getAppInstance().globalData.baseUrl}/bath/message/chatList`,
          method: "GET",
          data: {
            userId: currentUserId.value,
            userType: currentUserType.value
          },
          header: {
            "Authorization": `Bearer ${common_vendor.index.getStorageSync("token")}`
          }
        });
        if (res.statusCode === 200 && res.data.code === 200) {
          messages.value = res.data.data || [];
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/message/message.vue:145", "加载聊天列表失败:", error);
        common_vendor.index.showToast({
          title: "加载失败",
          icon: "none"
        });
      } finally {
        loading.value = false;
      }
    };
    const viewMessageDetail = (msg) => {
      const otherUser = getOtherUserInfo(msg);
      common_vendor.index.navigateTo({
        url: `/pages/chat/chat?senderId=${otherUser.id}&senderType=${otherUser.type}&receiverId=${currentUserId.value}&receiverType=${currentUserType.value}`
      });
    };
    const sendMessage = () => {
      common_vendor.index.navigateTo({
        url: "/pages/sendMessage/sendMessage"
      });
    };
    common_vendor.onMounted(() => {
      loadChatList();
    });
    const onPullDownRefresh = () => {
      loadChatList().finally(() => {
        common_vendor.index.stopPullDownRefresh();
      });
    };
    const switchTab = (page) => {
      if (page === "index") {
        common_vendor.index.switchTab({
          url: "/pages/index/index"
        });
      } else if (page === "my") {
        common_vendor.index.switchTab({
          url: "/pages/my/my"
        });
      }
    };
    const getAppInstance = () => {
      return getApp();
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: loading.value && messages.value.length === 0
      }, loading.value && messages.value.length === 0 ? {} : messages.value.length === 0 ? {} : {
        c: common_vendor.f(messages.value, (msg, k0, i0) => {
          return common_vendor.e({
            a: "33f06799-0-" + i0,
            b: common_vendor.p({
              type: getOtherUserInfo(msg).type === "1" ? "shop" : "staff",
              size: "30",
              color: "#ff6b35"
            }),
            c: common_vendor.t(getOtherUserInfo(msg).name),
            d: common_vendor.t(formatTime(msg.createTime)),
            e: common_vendor.t(getMessagePreview(msg)),
            f: msg.isRead === "0" && msg.receiverId === currentUserId.value && msg.receiverType === currentUserType.value
          }, msg.isRead === "0" && msg.receiverId === currentUserId.value && msg.receiverType === currentUserType.value ? {} : {}, {
            g: msg.messageId,
            h: common_vendor.o(($event) => viewMessageDetail(msg), msg.messageId)
          });
        })
      }, {
        b: messages.value.length === 0,
        d: common_vendor.o(onPullDownRefresh, "5f"),
        e: common_vendor.o(sendMessage, "aa"),
        f: common_assets._imports_0,
        g: common_vendor.o(($event) => switchTab("index"), "13"),
        h: common_assets._imports_1,
        i: common_vendor.o(($event) => switchTab("my"), "ee")
      });
    };
  }
});
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/message/message.js.map
