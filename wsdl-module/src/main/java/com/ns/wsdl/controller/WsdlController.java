package com.ns.wsdl.controller;

import com.ns.common.service.DownloadService;
import com.ns.wsdl.dto.WsdlRequest;
import com.ns.wsdl.dto.WsdlResponse;
import com.ns.wsdl.service.WsdlProcessResult;
import com.ns.wsdl.service.WsdlService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wsdl")
public class WsdlController {

    private final WsdlService wsdlService;
    private final DownloadService downloadService;

    public WsdlController(WsdlService wsdlService, DownloadService downloadService) {
        this.wsdlService = wsdlService;
        this.downloadService = downloadService;
    }

    @GetMapping("/hi")
    public String hi() {
        return "HI - WSDL Module is working!";
    }

    @PostMapping("/generate")
    public WsdlResponse generate(@Valid @RequestBody WsdlRequest request) {
        WsdlProcessResult result = wsdlService.processWsdl(request);
        WsdlResponse response = new WsdlResponse(result.isSuccess(), result.getMessage());
        if (result.isSuccess() && result.getZipContent() != null) {
            String token = downloadService.store(result.getZipContent());
            response.setDownloadToken(token);
            response.setFileName("wsdl-generated-classes.zip");
            response.setFileCount(result.getFileCount());
        }
        return response;
    }

    @GetMapping("/download/{token}")
    public ResponseEntity<byte[]> download(@PathVariable String token) {
        byte[] data = downloadService.retrieve(token);
        if (data == null) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "wsdl-generated-classes.zip");
        return ResponseEntity.ok().headers(headers).body(data);
    }
}
