export type AiPromptPresetCategory =
  | "starter"
  | "medieval"
  | "survival"
  | "dungeon"
  | "bridge"
  | "statue"
  | "utility"
  | "custom";

export type AiPromptPreset = {
  id: string;
  name: string;
  description: string;
  category: AiPromptPresetCategory;
  prompt: string;
  sizeHint?: {
    width?: number;
    height?: number;
    depth?: number;
  };
  styleHint?: string;
  maxBlocks?: number;
  tags: string[];
};
