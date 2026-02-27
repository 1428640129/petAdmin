"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_constants = require("../../utils/constants.js");
const _sfc_main = {
  data() {
    return {
      appointmentId: "",
      appointment: {
        id: 1,
        appointmentDate: "2026-01-21",
        appointmentTime: "14:00",
        serviceName: "基础洗浴",
        price: 88,
        status: utils_constants.APPOINTMENT_STATUS.PENDING,
        // 使用数字状态：'0'
        petName: "旺财",
        petType: "金毛",
        petWeight: "25",
        contactName: "张先生",
        contactPhone: "13800138000",
        remark: "狗狗比较怕水，请温柔一点",
        review: null
      }
    };
  },
  onLoad(options) {
    if (options.id) {
      this.appointmentId = options.id;
      this.loadDetail();
    }
  },
  methods: {
    async loadDetail() {
    },
    handleAction(action) {
      let title = "";
      let content = "";
      switch (action) {
        case "confirm":
          title = "确认预约";
          content = "确认接受这个预约吗？";
          break;
        case "reject":
          title = "拒绝预约";
          content = "确定要拒绝这个预约吗？";
          break;
        case "complete":
          title = "完成服务";
          content = "确认已完成服务吗？";
          break;
      }
      common_vendor.index.showModal({
        title,
        content,
        success: async (res) => {
          if (res.confirm) {
            try {
              common_vendor.index.showToast({
                title: "操作成功",
                icon: "success"
              });
              setTimeout(() => {
                common_vendor.index.navigateBack();
              }, 1500);
            } catch (error) {
              common_vendor.index.showToast({
                title: "操作失败",
                icon: "none"
              });
            }
          }
        }
      });
    },
    goToReply() {
      common_vendor.index.navigateTo({
        url: `/pages/merchant/reply?id=${this.appointmentId}`
      });
    },
    makeCall() {
      common_vendor.index.makePhoneCall({
        phoneNumber: this.appointment.contactPhone
      });
    },
    getStatusText(status) {
      return utils_constants.APPOINTMENT_STATUS_TEXT[status] || "未知";
    },
    getStars(rating) {
      return "⭐".repeat(rating || 0);
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.t($data.appointment.appointmentDate),
    b: common_vendor.t($data.appointment.appointmentTime),
    c: common_vendor.t($data.appointment.serviceName),
    d: common_vendor.t($data.appointment.price),
    e: common_vendor.t($options.getStatusText($data.appointment.status)),
    f: common_vendor.n("status-" + $data.appointment.status),
    g: common_vendor.t($data.appointment.petName),
    h: common_vendor.t($data.appointment.petType),
    i: common_vendor.t($data.appointment.petWeight),
    j: common_vendor.t($data.appointment.contactName),
    k: common_vendor.t($data.appointment.contactPhone),
    l: common_vendor.o((...args) => $options.makeCall && $options.makeCall(...args)),
    m: $data.appointment.remark
  }, $data.appointment.remark ? {
    n: common_vendor.t($data.appointment.remark)
  } : {}, {
    o: $data.appointment.review
  }, $data.appointment.review ? common_vendor.e({
    p: common_vendor.t($options.getStars($data.appointment.review.rating)),
    q: common_vendor.t($data.appointment.review.content),
    r: common_vendor.t($data.appointment.review.createTime),
    s: $data.appointment.review.reply
  }, $data.appointment.review.reply ? {
    t: common_vendor.t($data.appointment.review.reply)
  } : {}) : {}, {
    v: $data.appointment.status === _ctx.APPOINTMENT_STATUS.PENDING
  }, $data.appointment.status === _ctx.APPOINTMENT_STATUS.PENDING ? {
    w: common_vendor.o(($event) => $options.handleAction("reject"))
  } : {}, {
    x: $data.appointment.status === _ctx.APPOINTMENT_STATUS.PENDING
  }, $data.appointment.status === _ctx.APPOINTMENT_STATUS.PENDING ? {
    y: common_vendor.o(($event) => $options.handleAction("confirm"))
  } : {}, {
    z: $data.appointment.status === _ctx.APPOINTMENT_STATUS.CONFIRMED
  }, $data.appointment.status === _ctx.APPOINTMENT_STATUS.CONFIRMED ? {
    A: common_vendor.o(($event) => $options.handleAction("complete"))
  } : {}, {
    B: $data.appointment.status === _ctx.APPOINTMENT_STATUS.COMPLETED && $data.appointment.review && !$data.appointment.review.reply
  }, $data.appointment.status === _ctx.APPOINTMENT_STATUS.COMPLETED && $data.appointment.review && !$data.appointment.review.reply ? {
    C: common_vendor.o((...args) => $options.goToReply && $options.goToReply(...args))
  } : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-12858ede"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/merchant/detail.js.map
