"use strict";
const common_vendor = require("../../common/vendor.js");
const common_assets = require("../../common/assets.js");
const _sfc_main = {
  data() {
    return {
      loginType: "phone",
      // 'phone' 或 'account'
      phone: "",
      code: "",
      countdown: 0,
      isCounting: false,
      username: "",
      password: ""
    };
  },
  computed: {
    isPhoneValid() {
      return /^1[3-9]\d{9}$/.test(this.phone);
    },
    canLogin() {
      return this.isPhoneValid && this.code.length === 6;
    },
    canAccountLogin() {
      return this.username.trim().length > 0 && this.password.length >= 6;
    },
    codeBtnText() {
      if (this.isCounting) {
        return `${this.countdown}秒后重新获取`;
      }
      return "获取验证码";
    }
  },
  methods: {
    switchLoginType(type) {
      this.loginType = type;
      if (type === "phone") {
        this.username = "";
        this.password = "";
      } else {
        this.phone = "";
        this.code = "";
        this.isCounting = false;
        this.countdown = 0;
      }
    },
    async getCode() {
      if (!this.isPhoneValid) {
        common_vendor.index.showToast({
          title: "请输入正确的手机号",
          icon: "none"
        });
        return;
      }
      try {
        common_vendor.index.showToast({
          title: "验证码已发送",
          icon: "success"
        });
        this.isCounting = true;
        this.countdown = 60;
        const timer = setInterval(() => {
          this.countdown--;
          if (this.countdown <= 0) {
            clearInterval(timer);
            this.isCounting = false;
          }
        }, 1e3);
      } catch (error) {
        common_vendor.index.showToast({
          title: "发送失败，请重试",
          icon: "none"
        });
      }
    },
    async handleLogin() {
      if (!this.canLogin) {
        return;
      }
      try {
        common_vendor.index.showToast({
          title: "登录成功",
          icon: "success"
        });
        setTimeout(() => {
          common_vendor.index.switchTab({
            url: "/pages/index/index"
          });
        }, 1500);
      } catch (error) {
        common_vendor.index.showToast({
          title: "登录失败，请重试",
          icon: "none"
        });
      }
    },
    // 账号密码登录（使用新的前台用户表）
    async handleAccountLogin() {
      var _a;
      if (!this.canAccountLogin) {
        return;
      }
      try {
        const app = getApp();
        const baseUrl = app && app.globalData && app.globalData.baseUrl || "http://localhost:8080";
        const res = await common_vendor.index.request({
          url: `${baseUrl}/bath/user/login`,
          method: "POST",
          header: {
            "Content-Type": "application/json"
          },
          data: {
            userName: this.username.trim(),
            password: this.password
          }
        });
        if (res.statusCode === 200 && res.data && res.data.code === 200) {
          const userInfo = res.data.data;
          if (userInfo) {
            common_vendor.index.setStorageSync("userInfo", userInfo);
            common_vendor.index.setStorageSync("userId", userInfo.userId);
            common_vendor.index.setStorageSync("userType", userInfo.userType);
          }
          common_vendor.index.showToast({
            title: "登录成功",
            icon: "success"
          });
          setTimeout(() => {
            common_vendor.index.switchTab({
              url: "/pages/index/index"
            });
          }, 1500);
        } else {
          throw new Error(((_a = res.data) == null ? void 0 : _a.msg) || "登录失败");
        }
      } catch (error) {
        common_vendor.index.showToast({
          title: error.message || "账号或密码错误",
          icon: "none"
        });
      }
    },
    wechatLogin() {
      common_vendor.index.showToast({
        title: "微信登录功能开发中",
        icon: "none"
      });
    },
    qqLogin() {
      common_vendor.index.showToast({
        title: "QQ登录功能开发中",
        icon: "none"
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $data.loginType === "phone" ? 1 : "",
    b: common_vendor.o(($event) => $options.switchLoginType("phone")),
    c: $data.loginType === "account" ? 1 : "",
    d: common_vendor.o(($event) => $options.switchLoginType("account")),
    e: $data.loginType === "phone"
  }, $data.loginType === "phone" ? {
    f: $data.phone,
    g: common_vendor.o(($event) => $data.phone = $event.detail.value),
    h: $data.code,
    i: common_vendor.o(($event) => $data.code = $event.detail.value),
    j: common_vendor.t($options.codeBtnText),
    k: $data.isCounting || !$options.isPhoneValid,
    l: common_vendor.o((...args) => $options.getCode && $options.getCode(...args)),
    m: !$options.canLogin,
    n: common_vendor.o((...args) => $options.handleLogin && $options.handleLogin(...args))
  } : {}, {
    o: $data.loginType === "account"
  }, $data.loginType === "account" ? {
    p: $data.username,
    q: common_vendor.o(($event) => $data.username = $event.detail.value),
    r: $data.password,
    s: common_vendor.o(($event) => $data.password = $event.detail.value),
    t: !$options.canAccountLogin,
    v: common_vendor.o((...args) => $options.handleAccountLogin && $options.handleAccountLogin(...args))
  } : {}, {
    w: common_assets._imports_0$1
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-e4e4508d"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/login/login.js.map
