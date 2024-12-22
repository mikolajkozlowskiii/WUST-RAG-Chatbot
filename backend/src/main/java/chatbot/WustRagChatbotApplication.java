package chatbot;

import chatbot.llm.service.ChatbotService;
import chatbot.speech.service.SpeechToTextService;
import chatbot.speech.service.TextToSpeechService;
import chatbot.vision.CameraService;
import chatbot.vision.FaceIdentifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
@Slf4j
public class WustRagChatbotApplication {

    private  ChatbotService chatbotService;
    private  CameraService cameraService;
    private  SpeechToTextService speechToTextService;
    private  TextToSpeechService textToSpeechService;
    private FaceIdentifyService faceIdentifyService;


    public WustRagChatbotApplication(
            ChatbotService chatbotService,
            CameraService cameraService,
            SpeechToTextService speechToTextService,
            TextToSpeechService textToSpeechService,
            FaceIdentifyService faceIdentifyService) {
        this.cameraService = cameraService;
        this.chatbotService = chatbotService;
        this.speechToTextService = speechToTextService;
        this.textToSpeechService = textToSpeechService;
        this.faceIdentifyService = faceIdentifyService;
    }

    private static final String WELCOME_VOICE_MESSAGE = "Cześć! Jestem wirtualnym asystentem dziekanatu " +
            "wydziału informatyki i telekomunikacji na Politechnice Wrocławskiej! Jak mogę Ci pomóc?";
    private static final String EXCEPTION_VOICE_MESSAGE = "Bardzo mi przykro, ale wystąpił błąd. Proszę spróbuj ponownie.";
    private static final String TAKE_PHOTO_VOICE_MESSAGE = "Proszę spojrzeć w kamerę, aby zrobić zdjęcie przed rozpoczęciem rozmowy.";
    private static final String RETAKE_PHOTO_VOICE_MESSAGE = "Nie udało się rozpoznać studenta wydziału Informatyki i Telekomunikacji.";

    public static void main(String[] args) {
        SpringApplication.run(WustRagChatbotApplication.class, args);
    }

    @Bean
    CommandLineRunner runChatbot() {
        return args -> {
            log.info("start");
            boolean isPersonIdentified = false;
            try {
                while (!isPersonIdentified){
                    isPersonIdentified = identifyPerson();
                }
            } catch (Exception e) {
                log.error("Error capturing photo", e);
                textToSpeechService.textToSpeech("Nie udało się zrobić zdjęcia. Spróbuj ponownie.");
                return;
            }

            log.info("Starting W4N Informatics and Telecommunication Faculty Chatbot...");
            textToSpeechService.textToSpeech(WELCOME_VOICE_MESSAGE);

            startChatbotLoop();
        };
    }

    private boolean identifyPerson() throws Exception {
        boolean isPersonIdentified;
        textToSpeechService.textToSpeech(TAKE_PHOTO_VOICE_MESSAGE);
        BufferedImage image = cameraService.capturePhoto();
        log.info("Photo captured successfully.");
        Thread.sleep(10000);
        isPersonIdentified = faceIdentifyService.isFaceRecognized(image);
        log.info("Is person identified " + isPersonIdentified);
        Thread.sleep(10000);
        if(isPersonIdentified){
            textToSpeechService.textToSpeech(RETAKE_PHOTO_VOICE_MESSAGE);
        }
        return isPersonIdentified;
    }

    private void startChatbotLoop() throws InterruptedException, ExecutionException {
        while (true) {
            log.info("Waiting for your input...");

            try {
                final String recognizedSpokenText = speechToTextService.listenToUser();

                final String llmResponse = chatbotService.getResponse(recognizedSpokenText);

                textToSpeechService.textToSpeech(llmResponse);
            } catch (Exception e) {
                log.error("Error occurred during speech processing", e);
                textToSpeechService.textToSpeech(EXCEPTION_VOICE_MESSAGE);
            }
        }
    }
}
