declare namespace Api.Home {
  /** 首页统计数据 */
  interface Statistics {
    /** 头部统计数据 */
    headerStats: {
      projectCount: number;
      todoCount: number;
      messageCount: number;
    };
    /** 卡片数据 */
    cardData: {
      visitCount: number;
      turnover: number;
      downloadCount: number;
      dealCount: number;
    };
    /** 折线图数据 */
    lineChart: {
      xAxis: string[];
      serviceOrders: number[];
      newUsers: number[];
    };
    /** 饼图数据 */
    pieChart: {
      data: Array<{
        name: string;
        value: number;
      }>;
    };
    /** 服务动态信息 */
    serviceNews: Array<{
      id: number;
      content: string;
      time: string;
    }>;
  }
}

