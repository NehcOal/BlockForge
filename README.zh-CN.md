# BlockForge

从文本提示生成 Minecraft 风格 voxel 方块建筑蓝图。

BlockForge 是一个本地优先的 Minecraft 风格方块建筑蓝图生成器。当前 v1.2.3
包含 Web 蓝图生成、3D 预览、导出链路、NeoForge Connector 游戏内建造流程，以及
Fabric / Forge Connector Alpha GUI Selector + Builder Wand + Ghost Preview + 生存材料成本闭环。

[English README](./README.md) | [使用手册](./docs/USER_MANUAL.zh-CN.md)

## v1.2.3 Fabric / Forge Survival Material Cost Parity

BlockForge v1.2.3-alpha.1 补齐 Fabric 和 Forge 的 Survival Material Cost Alpha parity。
NeoForge 仍然是当前功能最完整的 connector。Fabric 和 Forge 现在会统计蓝图材料需求、
显示缺失材料，并在生存模式材料不足时拒绝 build；材料足够时会在命令 build 或
Builder Wand build 前扣除玩家背包材料。

预期发布 jar：

- `blockforge-connector-neoforge-1.2.3-alpha.1.jar`
- `blockforge-connector-fabric-1.2.3-alpha.1.jar`
- `blockforge-connector-forge-1.2.3-alpha.1.jar`

## Loader 功能矩阵

| 功能 | NeoForge | Fabric Alpha | Forge Alpha |
|---|---|---|---|
| Blueprint v1/v2 加载 | 支持 | 支持 | 支持 |
| 安装示例蓝图 | 支持 | 支持 | 支持 |
| Reload/list/info/dryrun | 支持 | 支持 | 支持 |
| Build 命令 | 支持 | 支持 | 支持 |
| 旋转 | 支持 | 支持 | 支持 |
| Undo 方块 | 支持 | 支持 | 支持 |
| GUI Selector | 支持 | Alpha 支持 | Alpha 支持 |
| Builder Wand | 支持 | Alpha 支持 | Alpha 支持 |
| Ghost Preview | 支持 | Alpha 支持 | Alpha 支持 |
| 生存模式材料成本 | 支持 | Alpha 支持 | Alpha 支持 |
| Undo 材料返还 | 支持 | 不支持 | 不支持 |
| BlockEntity NBT undo | best effort 支持 | 不支持 | 不支持 |

Fabric 和 Forge 的 GUI Selector、Builder Wand、Ghost Preview 与生存材料成本仍是
Alpha。Ghost Preview 只显示随旋转变化的包围盒和地面 footprint；Fabric / Forge
现在会扣除生存模式材料，但 `/blockforge undo` 目前只恢复方块，不返还材料。

Fabric / Forge GUI + Builder Wand + Ghost Preview + 生存材料 Alpha 流程：

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge list
/blockforge gui
/blockforge materials selected
/blockforge wand
```

也可以按默认 `B` 键打开选择器。选择蓝图和旋转角度后点击 Select，手持 Builder Wand
看向方块时应显示 Ghost Preview 轮廓，右键生成建筑，然后执行 `/blockforge undo`
恢复方块。生存模式需要材料充足并会扣除材料；创造模式不消耗材料。

## v1.1.1 Fabric Connector Alpha

BlockForge v1.1.1 新增 `mod/fabric-connector`，这是 Fabric 1.21.1 的命令版
Alpha。它可以安装示例蓝图、reload 和 list Blueprint JSON、dryrun 构建计划、
在世界中 build 蓝图，并按玩家连续撤销最近的 Fabric build。

Fabric Alpha 复用 `mod/common` 中的蓝图解析、旋转和 build planning 数据。
v1.2.3 起 Fabric 已包含 GUI Selector、Builder Wand、Ghost Preview 与生存材料成本
Alpha。本阶段仍不包含材料返还或 BlockEntity NBT undo。NeoForge 仍然是当前功能最完整、
最稳定的 Connector 目标。

命令、安装和限制见：[Fabric Connector README](./mod/fabric-connector/README.md)。

## v1.1.2 Forge Connector Alpha

BlockForge v1.1.2 新增 `mod/forge-connector`，这是 Forge 1.21.1 的命令版
Alpha。它与 Fabric Alpha 对齐：安装示例蓝图、reload 和 list Blueprint JSON、
dryrun 构建计划、在世界中 build 蓝图，并按玩家连续撤销最近的 Forge build。

Forge Alpha 复用 `mod/common` 中的蓝图解析、旋转和 build planning 数据。
v1.2.3 起 Forge 已包含 GUI Selector、Builder Wand、Ghost Preview 与生存材料成本
Alpha。本阶段仍不包含材料返还或 BlockEntity NBT undo。NeoForge 仍然是当前功能最完整、
最稳定的 Connector 目标。

命令、安装和限制见：[Forge Connector README](./mod/forge-connector/README.md)。

## v1.1.0 Multi-loader 架构

BlockForge v1.1.0 开始进入多加载器架构阶段。NeoForge 1.21.1 仍然是当前稳定
Connector 目标；Fabric 1.21.1 和 Forge 1.21.1 支持已进入规划，但本版本不实现
Fabric / Forge 端功能。

本版本新增 `mod/common`，作为 loader-neutral 的 Java common core，用于复用蓝图解析、
旋转、build planning、材料数据、undo 记录、platform adapter 接口和工具类型。
NeoForge Connector 已在低风险位置复用 common core；命令注册、物品注册、GUI、网络、
Ghost Preview 渲染、config 注册、真实 world setBlock 和背包访问仍保留在 NeoForge 适配层。

架构计划见：[Multi-loader Plan](./docs/MULTILOADER_PLAN.md)。

## v1.0.1 Bug 修复

- 修复生存模式材料可能被多扣的问题：当部分目标方块因受保护、不可替换、超出世界高度或其他原因被跳过时，现在只按实际可放置方块计算材料消耗。
- 加固 Connector 蓝图解析：格式不正确但 JSON 语法合法的蓝图文件会被记录为加载 warning，不再让未捕获解析异常中断 reload。
- 增加 Connector 侧蓝图结构校验：检查尺寸必须为正数、方块坐标必须在声明尺寸内、坐标不能重复、方块引用必须存在于 palette。
- 新增 NeoForge Connector 单元测试，覆盖坏蓝图解析和部分放置时的材料统计。

## 功能特性

- 5 个内置 voxel 建筑模板：中世纪塔楼、小木屋、地牢入口、石桥、像素雕像。
- 基于 React Three Fiber 的浏览器 3D 预览。
- 支持旋转、缩放、拖动查看模型。
- 支持导出当前 `VoxelModel` 为 JSON。
- 支持导出 BlockForge Blueprint v1 JSON，用于未来 Mod Connector。
- 支持导出 BlockForge Blueprint v2 JSON，携带 Minecraft BlockState properties。
- 新增 NeoForge 1.21.1 BlockForge Connector MVP。
- 新增 Builder Wand MVP，可通过法杖放置已选择蓝图。
- 新增 `/blockforge undo`，可撤销最近一次 BlockForge 放置。
- 新增放置快照和 NeoForge common config 安全配置。
- 新增 Builder Wand Ghost Preview MVP 候选版。
- 新增游戏内 Blueprint Selector GUI MVP，可选择蓝图和旋转角度。
- 新增 `/blockforge gui` 和默认 `B` 键打开选择器。
- 新增材料需求统计。
- 新增生存模式材料检查与物品消耗。
- 新增材料事务，Undo 可返还生存模式已消耗材料。
- 创造模式会跳过材料消耗。
- 新增 Connector 示例蓝图和手动测试文档。
- 新增 Fabric Connector Alpha 命令闭环与 CI 构建。
- 新增 Forge Connector Alpha 命令闭环与 CI 构建。
- 支持导出 Minecraft `.mcfunction` 命令文件。
- 支持导出 Minecraft Java 1.21.1 Data Pack ZIP。
- 清晰的 TypeScript voxel 数据结构和校验工具。
- 使用 Vitest 覆盖 preset、坐标边界、重复坐标、材质映射、渲染坐标和导出逻辑。

## Demo 截图

![BlockForge 首页截图](./public/screenshots/blockforge-hero.png)

> 请将最新项目首图放在 `public/screenshots/blockforge-hero.png`。

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

## 游戏内最快开始

```text
1. 安装 BlockForge Connector Mod。
2. 执行 /blockforge examples install。
3. 执行 /blockforge reload。
4. 打开 /blockforge gui。
5. 选择一个蓝图。
6. 执行 /blockforge wand 获取法杖。
7. 右键预览并建造。
8. 使用 /blockforge undo 撤销。
```

完整安装步骤见：[安装指南](./docs/INSTALL.zh-CN.md)。

## 使用方式

1. 选择一个内置建筑模板。
2. 在 3D 预览区查看生成的 voxel 模型。
3. 使用鼠标旋转、缩放、拖动模型。
4. 点击 `导出 Blueprint JSON`，导出未来 BlockForge Mod Connector 可读取的蓝图协议文件。
5. 点击 `导出 Data Pack ZIP`。
6. 把 zip 复制到 `.minecraft/saves/<world>/datapacks`。
7. 执行 `/reload`。
8. 执行 `/function blockforge:build/<blueprint_id>`。
9. 点击 `Export JSON` 导出数据文件。
10. 点击 `Export .mcfunction` 导出 Minecraft 命令文件。
11. 可选：输入 prompt，更新页面中的本地提示状态。

## Blueprint 协议导出

BlockForge Blueprint v1 是简单 block id 协议。它保留原始 voxel 坐标，并通过
palette 把 BlockForge 方块类型映射到 Minecraft Java 方块 id。

Blueprint v2 增加 Minecraft BlockState 支持。palette entry 使用 `{ name,
properties }`，blocks 使用 `state` 字段引用 palette key。

字段契约见：[Blueprint Protocol](./docs/BLUEPRINT_PROTOCOL.md)。

## NeoForge Connector MVP

仓库已包含最小版 NeoForge 1.21.1 Mod Connector：

```text
mod/neoforge-connector
```

它会读取 Web 端导出的 Blueprint v1 JSON：

```text
.minecraft/config/blockforge/blueprints/
```

然后通过命令在游戏内生成建筑：

```mcfunction
/blockforge build <id>
/blockforge build <id> <x> <y> <z>
/blockforge build <id> rotate <0|90|180|270>
/blockforge build <id> at <x> <y> <z> rotate <0|90|180|270>
```

安装和命令详情见：[BlockForge Connector README](./mod/neoforge-connector/README.md)。

## Fabric Connector Alpha

仓库已新增 Fabric 1.21.1 Connector Alpha：

```text
mod/fabric-connector
```

构建：

```powershell
cd mod/fabric-connector
gradlew.bat build
```

Fabric jar 输出目录：

```text
mod/fabric-connector/build/libs/
```

最快命令闭环：

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge list
/blockforge dryrun tiny_platform
/blockforge build tiny_platform
/blockforge undo
```

Fabric 命令闭环实机测试已通过：examples install、reload、list、dryrun、build、
undo、`state_test_house rotate 90` 和无效 blueprint id 处理均已验证。

## Forge Connector Alpha

仓库已新增 Forge 1.21.1 Connector Alpha：

```text
mod/forge-connector
```

构建：

```powershell
cd mod/forge-connector
gradlew.bat build
```

Forge jar 输出目录：

```text
mod/forge-connector/build/libs/
```

最快命令闭环：

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge list
/blockforge dryrun tiny_platform
/blockforge build tiny_platform
/blockforge undo
```

Forge 命令闭环实机测试已通过：examples install、reload、list、dryrun、build、
undo、`state_test_house rotate 90` 和无效 blueprint id 处理均已验证。测试中发现
Forge undo 回滚门和火把时会产生掉落物，现已改为在快照恢复时抑制掉落。

实机测试建议先执行：

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge list
```

完整清单见：[Mod Connector 手动测试](./docs/MOD_CONNECTOR_TESTING.md)。

实机测试状态：已在 Minecraft Java Edition 1.21.1 + NeoForge 21.1.227 下通过，
已验证内置 `tiny_platform`、`small_test_house` 和 `medieval_tower` 示例。

Blueprint v2 实机测试状态：`state_test_house` 已通过，已验证 oak door
properties、wall torch facing，以及 `rotate 90` / `rotate 180` 旋转生成。

Builder Wand MVP 使用流程：

```mcfunction
/blockforge select state_test_house
/blockforge rotate 90
/blockforge wand
```

然后手持 Builder Wand 右键方块，蓝图会生成在被点击方块外侧。法杖需要权限等级
2，并带有 2 秒冷却。

撤销最近一次 BlockForge 放置：

```mcfunction
/blockforge undo list
/blockforge undo
/blockforge undo clear
```

v0.6.1 已通过 Gradle 构建，Minecraft 实机测试待进行。

Ghost Preview MVP 候选版：

```mcfunction
/blockforge select tiny_platform
/blockforge rotate 90
/blockforge wand
```

手持 Builder Wand 并看向一个方块时，客户端会在
`clickedPos.relative(clickedFace)` 位置绘制半透明包围盒和地面占位轮廓。
预览使用当前选中蓝图尺寸和旋转角度，不修改世界，也不替代服务端真实放置校验。
v0.7 已通过 Gradle 构建，Minecraft 实机测试待进行。

Blueprint Selector GUI MVP：

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge gui
```

也可以按默认 `B` 键打开选择器。玩家可以在 GUI 中选择蓝图，设置 `0°`、`90°`、
`180°` 或 `270°` 旋转角度，然后点击 Select。客户端只发送选择请求，服务端会
校验蓝图和旋转角度，校验通过后再同步给 Ghost Preview 和 Builder Wand。
v0.8 已通过 Gradle 构建，Minecraft 实机测试待进行。

材料需求 MVP：

```mcfunction
/blockforge materials selected
/blockforge materials tiny_platform
```

生存模式玩家在使用命令 build 或 Builder Wand 建造前，必须拥有足够材料。
材料充足时会从背包扣除对应物品；创造模式会跳过材料检查和消耗。
Undo 现在会恢复世界方块，并返还记录到的生存模式材料；如果玩家背包已满，
返还材料会掉落在玩家附近。v1.0.0-rc.1 保留 v0.9.1 已验证行为，并补充
release 打包、metadata、CI 和 common config 整理。当前 RC 已通过客户端启动和
Connector 核心流程烟测。

Connector common config：

```text
.minecraft/config/blockforge_connector-common.toml
```

该配置文件可调整安全限制、覆盖规则、Builder Wand 冷却、Undo 历史数量和生存模式材料需求。

## Minecraft Function 导出

BlockForge 可以把每个 voxel 方块导出为一行 Minecraft Java Edition 的 `setblock` 命令。

导出的 `.mcfunction` 文件适合作为 Minecraft Java Edition 命令和 datapack 工作流的起点。

示例命令：

```mcfunction
setblock ~ ~ ~ minecraft:stone_bricks replace
setblock ~1 ~0 ~2 minecraft:glass replace
```

注意：当前版本已经支持 `.mcfunction` 和 Data Pack ZIP。更完整的高级 datapack 工作流会在后续版本中增强。

## Data Pack ZIP 导出

BlockForge 可以导出可直接安装的 Minecraft Java 1.21.1 数据包。
生成的数据包包含一个 BlockForge function，用 `setblock` 命令放置当前选中的 voxel 模型。

生成的 zip 包含：

```text
pack.mcmeta
data/blockforge/function/build/<blueprint_id>.mcfunction
README.txt
```

使用方式：把 zip 复制到 `.minecraft/saves/<world>/datapacks`，执行 `/reload`，
然后运行 `/function blockforge:build/<blueprint_id>`。

## 项目结构

```text
src/
├─ app/                 Next.js 页面和全局样式
├─ components/          UI 组件和 3D 预览组件
├─ lib/voxel/           voxel 类型、preset、校验、渲染和导出逻辑
├─ test/                Vitest 测试文件
└─ types/               共享 TypeScript 类型
mod/
├─ common/              面向多加载器的 loader-neutral Java core
├─ fabric-connector/    Fabric 1.21.1 命令版 Connector Alpha
├─ forge-connector/     Forge 1.21.1 命令版 Connector Alpha
└─ neoforge-connector/  NeoForge 1.21.1 Mod Connector
examples/
└─ blueprints/          用于 Connector 测试的 Blueprint v1 示例
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
- Builder Wand 实机验证与 Ghost Preview。
- Ghost Preview 的完整碰撞和覆盖检查。
- Blueprint Selector 增加搜索、分页和缩略图。
- Undo 材料返还边界测试。
- 为门、流体、火把和多方块结构添加特殊材料成本规则。
- 改进 release artifact 自动发布。
- Blueprint v1/v2 schema 校验工具。
- Fabric / Forge Connector Alpha 后续 parity work。
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
