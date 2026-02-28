"use strict";
const common_vendor = require("../../common/vendor.js");
const _sfc_main = {
  data() {
    return {
      activeTab: "all",
      appointmentList: [],
      loading: false,
      refreshing: false,
      currentPage: 1,
      pageSize: 10,
      hasMore: true,
      orderStatusCache: {}
      // 缓存订单状态，key为appointmentId，value为订单状态
    };
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
      var _a;
      if (this.loading)
        return;
      this.loading = true;
      try {
        const app = getApp();
        const baseUrl = app && app.globalData && app.globalData.baseUrl || "http://localhost:8080";
        const token = common_vendor.index.getStorageSync("token");
        const params = {
          pageNum: this.currentPage,
          pageSize: this.pageSize
        };
        if (this.activeTab !== "all") {
          params.status = this.activeTab;
        }
        const res = await common_vendor.index.request({
          url: `${baseUrl}/bath/appointment/miniprogram/list`,
          method: "GET",
          data: params,
          header: {
            "Content-Type": "application/json",
            "Authorization": token ? `Bearer ${token}` : ""
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
          this.batchCheckOrderStatus(rows);
          this.hasMore = this.appointmentList.length < total;
        } else {
          throw new Error(((_a = res.data) == null ? void 0 : _a.msg) || "获取预约列表失败");
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/appointment/appointment.vue:209", "获取预约列表失败:", error);
        common_vendor.index.showToast({
          title: error.message || "获取预约列表失败",
          icon: "none"
        });
      } finally {
        this.loading = false;
        this.refreshing = false;
        common_vendor.index.stopPullDownRefresh();
      }
    },
    loadMore() {
      if (!this.hasMore || this.loading)
        return;
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
      common_vendor.index.navigateTo({
        url: `/pages/appointment/detail?appointmentId=${item.appointmentId}`
      });
    },
    getStatusText(status) {
      const statusMap = {
        "0": "待确认",
        "1": "已确认",
        "2": "服务中",
        "3": "已完成",
        "4": "已取消"
      };
      return statusMap[status] || "未知";
    },
    getStatusClass(status) {
      const classMap = {
        "0": "status-pending",
        "1": "status-confirmed",
        "2": "status-in-service",
        "3": "status-completed",
        "4": "status-cancelled"
      };
      return classMap[status] || "";
    },
    formatDateTime(dateTime) {
      if (!dateTime)
        return "";
      let dateStr = dateTime;
      if (typeof dateTime === "string") {
        dateStr = dateTime.replace(" ", "T");
        if (dateStr.split(":").length === 2) {
          dateStr += ":00";
        }
      }
      const date = new Date(dateStr);
      if (isNaN(date.getTime())) {
        if (typeof dateTime === "string") {
          const parts = dateTime.match(/(\d{4})-(\d{2})-(\d{2})\s+(\d{2}):(\d{2}):?(\d{2})?/);
          if (parts) {
            const year2 = parseInt(parts[1]);
            const month2 = parseInt(parts[2]) - 1;
            const day2 = parseInt(parts[3]);
            const hours2 = parseInt(parts[4]);
            const minutes2 = parseInt(parts[5]);
            return `${year2}-${String(month2 + 1).padStart(2, "0")}-${String(day2).padStart(2, "0")} ${String(hours2).padStart(2, "0")}:${String(minutes2).padStart(2, "0")}`;
          }
        }
        return dateTime;
      }
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const day = String(date.getDate()).padStart(2, "0");
      const hours = String(date.getHours()).padStart(2, "0");
      const minutes = String(date.getMinutes()).padStart(2, "0");
      return `${year}-${month}-${day} ${hours}:${minutes}`;
    },
    formatPrice(price) {
      if (!price)
        return "0.00";
      return parseFloat(price).toFixed(2);
    },
    // 检查订单是否已支付
    isOrderPaid(item) {
      if (this.orderStatusCache[item.appointmentId] !== void 0) {
        const orderStatus = this.orderStatusCache[item.appointmentId];
        return orderStatus === "1" || orderStatus === "paid";
      }
      this.checkOrderStatus(item);
      return false;
    },
    // 检查订单状态并缓存
    async checkOrderStatus(item) {
      try {
        const app = getApp();
        const baseUrl = app && app.globalData && app.globalData.baseUrl || "http://localhost:8080";
        const token = common_vendor.index.getStorageSync("token");
        const orderRes = await common_vendor.index.request({
          url: `${baseUrl}/bath/order/miniprogram/byAppointment/${item.appointmentId}`,
          method: "GET",
          header: {
            "Content-Type": "application/json",
            "Authorization": token ? `Bearer ${token}` : ""
          }
        });
        if (orderRes.statusCode === 200 && orderRes.data && orderRes.data.code === 200) {
          const order = orderRes.data.data;
          if (order && order.status) {
            this.$set(this.orderStatusCache, item.appointmentId, order.status);
            this.$forceUpdate();
          }
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/appointment/appointment.vue:341", "查询订单状态失败:", error);
      }
    },
    // 批量检查订单状态
    async batchCheckOrderStatus(appointments) {
      const confirmedAppointments = appointments.filter((item) => item.status === "1");
      if (confirmedAppointments.length === 0)
        return;
      const promises = confirmedAppointments.map((item) => this.checkOrderStatus(item));
      await Promise.all(promises);
    },
    // 检查是否已评价
    async checkReviewStatus(item) {
      try {
        const app = getApp();
        const baseUrl = app && app.globalData && app.globalData.baseUrl || "http://localhost:8080";
        const token = common_vendor.index.getStorageSync("token");
        const reviewRes = await common_vendor.index.request({
          url: `${baseUrl}/bath/review/miniprogram/byAppointment/${item.appointmentId}`,
          method: "GET",
          header: {
            "Content-Type": "application/json",
            "Authorization": token ? `Bearer ${token}` : ""
          }
        });
        if (reviewRes.statusCode === 200 && reviewRes.data && reviewRes.data.code === 200) {
          const review = reviewRes.data.data;
          this.$set(item, "hasReview", review != null);
          this.$forceUpdate();
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/appointment/appointment.vue:380", "查询评价状态失败:", error);
        this.$set(item, "hasReview", false);
      }
    },
    // 批量检查评价状态
    async batchCheckReviewStatus(appointments) {
      const completedAppointments = appointments.filter((item) => item.status === "3");
      if (completedAppointments.length === 0)
        return;
      const promises = completedAppointments.map((item) => this.checkReviewStatus(item));
      await Promise.all(promises);
    },
    // 跳转到评价页面
    goToReview(item) {
      common_vendor.index.navigateTo({
        url: `/pages/review/review?appointmentId=${item.appointmentId}&serviceId=${item.serviceId || ""}&serviceName=${encodeURIComponent(item.serviceName || "")}`
      });
    },
    // 处理支付成功
    async handlePaymentSuccess(baseUrl, token, orderId, tradeNo, appointmentId) {
      try {
        const callbackRes = await common_vendor.index.request({
          url: `${baseUrl}/bath/order/miniprogram/alipay/callback`,
          method: "POST",
          data: {
            orderId,
            tradeNo,
            tradeStatus: "TRADE_SUCCESS"
          },
          header: {
            "Content-Type": "application/json",
            "Authorization": token ? `Bearer ${token}` : ""
          }
        });
        if (callbackRes.statusCode === 200 && callbackRes.data && callbackRes.data.code === 200) {
          if (appointmentId) {
            this.$set(this.orderStatusCache, appointmentId, "1");
          }
          common_vendor.index.showToast({
            title: "支付成功",
            icon: "success"
          });
          this.onRefresh();
        } else {
          common_vendor.index.showToast({
            title: "支付成功，但更新订单状态失败",
            icon: "none"
          });
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/appointment/appointment.vue:437", "更新订单状态失败:", error);
        common_vendor.index.showToast({
          title: "支付成功，但更新订单状态失败",
          icon: "none"
        });
      }
    },
    // 处理支付失败
    handlePaymentFail(err) {
      common_vendor.index.__f__("error", "at pages/appointment/appointment.vue:446", "支付失败:", err);
      if (err.errMsg && (err.errMsg.indexOf("cancel") !== -1 || err.errMsg.indexOf("取消") !== -1)) {
        common_vendor.index.showToast({
          title: "已取消支付",
          icon: "none"
        });
      } else {
        common_vendor.index.showToast({
          title: "支付失败：" + (err.errMsg || "未知错误"),
          icon: "none"
        });
      }
    },
    // 处理支付
    async handlePay(item) {
      var _a, _b;
      try {
        const app = getApp();
        const baseUrl = app && app.globalData && app.globalData.baseUrl || "http://localhost:8080";
        const token = common_vendor.index.getStorageSync("token");
        const orderRes = await common_vendor.index.request({
          url: `${baseUrl}/bath/order/miniprogram/byAppointment/${item.appointmentId}`,
          method: "GET",
          header: {
            "Content-Type": "application/json",
            "Authorization": token ? `Bearer ${token}` : ""
          }
        });
        if (orderRes.statusCode === 200 && orderRes.data && orderRes.data.code === 200) {
          const order = orderRes.data.data;
          if (!order || !order.orderId) {
            common_vendor.index.showToast({
              title: "订单不存在",
              icon: "none"
            });
            return;
          }
          if (order.status !== "0") {
            common_vendor.index.showToast({
              title: "订单状态不正确，无法支付",
              icon: "none"
            });
            return;
          }
          const confirmRes = await common_vendor.index.showModal({
            title: "确认支付",
            content: `确认支付 ¥${this.formatPrice(order.totalAmount)} 吗？`,
            confirmText: "确认支付",
            cancelText: "取消"
          });
          if (!confirmRes.confirm) {
            return;
          }
          common_vendor.index.showLoading({
            title: "正在创建支付订单..."
          });
          const paymentRes = await common_vendor.index.request({
            url: `${baseUrl}/bath/order/miniprogram/alipay/create/${order.orderId}`,
            method: "POST",
            header: {
              "Content-Type": "application/json",
              "Authorization": token ? `Bearer ${token}` : ""
            }
          });
          common_vendor.index.hideLoading();
          if (paymentRes.statusCode === 200 && paymentRes.data && paymentRes.data.code === 200) {
            const paymentData = paymentRes.data.data;
            common_vendor.index.showLoading({
              title: "支付中..."
            });
            common_vendor.index.__f__("log", "at pages/appointment/appointment.vue:557", "微信小程序：使用模拟支付（沙盒环境）");
            common_vendor.index.hideLoading();
            const confirmRes2 = await common_vendor.index.showModal({
              title: "模拟支付",
              content: `沙盒环境：确认支付 ¥${this.formatPrice(order.totalAmount)} 吗？`,
              confirmText: "确认支付",
              cancelText: "取消"
            });
            if (confirmRes2.confirm) {
              await this.handlePaymentSuccess(baseUrl, token, order.orderId, paymentData.tradeNo, item.appointmentId);
            } else {
              common_vendor.index.showToast({
                title: "已取消支付",
                icon: "none"
              });
            }
          } else {
            throw new Error(((_a = paymentRes.data) == null ? void 0 : _a.msg) || "创建支付订单失败");
          }
        } else {
          throw new Error(((_b = orderRes.data) == null ? void 0 : _b.msg) || "查询订单失败");
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/appointment/appointment.vue:652", "支付失败:", error);
        common_vendor.index.hideLoading();
        common_vendor.index.showToast({
          title: error.message || "支付失败",
          icon: "none"
        });
      }
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $data.activeTab === "all" ? 1 : "",
    b: common_vendor.o(($event) => $options.switchStatusTab("all")),
    c: $data.activeTab === "0" ? 1 : "",
    d: common_vendor.o(($event) => $options.switchStatusTab("0")),
    e: $data.activeTab === "1" ? 1 : "",
    f: common_vendor.o(($event) => $options.switchStatusTab("1")),
    g: $data.activeTab === "2" ? 1 : "",
    h: common_vendor.o(($event) => $options.switchStatusTab("2")),
    i: $data.activeTab === "3" ? 1 : "",
    j: common_vendor.o(($event) => $options.switchStatusTab("3")),
    k: $data.loading && $data.appointmentList.length === 0
  }, $data.loading && $data.appointmentList.length === 0 ? {} : $data.appointmentList.length === 0 ? {} : {
    m: common_vendor.f($data.appointmentList, (item, k0, i0) => {
      return common_vendor.e({
        a: common_vendor.t(item.appointmentNo),
        b: common_vendor.t($options.getStatusText(item.status)),
        c: common_vendor.n($options.getStatusClass(item.status)),
        d: common_vendor.t(item.serviceName || "未命名服务"),
        e: common_vendor.t(item.petName || "未填写"),
        f: common_vendor.t($options.formatDateTime(item.appointmentTime)),
        g: common_vendor.t($options.formatPrice(item.expectedPrice)),
        h: common_vendor.t($options.formatDateTime(item.createTime)),
        i: item.status === "1" && !$options.isOrderPaid(item)
      }, item.status === "1" && !$options.isOrderPaid(item) ? {
        j: common_vendor.o(($event) => $options.handlePay(item), item.appointmentId)
      } : {}, {
        k: item.status === "3"
      }, item.status === "3" ? common_vendor.e({
        l: !item.hasReview
      }, !item.hasReview ? {
        m: common_vendor.o(($event) => $options.goToReview(item), item.appointmentId)
      } : {}) : {}, {
        n: item.appointmentId,
        o: common_vendor.o(($event) => $options.viewDetail(item), item.appointmentId)
      });
    })
  }, {
    l: $data.appointmentList.length === 0,
    n: $data.hasMore && !$data.loading
  }, $data.hasMore && !$data.loading ? {} : {}, {
    o: !$data.hasMore && $data.appointmentList.length > 0
  }, !$data.hasMore && $data.appointmentList.length > 0 ? {} : {}, {
    p: common_vendor.o((...args) => $options.loadMore && $options.loadMore(...args)),
    q: common_vendor.o((...args) => $options.onRefresh && $options.onRefresh(...args)),
    r: $data.refreshing
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-ef2dfbea"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/appointment/appointment.js.map
