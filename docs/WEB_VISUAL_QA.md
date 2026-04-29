# Web Visual QA

Status: Browser visual QA pending until manually checked.

Use this checklist before promoting `v3.1.0-alpha.1` from Alpha candidate to a public release.

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
| Render mode Auto | Small models use mesh; larger models use instanced | pending |
| Render mode Mesh | Preview renders with individual meshes | pending |
| Render mode Instanced | Preview renders with InstancedMesh groups | pending |
| 100 block model | Model renders and remains interactive | pending |
| 500 block model | Model renders without obvious stutter | pending |
| 1000+ block model | Model does not freeze the browser | pending |
| Export Preview PNG | PNG export completes from current camera angle | pending |
| PNG file non-empty | Exported file opens and has visible model pixels | pending |
| Import `.schem` then screenshot | Imported schematic can be captured | pending |
| Local tower screenshot | Local Rule Generator tower can be captured | pending |
| Blueprint Pack screenshot | Imported pack model can be captured | pending |

## v1.9.1 / v2.0 AI Generation Visual QA

Status values: `passed`, `pending`, `needs manual check`.

| Flow | Expected Result | Status |
|---|---|---|
| AI Generator UI disabled without API key | External AI section shows setup required and disabled button | pending |
| External AI ready with API status | Status endpoint reports configured provider and UI enables external generation | needs manual check |
| AI Generator request state | Generate with AI button shows generating state during request | needs manual check |
| AI Generator success state | Validated structure plan summary appears and preview updates | needs manual check |
| AI Generator error state | Friendly error appears without replacing current preview | needs manual check |
| Validation failed state | Validation errors are shown and preview remains unchanged | needs manual check |
| 3D preview not clipped | tiny_platform, tower, bridge, cottage, and imported `.schem` fit camera reasonably | pending |
| Screenshot success state | Export Preview PNG shows success message after file creation | pending |
| Screenshot failure state | Export Preview PNG shows friendly error if canvas export fails | needs manual check |
| AI prompt presets | Preset dropdown filters and fills prompts | pending |
| Multi-candidate generation | Candidate cards render and best candidate is marked | pending |
| Quality score | Score badge and breakdown are readable | pending |
| Generation history | Recent generations can be searched and loaded | pending |
| Local blueprint library | Saved blueprints can be searched, favorited, loaded, and exported | pending |
| Import job queue | Jobs show pending/running/success/warning/error states | pending |
| IndexedDB persistence | Local data survives reload in same browser profile | needs manual check |
| Worker fallback | Import worker fallback shows friendly errors when worker is unavailable | needs manual check |

## Limitations

- This checklist is manual until browser screenshot automation is added.
- Local Rule Generator is deterministic and local-only; no prompt is sent to any server.
- External AI Generation Alpha requires a server runtime and server-side API key.
- Minecraft vanilla texture files are not bundled; material styling is procedural.
# v3.1.0-alpha.1 Visual QA Checklist

Status values must be `pending`, `needs manual check`, or `passed`. Do not mark
items passed without manual browser verification.

| Area | Check | Status |
|---|---|---|
| Viewport | Chrome 1440px | pending |
| Viewport | Chrome 1024px | pending |
| Import | Blueprint JSON | pending |
| Import | invalid Blueprint JSON | pending |
| Import | `.schem` | pending |
| Import | `.blockforgepack.zip` | pending |
| Import | `.litematic` alpha | pending |
| Generation | Local Rule Generator tower | pending |
| AI | Prompt Presets | pending |
| AI | Multi-candidate generation | pending |
| AI | Quality Score | pending |
| AI | Structure Plan Viewer | pending |
| AI | Candidate Compare | pending |
| Library | Generation History | pending |
| Library | Local Blueprint Library | pending |
| Gallery | Gallery grid/search/export | pending |
| Import Jobs | Import Job Queue | pending |
| Preview | Preview PNG export | pending |
| Rendering | Render mode Auto / Mesh / Instanced | pending |
| Layout | Dark UI contrast readable | pending |
| Layout | ExportPanel not overflowing | pending |
| Validation | Validation report readable | pending |
| Preview | 3D preview not clipped | pending |
