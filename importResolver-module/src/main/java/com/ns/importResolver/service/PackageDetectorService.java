package com.ns.importResolver.service;

import com.ns.importResolver.util.SourceParser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class PackageDetectorService {

    public Set<String> detectPackagesToRefactor(List<MultipartFile> files) throws IOException {
        Map<String, Set<String>> classPackageMap = buildClassPackageMap(files);
        Set<String> validImports = extractValidImports(files);
        Set<String> packagesToRefactor = new HashSet<>();

        for (MultipartFile file : files) {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            var matcher = SourceParser.matchPackageReference(content);

            while (matcher.find()) {
                String reference = matcher.group(1);

                if (validImports.contains(reference) || reference.startsWith("java.") || reference.startsWith("javax.")) {
                    continue;
                }

                String[] parts = reference.split("\\.");
                String className = parts[parts.length - 1];
                String referencedPackage = String.join(".", Arrays.copyOf(parts, parts.length - 1));

                if (classPackageMap.containsKey(className)) {
                    Set<String> actualPackages = classPackageMap.get(className);

                    if (!actualPackages.contains(referencedPackage)) {
                        if (actualPackages.contains("") && !referencedPackage.isEmpty()) {
                            packagesToRefactor.add(referencedPackage);
                        } else if (!actualPackages.contains(referencedPackage) && !actualPackages.contains("")) {
                            packagesToRefactor.add(referencedPackage);
                        }
                    }
                }
            }
        }

        return packagesToRefactor;
    }

    private Map<String, Set<String>> buildClassPackageMap(List<MultipartFile> files) throws IOException {
        Map<String, Set<String>> classPackageMap = new HashMap<>();

        for (MultipartFile file : files) {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            String declaredPackage = SourceParser.extractDeclaredPackage(content);
            String declaredClassName = SourceParser.extractDeclaredClassName(content);

            if (declaredClassName != null) {
                classPackageMap.computeIfAbsent(declaredClassName, k -> new HashSet<>())
                        .add(declaredPackage != null ? declaredPackage : "");
            }
        }

        return classPackageMap;
    }

    private Set<String> extractValidImports(List<MultipartFile> files) throws IOException {
        Set<String> validImports = new HashSet<>();

        for (MultipartFile file : files) {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            validImports.addAll(SourceParser.extractImports(content));
        }

        return validImports;
    }
}
