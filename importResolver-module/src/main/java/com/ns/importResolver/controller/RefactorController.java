package com.ns.importResolver.controller;

import com.ns.common.service.DownloadService;
import com.ns.importResolver.dto.RefactorRequest;
import com.ns.importResolver.service.RefactorService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/refactor")
public class RefactorController {

    private final RefactorService refactorService;
    private final DownloadService downloadService;

    public RefactorController(RefactorService refactorService, DownloadService downloadService) {
        this.refactorService = refactorService;
        this.downloadService = downloadService;
    }

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

    @GetMapping("/download/{token}")
    public ResponseEntity<byte[]> downloadRefactored(@PathVariable String token) {
        byte[] data = downloadService.retrieve(token);
        if (data == null) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "refactored_files.zip");
        return ResponseEntity.ok().headers(headers).body(data);
    }
}
