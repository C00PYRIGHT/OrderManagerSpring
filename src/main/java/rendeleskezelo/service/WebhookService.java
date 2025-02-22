package rendeleskezelo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.MediaType;
@Slf4j
@Service
public class WebhookService {

    // A RestTemplate automatikusan beállítható, ha használod a @Bean annotációt egy konfigurációs osztályban
    private static final String WEBHOOK_URL = "https://192.168.0.66:5678/webhook/3dproject"; // A fix URL

    @Autowired
    private RestTemplate restTemplate;

    // A funkció, amely a POST kérést küldi a megadott adat segítségével
    public void sendPostRequest(String data) {
        // HTTP fejlécek beállítása
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN); // Mivel szöveges adatot küldünk, nem JSON
        log.info("Sending POST request to " + WEBHOOK_URL);
        // HTTP entitás létrehozása (adat + fejlécek)
        HttpEntity<String> entity = new HttpEntity<>(data, headers);


            ResponseEntity<String> response = restTemplate.exchange(WEBHOOK_URL, HttpMethod.POST, entity, String.class);



    }
}
