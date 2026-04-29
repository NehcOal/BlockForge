# Import Pipeline

Status: `v3.5.0-alpha.1` Alpha.

## Import Report

All import flows should converge on `ImportReport`:

- source type
- source filename
- status: success, warning, error
- imported item count
- warning/error counts
- field/path messages

## Import Job Queue

Import jobs track:

- pending
- running
- success
- warning
- error
- cancelled

## Worker Fallback

The import worker is optional. If worker execution is unavailable or fails, the
main-thread fallback should return a friendly error or complete the import.

## Hardening Targets

- invalid gzip
- invalid NBT
- unsupported schematic version
- large volume
- large palette
- unknown block ids
- invalid block properties
- zip entry limits
- duplicate pack ids
- missing blueprint paths
- path traversal

Manual browser QA is pending.
# v3.0 Import Pipeline Notes

The import pipeline now includes Litematica alpha and Gallery bundle reports.
Blueprint JSON, `.schem`, `.litematic`, `.blockforgepack.zip`, workspace, and
gallery imports should all produce an ImportReport before entering preview,
library, export, or connector workflows.
