# BlockForge

从文本提示生成 Minecraft 风格 voxel 方块建筑蓝图。

BlockForge 是一个本地优先的 Minecraft 风格方块建筑蓝图生成器。当前 v0.1 版本使用规则生成和内置 preset 模板，后续可以扩展为真正的 AI 蓝图生成器。

[English README](./README.md)
| [使用手册](./docs/USER_MANUAL.zh-CN.md)

## 功能特性

- 5 个内置 voxel 建筑模板：中世纪塔楼、小木屋、地牢入口、石桥、像素雕像。
- 基于 React Three Fiber 的浏览器 3D 预览。
- 支持旋转、缩放、拖动查看模型。
- 支持导出当前 `VoxelModel` 为 JSON。
- 支持导出 Minecraft `.mcfunction` 命令文件。
- 清晰的 TypeScript voxel 数据结构和校验工具。
- 使用 Vitest 覆盖 preset、坐标边界、重复坐标、材质映射、渲染坐标和导出逻辑。

## Demo 截图

截图将在 v0.1 视觉整理后补充。

## 技术栈

- Next.js
- TypeScript
- Tailwind CSS
- Three.js
- React Three Fiber
- Drei
- Vitest
- pnpm

## 本地运行

```bash
pnpm install
pnpm dev
```

打开浏览器访问：

```text
http://localhost:3000
```

## 使用方式

1. 选择一个内置建筑模板。
2. 在 3D 预览区查看生成的 voxel 模型。
3. 使用鼠标旋转、缩放、拖动模型。
4. 点击 `Export JSON` 导出数据文件。
5. 点击 `Export .mcfunction` 导出 Minecraft 命令文件。
6. 可选：输入 prompt，更新页面中的本地提示状态。

## Minecraft Function 导出

BlockForge 可以把每个 voxel 方块导出为一行 Minecraft Java Edition 的 `setblock` 命令。

导出的 `.mcfunction` 文件适合作为 Minecraft Java Edition 命令和 datapack 工作流的起点。

示例命令：

```mcfunction
setblock ~ ~ ~ minecraft:stone_bricks replace
setblock ~1 ~0 ~2 minecraft:glass replace
```

注意：当前版本只导出 `.mcfunction` 文件。完整 datapack ZIP 导出会在后续版本中实现。

## 项目结构

```text
src/
├─ app/                 Next.js 页面和全局样式
├─ components/          UI 组件和 3D 预览组件
├─ lib/voxel/           voxel 类型、preset、校验、渲染和导出逻辑
├─ test/                Vitest 测试文件
└─ types/               共享 TypeScript 类型
```

## 常用命令

```bash
pnpm dev
pnpm build
pnpm test
pnpm lint
```

## Roadmap

- 完整 datapack ZIP 导出。
- `.schem` 导出。
- 方块贴图渲染。
- 使用 `InstancedMesh` 优化大模型渲染性能。
- prompt-to-structure 规则生成引擎。
- 接入真实 AI adapter，实现自然语言蓝图生成。
- 为 GitHub README 补充项目截图。

## 贡献

欢迎提交 issue 和 pull request。请尽量保持改动聚焦、类型清晰；如果改动 voxel 生成、导出或核心数据结构，请补充对应测试。

## License

MIT
