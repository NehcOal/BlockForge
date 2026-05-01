package com.blockforge.common.serverplay;

import java.nio.file.Path;

public record AuditJsonlWriteResult(
        boolean written,
        Path path,
        String warning
) {
    public AuditJsonlWriteResult {
        warning = warning == null ? "" : warning;
    }
}
