import { describe, expect, it } from "vitest";
import { appCopy, getPresetCopy, type Locale } from "@/lib/i18n";
import { getAllPresets } from "@/lib/voxel";

const locales: Locale[] = ["en", "zh"];

describe("i18n copy", () => {
  it("defines complete preset copy for every locale", () => {
    const presets = getAllPresets();

    for (const locale of locales) {
      for (const preset of presets) {
        const copy = getPresetCopy(locale, preset.id);

        expect(copy.name.length).toBeGreaterThan(0);
        expect(copy.description.length).toBeGreaterThan(0);
      }
    }
  });

  it("defines language toggle labels", () => {
    for (const locale of locales) {
      expect(appCopy[locale].language.english).toBe("EN");
      expect(appCopy[locale].language.chinese).toBe("中文");
    }
  });
});
