package chatbot;

import chatbot.llm.service.ChatbotService;
import chatbot.speech.service.SpeechToTextService;
import chatbot.speech.service.TextToSpeechService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class WustRagChatbotApplication {

    private final ChatbotService chatbotService;
    private final SpeechToTextService speechToTextService;
    private final TextToSpeechService textToSpeechService;

    public WustRagChatbotApplication(ChatbotService chatbotService, SpeechToTextService speechToTextService, TextToSpeechService textToSpeechService) {
        this.chatbotService = chatbotService;
        this.speechToTextService = speechToTextService;
        this.textToSpeechService = textToSpeechService;
    }

    private static final String WELCOME_MESSAGE = "Cześć! Jestem wirtualnym asystentem dziekanatu " +
            "wydziału informatyki i telekomunikacji na Politechnice Wrocławskiej! Jak mogę Ci pomóc?";
    private static final String EXCEPTION_MESSAGE = "Bardzo mi przykro, ale wystąpił błąd. Proszę spróbuj ponownie.";


    public static void main(String[] args) {
        SpringApplication.run(WustRagChatbotApplication.class, args);
    }

    @Bean
    CommandLineRunner runChatbot() {
        return args -> {
            log.info("Starting W4N Informatics and Telecommunication Faculty Chatbot...");
            textToSpeechService.textToSpeech(WELCOME_MESSAGE);

            while (true) {
                log.info("Waiting for your input...");

                try {
                    final String recognizedSpokenText = speechToTextService.listenToUser();

                    final String llmResponse = chatbotService.getResponse(recognizedSpokenText);

                    textToSpeechService.textToSpeech(llmResponse);
                } catch (Exception e) {
                    log.error("Error occurred during speech processing", e);
                    textToSpeechService.textToSpeech(EXCEPTION_MESSAGE);
                }
            }
        };
    }
}
