import { describe, expect, it } from "vitest";
import { ungzip } from "@/lib/nbt/gzip";
import { readNamedNbt } from "@/lib/nbt/reader";
import { decodeVarInts } from "@/lib/nbt/varint";
import { exportSpongeSchematicBlob } from "@/lib/voxel/schematic/spongeSchematicExport";
import { voxelModelToBlueprintV2 } from "@/lib/voxel/blueprintProtocolV2";
import { getPresetById } from "@/lib/voxel";

describe("Sponge schematic export", () => {
  it("exports gzip NBT Sponge schematic v3", async () => {
    const blueprint = voxelModelToBlueprintV2(getPresetById("small-cottage"));
    const blob = await exportSpongeSchematicBlob(blueprint, { name: "Small Cottage" });
    const nbt = readNamedNbt(await ungzip(new Uint8Array(await blob.arrayBuffer())));

    expect(nbt.name).toBe("Schematic");
    expect(nbt.value.type).toBe("compound");
    const root = nbt.value.value;
    expect(root.Version).toEqual({ type: "int", value: 3 });
    expect(root.Width).toEqual({ type: "short", value: blueprint.size.width });
    expect(root.Height).toEqual({ type: "short", value: blueprint.size.height });
    expect(root.Length).toEqual({ type: "short", value: blueprint.size.depth });
    expect(root.Blocks?.type).toBe("compound");
    if (root.Blocks?.type === "compound") {
      expect(root.Blocks.value.Palette?.type).toBe("compound");
      expect(root.Blocks.value.Data?.type).toBe("byteArray");
      if (root.Blocks.value.Data?.type === "byteArray") {
        expect(decodeVarInts(root.Blocks.value.Data.value)).toHaveLength(
          blueprint.size.width * blueprint.size.height * blueprint.size.depth
        );
      }
    }
  });
});
