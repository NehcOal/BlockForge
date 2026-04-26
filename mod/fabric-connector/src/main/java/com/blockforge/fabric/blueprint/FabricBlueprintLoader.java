package com.blockforge.fabric.blueprint;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.blueprint.BlueprintParser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class FabricBlueprintLoader {
    private static final Gson GSON = new Gson();
    private static final BlueprintParser PARSER = new BlueprintParser();

    public static Path defaultBlueprintDirectory() {
        return FabricLoader.getInstance().getConfigDir().resolve("blockforge").resolve("blueprints");
    }

    public LoadResult load(Path directory) {
        List<Blueprint> blueprints = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        try {
            Files.createDirectories(directory);
        } catch (IOException error) {
            warnings.add("Could not create blueprint directory: " + error.getMessage());
            return new LoadResult(blueprints, warnings);
        }

        List<Path> files;
        try (Stream<Path> stream = Files.list(directory)) {
            files = stream
                    .filter(Files::isRegularFile)
                    .filter(FabricBlueprintLoader::isBlueprintFile)
                    .sorted(Comparator.comparing(Path::toString))
                    .toList();
        } catch (IOException error) {
            warnings.add("Could not scan blueprint directory: " + error.getMessage());
            return new LoadResult(blueprints, warnings);
        }

        if (files.isEmpty()) {
            warnings.add("No blueprint JSON files found in " + directory);
            return new LoadResult(blueprints, warnings);
        }

        for (Path file : files) {
            try (Reader reader = Files.newBufferedReader(file)) {
                JsonObject root = GSON.fromJson(reader, JsonObject.class);

                if (root == null) {
                    warnings.add(file.getFileName() + ": empty JSON file");
                    continue;
                }

                blueprints.add(PARSER.parse(root));
            } catch (IllegalArgumentException error) {
                warnings.add(file.getFileName() + ": invalid blueprint: " + error.getMessage());
            } catch (JsonSyntaxException error) {
                warnings.add(file.getFileName() + ": JSON parse failed: " + error.getMessage());
            } catch (IOException error) {
                warnings.add(file.getFileName() + ": read failed: " + error.getMessage());
            }
        }

        return new LoadResult(blueprints, warnings);
    }

    private static boolean isBlueprintFile(Path file) {
        String fileName = file.getFileName().toString().toLowerCase();
        return fileName.endsWith(".blueprint.json") || fileName.endsWith(".json");
    }

    public record LoadResult(List<Blueprint> blueprints, List<String> warnings) {
    }
}
