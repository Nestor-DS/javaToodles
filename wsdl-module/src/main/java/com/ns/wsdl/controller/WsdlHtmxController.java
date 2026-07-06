package com.ns.wsdl.controller;

import com.ns.common.service.DownloadService;
import com.ns.wsdl.dto.WsdlRequest;
import com.ns.wsdl.dto.WsdlResponse;
import com.ns.wsdl.service.WsdlProcessResult;
import com.ns.wsdl.service.WsdlService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/wsdl")
public class WsdlHtmxController {

    private final WsdlService wsdlService;
    private final DownloadService downloadService;

    public WsdlHtmxController(WsdlService wsdlService, DownloadService downloadService) {
        this.wsdlService = wsdlService;
        this.downloadService = downloadService;
    }

    @PostMapping("/generate-htmx")
    public String generateHtmx(
            @RequestParam("wsdlUrl") String wsdlUrl,
            Model model) {

        WsdlRequest request = new WsdlRequest(wsdlUrl, null);
        WsdlProcessResult result = wsdlService.processWsdl(request);

        WsdlResponse response = new WsdlResponse(result.isSuccess(), result.getMessage());
        if (result.isSuccess() && result.getZipContent() != null) {
            String token = downloadService.store(result.getZipContent());
            response.setDownloadToken(token);
            response.setFileName("wsdl-generated-classes.zip");
            response.setFileCount(result.getFileCount());
        }

        model.addAttribute("response", response);
        return "fragments/wsdl-fragments :: result";
    }
}
