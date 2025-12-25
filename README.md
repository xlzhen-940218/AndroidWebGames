# AndroidWebGames

一个面向移动端的 Web 游戏集合与 Android 打包示例。

本仓库同时包含：

- 一个轻量级的网页小游戏集合，位于 `app/src/main/assets/webgames/`，包含主页、若干小游戏、样式与脚本。
- 一个 Android 示例工程，展示如何在 `WebView` 中加载本地或远端页面并处理基本的导航与回退逻辑。

## 项目结构（简要）

- `app/`：Android 应用模块。
- `app/src/main/assets/webgames/`：网页游戏目录，包含：
  - `index.html`：入口页面，展示“精选游戏”并支持搜索与视图切换。
  - `css/`、`js/`：样式与脚本文件。
  - `games/`：各独立游戏页面（如 `tuixiangzi.html`、`wuziqi.html`、`tanchishe.html`、`eluosifangkuai.html`）。
  - `images/`：游戏资源图片。

## 网页端说明（来自 `assets/webgames`）

这是一个适合本地或静态主机运行的小游戏集合，包含若干基于 HTML/CSS/JavaScript 的轻量级游戏，便于学习与演示。

已包含的游戏（简述）：
- 推箱子（`games/tuixiangzi.html`）：将箱子推到目标位置以胜利。
- 五子棋（`games/wuziqi.html`）：本地双方下棋并判定胜负。
- 贪吃蛇（`games/tanchishe.html`）：吃食物变长并尽量避免碰撞以获得更高分。
- 俄罗斯方块（`games/eluosifangkuai.html`）：通过消除行获取分数，避免堆到顶部。
- 美少女拼图（`games/pintu.html`）：拼图游戏

## 本项目的用户流程（业务逻辑）

- 启动时检测网络：有网络优先加载远端目录/运营页面，无网络则回退加载内置本地首页（`app/src/main/assets/webgames/index.html`）。
- 在本地首页中，用户可以浏览游戏、搜索、切换视图；点击游戏后会打开对应页面；非首页内容会在单独页面/Activity 中打开以便区分目录与详情。
- 导航：显示主页按钮以快速返回目录；物理返回键优先在 Web 历史中回退，若无历史则退出应用。

## 在本地预览网页端内容

- 直接打开 `index.html`（某些浏览器在 `file://` 协议下对本地文件有限制）。
- 推荐使用静态服务器以避免本地文件限制，例如：
```bash
python3 -m http.server 8000
```
然后访问 `http://localhost:8000/`。

或在 VS Code 中使用 `Live Server` 插件进行预览。

## 运行 Android 应用

- 在 Android Studio 中打开本项目并运行到模拟器或真机。
- 命令行构建：
```bash
./gradlew assembleDebug
```

## 在线演示

- 示例在线地址： https://webgames.codetools.fun/

## 贡献 & 许可

- 欢迎通过 Issue 或 Pull Request 提交改进、修复或新游戏。请在 PR 中说明变更点和测试方法。
- 仓库与 `app/src/main/assets/webgames/` 下包含 `LICENSE` 文件，请查看具体许可条款（例如 MIT）。

如果你希望我把 README 翻译为英文、或把 `assets/webgames` 中的外部资源（CDN 引用）迁移到本地以支持完全离线体验，我可以继续帮你实现这些改动。
