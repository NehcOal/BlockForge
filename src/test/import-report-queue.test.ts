import { describe, expect, it } from "vitest";
import { createImportReport } from "@/lib/import/importReport";
import { ImportJobQueue } from "@/lib/import/importJobQueue";

describe("import report and queue", () => {
  it("creates success, warning, and error reports", () => {
    expect(createImportReport({ id: "a", sourceType: "schem", messages: [{ severity: "info", message: "ok" }] }).status).toBe("success");
    expect(createImportReport({ id: "b", sourceType: "schem", messages: [{ severity: "warning", message: "ignored entity" }] }).status).toBe("warning");
    expect(createImportReport({ id: "c", sourceType: "schem", messages: [{ severity: "error", message: "invalid gzip" }] }).status).toBe("error");
  });

  it("tracks job status transitions", () => {
    const queue = new ImportJobQueue();
    queue.enqueue({ id: "job-1" });
    queue.update("job-1", "running");
    queue.update("job-1", "success");
    expect(queue.list()[0].status).toBe("success");
  });
});
