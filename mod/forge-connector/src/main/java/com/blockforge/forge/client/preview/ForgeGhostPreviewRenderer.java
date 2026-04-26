package com.blockforge.forge.client.preview;

import com.blockforge.common.preview.PreviewBounds;
import com.blockforge.common.preview.PreviewTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

public final class ForgeGhostPreviewRenderer {
    private ForgeGhostPreviewRenderer() {
    }

    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        PreviewBounds bounds = ForgeClientPreviewState.bounds();
        PreviewTarget target = ForgeClientPreviewState.target();
        if (bounds == null || target == null || !target.visible()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        VertexConsumer lines = bufferSource.getBuffer(RenderType.lines());
        PoseStack poseStack = new PoseStack();
        Matrix4f eventPose = event.getPoseStack();
        Vec3 cameraPosition = event.getCamera().getPosition();

        double minX = target.x();
        double minY = target.y();
        double minZ = target.z();
        double maxX = minX + bounds.rotatedWidth();
        double maxY = minY + bounds.height();
        double maxZ = minZ + bounds.rotatedDepth();
        PreviewColor color = target.valid()
                ? new PreviewColor(0.15F, 0.95F, 0.95F, 0.85F)
                : new PreviewColor(1.0F, 0.2F, 0.2F, 0.9F);

        poseStack.last().pose().set(eventPose);
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
