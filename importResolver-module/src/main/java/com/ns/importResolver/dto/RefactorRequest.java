package com.ns.importResolver.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class RefactorRequest {
    private List<MultipartFile> files;
    private String newPackage;

    public List<MultipartFile> getFiles() { return files; }
    public void setFiles(List<MultipartFile> files) { this.files = files; }
    public String getNewPackage() { return newPackage; }
    public void setNewPackage(String newPackage) { this.newPackage = newPackage; }
}