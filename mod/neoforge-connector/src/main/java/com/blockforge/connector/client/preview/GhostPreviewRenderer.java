package com.blockforge.connector.client.preview;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public final class GhostPreviewRenderer {
    private GhostPreviewRenderer() {
    }

    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        ClientPreviewState.SelectedBlueprint selected = ClientPreviewState.selectedBlueprint();
        ClientPreviewState.PreviewTarget target = ClientPreviewState.previewTarget();
        if (selected == null || target == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        VertexConsumer lines = bufferSource.getBuffer(RenderType.lines());
        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPosition = event.getCamera().getPosition();

        double minX = target.basePos().getX();
        double minY = target.basePos().getY();
        double minZ = target.basePos().getZ();
        double maxX = minX + selected.rotatedWidth();
        double maxY = minY + selected.height();
        double maxZ = minZ + selected.rotatedDepth();

        PreviewColor color = target.valid()
                ? new PreviewColor(0.15F, 0.95F, 0.95F, 0.8F)
                : new PreviewColor(1.0F, 0.2F, 0.2F, 0.9F);

        poseStack.pushPose();
        poseStack.translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);

        LevelRenderer.renderLineBox(
                poseStack,
                lines,
                new AABB(minX, minY, minZ, maxX, maxY, maxZ),
                color.red(),
                color.green(),
                color.blue(),
                color.alpha()
        );
        LevelRenderer.renderLineBox(
                poseStack,
                lines,
                new AABB(minX, minY + 0.02D, minZ, maxX, minY + 0.04D, maxZ),
                color.red(),
                color.green(),
                color.blue(),
                color.alpha()
        );

        poseStack.popPose();
        bufferSource.endBatch(RenderType.lines());
    }

    private record PreviewColor(float red, float green, float blue, float alpha) {
    }
}
