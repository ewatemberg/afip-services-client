package com.example.app.client;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.security.KeyStore;

public class AfipClient {

    private final WebServiceTemplate webServiceTemplate;

    public AfipClient(String url, String certPath, String keyPath, String keyPassword) throws Exception {
        this.webServiceTemplate = new WebServiceTemplate();
        this.webServiceTemplate.setDefaultUri(url);
        this.webServiceTemplate.setMessageSender(createMessageSender(certPath, keyPath, keyPassword));
    }

    private HttpComponentsMessageSender createMessageSender(String certPath, String keyPath, String keyPassword) throws Exception {
        // Cargar el certificado y la clave privada
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null);

        // SSL context
        SSLContext sslContext = SSLContextBuilder.create()
                .loadKeyMaterial(new File(certPath), keyPassword.toCharArray(), keyPassword.toCharArray())
                .build();

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(socketFactory)
                .build();

        HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender(httpClient);
        return messageSender;
    }

    public Object callAfipService(Object requestPayload) {
        return webServiceTemplate.marshalSendAndReceive(requestPayload);
    }
}
