# Web Visual QA

Status: Browser visual QA pending until manually checked.

Use this checklist before promoting `v1.8.0-alpha.1` from Alpha candidate to a public release.

## Viewports

| Browser | Width | Status | Notes |
|---|---:|---|---|
| Chrome | 1440px | pending | Desktop layout, ExportPanel, 3D preview |
| Chrome | 1024px | pending | Tablet/narrow desktop layout |

## Workbench Flows

| Flow | Expected Result | Status |
|---|---|---|
| Import Blueprint JSON | Import summary shows type, id/name, size, blocks, palette, warnings, errors, filename | pending |
| Import invalid Blueprint JSON | User-readable error appears; developer details are expandable | pending |
| Import Sponge `.schem` | Summary renders, parser warnings are visible | pending |
| Import `.blockforgepack.zip` | Pack summary and validation report render without layout overflow | pending |
| Local Rule Generator: tower | Local rule-based output renders in 3D and can export | pending |
| Local Rule Generator: cottage | Local rule-based output renders in 3D and can export | pending |
| Local Rule Generator: bridge | Local rule-based output renders in 3D and can export | pending |
| 3D preview | Canvas is non-empty and model is framed | pending |
| ExportPanel | Data formats, Minecraft command formats, Minecraft structure formats, and Pack formats stay readable | pending |
| Validation report | Model, Size, Origin, Palette, Blocks, Coordinates, Duplicate blocks, and Missing palette references are readable | pending |
| Dark mode contrast | Text and badges are legible on the current dark voxel theme | pending |

## Limitations

- This checklist is manual until browser screenshot automation is added.
- Local Rule Generator is deterministic and local-only; no prompt is sent to any server.
- External AI API adapter is planned for v2.0.
