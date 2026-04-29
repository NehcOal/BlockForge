# BlockForge 安装指南

本文档说明 BlockForge Web 端，以及 NeoForge、Fabric、Forge 三端 Minecraft Java
Connector jar 的安装方式。

## 版本

- BlockForge Web：`1.6.0-alpha.1`
- Minecraft Java Edition：`1.21.1`
- Java：`21`
- NeoForge：`21.1.227`
- Fabric Loader：`0.19.2`
- Fabric API：`0.116.11+1.21.1`
- Forge：`52.1.14`

## 选择 Loader

NeoForge 是当前推荐的完整游戏内体验。Fabric 和 Forge 是带 GUI Selector Alpha、
Builder Wand Alpha、Ghost Preview Alpha 轮廓预览、生存材料成本 Alpha 与 Undo 材料返还 Alpha 的轻量 Alpha。
v1.3.5 已为三端补齐附近箱子取材 Alpha。v1.4.0 新增 Blueprint Pack 导入 / 导出
Alpha，并从 `config/blockforge/packs/` 加载蓝图包。v1.6.0 新增 Sponge `.schem`
导入 / 导出 Alpha，并从 `config/blockforge/schematics/` 加载 schematic。v1.6.0
实机回归仍待执行。

| Connector | 适合场景 | 当前状态 |
|---|---|---|
| NeoForge | GUI Selector、Builder Wand、Ghost Preview、生存材料、Undo 材料返还、附近箱子取材 Alpha | 功能最完整 |
| Fabric Alpha | 命令 reload/list/dryrun/build/undo、GUI Selector Alpha、Builder Wand Alpha、Ghost Preview Alpha、生存材料成本验证、Undo 材料返还、附近箱子取材 Alpha | Alpha |
| Forge Alpha | 命令 reload/list/dryrun/build/undo、GUI Selector Alpha、Builder Wand Alpha、Ghost Preview Alpha、生存材料成本验证、Undo 材料返还、附近箱子取材 Alpha | Alpha |

不要把多个 BlockForge connector jar 同时放进同一个 Minecraft 实例。请选择与当前
loader 匹配的 jar。

## 本地运行 Web 端

```bash
pnpm install
pnpm dev
```

打开：

```text
http://localhost:3000
```

选择 preset，在浏览器中 3D 预览，然后导出 `Blueprint JSON v2` 给 Connector 使用。

## 构建 Connector Jar

NeoForge：

```bash
cd mod/neoforge-connector
./gradlew build
```

Fabric：

```bash
cd mod/fabric-connector
./gradlew build
```

Forge：

```bash
cd mod/forge-connector
./gradlew build
```

Windows 用户在对应目录执行 `gradlew.bat build`。

预期 release jar 名称：

```text
mod/neoforge-connector/build/libs/blockforge-connector-neoforge-1.6.0-alpha.1.jar
mod/fabric-connector/build/libs/blockforge-connector-fabric-1.6.0-alpha.1.jar
mod/forge-connector/build/libs/blockforge-connector-forge-1.6.0-alpha.1.jar
```

把对应 loader 的 jar 放入 Minecraft 实例的 `mods` 文件夹。

## 蓝图目录

三端 connector 都读取：

```text
.minecraft/config/blockforge/blueprints/
```

Mod 启动或执行 reload 时会自动创建该目录。

## 蓝图包目录

三端也会读取 Blueprint Pack zip：

```text
.minecraft/config/blockforge/packs/
```

支持：

```text
*.blockforgepack.zip
*.zip
```

常用命令：

```mcfunction
/blockforge packs validate
/blockforge packs list
/blockforge packs blueprints starter_buildings
/blockforge reload
```

Pack 蓝图 id 使用 `packId/blueprintId`，例如：

```mcfunction
/blockforge select starter_buildings/tiny_platform
```

## Schematic 目录

三端还会读取 Sponge `.schem` v3 文件：

```text
.minecraft/config/blockforge/schematics/
```

常用命令：

```mcfunction
/blockforge schematics validate
/blockforge schematics reload
/blockforge schematics list
/blockforge select schem/tiny_platform
```

## 安装示例蓝图

进入游戏后执行：

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge list
```

## 基础命令流程

NeoForge、Fabric Alpha、Forge Alpha 都支持这组命令：

```mcfunction
/blockforge folder
/blockforge examples list
/blockforge examples install
/blockforge reload
/blockforge list
/blockforge info tiny_platform
/blockforge dryrun tiny_platform
/blockforge materials tiny_platform
/blockforge build tiny_platform
/blockforge undo
/blockforge select tiny_platform
/blockforge materials selected
/blockforge rotate 90
/blockforge wand
/blockforge build state_test_house rotate 90
/blockforge undo
```

## NeoForge 完整体验

打开 Blueprint Selector：

```mcfunction
/blockforge gui
```

也可以按默认快捷键 `B`。

获取 Builder Wand：

```mcfunction
/blockforge wand
```

手持法杖，看向一个方块并右键。Ghost Preview 会显示将要放置的位置和范围。

生存模式下，NeoForge 会检查所需材料。Undo 会恢复方块，并返还该次建造事务记录的
生存模式材料。

## Fabric / Forge GUI + Wand + Ghost Preview + 材料 Alpha

Fabric 和 Forge 可以通过以下命令打开 Blueprint Selector：

```mcfunction
/blockforge gui
```

也可以按默认 `B` 键打开。Alpha GUI 会从服务端同步蓝图列表，让玩家选择蓝图和
旋转角度，并更新 Builder Wand 使用的同一份选择状态。

手持 Builder Wand 看向方块时会显示 Ghost Preview Alpha 轮廓。它只渲染包围盒和
地面 footprint，不会修改世界。

生存模式下，Fabric 和 Forge 会在命令 build 或 Builder Wand build 前检查蓝图所需材料。
材料不足会拒绝 build；材料足够会先扣除背包材料再放置。创造模式不消耗材料。
Undo 会恢复方块并返还记录下来的生存模式材料；背包满时，溢出的返还材料会掉落在玩家附近。

## Fabric / Forge Alpha 限制

Fabric 和 Forge Alpha 支持命令 build、Builder Wand Alpha 放置和方块 undo。
Undo 材料返还是 Alpha；它们暂不支持 BlockEntity NBT undo、附近箱子取材、配方替代或 GUI 材料图标。
材料报告请使用 `/blockforge materials <id>` 或 `/blockforge materials selected`；
GUI 材料摘要计划后续补齐。

## Release Artifacts

BlockForge v1.6.0-alpha.1 release 应包含：

- GitHub tag 提供的 Web source release。
- `blockforge-connector-neoforge-1.6.0-alpha.1.jar`
- `blockforge-connector-fabric-1.6.0-alpha.1.jar`
- `blockforge-connector-forge-1.6.0-alpha.1.jar`
- `examples/blueprints/`
- `docs/BLUEPRINT_PROTOCOL.md`
- `docs/MOD_CONNECTOR_TESTING.md`
- `docs/RELEASE_NOTES_TEMPLATE.md`

GitHub Actions 会把三个 Connector jar 分别上传为 CI artifact。
## v1.5.0 保护区域

服务器管理员可以按需编辑：

```text
config/blockforge/protection-regions.json
```

该文件会在 connector 首次启动或执行 protection reload 时自动创建。被保护区域拒绝的
build 会在材料检查和扣除前停止。
# v4.3.0-beta.1 说明

- 预期 jar 为 `blockforge-connector-neoforge-4.3.0-beta.1.jar`、
  `blockforge-connector-fabric-4.3.0-beta.1.jar`、
  `blockforge-connector-forge-4.3.0-beta.1.jar`。
- `/blockforge gui` 会打开带搜索、分页、来源过滤、warning 过滤、排序、来源标签和
  warning badge 的 Alpha 选择器。
- 本 Alpha 仍待 Minecraft 实机回归。
