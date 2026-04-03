package com.ns.wsdl.service;

import com.ns.wsdl.dto.WsdlRequest;
import com.ns.wsdl.dto.WsdlResponse;
import com.ns.wsdl.util.AxisApiGenerator;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.stream.Stream;

@Service
public class WsdlServiceImpl implements WsdlService {

    private static final String DEFAULT_DIR = "C:\\wsdl2";

    @Override
    public WsdlResponse processWsdl(WsdlRequest request) {
        try {
            if (request.getWsdlUrl() == null || request.getWsdlUrl().isEmpty()) {
                return new WsdlResponse(false, "URL WSDL requerida");
            }

            String destination = request.getDestination();
            if (destination == null || destination.isEmpty()) {
                destination = DEFAULT_DIR;
            }

            Path destPath = Paths.get(destination);
            Files.createDirectories(destPath);

            Path srcDir = destPath.resolve("src");
            Files.createDirectories(srcDir);

            Path wsdlFile = destPath.resolve("Service.wsdl");

            downloadWsdl(request.getWsdlUrl(), wsdlFile);

            AxisApiGenerator.execute(wsdlFile.toString(), srcDir.toString());

            flattenClasses(srcDir);

            cleanUnwantedFiles(srcDir);

            boolean generated = containsJavaFiles(srcDir);
            if (generated) {
                return new WsdlResponse(true, "Classes generated correctly in: "+ srcDir.toString());
            }

            return new WsdlResponse(false, "Axis2 did not generate classes. Check WSDL or logs.");

        } catch (Exception e) {
            String message = "Axis2 did not generate classes: " + e.getMessage();
            return new WsdlResponse(false, message);
        }
    }

    private void downloadWsdl(String wsdlUrl, Path target) throws Exception {
        try (InputStream in = new URL(wsdlUrl).openStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private boolean containsJavaFiles(Path dir) throws Exception {
        try (Stream<Path> stream = Files.walk(dir)) {
            return stream.filter(Files::isRegularFile)
                    .anyMatch(p -> p.toString().endsWith(".java"));
        }
    }

    private void flattenClasses(Path srcDir) throws Exception {
        if (!Files.exists(srcDir)) return;

        try (Stream<Path> stream = Files.walk(srcDir)) {
            stream.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .forEach(file -> {
                        try {
                            Path target = srcDir.resolve(file.getFileName());
                            if (!file.equals(target)) {
                                Files.move(file, target, StandardCopyOption.REPLACE_EXISTING);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
        deleteEmptyDirectories(srcDir);
    }

    private void deleteEmptyDirectories(Path root) throws Exception {
        Files.walk(root)
                .sorted(Comparator.reverseOrder())
                .filter(Files::isDirectory)
                .forEach(dir -> {
                    try (Stream<Path> files = Files.list(dir)) {
                        if (!dir.equals(root) && !files.findAny().isPresent()) {
                            Files.delete(dir);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private void cleanUnwantedFiles(Path srcDir) throws Exception {
        if (!Files.exists(srcDir)) return;

        try (Stream<Path> stream = Files.walk(srcDir)) {
            stream.filter(Files::isRegularFile)
                    .filter(file -> {
                        String name = file.getFileName().toString();
                        return name.equalsIgnoreCase("build.xml")
                                || name.equalsIgnoreCase("build.properties")
                                || name.equalsIgnoreCase("pom.xml");
                    })
                    .forEach(file -> {
                        try {
                            Files.deleteIfExists(file);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

}