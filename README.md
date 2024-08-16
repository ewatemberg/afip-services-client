# afip-services-client
---
Ejemplo de un cliente para consumir los servicios de la [AFIP](https://www.afip.gob.ar/ws/introduccionwsapi.asp)

### Pre-requisitos
* Maven >= 3
* Java >= 17

## Ejecución  💻

Ejecutar

    mvn spring-boot:run

## Doc 📖️

1. Configuración del cliente SOAP: necesitarás configurar un cliente SOAP para interactuar con los servicios de la AFIP.

```java
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
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
```

2. Uso del Cliente en un Servicio Spring Boot:

```java
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
```

3. Configuración de propiedades:

```java
afip.url=https://wswhomo.afip.gov.ar/wsfev1/service.asmx
afip.cert.path=src/main/resources/certs/mi_certificado.pem
afip.key.path=src/main/resources/certs/mi_llave_privada.key
afip.key.password=mi_contraseña
```

4. Creación de la Petición: El siguiente paso es construir el request según el servicio que estés consumiendo. La AFIP generalmente utiliza SOAP y para esto se generan clases con JAXB o puedes construir el payload manualmente.

```java
public class AfipRequestBuilder {

    public Object buildRequest() {
        // Construir la solicitud SOAP (payload) aquí
        // Por ejemplo, para el servicio FECompConsultar
        return new FECompConsultarRequest();
    }
}
```

5. Controlador Rest

```java
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

```




## Herramientas 🔧
* [Spring boot](https://spring.io/projects/spring-boot) - Framework de java

## Notas 📋
(1) _Se recomienda [IntelliJ Community](https://www.jetbrains.com/idea/download/) o [Eclipse IDE for Enterprise Java Developers](https://www.eclipse.org/downloads/packages/)_