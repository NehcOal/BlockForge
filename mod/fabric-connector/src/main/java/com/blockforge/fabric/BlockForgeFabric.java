package com.blockforge.fabric;

import com.blockforge.fabric.blueprint.FabricBlueprintLoader;
import com.blockforge.fabric.blueprint.FabricBlueprintRegistry;
import com.blockforge.fabric.buildplan.FabricBuildPlanManager;
import com.blockforge.fabric.command.FabricBlockForgeCommands;
import com.blockforge.fabric.network.FabricBlueprintGuiNetworking;
import com.blockforge.fabric.player.FabricPlayerSelectionManager;
import com.blockforge.fabric.registry.FabricModItems;
import com.blockforge.fabric.security.FabricPermissionService;
import com.blockforge.fabric.security.FabricProtectionService;
import com.blockforge.fabric.undo.FabricUndoManager;
import com.blockforge.common.gameplay.BuilderWandStateStore;
import com.blockforge.common.security.permission.BlockForgePermissionAction;
import com.blockforge.common.security.permission.PermissionCheckResult;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockForgeFabric implements ModInitializer {
    public static final String MOD_ID = "blockforge_connector";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final FabricBlueprintRegistry BLUEPRINTS = new FabricBlueprintRegistry(
            FabricBlueprintLoader.defaultBlueprintDirectory()
    );
    public static final FabricUndoManager UNDO = new FabricUndoManager();
    public static final FabricPlayerSelectionManager SELECTIONS = new FabricPlayerSelectionManager();
    public static final FabricPermissionService PERMISSIONS = new FabricPermissionService();
    public static final FabricProtectionService PROTECTION = new FabricProtectionService();
    public static final BuilderWandStateStore WAND_STATES = new BuilderWandStateStore();
    public static final FabricBuildPlanManager BUILD_PLANS = new FabricBuildPlanManager();

    @Override
    public void onInitialize() {
        FabricModItems.register();
        registerGameplayBlockInteractions();
        FabricBlueprintGuiNetworking.registerServer();
        BLUEPRINTS.reload();
        PROTECTION.reload();
        FabricBlockForgeCommands.register(BLUEPRINTS, UNDO, SELECTIONS);
        LOGGER.info(
                "Loaded {} BlockForge Fabric blueprint(s) from {}",
                BLUEPRINTS.getBlueprints().size(),
                BLUEPRINTS.getDirectory()
        );
    }

    private void registerGameplayBlockInteractions() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!(player instanceof ServerPlayerEntity serverPlayer)) {
                return ActionResult.PASS;
            }

            var state = world.getBlockState(hitResult.getBlockPos());
            if (state.isOf(FabricModItems.BLUEPRINT_TABLE)) {
                if (!checkGameplayPermission(serverPlayer, BlockForgePermissionAction.GAMEPLAY_BLUEPRINT_TABLE_USE)) {
                    return ActionResult.FAIL;
                }
                FabricBlueprintGuiNetworking.sendBlueprintList(serverPlayer, true);
                return ActionResult.SUCCESS;
            }

            if (state.isOf(FabricModItems.BUILDER_ANCHOR)) {
                if (!checkGameplayPermission(serverPlayer, BlockForgePermissionAction.GAMEPLAY_ANCHOR_USE)) {
                    return ActionResult.FAIL;
                }
                var pos = hitResult.getBlockPos();
                var wandState = WAND_STATES.update(serverPlayer.getUuid(), current -> current.withAnchor(
                        pos.getX() + "," + pos.getY() + "," + pos.getZ(),
                        world.getTime()
                ));
                serverPlayer.sendMessage(Text.literal("BlockForge Builder Wand bound to anchor " + wandState.anchorId() + "."), false);
                return ActionResult.SUCCESS;
            }

            if (state.isOf(FabricModItems.MATERIAL_CACHE)) {
                if (!checkGameplayPermission(serverPlayer, BlockForgePermissionAction.GAMEPLAY_CACHE_USE)) {
                    return ActionResult.FAIL;
                }
                serverPlayer.sendMessage(Text.literal("BlockForge Material Cache alpha block registered. Inventory-backed sourcing is planned for a later v3.1 alpha polish commit."), false);
                return ActionResult.SUCCESS;
            }

            if (state.isOf(FabricModItems.BUILDER_STATION)) {
                if (!checkGameplayPermission(serverPlayer, BlockForgePermissionAction.GAMEPLAY_STATION_USE)) {
                    return ActionResult.FAIL;
                }
                serverPlayer.sendMessage(Text.literal("BlockForge Builder Station alpha scaffold. Use station commands for command-driven jobs."), false);
                return ActionResult.SUCCESS;
            }

            if (state.isOf(FabricModItems.MATERIAL_LINK)) {
                if (!checkGameplayPermission(serverPlayer, BlockForgePermissionAction.GAMEPLAY_MATERIAL_LINK_USE)) {
                    return ActionResult.FAIL;
                }
                serverPlayer.sendMessage(Text.literal("BlockForge Material Link alpha scaffold. Links expose Material Cache sources to station jobs."), false);
                return ActionResult.SUCCESS;
            }

            if (state.isOf(FabricModItems.CONSTRUCTION_CORE)) {
                if (!checkGameplayPermission(serverPlayer, BlockForgePermissionAction.GAMEPLAY_CONSTRUCTION_CORE_USE)) {
                    return ActionResult.FAIL;
                }
                serverPlayer.sendMessage(Text.literal("BlockForge Construction Core alpha scaffold. Multi-station project coordination remains planned."), false);
                return ActionResult.SUCCESS;
            }

            if (state.isOf(FabricModItems.SETTLEMENT_CORE)) {
                if (!checkGameplayPermission(serverPlayer, BlockForgePermissionAction.GAMEPLAY_SETTLEMENT_USE)) {
                    return ActionResult.FAIL;
                }
                serverPlayer.sendMessage(Text.literal("BlockForge Settlement Core alpha. Use settlement commands to create and inspect settlements."), false);
                return ActionResult.SUCCESS;
            }

            if (state.isOf(FabricModItems.CONTRACT_BOARD)) {
                if (!checkGameplayPermission(serverPlayer, BlockForgePermissionAction.GAMEPLAY_CONTRACT_USE)) {
                    return ActionResult.FAIL;
                }
                serverPlayer.sendMessage(Text.literal("BlockForge Contract Board alpha. Use contract commands to list, accept, verify, and submit contracts."), false);
                return ActionResult.SUCCESS;
            }

            if (state.isOf(FabricModItems.REWARD_CRATE)) {
                if (!checkGameplayPermission(serverPlayer, BlockForgePermissionAction.GAMEPLAY_REWARD_USE)) {
                    return ActionResult.FAIL;
                }
                serverPlayer.sendMessage(Text.literal("BlockForge Reward Crate alpha. Reward claiming is command-driven in v5.0."), false);
                return ActionResult.SUCCESS;
            }

            if (state.isOf(FabricModItems.ARCHITECT_DESK)) {
                if (!checkGameplayPermission(serverPlayer, BlockForgePermissionAction.GAMEPLAY_ARCHITECT_USE)) {
                    return ActionResult.FAIL;
                }
                serverPlayer.sendMessage(Text.literal("BlockForge Architect Desk alpha. Use architect commands for profile and reputation."), false);
                return ActionResult.SUCCESS;
            }

            if (state.isOf(FabricModItems.EVENT_BOARD)) {
                if (!checkGameplayPermission(serverPlayer, BlockForgePermissionAction.GAMEPLAY_EVENTS_USE)) {
                    return ActionResult.FAIL;
                }
                serverPlayer.sendMessage(Text.literal("BlockForge Event Board alpha. Use event commands to inspect settlement events."), false);
                return ActionResult.SUCCESS;
            }

            if (state.isOf(FabricModItems.PROJECT_MAP)) {
                if (!checkGameplayPermission(serverPlayer, BlockForgePermissionAction.GAMEPLAY_PROJECTS_USE)) {
                    return ActionResult.FAIL;
                }
                serverPlayer.sendMessage(Text.literal("BlockForge Project Map alpha. Use project commands to inspect project chains."), false);
                return ActionResult.SUCCESS;
            }

            if (state.isOf(FabricModItems.EMERGENCY_BEACON)) {
                if (!checkGameplayPermission(serverPlayer, BlockForgePermissionAction.GAMEPLAY_EMERGENCY_USE)) {
                    return ActionResult.FAIL;
                }
                serverPlayer.sendMessage(Text.literal("BlockForge Emergency Beacon alpha. Use emergency commands to inspect repair requests."), false);
                return ActionResult.SUCCESS;
            }

            if (state.isOf(FabricModItems.SUPPLY_DEPOT)) {
                if (!checkGameplayPermission(serverPlayer, BlockForgePermissionAction.GAMEPLAY_CACHE_USE)) {
                    return ActionResult.FAIL;
                }
                serverPlayer.sendMessage(Text.literal("BlockForge Supply Depot alpha. Settlement material aggregation is scaffolded."), false);
                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        });
    }

    private boolean checkGameplayPermission(ServerPlayerEntity player, BlockForgePermissionAction action) {
        PermissionCheckResult result = PERMISSIONS.check(player, action);
        if (!result.allowed()) {
            player.sendMessage(Text.literal(result.reason()), false);
            return false;
        }
        return true;
    }
}
