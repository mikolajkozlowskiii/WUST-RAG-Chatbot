package chatbot.vision.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class VisionConfig {
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        DefaultUriBuilderFactory uriFactory = new DefaultUriBuilderFactory("http://localhost:8080/verify");
        restTemplate.setUriTemplateHandler(uriFactory);
        return restTemplate;
    }
}
