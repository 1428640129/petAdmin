"use strict";
Object.defineProperty(exports, Symbol.toStringTag, { value: "Module" });
const common_vendor = require("./common/vendor.js");
if (!Math) {
  "./pages/index/index.js";
  "./pages/service/service.js";
  "./pages/my/my.js";
  "./pages/message/message.js";
  "./pages/chat/chat.js";
  "./pages/login/login.js";
  "./pages/merchant/index.js";
  "./pages/merchant/detail.js";
  "./pages/merchant/reply.js";
  "./pages/appointment/appointment.js";
  "./pages/review/review.js";
  "./pages/profile/profile.js";
  "./pages/settings/settings.js";
}
const _sfc_main = {
  globalData: {
    baseUrl: "http://localhost:8080"
    // 后端API地址，根据实际情况修改
  },
  onLaunch: function() {
    common_vendor.index.__f__("log", "at App.vue:7", "App Launch");
  },
  onShow: function() {
    common_vendor.index.__f__("log", "at App.vue:10", "App Show");
  },
  onHide: function() {
    common_vendor.index.__f__("log", "at App.vue:13", "App Hide");
  }
};
function createApp() {
  const app = common_vendor.createSSRApp(_sfc_main);
  return {
    app
  };
}
createApp().app.mount("#app");
exports.createApp = createApp;
//# sourceMappingURL=../.sourcemap/mp-weixin/app.js.map
