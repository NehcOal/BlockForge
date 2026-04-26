# BlockForge Connector Manual Testing

This checklist prepares the NeoForge Connector for real Minecraft validation.
Passing `gradlew build` confirms compilation only; it does not replace in-game
testing.

Fabric and Forge Alpha are also covered here as separate checklists. NeoForge
remains the full-featured Connector; Fabric and Forge now include GUI Selector,
Builder Wand, Ghost Preview, and Survival Material Cost Alpha support. Fabric
and Forge still intentionally do not cover material refunds or BlockEntity NBT
undo.

## v1.2.3 Fabric / Forge Survival Material Cost Alpha Checklist

Release version:

```text
1.2.3-alpha.1
```

Expected release jars:

```text
mod/neoforge-connector/build/libs/blockforge-connector-neoforge-1.2.3-alpha.1.jar
mod/fabric-connector/build/libs/blockforge-connector-fabric-1.2.3-alpha.1.jar
mod/forge-connector/build/libs/blockforge-connector-forge-1.2.3-alpha.1.jar
```

Recommended Fabric and Forge preview test flow:

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge select tiny_platform
/blockforge materials selected
/blockforge wand
```

Recommended survival cost manual test:

1. Run `/blockforge examples install`.
2. Run `/blockforge reload`.
3. Run `/blockforge select tiny_platform`.
4. Run `/blockforge materials selected`.
5. Run `/blockforge wand`.
6. In creative mode, build with the Builder Wand; it should consume no materials.
7. Switch to survival mode.
8. Clear inventory.
9. Run `/blockforge materials selected`; it should show missing stone bricks.
10. Try the Builder Wand build; it should be rejected.
11. Give the required stone bricks.
12. Build with the Builder Wand; it should succeed and consume materials.
13. Run `/blockforge undo`; it should restore blocks but not refund materials.

Expected result:

- `/blockforge materials <id>` and `/blockforge materials selected` show
  required items, available items, missing item types, and missing materials.
- Creative mode prints that no materials were consumed.
- Survival mode rejects builds when required materials are missing.
- Survival mode consumes required inventory items before command or Builder Wand
  placement.
- Adventure and Spectator mode builds are rejected by the Alpha material gate.
- `/blockforge undo` restores the latest placement after building; repeated
  calls should walk back through earlier placements for the same player.
- Fabric / Forge v1.2.0 through v1.2.3 cumulative manual smoke testing was run
  on 2026-04-26 in Fabric and Forge development clients.

Manual result notes from 2026-04-26:

- Fabric: GUI selection, Ghost Preview outline, Builder Wand placement, creative
  material bypass, and repeated placement/undo flow were exercised.
- Forge: GUI selection, Builder Wand placement, creative material bypass, and
  placement/undo flow were exercised.
- Forge Ghost Preview initially rendered as a skewed/slanted line box. Fixed by
  removing the deprecated event pose matrix from the Forge preview renderer and
  rendering the line box camera-relative like Fabric.
- Fabric and Forge undo initially only kept one snapshot per player. Fixed by
  replacing the single latest snapshot with a 20-entry per-player undo history.
- Post-fix Fabric, Forge, and NeoForge Gradle builds passed.
- Targeted survival missing-material rejection and survival material consumption
  retesting is still recommended before a public v1.2.3 release.

## v1.2.2 Fabric / Forge Ghost Preview Alpha Checklist

Release version:

```text
1.2.2-alpha.1
```

Expected release jars:

```text
mod/neoforge-connector/build/libs/blockforge-connector-neoforge-1.2.2-alpha.1.jar
mod/fabric-connector/build/libs/blockforge-connector-fabric-1.2.2-alpha.1.jar
mod/forge-connector/build/libs/blockforge-connector-forge-1.2.2-alpha.1.jar
```

Recommended Fabric and Forge preview test flow:

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge gui
/blockforge selected
/blockforge wand
```

Select a blueprint and rotation in the GUI, hold the Builder Wand, and look at a
block.

Expected result:

- A Ghost Preview outline appears only while holding the Builder Wand with a
  selected blueprint.
- The preview base position is the looked-at block plus the clicked side.
- The bounding box uses the selected blueprint size and current rotation.
- The footprint appears on the ground plane of the preview volume.
- Valid previews render cyan/green; height-invalid previews render red.
- Looking away from blocks hides the preview.
- Right-click build still goes through the server Builder Wand placement path.
- `/blockforge undo` restores the latest placement after building; repeated
  calls should walk back through earlier placements for the same player.
- No world blocks are modified by the preview itself.
- Fabric / Forge Ghost Preview manual Minecraft testing is pending.

## v1.2.1 Fabric / Forge GUI Selector Alpha Checklist

Release version:

```text
1.2.1-alpha.1
```

Expected release jars:

```text
mod/neoforge-connector/build/libs/blockforge-connector-neoforge-1.2.1-alpha.1.jar
mod/fabric-connector/build/libs/blockforge-connector-fabric-1.2.1-alpha.1.jar
mod/forge-connector/build/libs/blockforge-connector-forge-1.2.1-alpha.1.jar
```

Recommended Fabric and Forge GUI test flow:

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge list
/blockforge gui
/blockforge selected
/blockforge wand
```

Also test the default `B` key from a player client.

Expected result:

- `/blockforge gui` opens the Blueprint Selector on Fabric and Forge clients.
- Pressing `B` opens the same selector unless the key is rebound or conflicts.
- The GUI requests a server blueprint list on open.
- The left list shows loaded blueprint name/id/size/block count/schema version.
- The right panel shows details and rotation buttons for `0°`, `90°`, `180°`, and `270°`.
- Clicking Select sends a server-validated selection request.
- `/blockforge selected` matches the GUI-selected blueprint and rotation.
- Builder Wand placement uses the GUI-selected blueprint and rotation.
- `/blockforge undo` restores the latest GUI-selected wand placement.
- If no blueprints are loaded, the GUI shows the examples install/reload hint.
- Fabric / Forge GUI manual Minecraft testing is pending.

## v1.2.0 Fabric / Forge Builder Wand Alpha Checklist

Release version:

```text
1.2.0-alpha.1
```

Expected release jars:

```text
mod/neoforge-connector/build/libs/blockforge-connector-neoforge-1.2.0-alpha.1.jar
mod/fabric-connector/build/libs/blockforge-connector-fabric-1.2.0-alpha.1.jar
mod/forge-connector/build/libs/blockforge-connector-forge-1.2.0-alpha.1.jar
```

Recommended Fabric and Forge wand test flow:

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge list
/blockforge select tiny_platform
/blockforge selected
/blockforge rotate 90
/blockforge wand
```

Then hold the Builder Wand, right-click a block, and run:

```mcfunction
/blockforge undo
```

Expected result:

- `/blockforge wand` gives `blockforge_connector:builder_wand`.
- Right-click placement uses the selected blueprint and rotation.
- Placement happens at the clicked block plus clicked side.
- Output includes the same placed/skipped statistics as command builds.
- `/blockforge undo` restores the latest wand placement.
- Repeated right-clicks within 2 seconds are blocked by the wand cooldown.
- Command builds are not throttled by the wand cooldown.
- Fabric / Forge Builder Wand manual Minecraft testing is pending.

## v1.1.3 Multi-loader Alpha Checklist

Release version:

```text
1.1.3-alpha.1
```

Expected release jars:

```text
mod/neoforge-connector/build/libs/blockforge-connector-neoforge-1.1.3-alpha.1.jar
mod/fabric-connector/build/libs/blockforge-connector-fabric-1.1.3-alpha.1.jar
mod/forge-connector/build/libs/blockforge-connector-forge-1.1.3-alpha.1.jar
```

Required build checks:

```powershell
pnpm lint
pnpm test
pnpm build
cd mod/neoforge-connector
gradlew.bat build
cd ..\fabric-connector
gradlew.bat build
cd ..\forge-connector
gradlew.bat build
```

Manual testing status for v1.1.3:

- NeoForge remains the recommended full-experience Connector.
- Fabric Alpha command-loop testing passed in v1.1.1.
- Forge Alpha command-loop testing passed in v1.1.2.
- v1.1.3 is a stabilization and packaging pass; it does not add new gameplay
  features to Fabric or Forge.

## 1. Environment Requirements

- Minecraft Java Edition `1.21.1`
- NeoForge `21.1.227`
- Java `21`

Fabric Alpha requirements:

- Minecraft Java Edition `1.21.1`
- Fabric Loader `0.19.2`
- Fabric API `0.116.11+1.21.1`
- Java `21`

Forge Alpha requirements:

- Minecraft Java Edition `1.21.1`
- Forge `52.1.14`
- Java `21`

## Fabric Alpha Build And Smoke Checklist

From `mod/fabric-connector`:

```bash
./gradlew build
```

On Windows:

```powershell
gradlew.bat build
```

The jar is generated in:

```text
mod/fabric-connector/build/libs/
```

Recommended first Fabric in-game test:

```mcfunction
/blockforge folder
/blockforge examples list
/blockforge examples install
/blockforge reload
/blockforge list
/blockforge info tiny_platform
/blockforge dryrun tiny_platform
/blockforge build tiny_platform
/blockforge undo
```

Also test coordinate and rotation builds:

```mcfunction
/blockforge build tiny_platform 0 80 0
/blockforge build state_test_house rotate 90
```

Expected Fabric Alpha result:

- Commands register under `/blockforge`.
- Example blueprints install without overwriting existing files.
- Reload reads `*.blueprint.json` and `*.json` from `.minecraft/config/blockforge/blueprints/`.
- `dryrun` reports schema version, size, block count, palette count, build plan validity, and skipped counts.
- `build` places valid blocks and skips invalid/out-of-world entries.
- `undo` restores the latest Fabric build for the current player.

Known Fabric command-loop Alpha limits:

- GUI exists as of v1.2.1 Alpha; this command-loop checklist does not cover it.
- No Ghost Preview.
- No survival material cost or inventory mutation.
- No material refund.
- No BlockEntity NBT snapshot or restore.
- Undo is in-memory and stores up to 20 Fabric builds per player.

Fabric v1.1.1 manual Minecraft test status: passed for the command-loop Alpha.

Verified Fabric commands and behaviors:

- `/blockforge examples install`
- `/blockforge reload`
- `/blockforge list`
- `/blockforge dryrun tiny_platform`
- `/blockforge build tiny_platform`
- `/blockforge undo`
- `/blockforge build state_test_house rotate 90`
- `/blockforge undo`
- Invalid blueprint id handling does not crash.

## Forge Alpha Build And Smoke Checklist

From `mod/forge-connector`:

```bash
./gradlew build
```

On Windows:

```powershell
gradlew.bat build
```

The jar is generated in:

```text
mod/forge-connector/build/libs/
```

Recommended first Forge in-game test:

```mcfunction
/blockforge folder
/blockforge examples list
/blockforge examples install
/blockforge reload
/blockforge list
/blockforge info tiny_platform
/blockforge dryrun tiny_platform
/blockforge build tiny_platform
/blockforge undo
```

Also test coordinate and rotation builds:

```mcfunction
/blockforge build tiny_platform 0 80 0
/blockforge build state_test_house rotate 90
```

Expected Forge Alpha result:

- Commands register under `/blockforge`.
- Example blueprints install without overwriting existing files.
- Reload reads `*.blueprint.json` and `*.json` from `.minecraft/config/blockforge/blueprints/`.
- `dryrun` reports schema version, size, block count, palette count, build plan validity, and skipped counts.
- `build` places valid blocks and skips invalid/out-of-world entries.
- `undo` restores the latest Forge build for the current player.

Known Forge command-loop Alpha limits:

- GUI exists as of v1.2.1 Alpha; this command-loop checklist does not cover it.
- No Ghost Preview.
- No survival material cost or inventory mutation.
- No material refund.
- No BlockEntity NBT snapshot or restore.
- Undo is in-memory and stores up to 20 Forge builds per player.

Forge v1.1.2 manual Minecraft test status: passed for the command-loop Alpha.

Verified Forge commands and behaviors:

- `/blockforge examples install`
- `/blockforge reload`
- `/blockforge list`
- `/blockforge dryrun tiny_platform`
- `/blockforge build tiny_platform`
- `/blockforge undo`
- `/blockforge build state_test_house rotate 90`
- `/blockforge undo`
- Invalid blueprint id handling does not crash.

Observed Forge undo issue and fix:

- Initial `state_test_house` undo restored the block count but allowed attached
  door and torch states to drop items during rollback.
- Forge undo now restores snapshot block states with drop suppression.
- Forge Alpha still does not refund materials; that remains a NeoForge full
  Connector feature.

## 2. Build The Mod

From `mod/neoforge-connector`:

```bash
./gradlew build
```

On Windows:

```powershell
gradlew.bat build
```

The jar is generated in:

```text
mod/neoforge-connector/build/libs/
```

## 3. Install The Mod

Copy the generated jar into the test instance `mods` folder.

Example:

```text
.minecraft/mods/blockforge-connector-neoforge-1.2.3-alpha.1.jar
```

## 4. Install Example Blueprints

Enter a test world and run:

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge list
```

The command copies bundled example files into:

```text
.minecraft/config/blockforge/blueprints/
```

## 5. Minimum Placement Test

```mcfunction
/blockforge build tiny_platform
```

Expected result:

- A 3 x 1 x 3 platform appears at the player's block position.
- The command reports placed blocks and skipped counts.

## 6. Coordinate Placement Test

```mcfunction
/blockforge build tiny_platform 0 80 0
/blockforge build state_test_house
/blockforge build state_test_house rotate 90
```

Expected result:

- The platform is placed at world position `0 80 0`.
- No centering, rotation, or mirroring is applied.

## 7. Medium Blueprint Test

```mcfunction
/blockforge build small_test_house
```

Expected result:

- A small complete-block house is placed.
- The sample only uses simple blocks: stone bricks, oak planks, oak logs, and glass.

## 8. Larger Preset Test

```mcfunction
/blockforge build medieval_tower
```

Expected result:

- A compact medieval tower sample is placed.
- This test is larger than the platform and house samples, but still below the safety limit.

## 9. Dry Run Test

```mcfunction
/blockforge dryrun medieval_tower
```

Expected result:

- The command prints blueprint id, name, size, total blocks, palette entries,
  estimated placed blocks, missing palette count, invalid block id count, and
  max block limit status.
- No blocks are placed.

## 10. Acceptance Criteria

- `/blockforge` command registers.
- `/blockforge folder` shows the blueprint directory.
- `/blockforge examples list` lists built-in examples.
- `/blockforge examples install` copies example files without overwriting existing files.
- `/blockforge reload` loads blueprint files.
- `/blockforge list` lists loaded blueprints.
- `/blockforge dryrun <id>` does not modify the world.
- `/blockforge build <id>` places blocks.
- Invalid block ids do not crash the game.
- Out-of-world Y coordinates do not crash the game.
- Blueprints over the max block limit are rejected.

## 11. Known Limits

- Block states are not supported yet.
- Rotation and mirroring are not supported yet.
- Undo is not supported yet.
- GUI selection is not supported yet.
- Ghost Preview is not supported yet.
- The connector uses `defaultBlockState()` for all valid Minecraft block ids.

## 12. Manual Test Result

Status: passed on Minecraft Java Edition `1.21.1` with NeoForge `21.1.227`.

Verified commands:

- `/blockforge examples install`
- `/blockforge reload`
- `/blockforge list`
- `/blockforge build tiny_platform`
- `/blockforge build small_test_house`
- `/blockforge build medieval_tower`
- `/blockforge build tiny_platform 0 80 0`

Observed placement results:

- `tiny_platform`: placed `9` blocks.
- `small_test_house`: placed `162` blocks.
- `medieval_tower`: placed `229` blocks.

v0.5 adds `state_test_house` for Blueprint v2 BlockState testing.

Observed skipped counts:

- `missingPalette=0`
- `invalidBlockId=0`
- `outOfWorld=0`

Manual test note: commands registered, examples installed, reload loaded all
three blueprints, list displayed blueprint metadata, and build commands placed
the expected structures without crashes.

## 13. 中文实机测试结果

状态：已在 Minecraft Java Edition `1.21.1` + NeoForge `21.1.227` 环境下通过。

已验证命令：

- `/blockforge examples install`
- `/blockforge reload`
- `/blockforge list`
- `/blockforge build tiny_platform`
- `/blockforge build small_test_house`
- `/blockforge build medieval_tower`
- `/blockforge build tiny_platform 0 80 0`

实测放置结果：

- `tiny_platform`：成功放置 `9` 个方块。
- `small_test_house`：成功放置 `162` 个方块。
- `medieval_tower`：成功放置 `229` 个方块。

实测跳过统计：

- `missingPalette=0`
- `invalidBlockId=0`
- `outOfWorld=0`

测试结论：命令成功注册，内置示例可安装，`reload` 能加载全部 3 个蓝图，
`list` 能展示蓝图信息，`build` 能正常放置结构，测试过程中未出现崩溃。

## 14. Blueprint v2 / BlockState / Rotation Test Result

Status: passed on Minecraft Java Edition `1.21.1` with NeoForge `21.1.227`.

Verified commands:

- `/blockforge examples install`
- `/blockforge reload`
- `/blockforge list`
- `/blockforge dryrun state_test_house`
- `/blockforge build state_test_house`
- `/blockforge build state_test_house rotate 90`
- `/blockforge build state_test_house at 0 80 0 rotate 180`

Observed placement result:

- `state_test_house`: placed `116` blocks.
- `appliedProperties=10`
- `missingPalette=0`
- `invalidBlockId=0`
- `invalidProperties=0`
- `outOfWorld=0`

Manual test note: Blueprint v2 loaded successfully, oak door properties were
applied, wall torch facing properties were applied, and rotated builds placed
without crashes.

## 15. 中文 v0.5 实机测试结果

状态：已在 Minecraft Java Edition `1.21.1` + NeoForge `21.1.227` 环境下通过。

已验证内容：

- Blueprint v2 示例 `state_test_house` 可加载。
- `oak_door` 的 `facing`、`half`、`hinge`、`open` properties 可应用。
- `wall_torch` 的 `facing` property 可应用。
- `/blockforge build state_test_house` 可正常生成。
- `/blockforge build state_test_house rotate 90` 可正常生成旋转版本。
- `/blockforge build state_test_house at 0 80 0 rotate 180` 可在指定坐标生成旋转版本。

实测放置结果：

- `state_test_house`：成功放置 `116` 个方块。
- `appliedProperties=10`
- `missingPalette=0`
- `invalidBlockId=0`
- `invalidProperties=0`
- `outOfWorld=0`

测试结论：Blueprint v2、BlockState properties、基础旋转已经完成游戏内闭环验证。

## 16. Builder Wand MVP Test Plan

Build status: Gradle build passed for v0.6. Minecraft manual testing is pending.

Recommended test flow:

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge list
/blockforge select state_test_house
/blockforge selected
/blockforge rotate 90
/blockforge wand
```

Then hold the Builder Wand and right-click the side or top of a block.

Expected result:

- The selected blueprint is placed at `clickedPos.relative(clickedFace)`.
- Rotation follows the player's `/blockforge rotate` setting.
- Output includes placed blocks, skipped missing palette, skipped invalid block ids,
  skipped invalid properties, skipped out-of-world, applied properties, and total blocks.
- Repeated right-clicks within 2 seconds show a cooldown message.
- Players without permission level 2 cannot place with the wand.

Known Builder Wand MVP limits:

- No Ghost Preview.
- No GUI.
- Selection state is in-memory only.
- No undo.
- No material cost.
- No client-side preview.

## 17. 中文 Builder Wand MVP 测试计划

构建状态：v0.6 已通过 Gradle build，Minecraft 实机测试待进行。

推荐测试流程：

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge list
/blockforge select state_test_house
/blockforge selected
/blockforge rotate 90
/blockforge wand
```

然后手持 Builder Wand 右键一个方块。

预期结果：

- 蓝图生成在 `clickedPos.relative(clickedFace)` 位置。
- 旋转角度使用 `/blockforge rotate` 设置。
- 输出包含 placed blocks、missing palette、invalid block ids、invalid properties、
  out-of-world、applied properties 和 total blocks。
- 2 秒内连续右键会提示冷却。
- 权限等级不足 2 的玩家不能使用法杖生成建筑。

## 18. Undo And Safety Test Plan

Build status: Gradle build passed for v0.6.1. Minecraft manual testing is pending.

Recommended command placement undo flow:

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge select tiny_platform
/blockforge wand
```

Hold the Builder Wand, right-click a block, then run:

```mcfunction
/blockforge undo list
/blockforge undo
```

Expected result:

- The latest Builder Wand placement is listed.
- `/blockforge undo` restores the previous blocks.
- The command reports restored block count and blueprint id.

Recommended rotated placement undo flow:

```mcfunction
/blockforge select state_test_house
/blockforge rotate 90
```

Right-click with the Builder Wand, then run:

```mcfunction
/blockforge undo list
/blockforge undo
/blockforge undo clear
```

Expected result:

- Undo snapshots are stored per player.
- Command builds and Builder Wand builds both create undo snapshots when blocks are placed.
- `protectBlockEntities=true` skips BlockEntity targets and reports `protected`.
- `allowReplaceNonAir=false`, when enabled in code, skips non-air non-replaceable targets and reports `nonReplaceable`.

## 19. 中文 Undo 与安全测试计划

构建状态：v0.6.1 已通过 Gradle build。Builder Wand 放置流程已完成 Minecraft
实机验证；Undo 命令本身仍待单独验证。

推荐测试流程：

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge select tiny_platform
/blockforge wand
```

手持 Builder Wand 右键生成后执行：

```mcfunction
/blockforge undo list
/blockforge undo
```

预期结果：

- 能看到最近一次法杖放置快照。
- `/blockforge undo` 能恢复放置前的方块。
- 输出包含 restored blocks 和 blueprint id。

旋转蓝图测试：

```mcfunction
/blockforge select state_test_house
/blockforge rotate 90
```

右键生成后执行：

```mcfunction
/blockforge undo list
/blockforge undo
/blockforge undo clear
```

预期结果：

- Undo 记录按玩家保存。
- 命令 build 和 Builder Wand build 放置成功后都会产生 undo snapshot。
- `protectBlockEntities=true` 时会跳过 BlockEntity 目标位置，并计入 `protected`。
- 如果后续把 `allowReplaceNonAir` 改为 `false`，非空气且不可替换方块会被跳过，并计入 `nonReplaceable`。

### v0.6.1 中文实机测试结果

状态：已在 Minecraft Java Edition `1.21.1` + NeoForge `21.1.227` 环境下完成
Builder Wand 放置验证。

已验证命令与行为：

- `/blockforge select state_test_house` 成功选择蓝图。
- `/blockforge selected` 正常显示当前蓝图、尺寸、方块数量和 rotation。
- `/blockforge rotate 90` 成功设置旋转角度。
- `/blockforge wand` 成功发放 Builder Wand。
- 手持 Builder Wand 右键方块可生成 `state_test_house`。
- 输入无效旋转值时会被拒绝，例如 `Unsupported rotation: 399`。

实测放置结果：

- `state_test_house`：成功放置 `116` 个方块。
- `appliedProperties=10`
- `missingPalette=0`
- `invalidBlockId=0`
- `invalidProperties=0`
- `outOfWorld=0`
- `protected=0`
- `nonReplaceable=0`

测试结论：Builder Wand 能使用当前选择的 Blueprint v2、BlockState properties 和
rotation 完成服务端放置。放置结果提示中已出现 `/blockforge undo` 回滚入口。

待验证：`/blockforge undo list`、`/blockforge undo`、`/blockforge undo clear` 的
实际回滚行为仍需单独实机测试。

## 20. Ghost Preview MVP Candidate Test Plan

Build status: Gradle build passed for v0.7. Minecraft manual testing is pending.

Recommended flow:

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge select tiny_platform
/blockforge rotate 90
/blockforge wand
```

Manual checks:

1. Hold the Builder Wand.
2. Look at a ground block.
3. A Ghost Preview outline should appear at `clickedPos.relative(clickedFace)`.
4. Run `/blockforge rotate 180`.
5. The preview should update to the new rotation.
6. Run `/blockforge select state_test_house`.
7. The preview dimensions should update.
8. Right-clicking should still place the real structure through the server.
9. Switching away from the Builder Wand should hide the preview.
10. A dedicated server should not crash from client-only preview classes.

Expected render contents:

- Translucent bounding box for the rotated blueprint size.
- Ground footprint rectangle at the base Y.
- Cyan for lightweight valid previews.
- Red for invalid height range or missing size.

Known v0.7 preview limits:

- No collision scan.
- No protected-block scan.
- No sampled voxel outlines.
- No preview on/off command yet.
- Real Minecraft manual testing is pending.

## 21. 中文 Ghost Preview MVP 候选版测试计划

构建状态：v0.7 已通过 Gradle build，并已完成 Minecraft 实机验证。

推荐流程：

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge select tiny_platform
/blockforge rotate 90
/blockforge wand
```

手动检查：

1. 手持 Builder Wand。
2. 看向一个地面方块。
3. Ghost Preview 轮廓应出现在 `clickedPos.relative(clickedFace)` 位置。
4. 执行 `/blockforge rotate 180`。
5. 预览应按新旋转状态更新。
6. 执行 `/blockforge select state_test_house`。
7. 预览尺寸应更新。
8. 右键后真实结构仍由服务端放置。
9. 切换到非 Builder Wand 物品时，预览应隐藏。
10. dedicated server 不应因为 client-only preview class 崩溃。

预期渲染内容：

- 旋转后蓝图尺寸的半透明包围盒。
- 底部 Y 位置的地面占位矩形。
- 青色表示轻量有效预览。
- 红色表示高度越界或尺寸缺失。

当前限制：

- 还没有碰撞扫描。
- 还没有 protected block 扫描。
- 还没有 sampled voxel 方块轮廓。
- 还没有 `/blockforge preview on|off` 命令。

### v0.7 中文实机测试结果

状态：已在 Minecraft Java Edition `1.21.1` + NeoForge `21.1.227` 环境下通过。

已验证命令与行为：

- `/blockforge examples install` 可安装示例蓝图。
- `/blockforge reload` 可加载示例蓝图。
- `/blockforge select tiny_platform` 可同步当前选择。
- `/blockforge rotate 180` 可更新 Builder Wand rotation。
- 手持 Builder Wand 时 Ghost Preview 轮廓可显示，并跟随玩家看向的方块位置。
- `tiny_platform` 可通过 Builder Wand 正常放置。
- `/blockforge select state_test_house` 后，Ghost Preview 尺寸会随蓝图更新。
- `/blockforge rotate 90` 后，Ghost Preview 与实际放置方向可继续工作。
- 右键后真实结构仍由服务端放置。

实测放置结果：

- `tiny_platform`：成功放置 `9` 个方块。
- `state_test_house`：成功放置 `116` 个方块。
- `appliedProperties=10`
- `missingPalette=0`
- `invalidBlockId=0`
- `invalidProperties=0`
- `outOfWorld=0`
- `protected=0`
- `nonReplaceable=0`

测试结论：Ghost Preview MVP candidate 已完成游戏内闭环验证。预览能作为
Builder Wand 放置前的位置和范围提示；真实放置仍由服务端执行，且 v0.6.1 的
Undo 提示仍正常出现。

## 22. Blueprint Selector GUI MVP Test Plan

Build status: Gradle build passed for v0.8. Minecraft manual testing is pending.

Recommended flow:

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge gui
```

Manual checks:

1. The Blueprint Selector GUI opens.
2. The list shows loaded blueprints such as `tiny_platform`, `small_test_house`, and `state_test_house`.
3. Click `state_test_house`.
4. Choose `90°`.
5. Click Select.
6. Hold the Builder Wand and look at a ground block.
7. Ghost Preview updates to the selected blueprint and rotation.
8. Right-click to place the structure.
9. `/blockforge selected` shows the same blueprint and rotation.
10. `/blockforge undo` can still revert the placement.

Also test the default keybind:

```text
B
```

Expected result:

- The keybind opens the same selector.
- The client only sends a selection request.
- The server validates blueprint id and rotation before updating selection state.
- Ghost Preview and Builder Wand use the server-confirmed selection.

Known v0.8 GUI limits:

- No blueprint editing.
- No thumbnails.
- No search or advanced filtering.
- No complex paging.
- No Web live sync.

## 23. 中文 Blueprint Selector GUI MVP 测试计划

构建状态：v0.8 已通过 Gradle build，并已完成 Minecraft 实机验证。

推荐流程：

```mcfunction
/blockforge examples install
/blockforge reload
/blockforge gui
```

手动检查：

1. Blueprint Selector GUI 能打开。
2. 列表能看到 `tiny_platform`、`small_test_house`、`state_test_house` 等已加载蓝图。
3. 点击 `state_test_house`。
4. 选择 `90°`。
5. 点击 Select。
6. 手持 Builder Wand 看向地面方块。
7. Ghost Preview 更新为选中的蓝图和旋转角度。
8. 右键后真实结构仍能放置。
9. `/blockforge selected` 显示的蓝图和 rotation 与 GUI 选择一致。
10. `/blockforge undo` 仍能撤销放置。

同时测试默认按键：

```text
B
```

预期结果：

- 按键能打开同一个选择器。
- 客户端只发送选择请求。
- 服务端会校验 blueprint id 和 rotation 后再更新选择状态。
- Ghost Preview 和 Builder Wand 使用服务端确认后的选择状态。

当前 v0.8 GUI 限制：

- 没有蓝图编辑。
- 没有缩略图。
- 没有搜索或高级过滤。
- 没有复杂分页。
- 没有 Web 实时同步。

### v0.8 中文实机测试结果

状态：已在 Minecraft Java Edition `1.21.1` + NeoForge `21.1.227` 环境下通过。

已验证命令与行为：

- `/blockforge gui` 可以打开 Blueprint Selector GUI。
- 默认 `B` 键可以打开 Blueprint Selector GUI。
- GUI 能同步并显示已加载蓝图列表。
- GUI 可以选择 blueprint。
- GUI 可以选择 rotation。
- 点击 Select 后服务端选择状态会更新。
- Ghost Preview 会跟随 GUI 选择更新。
- Builder Wand 仍可正常放置。
- `/blockforge selected` 显示状态与 GUI 选择一致。
- 其他既有功能未发现回归。

实测 UI 修正：

- Blueprint Selector 背景已从默认模糊背景改为单层半透明遮罩。
- 面板背景更清晰，不再呈现明显“两层界面”效果。

测试结论：Blueprint Selector GUI MVP 已完成游戏内闭环验证。GUI 选择结果会经由
服务端校验后同步到 Builder Wand 和 Ghost Preview，真实放置仍由服务端执行。

## 24. Material Requirements / Survival Cost MVP Test Plan

Build status: Gradle build passed for v0.9. Minecraft manual testing is pending.

Creative mode test:

```mcfunction
/gamemode creative
/blockforge examples install
/blockforge reload
/blockforge select tiny_platform
/blockforge materials selected
/blockforge wand
```

Right-click with the Builder Wand.

Expected result:

- Material report is shown.
- Build succeeds.
- Creative mode consumes no materials.

Survival missing-materials test:

```mcfunction
/gamemode survival
/clear
/blockforge materials selected
```

Try to place with the Builder Wand.

Expected result:

- Build is rejected.
- Missing materials are listed.
- No blocks are placed.

Survival enough-materials test:

```mcfunction
/give @s minecraft:stone_bricks 16
/blockforge materials selected
```

Place `tiny_platform` with the Builder Wand.

Expected result:

- Build succeeds.
- Required items are consumed from inventory.
- Placement output includes consumed item count.

Undo check:

```mcfunction
/blockforge undo
```

Expected result:

- World blocks are restored.
- Consumed materials are not refunded in v0.9. v0.9.1 adds material refunds.

## 25. 中文材料需求 / 生存成本 MVP 测试计划

构建状态：v0.9 已通过 Gradle build，并已完成 Minecraft 实机验证。

创造模式测试：

```mcfunction
/gamemode creative
/blockforge examples install
/blockforge reload
/blockforge select tiny_platform
/blockforge materials selected
/blockforge wand
```

手持 Builder Wand 右键生成。

预期结果：

- 能显示材料需求。
- 建造成功。
- 创造模式不消耗材料。

生存模式材料不足测试：

```mcfunction
/gamemode survival
/clear
/blockforge materials selected
```

然后尝试使用 Builder Wand 放置。

预期结果：

- 建造被拒绝。
- 输出缺少的材料。
- 不应放置方块。

生存模式材料充足测试：

```mcfunction
/give @s minecraft:stone_bricks 16
/blockforge materials selected
```

用 Builder Wand 放置 `tiny_platform`。

预期结果：

- 建造成功。
- 背包中对应材料被扣除。
- 放置输出包含 consumed item count。

Undo 测试：

```mcfunction
/blockforge undo
```

预期结果：

- 世界方块恢复。
- v0.9 暂不返还已消耗材料。

### v0.9 中文实机测试结果

状态：已在 Minecraft Java Edition `1.21.1` + NeoForge `21.1.227` 环境下通过。

已验证命令与行为：

- 创造模式下使用 Builder Wand 可以正常生成建筑。
- 创造模式提示 `Creative mode: no materials consumed`，不会消耗材料。
- 切换到生存模式后，材料不足时 Builder Wand 会拒绝 build。
- 材料不足提示正确显示缺少项：
  - `minecraft:stone_bricks missing=9 required=9 available=0`
- `/blockforge materials selected` 能显示材料报告。
- 给玩家 `16` 个 `minecraft:stone_bricks` 后，材料报告变为充足。
- 生存模式材料充足时 Builder Wand 可以正常生成建筑。
- 生存模式生成后会扣除材料，`16` 个石砖生成 `tiny_platform` 后剩余 `7` 个。
- `/blockforge undo` 可以恢复 `9` 个方块。
- v0.9 中 Undo 不返还已消耗材料，符合当前已知限制。

实测材料报告：

- 材料不足：
  - `blueprint=tiny_platform`
  - `requiredItems=9`
  - `availableItems=0`
  - `missingItemTypes=1`
  - `enoughMaterials=false`
- 材料充足：
  - `blueprint=tiny_platform`
  - `requiredItems=9`
  - `availableItems=9`
  - `missingItemTypes=0`
  - `enoughMaterials=true`

测试结论：Material Requirements / Survival Cost MVP 已完成游戏内闭环验证。
创造模式绕过、生存模式材料不足拒绝、生存模式材料足够扣除并建造、Undo 仅恢复方块
这四条核心规则均按预期工作。

## 26. Material Transaction / Undo Refund Test Plan

Build status: v0.9.1 Gradle build passed. Minecraft manual testing is pending.

Survival refund test:

```mcfunction
/gamemode survival
/clear
/give @s minecraft:stone_bricks 9
/blockforge select tiny_platform
/blockforge materials selected
```

Place `tiny_platform` with the Builder Wand.

Expected result:

- Build succeeds.
- Inventory stone bricks decrease from `9` to `0`.
- Output says to use `/blockforge undo` to restore blocks and refund materials.

Then run:

```mcfunction
/blockforge undo
```

Expected result:

- World blocks are restored.
- `9` stone bricks are refunded to the player inventory.
- Output includes restored block count and refunded item count.

Full-inventory refund drop test:

```mcfunction
/gamemode survival
/clear
/give @s minecraft:stone_bricks 9
```

Fill the remaining inventory slots with any filler item, build `tiny_platform`,
then run:

```mcfunction
/blockforge undo
```

Expected result:

- World blocks are restored.
- Any refunded items that do not fit in the inventory are dropped near the player.
- Output includes dropped item count when drops occur.

Creative no-refund test:

```mcfunction
/gamemode creative
/blockforge select tiny_platform
```

Place with the Builder Wand, then run:

```mcfunction
/blockforge undo
```

Expected result:

- World blocks are restored.
- Output says no materials were consumed.

## 27. 中文材料事务 / Undo 返还测试计划

构建状态：v0.9.1 已通过 Gradle build，并已完成 Minecraft 实机验证。

测试 1：生存模式材料返还

```mcfunction
/gamemode survival
/clear
/give @s minecraft:stone_bricks 9
/blockforge select tiny_platform
/blockforge materials selected
```

手持 Builder Wand 生成 `tiny_platform`。

预期结果：

- 建造成功。
- 石砖从 `9` 个变成 `0` 个。
- 建造提示包含 `/blockforge undo`，说明可恢复方块并返还材料。

然后执行：

```mcfunction
/blockforge undo
```

预期结果：

- 世界方块恢复。
- 石砖返还为 `9` 个。
- 输出包含恢复方块数量和返还物品数量。

测试 2：背包满时返还掉落

```mcfunction
/gamemode survival
/clear
/give @s minecraft:stone_bricks 9
```

填满剩余背包格，生成 `tiny_platform`，再执行：

```mcfunction
/blockforge undo
```

预期结果：

- 世界方块恢复。
- 背包放不下的返还材料会掉落在玩家附近。
- 如果发生掉落，输出包含 dropped item count。

测试 3：创造模式不返还

```mcfunction
/gamemode creative
/blockforge select tiny_platform
```

用 Builder Wand 生成后执行：

```mcfunction
/blockforge undo
```

预期结果：

- 世界方块恢复。
- 输出提示没有消耗材料。

### v0.9.1 中文实机测试结果

状态：已在 Minecraft Java Edition `1.21.1` + NeoForge `21.1.227` 环境下通过。

已验证行为：

- 生存模式下使用 Builder Wand 生成 `tiny_platform` 会消耗 `9` 个 `minecraft:stone_bricks`。
- 执行 `/blockforge undo` 后，已放置方块恢复到放置前状态。
- 执行 `/blockforge undo` 后，`9` 个 `minecraft:stone_bricks` 成功返还到玩家背包。
- 背包满时执行 `/blockforge undo`，无法放入背包的返还材料会掉落在玩家附近。

测试结论：v0.9.1 已补齐 v0.9 的核心限制。BlockForge 现在可以在生存模式下完成
材料检查、材料扣除、建造、Undo 方块回滚、Undo 材料返还，以及背包满时的返还掉落。

## 28. v1.0 RC Smoke Test Result

Status: passed on Minecraft Java Edition `1.21.1` + NeoForge `21.1.227`.

Verified:

- The client launches with BlockForge Connector `1.0.0-rc.1`.
- NeoForge common config registration does not prevent startup.
- Core Connector flow still works after the release-candidate packaging changes.

The v1.0 RC smoke test is intentionally narrower than the previous full manual
tests because this release pass focuses on packaging, metadata, CI, docs, and
common config registration.

## 29. v1.0 RC 中文烟测结果

状态：已在 Minecraft Java Edition `1.21.1` + NeoForge `21.1.227` 环境下通过。

已验证：

- BlockForge Connector `1.0.0-rc.1` 可以正常启动客户端。
- NeoForge common config 注册不会导致启动失败。
- Release Candidate 的打包、metadata、CI 文档和 common config 改动没有破坏核心 Connector 流程。

本轮是 v1.0 RC 发布前烟测，范围小于前面版本的完整实机测试，重点验证发布整理改动没有引入启动或核心流程问题。
