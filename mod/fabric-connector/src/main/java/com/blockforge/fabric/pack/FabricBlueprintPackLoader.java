package com.blockforge.fabric.pack;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.blueprint.BlueprintParser;
import com.blockforge.common.pack.BlueprintPackEntry;
import com.blockforge.common.pack.BlueprintPackManifest;
import com.blockforge.common.pack.BlueprintPackManifestParser;
import com.blockforge.common.pack.BlueprintPackPaths;
import com.blockforge.common.pack.BlueprintPackRegistryEntry;
import com.blockforge.common.pack.LoadedBlueprintPack;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FabricBlueprintPackLoader {
    private static final Gson GSON = new Gson();
    private static final BlueprintPackManifestParser MANIFEST_PARSER = new BlueprintPackManifestParser();
    private static final BlueprintParser BLUEPRINT_PARSER = new BlueprintParser();

    public static Path defaultPackDirectory() {
        return FabricLoader.getInstance().getConfigDir().resolve("blockforge").resolve("packs");
    }

    public LoadResult load(Path directory, Set<String> reservedIds) {
        Map<String, Blueprint> blueprints = new LinkedHashMap<>();
        List<LoadedBlueprintPack> packs = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        try {
            Files.createDirectories(directory);
        } catch (IOException error) {
            warnings.add("Could not create pack directory: " + error.getMessage());
            return new LoadResult(blueprints, packs, warnings);
        }

        List<Path> files;
        try (Stream<Path> stream = Files.list(directory)) {
            files = stream
                    .filter(Files::isRegularFile)
                    .filter(FabricBlueprintPackLoader::isPackFile)
                    .sorted(Comparator.comparing(Path::toString))
                    .toList();
        } catch (IOException error) {
            warnings.add("Could not scan pack directory: " + error.getMessage());
            return new LoadResult(blueprints, packs, warnings);
        }

        for (Path file : files) {
            loadPack(file, reservedIds, blueprints, packs, warnings);
        }

        return new LoadResult(blueprints, packs, warnings);
    }

    private void loadPack(
            Path file,
            Set<String> reservedIds,
            Map<String, Blueprint> blueprints,
            List<LoadedBlueprintPack> packs,
            List<String> warnings
    ) {
        List<String> packWarnings = new ArrayList<>();
        List<BlueprintPackRegistryEntry> loadedEntries = new ArrayList<>();

        try (ZipFile zip = new ZipFile(file.toFile())) {
            ZipEntry manifestEntry = zip.getEntry(BlueprintPackPaths.MANIFEST_PATH);
            if (manifestEntry == null) {
                warnings.add(file.getFileName() + ": missing blockforge-pack.json");
                return;
            }

            BlueprintPackManifest manifest;
            try (InputStreamReader reader = new InputStreamReader(zip.getInputStream(manifestEntry), StandardCharsets.UTF_8)) {
                manifest = MANIFEST_PARSER.parse(GSON.fromJson(reader, JsonObject.class));
            }

            for (BlueprintPackEntry entry : manifest.blueprints()) {
                String registryId = BlueprintPackPaths.registryId(manifest.packId(), entry.id());
                if (reservedIds.contains(registryId) || blueprints.containsKey(registryId)) {
                    packWarnings.add("skipped conflict: " + registryId);
                    continue;
                }

                ZipEntry blueprintEntry = zip.getEntry(entry.path());
                if (blueprintEntry == null) {
                    packWarnings.add("missing blueprint file: " + entry.path());
                    continue;
                }

                try (InputStreamReader reader = new InputStreamReader(zip.getInputStream(blueprintEntry), StandardCharsets.UTF_8)) {
                    Blueprint parsed = BLUEPRINT_PARSER.parse(GSON.fromJson(reader, JsonObject.class));
                    blueprints.put(registryId, withId(parsed, registryId, entry.name()));
                    loadedEntries.add(new BlueprintPackRegistryEntry(manifest, entry));
                } catch (RuntimeException error) {
                    packWarnings.add(entry.path() + ": invalid blueprint: " + error.getMessage());
                }
            }

            packs.add(new LoadedBlueprintPack(manifest, loadedEntries, packWarnings));
            packWarnings.forEach(warning -> warnings.add(manifest.packId() + ": " + warning));
        } catch (RuntimeException error) {
            warnings.add(file.getFileName() + ": invalid pack manifest: " + error.getMessage());
        } catch (IOException error) {
            warnings.add(file.getFileName() + ": pack read failed: " + error.getMessage());
        }
    }

    private static Blueprint withId(Blueprint blueprint, String id, String name) {
        return new Blueprint(
                blueprint.getSchemaVersion(),
                id,
                name == null || name.isBlank() ? blueprint.getName() : name,
                blueprint.getDescription(),
                blueprint.getMinecraftVersion(),
                blueprint.getGenerator(),
                blueprint.getSize(),
                blueprint.getPalette(),
                blueprint.getBlocks()
        );
    }

    private static boolean isPackFile(Path file) {
        String fileName = file.getFileName().toString().toLowerCase();
        return fileName.endsWith(".blockforgepack.zip") || fileName.endsWith(".zip");
    }

    public record LoadResult(
            Map<String, Blueprint> blueprints,
            List<LoadedBlueprintPack> packs,
            List<String> warnings
    ) {
        public LoadResult {
            blueprints = blueprints == null ? Map.of() : Map.copyOf(blueprints);
            packs = packs == null ? List.of() : List.copyOf(packs);
            warnings = warnings == null ? List.of() : List.copyOf(warnings);
        }
    }
}
