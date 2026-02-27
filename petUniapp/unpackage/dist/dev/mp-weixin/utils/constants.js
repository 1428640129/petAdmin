"use strict";
const APPOINTMENT_STATUS = {
  PENDING: "0",
  // 待确认
  CONFIRMED: "1",
  // 已确认
  IN_SERVICE: "2",
  // 服务中
  COMPLETED: "3",
  // 已完成
  CANCELLED: "4"
  // 已取消
};
const APPOINTMENT_STATUS_TEXT = {
  "0": "待处理",
  "1": "已确认",
  "2": "服务中",
  "3": "已完成",
  "4": "已取消"
};
exports.APPOINTMENT_STATUS = APPOINTMENT_STATUS;
exports.APPOINTMENT_STATUS_TEXT = APPOINTMENT_STATUS_TEXT;
//# sourceMappingURL=../../.sourcemap/mp-weixin/utils/constants.js.map
