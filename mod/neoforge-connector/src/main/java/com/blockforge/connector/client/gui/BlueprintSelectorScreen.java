package com.blockforge.connector.client.gui;

import com.blockforge.connector.network.payload.BlueprintSummary;
import com.blockforge.connector.network.payload.MaterialReportPayload;
import com.blockforge.connector.network.payload.MaterialReportRequestPayload;
import com.blockforge.connector.network.payload.MaterialRequirementSummary;
import com.blockforge.connector.network.payload.RequestBlueprintListPayload;
import com.blockforge.connector.network.payload.SelectBlueprintRequestPayload;
import com.blockforge.connector.config.BlockForgeConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class BlueprintSelectorScreen extends Screen {
    private static final int[] ROTATIONS = {0, 90, 180, 270};

    public BlueprintSelectorScreen() {
        super(Component.translatable("screen.blockforge_connector.blueprint_selector"));
    }

    public static void openAndRequestList() {
        BlueprintClientCache.beginLoading();
        Minecraft.getInstance().setScreen(new BlueprintSelectorScreen());
        PacketDistributor.sendToServer(new RequestBlueprintListPayload(true));
    }

    public static void refreshOpenScreen() {
        if (Minecraft.getInstance().screen instanceof BlueprintSelectorScreen screen) {
            screen.rebuildWidgets();
        }
    }

    @Override
    protected void init() {
        rebuildWidgets();

        if (BlueprintClientCache.blueprints().isEmpty()) {
            BlueprintClientCache.beginLoading();
            PacketDistributor.sendToServer(new RequestBlueprintListPayload(false));
        }
    }

    @Override
    protected void rebuildWidgets() {
        clearWidgets();

        int left = Math.max(20, width / 2 - 190);
        int top = Math.max(32, height / 2 - 100);
        int listWidth = 180;
        int detailsLeft = left + listWidth + 20;
        int detailsWidth = 170;
        List<BlueprintSummary> summaries = BlueprintClientCache.blueprints();

        int row = 0;
        for (BlueprintSummary summary : summaries) {
            if (row >= 7) {
                break;
            }

            int buttonY = top + 24 + row * 24;
            Component label = Component.literal(summary.name());
            Button button = Button.builder(label, ignored -> {
                        BlueprintClientCache.selectLocally(summary.id());
                        rebuildWidgets();
                    })
                    .bounds(left, buttonY, listWidth, 20)
                    .build();
            if (summary.id().equals(BlueprintClientCache.selectedBlueprintId())) {
                button.active = false;
            }
            addRenderableWidget(button);
            row++;
        }

        int rotationY = top + 84;
        for (int index = 0; index < ROTATIONS.length; index++) {
            int degrees = ROTATIONS[index];
            Button button = Button.builder(Component.literal(degrees + "\u00b0"), ignored -> {
                        BlueprintClientCache.setRotation(degrees);
                        rebuildWidgets();
                    })
                    .bounds(detailsLeft + index * 42, rotationY, 38, 20)
                    .build();
            if (BlueprintClientCache.rotation() == degrees) {
                button.active = false;
            }
            addRenderableWidget(button);
        }

        Button selectButton = Button.builder(Component.translatable("screen.blockforge_connector.select"), ignored -> submitSelection())
                .bounds(detailsLeft, top + 118, 82, 20)
                .build();
        selectButton.active = BlueprintClientCache.selectedBlueprint().isPresent();
        addRenderableWidget(selectButton);

        Button materialsButton = Button.builder(Component.translatable("screen.blockforge_connector.materials"), ignored -> requestMaterials())
                .bounds(detailsLeft + 90, top + 118, 82, 20)
                .build();
        materialsButton.active = BlueprintClientCache.selectedBlueprint().isPresent();
        addRenderableWidget(materialsButton);

        addRenderableWidget(Button.builder(Component.translatable("screen.blockforge_connector.close"), ignored -> onClose())
                .bounds(detailsLeft, top + 194, 172, 20)
                .build());
    }

    private void submitSelection() {
        BlueprintClientCache.selectedBlueprint().ifPresent(summary -> {
            PacketDistributor.sendToServer(new SelectBlueprintRequestPayload(summary.id(), BlueprintClientCache.rotation()));
            onClose();
        });
    }

    private void requestMaterials() {
        BlueprintClientCache.selectedBlueprint().ifPresent(summary ->
                PacketDistributor.sendToServer(new MaterialReportRequestPayload(summary.id()))
        );
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int left = Math.max(20, width / 2 - 190);
        int top = Math.max(32, height / 2 - 100);
        int detailsLeft = left + 200;
        int panelRight = detailsLeft + 180;
        int panelBottom = top + 230;

        graphics.fill(0, 0, width, height, 0x99000000);
        graphics.fill(left - 12, top - 18, panelRight + 12, panelBottom + 12, 0xF0101820);
        graphics.fill(left - 8, top - 14, panelRight + 8, panelBottom + 8, 0xE8182630);
        graphics.renderOutline(left - 12, top - 18, panelRight + 12 - (left - 12), panelBottom + 12 - (top - 18), 0xFF4AA8C8);
        graphics.drawString(font, title, left, top - 10, 0xFF9CEBFF, false);
        graphics.drawString(font, Component.translatable("screen.blockforge_connector.blueprints"), left, top + 8, 0xFFE7F7FF, false);

        if (BlueprintClientCache.loading()) {
            graphics.drawString(font, Component.literal("Loading..."), left, top + 34, 0xFFFFD37A, false);
        } else if (BlueprintClientCache.blueprints().isEmpty()) {
            graphics.drawWordWrap(
                    font,
                    Component.translatable("screen.blockforge_connector.empty"),
                    left,
                    top + 34,
                    180,
                    0xFFFFD37A
            );
        }

        renderDetails(graphics, detailsLeft, top);

        if (!BlueprintClientCache.error().isBlank()) {
            graphics.drawWordWrap(font, Component.literal(BlueprintClientCache.error()), left, panelBottom - 34, 350, 0xFFFF7070);
        }

        graphics.drawWordWrap(
                font,
                Component.translatable("screen.blockforge_connector.hint"),
                left,
                panelBottom - 18,
                360,
                0xFFB6C7D4
        );

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void renderDetails(GuiGraphics graphics, int x, int y) {
        graphics.drawString(font, Component.translatable("screen.blockforge_connector.details"), x, y + 8, 0xFFE7F7FF, false);
        BlueprintSummary summary = BlueprintClientCache.selectedBlueprint().orElse(null);
        if (summary == null) {
            graphics.drawString(font, Component.translatable("screen.blockforge_connector.no_selection"), x, y + 30, 0xFFFFD37A, false);
            return;
        }

        graphics.drawString(font, summary.name(), x, y + 28, 0xFFFFFFFF, false);
        graphics.drawString(font, summary.id(), x, y + 40, 0xFF9AA8B5, false);
        graphics.drawString(font, Component.literal(sourceLabel(summary.id())), x, y + 52, 0xFFB6C7D4, false);
        graphics.drawString(font, Component.translatable("screen.blockforge_connector.size", summary.sizeLabel()), x, y + 64, 0xFFC9D7E2, false);
        graphics.drawString(font, Component.translatable("screen.blockforge_connector.blocks", summary.blockCount()), x, y + 76, 0xFFC9D7E2, false);
        graphics.drawString(font, "schemaVersion=" + summary.schemaVersion(), x, y + 88, 0xFFC9D7E2, false);
        graphics.drawString(
                font,
                Component.translatable("screen.blockforge_connector.block_states", summary.hasBlockStates()),
                x,
                y + 100,
                summary.hasBlockStates() ? 0xFF8EF0B4 : 0xFFB6C7D4,
                false
        );
        graphics.drawString(font, Component.translatable("screen.blockforge_connector.rotation"), x, y + 112, 0xFFE7F7FF, false);
        graphics.drawString(
                font,
                Component.literal("Material sources: "
                        + (BlockForgeConfig.enableNearbyContainers()
                        ? "Nearby containers enabled"
                        : "Player inventory only")),
                x,
                y + 116,
                0xFFC9D7E2,
                false
        );
        graphics.drawString(
                font,
                Component.literal("Source priority=" + BlockForgeConfig.materialSourcePriority()
                        + " | radius=" + BlockForgeConfig.nearbyContainerSearchRadius()),
                x,
                y + 128,
                0xFFB6C7D4,
                false
        );
        renderMaterialReport(graphics, x, y + 152);
    }

    private String sourceLabel(String id) {
        int separator = id.indexOf('/');
        return separator > 0 ? "source=pack | pack=" + id.substring(0, separator) : "source=loose";
    }

    private void renderMaterialReport(GuiGraphics graphics, int x, int y) {
        MaterialReportPayload report = BlueprintClientCache.materialReport();
        if (report == null || !BlueprintClientCache.selectedBlueprint().map(BlueprintSummary::id).orElse("").equals(report.blueprintId())) {
            graphics.drawString(font, Component.translatable("screen.blockforge_connector.materials_hint"), x, y, 0xFFB6C7D4, false);
            return;
        }

        int color = report.enoughMaterials() ? 0xFF8EF0B4 : 0xFFFF7070;
        graphics.drawString(
                font,
                Component.translatable("screen.blockforge_connector.enough_materials", report.enoughMaterials()),
                x,
                y,
                color,
                false
        );
        graphics.drawString(
                font,
                Component.translatable("screen.blockforge_connector.material_totals", report.totalRequiredItems(), report.totalAvailableItems()),
                x,
                y + 12,
                0xFFC9D7E2,
                false
        );

        int line = 0;
        for (MaterialRequirementSummary requirement : report.requirements()) {
            if (line >= 3) {
                break;
            }
            graphics.drawString(
                    font,
                    requirement.itemId() + " " + requirement.required() + "/" + requirement.available() + " missing " + requirement.missing(),
                    x,
                    y + 28 + line * 12,
                    requirement.missing() > 0 ? 0xFFFFA0A0 : 0xFFB6C7D4,
                    false
            );
            line++;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
