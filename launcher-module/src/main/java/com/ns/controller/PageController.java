package com.ns.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("currentPage", "dashboard");
        return "dashboard";
    }

    @GetMapping("/wsdl")
    public String wsdlPage(Model model) {
        model.addAttribute("currentPage", "wsdl");
        return "wsdl/index";
    }

    @GetMapping("/refactor")
    public String refactorPage(Model model) {
        model.addAttribute("currentPage", "refactor");
        return "refactor/index";
    }
}
