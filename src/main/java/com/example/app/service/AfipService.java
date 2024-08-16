package com.example.app.service;

import com.example.app.client.AfipClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AfipService {

    private final AfipClient afipClient;

    public AfipService(@Value("${afip.url}") String afipUrl,
                       @Value("${afip.cert.path}") String certPath,
                       @Value("${afip.key.path}") String keyPath,
                       @Value("${afip.key.password}") String keyPassword) throws Exception {
        this.afipClient = new AfipClient(afipUrl, certPath, keyPath, keyPassword);
    }

    public Object constatarComprobante(Object request) {
        return afipClient.callAfipService(request);
    }
}
