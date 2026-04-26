package com.blockforge.fabric.blueprint;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FabricExampleBlueprintInstaller {
    private static final String RESOURCE_ROOT = "blockforge_examples/";

    private static final List<ExampleBlueprint> EXAMPLES = List.of(
            new ExampleBlueprint("tiny_platform.blueprint.json", "tiny_platform", "Tiny Platform"),
            new ExampleBlueprint("small_test_house.blueprint.json", "small_test_house", "Small Test House"),
            new ExampleBlueprint("medieval_tower.blueprint.json", "medieval_tower", "Medieval Tower"),
            new ExampleBlueprint("state_test_house.blueprint.json", "state_test_house", "State Test House")
    );

    public List<ExampleBlueprint> getExamples() {
        return EXAMPLES;
    }

    public InstallResult install(Path directory) {
        int installed = 0;
        int skipped = 0;
        int missing = 0;

        try {
            Files.createDirectories(directory);
        } catch (IOException error) {
            return new InstallResult(0, 0, EXAMPLES.size(), "Could not create blueprint directory: " + error.getMessage());
        }

        ClassLoader classLoader = FabricExampleBlueprintInstaller.class.getClassLoader();

        for (ExampleBlueprint example : EXAMPLES) {
            Path target = directory.resolve(example.fileName());

            if (Files.exists(target)) {
                skipped++;
                continue;
            }

            try (InputStream input = classLoader.getResourceAsStream(RESOURCE_ROOT + example.fileName())) {
                if (input == null) {
                    missing++;
                    continue;
                }

                Files.copy(input, target);
                installed++;
            } catch (IOException error) {
                missing++;
            }
        }

        return new InstallResult(installed, skipped, missing, "");
    }

    public record ExampleBlueprint(String fileName, String id, String name) {
    }

    public record InstallResult(int installed, int skipped, int missing, String error) {
        public boolean hasError() {
            return error != null && !error.isBlank();
        }
    }
}
