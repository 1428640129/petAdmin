/** Default theme settings */
export const themeSettings: App.Theme.ThemeSetting = {
  themeScheme: 'light',
  grayscale: false,
  colourWeakness: false,
  recommendColor: false,
  themeColor: '#F4A460', // 沙橙色/沙漠色 - 大漠戈壁风格，温暖而有力
  otherColor: {
    info: '#D2B48C', // 信息色跟随主色（因为 isInfoFollowPrimary 为 true）
    success: '#52c41a', // 成功色保持绿色
    warning: '#F4A460', // 警告色使用沙棕色
    error: '#f5222d' // 错误色保持红色
  },
  isInfoFollowPrimary: true,
  layout: {
    mode: 'vertical',
    scrollMode: 'content',
    reverseHorizontalMix: false
  },
  page: {
    animate: true,
    animateMode: 'fade-slide'
  },
  header: {
    height: 56,
    breadcrumb: {
      visible: true,
      showIcon: true
    },
    multilingual: {
      visible: false
    },
    globalSearch: {
      visible: true
    }
  },
  tab: {
    visible: true,
    cache: true,
    height: 44,
    mode: 'chrome'
  },
  fixedHeaderAndTab: true,
  sider: {
    inverted: false,
    width: 220,
    collapsedWidth: 64,
    mixWidth: 90,
    mixCollapsedWidth: 64,
    mixChildMenuWidth: 200
  },
  footer: {
    visible: true,
    fixed: false,
    height: 48,
    right: true
  },
  watermark: {
    visible: false,
    text: '宠浴管理系统',
    enableUserName: false
  },
  tokens: {
    light: {
      colors: {
        container: 'rgb(255, 255, 255)', // 白色容器背景
        layout: 'rgb(247, 250, 252)', // 浅灰白色背景 - 保持清爽
        inverted: 'rgb(139, 115, 85)', // 深棕色 - 用于深色模式
        'base-text': 'rgb(31, 31, 31)' // 深色文字 - 与浅色背景形成良好对比
      },
      boxShadow: {
        header: '0 1px 2px rgb(0, 21, 41, 0.08)',
        sider: '2px 0 8px 0 rgb(29, 35, 41, 0.05)',
        tab: '0 1px 2px rgb(0, 21, 41, 0.08)'
      }
    },
    dark: {
      colors: {
        container: 'rgb(28, 28, 28)',
        layout: 'rgb(18, 18, 18)',
        'base-text': 'rgb(224, 224, 224)'
      }
    }
  }
};

/**
 * Override theme settings
 *
 * If publish new version, use `overrideThemeSettings` to override certain theme settings
 */
export const overrideThemeSettings: Partial<App.Theme.ThemeSetting> = {};
