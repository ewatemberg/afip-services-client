package com.example.app.controller;

import com.example.app.service.AfipService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AfipController {

    private final AfipService afipService;

    public AfipController(AfipService afipService) {
        this.afipService = afipService;
    }

    @GetMapping("/constatar-comprobante")
    public Object constatarComprobante(@RequestParam String cuit, @RequestParam String comprobante) {
        // Construir la petición
        Object request = new AfipRequestBuilder().buildRequest();
        // Llamar al servicio de la AFIP
        return afipService.constatarComprobante(request);
    }
}