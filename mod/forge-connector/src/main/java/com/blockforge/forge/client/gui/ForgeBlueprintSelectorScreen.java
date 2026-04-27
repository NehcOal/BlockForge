package com.blockforge.forge.client.gui;

import com.blockforge.common.gui.BlueprintSummary;
import com.blockforge.common.gui.BlueprintGuiQuery;
import com.blockforge.common.gui.BlueprintSortMode;
import com.blockforge.common.gui.BlueprintSourceFilter;
import com.blockforge.common.gui.BlueprintWarningFilter;
import com.blockforge.forge.material.source.ForgeMaterialSourceSettings;
import com.blockforge.forge.network.ForgeBlueprintGuiNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ForgeBlueprintSelectorScreen extends Screen {
    private static final int[] ROTATIONS = {0, 90, 180, 270};
    private String searchText = "";
    private BlueprintSourceFilter sourceFilter = BlueprintSourceFilter.ALL;
    private BlueprintWarningFilter warningFilter = BlueprintWarningFilter.ALL;
    private BlueprintSortMode sortMode = BlueprintSortMode.NAME_ASC;
    private int page = 0;

    public ForgeBlueprintSelectorScreen() {
        super(Component.translatable("screen.blockforge_connector.blueprint_selector"));
    }

    public static void openAndRequestList() {
        Minecraft client = Minecraft.getInstance();
        if (client.getConnection() == null) {
            return;
        }

        ForgeBlueprintClientCache.beginLoading();
        client.setScreen(new ForgeBlueprintSelectorScreen());
        ForgeBlueprintGuiNetworking.requestBlueprintList(false);
    }

    public static void refreshOpenScreen() {
        if (Minecraft.getInstance().screen instanceof ForgeBlueprintSelectorScreen screen) {
            screen.rebuildWidgets();
        }
    }

    @Override
    protected void init() {
        rebuildWidgets();
    }

    @Override
    protected void rebuildWidgets() {
        clearWidgets();

        int left = Math.max(20, width / 2 - 190);
        int top = Math.max(28, height / 2 - 116);
        int listWidth = 180;
        int detailsLeft = left + listWidth + 20;
        List<BlueprintSummary> summaries = ForgeBlueprintClientCache.blueprints();

        EditBox search = new EditBox(font, left, top + 22, listWidth, 20, Component.literal("Search"));
        search.setValue(searchText);
        search.setResponder(value -> {
            searchText = value;
            page = 0;
            requestPage(false);
        });
        addRenderableWidget(search);

        addRenderableWidget(Button.builder(Component.literal("Source: " + sourceFilter.name().toLowerCase()), ignored -> {
                    sourceFilter = next(sourceFilter);
                    page = 0;
                    requestPage(false);
                    rebuildWidgets();
                })
                .bounds(left, top + 46, 86, 20)
                .build());
        addRenderableWidget(Button.builder(Component.literal("Warn: " + warningFilter.name().toLowerCase()), ignored -> {
                    warningFilter = next(warningFilter);
                    page = 0;
                    requestPage(false);
                    rebuildWidgets();
                })
                .bounds(left + 94, top + 46, 86, 20)
                .build());
        addRenderableWidget(Button.builder(Component.literal("Sort: " + sortMode.name().toLowerCase()), ignored -> {
                    sortMode = next(sortMode);
                    page = 0;
                    requestPage(false);
                    rebuildWidgets();
                })
                .bounds(left, top + 70, listWidth, 20)
                .build());

        int row = 0;
        for (BlueprintSummary summary : summaries) {
            if (row >= 7) {
                break;
            }

            int buttonY = top + 96 + row * 22;
            Button button = Button.builder(Component.literal(summary.name() + " [" + summary.sourceType() + "]" + (summary.warningCount() > 0 ? " !" : "")), ignored -> {
                        ForgeBlueprintClientCache.selectLocally(summary.id());
                        rebuildWidgets();
                    })
                    .bounds(left, buttonY, listWidth, 20)
                    .build();
            if (summary.id().equals(ForgeBlueprintClientCache.selectedBlueprintId())) {
                button.active = false;
            }
            addRenderableWidget(button);
            row++;
        }

        Button previous = Button.builder(Component.literal("Previous"), ignored -> {
                    page = Math.max(0, ForgeBlueprintClientCache.page() - 1);
                    requestPage(false);
                })
                .bounds(left, top + 254, 86, 20)
                .build();
        previous.active = ForgeBlueprintClientCache.hasPrevious();
        addRenderableWidget(previous);
        Button next = Button.builder(Component.literal("Next"), ignored -> {
                    page = ForgeBlueprintClientCache.page() + 1;
                    requestPage(false);
                })
                .bounds(left + 94, top + 254, 86, 20)
                .build();
        next.active = ForgeBlueprintClientCache.hasNext();
        addRenderableWidget(next);

        int rotationY = top + 140;
        for (int index = 0; index < ROTATIONS.length; index++) {
            int degrees = ROTATIONS[index];
            Button button = Button.builder(Component.literal(degrees + "\u00b0"), ignored -> {
                        ForgeBlueprintClientCache.setRotationDegrees(degrees);
                        rebuildWidgets();
                    })
                    .bounds(detailsLeft + index * 42, rotationY, 38, 20)
                    .build();
            if (ForgeBlueprintClientCache.rotationDegrees() == degrees) {
                button.active = false;
            }
            addRenderableWidget(button);
        }

        Button selectButton = Button.builder(Component.translatable("screen.blockforge_connector.select"), ignored -> submitSelection())
                .bounds(detailsLeft, top + 166, 82, 20)
                .build();
        selectButton.active = ForgeBlueprintClientCache.selectedBlueprint().isPresent();
        addRenderableWidget(selectButton);

        addRenderableWidget(Button.builder(Component.translatable("screen.blockforge_connector.close"), ignored -> onClose())
                .bounds(detailsLeft + 90, top + 254, 82, 20)
                .build());
    }

    private void requestPage(boolean openScreen) {
        ForgeBlueprintClientCache.beginLoading();
        ForgeBlueprintGuiNetworking.requestBlueprintList(
                openScreen,
                searchText,
                sourceFilter,
                warningFilter,
                sortMode,
                page,
                BlueprintGuiQuery.DEFAULT_PAGE_SIZE
        );
    }

    private void submitSelection() {
        ForgeBlueprintClientCache.selectedBlueprint().ifPresent(summary ->
                ForgeBlueprintGuiNetworking.requestSelection(summary.id(), ForgeBlueprintClientCache.rotationDegrees())
        );
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int left = Math.max(20, width / 2 - 190);
        int top = Math.max(28, height / 2 - 116);
        int detailsLeft = left + 200;
        int panelRight = detailsLeft + 180;
        int panelBottom = top + 288;

        graphics.fill(0, 0, width, height, 0x99000000);
        graphics.fill(left - 12, top - 18, panelRight + 12, panelBottom + 12, 0xF0101820);
        graphics.fill(left - 8, top - 14, panelRight + 8, panelBottom + 8, 0xE8182630);
        graphics.renderOutline(left - 12, top - 18, panelRight + 12 - (left - 12), panelBottom + 12 - (top - 18), 0xFF4AA8C8);

        graphics.drawString(font, title, left, top - 10, 0xFF9CEBFF, false);
        graphics.drawString(font, Component.translatable("screen.blockforge_connector.blueprints"), left, top + 8, 0xFFE7F7FF, false);

        if (ForgeBlueprintClientCache.loading()) {
            graphics.drawString(font, Component.translatable("screen.blockforge_connector.loading"), left, top + 98, 0xFFFFD37A, false);
        } else if (ForgeBlueprintClientCache.blueprints().isEmpty()) {
            graphics.drawWordWrap(
                    font,
                    Component.literal(searchText.isBlank()
                            ? "No blueprints loaded. Run /blockforge examples install and /blockforge reload."
                            : "No blueprints match your search."),
                    left,
                    top + 98,
                    180,
                    0xFFFFD37A
            );
        }
        graphics.drawString(font, "Page "
                + (ForgeBlueprintClientCache.totalPages() == 0 ? 0 : ForgeBlueprintClientCache.page() + 1)
                + " / "
                + ForgeBlueprintClientCache.totalPages()
                + " | Total "
                + ForgeBlueprintClientCache.totalItems()
                + " blueprints", left, top + 236, 0xFFB6C7D4, false);

        renderDetails(graphics, detailsLeft, top);

        if (!ForgeBlueprintClientCache.message().isBlank()) {
            graphics.drawWordWrap(font, Component.literal(ForgeBlueprintClientCache.message()), left, panelBottom - 34, 350, 0xFFFFD37A);
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
        BlueprintSummary summary = ForgeBlueprintClientCache.selectedBlueprint().orElse(null);
        if (summary == null) {
            graphics.drawString(font, Component.translatable("screen.blockforge_connector.no_selection"), x, y + 30, 0xFFFFD37A, false);
            return;
        }

        graphics.drawString(font, summary.name(), x, y + 28, 0xFFFFFFFF, false);
        graphics.drawString(font, summary.id(), x, y + 40, 0xFF9AA8B5, false);
        graphics.drawString(font, Component.literal("source=" + summary.sourceType()
                + (summary.sourceId().isBlank() ? "" : " | id=" + summary.sourceId())), x, y + 52, 0xFFB6C7D4, false);
        graphics.drawString(font, Component.translatable("screen.blockforge_connector.size", summary.sizeLabel()), x, y + 64, 0xFFC9D7E2, false);
        graphics.drawString(font, Component.translatable("screen.blockforge_connector.blocks", summary.blockCount()), x, y + 76, 0xFFC9D7E2, false);
        graphics.drawString(font, Component.literal(summary.warningCount() > 0 ? "warnings=" + summary.warningCount() : "warnings=0"), x, y + 88, summary.warningCount() > 0 ? 0xFFFFD37A : 0xFFB6C7D4, false);
        graphics.drawString(font, "schemaVersion=" + summary.schemaVersion(), x, y + 100, 0xFFC9D7E2, false);
        graphics.drawString(
                font,
                Component.translatable("screen.blockforge_connector.block_states", summary.hasBlockStates()),
                x,
                y + 112,
                summary.hasBlockStates() ? 0xFF8EF0B4 : 0xFFB6C7D4,
                false
        );
        graphics.drawString(
                font,
                Component.literal("Material sources: "
                        + (ForgeMaterialSourceSettings.enableNearbyContainers()
                        ? "Nearby containers enabled"
                        : "Player inventory only")),
                x,
                y + 106,
                0xFFC9D7E2,
                false
        );
        graphics.drawString(
                font,
                Component.literal("Source priority=" + ForgeMaterialSourceSettings.materialSourcePriority()
                        + " | radius=" + ForgeMaterialSourceSettings.nearbyContainerSearchRadius()),
                x,
                y + 118,
                0xFFB6C7D4,
                false
        );
        graphics.drawString(font, Component.translatable("screen.blockforge_connector.rotation"), x, y + 130, 0xFFE7F7FF, false);
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
    public boolean isPauseScreen() {
        return false;
    }
}
