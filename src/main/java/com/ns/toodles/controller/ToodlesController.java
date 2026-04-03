package com.ns.toodles.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ToodlesController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "Toodles - Inicio");
        model.addAttribute("message", "¡Bienvenido a Toodles!");
        return "index"; // Busca index.html en templates/
    }
}