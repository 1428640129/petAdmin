"use strict";
const common_vendor = require("../../common/vendor.js");
if (!Array) {
  const _component_uni_icons = common_vendor.resolveComponent("uni-icons");
  _component_uni_icons();
}
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "my",
  setup(__props) {
    const userInfo = common_vendor.ref({});
    const loadUserInfo = () => {
      const info = common_vendor.index.getStorageSync("userInfo");
      userInfo.value = info || {};
    };
    const goToProfile = () => {
      if (!common_vendor.index.getStorageSync("userId")) {
        common_vendor.index.showToast({ title: "请先登录", icon: "none" });
        setTimeout(() => {
          common_vendor.index.navigateTo({ url: "/pages/login/login" });
        }, 1500);
        return;
      }
      common_vendor.index.navigateTo({ url: "/pages/profile/profile" });
    };
    const goToSettings = () => {
      if (!common_vendor.index.getStorageSync("userId")) {
        common_vendor.index.showToast({ title: "请先登录", icon: "none" });
        setTimeout(() => {
          common_vendor.index.navigateTo({ url: "/pages/login/login" });
        }, 1500);
        return;
      }
      common_vendor.index.navigateTo({ url: "/pages/settings/settings" });
    };
    common_vendor.onShow(() => {
      loadUserInfo();
    });
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: userInfo.value.avatar
      }, userInfo.value.avatar ? {
        b: userInfo.value.avatar
      } : {}, {
        c: common_vendor.t(userInfo.value.nickName || userInfo.value.userName || "宠物主人"),
        d: common_vendor.t(userInfo.value.userType === "1" ? "商家" : "顾客"),
        e: common_vendor.p({
          type: "personadd",
          size: "20",
          color: "#ff6b35"
        }),
        f: common_vendor.p({
          type: "arrowright",
          size: "16",
          color: "#ccc"
        }),
        g: common_vendor.o(goToProfile, "e0"),
        h: common_vendor.p({
          type: "gear",
          size: "20",
          color: "#ff6b35"
        }),
        i: common_vendor.p({
          type: "arrowright",
          size: "16",
          color: "#ccc"
        }),
        j: common_vendor.o(goToSettings, "a6")
      });
    };
  }
});
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/my/my.js.map
