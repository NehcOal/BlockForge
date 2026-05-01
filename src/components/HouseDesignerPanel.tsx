"use client";

import { useMemo, useState } from "react";
import {
  analyzeHouseQuality,
  estimateHouseMaterials,
  generateHousePlan,
  housePlanToVoxelModel,
  houseStyles,
  type HouseRoofType,
  type HouseStyle
} from "@/lib/house";
import type { VoxelModel } from "@/types/blueprint";

type HouseDesignerPanelProps = {
  onGenerated: (model: VoxelModel, sourcePrompt: string) => void;
};

const roofTypes: HouseRoofType[] = ["gable", "flat", "hip", "pyramid", "tower", "shed", "none"];

export function HouseDesignerPanel({ onGenerated }: HouseDesignerPanelProps) {
  const [style, setStyle] = useState<HouseStyle>("starter_cottage");
  const [width, setWidth] = useState(9);
  const [depth, setDepth] = useState(7);
  const [floors, setFloors] = useState(1);
  const [roofType, setRoofType] = useState<HouseRoofType>("gable");
  const [buildRoof, setBuildRoof] = useState(true);
  const [addWindows, setAddWindows] = useState(true);
  const [addPorch, setAddPorch] = useState(false);
  const [addChimney, setAddChimney] = useState(false);
  const [hollowInterior, setHollowInterior] = useState(true);

  const plan = useMemo(
    () =>
      generateHousePlan({
        style,
        width,
        depth,
        floors,
        roofType,
        options: {
          buildRoof,
          addWindows,
          addPorch,
          addChimney,
          hollowInterior
        }
      }),
    [addChimney, addPorch, addWindows, buildRoof, depth, floors, hollowInterior, roofType, style, width]
  );
  const quality = useMemo(() => analyzeHouseQuality(plan), [plan]);
  const materials = useMemo(() => estimateHouseMaterials(plan).slice(0, 5), [plan]);

  function handleGenerateHouse() {
    onGenerated(housePlanToVoxelModel(plan), `House Designer: ${plan.name}`);
  }

  return (
    <section className="forge-panel p-5">
      <div className="mb-4">
        <h2 className="flex items-center gap-2 text-lg font-extrabold text-forge">
          <span className="h-4 w-4 border border-forge/70 bg-black/35 shadow-[inset_0_0_0_3px_rgba(217,164,65,0.16)]" />
          House Designer
        </h2>
        <p className="mt-2 text-sm leading-6 text-stone-400">
          Rule-based house planner for preview, material estimates, and Blueprint export.
        </p>
      </div>

      <div className="grid gap-3">
        <label className="grid gap-1 text-xs font-semibold uppercase tracking-[0.16em] text-stone-500">
          Preset
          <select
            className="rounded-md border border-stone-800 bg-black/35 px-3 py-2 text-sm normal-case tracking-normal text-stone-100"
            value={style}
            onChange={(event) => setStyle(event.target.value as HouseStyle)}
          >
            {houseStyles.map((houseStyle) => (
              <option key={houseStyle} value={houseStyle}>
                {formatLabel(houseStyle)}
              </option>
            ))}
          </select>
        </label>

        <div className="grid grid-cols-3 gap-2">
          <NumberField label="Width" max={32} min={5} value={width} onChange={setWidth} />
          <NumberField label="Depth" max={32} min={5} value={depth} onChange={setDepth} />
          <NumberField label="Floors" max={4} min={1} value={floors} onChange={setFloors} />
        </div>

        <label className="grid gap-1 text-xs font-semibold uppercase tracking-[0.16em] text-stone-500">
          Roof
          <select
            className="rounded-md border border-stone-800 bg-black/35 px-3 py-2 text-sm normal-case tracking-normal text-stone-100"
            value={roofType}
            onChange={(event) => setRoofType(event.target.value as HouseRoofType)}
          >
            {roofTypes.map((type) => (
              <option key={type} value={type}>
                {formatLabel(type)}
              </option>
            ))}
          </select>
        </label>

        <div className="grid grid-cols-2 gap-2 text-sm text-stone-300">
          <Toggle checked={buildRoof} label="Roof" onChange={setBuildRoof} />
          <Toggle checked={addWindows} label="Windows" onChange={setAddWindows} />
          <Toggle checked={addPorch} label="Porch" onChange={setAddPorch} />
          <Toggle checked={addChimney} label="Chimney" onChange={setAddChimney} />
          <Toggle checked={hollowInterior} label="Hollow" onChange={setHollowInterior} />
        </div>

        <div className="rounded-md border border-stone-800/80 bg-black/25 p-3">
          <div className="flex items-center justify-between">
            <span className="text-sm font-semibold text-stone-200">Quality</span>
            <span className="text-lg font-extrabold text-forge">{quality.total}</span>
          </div>
          <div className="mt-2 h-2 overflow-hidden rounded-full bg-stone-900">
            <div className="h-full bg-forge" style={{ width: `${quality.total}%` }} />
          </div>
          <p className="mt-2 text-xs leading-5 text-stone-500">
            {quality.warnings[0] ?? "No major house planning warnings."}
          </p>
        </div>

        <div className="rounded-md border border-stone-800/80 bg-black/25 p-3">
          <p className="text-sm font-semibold text-stone-200">Top Materials</p>
          <div className="mt-2 grid gap-1 text-xs text-stone-400">
            {materials.map((item) => (
              <div className="flex justify-between" key={item.block}>
                <span>{item.block}</span>
                <span>{item.count}</span>
              </div>
            ))}
          </div>
        </div>

        <button
          className="rounded-md border border-forge/70 bg-forge/15 px-4 py-2 text-sm font-bold text-forge transition hover:bg-forge/25"
          type="button"
          onClick={handleGenerateHouse}
        >
          Generate House
        </button>
      </div>
    </section>
  );
}

function NumberField({
  label,
  max,
  min,
  value,
  onChange
}: {
  label: string;
  max: number;
  min: number;
  value: number;
  onChange: (value: number) => void;
}) {
  return (
    <label className="grid gap-1 text-xs font-semibold uppercase tracking-[0.16em] text-stone-500">
      {label}
      <input
        className="rounded-md border border-stone-800 bg-black/35 px-3 py-2 text-sm normal-case tracking-normal text-stone-100"
        max={max}
        min={min}
        type="number"
        value={value}
        onChange={(event) => onChange(Number(event.target.value))}
      />
    </label>
  );
}

function Toggle({
  checked,
  label,
  onChange
}: {
  checked: boolean;
  label: string;
  onChange: (checked: boolean) => void;
}) {
  return (
    <label className="flex items-center gap-2 rounded-md border border-stone-800/80 bg-black/25 px-3 py-2">
      <input checked={checked} type="checkbox" onChange={(event) => onChange(event.target.checked)} />
      <span>{label}</span>
    </label>
  );
}

function formatLabel(value: string) {
  return value
    .split("_")
    .map((part) => part.slice(0, 1).toUpperCase() + part.slice(1))
    .join(" ");
}
