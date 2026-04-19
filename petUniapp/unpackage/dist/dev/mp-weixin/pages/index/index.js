"use strict";
const common_vendor = require("../../common/vendor.js");
if (!Array) {
  const _component_uni_icons = common_vendor.resolveComponent("uni-icons");
  _component_uni_icons();
}
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "index",
  setup(__props) {
    const carouselList = common_vendor.ref([]);
    const services = common_vendor.ref([]);
    const loading = common_vendor.ref(false);
    const getAppInstance = () => {
      return getApp();
    };
    const loadCarouselList = async () => {
      var _a;
      try {
        const app = getAppInstance();
        const baseUrl = ((_a = app == null ? void 0 : app.globalData) == null ? void 0 : _a.baseUrl) || "http://localhost:8080";
        const res = await common_vendor.index.request({
          url: `${baseUrl}/bath/carousel/enabled`,
          method: "GET",
          header: {
            "Content-Type": "application/json"
          }
        });
        if (res.statusCode === 200 && res.data.code === 200) {
          carouselList.value = (res.data.data || []).map((item) => ({
            title: item.title || "",
            image: item.imageUrl || ""
          }));
        } else {
          common_vendor.index.__f__("error", "at pages/index/index.vue:115", "获取轮播图失败:", res.data.msg);
          carouselList.value = [
            {
              title: "专业宠物洗澡",
              image: "https://ai-public.mastergo.com/ai/img_res/13b5469fd6d2406f55b473faf54a6268.jpg"
            }
          ];
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/index/index.vue:125", "获取轮播图异常:", error);
        carouselList.value = [
          {
            title: "专业宠物洗澡",
            image: "https://ai-public.mastergo.com/ai/img_res/13b5469fd6d2406f55b473faf54a6268.jpg"
          }
        ];
      }
    };
    const loadServiceList = async () => {
      var _a;
      try {
        const app = getAppInstance();
        const baseUrl = ((_a = app == null ? void 0 : app.globalData) == null ? void 0 : _a.baseUrl) || "http://localhost:8080";
        const res = await common_vendor.index.request({
          url: `${baseUrl}/bath/service/enabled`,
          method: "GET",
          header: {
            "Content-Type": "application/json"
          }
        });
        if (res.statusCode === 200 && res.data.code === 200) {
          const serviceList = res.data.data || [];
          services.value = serviceList.map((item) => {
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
              image: imageUrl || "https://ai-public.mastergo.com/ai/img_res/3a21b5f82e4f86baf34988ced88a8d66.jpg"
            };
          });
        } else {
          common_vendor.index.__f__("error", "at pages/index/index.vue:178", "获取服务列表失败:", res.data.msg);
          services.value = [
            {
              name: "基础洗澡",
              price: "68",
              image: "https://ai-public.mastergo.com/ai/img_res/3a21b5f82e4f86baf34988ced88a8d66.jpg"
            }
          ];
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/index/index.vue:189", "获取服务列表异常:", error);
        services.value = [
          {
            name: "基础洗澡",
            price: "68",
            image: "https://ai-public.mastergo.com/ai/img_res/3a21b5f82e4f86baf34988ced88a8d66.jpg"
          }
        ];
      }
    };
    common_vendor.onMounted(() => {
      loading.value = true;
      Promise.all([loadCarouselList(), loadServiceList()]).finally(() => {
        loading.value = false;
      });
    });
    const makeAppointment = () => {
      common_vendor.index.navigateTo({
        url: "/pages/service/service"
      });
    };
    const goToServicePage = (service) => {
      common_vendor.index.navigateTo({
        url: `/pages/service/service?serviceId=${service.id}`
      });
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: carouselList.value.length > 0
      }, carouselList.value.length > 0 ? {
        b: common_vendor.f(carouselList.value, (item, index, i0) => {
          return {
            a: item.image,
            b: common_vendor.t(item.title),
            c: index
          };
        })
      } : {}, {
        c: services.value.length > 0
      }, services.value.length > 0 ? {
        d: common_vendor.f(services.value, (service, index, i0) => {
          return {
            a: service.image,
            b: common_vendor.t(service.name),
            c: common_vendor.t(service.price),
            d: index,
            e: common_vendor.o(($event) => goToServicePage(service), index)
          };
        })
      } : {}, {
        e: common_vendor.p({
          type: "location",
          size: "16",
          color: "#ff6b35"
        }),
        f: common_vendor.p({
          type: "phone",
          size: "16",
          color: "#ff6b35"
        }),
        g: common_vendor.p({
          type: "clock",
          size: "16",
          color: "#ff6b35"
        }),
        h: common_vendor.o(makeAppointment)
      });
    };
  }
});
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/index/index.js.map
