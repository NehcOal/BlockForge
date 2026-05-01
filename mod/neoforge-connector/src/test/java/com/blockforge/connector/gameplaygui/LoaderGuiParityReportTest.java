package com.blockforge.connector.gameplaygui;

import com.blockforge.common.gameplaygui.LoaderGuiParityReport;
import com.blockforge.common.gameplaygui.LoaderGuiParityStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoaderGuiParityReportTest {
    @Test
    void partialCriticalGuiKeepsReleaseInDraftPrState() {
        LoaderGuiParityReport report = new LoaderGuiParityReport(
                "fabric",
                LoaderGuiParityStatus.PARTIAL,
                LoaderGuiParityStatus.PARTIAL,
                LoaderGuiParityStatus.PARTIAL,
                LoaderGuiParityStatus.ALPHA,
                LoaderGuiParityStatus.PARTIAL,
                LoaderGuiParityStatus.ALPHA,
                LoaderGuiParityStatus.BETA,
                LoaderGuiParityStatus.BETA,
                true,
                List.of("Loader GUI parity remains partial.")
        );

        assertTrue(report.hasBlockingGaps());
        assertEquals("draft-pr-only", report.releaseReadinessLabel());
    }
}
