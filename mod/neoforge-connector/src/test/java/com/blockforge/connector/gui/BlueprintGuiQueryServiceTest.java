package com.blockforge.connector.gui;

import com.blockforge.common.gui.BlueprintGuiQuery;
import com.blockforge.common.gui.BlueprintGuiQueryService;
import com.blockforge.common.gui.BlueprintSortMode;
import com.blockforge.common.gui.BlueprintSourceFilter;
import com.blockforge.common.gui.BlueprintSummary;
import com.blockforge.common.gui.BlueprintWarningFilter;
import com.blockforge.common.gui.PagedBlueprintResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlueprintGuiQueryServiceTest {
    private final List<BlueprintSummary> fixtures = List.of(
            summary("loose_house", "Small House", 20, "loose", "", 0, List.of("starter", "小屋")),
            summary("starter/tower", "Medieval Tower", 120, "pack", "starter", 2, List.of("tower")),
            summary("schem/bridge", "Stone Bridge", 64, "schematic", "bridge.schem", 1, List.of("bridge")),
            summary("starter/cottage", "Cottage", 32, "pack", "starter", 0, List.of("cottage"))
    );

    @Test
    void searchesAndFilters() {
        assertEquals("starter/tower", BlueprintGuiQueryService.query(fixtures, query("tower")).items().getFirst().id());
        assertEquals("loose_house", BlueprintGuiQueryService.query(fixtures, query("小屋")).items().getFirst().id());
        assertEquals(2, BlueprintGuiQueryService.query(fixtures, new BlueprintGuiQuery("", BlueprintSourceFilter.PACK, BlueprintWarningFilter.ALL, BlueprintSortMode.NAME_ASC, 0, 8)).totalItems());
        assertEquals(2, BlueprintGuiQueryService.query(fixtures, new BlueprintGuiQuery("", BlueprintSourceFilter.ALL, BlueprintWarningFilter.WITH_WARNINGS, BlueprintSortMode.NAME_ASC, 0, 8)).totalItems());
    }

    @Test
    void sortsAndPaginates() {
        assertEquals("starter/tower", BlueprintGuiQueryService.query(fixtures, new BlueprintGuiQuery("", BlueprintSourceFilter.ALL, BlueprintWarningFilter.ALL, BlueprintSortMode.BLOCKS_DESC, 0, 8)).items().getFirst().id());
        PagedBlueprintResult last = BlueprintGuiQueryService.query(fixtures, new BlueprintGuiQuery("", BlueprintSourceFilter.ALL, BlueprintWarningFilter.ALL, BlueprintSortMode.NAME_ASC, 99, 2));
        assertEquals(1, last.page());
        assertTrue(last.hasPrevious());
    }

    private static BlueprintGuiQuery query(String searchText) {
        return new BlueprintGuiQuery(searchText, BlueprintSourceFilter.ALL, BlueprintWarningFilter.ALL, BlueprintSortMode.NAME_ASC, 0, 8);
    }

    private static BlueprintSummary summary(String id, String name, int blocks, String sourceType, String sourceId, int warnings, List<String> tags) {
        return new BlueprintSummary(id, name, 2, 1, 1, 1, blocks, false, sourceType, sourceId, warnings, tags);
    }
}
