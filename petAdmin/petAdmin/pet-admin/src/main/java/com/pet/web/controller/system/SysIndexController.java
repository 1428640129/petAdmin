package com.pet.web.controller.system;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pet.common.config.PetConfig;
import com.pet.common.core.controller.BaseController;
import com.pet.common.core.domain.AjaxResult;
import com.pet.common.utils.StringUtils;
import com.pet.business.service.IPetBathAppointmentService;
import com.pet.business.service.IPetBathOrderService;
import com.pet.business.service.IPetBathServiceService;
import com.pet.business.service.IPetBathUserService;
import com.pet.system.domain.PetBathAppointment;
import com.pet.system.domain.PetBathOrder;
import com.pet.system.domain.PetBathService;
import com.pet.system.domain.PetBathUser;
import com.pet.system.service.ISysUserService;
import java.util.Calendar;
import java.util.Date;

/**
 * 首页
 *
 * @author Pet
 */
@RestController
@RequestMapping("/system/index")
public class SysIndexController extends BaseController
{
    /** 系统基础配置 */
    @Autowired
    private PetConfig PetConfig;

    @Autowired
    private IPetBathAppointmentService appointmentService;

    @Autowired
    private IPetBathOrderService orderService;

    @Autowired
    private IPetBathServiceService serviceService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private IPetBathUserService bathUserService;

    /**
     * 访问首页，提示语
     */
    @RequestMapping("/")
    public String index()
    {
        return StringUtils.format("欢迎使用{}后台管理框架，当前版本：v{}，请通过前端地址访问。", PetConfig.getName(), PetConfig.getVersion());
    }

    /**
     * 获取首页统计数据
     */
    @PreAuthorize("@ss.hasPermi('system:index:statistics')")
    @GetMapping("/statistics")
    public AjaxResult getStatistics()
    {
        Map<String, Object> statistics = new HashMap<>();
        
        // 统计数据：项目数、待办事项、消息数
        Map<String, Object> headerStats = new HashMap<>();
        headerStats.put("projectCount", serviceService.selectBathServiceList(null).size());
        List<com.pet.system.domain.PetBathAppointment> appointments = appointmentService.selectBathAppointmentList(null);
        long todoCount = appointments.stream()
            .filter(apt -> "0".equals(apt.getStatus()))
            .count();
        headerStats.put("todoCount", todoCount);
        headerStats.put("messageCount", 5); // 消息数可以从通知服务获取
        statistics.put("headerStats", headerStats);
        
        // 卡片数据：预约数量、营业额、服务订单、已完成服务
        Map<String, Object> cardData = new HashMap<>();
        // 预约数量：所有预约总数
        long visitCount = appointments.size();
        cardData.put("visitCount", visitCount);
        
        List<com.pet.system.domain.PetBathOrder> orders = orderService.selectBathOrderList(null);
        // 营业额：已支付或已完成的订单金额总和
        double turnover = orders.stream()
            .filter(order -> "1".equals(order.getStatus()) || "3".equals(order.getStatus())) // 已支付或已完成
            .mapToDouble(order -> order.getTotalAmount() != null ? order.getTotalAmount().doubleValue() : 0)
            .sum();
        cardData.put("turnover", turnover);
        
        // 服务订单：所有订单总数
        cardData.put("downloadCount", orders.size());
        
        // 已完成服务：状态为"3"（已完成）的订单数
        long dealCount = orders.stream()
            .filter(order -> "3".equals(order.getStatus()))
            .count();
        cardData.put("dealCount", dealCount);
        statistics.put("cardData", cardData);
        
        // 图表数据：折线图（服务订单数、新增用户数）- 统计最近一个月按日期汇总的数据
        Map<String, Object> lineChart = new HashMap<>();
        
        // 生成最近30天的日期数组（MM-dd格式）
        List<String> dateList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
        Calendar cal = Calendar.getInstance();
        for (int i = 29; i >= 0; i--) {
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_MONTH, -i);
            dateList.add(dateFormat.format(cal.getTime()));
        }
        lineChart.put("xAxis", dateList.toArray(new String[0]));
        
        // 与 xAxis 对齐：30 个点对应「今天往前 29 天」至「今天」（与上面 dateList 循环一致）
        // 原先用「今天-30 天」作起点会导致 dayIndex 最大为 30，超出 [0,29]，当天数据落不到任何桶里
        Calendar periodStart = Calendar.getInstance();
        periodStart.add(Calendar.DAY_OF_MONTH, -29);
        periodStart.set(Calendar.HOUR_OF_DAY, 0);
        periodStart.set(Calendar.MINUTE, 0);
        periodStart.set(Calendar.SECOND, 0);
        periodStart.set(Calendar.MILLISECOND, 0);
        Date startDate = periodStart.getTime();
        
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 23);
        now.set(Calendar.MINUTE, 59);
        now.set(Calendar.SECOND, 59);
        now.set(Calendar.MILLISECOND, 999);
        Date endDate = now.getTime();
        
        // 统计每天的订单数（最近30天，每天一个数据点）
        int[] serviceOrders = new int[30];
        for (PetBathOrder order : orders) {
            if (order.getCreateTime() != null && 
                !order.getCreateTime().before(startDate) && 
                !order.getCreateTime().after(endDate)) {
                Calendar orderCal = Calendar.getInstance();
                orderCal.setTime(order.getCreateTime());
                orderCal.set(Calendar.HOUR_OF_DAY, 0);
                orderCal.set(Calendar.MINUTE, 0);
                orderCal.set(Calendar.SECOND, 0);
                orderCal.set(Calendar.MILLISECOND, 0);
                
                long daysDiff = (orderCal.getTimeInMillis() - periodStart.getTimeInMillis()) / (1000 * 60 * 60 * 24);
                int dayIndex = (int) daysDiff;
                if (dayIndex >= 0 && dayIndex < 30) {
                    serviceOrders[dayIndex]++;
                }
            }
        }
        lineChart.put("serviceOrders", serviceOrders);
        
        // 统计每天的新增用户数（最近30天，每天一个数据点）
        List<PetBathUser> bathUsers = bathUserService.selectPetBathUserList(null);
        int[] newUsers = new int[30];
        for (PetBathUser user : bathUsers) {
            if (user.getCreateTime() != null && 
                !user.getCreateTime().before(startDate) && 
                !user.getCreateTime().after(endDate)) {
                Calendar userCal = Calendar.getInstance();
                userCal.setTime(user.getCreateTime());
                userCal.set(Calendar.HOUR_OF_DAY, 0);
                userCal.set(Calendar.MINUTE, 0);
                userCal.set(Calendar.SECOND, 0);
                userCal.set(Calendar.MILLISECOND, 0);
                
                long daysDiff = (userCal.getTimeInMillis() - periodStart.getTimeInMillis()) / (1000 * 60 * 60 * 24);
                int dayIndex = (int) daysDiff;
                if (dayIndex >= 0 && dayIndex < 30) {
                    newUsers[dayIndex]++;
                }
            }
        }
        lineChart.put("newUsers", newUsers);
        statistics.put("lineChart", lineChart);
        
        // 饼图数据：按「预约里的服务名称」统计各洗护项目分布（避免多条不同服务因 serviceType 同为 0 都显示成「基础洗浴」）
        Map<String, Object> pieChart = new HashMap<>();
        List<PetBathService> services = serviceService.selectBathServiceList(null);
        Map<Long, String> serviceIdToName = new HashMap<>();
        for (PetBathService s : services)
        {
            if (s.getServiceId() != null)
            {
                String nm = s.getServiceName();
                serviceIdToName.put(s.getServiceId(), StringUtils.isNotEmpty(nm) ? nm : ("服务ID " + s.getServiceId()));
            }
        }
        Map<String, Long> nameCount = new HashMap<>();
        for (PetBathAppointment apt : appointments)
        {
            String name = apt.getServiceName();
            if (StringUtils.isEmpty(name) && apt.getServiceId() != null)
            {
                name = serviceIdToName.get(apt.getServiceId());
            }
            if (StringUtils.isEmpty(name))
            {
                name = "未分类";
            }
            nameCount.merge(name, 1L, Long::sum);
        }
        List<Map.Entry<String, Long>> sorted = nameCount.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .collect(Collectors.toList());
        final int maxSlices = 10;
        List<Map<String, Object>> pieData = new ArrayList<>();
        long otherSum = 0L;
        for (int i = 0; i < sorted.size(); i++)
        {
            Map.Entry<String, Long> e = sorted.get(i);
            if (i < maxSlices)
            {
                Map<String, Object> item = new HashMap<>();
                item.put("name", e.getKey());
                item.put("value", e.getValue().intValue());
                pieData.add(item);
            }
            else
            {
                otherSum += e.getValue();
            }
        }
        if (otherSum > 0L)
        {
            Map<String, Object> other = new HashMap<>();
            other.put("name", "其他");
            other.put("value", (int) otherSum);
            pieData.add(other);
        }
        pieChart.put("data", pieData.toArray());
        statistics.put("pieChart", pieChart);
        
        // 服务动态信息：最近的预约和订单
        List<Map<String, Object>> serviceNews = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // 获取最近的预约（按创建时间倒序，取前5条）
        List<PetBathAppointment> recentAppointments = appointmentService.selectBathAppointmentList(null).stream()
            .sorted((a, b) -> {
                if (a.getCreateTime() == null && b.getCreateTime() == null) return 0;
                if (a.getCreateTime() == null) return 1;
                if (b.getCreateTime() == null) return -1;
                return b.getCreateTime().compareTo(a.getCreateTime());
            })
            .limit(5)
            .collect(Collectors.toList());
        
        for (PetBathAppointment apt : recentAppointments) {
            Map<String, Object> news = new HashMap<>();
            String statusText = "0".equals(apt.getStatus()) ? "待确认" :
                               "1".equals(apt.getStatus()) ? "已确认" :
                               "2".equals(apt.getStatus()) ? "服务中" :
                               "3".equals(apt.getStatus()) ? "已完成" : "已取消";
            news.put("id", apt.getAppointmentId());
            news.put("content", String.format("新预约：%s 预约了 %s 服务，状态：%s", 
                apt.getPetName() != null ? apt.getPetName() : "客户", 
                apt.getServiceName() != null ? apt.getServiceName() : "宠物洗澡",
                statusText));
            news.put("time", apt.getCreateTime() != null ? sdf.format(apt.getCreateTime()) : "");
            // 获取用户头像
            if (apt.getUserId() != null) {
                PetBathUser user = bathUserService.selectPetBathUserById(apt.getUserId());
                if (user != null && user.getAvatar() != null) {
                    news.put("avatar", user.getAvatar());
                }
            }
            serviceNews.add(news);
        }
        
        // 获取最近的订单（按创建时间倒序，取前5条，与预约合并）
        List<PetBathOrder> recentOrders = orderService.selectBathOrderList(null).stream()
            .sorted((a, b) -> {
                if (a.getCreateTime() == null && b.getCreateTime() == null) return 0;
                if (a.getCreateTime() == null) return 1;
                if (b.getCreateTime() == null) return -1;
                return b.getCreateTime().compareTo(a.getCreateTime());
            })
            .limit(5)
            .collect(Collectors.toList());
        
        for (PetBathOrder order : recentOrders) {
            Map<String, Object> news = new HashMap<>();
            String statusText = "0".equals(order.getStatus()) ? "待支付" :
                               "1".equals(order.getStatus()) ? "已支付" :
                               "2".equals(order.getStatus()) ? "服务中" :
                               "3".equals(order.getStatus()) ? "已完成" : "已取消";
            news.put("id", order.getOrderId());
            news.put("content", String.format("新订单：订单号 %s，金额 ¥%.2f，状态：%s", 
                order.getOrderNo() != null ? order.getOrderNo() : order.getOrderId().toString(),
                order.getTotalAmount() != null ? order.getTotalAmount().doubleValue() : 0,
                statusText));
            news.put("time", order.getCreateTime() != null ? sdf.format(order.getCreateTime()) : "");
            // 获取用户头像（订单也关联了userId）
            if (order.getUserId() != null) {
                PetBathUser user = bathUserService.selectPetBathUserById(order.getUserId());
                if (user != null && user.getAvatar() != null) {
                    news.put("avatar", user.getAvatar());
                }
            }
            serviceNews.add(news);
        }
        
        // 按时间排序，取前5条
        serviceNews = serviceNews.stream()
            .sorted((a, b) -> {
                String timeA = (String) a.get("time");
                String timeB = (String) b.get("time");
                if (timeA == null && timeB == null) return 0;
                if (timeA == null) return 1;
                if (timeB == null) return -1;
                return timeB.compareTo(timeA);
            })
            .limit(5)
            .collect(Collectors.toList());
        
        statistics.put("serviceNews", serviceNews);
        
        return success(statistics);
    }
}
