package com.ns.importResolver.controller;

import com.ns.importResolver.dto.RefactorRequest;
import com.ns.importResolver.service.RefactorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/refactor")
public class RefactorController {

    @Autowired
    private RefactorService refactorService;

    @GetMapping("/")
    public String hi() {
        return "HI - Refactor imports Module is working!";
    }

    @PostMapping("/refactorClass")
    public ResponseEntity<byte[]> refactorImports(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("newPackage") String newPackage) throws IOException {

        RefactorRequest request = new RefactorRequest();
        request.setFiles(files);
        request.setNewPackage(newPackage);

        byte[] zipContent = refactorService.refactorAndZip(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "refactored_files.zip");

        return ResponseEntity.ok().headers(headers).body(zipContent);
    }
}