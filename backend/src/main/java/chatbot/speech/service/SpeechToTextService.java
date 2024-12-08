package chatbot.speech.service;

import java.util.concurrent.ExecutionException;

public interface SpeechToTextService {
    String listenToUser() throws InterruptedException, ExecutionException;
}
