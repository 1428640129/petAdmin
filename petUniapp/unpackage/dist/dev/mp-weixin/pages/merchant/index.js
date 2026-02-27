"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_constants = require("../../utils/constants.js");
const _sfc_main = {
  data() {
    return {
      stats: {
        todayCount: 0,
        pendingCount: 0,
        completedCount: 0
      },
      activeFilter: "all",
      filters: [
        { label: "全部", value: "all", count: 0 },
        { label: "待处理", value: utils_constants.APPOINTMENT_STATUS.PENDING, count: 0 },
        { label: "已确认", value: utils_constants.APPOINTMENT_STATUS.CONFIRMED, count: 0 },
        { label: "已完成", value: utils_constants.APPOINTMENT_STATUS.COMPLETED, count: 0 },
        { label: "已取消", value: utils_constants.APPOINTMENT_STATUS.CANCELLED, count: 0 }
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
    };
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
      common_vendor.index.stopPullDownRefresh();
    }, 1e3);
  },
  methods: {
    async loadData() {
      await this.loadAppointments();
    },
    async loadAppointments() {
    },
    updateFilterCounts() {
    },
    switchFilter(value) {
      this.activeFilter = value;
      this.loadAppointments();
    },
    viewDetail(item) {
      common_vendor.index.navigateTo({
        url: `/pages/merchant/detail?id=${item.id}`
      });
    },
    async handleAction(item, action) {
      let title = "";
      let content = "";
      switch (action) {
        case "confirm":
          title = "确认预约";
          content = "确认接受这个预约吗？";
          break;
        case "cancel":
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
              this.loadData();
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
    replyReview(item) {
      common_vendor.index.navigateTo({
        url: `/pages/merchant/reply?id=${item.id}`
      });
    },
    getStatusText(status) {
      return utils_constants.APPOINTMENT_STATUS_TEXT[status] || "未知";
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.t($data.stats.todayCount),
    b: common_vendor.t($data.stats.pendingCount),
    c: common_vendor.t($data.stats.completedCount),
    d: common_vendor.f($data.filters, (filter, k0, i0) => {
      return common_vendor.e({
        a: common_vendor.t(filter.label),
        b: filter.count > 0
      }, filter.count > 0 ? {
        c: common_vendor.t(filter.count)
      } : {}, {
        d: $data.activeFilter === filter.value ? 1 : "",
        e: filter.value,
        f: common_vendor.o(($event) => $options.switchFilter(filter.value), filter.value)
      });
    }),
    e: common_vendor.f($data.appointments, (item, index, i0) => {
      return common_vendor.e({
        a: common_vendor.t(item.appointmentDate),
        b: common_vendor.t(item.appointmentTime),
        c: common_vendor.t(item.serviceName),
        d: common_vendor.t($options.getStatusText(item.status)),
        e: common_vendor.n("status-" + item.status),
        f: common_vendor.t(item.petName),
        g: common_vendor.t(item.petType),
        h: common_vendor.t(item.petWeight),
        i: common_vendor.t(item.contactName),
        j: common_vendor.t(item.contactPhone),
        k: item.remark
      }, item.remark ? {
        l: common_vendor.t(item.remark)
      } : {}, {
        m: common_vendor.t(item.price),
        n: item.status === _ctx.APPOINTMENT_STATUS.PENDING
      }, item.status === _ctx.APPOINTMENT_STATUS.PENDING ? {
        o: common_vendor.o(($event) => $options.handleAction(item, "cancel"), index)
      } : {}, {
        p: item.status === _ctx.APPOINTMENT_STATUS.PENDING
      }, item.status === _ctx.APPOINTMENT_STATUS.PENDING ? {
        q: common_vendor.o(($event) => $options.handleAction(item, "confirm"), index)
      } : {}, {
        r: item.status === _ctx.APPOINTMENT_STATUS.CONFIRMED
      }, item.status === _ctx.APPOINTMENT_STATUS.CONFIRMED ? {
        s: common_vendor.o(($event) => $options.handleAction(item, "complete"), index)
      } : {}, {
        t: item.status === _ctx.APPOINTMENT_STATUS.COMPLETED && item.hasReview
      }, item.status === _ctx.APPOINTMENT_STATUS.COMPLETED && item.hasReview ? {
        v: common_vendor.o(($event) => $options.replyReview(item), index)
      } : {}, {
        w: index,
        x: common_vendor.o(($event) => $options.viewDetail(item), index)
      });
    }),
    f: $data.appointments.length === 0
  }, $data.appointments.length === 0 ? {} : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-46294a64"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/merchant/index.js.map
