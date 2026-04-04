package com.ns.common.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CompressionConstants {

    public static final long MAX_ZIP_SIZE = 100 * 1024 * 1024; // 100 MB
    public static final int MAX_FILES_IN_ZIP = 1000;
    public static final int MAX_FILENAME_LENGTH = 255;

    public static final int COMPRESSION_LEVEL_NONE = 0;
    public static final int COMPRESSION_LEVEL_FAST = 1;
    public static final int COMPRESSION_LEVEL_DEFAULT = 6;
    public static final int COMPRESSION_LEVEL_MAX = 9;

    public static final String DEFAULT_ZIP_NAME = "archive.zip";
    public static final String DEFAULT_ENTRY_NAME = "content.txt";

    public static final String CLASS_EXTENSION = ".class";
}