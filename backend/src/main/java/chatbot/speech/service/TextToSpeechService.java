package chatbot.speech.service;

import java.util.concurrent.ExecutionException;

public interface TextToSpeechService {
    void textToSpeech(String text) throws InterruptedException, ExecutionException;
}
