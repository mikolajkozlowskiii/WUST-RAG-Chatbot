package chatbot.llm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {

    private final Assistant assistant;

    public String getResponse(String prompt) {
        log.info("Assistant Bean HashCode: {}", assistant.hashCode());
        return assistant.answer(prompt);
    }
}
