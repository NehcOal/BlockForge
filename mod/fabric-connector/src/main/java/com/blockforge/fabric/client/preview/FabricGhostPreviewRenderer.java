package com.blockforge.fabric.client.preview;

import com.blockforge.common.preview.PreviewBounds;
import com.blockforge.common.preview.PreviewTarget;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public final class FabricGhostPreviewRenderer {
    private FabricGhostPreviewRenderer() {
    }

    public static void register() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(FabricGhostPreviewRenderer::render);
    }

    private static void render(WorldRenderContext context) {
        PreviewBounds bounds = FabricClientPreviewState.bounds();
        PreviewTarget target = FabricClientPreviewState.target();
        if (bounds == null || target == null || !target.visible()) {
            return;
        }

        MatrixStack matrices = context.matrixStack();
        VertexConsumerProvider consumers = context.consumers();
        if (matrices == null || consumers == null) {
            return;
        }

        VertexConsumer lines = consumers.getBuffer(RenderLayer.getLines());
        Vec3d camera = context.camera().getPos();
        double minX = target.x();
        double minY = target.y();
        double minZ = target.z();
        double maxX = minX + bounds.rotatedWidth();
        double maxY = minY + bounds.height();
        double maxZ = minZ + bounds.rotatedDepth();
        PreviewColor color = target.valid()
                ? new PreviewColor(0.15F, 0.95F, 0.95F, 0.85F)
                : new PreviewColor(1.0F, 0.2F, 0.2F, 0.9F);

        matrices.push();
        matrices.translate(-camera.x, -camera.y, -camera.z);
        WorldRenderer.drawBox(matrices, lines, new Box(minX, minY, minZ, maxX, maxY, maxZ), color.red(), color.green(), color.blue(), color.alpha());
        WorldRenderer.drawBox(matrices, lines, new Box(minX, minY + 0.02D, minZ, maxX, minY + 0.04D, maxZ), color.red(), color.green(), color.blue(), color.alpha());
        matrices.pop();

        if (consumers instanceof VertexConsumerProvider.Immediate immediate) {
            immediate.draw(RenderLayer.getLines());
        }
    }

    private record PreviewColor(float red, float green, float blue, float alpha) {
    }
}
