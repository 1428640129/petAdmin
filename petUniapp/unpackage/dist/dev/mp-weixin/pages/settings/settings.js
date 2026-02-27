"use strict";
const common_vendor = require("../../common/vendor.js");
const _sfc_main = {
  data() {
    return {
      showPasswordModal: false,
      changing: false,
      passwordForm: {
        oldPassword: "",
        newPassword: "",
        confirmPassword: ""
      }
    };
  },
  methods: {
    getBaseUrl() {
      const app = getApp();
      return app && app.globalData && app.globalData.baseUrl || "http://localhost:8080";
    },
    goChangePassword() {
      this.passwordForm = { oldPassword: "", newPassword: "", confirmPassword: "" };
      this.showPasswordModal = true;
    },
    async submitPassword() {
      const { oldPassword, newPassword, confirmPassword } = this.passwordForm;
      if (!oldPassword) {
        common_vendor.index.showToast({ title: "请输入原密码", icon: "none" });
        return;
      }
      if (!newPassword || newPassword.length < 6) {
        common_vendor.index.showToast({ title: "新密码至少6位", icon: "none" });
        return;
      }
      if (newPassword !== confirmPassword) {
        common_vendor.index.showToast({ title: "两次输入密码不一致", icon: "none" });
        return;
      }
      const userId = common_vendor.index.getStorageSync("userId");
      if (!userId) {
        common_vendor.index.showToast({ title: "请先登录", icon: "none" });
        return;
      }
      this.changing = true;
      try {
        const res = await common_vendor.index.request({
          url: `${this.getBaseUrl()}/bath/user/password`,
          method: "PUT",
          header: { "Content-Type": "application/json" },
          data: {
            userId,
            oldPassword,
            newPassword
          }
        });
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          common_vendor.index.showToast({ title: "修改成功", icon: "success" });
          this.showPasswordModal = false;
        } else {
          throw new Error(res.data && res.data.msg ? res.data.msg : "修改失败");
        }
      } catch (e) {
        common_vendor.index.showToast({ title: e.message || "修改失败", icon: "none" });
      } finally {
        this.changing = false;
      }
    },
    logout() {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定要退出登录吗？",
        success: (res) => {
          if (res.confirm) {
            common_vendor.index.removeStorageSync("userInfo");
            common_vendor.index.removeStorageSync("userId");
            common_vendor.index.removeStorageSync("userType");
            common_vendor.index.removeStorageSync("token");
            common_vendor.index.showToast({ title: "已退出", icon: "success" });
            setTimeout(() => {
              common_vendor.index.switchTab({ url: "/pages/index/index" });
            }, 1e3);
          }
        }
      });
    }
  }
};
if (!Array) {
  const _component_uni_icons = common_vendor.resolveComponent("uni-icons");
  _component_uni_icons();
}
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.p({
      type: "locked",
      size: "20",
      color: "#ff6b35"
    }),
    b: common_vendor.p({
      type: "arrowright",
      size: "16",
      color: "#ccc"
    }),
    c: common_vendor.o((...args) => $options.goChangePassword && $options.goChangePassword(...args)),
    d: common_vendor.p({
      type: "redo",
      size: "20",
      color: "#ff6b35"
    }),
    e: common_vendor.p({
      type: "arrowright",
      size: "16",
      color: "#ccc"
    }),
    f: common_vendor.o((...args) => $options.logout && $options.logout(...args)),
    g: $data.showPasswordModal
  }, $data.showPasswordModal ? {
    h: $data.passwordForm.oldPassword,
    i: common_vendor.o(($event) => $data.passwordForm.oldPassword = $event.detail.value),
    j: $data.passwordForm.newPassword,
    k: common_vendor.o(($event) => $data.passwordForm.newPassword = $event.detail.value),
    l: $data.passwordForm.confirmPassword,
    m: common_vendor.o(($event) => $data.passwordForm.confirmPassword = $event.detail.value),
    n: common_vendor.o(($event) => $data.showPasswordModal = false),
    o: $data.changing,
    p: common_vendor.o((...args) => $options.submitPassword && $options.submitPassword(...args)),
    q: common_vendor.o(() => {
    }),
    r: common_vendor.o(($event) => $data.showPasswordModal = false)
  } : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-7fad0a1c"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/settings/settings.js.map
