"use strict";
const common_vendor = require("../../common/vendor.js");
const _sfc_main = {
  data() {
    return {
      appointmentId: null,
      serviceId: null,
      serviceName: "",
      formData: {
        rating: 0,
        content: "",
        images: ""
      },
      imageList: []
    };
  },
  computed: {
    canSubmit() {
      return this.formData.rating > 0 && this.formData.content.trim().length > 0;
    }
  },
  onLoad(options) {
    if (options.appointmentId) {
      this.appointmentId = parseInt(options.appointmentId);
    }
    if (options.serviceId) {
      this.serviceId = parseInt(options.serviceId);
    }
    if (options.serviceName) {
      this.serviceName = decodeURIComponent(options.serviceName);
    }
  },
  methods: {
    setRating(rating) {
      this.formData.rating = rating;
    },
    chooseImage() {
      const maxCount = 3 - this.imageList.length;
      common_vendor.index.chooseImage({
        count: maxCount,
        sizeType: ["compressed"],
        sourceType: ["album", "camera"],
        success: (res) => {
          const tempFilePaths = res.tempFilePaths;
          this.uploadImages(tempFilePaths);
        },
        fail: (err) => {
          common_vendor.index.__f__("error", "at pages/review/review.vue:153", "选择图片失败:", err);
          common_vendor.index.showToast({
            title: "选择图片失败",
            icon: "none"
          });
        }
      });
    },
    async uploadImages(filePaths) {
      common_vendor.index.showLoading({
        title: "上传中..."
      });
      try {
        const app = getApp();
        const baseUrl = app && app.globalData && app.globalData.baseUrl || "http://localhost:8080";
        const token = common_vendor.index.getStorageSync("token");
        const uploadPromises = filePaths.map((filePath) => {
          return new Promise((resolve, reject) => {
            common_vendor.index.uploadFile({
              url: `${baseUrl}/common/upload`,
              filePath,
              name: "file",
              header: {
                "Authorization": token ? `Bearer ${token}` : ""
              },
              success: (uploadRes) => {
                try {
                  const data = JSON.parse(uploadRes.data);
                  if (data.code === 200) {
                    resolve(data.url || data.data);
                  } else {
                    reject(new Error(data.msg || "上传失败"));
                  }
                } catch (e) {
                  reject(new Error("解析上传结果失败"));
                }
              },
              fail: (err) => {
                reject(err);
              }
            });
          });
        });
        const uploadedUrls = await Promise.all(uploadPromises);
        this.imageList = this.imageList.concat(uploadedUrls);
        common_vendor.index.hideLoading();
        common_vendor.index.showToast({
          title: "上传成功",
          icon: "success"
        });
      } catch (error) {
        common_vendor.index.hideLoading();
        common_vendor.index.__f__("error", "at pages/review/review.vue:209", "上传图片失败:", error);
        common_vendor.index.showToast({
          title: error.message || "上传图片失败",
          icon: "none"
        });
      }
    },
    deleteImage(index) {
      this.imageList.splice(index, 1);
    },
    async submitReview() {
      var _a;
      if (!this.canSubmit) {
        common_vendor.index.showToast({
          title: "请完善必填信息",
          icon: "none"
        });
        return;
      }
      if (!this.appointmentId) {
        common_vendor.index.showToast({
          title: "预约信息错误",
          icon: "none"
        });
        return;
      }
      const submitData = {
        appointmentId: this.appointmentId,
        serviceId: this.serviceId,
        rating: this.formData.rating,
        content: this.formData.content.trim(),
        images: this.imageList.length > 0 ? this.imageList.join(",") : ""
      };
      common_vendor.index.showLoading({
        title: "提交中..."
      });
      try {
        const app = getApp();
        const baseUrl = app && app.globalData && app.globalData.baseUrl || "http://localhost:8080";
        const token = common_vendor.index.getStorageSync("token");
        const res = await common_vendor.index.request({
          url: `${baseUrl}/bath/review/miniprogram`,
          method: "POST",
          data: submitData,
          header: {
            "Content-Type": "application/json",
            "Authorization": token ? `Bearer ${token}` : ""
          }
        });
        common_vendor.index.hideLoading();
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          common_vendor.index.showToast({
            title: "评价成功",
            icon: "success"
          });
          setTimeout(() => {
            common_vendor.index.navigateBack();
          }, 1500);
        } else {
          throw new Error(((_a = res.data) == null ? void 0 : _a.msg) || "评价失败");
        }
      } catch (error) {
        common_vendor.index.hideLoading();
        common_vendor.index.__f__("error", "at pages/review/review.vue:281", "提交评价失败:", error);
        common_vendor.index.showToast({
          title: error.message || "评价失败",
          icon: "none"
        });
      }
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.t($data.serviceName || "宠物服务"),
    b: common_vendor.f(5, (star, index, i0) => {
      return {
        a: common_vendor.t(index < $data.formData.rating ? "★" : "☆"),
        b: index < $data.formData.rating ? 1 : "",
        c: index,
        d: common_vendor.o(($event) => $options.setRating(index + 1), index)
      };
    }),
    c: common_vendor.t($data.formData.rating > 0 ? $data.formData.rating + "分" : "请选择评分"),
    d: $data.formData.content,
    e: common_vendor.o(($event) => $data.formData.content = $event.detail.value),
    f: common_vendor.t($data.formData.content.length),
    g: common_vendor.f($data.imageList, (image, index, i0) => {
      return {
        a: image,
        b: common_vendor.o(($event) => $options.deleteImage(index), index),
        c: index
      };
    }),
    h: $data.imageList.length < 3
  }, $data.imageList.length < 3 ? {
    i: common_vendor.o((...args) => $options.chooseImage && $options.chooseImage(...args))
  } : {}, {
    j: common_vendor.t($options.canSubmit ? "提交评价" : "请完善必填信息"),
    k: !$options.canSubmit,
    l: common_vendor.o((...args) => $options.submitReview && $options.submitReview(...args))
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-7018a65d"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/review/review.js.map
