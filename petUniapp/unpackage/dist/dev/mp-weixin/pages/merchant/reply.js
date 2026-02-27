"use strict";
const common_vendor = require("../../common/vendor.js");
const _sfc_main = {
  data() {
    return {
      appointmentId: "",
      review: {
        id: 1,
        rating: 5,
        content: "服务很好，狗狗洗得很干净，工作人员很专业！",
        createTime: "2026-01-21 16:30"
      },
      replyContent: ""
    };
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
    },
    async submitReply() {
      if (!this.canSubmit) {
        common_vendor.index.showToast({
          title: "请输入回复内容",
          icon: "none"
        });
        return;
      }
      try {
        common_vendor.index.showToast({
          title: "回复成功",
          icon: "success"
        });
        setTimeout(() => {
          common_vendor.index.navigateBack();
        }, 1500);
      } catch (error) {
        common_vendor.index.showToast({
          title: "回复失败，请重试",
          icon: "none"
        });
      }
    },
    getStars(rating) {
      return "⭐".repeat(rating || 0);
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {
    a: common_vendor.t($options.getStars($data.review.rating)),
    b: common_vendor.t($data.review.rating),
    c: common_vendor.t($data.review.content),
    d: common_vendor.t($data.review.createTime),
    e: $data.replyContent,
    f: common_vendor.o(($event) => $data.replyContent = $event.detail.value),
    g: common_vendor.t($data.replyContent.length),
    h: !$options.canSubmit,
    i: common_vendor.o((...args) => $options.submitReply && $options.submitReply(...args))
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-4b6a929d"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/merchant/reply.js.map
