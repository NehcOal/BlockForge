# BlockForge 安装指南

本文档说明 BlockForge Web 端和 NeoForge Connector 候选版本的安装方式。

## 版本

- BlockForge Web：`1.0.0-rc.1`
- BlockForge Connector：`1.0.0-rc.1`
- Minecraft Java Edition：`1.21.1`
- NeoForge：`21.1.227`
- Java：`21`

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

## 安装 NeoForge 1.21.1

1. 安装 Minecraft Java Edition `1.21.1`。
2. 安装 Java `21`。
3. 安装 Minecraft `1.21.1` 对应的 NeoForge。
4. 创建或打开一个 NeoForge `1.21.1` 测试实例。

BlockForge Connector 已按 NeoForge `21.1.227` 验证。

## 安装 BlockForge Connector jar

1. 构建 Mod：

   ```bash
   cd mod/neoforge-connector
   ./gradlew build
   ```

   Windows：

   ```powershell
   cd mod/neoforge-connector
   .\gradlew.bat build
   ```

2. 从下面目录找到生成的 jar：

   ```text
   mod/neoforge-connector/build/libs/
   ```

3. 把 jar 放入 Minecraft 实例的 `mods` 文件夹。

## 蓝图目录

BlockForge Connector 会读取：

```text
.minecraft/config/blockforge/blueprints/
```

Mod 启动或执行 reload 时会自动创建该目录。

## 安装示例蓝图

进入游戏后执行：

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge list
```

## 使用 GUI

打开 Blueprint Selector：

```mcfunction
/blockforge gui
```

也可以按默认快捷键 `B`。

选择蓝图、选择旋转角度，然后点击 `Select`。

## 使用 Builder Wand

获取 Builder Wand：

```mcfunction
/blockforge wand
```

手持法杖，看向一个方块并右键。Ghost Preview 会显示将要放置的位置和范围。

## 生存模式材料

生存模式下，BlockForge 会在建造前检查材料。材料不足时拒绝建造；材料足够时，
建造开始后会扣除材料。

创造模式会跳过材料检查，不消耗材料。

## Undo

撤销最近一次 BlockForge 放置：

```mcfunction
/blockforge undo
```

Undo 会恢复原方块，并返还该次建造事务记录的生存模式材料。如果背包已满，
返还材料会掉落在玩家附近。

## Common Config

Connector 会生成 NeoForge common config：

```text
.minecraft/config/blockforge_connector-common.toml
```

里面包含最大建造方块数、Builder Wand 冷却、Undo 历史数量、是否允许覆盖非空气方块、
是否保护 BlockEntity、是否启用生存模式材料需求等配置。

默认值保持 v1.0 release candidate 前已验证的行为。v1.0 RC 已通过 Minecraft
客户端启动和 common config 注册后的 Connector 核心流程烟测。

## Release Artifacts

BlockForge release 应包含：

- GitHub tag 提供的 Web source release。
- `mod/neoforge-connector/build/libs/*.jar` 中的 Mod jar。
- `examples/blueprints/`。
- `docs/BLUEPRINT_PROTOCOL.md`。
- `docs/MOD_CONNECTOR_TESTING.md`。

GitHub Actions 会把 Connector jar 上传为 CI artifact。
