package com.ns.importResolver.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileInfo {
    private MultipartFile file;
    private String originalFileName;
}