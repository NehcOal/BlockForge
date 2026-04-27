package com.blockforge.forge.security;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.security.permission.BlockForgePermissionAction;
import com.blockforge.common.security.permission.PermissionCheckResult;
import com.blockforge.common.security.protection.BlockForgeProtectionRegion;
import com.blockforge.common.security.protection.BuildArea;
import com.blockforge.common.security.protection.BuildAreaCalculator;
import com.blockforge.common.security.protection.ProtectionAction;
import com.blockforge.common.security.protection.ProtectionCheckRequest;
import com.blockforge.common.security.protection.ProtectionCheckResult;
import com.blockforge.common.security.protection.ProtectionPreflightReport;
import com.blockforge.common.security.protection.ProtectionPreflightService;
import com.blockforge.common.security.protection.ProtectionRegionMatcher;
import com.blockforge.common.security.protection.ProtectionRegionsConfig;
import com.blockforge.common.security.protection.ProtectionRegionsParser;
import com.blockforge.common.util.BlockPosition;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class ForgeProtectionService {
    private final ForgePermissionService permissions = new ForgePermissionService();
    private final Path file = FMLPaths.CONFIGDIR.get().resolve("blockforge").resolve("protection-regions.json");
    private volatile ProtectionRegionsConfig config = ProtectionRegionsConfig.empty();

    public Path file() {
        return file;
    }

    public List<BlockForgeProtectionRegion> regions() {
        return config.regions();
    }

    public List<String> warnings() {
        return config.warnings();
    }

    public ForgePermissionService permissions() {
        return permissions;
    }

    public ProtectionRegionsConfig reload() {
        try {
            Files.createDirectories(file.getParent());
            if (Files.notExists(file)) {
                Files.writeString(file, exampleConfig(), StandardCharsets.UTF_8);
            }
            config = ProtectionRegionsParser.parse(Files.readString(file, StandardCharsets.UTF_8));
        } catch (RuntimeException | IOException error) {
            config = new ProtectionRegionsConfig(1, List.of(), List.of("Failed to load protection regions: " + error.getMessage()));
        }
        return config;
    }

    public Optional<BlockForgeProtectionRegion> find(String id) {
        return config.regions().stream().filter(region -> region.id().equals(id)).findFirst();
    }

    public ProtectionPreflightReport preflight(
            ServerPlayer player,
            ServerLevel level,
            BlockPos basePos,
            Blueprint blueprint,
            com.blockforge.common.rotation.BlueprintRotation rotation,
            BlockForgePermissionAction action
    ) {
        PermissionCheckResult permission = permissions.check(player, action);
        PermissionCheckResult bypass = permissions.check(player, BlockForgePermissionAction.BUILD_BYPASS_PROTECTION);
        BuildArea area = buildArea(level, basePos, blueprint, rotation.degrees());
        ProtectionCheckResult protection = check(player, level, area, ProtectionAction.PLACE_BLOCKS, bypass);
        return ProtectionPreflightService.combine(permission, protection, area.blockCount());
    }

    public ProtectionCheckResult check(
            ServerPlayer player,
            ServerLevel level,
            BuildArea area,
            ProtectionAction action,
            PermissionCheckResult bypass
    ) {
        if (!ForgeSecuritySettings.ENABLE_PROTECTION_REGIONS) {
            return ProtectionCheckResult.allowed(List.of());
        }
        ProtectionCheckRequest request = new ProtectionCheckRequest(
                player.getUUID(),
                player.getName().getString(),
                dimensionId(level),
                action,
                area,
                config.regions()
        );
        return ProtectionRegionMatcher.check(request, bypass);
    }

    public boolean canUseContainer(ServerPlayer player, ServerLevel level, BlockPos pos, ProtectionAction action) {
        if (!ForgeSecuritySettings.ENABLE_PROTECTION_REGIONS) {
            return true;
        }
        BuildArea area = new BuildArea(dimensionId(level), pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ(), 1);
        PermissionCheckResult bypass = permissions.check(player, BlockForgePermissionAction.BUILD_BYPASS_PROTECTION);
        return check(player, level, area, action, bypass).allowed();
    }

    public BuildArea buildArea(ServerLevel level, BlockPos basePos, Blueprint blueprint, int rotationDegrees) {
        return BuildAreaCalculator.fromBlueprint(
                dimensionId(level),
                new BlockPosition(basePos.getX(), basePos.getY(), basePos.getZ()),
                blueprint.getSize(),
                rotationDegrees,
                blueprint.getBlockCount()
        );
    }

    private String dimensionId(ServerLevel level) {
        return level.dimension().location().toString();
    }

    private String exampleConfig() {
        return """
                {
                  "schemaVersion": 1,
                  "regions": []
                }
                """;
    }
}
