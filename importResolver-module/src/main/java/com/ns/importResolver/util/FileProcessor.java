package com.ns.importResolver.util;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class FileProcessor {

    public String processFile(String content,
                              Set<String> oldPackages,
                              String newPackage,
                              Map<String, Set<String>> classPackages) {

        String result = content;

        for (String oldPackage : oldPackages) {
            String searchPattern = oldPackage + ".";

            int index = 0;
            while ((index = result.indexOf(searchPattern, index)) != -1) {

                int start = index + searchPattern.length();
                int end = start;

                while (end < result.length() &&
                        (Character.isLetterOrDigit(result.charAt(end)) ||
                                result.charAt(end) == '_' ||
                                result.charAt(end) == '$')) {
                    end++;
                }

                if (end > start) {
                    String className = result.substring(start, end);
                    if (classPackages.containsKey(className)) {
                        Set<String> actualPackages = classPackages.get(className);
                        if (!actualPackages.contains(oldPackage)) {
                            String oldText = oldPackage + "." + className;
                            String newText = newPackage + "." + className;

                            result = result.replace(oldText, newText);
                            index = index + newText.length();
                        } else {
                            index = end;
                        }
                    } else {
                        index = end;
                    }
                } else {
                    index = end + 1;
                }
            }
        }
        return result;
    }
}
