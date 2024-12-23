package chatbot.speech.service.impl;

import chatbot.speech.service.SpeechToTextService;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.microsoft.cognitiveservices.speech.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpeechToTextServiceAzure implements SpeechToTextService {
    private final SpeechConfig speechConfig;

    @Override
    public String listenToUser() throws InterruptedException, ExecutionException {
        AudioConfig audioConfig = AudioConfig.fromDefaultMicrophoneInput();
        SpeechRecognizer speechRecognizer = new SpeechRecognizer(speechConfig, audioConfig);

        log.info("Listening...");

        Future<SpeechRecognitionResult> task = speechRecognizer.recognizeOnceAsync();
        SpeechRecognitionResult speechRecognitionResult = task.get();

        if (speechRecognitionResult.getReason() == ResultReason.RecognizedSpeech) {
            final String recognizedText = speechRecognitionResult.getText();
            log.info("RECOGNIZED: Text={}", recognizedText);
            return recognizedText;
        } else if (speechRecognitionResult.getReason() == ResultReason.NoMatch) {
            log.warn("NOMATCH: Speech could not be recognized.");
        } else if (speechRecognitionResult.getReason() == ResultReason.Canceled) {
            CancellationDetails cancellation = CancellationDetails.fromResult(speechRecognitionResult);
            log.error("CANCELED: Reason={}", cancellation.getReason());

            if (cancellation.getReason() == CancellationReason.Error) {
                log.error("CANCELED: ErrorCode={}", cancellation.getErrorCode());
                log.error("CANCELED: ErrorDetails={}", cancellation.getErrorDetails());
            }
        }
        throw new InterruptedException();
    }
}
