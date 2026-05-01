import type { PresetId } from "@/types/blueprint";

export type Locale = "en" | "zh";

export type PresetCopy = {
  name: string;
  description: string;
};

export type AppCopy = {
  hero: {
    eyebrow: string;
    subtitle: string;
    description: string;
    steps: string[];
  };
  language: {
    label: string;
    english: string;
    chinese: string;
  };
  prompt: {
    title: string;
    description: string;
    label: string;
    placeholder: string;
    button: string;
    generatedPrefix: string;
    emptyPrefix: string;
    generatedModelPrefix: string;
    generatorHint: string;
  };
  presets: {
    title: string;
    description: string;
    items: Partial<Record<PresetId, PresetCopy>>;
  };
  preview: {
    eyebrow: string;
    promptLabel: string;
    controls: string;
    fallbackPrompt: string;
    modelSize: string;
    modelBlocks: string;
    promptState: string;
    generated: string;
    draft: string;
    blockTypes: string;
    empty: string;
  };
  export: {
    title: string;
    description: string;
    json: string;
    blueprintJson: string;
    blueprintV2Json: string;
    blueprintPack: string;
    importBlueprintPack: string;
    spongeSchematic: string;
    importSpongeSchematic: string;
    importBlueprintJson: string;
    mcfunction: string;
    datapack: string;
    blueprintFilesGroup: string;
    minecraftInstallGroup: string;
    interopImportGroup: string;
    validationLabel: string;
    errorsLabel: string;
    warningsLabel: string;
    hint: string;
    blueprintHint: string;
    blueprintV2Hint: string;
    blueprintPackHint: string;
    importSuccess: string;
    importError: string;
    schematicImportSuccess: string;
    schematicImportError: string;
    schematicHint: string;
    datapackHint: string;
  };
  footer: {
    status: string;
    roadmap: string;
  };
};

export const appCopy: Record<Locale, AppCopy> = {
  en: {
    hero: {
      eyebrow: "Voxel blueprint generator",
      subtitle: "Generate Minecraft-style voxel buildings from text prompts.",
      description: "A local-first voxel blueprint generator for Minecraft builders.",
      steps: ["Prompt", "Preset", "Preview"]
    },
    language: {
      label: "Language",
      english: "EN",
      chinese: "中文"
    },
    prompt: {
      title: "Generate Blueprint",
      description:
        "Start with a prompt now, then connect it to voxel generation later.",
      label: "Building prompt",
      placeholder: 'Describe a building, e.g. "a medieval tower with glass windows"',
      button: "Generate Blueprint",
      generatedPrefix: "Blueprint generated from prompt:",
      emptyPrefix: "No prompt generated yet. Current preset:",
      generatedModelPrefix: "Local model generated:",
      generatorHint: "The v2.0 generator is deterministic and local-first."
    },
    presets: {
      title: "Preset Library",
      description: "Choose a starter blueprint for the preview state.",
      items: {
        "medieval-tower": {
          name: "Medieval Tower",
          description:
            "A stone watchtower with glass windows, torchlight, and crenellated battlements."
        },
        "small-cottage": {
          name: "Small Cottage",
          description:
            "A cozy timber cottage with log corners, plank walls, windows, and a stone base."
        },
        "dungeon-entrance": {
          name: "Dungeon Entrance",
          description:
            "A reinforced stone doorway with an arched entrance and torch-lit side walls."
        },
        "stone-bridge": {
          name: "Stone Bridge",
          description:
            "A low stone bridge crossing a water channel with cobbled supports and rails."
        },
        "pixel-statue": {
          name: "Pixel Statue",
          description:
            "A blocky pixel-art statue with a stone base, blue legs, red body, and gold crown."
        }
      }
    },
    preview: {
      eyebrow: "3D Browser Preview",
      promptLabel: "Preview prompt:",
      controls: "Orbit, pan, and zoom enabled",
      fallbackPrompt: "Choose a preset or describe a building to stage the first blueprint.",
      modelSize: "Model size",
      modelBlocks: "Model blocks",
      promptState: "Prompt state",
      generated: "Generated",
      draft: "Draft",
      blockTypes: "Block types",
      empty: "No voxel blocks to preview yet."
    },
    export: {
      title: "Export",
      description: "Download voxel data or Minecraft function commands.",
      json: "Export JSON",
      blueprintJson: "Export Blueprint JSON v1",
      blueprintV2Json: "Export Blueprint JSON v2",
      blueprintPack: "Export Blueprint Pack",
      importBlueprintPack: "Import Blueprint Pack",
      spongeSchematic: "Export .schem",
      importSpongeSchematic: "Import .schem",
      importBlueprintJson: "Import Blueprint JSON",
      mcfunction: "Export .mcfunction",
      datapack: "Export Data Pack ZIP",
      blueprintFilesGroup: "Blueprint files",
      minecraftInstallGroup: "Minecraft install",
      interopImportGroup: "Interop import",
      validationLabel: "Validation",
      errorsLabel: "Errors",
      warningsLabel: "Warnings",
      hint:
        "Export as Minecraft function commands for Java Edition datapack workflows.",
      blueprintHint: "Stable blueprint file for BlockForge Mod Connector.",
      blueprintV2Hint:
        "Supports Minecraft BlockState properties for the newer BlockForge Connector.",
      blueprintPackHint:
        "Share multiple Blueprint JSON v2 files as a .blockforgepack.zip.",
      importSuccess: "Imported pack",
      importError: "Pack import failed",
      schematicImportSuccess: "Imported schematic",
      schematicImportError: "Schematic import failed",
      schematicHint: "Sponge .schem v3 interop for WorldEdit / FAWE workflows.",
      datapackHint:
        "Download a Minecraft Java 1.21.1 data pack for the datapacks folder."
    },
    footer: {
      status: "Project status: v0.1 MVP ready for release polish.",
      roadmap: "Roadmap: datapack ZIP -> .schem export -> Local Prompt Rule Generator."
    }
  },
  zh: {
    hero: {
      eyebrow: "Voxel 蓝图生成器",
      subtitle: "从文本提示生成 Minecraft 风格的 voxel 方块建筑。",
      description: "一个本地优先、面向 Minecraft 建筑玩家的方块蓝图生成器。",
      steps: ["提示词", "模板", "预览"]
    },
    language: {
      label: "语言",
      english: "EN",
      chinese: "中文"
    },
    prompt: {
      title: "生成蓝图",
      description: "先输入提示词，后续可以接入真正的 voxel 生成逻辑。",
      label: "建筑描述",
      placeholder: "描述一个建筑，例如：带玻璃窗的中世纪塔楼",
      button: "生成蓝图",
      generatedPrefix: "已根据提示词生成蓝图：",
      emptyPrefix: "尚未生成提示词蓝图。当前模板：",
      generatedModelPrefix: "已生成本地模型：",
      generatorHint: "v2.0 生成器是确定性的本地规则生成，不调用外部 AI。"
    },
    presets: {
      title: "内置模板",
      description: "选择一个起始建筑模板，用于当前 3D 预览。",
      items: {
        "medieval-tower": {
          name: "中世纪塔楼",
          description: "带玻璃窗、火把和顶部城垛的石砖瞭望塔。"
        },
        "small-cottage": {
          name: "小木屋",
          description: "带原木角柱、木板墙、窗户和石质地基的温暖小屋。"
        },
        "dungeon-entrance": {
          name: "地牢入口",
          description: "带拱门入口、石质加固墙和火把照明的地下入口。"
        },
        "stone-bridge": {
          name: "石桥",
          description: "跨越水道的低矮石桥，包含桥面、护栏和桥拱。"
        },
        "pixel-statue": {
          name: "像素雕像",
          description: "带石质底座、蓝色腿部、红色身体和金色皇冠的像素雕像。"
        }
      }
    },
    preview: {
      eyebrow: "浏览器 3D 预览",
      promptLabel: "预览提示词：",
      controls: "支持旋转、拖动和缩放",
      fallbackPrompt: "选择一个模板，或输入建筑描述来准备第一份蓝图。",
      modelSize: "模型尺寸",
      modelBlocks: "方块数量",
      promptState: "提示词状态",
      generated: "已生成",
      draft: "草稿",
      blockTypes: "方块类型",
      empty: "当前没有可预览的 voxel 方块。"
    },
    export: {
      title: "导出",
      description: "下载 voxel 数据，或导出 Minecraft function 命令。",
      json: "导出 JSON",
      blueprintJson: "导出 Blueprint JSON v1",
      blueprintV2Json: "导出 Blueprint JSON v2",
      blueprintPack: "导出蓝图包",
      importBlueprintPack: "导入蓝图包",
      spongeSchematic: "导出 .schem",
      importSpongeSchematic: "导入 .schem",
      importBlueprintJson: "导入 Blueprint JSON",
      mcfunction: "导出 .mcfunction",
      datapack: "导出 Data Pack ZIP",
      blueprintFilesGroup: "蓝图文件",
      minecraftInstallGroup: "Minecraft 安装",
      interopImportGroup: "互通导入",
      validationLabel: "校验",
      errorsLabel: "错误",
      warningsLabel: "警告",
      hint: "导出为 Minecraft Java Edition datapack 工作流可用的 function 命令。",
      blueprintHint: "用于 BlockForge Mod Connector 的稳定蓝图协议文件。",
      blueprintV2Hint:
        "支持 Minecraft BlockState，供新版 BlockForge Connector 使用。",
      blueprintPackHint:
        "把多个 Blueprint JSON v2 打包成可分享的 .blockforgepack.zip。",
      importSuccess: "已导入蓝图包",
      importError: "蓝图包导入失败",
      schematicImportSuccess: "已导入 schematic",
      schematicImportError: "schematic 导入失败",
      schematicHint: "Sponge .schem v3 互通，用于 WorldEdit / FAWE 工作流。",
      datapackHint:
        "下载可放入 Minecraft Java 1.21.1 datapacks 文件夹的数据包。"
    },
    footer: {
      status: "项目状态：v0.1 MVP 已具备发布前整理条件。",
      roadmap: "Roadmap：datapack ZIP -> .schem 导出 -> 提示词生成。"
    }
  }
};

export function getPresetCopy(locale: Locale, presetId: PresetId) {
  return appCopy[locale].presets.items[presetId] ?? fallbackPresetCopy(presetId);
}

function fallbackPresetCopy(presetId: PresetId): PresetCopy {
  const name = presetId
    .replace(/[-_]/g, " ")
    .replace(/\b\w/g, (letter) => letter.toUpperCase());
  return {
    name,
    description: `${name} structure preset.`
  };
}
