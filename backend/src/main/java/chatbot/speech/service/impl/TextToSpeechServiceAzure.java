package chatbot.speech.service.impl;

import chatbot.speech.service.TextToSpeechService;
import com.microsoft.cognitiveservices.speech.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
@Service
@Slf4j
@RequiredArgsConstructor
public class TextToSpeechServiceAzure implements TextToSpeechService {
    private final SpeechConfig speechConfig;

    @Override
    public void textToSpeech(String text) throws InterruptedException, ExecutionException {
        SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig);

        SpeechSynthesisResult result = speechSynthesizer.SpeakTextAsync(text).get();

        if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
            log.info("Bot: {}", text);
        } else if (result.getReason() == ResultReason.Canceled) {
            SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(result);
            log.error("CANCELED: Reason={}", cancellation.getReason());

            if (cancellation.getReason() == CancellationReason.Error) {
                log.error("CANCELED: ErrorCode={}", cancellation.getErrorCode());
                log.error("CANCELED: ErrorDetails={}", cancellation.getErrorDetails());
            }
        }
    }
}
