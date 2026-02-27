# TabBar 图标说明

此目录需要放置底部导航栏的图标文件。

## 需要的图标文件

每个菜单需要两个图标（未选中和选中状态）：

1. **首页**
   - `home.png` - 未选中状态图标（已提供SVG源文件：`home.svg`）
   - `home-active.png` - 选中状态图标（已提供SVG源文件：`home-active.svg`）

2. **服务**
   - `service.png` - 未选中状态图标（已提供SVG源文件：`service.svg`）
   - `service-active.png` - 选中状态图标（已提供SVG源文件：`service-active.svg`）

3. **我的**
   - `my.png` - 未选中状态图标（已提供SVG源文件：`my.svg`）
   - `my-active.png` - 选中状态图标（已提供SVG源文件：`my-active.svg`）

## 图标规格

- 尺寸：建议 81px × 81px（小程序标准）
- 格式：PNG（支持透明背景）
- 颜色：
  - 未选中：灰色 (#707070)
  - 选中：沙橙色 (#F4A460)

## 重要提示

⚠️ **uni-app的tabBar只支持PNG格式，不支持SVG**

已为您创建了图标的SVG源文件：

**首页图标：**
- `home.svg` - 首页图标（未选中，灰色 #707070）
- `home-active.svg` - 首页图标（选中，橙色 #F4A460）

**服务图标：**
- `service.svg` - 服务图标（未选中，灰色 #707070）
- `service-active.svg` - 服务图标（选中，橙色 #F4A460）

**我的图标：**
- `my.svg` - 我的图标（未选中，灰色 #707070）
- `my-active.svg` - 我的图标（选中，橙色 #F4A460）

**请将这些SVG文件转换为PNG格式：**

### 转换方法：

1. **在线转换工具：**
   - https://convertio.co/zh/svg-png/
   - https://cloudconvert.com/svg-to-png
   - 上传SVG文件，设置尺寸为 81x81px，下载PNG文件

2. **使用设计工具：**
   - 使用 Figma、Sketch、Adobe Illustrator 等工具打开SVG
   - 导出为PNG格式，尺寸设置为 81x81px

3. **使用命令行工具（如果已安装ImageMagick）：**
   ```bash
   magick convert -background none -resize 81x81 home.svg home.png
   magick convert -background none -resize 81x81 home-active.svg home-active.png
   magick convert -background none -resize 81x81 service.svg service.png
   magick convert -background none -resize 81x81 service-active.svg service-active.png
   magick convert -background none -resize 81x81 my.svg my.png
   magick convert -background none -resize 81x81 my-active.svg my-active.png
   ```

转换完成后，将生成的PNG文件放在此目录下：
- `home.png` 和 `home-active.png`（首页图标）
- `service.png` 和 `service-active.png`（服务图标）
- `my.png` 和 `my-active.png`（我的图标）
