"use strict";
const common_vendor = require("../../common/vendor.js");
const _sfc_main = {
  data() {
    return {
      // 表单数据
      formData: {
        serviceId: null,
        petName: "",
        hairType: "0",
        // 默认短毛
        petBreed: "",
        petWeight: "",
        appointmentDate: "",
        appointmentTime: "",
        contactPhone: "",
        remark: "",
        expectedPrice: null
      },
      // 服务列表
      services: [],
      loading: false,
      // 评价列表
      reviews: [],
      loadingReviews: false,
      // 毛发类型选项（0=短毛,1=长毛）
      hairTypes: [
        { value: "0", label: "短毛" },
        { value: "1", label: "长毛" }
      ],
      hairTypeIndex: 0,
      // 预选的服务ID（从首页传递）
      preSelectedServiceId: null
    };
  },
  computed: {
    // 当前选中的服务
    selectedService() {
      if (!this.formData.serviceId)
        return null;
      return this.services.find((s) => s.id === this.formData.serviceId) || null;
    },
    // 最小日期（今天）
    minDate() {
      const date = /* @__PURE__ */ new Date();
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const day = String(date.getDate()).padStart(2, "0");
      return `${year}-${month}-${day}`;
    },
    // 是否可以提交
    canSubmit() {
      return this.formData.serviceId && this.formData.appointmentDate && this.formData.appointmentTime && this.formData.petWeight && this.formData.contactPhone;
    }
  },
  onLoad(options) {
    const token = common_vendor.index.getStorageSync("token");
    const userId = common_vendor.index.getStorageSync("userId");
    if (!token || !userId) {
      common_vendor.index.showToast({
        title: "请先登录",
        icon: "none"
      });
      setTimeout(() => {
        common_vendor.index.navigateTo({
          url: "/pages/login/login"
        });
      }, 1500);
      return;
    }
    if (options.serviceId) {
      this.preSelectedServiceId = parseInt(options.serviceId);
    }
    this.loadServices();
  },
  methods: {
    // 加载服务列表
    async loadServices() {
      var _a;
      this.loading = true;
      try {
        const app = getApp();
        const baseUrl = app && app.globalData && app.globalData.baseUrl || "http://localhost:8080";
        const res = await common_vendor.index.request({
          url: `${baseUrl}/bath/service/enabled`,
          method: "GET",
          header: {
            "Content-Type": "application/json"
          }
        });
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          const serviceList = res.data.data || [];
          this.services = serviceList.map((item) => {
            let imageUrl = "";
            if (item.serviceImages) {
              try {
                const images = JSON.parse(item.serviceImages);
                imageUrl = Array.isArray(images) && images.length > 0 ? images[0] : "";
              } catch (e) {
                imageUrl = item.serviceImages;
              }
            }
            let price = "0";
            if (item.prices && item.prices.length > 0) {
              const prices = item.prices.map((p) => parseFloat(p.price || 0));
              price = Math.min(...prices).toString();
            }
            return {
              id: item.serviceId,
              name: item.serviceName || "",
              price,
              image: imageUrl || "https://ai-public.mastergo.com/ai/img_res/3a21b5f82e4f86baf34988ced88a8d66.jpg",
              desc: item.serviceDesc || ""
            };
          });
          if (this.preSelectedServiceId) {
            const selectedService = this.services.find((s) => s.id === this.preSelectedServiceId);
            if (selectedService) {
              this.selectService(selectedService);
            }
          }
        } else {
          throw new Error(((_a = res.data) == null ? void 0 : _a.msg) || "获取服务列表失败");
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/service/service.vue:351", "获取服务列表失败:", error);
        common_vendor.index.showToast({
          title: error.message || "获取服务列表失败",
          icon: "none"
        });
      } finally {
        this.loading = false;
      }
    },
    // 选择服务
    selectService(service) {
      this.formData.serviceId = service.id;
      this.calculatePrice();
      this.loadReviews(service.id);
    },
    // 加载评价列表
    async loadReviews(serviceId) {
      if (!serviceId) {
        this.reviews = [];
        return;
      }
      this.loadingReviews = true;
      try {
        const app = getApp();
        const baseUrl = app && app.globalData && app.globalData.baseUrl || "http://localhost:8080";
        const res = await common_vendor.index.request({
          url: `${baseUrl}/bath/review/miniprogram/list`,
          method: "GET",
          data: {
            serviceId,
            pageNum: 1,
            pageSize: 10
          },
          header: {
            "Content-Type": "application/json"
          }
        });
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          this.reviews = res.data.rows || [];
        } else {
          this.reviews = [];
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/service/service.vue:399", "加载评价失败:", error);
        this.reviews = [];
      } finally {
        this.loadingReviews = false;
      }
    },
    // 获取星级显示
    getStars(rating) {
      if (!rating)
        return "☆☆☆☆☆";
      const fullStars = "★".repeat(rating);
      const emptyStars = "☆".repeat(5 - rating);
      return fullStars + emptyStars;
    },
    // 格式化时间
    formatTime(timeStr) {
      if (!timeStr)
        return "";
      try {
        const date = new Date(timeStr);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const day = String(date.getDate()).padStart(2, "0");
        return `${year}-${month}-${day}`;
      } catch (e) {
        return timeStr;
      }
    },
    // 毛发类型改变
    onHairTypeChange(e) {
      this.hairTypeIndex = e.detail.value;
      this.formData.hairType = this.hairTypes[e.detail.value].value;
      this.calculatePrice();
    },
    // 获取毛发类型标签
    getHairTypeLabel(value) {
      const type = this.hairTypes.find((t) => t.value === value);
      return type ? type.label : "";
    },
    // 日期改变
    onDateChange(e) {
      this.formData.appointmentDate = e.detail.value;
    },
    // 时间改变
    onTimeChange(e) {
      this.formData.appointmentTime = e.detail.value;
    },
    // 计算价格
    async calculatePrice() {
      if (!this.formData.serviceId || !this.formData.petWeight) {
        this.formData.expectedPrice = null;
        return;
      }
      const weight = parseFloat(this.formData.petWeight);
      if (isNaN(weight) || weight <= 0) {
        this.formData.expectedPrice = null;
        return;
      }
      try {
        const app = getApp();
        const baseUrl = app && app.globalData && app.globalData.baseUrl || "http://localhost:8080";
        const petType = this.formData.hairType || "0";
        const res = await common_vendor.index.request({
          url: `${baseUrl}/bath/service/calculatePriceWithType`,
          method: "GET",
          data: {
            serviceId: this.formData.serviceId,
            petType,
            weight
          },
          header: {
            "Content-Type": "application/json"
          }
        });
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          this.formData.expectedPrice = res.data.data || null;
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/service/service.vue:481", "计算价格失败:", error);
      }
    },
    // 格式化价格
    formatPrice(price) {
      if (!price)
        return "0.00";
      return parseFloat(price).toFixed(2);
    },
    // 提交预约
    async submitAppointment() {
      var _a;
      const token = common_vendor.index.getStorageSync("token");
      const userId = common_vendor.index.getStorageSync("userId");
      if (!token || !userId) {
        common_vendor.index.showToast({
          title: "请先登录",
          icon: "none"
        });
        setTimeout(() => {
          common_vendor.index.navigateTo({
            url: "/pages/login/login"
          });
        }, 1500);
        return;
      }
      if (!this.canSubmit) {
        common_vendor.index.showToast({
          title: "请完善必填信息",
          icon: "none"
        });
        return;
      }
      if (!this.formData.petWeight) {
        common_vendor.index.showToast({
          title: "请输入宠物体重",
          icon: "none"
        });
        return;
      }
      const weight = parseFloat(this.formData.petWeight);
      if (isNaN(weight) || weight <= 0) {
        common_vendor.index.showToast({
          title: "请输入有效的宠物体重",
          icon: "none"
        });
        return;
      }
      if (!this.formData.contactPhone) {
        common_vendor.index.showToast({
          title: "请输入联系电话",
          icon: "none"
        });
        return;
      }
      const phone = this.formData.contactPhone.trim();
      if (phone.length < 11 || !/^1[3-9]\d{9}$/.test(phone)) {
        common_vendor.index.showToast({
          title: "请输入正确的手机号码",
          icon: "none"
        });
        return;
      }
      const appointmentDateTime = `${this.formData.appointmentDate} ${this.formData.appointmentTime}:00`;
      const submitData = {
        serviceId: this.formData.serviceId,
        appointmentTime: appointmentDateTime,
        petName: this.formData.petName || "未命名宠物",
        petWeight: weight,
        petType: this.formData.hairType || "0",
        // 0=短毛,1=长毛
        petBreed: this.formData.petBreed || "",
        contactPhone: this.formData.contactPhone || "",
        remark: this.formData.remark || ""
      };
      common_vendor.index.showLoading({
        title: "提交中..."
      });
      try {
        const app = getApp();
        const baseUrl = app && app.globalData && app.globalData.baseUrl || "http://localhost:8080";
        const token2 = common_vendor.index.getStorageSync("token");
        const res = await common_vendor.index.request({
          url: `${baseUrl}/bath/appointment/miniprogram`,
          method: "POST",
          data: submitData,
          header: {
            "Content-Type": "application/json",
            "Authorization": token2 ? `Bearer ${token2}` : ""
          }
        });
        common_vendor.index.hideLoading();
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          common_vendor.index.showToast({
            title: "预约成功",
            icon: "success"
          });
          setTimeout(() => {
            common_vendor.index.switchTab({
              url: "/pages/appointment/appointment"
            });
          }, 1500);
        } else {
          throw new Error(((_a = res.data) == null ? void 0 : _a.msg) || "预约失败");
        }
      } catch (error) {
        common_vendor.index.hideLoading();
        common_vendor.index.__f__("error", "at pages/service/service.vue:605", "提交预约失败:", error);
        common_vendor.index.showToast({
          title: error.message || "预约失败",
          icon: "none"
        });
      }
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.f($data.services, (service, k0, i0) => {
      return common_vendor.e({
        a: service.image
      }, service.image ? {
        b: service.image
      } : {}, {
        c: common_vendor.t(service.name),
        d: common_vendor.t($options.formatPrice(service.price)),
        e: $data.formData.serviceId === service.id
      }, $data.formData.serviceId === service.id ? {} : {}, {
        f: service.id,
        g: $data.formData.serviceId === service.id ? 1 : "",
        h: common_vendor.o(($event) => $options.selectService(service), service.id)
      });
    }),
    b: $options.selectedService
  }, $options.selectedService ? {
    c: common_vendor.t($options.selectedService.desc || "暂无服务介绍")
  } : {}, {
    d: $options.selectedService
  }, $options.selectedService ? common_vendor.e({
    e: $data.reviews.length > 0
  }, $data.reviews.length > 0 ? {
    f: common_vendor.t($data.reviews.length)
  } : {}, {
    g: $data.loadingReviews
  }, $data.loadingReviews ? {} : $data.reviews.length === 0 ? {} : {
    i: common_vendor.f($data.reviews, (review, index, i0) => {
      return common_vendor.e({
        a: common_vendor.t($options.getStars(review.rating)),
        b: common_vendor.t(review.rating),
        c: common_vendor.t($options.formatTime(review.createTime)),
        d: common_vendor.t(review.content),
        e: review.reply
      }, review.reply ? {
        f: common_vendor.t(review.reply)
      } : {}, {
        g: index
      });
    })
  }, {
    h: $data.reviews.length === 0
  }) : {}, {
    j: $data.formData.petName,
    k: common_vendor.o(($event) => $data.formData.petName = $event.detail.value),
    l: common_vendor.t($data.formData.hairType ? $options.getHairTypeLabel($data.formData.hairType) : "请选择毛发类型"),
    m: !$data.formData.hairType ? 1 : "",
    n: $data.hairTypes,
    o: $data.hairTypeIndex,
    p: common_vendor.o((...args) => $options.onHairTypeChange && $options.onHairTypeChange(...args)),
    q: $data.formData.petBreed,
    r: common_vendor.o(($event) => $data.formData.petBreed = $event.detail.value),
    s: common_vendor.o((...args) => $options.calculatePrice && $options.calculatePrice(...args)),
    t: $data.formData.petWeight,
    v: common_vendor.o(($event) => $data.formData.petWeight = $event.detail.value),
    w: common_vendor.t($data.formData.appointmentDate || "请选择日期"),
    x: !$data.formData.appointmentDate ? 1 : "",
    y: $data.formData.appointmentDate,
    z: $options.minDate,
    A: common_vendor.o((...args) => $options.onDateChange && $options.onDateChange(...args)),
    B: common_vendor.t($data.formData.appointmentTime || "请选择时间"),
    C: !$data.formData.appointmentTime ? 1 : "",
    D: $data.formData.appointmentTime,
    E: common_vendor.o((...args) => $options.onTimeChange && $options.onTimeChange(...args)),
    F: $data.formData.contactPhone,
    G: common_vendor.o(($event) => $data.formData.contactPhone = $event.detail.value),
    H: $data.formData.remark,
    I: common_vendor.o(($event) => $data.formData.remark = $event.detail.value),
    J: $data.formData.expectedPrice
  }, $data.formData.expectedPrice ? {
    K: common_vendor.t($options.formatPrice($data.formData.expectedPrice))
  } : {}, {
    L: common_vendor.t($options.canSubmit ? "提交预约" : "请完善必填信息"),
    M: !$options.canSubmit,
    N: common_vendor.o((...args) => $options.submitAppointment && $options.submitAppointment(...args))
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-cb2df937"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/service/service.js.map
