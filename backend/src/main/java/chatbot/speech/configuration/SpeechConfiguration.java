package chatbot.speech.configuration;

import com.microsoft.cognitiveservices.speech.SpeechConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpeechConfiguration {
    @Value("${azure.speech.key}")
    private String speechKey;

    @Value("${azure.speech.region}")
    private String speechRegion;
    private static final String SPEECH_POLISH_LANGUAGE_CONFIG = "pl-PL";

    @Bean
    public SpeechConfig createSpeechConfig() {
        com.microsoft.cognitiveservices.speech.SpeechConfig speechConfig =
                com.microsoft.cognitiveservices.speech.SpeechConfig.fromSubscription(speechKey, speechRegion);
        speechConfig.setSpeechRecognitionLanguage(SPEECH_POLISH_LANGUAGE_CONFIG);
        speechConfig.setSpeechSynthesisLanguage(SPEECH_POLISH_LANGUAGE_CONFIG);

        return speechConfig;
    }
}
