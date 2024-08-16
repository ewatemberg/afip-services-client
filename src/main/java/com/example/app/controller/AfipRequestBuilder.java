package com.example.app.controller;

import com.example.app.client.request.FECompConsultarRequest;

public class AfipRequestBuilder {

    public Object buildRequest() {
        // Construir la solicitud SOAP (payload) aquí
        // Por ejemplo, para el servicio FECompConsultar
        return new FECompConsultarRequest();
    }
}
