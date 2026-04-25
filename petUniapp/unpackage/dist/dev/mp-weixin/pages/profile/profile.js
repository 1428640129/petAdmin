"use strict";
const common_vendor = require("../../common/vendor.js");
const _sfc_main = {
  data() {
    return {
      form: {
        userId: null,
        userName: "",
        nickName: "",
        phone: "",
        avatar: "",
        userType: "0"
      },
      saving: false
    };
  },
  onLoad() {
    this.loadProfile();
  },
  methods: {
    getBaseUrl() {
      const app = getApp();
      return app && app.globalData && app.globalData.baseUrl || "http://localhost:8080";
    },
    async loadProfile() {
      const userId = common_vendor.index.getStorageSync("userId");
      if (!userId) {
        common_vendor.index.showToast({ title: "请先登录", icon: "none" });
        setTimeout(() => common_vendor.index.navigateBack(), 1500);
        return;
      }
      try {
        const res = await common_vendor.index.request({
          url: `${this.getBaseUrl()}/bath/user/profile`,
          method: "GET",
          data: { userId }
        });
        if (res.statusCode === 200 && res.data && res.data.code === 200 && res.data.data) {
          const d = res.data.data;
          let avatar = d.avatar || "";
          if (avatar && avatar.startsWith("/") && !avatar.startsWith("http")) {
            avatar = this.getBaseUrl() + avatar;
          }
          this.form = {
            userId: d.userId,
            userName: d.userName || "",
            nickName: d.nickName || "",
            phone: d.phone || "",
            avatar,
            userType: d.userType || "0"
          };
        }
      } catch (e) {
        common_vendor.index.showToast({ title: "加载失败", icon: "none" });
      }
    },
    chooseAvatar() {
      common_vendor.index.chooseImage({
        count: 1,
        sizeType: ["compressed"],
        sourceType: ["album", "camera"],
        success: (chooseRes) => {
          const tempPath = chooseRes.tempFilePaths[0];
          this.form.avatar = tempPath;
          common_vendor.index.showLoading({ title: "上传中..." });
          common_vendor.index.uploadFile({
            url: `${this.getBaseUrl()}/bath/user/uploadAvatar`,
            filePath: tempPath,
            name: "file",
            success: (uploadRes) => {
              common_vendor.index.hideLoading();
              try {
                const data = JSON.parse(uploadRes.data);
                if (data.code === 200 && data.url) {
                  this.form.avatar = data.url;
                  common_vendor.index.showToast({ title: "头像上传成功", icon: "success" });
                } else {
                  throw new Error(data.msg || "上传失败");
                }
              } catch (e) {
                common_vendor.index.showToast({ title: e.message || "上传失败", icon: "none" });
              }
            },
            fail: () => {
              common_vendor.index.hideLoading();
              common_vendor.index.showToast({ title: "上传失败", icon: "none" });
            }
          });
        }
      });
    },
    async saveProfile() {
      if (!this.form.userId) {
        common_vendor.index.showToast({ title: "请先登录", icon: "none" });
        return;
      }
      if (this.form.phone && !/^1[3-9]\d{9}$/.test(this.form.phone)) {
        common_vendor.index.showToast({ title: "手机号格式不正确", icon: "none" });
        return;
      }
      this.saving = true;
      try {
        const res = await common_vendor.index.request({
          url: `${this.getBaseUrl()}/bath/user/profile`,
          method: "PUT",
          header: { "Content-Type": "application/json" },
          data: {
            userId: this.form.userId,
            nickName: this.form.nickName || "",
            phone: this.form.phone || "",
            avatar: this.form.avatar || ""
          }
        });
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          const userInfo = common_vendor.index.getStorageSync("userInfo") || {};
          userInfo.nickName = this.form.nickName;
          userInfo.phone = this.form.phone;
          userInfo.avatar = this.form.avatar;
          common_vendor.index.setStorageSync("userInfo", userInfo);
          common_vendor.index.showToast({ title: "保存成功", icon: "success" });
          setTimeout(() => common_vendor.index.navigateBack(), 1500);
        } else {
          throw new Error(res.data && res.data.msg ? res.data.msg : "保存失败");
        }
      } catch (e) {
        common_vendor.index.showToast({ title: e.message || "保存失败", icon: "none" });
      } finally {
        this.saving = false;
      }
    }
  }
};
if (!Array) {
  const _component_uni_icons = common_vendor.resolveComponent("uni-icons");
  _component_uni_icons();
}
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $data.form.avatar
  }, $data.form.avatar ? {
    b: $data.form.avatar
  } : {
    c: common_vendor.p({
      type: "person",
      size: "60",
      color: "#ccc"
    })
  }, {
    d: common_vendor.o((...args) => $options.chooseAvatar && $options.chooseAvatar(...args), "fd"),
    e: $data.form.nickName,
    f: common_vendor.o(($event) => $data.form.nickName = $event.detail.value, "6e"),
    g: common_vendor.t($data.form.userName || "-"),
    h: $data.form.phone,
    i: common_vendor.o(($event) => $data.form.phone = $event.detail.value, "c3"),
    j: $data.saving,
    k: common_vendor.o((...args) => $options.saveProfile && $options.saveProfile(...args), "3c")
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-dd383ca2"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/profile/profile.js.map
