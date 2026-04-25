# AGENTS.md

## 项目定位

BlockForge 是一个 Minecraft 风格 voxel 建筑蓝图生成器。项目目标是做成一个可以展示在 GitHub 作品集里的开源项目，而不是一次性堆很多未完成的大功能。

## 工作原则

- 优先保证项目能运行、能测试、能构建。
- 每次修改后都要说明改了哪些文件。
- 不要引入不必要的大型依赖。
- 不要把核心逻辑全部写进页面组件。
- 新功能应尽量配套测试。
- 保持 TypeScript 类型清晰。
- UI 风格保持简洁、现代、适合截图展示。

## 常用命令

- 安装依赖：`pnpm install`
- 本地开发：`pnpm dev`
- 运行测试：`pnpm test`
- 构建项目：`pnpm build`
- 代码检查：`pnpm lint`

## 目录约定

- `src/app`：Next.js 页面
- `src/components`：React 组件
- `src/lib/voxel`：voxel 数据结构、preset 生成器、导出逻辑
- `src/types`：共享 TypeScript 类型
- `src/test` 或 `__tests__`：测试文件

## 完成标准

一个任务完成前必须确认：

- 代码可以运行。
- TypeScript 没有明显类型错误。
- 相关测试可以通过。
- README 或注释在必要时已更新。
- 没有把临时代码、调试输出、无用文件提交进项目。
