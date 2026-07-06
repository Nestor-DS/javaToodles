package com.ns.importResolver.util;

import java.util.regex.Pattern;

public final class SourceParser {

    private static final Pattern PACKAGE_PATTERN =
            Pattern.compile("^package\\s+([a-zA-Z0-9_.]+)\\s*;", Pattern.MULTILINE);
    private static final Pattern CLASS_NAME_PATTERN =
            Pattern.compile("(?:public\\s+)?(?:class|interface|enum|record)\\s+(\\w+)");
    private static final Pattern IMPORT_PATTERN =
            Pattern.compile("import\\s+([a-zA-Z0-9_.]+)\\s*;", Pattern.MULTILINE);
    private static final Pattern PACKAGE_REFERENCE_PATTERN =
            Pattern.compile("([a-zA-Z][a-zA-Z0-9_]*(?:\\.[a-zA-Z][a-zA-Z0-9_]*)+)");

    private SourceParser() {}

    public static String extractDeclaredPackage(String content) {
        var matcher = PACKAGE_PATTERN.matcher(content);
        return matcher.find() ? matcher.group(1) : null;
    }

    public static String extractDeclaredClassName(String content) {
        var matcher = CLASS_NAME_PATTERN.matcher(content);
        return matcher.find() ? matcher.group(1) : null;
    }

    public static java.util.Set<String> extractImports(String content) {
        var imports = new java.util.HashSet<String>();
        var matcher = IMPORT_PATTERN.matcher(content);
        while (matcher.find()) {
            imports.add(matcher.group(1));
        }
        return imports;
    }

    public static java.util.regex.Matcher matchPackageReference(String content) {
        return PACKAGE_REFERENCE_PATTERN.matcher(content);
    }
}
