package com.ns.importResolver.controller;

import com.ns.common.service.DownloadService;
import com.ns.importResolver.dto.RefactorRequest;
import com.ns.importResolver.service.RefactorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/refactor")
public class RefactorHtmxController {

    private final RefactorService refactorService;
    private final DownloadService downloadService;

    public RefactorHtmxController(RefactorService refactorService, DownloadService downloadService) {
        this.refactorService = refactorService;
        this.downloadService = downloadService;
    }

    @PostMapping("/refactorClass-htmx")
    public String refactorHtmx(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("newPackage") String newPackage,
            Model model) throws IOException {

        RefactorRequest request = new RefactorRequest();
        request.setFiles(files);
        request.setNewPackage(newPackage);

        byte[] zipContent = refactorService.refactorAndZip(request);
        String token = downloadService.store(zipContent);
        model.addAttribute("token", token);
        model.addAttribute("fileName", "refactored_files.zip");
        model.addAttribute("fileCount", files.size());
        return "fragments/refactor-fragments :: result";
    }
}
