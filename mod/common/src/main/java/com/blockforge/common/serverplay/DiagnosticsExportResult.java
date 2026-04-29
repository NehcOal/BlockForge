package com.blockforge.common.serverplay;

import java.nio.file.Path;

public record DiagnosticsExportResult(
        boolean written,
        Path path,
        String warning
) {
    public DiagnosticsExportResult {
        warning = warning == null ? "" : warning;
    }
}
