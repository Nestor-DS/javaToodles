package com.ns.wsdl.controller;

import com.ns.wsdl.dto.WsdlRequest;
import com.ns.wsdl.dto.WsdlResponse;
import com.ns.wsdl.service.WsdlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wsdl")
public class WsdlController {

    @Autowired
    private WsdlService wsdlService;

    @GetMapping("/hi")
    public String hi() {
        return "HI - WSDL Module is working!";
    }

    @PostMapping("/generate")
    public WsdlResponse generate(@RequestBody WsdlRequest request) {
        return wsdlService.processWsdl(request);
    }
}