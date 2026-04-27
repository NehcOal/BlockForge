package com.blockforge.fabric.client.gui;

import com.blockforge.common.gui.BlueprintSummary;
import com.blockforge.common.gui.BlueprintGuiQuery;
import com.blockforge.common.gui.BlueprintSortMode;
import com.blockforge.common.gui.BlueprintSourceFilter;
import com.blockforge.common.gui.BlueprintWarningFilter;
import com.blockforge.fabric.material.source.FabricMaterialSourceSettings;
import com.blockforge.fabric.network.FabricBlueprintGuiNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

public class FabricBlueprintSelectorScreen extends Screen {
    private static final int[] ROTATIONS = {0, 90, 180, 270};
    private String searchText = "";
    private BlueprintSourceFilter sourceFilter = BlueprintSourceFilter.ALL;
    private BlueprintWarningFilter warningFilter = BlueprintWarningFilter.ALL;
    private BlueprintSortMode sortMode = BlueprintSortMode.NAME_ASC;
    private int page = 0;

    public FabricBlueprintSelectorScreen() {
        super(Text.translatable("screen.blockforge_connector.blueprint_selector"));
    }

    public static void openAndRequestList() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getNetworkHandler() == null) {
            return;
        }

        FabricBlueprintClientCache.beginLoading();
        client.setScreen(new FabricBlueprintSelectorScreen());
        ClientPlayNetworking.send(new FabricBlueprintGuiNetworking.BlueprintListRequestPayload(true));
    }

    public static void refreshOpenScreen() {
        if (MinecraftClient.getInstance().currentScreen instanceof FabricBlueprintSelectorScreen screen) {
            screen.rebuildWidgets();
        }
    }

    @Override
    protected void init() {
        rebuildWidgets();
    }

    protected void rebuildWidgets() {
        clearChildren();

        int left = Math.max(20, width / 2 - 190);
        int top = Math.max(28, height / 2 - 116);
        int listWidth = 180;
        int detailsLeft = left + listWidth + 20;
        List<BlueprintSummary> summaries = FabricBlueprintClientCache.blueprints();

        TextFieldWidget search = new TextFieldWidget(textRenderer, left, top + 22, listWidth, 20, Text.literal("Search"));
        search.setText(searchText);
        search.setChangedListener(value -> {
            searchText = value;
            page = 0;
            requestPage(false);
        });
        addDrawableChild(search);

        addDrawableChild(ButtonWidget.builder(Text.literal("Source: " + sourceFilter.name().toLowerCase()), ignored -> {
                    sourceFilter = next(sourceFilter);
                    page = 0;
                    requestPage(false);
                    rebuildWidgets();
                })
                .dimensions(left, top + 46, 86, 20)
                .build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Warn: " + warningFilter.name().toLowerCase()), ignored -> {
                    warningFilter = next(warningFilter);
                    page = 0;
                    requestPage(false);
                    rebuildWidgets();
                })
                .dimensions(left + 94, top + 46, 86, 20)
                .build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Sort: " + sortMode.name().toLowerCase()), ignored -> {
                    sortMode = next(sortMode);
                    page = 0;
                    requestPage(false);
                    rebuildWidgets();
                })
                .dimensions(left, top + 70, listWidth, 20)
                .build());

        int row = 0;
        for (BlueprintSummary summary : summaries) {
            if (row >= 7) {
                break;
            }

            int buttonY = top + 96 + row * 22;
            ButtonWidget button = ButtonWidget.builder(Text.literal(summary.name() + " [" + summary.sourceType() + "]" + (summary.warningCount() > 0 ? " !" : "")), ignored -> {
                        FabricBlueprintClientCache.selectLocally(summary.id());
                        rebuildWidgets();
                    })
                    .dimensions(left, buttonY, listWidth, 20)
                    .build();
            if (summary.id().equals(FabricBlueprintClientCache.selectedBlueprintId())) {
                button.active = false;
            }
            addDrawableChild(button);
            row++;
        }

        ButtonWidget previous = ButtonWidget.builder(Text.literal("Previous"), ignored -> {
                    page = Math.max(0, FabricBlueprintClientCache.page() - 1);
                    requestPage(false);
                })
                .dimensions(left, top + 254, 86, 20)
                .build();
        previous.active = FabricBlueprintClientCache.hasPrevious();
        addDrawableChild(previous);
        ButtonWidget next = ButtonWidget.builder(Text.literal("Next"), ignored -> {
                    page = FabricBlueprintClientCache.page() + 1;
                    requestPage(false);
                })
                .dimensions(left + 94, top + 254, 86, 20)
                .build();
        next.active = FabricBlueprintClientCache.hasNext();
        addDrawableChild(next);

        int rotationY = top + 116;
        for (int index = 0; index < ROTATIONS.length; index++) {
            int degrees = ROTATIONS[index];
            ButtonWidget button = ButtonWidget.builder(Text.literal(degrees + "\u00b0"), ignored -> {
                        FabricBlueprintClientCache.setRotationDegrees(degrees);
                        rebuildWidgets();
                    })
                    .dimensions(detailsLeft + index * 42, rotationY, 38, 20)
                    .build();
            if (FabricBlueprintClientCache.rotationDegrees() == degrees) {
                button.active = false;
            }
            addDrawableChild(button);
        }

        ButtonWidget selectButton = ButtonWidget.builder(Text.translatable("screen.blockforge_connector.select"), ignored -> submitSelection())
                .dimensions(detailsLeft, top + 162, 82, 20)
                .build();
        selectButton.active = FabricBlueprintClientCache.selectedBlueprint().isPresent();
        addDrawableChild(selectButton);

        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.blockforge_connector.close"), ignored -> close())
                .dimensions(detailsLeft + 90, top + 254, 82, 20)
                .build());
    }

    private void requestPage(boolean openScreen) {
        FabricBlueprintClientCache.beginLoading();
        ClientPlayNetworking.send(new FabricBlueprintGuiNetworking.BlueprintListRequestPayload(
                openScreen,
                searchText,
                sourceFilter,
                warningFilter,
                sortMode,
                page,
                BlueprintGuiQuery.DEFAULT_PAGE_SIZE
        ));
    }

    private void submitSelection() {
        FabricBlueprintClientCache.selectedBlueprint().ifPresent(summary ->
                ClientPlayNetworking.send(new FabricBlueprintGuiNetworking.SelectBlueprintRequestPayload(
                        summary.id(),
                        FabricBlueprintClientCache.rotationDegrees()
                ))
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int left = Math.max(20, width / 2 - 190);
        int top = Math.max(28, height / 2 - 116);
        int detailsLeft = left + 200;
        int panelRight = detailsLeft + 180;
        int panelBottom = top + 288;

        context.fill(0, 0, width, height, 0x99000000);
        context.fill(left - 12, top - 18, panelRight + 12, panelBottom + 12, 0xF0101820);
        context.fill(left - 8, top - 14, panelRight + 8, panelBottom + 8, 0xE8182630);
        context.drawBorder(left - 12, top - 18, panelRight + 12 - (left - 12), panelBottom + 12 - (top - 18), 0xFF4AA8C8);

        context.drawText(textRenderer, title, left, top - 10, 0xFF9CEBFF, false);
        context.drawText(textRenderer, Text.translatable("screen.blockforge_connector.blueprints"), left, top + 8, 0xFFE7F7FF, false);

        if (FabricBlueprintClientCache.loading()) {
            context.drawText(textRenderer, Text.translatable("screen.blockforge_connector.loading"), left, top + 98, 0xFFFFD37A, false);
        } else if (FabricBlueprintClientCache.blueprints().isEmpty()) {
            drawWrapped(context, Text.literal(searchText.isBlank()
                    ? "No blueprints loaded. Run /blockforge examples install and /blockforge reload."
                    : "No blueprints match your search."), left, top + 98, 180, 0xFFFFD37A);
        }
        context.drawText(textRenderer, Text.literal("Page "
                + (FabricBlueprintClientCache.totalPages() == 0 ? 0 : FabricBlueprintClientCache.page() + 1)
                + " / "
                + FabricBlueprintClientCache.totalPages()
                + " | Total "
                + FabricBlueprintClientCache.totalItems()
                + " blueprints"), left, top + 236, 0xFFB6C7D4, false);

        renderDetails(context, detailsLeft, top);

        if (!FabricBlueprintClientCache.message().isBlank()) {
            drawWrapped(context, Text.literal(FabricBlueprintClientCache.message()), left, panelBottom - 34, 350, 0xFFFFD37A);
        }

        drawWrapped(context, Text.translatable("screen.blockforge_connector.hint"), left, panelBottom - 18, 360, 0xFFB6C7D4);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawWrapped(DrawContext context, Text text, int x, int y, int width, int color) {
        int lineY = y;
        for (OrderedText line : textRenderer.wrapLines(text, width)) {
            context.drawText(textRenderer, line, x, lineY, color, false);
            lineY += textRenderer.fontHeight + 1;
        }
    }

    private void renderDetails(DrawContext context, int x, int y) {
        context.drawText(textRenderer, Text.translatable("screen.blockforge_connector.details"), x, y + 8, 0xFFE7F7FF, false);
        BlueprintSummary summary = FabricBlueprintClientCache.selectedBlueprint().orElse(null);
        if (summary == null) {
            context.drawText(textRenderer, Text.translatable("screen.blockforge_connector.no_selection"), x, y + 30, 0xFFFFD37A, false);
            return;
        }

        context.drawText(textRenderer, Text.literal(summary.name()), x, y + 28, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal(summary.id()), x, y + 40, 0xFF9AA8B5, false);
        context.drawText(textRenderer, Text.literal("source=" + summary.sourceType()
                + (summary.sourceId().isBlank() ? "" : " | id=" + summary.sourceId())), x, y + 52, 0xFFB6C7D4, false);
        context.drawText(textRenderer, Text.translatable("screen.blockforge_connector.size", summary.sizeLabel()), x, y + 64, 0xFFC9D7E2, false);
        context.drawText(textRenderer, Text.translatable("screen.blockforge_connector.blocks", summary.blockCount()), x, y + 76, 0xFFC9D7E2, false);
        context.drawText(textRenderer, Text.literal(summary.warningCount() > 0 ? "warnings=" + summary.warningCount() : "warnings=0"), x, y + 88, summary.warningCount() > 0 ? 0xFFFFD37A : 0xFFB6C7D4, false);
        context.drawText(textRenderer, Text.literal("schemaVersion=" + summary.schemaVersion()), x, y + 100, 0xFFC9D7E2, false);
        context.drawText(
                textRenderer,
                Text.translatable("screen.blockforge_connector.block_states", summary.hasBlockStates()),
                x,
                y + 112,
                summary.hasBlockStates() ? 0xFF8EF0B4 : 0xFFB6C7D4,
                false
        );
        context.drawText(textRenderer, Text.translatable("screen.blockforge_connector.rotation"), x, y + 112, 0xFFE7F7FF, false);
        context.drawText(textRenderer, Text.literal("Material sources: "
                + (FabricMaterialSourceSettings.enableNearbyContainers() ? "Nearby containers enabled" : "Player inventory only")), x, y + 140, 0xFFC9D7E2, false);
        context.drawText(textRenderer, Text.literal("Source priority="
                + FabricMaterialSourceSettings.materialSourcePriority()
                + " | radius="
                + FabricMaterialSourceSettings.nearbyContainerSearchRadius()), x, y + 152, 0xFF9AA8B5, false);
    }

    private static BlueprintSourceFilter next(BlueprintSourceFilter value) {
        BlueprintSourceFilter[] values = BlueprintSourceFilter.values();
        return values[(value.ordinal() + 1) % values.length];
    }

    private static BlueprintWarningFilter next(BlueprintWarningFilter value) {
        BlueprintWarningFilter[] values = BlueprintWarningFilter.values();
        return values[(value.ordinal() + 1) % values.length];
    }

    private static BlueprintSortMode next(BlueprintSortMode value) {
        BlueprintSortMode[] values = BlueprintSortMode.values();
        return values[(value.ordinal() + 1) % values.length];
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
