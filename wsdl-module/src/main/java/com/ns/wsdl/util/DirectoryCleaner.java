package com.ns.wsdl.util;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Stream;

@Component
public class DirectoryCleaner {

    private static final Set<String> UNWANTED_FILES = Set.of(
            "build.xml", "build.properties", "pom.xml"
    );

    public void flattenAndClean(Path srcDir) throws IOException {
        if (!Files.exists(srcDir)) return;
        flattenClasses(srcDir);
        removeUnwantedFiles(srcDir);
        removeEmptyDirectories(srcDir);
    }

    private void flattenClasses(Path srcDir) throws IOException {
        try (Stream<Path> stream = Files.walk(srcDir)) {
            stream.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .forEach(file -> {
                        try {
                            Path target = srcDir.resolve(file.getFileName());
                            if (!file.equals(target)) {
                                Files.move(file, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    private void removeUnwantedFiles(Path srcDir) throws IOException {
        try (Stream<Path> stream = Files.walk(srcDir)) {
            stream.filter(Files::isRegularFile)
                    .filter(file -> UNWANTED_FILES.contains(file.getFileName().toString().toLowerCase()))
                    .forEach(file -> {
                        try {
                            Files.deleteIfExists(file);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    private void removeEmptyDirectories(Path root) throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                try (Stream<Path> entries = Files.list(dir)) {
                    if (!dir.equals(root) && entries.findAny().isEmpty()) {
                        Files.delete(dir);
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void deleteDirectory(Path dir) {
        if (!Files.exists(dir)) return;
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException ignored) {
                        }
                    });
        } catch (IOException ignored) {
        }
    }
}
